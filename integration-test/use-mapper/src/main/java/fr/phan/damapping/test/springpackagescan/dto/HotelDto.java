package fr.phan.damapping.test.springpackagescan.dto;

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

    public List<FloorDto> getFloors() {
        return floors;
    }
}
