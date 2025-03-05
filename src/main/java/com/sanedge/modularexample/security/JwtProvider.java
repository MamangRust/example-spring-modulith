package com.sanedge.modularexample.security;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtProvider {
    static final String issuer = "MyApp";

    @Value("${springjwt.app.jwtSecret}")
    private String jwtSecret;

    @Value("${springjwt.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    private Algorithm accessTokenAlgorithm;

    private JWTVerifier jwtTokenVerifier;

    public JwtProvider(@Value("${springjwt.app.jwtSecret}") String accessTokenSecret,
            @Value("${springjwt.app.jwtExpirationMs}") int jwtExpirationMs) {
        jwtExpirationMs = (int) jwtExpirationMs * 60 * 1000;
        accessTokenAlgorithm = Algorithm.HMAC512(accessTokenSecret);
        jwtTokenVerifier = JWT.require(accessTokenAlgorithm)
                .withIssuer(issuer)
                .build();
    }

    public String generateAccessToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return generateTokenFromUsername(userPrincipal.getUsername());
    }

    public String generateTokenFromUsername(String username) {
        return JWT.create()
                .withIssuer(issuer)
                .withSubject(username)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(new Date().getTime() + jwtExpirationMs))
                .sign(accessTokenAlgorithm);
    }

    private Optional<DecodedJWT> decodeAccessToken(String token) {
        try {
            return Optional.of(jwtTokenVerifier.verify(token));
        } catch (JWTVerificationException e) {
            log.error("invalid access token", e);
        }
        return Optional.empty();
    }

    public boolean validateAccessToken(String token) {
        return decodeAccessToken(token).isPresent();
    }

    public String getUsernameAccessToken(String token) {
        return decodeAccessToken(token).get().getSubject();
    }

    public int getjwtExpirationMs() {
        return jwtExpirationMs;
    }
}