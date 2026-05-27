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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.stream.Stream;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TrafficSystemControllerTest {

    @LocalServerPort
    private int port;

    private RestTestClient client;
    private String intersectionId;

    @BeforeEach
    public void setup() {
        this.client = RestTestClient.bindToServer()
                .baseUrl("http://localhost:%d/api/intersection".formatted(port))
                .build();

        this.intersectionId = client.post()
                .uri("/v1/create")
                .exchange()
                .expectStatus().isOk()
                .expectBody(CreateIntersectionResponse.class)
                .returnResult()
                .getResponseBody()
                .getId();
    }

    @Test
    public void test_CreateIntersection_ResponseOk() {
        client.post()
                .uri("/v1/create")
                .exchange()
                .expectStatus().isOk()
                .expectBody(CreateIntersectionResponse.class)
                .value(value -> {
                    Assertions.assertNotNull(value);
                    Assertions.assertNotNull(value.getId());
                });
    }

    @Test
    public void test_CreateIntersection_ShouldReturnId() {
        CreateIntersectionResponse response = client.post()
                .uri("/v1/create")
                .exchange()
                .expectStatus().isOk()
                .expectBody(CreateIntersectionResponse.class)
                .returnResult()
                .getResponseBody();

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getId());
    }

    @Test
    public void test_DeleteIntersection_ShouldResponseOk() {
        DeleteIntersectionResponse response = client.delete()
                .uri("/v1/" + intersectionId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(DeleteIntersectionResponse.class)
                .returnResult()
                .getResponseBody();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(intersectionId, response.getId());
    }

    @Test
    public void test_InvalidIntersectionId_ShouldReturn_BadRequest() {
        test_DeleteIntersection_ShouldResponseOk();

        client.get()
                .uri("/v1/%s/pause".formatted(intersectionId))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .value(value -> {
                    Assertions.assertNotNull(value);
                    Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), value.getCode());
                });
    }

    @Test
    public void test_PauseIntersection_ShouldResponseOk() {
        client.get()
                .uri("/v1/%s/pause".formatted(intersectionId))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(Assertions::assertNotNull);
    }

    @Test
    public void test_PausedStateIntersection_ShouldResponseOk() {
        test_PauseIntersection_ShouldResponseOk();

        final IntersectionStatusResponse response = client.get()
                .uri("/v1/%s/status".formatted(intersectionId))
                .exchange()
                .expectStatus().isOk()
                .expectBody(IntersectionStatusResponse.class)
                .value(Assertions::assertNotNull)
                .returnResult()
                .getResponseBody();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(Constants.PAUSED, response.getStatus());
    }

    @Test
    public void test_RunningStateIntersection_ShouldResponseOk() {
        final IntersectionStatusResponse response = client.get()
                .uri("/v1/%s/status".formatted(intersectionId))
                .exchange()
                .expectStatus().isOk()
                .expectBody(IntersectionStatusResponse.class)
                .value(Assertions::assertNotNull)
                .returnResult()
                .getResponseBody();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(Constants.RUNNING, response.getStatus());
    }

    @ParameterizedTest
    @MethodSource("getSequence")
    public void test_ConflictStateUpdate_ShouldResponseBadRequest(final SequenceRequestDto requestDto) {
        test_PauseIntersection_ShouldResponseOk();

        client.put()
                .uri("/v1/%s".formatted(intersectionId))
                .body(requestDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class);
    }

    @Test
    public void test_InvalidDirectionInput_ShouldResponseBadRequest() {
        final SequenceRequestDto requestDto = new SequenceRequestDto("SOUTH_EAST", Signal.RED.toString());

        client.put()
                .uri("/v1/%s".formatted(intersectionId))
                .body(requestDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class);
    }

    @Test
    public void test_InvalidSignalInput_ShouldResponseBadRequest() {
        final SequenceRequestDto requestDto = new SequenceRequestDto(Direction.NORTH.toString(), "APPLE");

        client.put()
                .uri("/v1/%s".formatted(intersectionId))
                .body(requestDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class);
    }

    @Test
    public void test_MaxLimitReachedException() {
        for (int i = 0; i < 9; i++) {
            test_CreateIntersection_ResponseOk();
        }

        client.post()
                .uri("/v1/create")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .value(Assertions::assertNotNull);
    }

    public static Stream<SequenceRequestDto> getSequence() {
        return Stream.of(
                new SequenceRequestDto(Direction.EAST.toString(), Signal.GREEN.toString()),
                new SequenceRequestDto(Direction.WEST.toString(), Signal.YELLOW.toString())
        );
    }
}
