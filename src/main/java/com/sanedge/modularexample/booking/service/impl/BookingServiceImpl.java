package com.sanedge.modularexample.booking.service.impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.sanedge.modularexample.booking.domain.request.CreateBookingRequest;
import com.sanedge.modularexample.booking.domain.request.UpdateBookingRequest;
import com.sanedge.modularexample.booking.models.Booking;
import com.sanedge.modularexample.booking.repository.BookingRepository;
import com.sanedge.modularexample.booking.service.BookingMailService;
import com.sanedge.modularexample.booking.service.BookingService;
import com.sanedge.modularexample.domain.response.MessageResponse;
import com.sanedge.modularexample.room.enums.RoomStatus;
import com.sanedge.modularexample.room.models.Room;
import com.sanedge.modularexample.room.repository.RoomRepository;
import com.sanedge.modularexample.user.models.User;
import com.sanedge.modularexample.user.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;



@Slf4j
@Service
public class BookingServiceImpl implements BookingService {
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final BookingMailService bookingMailService;

    @Autowired
    public BookingServiceImpl(UserRepository userRepository, RoomRepository roomRepository,
            BookingRepository bookingRepository, BookingMailService bookingMailService) {
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
        this.bookingMailService = bookingMailService;
    }

    @Override
    public MessageResponse findAll() {
        try {
            log.info("Fetching all bookings");
            List<Booking> bookingList = this.bookingRepository.findAll();

            log.info("Found {} bookings", bookingList.size());

            return MessageResponse.builder()
                    .message("Booking data retrieved successfully")
                    .data(bookingList)
                    .statusCode(HttpStatus.OK.value())
                    .build();
        } catch (Exception e) {
            log.error("Error fetching all bookings", e);

            return MessageResponse.builder()
                    .message("Error fetching all bookings")
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
        }
    }

    @Override
    public MessageResponse findById(Long id) {
        try {
            log.info("Fetching booking by id: {}", id);

            Booking findBooking = this.bookingRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Not found booking"));

            log.info("Found booking: {}", findBooking);

            return MessageResponse.builder().message("Success").data(findBooking).statusCode(HttpStatus.OK.value())
                    .build();
        } catch (Exception e) {
            log.error("Error fetching booking by id: {}", id, e);

            return MessageResponse.builder()
                    .message("Error fetching booking")
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
        }
    }

    @Override
    public MessageResponse createBooking(Long userId, CreateBookingRequest request) {
        try {
            log.info("Creating new booking for user {} and room {}", userId, request.getRoomId());

            User findUser = this.userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Not found user"));
            Room findRoom = this.roomRepository.findById(request.getRoomId())
                    .orElseThrow(() -> new RuntimeException("Not found room"));

            if (request.getTotalPerson() >= findRoom.getRoomCapacity()) {
                return MessageResponse.builder()
                        .message("Room capacity not enough")
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .build();
            }

            if (findRoom.getRoomStatus() == RoomStatus.BOOKING) {
                return MessageResponse.builder()
                        .message("Room is booking")
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .build();
            }

            Booking orderBooking = new Booking();

            orderBooking.setOrderId("ORDER" + System.currentTimeMillis());
            orderBooking.setUser(findUser);
            orderBooking.setRoom(findRoom);
            orderBooking.setTotalPerson(request.getTotalPerson());
            orderBooking.setBookingTime(request.getBookingTime());
            orderBooking.setNoted(request.getNoted());
            orderBooking.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));

            this.bookingRepository.save(orderBooking);

            log.info("Booking created successfully: {}", orderBooking);

            findRoom.setRoomStatus(RoomStatus.BOOKING);

            this.roomRepository.save(findRoom);

            return MessageResponse.builder().message("Success").data(orderBooking).statusCode(200).build();
        } catch (Exception e) {
            log.error("Error creating booking for user {} and room {}", userId, request.getRoomId(), e);

            return MessageResponse.builder()
                    .message("Error creating booking")
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
        }
    }

    @Override
    public MessageResponse updateBooking(Long id, Long userId, UpdateBookingRequest request) {
        try {
            log.info("Updating booking with id: {}", id);

            User findUser = this.userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Not found user"));
            Room findRoom = this.roomRepository.findById(request.getRoomId())
                    .orElseThrow(() -> new RuntimeException("Not found room"));

            Booking findBooking = this.bookingRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Not found booking"));

            if (request.getTotalPerson() >= findRoom.getRoomCapacity()) {
                return MessageResponse.builder()
                        .message("Room capacity not enough")
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .build();
            }

            if (findRoom.getRoomStatus() == RoomStatus.BOOKING) {
                return MessageResponse.builder()
                        .message("Room is booking")
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .build();
            }

            findBooking.setUser(findUser);
            findBooking.setRoom(findRoom);
            findBooking.setTotalPerson(request.getTotalPerson());
            findBooking.setBookingTime(request.getBookingTime());
            findBooking.setNoted(request.getNoted());

            this.bookingRepository.save(findBooking);

            log.info("Booking updated successfully: {}", findBooking);

            return MessageResponse.builder().message("Booking updated successfully").data(findBooking)
                    .statusCode(HttpStatus.OK.value()).build();
        } catch (Exception e) {
            log.error("Error updating booking with id: {}", id, e);

            return MessageResponse.builder().message("Error updating booking")
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value()).build();
        }
    }

    @Scheduled(cron = "0 0 * * * ?", zone = "Asia/Jakarta")
    public void bookingTimeCronJob() {
        try {
            LocalDateTime dateNow = LocalDateTime.now();

            List<Booking> bookings = bookingRepository.findBookingsByBookingTime(dateNow);

            for (Booking booking : bookings) {
                bookingMailService.sendEmailBookingTime(booking.getOrderId(), booking.getUser().getEmail(),
                        booking.getCheckInTime());
                log.info("Booking time cron job sent email to: {}", booking.getUser().getEmail());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error booking time cron job", e);
        }
    }

    @Override
    public MessageResponse deleteById(Long id) {
        try {
            log.info("Deleting booking with id: {}", id);

            Booking findBooking = this.bookingRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Not found booking"));

            this.bookingRepository.delete(findBooking);

            log.info("Booking deleted successfully");

            return MessageResponse.builder().message("Success").statusCode(HttpStatus.OK.value()).build();
        } catch (Exception e) {
            log.error("Error deleting booking with id: {}", id, e);

            return MessageResponse.builder().message("Error deleting booking")
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value()).build();
        }
    }

}