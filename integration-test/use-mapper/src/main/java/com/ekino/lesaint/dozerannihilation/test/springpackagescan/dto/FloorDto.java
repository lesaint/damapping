package com.ekino.lesaint.dozerannihilation.test.springpackagescan.dto;

import java.util.List;

/**
 * FloorDto -
 *
 * @author Sébastien Lesaint
 */
public class FloorDto {
    private final List<RoomDto> rooms;

    public FloorDto(List<RoomDto> rooms) {
        this.rooms = rooms;
    }

    public List<RoomDto> getRooms() {
        return rooms;
    }
}
