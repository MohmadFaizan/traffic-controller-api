package com.natwest.tc.service;

import com.natwest.tc.constants.Direction;
import com.natwest.tc.constants.Signal;
import com.natwest.tc.model.SignalPhase;
import com.natwest.tc.model.TrafficLight;
import jakarta.annotation.Nonnull;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Intersection {
    private final String id;
    private final ConcurrentHashMap<Direction, TrafficLight> signals = new ConcurrentHashMap<>();
    private List<SignalPhase> phases;

    private final AtomicInteger currentPhase = new AtomicInteger(0);

    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

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

        ReentrantReadWriteLock.WriteLock lock = readWriteLock.writeLock();
        try {
            lock.lock();
            this.phases = Collections.unmodifiableList(phases);
        } finally {
            lock.unlock();
        }
    }

    private void initiateCycle() {
        while (!Thread.currentThread().isInterrupted()) {
            if (CollectionUtils.isEmpty(this.phases)) {
                continue;
            }

            ReentrantReadWriteLock.ReadLock lock = readWriteLock.readLock();
            try {
                lock.lock();
                final SignalPhase phase = this.phases.get(currentPhase.get());

                updatePhase(phase, Signal.GREEN);

                delay(phase.getGreenDelayInSec());

                updatePhase(phase, Signal.YELLOW);

                delay(phase.getYellowDelayInSec());

                updatePhase(phase, Signal.RED);

                currentPhase.set((currentPhase.get() + 1) % this.phases.size());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } finally {
                lock.unlock();
            }
        }
    }

    private void delay(final int delayed) throws InterruptedException {
        Thread.sleep(delayed * 1_000L);
    }

    private void updatePhase(final SignalPhase phase, final Signal signal) {
        signals.values().stream()
                .filter(light -> phase.getDirections().contains(light.getDirection()))
                .forEach(light -> light.setState(signal));
    }

    public Map<Direction, Signal> getState() {
        return this.signals.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getState()));
    }
}
