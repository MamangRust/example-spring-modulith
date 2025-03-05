package com.sanedge.modularexample.booking.service;

import com.sanedge.modularexample.booking.domain.request.CreateBookingRequest;
import com.sanedge.modularexample.booking.domain.request.UpdateBookingRequest;
import com.sanedge.modularexample.domain.response.MessageResponse;

public interface BookingService {
    MessageResponse findAll();

    MessageResponse findById(Long id);

    MessageResponse createBooking(Long userId, CreateBookingRequest request);

    MessageResponse updateBooking(Long id, Long userId, UpdateBookingRequest request);

    MessageResponse deleteById(Long id);

}