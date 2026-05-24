package com.natwest.tc.service;

import com.natwest.tc.constants.Direction;
import com.natwest.tc.model.BasicTrafficLight;
import com.natwest.tc.model.TrafficLight;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

@Service
public class Intersection {
    private final ConcurrentHashMap<Direction, TrafficLight> controller = new ConcurrentHashMap<>();
    private final ExecutorService machine;

    public Intersection(@Nonnull final ExecutorService executor) {
        for (Direction d : Direction.values()) {
            controller.put(d, new BasicTrafficLight(d));
        }

        this.machine = executor;
    }

}
