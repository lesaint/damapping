package fr.phan.damapping.test.springpackagescan.service;

import java.util.List;

/**
 * Floor -
 *
 * @author Sébastien Lesaint
 */
public class Floor {
    private final int floorNumber;
    private final List<Room> rooms;

    public Floor(int floorNumber, List<Room> rooms) {
        this.floorNumber = floorNumber;
        this.rooms = rooms;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public List<Room> getRooms() {
        return rooms;
    }
}
