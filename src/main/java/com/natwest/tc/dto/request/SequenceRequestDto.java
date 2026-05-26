package com.natwest.tc.dto.request;

import com.natwest.tc.constants.Direction;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

public class SequenceRequestDto implements Serializable {
    private final String direction;
    private final String color;

    public SequenceRequestDto(String direction, String color) {
        this.direction = direction;
        this.color = color;
    }

    public String getDirection() {
        return direction;
    }

    public String getColor() {
        return color;
    }
}
