package com.sanedge.modularexample.auth.service.impl;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sanedge.modularexample.auth.domain.request.ForgotRequest;
import com.sanedge.modularexample.auth.domain.request.LoginRequest;
import com.sanedge.modularexample.auth.domain.request.RegisterRequest;
import com.sanedge.modularexample.auth.domain.request.ResetPasswordRequest;
import com.sanedge.modularexample.auth.domain.response.AuthResponse;
import com.sanedge.modularexample.auth.domain.response.TokenRefreshResponse;
import com.sanedge.modularexample.auth.models.RefreshToken;
import com.sanedge.modularexample.auth.models.ResetToken;
import com.sanedge.modularexample.auth.service.AuthService;
import com.sanedge.modularexample.auth.service.MailService;
import com.sanedge.modularexample.auth.service.RefreshTokenService;
import com.sanedge.modularexample.auth.service.ResetTokenService;
import com.sanedge.modularexample.domain.response.MessageResponse;
import com.sanedge.modularexample.exception.ResourceNotFoundException;
import com.sanedge.modularexample.exception.TokenRefreshException;
import com.sanedge.modularexample.role.enums.ERole;
import com.sanedge.modularexample.role.models.Role;
import com.sanedge.modularexample.role.repository.RoleRepository;
import com.sanedge.modularexample.security.JwtProvider;
import com.sanedge.modularexample.security.UserDetailsImpl;
import com.sanedge.modularexample.user.models.User;
import com.sanedge.modularexample.user.repository.UserRepository;
import com.sanedge.modularexample.utils.RandomString;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenServiceImpl refreshTokenService;
    private final MailService authMailService;
    private final ResetTokenService resetTokenService;

    @Autowired
    public AuthServiceImpl(AuthenticationManager authenticationManager, UserRepository userRepository,
            RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtProvider jwtProvider,
            RefreshTokenServiceImpl refreshTokenServiceImpl, MailService authMailService,
            ResetTokenService resetTokenService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jwtProvider = jwtProvider;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenServiceImpl;
        this.authMailService = authMailService;
        this.resetTokenService = resetTokenService;
    }

    @Override
    public MessageResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtProvider.generateAccessToken(authentication);

        long expiresAt = jwtProvider.getjwtExpirationMs();
        Date date = new Date();
        date.setTime(expiresAt);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Optional<RefreshToken> existingRefreshToken = refreshTokenService.findByUser(user);

        if (existingRefreshToken.isPresent()) {
            refreshTokenService.updateExpiryDate(existingRefreshToken.get());
        } else {
            refreshTokenService.deleteByUserId(userDetails.getId());
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
            existingRefreshToken = Optional.of(refreshToken);
        }

        AuthResponse authResponse = AuthResponse.builder()
                .access_token(jwt)
                .refresh_token(existingRefreshToken.get().getToken())
                .expiresAt(dateFormat.format(date))
                .username(userDetails.getUsername())
                .build();

        log.info("User successfully logged in: {}", userDetails.getUsername());

        return MessageResponse.builder().message("Berhasil login").data(authResponse).build();
    }

    @Override
    public MessageResponse register(RegisterRequest registerRequest) {
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        Set<String> strRoles = registerRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new ResourceNotFoundException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new ResourceNotFoundException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;
                    case "mod":
                        Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new ResourceNotFoundException("Error: Role is not found."));
                        roles.add(modRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new ResourceNotFoundException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }
        user.setRoles(roles);
        user.setVerified(false);

        String token = RandomString.generateRandomString(50);

        user.setVerificationCode(token);

        this.userRepository.save(user);

        authMailService.sendEmailVerify(registerRequest.getEmail(), token);

        log.info("User registered successfully: {}", user.getUsername());

        return MessageResponse.builder().message("Successs create user").data(null).statusCode(200).build();

    }

    @Override
    public MessageResponse forgotPassword(ForgotRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ResetToken resetToken = resetTokenService.createResetToken(user);

        String resetLink = "http://localhost:8080/api/auth/reset-password?token=" + resetToken.getToken();
        authMailService.sendResetPasswordEmail(user.getEmail(), resetLink);

        log.info("Forgot password request processed for user: {}", user.getEmail());

        return MessageResponse.builder().message("Successs send email").statusCode(200).build();
    }

    @Override
    public MessageResponse resetPassword(ResetPasswordRequest request) {
        ResetToken resetToken = resetTokenService.findByToken(request.getToken())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid or expired token"));

        if (resetToken.getExpiryDate().isBefore(Instant.now())) {
            return MessageResponse.builder().message("Reset token has expired.").statusCode(400).build();
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        resetTokenService.deleteResetToken(user.getId());

        log.info("Password reset successfully for user: {}", user.getUsername());

        return MessageResponse.builder().message("Password reset successfully.").statusCode(200).build();
    }

    public MessageResponse verifyEmail(String token) {
        Optional<User> optionalUser = userRepository.findByVerificationCode(token);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setVerified(true);
            userRepository.save(user);

            log.info("Email verification successful for user: {}", user.getUsername());
            return MessageResponse.builder().message("Success verify email").statusCode(200).build();
        } else {
            log.info("Verification code not found: {}", token);
            return MessageResponse.builder().message("Verification code not found").statusCode(404).build();
        }
    }

    @Transactional(readOnly = true)
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return this.userRepository.findByUsername(authentication.getName())
                .orElseThrow(
                        () -> new UsernameNotFoundException("User name not found - " + authentication.getName()));
    }

    @Override
    public TokenRefreshResponse refreshToken(String refreshToken) {
        return refreshTokenService.findByToken(refreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String newAccessToken = jwtProvider.generateTokenFromUsername(user.getUsername());

                    return new TokenRefreshResponse(newAccessToken, refreshToken);
                })
                .orElseThrow(() -> new TokenRefreshException(refreshToken,
                        "Invalid or expired refresh token"));
    }

    @Override
    public MessageResponse logout() {
        refreshTokenService.deleteByUserId(getCurrentUser().getId());
        log.info("User logged out successfully");
        return MessageResponse.builder().message("Logout success").statusCode(200).build();
    }

}