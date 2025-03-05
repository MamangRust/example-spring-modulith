package com.sanedge.modularexample.auth.domain.request;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String token;
    private String password;
}