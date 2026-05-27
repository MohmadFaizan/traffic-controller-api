package com.natwest.tc.model;

import com.natwest.tc.constants.Direction;
import com.natwest.tc.constants.Signal;
import com.natwest.tc.exceptions.ConflictStateUpdateException;
import com.natwest.tc.service.Intersection;
import org.junit.jupiter.api.*;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IntersectionTest {

    private ExecutorService executor;
    private Intersection intersection;

    @BeforeEach
    void setup() {
        intersection = new Intersection();
        executor = Executors.newSingleThreadExecutor();

        executor.submit(intersection);
    }

    @AfterEach
    void destroy() {
        executor.shutdown();
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
    public void test_IntersectionState() {
        Map<Direction, Signal> state = intersection.getState();

        Assertions.assertNotNull(state);
    }

    @Test
    public void test_Intersection_WhenConflictUpdate() {
        intersection.pause();

        Map<Direction, Signal> state = intersection.getState();

        final Direction d = state.entrySet().stream()
                .filter(e -> e.getValue() == Signal.RED)
                .map(Map.Entry::getKey)
                .findFirst().orElse(null);

        Assertions.assertThrows(ConflictStateUpdateException.class, () -> intersection.updateSignal(d, Signal.GREEN));
    }
}
