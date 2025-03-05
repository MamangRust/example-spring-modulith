package com.sanedge.modularexample.auth.service;

import java.util.Optional;

import com.sanedge.modularexample.auth.models.ResetToken;
import com.sanedge.modularexample.user.models.User;

public interface ResetTokenService {
    ResetToken createResetToken(User user);

    void deleteResetToken(Long userId);

    Optional<ResetToken> findByToken(String token);

    void updateExpiryDate(ResetToken resetToken);
}