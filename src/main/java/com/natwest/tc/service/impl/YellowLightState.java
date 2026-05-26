package com.natwest.tc.service.impl;

import com.natwest.tc.constants.Signal;
import com.natwest.tc.model.TrafficLight;
import com.natwest.tc.service.TrafficLightState;

public class YellowLightState implements TrafficLightState {
    private final Signal color;

    public YellowLightState() {
        this.color = Signal.YELLOW;
    }

    @Override
    public void next(final TrafficLight trafficLight) {
        trafficLight.setState(new RedLightState());
    }

    @Override
    public Signal getSignal() {
        return this.color;
    }
}
