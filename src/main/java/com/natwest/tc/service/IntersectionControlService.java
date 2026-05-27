package com.natwest.tc.service;

import com.natwest.tc.constants.Constants;
import com.natwest.tc.constants.Direction;
import com.natwest.tc.constants.Signal;
import com.natwest.tc.dto.request.SequenceRequestDto;
import com.natwest.tc.dto.response.IntersectionStatusResponse;
import com.natwest.tc.exceptions.IntersectionNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

@Service
public class IntersectionControlService {

    private final ConcurrentHashMap<String, Intersection> intersections = new ConcurrentHashMap<>();
    private final ExecutorService executor;

    public IntersectionControlService(final ExecutorService executor) {
        this.executor = executor;
    }

    public String createNewInterSection() {
        final Intersection intersection = new Intersection();

        this.intersections.put(intersection.getId(), intersection);

        executor.submit(intersection);

        return intersection.getId();

    }

    public void pauseIntersection(final String id) {
        validateIntersectionId(id);

        final Intersection intersection = intersections.get(id);
        intersection.pause();
    }

    public void resumeIntersection(final String id) {
        validateIntersectionId(id);

        final Intersection intersection = intersections.get(id);
        intersection.resume();
    }

    public String updateIntersectionSequence(final String id, final SequenceRequestDto sequence) {
        validateIntersectionId(id);

        final Intersection intersection = intersections.get(id);

        final Direction direction = Direction.get(sequence.getDirection());
        final Signal signal = Signal.get(sequence.getColor());

        intersection.updateSignal(direction, signal);

        return intersection.getState().toString();
    }

    private void validateIntersectionId(final String id) {
        if (!intersections.containsKey(id)) {
            throw new IntersectionNotFoundException("Intersection Details Not Found");
        }
    }

    public String deleteIntersection(final String id) {
        validateIntersectionId(id);

        final Intersection intersection = intersections.get(id);
        intersection.stop();

        intersections.remove(id);

        return intersection.getState().toString();
    }

    public IntersectionStatusResponse currentStatus(final String id) {
        validateIntersectionId(id);

        final Intersection intersection = intersections.get(id);

        final boolean isPaused = intersection.isPaused();
        final String status = isPaused ? Constants.PAUSED : Constants.RUNNING;

        final String state = intersection.getState().toString();

        return new IntersectionStatusResponse(status, state);
    }
}
