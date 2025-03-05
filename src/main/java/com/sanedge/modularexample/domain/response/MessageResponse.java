package com.sanedge.modularexample.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Data
@AllArgsConstructor
@Builder
public class MessageResponse {
    private String message;
    private Object data;
    private Integer statusCode;
}