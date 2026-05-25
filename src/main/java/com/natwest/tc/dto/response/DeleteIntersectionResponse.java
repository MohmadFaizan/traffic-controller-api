package com.natwest.tc.dto.response;

import java.io.Serializable;

public class DeleteIntersectionResponse implements Serializable {
    private final String id;
    private final String state;

    public DeleteIntersectionResponse(String id, String state) {
        this.id = id;
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public String getId() {
        return id;
    }
}
