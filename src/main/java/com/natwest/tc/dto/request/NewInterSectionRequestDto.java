package com.natwest.tc.dto.request;

import java.io.Serializable;
import java.util.List;

public class NewInterSectionRequestDto implements Serializable {
    private final List<SequenceRequestDto> sequences;

    public NewInterSectionRequestDto(final List<SequenceRequestDto> sequences) {
        this.sequences = sequences;
    }

    public List<SequenceRequestDto> getSequences() {
        return sequences;
    }
}
