package com.natwest.tc.constants;

import com.natwest.tc.exceptions.InvalidSignalStateException;
import org.springframework.util.StringUtils;

import java.io.Serializable;

public enum Signal implements Serializable {
    RED, GREEN, YELLOW;

    @Override
    public String toString() {
        return super.toString();
    }

    public static Signal get(final String name) {
        if (!StringUtils.hasText(name)) {
            return null;
        }

        try {
            return Signal.valueOf(name);
        } catch (IllegalArgumentException e) {
            throw new InvalidSignalStateException("Invalid Signal Name");
        }
    }

    public boolean isGreen() {
        return this == Signal.GREEN;
    }

    public boolean isYellow() {
        return this == Signal.YELLOW;
    }

    public boolean isRed() {
        return this == Signal.RED;
    }
}
