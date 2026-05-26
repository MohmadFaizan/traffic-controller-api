package com.natwest.tc.constants;

import com.natwest.tc.exceptions.InvalidDirectionException;
import org.springframework.util.StringUtils;

import java.io.Serializable;

public enum Direction implements Serializable {
    NORTH, SOUTH, EAST, WEST;

    @Override
    public String toString() {
        return super.toString();
    }

    public static Direction get(final String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }

        try {
            return Direction.valueOf(value);
        } catch (IllegalArgumentException ex) {
            throw new InvalidDirectionException("Invalid Direction Name");
        }
    }
}
