package com.sanedge.modularexample.booking.domain.request;


import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CheckInRequest {
    private String orderId;
    private LocalDateTime CheckInTime;
    private String email;
}