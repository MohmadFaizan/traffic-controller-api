package com.natwest.tc.model;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class SignalCycle {
    private final int states;
    private final AtomicInteger currentIndex = new AtomicInteger(0);
    private final AtomicBoolean completed = new AtomicBoolean(false);

    public SignalCycle(final int n) {
        this.states = n;
    }

    public void next() {
        this.completed.set(false);
        final int curr = this.currentIndex.get();

        this.currentIndex.set((curr + 1) % this.states);

        this.completed.set(curr == this.states - 1);
    }

    public boolean isCompleted() {
        return this.completed.get();
    }
}
