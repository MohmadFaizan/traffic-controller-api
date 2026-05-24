package com.natwest.tc.service;

import com.natwest.tc.dto.request.NewInterSectionRequestDto;
import com.natwest.tc.dto.request.SequenceRequestDto;
import com.natwest.tc.exceptions.InvalidSignalDelayException;
import com.natwest.tc.model.SignalPhase;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

@Service
public class IntersectionControlService {

    private final ConcurrentHashMap<String, Intersection> intersections = new ConcurrentHashMap<>();
    private final ExecutorService executor;

    public IntersectionControlService(final ExecutorService executor) {
        this.executor = executor;
    }

    public void createNewInterSection(final NewInterSectionRequestDto interSectionRequestDto) {
        final List<SequenceRequestDto> sequences = interSectionRequestDto.getSequences() != null ?
                interSectionRequestDto.getSequences() : new ArrayList<>();

        final LinkedList<SignalPhase> phases = new LinkedList<>();

        try {
            for (final SequenceRequestDto seq : sequences) {
                final SignalPhase phase = new SignalPhase(seq.getDirections());

                phase.setGreenDelayInSec(seq.getGreenDelayInSec());
                phase.setYellowDelayInSec(seq.getYellowDelayInSec());

                phases.add(phase);
            }

            final Intersection intersection = new Intersection(executor::submit, phases);

            this.intersections.put(intersection.getId(), intersection);
        } catch (IllegalArgumentException e) {
            throw new InvalidSignalDelayException(e.getMessage());
        }
    }
}
