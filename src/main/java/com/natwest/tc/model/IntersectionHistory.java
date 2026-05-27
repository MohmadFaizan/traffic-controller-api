package com.natwest.tc.model;

import com.natwest.tc.constants.Constants;
import com.natwest.tc.constants.Direction;
import com.natwest.tc.constants.Signal;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

public class IntersectionHistory implements Serializable {
    private String intersectionId;
    private Set<Direction> directions;
    private Signal state;
    private String mode;
    private String time;
    private String paused;

    public String getIntersectionId() {
        return intersectionId;
    }

    public void setIntersectionId(String intersectionId) {
        this.intersectionId = intersectionId;
    }

    public Set<Direction> getDirections() {
        return directions;
    }

    public void setDirections(Set<Direction> directions) {
        this.directions = directions;
    }

    public Signal getState() {
        return state;
    }

    public void setState(Signal state) {
        this.state = state;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time.toString();
    }

    public String getPaused() {
        return this.paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused ? Constants.PAUSED : Constants.RUNNING;
    }
}
