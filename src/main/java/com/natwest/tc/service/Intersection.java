package com.natwest.tc.service;

import com.natwest.tc.constants.Direction;
import com.natwest.tc.constants.Signal;
import com.natwest.tc.model.BasicTrafficLight;
import com.natwest.tc.model.TrafficLight;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class Intersection {
    private final ConcurrentHashMap<Direction, TrafficLight> machine = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(1);

    public Intersection() {
        for (Direction d : Direction.values()) {
            machine.put(d, new BasicTrafficLight(d));
        }
    }

    
}
