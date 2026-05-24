package com.natwest.tc.model;

import com.natwest.tc.constants.Direction;
import com.natwest.tc.constants.Signal;

import java.io.Serializable;

public class TrafficLight implements Serializable {
    private final Direction direction;
    private final Signal state;

    public TrafficLight(final Direction direction, final Signal state) {
        this.direction = direction;
        this.state = state;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public Signal getState() {
        return this.state;
    }
}
