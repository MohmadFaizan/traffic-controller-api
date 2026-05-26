package com.natwest.tc.model;

import com.natwest.tc.constants.Direction;
import com.natwest.tc.service.TrafficLightState;
import com.natwest.tc.service.impl.RedLightState;

import java.io.Serializable;

public final class TrafficLight implements Serializable {
    private final Direction direction;
    private TrafficLightState state;

    public TrafficLight(final Direction direction) {
        this.direction = direction;
        this.state = new RedLightState();
    }

    public Direction getDirection() {
        return this.direction;
    }

    public TrafficLightState getState() {
        return this.state;
    }

    public void setState(final TrafficLightState state) {
        this.state = state;
    }
}
