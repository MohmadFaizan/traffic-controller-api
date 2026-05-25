package com.natwest.tc.dto.response;

import java.io.Serializable;

public class UpdateIntersectionResponse implements Serializable {

    private final String id;
    private final String message;

    public UpdateIntersectionResponse(String id, String message) {
        this.id = id;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }
}
