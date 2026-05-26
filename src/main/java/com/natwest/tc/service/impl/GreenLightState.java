package com.natwest.tc.service.impl;

import com.natwest.tc.constants.Signal;
import com.natwest.tc.model.TrafficLight;
import com.natwest.tc.service.TrafficLightState;

public class GreenLightState implements TrafficLightState {
    private final Signal color;

    public GreenLightState() {
        this.color = Signal.GREEN;
    }

    @Override
    public void next(final TrafficLight trafficLight) {
        trafficLight.setState(new YellowLightState());
    }

    @Override
    public Signal getSignal() {
        return this.color;
    }
}
