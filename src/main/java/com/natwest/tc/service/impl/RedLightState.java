package com.natwest.tc.service.impl;

import com.natwest.tc.constants.Signal;
import com.natwest.tc.model.TrafficLight;
import com.natwest.tc.service.Intersection;
import com.natwest.tc.service.TrafficLightState;

public class RedLightState implements TrafficLightState {
    private final Signal color;

    public RedLightState() {
        this.color = Signal.RED;
    }

    @Override
    public void next(TrafficLight trafficLight) {
        trafficLight.setState(new GreenLightState());
    }

    @Override
    public Signal getSignal() {
        return this.color;
    }
}
