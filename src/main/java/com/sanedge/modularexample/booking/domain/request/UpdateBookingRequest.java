package com.sanedge.modularexample.booking.domain.request;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBookingRequest {
    private Long roomId;
    private Integer totalPerson;
    private LocalDateTime bookingTime;
    private String noted;
}