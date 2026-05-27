package com.natwest.tc.model;

import com.natwest.tc.constants.Direction;

import java.util.Set;

public class SignalPhase {
    private final Set<Direction> directions;

    public SignalPhase(Set<Direction> directions) {
        this.directions = directions;
    }

    public Set<Direction> getDirections() {
        return directions;
    }
}
