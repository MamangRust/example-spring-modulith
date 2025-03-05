package com.sanedge.modularexample.room.domain.requests;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class CreateRoomRequest {
    private String roomName;
    private Integer roomCapacity;
    private MultipartFile file;
}