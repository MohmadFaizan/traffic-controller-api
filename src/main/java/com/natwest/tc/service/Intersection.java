package com.natwest.tc.service;

import com.natwest.tc.constants.Direction;
import com.natwest.tc.constants.Signal;
import com.natwest.tc.exceptions.ConflictStateUpdateException;
import com.natwest.tc.exceptions.InvalidStateException;
import com.natwest.tc.model.IntersectionHistory;
import com.natwest.tc.model.SignalPhase;
import com.natwest.tc.model.TrafficLight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class Intersection {
    private static final Logger logger = LoggerFactory.getLogger(Intersection.class);
    private final CopyOnWriteArrayList<IntersectionHistory> history = new CopyOnWriteArrayList<>();

    private final String id;
    private final Map<Direction, TrafficLight> signals = new EnumMap<>(Direction.class);
    private final List<SignalPhase> phases = new ArrayList<>(2);

    private final AtomicBoolean paused = new AtomicBoolean(false);
    private final AtomicBoolean running = new AtomicBoolean(true);

    private final ReentrantLock lock = new ReentrantLock();

    public Intersection() {
        this.id = UUID.randomUUID().toString();

        for (Direction d : Direction.values()) {
            signals.put(d, new TrafficLight(d));
        }

        this.phases.add(new SignalPhase(Set.of(Direction.NORTH, Direction.SOUTH)));
        this.phases.add(new SignalPhase(Set.of(Direction.EAST, Direction.WEST)));
    }

    public String getId() {
        return this.id;
    }

    public void updateSignal(final Direction targetDirection, final Signal targetColor) {
        if (this.isPaused()) {
            throw new InvalidStateException("Intersection is in Pause Mode");
        }

        lock.lock();
        try {
            final Set<Direction> phase = this.phases.stream()
                    .map(SignalPhase::getDirections)
                    .filter(directions -> directions.contains(targetDirection))
                    .flatMap(Set::stream)
                    .collect(Collectors.toSet());

            final TrafficLightState lastState = this.signals.values().stream()
                    .filter(trafficLight -> phase.contains(trafficLight.getDirection()))
                    .map(TrafficLight::getState)
                    .findFirst().orElse(null);

            this.signals.values().stream()
                    .filter(light -> phase.contains(light.getDirection()))
                    .forEach(t -> t.setState(TrafficLightState.getState(targetColor)));

            try {
                ConflictValidator.validate(this.signals);
            } catch (ConflictStateUpdateException e) {
                this.signals.values().stream()
                        .filter(light -> phase.contains(light.getDirection()))
                        .forEach(t -> t.setState(lastState));

                throw e;
            }

            final IntersectionHistory history = new IntersectionHistory();
            history.setIntersectionId(getId());
            history.setDirections(phase);
            history.setState(targetColor);
            history.setMode("MANUAL");
            history.setTime(LocalDateTime.now());

            this.history.add(history);
        } finally {
            this.lock.unlock();
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
        final IntersectionHistory intersectionHistory = new IntersectionHistory();
        intersectionHistory.setIntersectionId(getId());
        intersectionHistory.setTime(LocalDateTime.now());
        intersectionHistory.setPaused(true);

        boolean updated = this.paused.compareAndSet(false, true);

        if (updated) this.history.add(intersectionHistory);
    }

    public void resume() {
        final IntersectionHistory intersectionHistory = new IntersectionHistory();
        intersectionHistory.setIntersectionId(getId());
        intersectionHistory.setTime(LocalDateTime.now());
        intersectionHistory.setPaused(false);

        boolean updated = this.paused.compareAndSet(true, false);

        if (updated) this.history.add(intersectionHistory);
    }

    public void stop() {
        if (!this.running.get()) {
            throw new InvalidStateException("Intersection is already STOPPED");
        }

        this.running.set(false);
    }

    public boolean isPaused() {
        return this.paused.get();
    }

    public List<IntersectionHistory> getHistory() {
        return new ArrayList<>(this.history);
    }
}
