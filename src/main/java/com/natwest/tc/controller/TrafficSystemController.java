package com.natwest.tc.controller;

import com.natwest.tc.dto.request.SequenceRequestDto;
import com.natwest.tc.dto.response.CreateIntersectionResponse;
import com.natwest.tc.dto.response.DeleteIntersectionResponse;
import com.natwest.tc.dto.response.IntersectionStatusResponse;
import com.natwest.tc.dto.response.UpdateIntersectionResponse;
import com.natwest.tc.service.IntersectionControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/intersection")
public class TrafficSystemController {

    @Autowired
    private IntersectionControlService intersectionControlService;

    @PostMapping(value = {"/v1/create"})
    public ResponseEntity<CreateIntersectionResponse> createNewIntersection() {
        final String id = intersectionControlService.createNewInterSection();

        CreateIntersectionResponse response = new CreateIntersectionResponse(id, "Intersection Created");

        return ResponseEntity.ok(response);
    }

    @PutMapping(value = {"/v1/{id}"}, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UpdateIntersectionResponse> updateIntersection(@PathVariable("id") final String id,
                                                            @RequestBody SequenceRequestDto sequence) {
        intersectionControlService.updateIntersectionSequence(id, sequence);

        final UpdateIntersectionResponse response = new UpdateIntersectionResponse(id, "Intersection Updated");

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(value = {"/v1/{id}"})
    public ResponseEntity<DeleteIntersectionResponse> deleteIntersection(@PathVariable("id") final String id) {
        final String currentState = intersectionControlService.deleteIntersection(id);

        DeleteIntersectionResponse response = new DeleteIntersectionResponse(id, currentState);

        return ResponseEntity.ok(response);
    }

    @GetMapping(value = {"/v1/{id}/pause"})
    public ResponseEntity<String> pauseIntersection(@PathVariable("id") final String id) {
        intersectionControlService.pauseIntersection(id);

        return ResponseEntity.ok("Intersection Paused");
    }

    @GetMapping(value = {"/v1/{id}/resume"})
    public ResponseEntity<String> resumeIntersection(@PathVariable("id") final String id) {
        intersectionControlService.resumeIntersection(id);

        return ResponseEntity.ok("Intersection Resumed");
    }

    @GetMapping(value = {"/v1/{id}/status"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IntersectionStatusResponse> currentStatus(@PathVariable("id") final String id) {
        final IntersectionStatusResponse currentState = intersectionControlService.currentStatus(id);

        return ResponseEntity.ok(currentState);
    }

    @GetMapping(value = {"/v1"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getAllIntersection() {
        final List<String> ids = intersectionControlService.getAllIntersectionIds();

        return ResponseEntity.ok(ids);
    }
}
