package com.natwest.tc.model;

import com.natwest.tc.constants.Constants;
import com.natwest.tc.constants.Direction;
import com.natwest.tc.constants.Phase;
import org.springframework.util.Assert;

import java.util.Set;

public class SignalPhase {
    private final Phase phase;
    private int greenDelayInSec = Constants.DEFAULT_GREEN_DELAY;
    private int yellowDelayInSec = Constants.DEFAULT_YELLOW_DELAY;

    public SignalPhase(final Phase phase) {
        this.phase = phase;
    }

    public Set<Direction> getDirections() {
        return this.phase.getDirections();
    }

    public int getGreenDelayInSec() {
        return greenDelayInSec;
    }

    public int getYellowDelayInSec() {
        return yellowDelayInSec;
    }

    public void setGreenDelayInSec(int greenDelayInSec) {
        Assert.isTrue(greenDelayInSec > 0, () -> "Invalid duration for Green delay");
        this.greenDelayInSec = greenDelayInSec;
    }

    public void setYellowDelayInSec(int yellowDelayInSec) {
        Assert.isTrue(yellowDelayInSec > 0, () -> "Invalid duration for Yellow delay");
        this.yellowDelayInSec = yellowDelayInSec;
    }
}
