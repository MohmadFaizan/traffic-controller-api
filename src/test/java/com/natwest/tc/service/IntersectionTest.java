package com.natwest.tc.service;

import com.natwest.tc.constants.Constants;
import com.natwest.tc.constants.Direction;
import com.natwest.tc.constants.Signal;
import com.natwest.tc.model.SignalPhase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IntersectionTest {
    private ExecutorService executor;
    private Intersection intersection;

    private long start;
    @BeforeEach
    void setup() {
        this.executor = Executors.newSingleThreadExecutor();
        this.start = System.currentTimeMillis();

        this.intersection = new Intersection();
    }

    @AfterEach
    void destroy() {
        this.executor.shutdown();
        this.executor.shutdownNow();
    }

    @Test
    void test_Intersection_Creation() {
        assertNotNull(this.intersection.getId(), "Expected Intersection ID to be Present");
    }

    @Test
    void test_GreenState_WithDefaultDelay() {
        final SignalPhase nsPhase = new SignalPhase(Set.of(Direction.NORTH, Direction.SOUTH));
        final SignalPhase ewPhase = new SignalPhase(Set.of(Direction.EAST, Direction.WEST));

        final List<SignalPhase> phases = new LinkedList<>();
        phases.add(nsPhase);
        phases.add(ewPhase);

        this.intersection.updateSequence(phases);

        final Map<Direction, Signal> current = this.intersection.getState();

        final long diffSec = (System.currentTimeMillis() - this.start) / 1000;

        final boolean isNSGreen = diffSec < Constants.DEFAULT_GREEN_DELAY
                && current.get(Direction.NORTH) == Signal.GREEN && current.get(Direction.SOUTH) == Signal.GREEN;

        assertTrue(isNSGreen, () -> "Expected NS Green");
    }

    @Test
    void test_YellowState_WithDefaultDelay() throws InterruptedException {
        final SignalPhase nsPhase = new SignalPhase(Set.of(Direction.NORTH, Direction.SOUTH));
        final SignalPhase ewPhase = new SignalPhase(Set.of(Direction.EAST, Direction.WEST));

        final List<SignalPhase> phases = new LinkedList<>();
        phases.add(nsPhase);
        phases.add(ewPhase);

        this.intersection.updateSequence(phases);

        Thread.sleep(Constants.DEFAULT_GREEN_DELAY * 1000);

        final Map<Direction, Signal> current = this.intersection.getState();

        final long diffSec = (System.currentTimeMillis() - this.start) / 1000;

        final boolean isNSYellow = diffSec >= Constants.DEFAULT_GREEN_DELAY
                && current.get(Direction.NORTH) == Signal.YELLOW && current.get(Direction.SOUTH) == Signal.YELLOW;

        assertTrue(isNSYellow, () -> "Expected NS Yellow");
    }

    @Test
    void test_RedState_WithDefaultDelay() throws InterruptedException {
        final SignalPhase nsPhase = new SignalPhase(Set.of(Direction.NORTH, Direction.SOUTH));
        final SignalPhase ewPhase = new SignalPhase(Set.of(Direction.EAST, Direction.WEST));

        final List<SignalPhase> phases = new LinkedList<>();
        phases.add(nsPhase);
        phases.add(ewPhase);

        this.intersection.updateSequence(phases);

        final int delay = Constants.DEFAULT_GREEN_DELAY + Constants.DEFAULT_YELLOW_DELAY + 1;

        Thread.sleep(delay * 1000);

        final Map<Direction, Signal> current = this.intersection.getState();

        final long diffSec = (System.currentTimeMillis() - this.start) / 1000;

        final boolean isNSRed = diffSec >= delay && current.get(Direction.NORTH) == Signal.RED && current.get(Direction.SOUTH) == Signal.RED;

        assertTrue(isNSRed, () -> "Expected NS Red");
    }

    @ParameterizedTest
    @ValueSource(strings = {"10 2"})
    void test_YellowState_WithConfiguredDelay(final String delayTime) throws InterruptedException {
        final int GREEN_DELAY = Integer.parseInt(delayTime.split(" ")[0]);
        final int YELLOW_DELAY = Integer.parseInt(delayTime.split(" ")[1]);

        final SignalPhase nsPhase = new SignalPhase(Set.of(Direction.NORTH, Direction.SOUTH));
        nsPhase.setGreenDelayInSec(GREEN_DELAY);
        nsPhase.setYellowDelayInSec(YELLOW_DELAY);

        final List<SignalPhase> phases = new LinkedList<>();
        phases.add(nsPhase);

        this.intersection.updateSequence(phases);

        Thread.sleep(GREEN_DELAY * 1000L);

        final Map<Direction, Signal> current = this.intersection.getState();

        final long diffSec = (System.currentTimeMillis() - this.start) / 1000;

        final boolean isNSYellow = diffSec >= GREEN_DELAY && current.get(Direction.NORTH) == Signal.YELLOW && current.get(Direction.SOUTH) == Signal.YELLOW;

        assertTrue(isNSYellow, () -> "Expected NS Yellow");
    }

    @Test
    void test_PauseState_WithDefaultDelay() throws InterruptedException {
        final SignalPhase nsPhase = new SignalPhase(Set.of(Direction.NORTH, Direction.SOUTH));

        final List<SignalPhase> phases = new LinkedList<>();
        phases.add(nsPhase);

        this.intersection.updateSequence(phases);

        final int delay = Constants.DEFAULT_GREEN_DELAY;

        // Wait for signal to turn GREEN
        Thread.sleep(delay * 100L);

        this.intersection.pause();

        final Map<Direction, Signal> current = this.intersection.getState();

        final boolean isAllRed = current.values().stream().allMatch(signal -> Signal.RED == signal);

        assertTrue(isAllRed, () -> "Expected ALL Red");
    }
}
