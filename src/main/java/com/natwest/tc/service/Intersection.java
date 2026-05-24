package com.natwest.tc.service;

import com.natwest.tc.constants.Direction;
import com.natwest.tc.constants.Signal;
import com.natwest.tc.model.SignalPhase;
import com.natwest.tc.model.TrafficLight;
import jakarta.annotation.Nonnull;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public class Intersection {
    private final String id;
    private final ConcurrentHashMap<Direction, TrafficLight> signals = new ConcurrentHashMap<>();
    private List<SignalPhase> phases;

    private final AtomicInteger currentPhase = new AtomicInteger(0);

    private final ReentrantLock lock = new ReentrantLock();

    public Intersection(@Nonnull Consumer<Runnable> task, final List<SignalPhase> phases) {
        this.id = UUID.randomUUID().toString();

        for (Direction d : Direction.values()) {
            signals.put(d, new TrafficLight(d));
        }

        updateSequence(phases);

        task.accept(this::initiateCycle);
    }

    public String getId() {
        return this.id;
    }

    public void updateSequence(final List<SignalPhase> phases) {
        if (CollectionUtils.isEmpty(phases)) {
            return;
        }

        try {
            lock.lock();
            this.phases = Collections.unmodifiableList(phases);
        } finally {
            lock.unlock();
        }
    }

    private void initiateCycle() {
        while (true) {
            if (CollectionUtils.isEmpty(this.phases)) {
                continue;
            }

            try {
                lock.lock();

                final SignalPhase phase = this.phases.get(currentPhase.get());

                updateToGreenPhase(phase);

                delay(phase.getGreenDelayInSec());

                updateToYellowPhase(phase);

                delay(phase.getYellowDelayInSec());

                updateAllRed();

                currentPhase.set((currentPhase.get() + 1) % this.phases.size());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }
        }
    }

    private void updateToYellowPhase(final SignalPhase phase) {
        this.signals.values().stream()
                .filter(light -> phase.getDirections().contains(light.getDirection()))
                .forEach(light -> light.setState(Signal.YELLOW));
    }

    private void delay(final int greenDelayInSec) throws InterruptedException {
        Thread.sleep(greenDelayInSec * 1_000L);
    }

    private void updateToGreenPhase(final SignalPhase phase) {
        updateAllRed();

        signals.values().stream()
                .filter(light -> phase.getDirections().contains(light.getDirection()))
                .forEach(light -> light.setState(Signal.GREEN));
    }

    private void updateAllRed() {
        this.signals.values().forEach(light -> light.setState(Signal.RED));
    }
}
