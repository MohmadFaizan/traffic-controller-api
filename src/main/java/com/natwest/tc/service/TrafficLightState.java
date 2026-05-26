package com.natwest.tc.service;

import com.natwest.tc.constants.Signal;
import com.natwest.tc.model.TrafficLight;
import com.natwest.tc.service.impl.GreenLightState;
import com.natwest.tc.service.impl.RedLightState;
import com.natwest.tc.service.impl.YellowLightState;

public interface TrafficLightState {

    static TrafficLightState getState(final Signal color) {
        return switch(color) {
            case RED -> new RedLightState();
            case GREEN -> new GreenLightState();
            case YELLOW -> new YellowLightState();
        };
    }

    void next(final TrafficLight trafficLight);

    Signal getSignal();
}
