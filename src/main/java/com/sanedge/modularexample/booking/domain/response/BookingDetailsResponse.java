package com.sanedge.modularexample.booking.domain.response;

import java.time.LocalDateTime;

import com.sanedge.modularexample.room.enums.RoomStatus;

import lombok.Data;

@Data
public class BookingDetailsResponse {
    private String orderId;
    private Integer totalPerson;
    private LocalDateTime bookingTime;
    private String userEmail;
    private String roomName;
    private Integer roomCapacity;
    private String roomPhoto;
    private RoomStatus roomStatus;

    public BookingDetailsResponse(String orderId, Integer totalPerson, LocalDateTime bookingTime, 
                                  String userEmail, String roomName, Integer roomCapacity, 
                                  String roomPhoto, RoomStatus roomStatus) {
        this.orderId = orderId;
        this.totalPerson = totalPerson;
        this.bookingTime = bookingTime;
        this.userEmail = userEmail;
        this.roomName = roomName;
        this.roomCapacity = roomCapacity;
        this.roomPhoto = roomPhoto;
        this.roomStatus = roomStatus;
    }

   
}