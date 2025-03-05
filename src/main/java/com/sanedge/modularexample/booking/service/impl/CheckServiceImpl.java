package com.sanedge.modularexample.booking.service.impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sanedge.modularexample.booking.domain.request.CheckInRequest;
import com.sanedge.modularexample.booking.domain.response.BookingDetailsResponse;
import com.sanedge.modularexample.booking.domain.response.CheckOutResponse;
import com.sanedge.modularexample.booking.models.Booking;
import com.sanedge.modularexample.booking.repository.BookingRepository;
import com.sanedge.modularexample.booking.service.BookingMailService;
import com.sanedge.modularexample.booking.service.CheckService;
import com.sanedge.modularexample.domain.response.MessageResponse;
import com.sanedge.modularexample.room.enums.RoomStatus;
import com.sanedge.modularexample.room.models.Room;
import com.sanedge.modularexample.room.repository.RoomRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CheckServiceImpl implements CheckService {
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final BookingMailService bookingMailService;

    @Autowired
    public CheckServiceImpl(BookingRepository bookingRepository, RoomRepository roomRepository,
            BookingMailService bookingMailService) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.bookingMailService = bookingMailService;
    }

    public MessageResponse checkInOrder(CheckInRequest request) {
        try {
            log.info("Checking in order with orderId: {}", request.getOrderId());

            Booking findOrder = this.bookingRepository.findByOrderId(request.getOrderId())
                    .orElseThrow(() -> new RuntimeException("Not found order"));

            findOrder.setCheckInTime(request.getCheckInTime());
            findOrder.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

            this.bookingRepository.save(findOrder);

            log.info("Order checked in successfully: {}", findOrder);

            bookingMailService.sendEmailCheckIn(findOrder.getOrderId(), findOrder.getUser().getEmail(),
                    findOrder.getCheckInTime().toString());

            return MessageResponse.builder().message("Success").statusCode(200).build();

        } catch (Exception e) {
            log.error("Error checking in order", e);
            return MessageResponse.builder().message("Error checking in order").statusCode(500).build();
        }
    }

    public MessageResponse checkOrder(String orderId) {
        try {
            log.info("Checking order details for orderId: {}", orderId);

            BookingDetailsResponse booking = this.bookingRepository.findBookingDetailsByOrderId(orderId)
                    .orElseThrow(() -> new RuntimeException("Not found order"));

            log.info("Found order details: {}", booking);

            return MessageResponse.builder().message("Your detail booking order information").data(booking)
                    .statusCode(200)
                    .build();

        } catch (Exception e) {
            log.error("Error checking order details", e);
            return MessageResponse.builder().message("Error checking order details").statusCode(500).build();
        }
    }

    public MessageResponse checkOutOrder(String orderId) {
        CheckOutResponse checkIn = this.bookingRepository.findCheckoutDetailsByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Not found order"));

        log.info("Found check out details: {}", checkIn);

        if (checkIn.getCheckOutTime() != null) {
            return MessageResponse.builder().message("Already checked out").statusCode(400).build();
        }

        Booking booking = this.bookingRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Not found order"));

        Room room = this.roomRepository.findById(checkIn.getRoomId())
                .orElseThrow(() -> new RuntimeException("Not found room"));

        booking.setCheckOutTime(LocalDateTime.now());
        booking.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

        room.setRoomStatus(RoomStatus.READY);
        room.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

        this.bookingRepository.save(booking);
        this.roomRepository.save(room);

        bookingMailService.sendEmailCheckOut(booking.getOrderId(), booking.getUser().getEmail(),
                booking.getCheckOutTime().toString());

        return MessageResponse.builder().message("Check out room successfuly").data(checkIn).statusCode(200).build();
    }
}
