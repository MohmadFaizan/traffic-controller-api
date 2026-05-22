package com.natwest.tc.constants;

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
            throw new RuntimeException(e);
        }
    }
}
