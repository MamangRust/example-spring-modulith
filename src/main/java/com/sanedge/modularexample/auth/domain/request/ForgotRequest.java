package com.sanedge.modularexample.auth.domain.request;

import lombok.Data;

@Data
public class ForgotRequest {
    private String email;
}