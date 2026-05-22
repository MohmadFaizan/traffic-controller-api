package com.natwest.tc.model;

import com.natwest.tc.constants.Direction;
import com.natwest.tc.constants.Signal;

import java.io.Serializable;

public class TrafficLight implements Serializable {
    private Direction direction;
    private Signal state;

    public Direction getDirection() {
        return this.direction;
    }

    public Signal getState() {
        return this.state;
    }
}
