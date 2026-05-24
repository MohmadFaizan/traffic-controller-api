package com.natwest.tc.model;

import com.natwest.tc.constants.Direction;
import com.natwest.tc.constants.Signal;

import java.io.Serializable;

public final class TrafficLight implements Serializable {
    private final Direction direction;
    private Signal state;

    public TrafficLight(final Direction direction) {
        this.direction = direction;
        this.state = Signal.RED;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public Signal getState() {
        return this.state;
    }

    public void setState(Signal state) {
        this.state = state;
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
