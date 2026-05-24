package com.natwest.tc.model;

import com.natwest.tc.constants.Direction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TrafficLightTest {

    private TrafficLight trafficLight;

    @BeforeEach
    void setup() {
        this.trafficLight = new TrafficLight(Direction.EAST);
    }

    @Test
    void test_DefaultSignal() {
        Assertions.assertTrue(this.trafficLight.isRed(), "Invalid Signal State");
    }
}
