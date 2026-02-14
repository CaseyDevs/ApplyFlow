package com.casey.applyflow.controller;

import com.casey.applyflow.dto.InterviewRequestDto;
import com.casey.applyflow.dto.InterviewResponseDto;
import com.casey.applyflow.service.InterviewService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/api")
public class InterviewController {
    private final InterviewService interviewService;

    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }
    
    @GetMapping("/interviews/{id}")
    public ResponseEntity<InterviewResponseDto> getInterview (
        @Valid @PathVariable Long interviewId
    ) {
        InterviewResponseDto response = interviewService.getInterview(interviewId);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("applications/{id}/interviews")
    public ResponseEntity<InterviewResponseDto> createInterview(
        @Valid @PathVariable Long applicationId,
        @RequestBody InterviewRequestDto request
    ) {
        InterviewResponseDto response = interviewService.createInterview(applicationId, request);
        
        return ResponseEntity.ok(response);
    }
    
    
}