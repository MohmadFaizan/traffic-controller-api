package com.natwest.tc.dto.response;

import java.io.Serializable;

public class IntersectionStatusResponse implements Serializable {
    private final String status;
    private final String state;

    public IntersectionStatusResponse(final String status, final String state) {
        this.status = status;
        this.state = state;
    }

    public String getStatus() {
        return status;
    }

    public String getState() {
        return state;
    }
}
