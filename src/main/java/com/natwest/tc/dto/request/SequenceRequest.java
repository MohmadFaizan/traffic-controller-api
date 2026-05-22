package com.natwest.tc.dto.request;

import java.io.Serializable;
import java.util.Set;

public class SequenceRequest implements Serializable {
    private String name;
    private Set<String> allowedDirections;

    public String getName() {
        return this.name;
    }

    public Set<String> getAllowedDirections() {
        return this.allowedDirections;
    }
}
