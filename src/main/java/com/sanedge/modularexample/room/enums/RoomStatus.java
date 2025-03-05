package com.sanedge.modularexample.room.enums;


public enum RoomStatus {
    READY("ready"),
    OCCUPIED("occupied"),
    UNDER_MAINTENANCE("under_maintenance"),
    BOOKING("booking");

    private final String status;

    RoomStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}