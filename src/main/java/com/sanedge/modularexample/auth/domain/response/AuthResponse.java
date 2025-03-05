package com.sanedge.modularexample.auth.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {
    private String access_token;
    private String refresh_token;
    private Integer statusCode;
    private String expiresAt;
    private String username;
}