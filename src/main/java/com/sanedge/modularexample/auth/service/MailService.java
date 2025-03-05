package com.sanedge.modularexample.auth.service;

public interface MailService {
    void sendEmailVerify(String email, String token);

    void sendResetPasswordEmail(String email, String resetLink);
}
