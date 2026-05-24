package com.natwest.tc.model;

import com.natwest.tc.constants.Direction;

import java.util.Set;

public class SignalPhase {
    private final Set<Direction> directions;
    private int greenDelayInSec;
    private int yellowDelayInSec;

    public SignalPhase(Set<Direction> directions) {
        this.directions = directions;
    }

    public Set<Direction> getDirections() {
        return directions;
    }

    public int getGreenDelayInSec() {
        return greenDelayInSec;
    }

    public int getYellowDelayInSec() {
        return yellowDelayInSec;
    }

    public void setGreenDelayInSec(int greenDelayInSec) {
        this.greenDelayInSec = greenDelayInSec;
    }

    public void setYellowDelayInSec(int yellowDelayInSec) {
        this.yellowDelayInSec = yellowDelayInSec;
    }
}
