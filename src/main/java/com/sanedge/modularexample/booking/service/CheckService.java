package com.sanedge.modularexample.booking.service;

import com.sanedge.modularexample.booking.domain.request.CheckInRequest;
import com.sanedge.modularexample.domain.response.MessageResponse;

public interface CheckService {
    MessageResponse checkInOrder(CheckInRequest request);

    MessageResponse checkOrder(String orderId);

    MessageResponse checkOutOrder(String orderId);
}
