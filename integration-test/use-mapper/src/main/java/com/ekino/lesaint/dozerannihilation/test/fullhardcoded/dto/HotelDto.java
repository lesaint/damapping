package com.ekino.lesaint.dozerannihilation.test.fullhardcoded.dto;

import java.util.List;

/**
 * HotelDto -
 *
 * @author Sébastien Lesaint
 */
public class HotelDto {
    private final List<FloorDto> floors;

    public HotelDto(List<FloorDto> floors) {
        this.floors = floors;
    }
}
