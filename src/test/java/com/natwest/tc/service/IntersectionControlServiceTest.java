package com.natwest.tc.service;

import com.natwest.tc.constants.Direction;
import com.natwest.tc.dto.request.NewInterSectionRequestDto;
import com.natwest.tc.dto.request.SequenceRequestDto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IntersectionControlServiceTest {

    private static IntersectionControlService service;
    private static final ExecutorService executor = Executors.newCachedThreadPool();

    @BeforeAll
    static void setup() {
        service = new IntersectionControlService(executor);
    }

    @AfterAll
    static void destroy() {
        executor.shutdown();
        executor.shutdownNow();
    }

    @Test
    public void test_CreateNewIntersection() {
        final List<SequenceRequestDto> sequences = new LinkedList<>();

        sequences.add(new SequenceRequestDto(Set.of(Direction.SOUTH, Direction.NORTH)));
        sequences.add(new SequenceRequestDto(Set.of(Direction.EAST, Direction.WEST)));

        final NewInterSectionRequestDto dto = new NewInterSectionRequestDto(sequences);

        final String id = service.createNewInterSection(dto);

        Assertions.assertNotNull(id, () -> "Expected Intersection Id");
    }
}
