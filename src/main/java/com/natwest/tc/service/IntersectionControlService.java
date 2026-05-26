package com.natwest.tc.service;

import com.natwest.tc.constants.Direction;
import com.natwest.tc.constants.Signal;
import com.natwest.tc.dto.request.NewInterSectionRequestDto;
import com.natwest.tc.dto.request.SequenceRequestDto;
import com.natwest.tc.exceptions.IntersectionNotFoundException;
import com.natwest.tc.exceptions.InvalidSignalDelayException;
import com.natwest.tc.model.SignalPhase;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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

    public String createNewInterSection(final NewInterSectionRequestDto interSectionRequestDto) {
        final List<SequenceRequestDto> sequences = interSectionRequestDto.getSequences() != null ?
                interSectionRequestDto.getSequences() : new ArrayList<>();

        try {
            final LinkedList<SignalPhase> phases = createSignalPhases(sequences);

            final Intersection intersection = new Intersection();

            this.intersections.put(intersection.getId(), intersection);

            return intersection.getId();
        } catch (IllegalArgumentException e) {
            throw new InvalidSignalDelayException(e.getMessage());
        }
    }

    private static LinkedList<SignalPhase> createSignalPhases(final List<SequenceRequestDto> sequences) {
        final LinkedList<SignalPhase> phases = new LinkedList<>();
        for (final SequenceRequestDto seq : sequences) {
//            final SignalPhase phase = new SignalPhase(seq.getDirections());
//
//            phase.setGreenDelayInSec(seq.getGreenDelayInSec());
//            phase.setYellowDelayInSec(seq.getYellowDelayInSec());
//
//            phases.add(phase);
        }

        return phases;
    }

    public Map<Direction, Signal> pauseIntersection(final String id) {
        validateIntersectionId(id);

        final Intersection intersection = intersections.get(id);
        intersection.pause();

        return intersection.getState();
    }

    public Map<Direction, Signal> resumeIntersection(final String id) {
        validateIntersectionId(id);

        final Intersection intersection = intersections.get(id);
        intersection.resume();

        return intersection.getState();
    }

    public String updateIntersectionSequence(final String id, final List<SequenceRequestDto> sequences) {
        validateIntersectionId(id);

        final Intersection intersection = intersections.get(id);

        final LinkedList<SignalPhase> phases = createSignalPhases(sequences);

//        intersection.updateSequence(phases);

        return intersection.getState().toString();
    }

    private void validateIntersectionId(final String id) {
        if (!intersections.containsKey(id)) {
            throw new IntersectionNotFoundException("Invalid Intersection ID");
        }
    }

    public String deleteIntersection(final String id) {
        validateIntersectionId(id);

        final Intersection intersection = intersections.get(id);
        intersection.stop();

        intersections.remove(id);

        return intersection.getState().toString();
    }

    public String currentStatus(final String id) {
        validateIntersectionId(id);

        final Intersection intersection = intersections.get(id);

        return intersection.getState().toString();
    }
}
