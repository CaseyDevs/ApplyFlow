package com.casey.applyflow.controller;

import com.casey.applyflow.dto.InterviewRequestDto;
import com.casey.applyflow.dto.InterviewResponseDto;
import com.casey.applyflow.service.InterviewService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api")
@Validated
public class InterviewController {
    private final InterviewService interviewService;

    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }
    
    @GetMapping("applications/{applicationId}/interviews/{interviewId}")
    public ResponseEntity<InterviewResponseDto> getInterview(
        @PathVariable @Min(1) Long applicationId,
        @PathVariable @Min(1) Long interviewId
    ) {
        InterviewResponseDto response = interviewService.getInterview(applicationId, interviewId);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("applications/{applicaitonId}/interviews")
    public ResponseEntity<List<InterviewResponseDto>> getAllInterviews(
        @PathVariable @Min(1) Long applicationId
    ) {
        List<InterviewResponseDto> response = interviewService.getAllInterviews(applicationId);

        return ResponseEntity.ok(response);
    }
    

    @PostMapping("/applications/{applicationId}/interviews")
    public ResponseEntity<InterviewResponseDto> createInterview(
        @PathVariable @Min(1) Long applicationId,
        @Valid @RequestBody InterviewRequestDto request
    ) {
        InterviewResponseDto response = interviewService.createInterview(applicationId, request);
        return ResponseEntity.created(
            ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/interviews/{id}")
                .buildAndExpand(response.id())
                .toUri()
        ).body(response);
    }

    @PutMapping("/interviews/{interviewId}")
    public ResponseEntity<InterviewResponseDto> updateInterview(
        @PathVariable @Min(1) Long interviewId, 
        @Valid @RequestBody InterviewRequestDto request
    ) {
        InterviewResponseDto response = interviewService.updateInterview(interviewId, request);
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/applications/{applicationId}/interviews/{interviewId}")
    public ResponseEntity<Void> deleteInterview(
        @PathVariable @Min(1) Long applicationId,
        @PathVariable @Min(1) Long interviewId
    ) {
        interviewService.deleteInterview(applicationId, interviewId);
        
        return ResponseEntity.noContent().build();
    }
}