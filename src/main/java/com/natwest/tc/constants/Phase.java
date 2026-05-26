package com.natwest.tc.constants;

import java.util.Set;

public enum Phase {
    NS_PHASE(Set.of(Direction.NORTH, Direction.SOUTH)),
    EW_PHASE(Set.of(Direction.WEST, Direction.EAST));

    private final Set<Direction> directions;

    Phase(final Set<Direction> directions) {
        this.directions = directions;
    }

    public Set<Direction> getDirections() {
        return directions;
    }
}
