package com.natwest.tc.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorConfig {

    @Autowired
    private PropertyConfig propertyConfig;

    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(propertyConfig.getMaxIntersectionAllowed());
    }
}
