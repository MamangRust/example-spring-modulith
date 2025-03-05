package com.sanedge.modularexample.room.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sanedge.modularexample.domain.response.MessageResponse;
import com.sanedge.modularexample.room.domain.requests.CreateRoomRequest;
import com.sanedge.modularexample.room.domain.requests.UpdateRoomRequest;
import com.sanedge.modularexample.room.service.RoomService;

@RestController
@RequestMapping("/api/room")
public class RoomController {

    @Autowired
    RoomService roomService;

    @GetMapping("")
    public ResponseEntity<MessageResponse> getAllRoom() {
        MessageResponse room = roomService.findAll();

        return ResponseEntity.ok(room);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageResponse> getRoomById(@RequestParam("id") Long id) {
        MessageResponse room = roomService.findById(id);

        return ResponseEntity.ok(room);
    }

    @PostMapping("/create")
    public ResponseEntity<MessageResponse> createRoom(@RequestParam("file") MultipartFile myFile,
            @RequestParam("roomName") String roomName, @RequestParam("roomCapacity") Integer roomCapacity) {
        CreateRoomRequest createRoomRequest = new CreateRoomRequest();

        createRoomRequest.setRoomName(roomName);
        createRoomRequest.setRoomCapacity(roomCapacity);
        createRoomRequest.setFile(myFile);

        MessageResponse room = roomService.createRoom(createRoomRequest);

        return ResponseEntity.ok(room);

    }

    @PutMapping("/update/{id}")
    public ResponseEntity<MessageResponse> updateRoom(@RequestParam("id") Long id,
            @RequestParam("file") MultipartFile myFile,
            @RequestParam("roomName") String roomName, @RequestParam("roomCapacity") Integer roomCapacity) {

        UpdateRoomRequest updateRoomRequest = new UpdateRoomRequest();

        updateRoomRequest.setRoomName(roomName);
        updateRoomRequest.setRoomCapacity(roomCapacity);
        updateRoomRequest.setFile(myFile);

        MessageResponse room = roomService.updateRoom(id, updateRoomRequest);

        return ResponseEntity.ok(room);
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity<MessageResponse> deleteRoom(@RequestParam("id") Long id) {

        MessageResponse room = roomService.deleteRoom(id);

        return ResponseEntity.ok(room);
    }

}