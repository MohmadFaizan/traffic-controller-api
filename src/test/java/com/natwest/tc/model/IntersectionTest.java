package com.natwest.tc.model;

import com.natwest.tc.constants.Direction;
import com.natwest.tc.constants.Signal;
import com.natwest.tc.exceptions.ConflictStateUpdateException;
import com.natwest.tc.exceptions.InvalidStateException;
import com.natwest.tc.service.Intersection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class IntersectionTest {

    private Intersection intersection;

    @BeforeEach
    void setup() {
        intersection = new Intersection();
    }

    @Test
    public void test_NewIntersection() {
        Assertions.assertNotNull(intersection.getId());
    }

    @Test
    public void test_IntersectionIsNotPaused() {
        Assertions.assertFalse(intersection.isPaused());
    }

    @Test
    public void test_IntersectionIsPaused() {
        intersection.pause();

        Assertions.assertTrue(intersection.isPaused());
    }

    @Test
    public void test_IntersectionIsResumed() throws InterruptedException {
        intersection.pause();

        Thread.sleep(2000);

        intersection.resume();

        Assertions.assertFalse(intersection.isPaused());
    }

    @Test
    public void test_IntersectionInitialState_Red() {
        Map<Direction, Signal> state = intersection.getState();

        final boolean activeSignalFound = state.values().stream().anyMatch(s -> !s.isRed());

        Assertions.assertFalse(activeSignalFound);
    }

    @Test
    public void test_Intersection_TargetState() {
        final Direction direction = Direction.NORTH;
        final Signal signal = Signal.GREEN;

        intersection.updateSignal(direction, signal);

        Map<Direction, Signal> state = intersection.getState();

        final boolean activeSignalFound = state.entrySet().stream()
                .anyMatch(e -> e.getKey() == direction && e.getValue() == signal);

        Assertions.assertTrue(activeSignalFound);
    }

    @Test
    public void test_Intersection_WhenConflictUpdate() {
        intersection.updateSignal(Direction.NORTH, Signal.GREEN);

        Assertions.assertThrows(ConflictStateUpdateException.class,
                () -> intersection.updateSignal(Direction.EAST, Signal.GREEN));
    }

    @Test
    public void test_Intersection_ShouldUpdate_WhenNoConflict() {
        intersection.updateSignal(Direction.NORTH, Signal.GREEN);

        intersection.updateSignal(Direction.EAST, Signal.RED);
    }

    @Test
    public void test_Intersection_When_UpdateInPauseMode() {
        intersection.updateSignal(Direction.NORTH, Signal.GREEN);

        intersection.pause();

        Assertions.assertThrows(InvalidStateException.class,
                () -> intersection.updateSignal(Direction.EAST, Signal.RED));
    }
}
