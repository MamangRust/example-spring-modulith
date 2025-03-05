package com.sanedge.modularexample.room.service;

import com.sanedge.modularexample.domain.response.MessageResponse;
import com.sanedge.modularexample.room.domain.requests.CreateRoomRequest;
import com.sanedge.modularexample.room.domain.requests.UpdateRoomRequest;

public interface RoomService {
    MessageResponse findAll();

    MessageResponse findById(Long id);

    MessageResponse createRoom(CreateRoomRequest createRoomRequest);

    MessageResponse updateRoom(Long id, UpdateRoomRequest request);

    MessageResponse deleteRoom(Long id);
}