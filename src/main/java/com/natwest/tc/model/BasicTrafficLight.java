package com.natwest.tc.model;

import com.natwest.tc.constants.Direction;
import com.natwest.tc.constants.Signal;
import org.springframework.util.Assert;

public class BasicTrafficLight extends TrafficLight {
    private int greenDelayInSec;
    private int yellowDelayInSec;

    /**
     * <p>Configure Traffic Light on a {@code direction} with default {@code Signal.RED} .</p>
     *
     * <p>Green signal delay: {@code 30}seconds</p>
     * <p>Yellow signal delay: {@code 7}seconds</p>
     *
     * @param direction {@link Direction} of Signal
     */
    public BasicTrafficLight(final Direction direction) {
        super(direction, Signal.RED);

        this.greenDelayInSec = 30;
        this.yellowDelayInSec = 7;
    }

    public BasicTrafficLight(final Direction direction, final int greenDelayInSec,
                             final int yellowDelayInSec) {
        this(direction);

        this.setGreenDelayInSec(greenDelayInSec);
        this.setYellowDelayInSec(yellowDelayInSec);
    }

    public int getGreenDelayInSec() {
        return greenDelayInSec;
    }

    public void setGreenDelayInSec(int greenDelayInSec) {
        Assert.isTrue(greenDelayInSec > 0, () -> "Invalid delay for Green Signal");

        this.greenDelayInSec = greenDelayInSec;
    }

    public int getYellowDelayInSec() {
        return yellowDelayInSec;
    }

    public void setYellowDelayInSec(final int yellowDelayInSec) {
        Assert.isTrue(yellowDelayInSec > 0, () -> "Invalid delay for Yellow Signal");
        this.yellowDelayInSec = yellowDelayInSec;
    }
}
