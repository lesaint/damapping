package com.ekino.lesaint.dozerannihilation.test.fullhardcoded.dto;

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
}
