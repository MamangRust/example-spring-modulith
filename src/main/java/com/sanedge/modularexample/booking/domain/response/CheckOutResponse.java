package com.sanedge.modularexample.booking.domain.response;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CheckOutResponse {
    private String orderId;
    private Long roomId;
    private LocalDateTime checkOutTime;
    private String userEmail;

    public CheckOutResponse(String orderId, Long roomId, LocalDateTime checkOutTime, String userEmail) {
        this.orderId = orderId;
        this.roomId = roomId;
        this.checkOutTime = checkOutTime;
        this.userEmail = userEmail;
    }   
}