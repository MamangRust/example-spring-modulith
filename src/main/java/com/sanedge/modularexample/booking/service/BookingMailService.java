package com.sanedge.modularexample.booking.service;

import java.time.LocalDateTime;

public interface BookingMailService {
    void sendEmailCheckIn(String orderId, String email, String formattedDate);

    void sendEmailCheckOut(String orderId, String email, String formattedDate);

    void sendEmailBookingTime(String orderId, String email, LocalDateTime localDateTime);
}