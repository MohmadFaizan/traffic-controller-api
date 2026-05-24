package com.natwest.tc.model;

import com.natwest.tc.constants.Direction;
import com.natwest.tc.constants.Signal;

import java.io.Serializable;

public abstract class TrafficLight implements Serializable {
    private final Direction direction;
    private final Signal state;

    TrafficLight(final Direction direction, final Signal state) {
        this.direction = direction;
        this.state = state;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public Signal getState() {
        return this.state;
    }

    public final boolean isGreen() {
        return getState() == Signal.GREEN;
    }

    public final boolean isYellow() {
        return getState() == Signal.YELLOW;
    }

    public final boolean isRed() {
        return !isGreen() && !isYellow();
    }
}
