package com.natwest.tc.controller;

import com.natwest.tc.constants.Constants;
import com.natwest.tc.constants.Direction;
import com.natwest.tc.constants.Signal;
import com.natwest.tc.dto.request.SequenceRequestDto;
import com.natwest.tc.dto.response.CreateIntersectionResponse;
import com.natwest.tc.dto.response.DeleteIntersectionResponse;
import com.natwest.tc.dto.response.ErrorResponse;
import com.natwest.tc.dto.response.IntersectionStatusResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Stream;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TrafficSystemControllerConcurrencyTest {

    @LocalServerPort
    private int port;

    @Value("${max.intersection.allowed}")
    private int maxIntersectionAllowed;

    private RestTestClient client;

    @BeforeEach
    public void setup() {
        this.client = RestTestClient.bindToServer()
                .baseUrl("http://localhost:%d/api/intersection".formatted(port))
                .build();
    }

    @Test
    public void test_ConcurrentCreateIntersection_ResponseOk() throws InterruptedException {
        final CyclicBarrier barrier = new CyclicBarrier(maxIntersectionAllowed);

        final CountDownLatch latch = new CountDownLatch(maxIntersectionAllowed);
        final ExecutorService es = Executors.newFixedThreadPool(maxIntersectionAllowed);

        for (int i = 1; i <= maxIntersectionAllowed; i++) {
            es.submit(() -> {
                try {
                    barrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }

                client.post()
                        .uri("/v1/create")
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody(CreateIntersectionResponse.class)
                        .value(value -> {
                            Assertions.assertNotNull(value);
                            Assertions.assertNotNull(value.getId());
                        });

                latch.countDown();
            });
        }

        latch.await();
        es.shutdown();
    }

    @Test
    public void test_IntersectionCountMatch() throws InterruptedException {
        test_ConcurrentCreateIntersection_ResponseOk();

        client.get()
                .uri("/v1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(List.class)
                .value(value -> {
                    Assertions.assertNotNull(value);
                    Assertions.assertEquals(maxIntersectionAllowed, value.size());
                });
    }
}
