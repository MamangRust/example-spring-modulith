package com.sanedge.modularexample.room.service.impl;

import java.io.File;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sanedge.modularexample.domain.response.MessageResponse;
import com.sanedge.modularexample.room.domain.requests.CreateRoomRequest;
import com.sanedge.modularexample.room.domain.requests.UpdateRoomRequest;
import com.sanedge.modularexample.room.domain.response.RoomResponse;
import com.sanedge.modularexample.room.enums.RoomStatus;
import com.sanedge.modularexample.room.mapper.RoomMapper;
import com.sanedge.modularexample.room.models.Room;
import com.sanedge.modularexample.room.repository.RoomRepository;
import com.sanedge.modularexample.room.service.RoomService;

@Service
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;
    private final FileServiceImpl fileService;
    private final FolderServiceImpl folderService;

    @Autowired
    public RoomServiceImpl(RoomRepository roomRepository, FileServiceImpl fileService,
            FolderServiceImpl folderService) {
        this.roomRepository = roomRepository;
        this.fileService = fileService;
        this.folderService = folderService;
    }

    public MessageResponse findAll() {
        try {
            List<Room> roomList = roomRepository.findAll();
            return MessageResponse.builder()
                    .message("Success")
                    .data(RoomMapper.toRoomResponseList(roomList))
                    .statusCode(HttpStatus.OK.value())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return MessageResponse.builder()
                    .message("Error occurred while fetching rooms")
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
        }
    }

    public MessageResponse findById(Long id) {
        try {
            Optional<Room> room = roomRepository.findById(id);
            if (room.isPresent()) {
                return MessageResponse.builder()
                        .message("Success")
                        .data(RoomMapper.toRoomResponse(room.get()))
                        .statusCode(HttpStatus.OK.value())
                        .build();
            } else {
                return MessageResponse.builder()
                        .message("Room not found")
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return MessageResponse.builder()
                    .message("Error occurred while fetching room by ID")
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
        }
    }

    @Override
    public MessageResponse createRoom(CreateRoomRequest createRoomRequest) {
        MultipartFile myFile = createRoomRequest.getFile();

        Room room = new Room();
        room.setRoomName(createRoomRequest.getRoomName());
        room.setRoomCapacity(createRoomRequest.getRoomCapacity());
        room.setRoomStatus(RoomStatus.READY);

        Room findName = roomRepository.findByRoomName(createRoomRequest.getRoomName())
                .orElseThrow(() -> new RuntimeException("Room not found"));
        if (findName != null) {
            return MessageResponse.builder()
                    .message("Room name already exists")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .build();
        }

        try {
            if (myFile != null) {
                String folderPath = folderService.createFolder(createRoomRequest.getRoomName());

                if (folderPath != null) {
                    String filePath = folderPath + File.separator + myFile.getOriginalFilename();

                    String createdFilePath = fileService.createFileImage(myFile, filePath);

                    if (createdFilePath != null) {
                        room.setPhoto(createdFilePath);
                        roomRepository.save(room);

                        RoomResponse mapper = RoomMapper.toRoomResponse(room);

                        return MessageResponse.builder()
                                .message("Room created successfully")
                                .data(mapper)
                                .statusCode(HttpStatus.OK.value())
                                .build();
                    } else {

                        System.err.println("Failed to create the file");
                        return MessageResponse.builder()
                                .message("Failed to create the file")
                                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .build();
                    }
                } else {
                    System.err.println("Failed to create the folder");
                    return MessageResponse.builder()
                            .message("Failed to create the folder")
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .build();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return MessageResponse.builder()
                    .message("Unexpected error occurred")
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
        }

        return MessageResponse.builder()
                .message("No file provided")
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build();
    }

    public MessageResponse updateRoom(Long id, UpdateRoomRequest request) {
        Room findName = roomRepository.findByRoomName(request.getRoomName())
                .orElseThrow(() -> new RuntimeException("Room not found"));
        if (findName != null) {
            return MessageResponse.builder()
                    .message("Room name already exists")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .build();
        }

        MultipartFile myFile = request.getFile();

        Room room = roomRepository.findById(id).orElseThrow(() -> new RuntimeException("Room not found"));

        room.setRoomName(request.getRoomName());
        room.setRoomCapacity(request.getRoomCapacity());

        try {
            if (myFile != null) {
                folderService.deleteFolder(room.getRoomName());
                fileService.deleteFileImage(room.getPhoto());

                String folderPath = folderService.createFolder(request.getRoomName());

                if (folderPath != null) {

                    String filePath = folderPath + File.separator + myFile.getOriginalFilename();
                    String createdFilePath = fileService.createFileImage(myFile, filePath);

                    if (createdFilePath != null) {
                        room.setPhoto(createdFilePath);
                        roomRepository.save(room);

                        RoomResponse mapper = RoomMapper.toRoomResponse(room);

                        return MessageResponse.builder().message("Room updated successfully").data(mapper)
                                .statusCode(HttpStatus.OK.value()).build();
                    } else {
                        System.err.println("Failed to create the file");
                        return MessageResponse.builder()
                                .message("Failed to create the file")
                                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .build();
                    }
                } else {
                    System.err.println("Failed to create the folder");
                    return MessageResponse.builder()
                            .message("Failed to create the folder")
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .build();
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            return MessageResponse.builder()
                    .message("Unexpected error occurred")
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
        }
        return MessageResponse.builder()
                .message("No file provided")
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build();

    }

    public MessageResponse deleteRoom(Long id) {
        Room room = this.roomRepository.findById(id).orElseThrow(() -> new RuntimeException("Room not found"));

        try {

            roomRepository.delete(room);

            return MessageResponse.builder()
                    .message("Success")
                    .statusCode(HttpStatus.OK.value())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return MessageResponse.builder()
                    .message("Error occurred while deleting room")
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
        }
    }

}