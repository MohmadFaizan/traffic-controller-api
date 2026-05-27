package com.natwest.tc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PropertyConfig {

    @Value("${max.intersection.allowed:10}")
    private int maxIntersectionAllowed;

    public int getMaxIntersectionAllowed() {
        return maxIntersectionAllowed;
    }
}
