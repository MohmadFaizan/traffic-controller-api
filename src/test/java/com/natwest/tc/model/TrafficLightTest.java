package com.natwest.tc.model;

import com.natwest.tc.constants.Direction;
import com.natwest.tc.constants.Signal;
import com.natwest.tc.service.TrafficLightState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class TrafficLightTest {

    private TrafficLight trafficLight;

    @BeforeEach
    void setup() {
        this.trafficLight = new TrafficLight(Direction.EAST);
    }

    @Test
    void test_Direction() {
        boolean isValidDirection = Arrays.stream(Direction.values()).anyMatch(d -> d == trafficLight.getDirection());

        Assertions.assertTrue(isValidDirection);
    }

    @Test
    void test_DefaultState_MustBeRed() {
        final TrafficLightState state = trafficLight.getState();

        Assertions.assertEquals(Signal.RED, state.getSignal());
    }

    @Test
    void test_NewStateUpdate() {
        final Signal expected = Signal.GREEN;

        trafficLight.setState(TrafficLightState.getState(expected));

        final TrafficLightState state = trafficLight.getState();

        Assertions.assertEquals(expected, state.getSignal());
    }
}
