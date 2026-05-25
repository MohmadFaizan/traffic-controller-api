package com.natwest.tc.dto.request;

import com.natwest.tc.constants.Direction;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

public class SequenceRequestDto implements Serializable {
    private final Set<Direction> directions;
    private int greenDelayInSec;
    private int yellowDelayInSec;

    public SequenceRequestDto(final Set<Direction> directions) {
        this.directions = Collections.unmodifiableSet(directions);
        this.greenDelayInSec = 15;
        this.yellowDelayInSec = 5;
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
