package com.natwest.tc.model;

import com.natwest.tc.constants.Direction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class BasicTrafficLightTest {

    private BasicTrafficLight trafficLight;

    @BeforeEach
    void setup() {
        this.trafficLight = new BasicTrafficLight(Direction.EAST);
    }

    @Test
    void test_DefaultSignal() {
        Assertions.assertTrue(this.trafficLight.isRed(), "Invalid Signal State");
    }

    @Test
    void test_GreenDelay() {
        Assertions.assertTrue(this.trafficLight.getGreenDelayInSec() > 0, "Invalid Green delay time");
    }

    @Test
    void test_YellowDelay() {
        Assertions.assertTrue(this.trafficLight.getYellowDelayInSec() > 0, "Invalid Yellow delay time");
    }

    @ParameterizedTest
    @ValueSource(ints = {5, 10, 20})
    void test_Updated_GreenDelay(final int delay) {
        this.trafficLight.setGreenDelayInSec(delay);

        Assertions.assertEquals(delay, this.trafficLight.getGreenDelayInSec());
    }

    @ParameterizedTest
    @ValueSource(ints = {3, 5, 9})
    void test_Updated_YellowDelay(final int delay) {
        this.trafficLight.setYellowDelayInSec(delay);

        Assertions.assertEquals(delay, this.trafficLight.getYellowDelayInSec());
    }

    @Test
    void test_Invalid_GreenDelay() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.trafficLight.setGreenDelayInSec(0));
    }

    @Test
    void test_Invalid_YellowDelay() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.trafficLight.setYellowDelayInSec(-1));
    }
}
