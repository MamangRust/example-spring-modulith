package com.sanedge.modularexample.room.models;

import com.sanedge.modularexample.models.BaseModel;
import com.sanedge.modularexample.room.enums.RoomStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "rooms")
@EqualsAndHashCode(callSuper = true)
public class Room extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long id;

    @Column(name = "room_name", nullable = false)
    private String roomName;

    @Column(name = "room_capacity", nullable = false)
    private Integer roomCapacity;

    @Column(name = "photo", nullable = false)
    private String photo;

    @Column(name = "room_status", columnDefinition = "varchar(255) default 'ready'")
    @Enumerated(EnumType.STRING)
    private RoomStatus roomStatus;

}