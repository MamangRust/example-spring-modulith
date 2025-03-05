package com.sanedge.modularexample.auth.service;

import com.sanedge.modularexample.auth.domain.request.ForgotRequest;
import com.sanedge.modularexample.auth.domain.request.LoginRequest;
import com.sanedge.modularexample.auth.domain.request.RegisterRequest;
import com.sanedge.modularexample.auth.domain.request.ResetPasswordRequest;
import com.sanedge.modularexample.auth.domain.response.TokenRefreshResponse;
import com.sanedge.modularexample.domain.response.MessageResponse;
import com.sanedge.modularexample.user.models.User;

public interface AuthService {
    public MessageResponse login(LoginRequest loginRequest);

    public MessageResponse register(RegisterRequest registerRequest);

    public TokenRefreshResponse refreshToken(String refreshToken);

    User getCurrentUser();

    public MessageResponse logout();

    public MessageResponse forgotPassword(ForgotRequest request);

    public MessageResponse resetPassword(ResetPasswordRequest request);

    public MessageResponse verifyEmail(String token);
}