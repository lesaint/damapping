package com.ekino.lesaint.dozerannihilation.test.fullhardcoded.service;

import java.util.List;

/**
 * Hotel -
 *
 * @author Sébastien Lesaint
 */
public class Hotel {
    private String name;
    private List<Floor> floors;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Floor> getFloors() {
        return floors;
    }

    public void setFloors(List<Floor> floors) {
        this.floors = floors;
    }
}
