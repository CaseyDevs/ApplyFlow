package com.casey.applyflow.controller;

import com.casey.applyflow.dto.InterviewRequestDto;
import com.casey.applyflow.dto.InterviewResponseDto;
import com.casey.applyflow.service.InterviewService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Validated
public class InterviewController {
    private final InterviewService interviewService;

    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }
    
    @GetMapping("/interviews/{interviewId}")
    public ResponseEntity<InterviewResponseDto> getInterview(
        @PathVariable @Min(1) Long interviewId
    ) {
        InterviewResponseDto response = interviewService.getInterview(interviewId);
        
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
}