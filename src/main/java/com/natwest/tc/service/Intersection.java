package com.natwest.tc.service;

import com.natwest.tc.constants.Direction;
import com.natwest.tc.constants.Signal;
import com.natwest.tc.exceptions.ConflictStateUpdateException;
import com.natwest.tc.exceptions.InvalidStateException;
import com.natwest.tc.model.SignalPhase;
import com.natwest.tc.model.TrafficLight;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class Intersection implements Runnable {
    private final String id;
    private final Map<Direction, TrafficLight> signals = new EnumMap<>(Direction.class);
    private final List<SignalPhase> phases = new ArrayList<>(2);

    private final AtomicInteger currentPhaseIndex = new AtomicInteger(0);
    private final AtomicBoolean paused = new AtomicBoolean(false);
    private final AtomicBoolean running = new AtomicBoolean(true);

    private final ReentrantLock lock = new ReentrantLock();
    private Thread controlThread;

    public Intersection() {
        this.id = UUID.randomUUID().toString();

        for (Direction d : Direction.values()) {
            signals.put(d, new TrafficLight(d));
        }

        this.phases.add(new SignalPhase(Set.of(Direction.NORTH, Direction.SOUTH)));
        this.phases.add(new SignalPhase(Set.of(Direction.EAST, Direction.WEST)));
    }

    @Override
    public void run() {
        controlThread = Thread.currentThread();
        while (this.running.get() && !controlThread.isInterrupted()) {
            final SignalPhase phase = this.phases.get(this.currentPhaseIndex.get());

            delay(2);

            updatePhase(phase);
        }
    }

    private SignalPhase getActivePhase() {
        final Direction activeDirection = getState().entrySet().stream()
                .filter(t -> t.getValue() != Signal.RED)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);

        if (activeDirection == null) {
            return null;
        }

        return getPhaseByDirection(activeDirection);
    }

    private void updateCurrentPhaseIndex() {
        this.currentPhaseIndex.set((this.currentPhaseIndex.get() + 1) % this.phases.size());
    }

    public String getId() {
        return this.id;
    }

    public void updateSignal(final Direction targetDirection, final Signal targetColor) {
        final SignalPhase activePhase = getActivePhase();

        final SignalPhase targetPhase = getPhaseByDirection(targetDirection);

        lock.lock();
        try {
            if (activePhase == null && targetColor == Signal.RED) {
                // All Directions RED, no update required
                return;
            }

            validateConflict(activePhase, targetDirection, targetColor);

            this.signals.values().stream()
                    .filter(t -> targetPhase.getDirections().contains(t.getDirection()))
                    .forEach(t -> t.setState(TrafficLightState.getState(targetColor)));
        } finally {
            this.lock.unlock();
        }
    }

    private void validateConflict(final SignalPhase activePhase, final Direction targetDirection,
                                  final Signal targetColor) {
        if (activePhase != null && !activePhase.getDirections().contains(targetDirection)
                && Signal.RED != targetColor) {
            throw new ConflictStateUpdateException("Direction Conflict Detected");
        }
    }

    private SignalPhase getPhaseByDirection(final Direction direction) {
        lock.lock();
        try {
            return this.phases.stream()
                    .filter(p -> p.getDirections().contains(direction))
                    .findFirst()
                    .get();
        } finally {
            lock.unlock();
        }
    }

    private void awaitIfPaused() {
        while (this.paused.get()) {
            delay(1);
        }
    }

    private void delay(final int delayed) {
        try {
            Thread.sleep(delayed * 1_000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void updatePhase(final SignalPhase phase) {
        awaitIfPaused();

        final boolean[] isYellow = {false};

        lock.lock();
        try {
            signals.values().stream()
                    .filter(light -> phase.getDirections().contains(light.getDirection()))
                    .peek(light -> isYellow[0] = (light.getState().getSignal() == Signal.YELLOW))
                    .forEach(light -> light.getState().next(light));

        } finally {
            lock.unlock();
        }

        if (isYellow[0]) {
            updateCurrentPhaseIndex();
        }
    }

    public Map<Direction, Signal> getState() {
        lock.lock();
        try {
            return this.signals.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey,
                            e -> e.getValue().getState().getSignal()));
        } finally {
            lock.unlock();
        }
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
        if (this.controlThread != null) {
            lock.lock();
            try { this.controlThread.interrupt(); } finally { lock.unlock(); }
        }
    }
}
