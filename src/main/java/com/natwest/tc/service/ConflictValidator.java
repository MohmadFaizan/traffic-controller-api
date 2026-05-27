package com.natwest.tc.service;

import com.natwest.tc.constants.Direction;
import com.natwest.tc.exceptions.ConflictStateUpdateException;
import com.natwest.tc.model.TrafficLight;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ConflictValidator {
    private static final Map<Direction, Set<Direction>> conflicts = Map.of(
            Direction.NORTH, Set.of(Direction.EAST, Direction.WEST),
            Direction.SOUTH, Set.of(Direction.EAST, Direction.WEST),
            Direction.EAST, Set.of(Direction.NORTH, Direction.SOUTH),
            Direction.WEST, Set.of(Direction.NORTH, Direction.SOUTH)
    );

    public static void validate(final Map<Direction, TrafficLight> signals) {
        Set<Direction> activeDirections = signals.entrySet().stream()
                .filter(e -> !e.getValue().getState().getSignal().isRed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        for (final Direction green : activeDirections) {
            final Set<Direction> conflicted = conflicts.get(green);

            for (final Direction d : activeDirections) {
                if (d == green) continue;

                if (conflicted.contains(d)) {
                    throw new ConflictStateUpdateException("Direction Conflict Detected between %s -> %s"
                            .formatted(green, d));
                }
            }
        }
    }
}
