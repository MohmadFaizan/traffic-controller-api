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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Intersection {
    private final String id;
    private final ConcurrentHashMap<Direction, TrafficLight> signals = new ConcurrentHashMap<>();
    private List<SignalPhase> phases;

    private final AtomicInteger currentPhase = new AtomicInteger(0);
    private final AtomicBoolean isPaused = new AtomicBoolean(false);

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition resumeCondition;

    public Intersection(@Nonnull Consumer<Runnable> task, final List<SignalPhase> phases) {
        this.id = UUID.randomUUID().toString();

        for (Direction d : Direction.values()) {
            signals.put(d, new TrafficLight(d));
        }

        updateSequence(phases);
        this.resumeCondition = lock.newCondition();

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
        while (!Thread.currentThread().isInterrupted()) {
            if (CollectionUtils.isEmpty(this.phases)) {
                continue;
            }

            try {
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
            }
        }
    }

    private void updateAllRed() {
        this.signals.values().forEach(light -> light.setState(Signal.RED));
    }

    private void awaitIfPaused() throws InterruptedException {
        while (this.isPaused.get()) {
            resumeCondition.await();
        }
    }

    private void delay(final int delayed) throws InterruptedException {
        Thread.sleep(delayed * 1_000L);
    }

    private void updatePhase(final SignalPhase phase, final Signal signal) throws InterruptedException {
        try {
            awaitIfPaused();

            lock.lock();
            signals.values().stream()
                    .filter(light -> phase.getDirections().contains(light.getDirection()))
                    .forEach(light -> light.setState(signal));
        } finally {
            lock.unlock();
        }
    }

    public Map<Direction, Signal> getState() {
        return this.signals.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getState()));
    }

    public void pause() {
        if (!this.isPaused.get()) {
            this.isPaused.set(true);

            updateAllRed();
        }
    }

    public void resume() {
        if (this.isPaused.get()) {
            this.isPaused.set(false);
            resumeCondition.signalAll();
        }
    }
}
