package com.natwest.tc.service;

import com.natwest.tc.constants.Direction;
import com.natwest.tc.constants.Signal;
import com.natwest.tc.exceptions.InvalidStateException;
import com.natwest.tc.model.SignalPhase;
import com.natwest.tc.model.TrafficLight;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class Intersection implements Runnable {
    private final String id;
    private final Map<Direction, TrafficLight> signals = new EnumMap<>(Direction.class);
    private List<SignalPhase> phases;

    private final AtomicInteger currentPhaseIndex = new AtomicInteger(0);
    private final AtomicBoolean paused = new AtomicBoolean(false);
    private final AtomicBoolean running = new AtomicBoolean(true);

    private final ReentrantLock lock = new ReentrantLock();

    public Intersection() {
        this.id = UUID.randomUUID().toString();

        for (Direction d : Direction.values()) {
            signals.put(d, new TrafficLight(d));
        }
    }

    @Override
    public void run() {
        while (this.running.get()) {
            try {
                final SignalPhase phase = this.phases.get(currentPhaseIndex.get());

                updatePhase(phase, Signal.GREEN);

                delay(phase.getGreenDelayInSec());

                updatePhase(phase, Signal.YELLOW);

                delay(phase.getYellowDelayInSec());

                updatePhase(phase, Signal.RED);

                this.currentPhaseIndex.set((this.currentPhaseIndex.get() + 1) % this.phases.size());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
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

    private void updateAllRed() {
        this.signals.values().forEach(light -> light.setState(Signal.RED));
    }

    private void awaitIfPaused() throws InterruptedException {
        while (this.paused.get()) {
            delay(1);
        }
    }

    private void delay(final int delayed) throws InterruptedException {
        Thread.sleep(delayed * 1_000L);
    }

    private void updatePhase(final SignalPhase phase, final Signal signal) throws InterruptedException {
        awaitIfPaused();

        signals.values().stream()
                .filter(light -> phase.getDirections().contains(light.getDirection()))
                .forEach(light -> light.setState(signal));

        delay(1);
    }

    public Map<Direction, Signal> getState() {
        return this.signals.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getState()));
    }

    public void pause() {
        this.paused.compareAndSet(false, true);
    }

    public void resume() {
        this.paused.compareAndSet(true, false);
    }

    public void stop() {
        if (!this.running.get()) {
            throw new InvalidStateException("Intersection is already STOPPED");
        }

        this.running.set(false);
    }
}
