package com.casey.applyflow.controller.v1;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.casey.applyflow.dto.ApplicationResponseDto;
import com.casey.applyflow.dto.JobBoardRequestDto;
import com.casey.applyflow.dto.JobBoardResponseDto;
import com.casey.applyflow.service.JobBoardService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/v1")
public class JobBoardController {
    private final JobBoardService jobBoardService;

    public JobBoardController(
        JobBoardService jobBoardService
    ) {
        this.jobBoardService = jobBoardService;
    }

    @GetMapping("/job-boards")
    public ResponseEntity<Page<JobBoardResponseDto>> getJobBoards(
        Pageable pageable
    ) {

        return ResponseEntity.ok(jobBoardService.getAllJobBoards(pageable));
    }

    @GetMapping("/job-boards/{jobBoardId}/applications")
    public ResponseEntity<Page<ApplicationResponseDto>> getJobBoardApplications(
        Pageable pageable,
        @PathVariable Long jobBoardId
    ) {

        return ResponseEntity.ok(jobBoardService.getAllJobBoardApplications(jobBoardId, pageable));
    }

    @PostMapping("/job-boards")
    public ResponseEntity<JobBoardResponseDto> createJobBoard (
        @Valid @RequestBody JobBoardRequestDto request
    ) {
        
        return ResponseEntity.ok(jobBoardService.createJobBoard(request));
    }

    @PutMapping("/job-boards/{jobBoardId}")
    public ResponseEntity<JobBoardResponseDto> updateJobBoard(
        @PathVariable Long jobBoardId, 
        @Valid @RequestBody JobBoardRequestDto request
    ) {

        return ResponseEntity.ok(jobBoardService.updateJobBoard(jobBoardId, request));
    }
    
    @DeleteMapping("/job-boards/{jobBoardId")
        public ResponseEntity<Void> deleteJobBoard(
            @PathVariable Long jobBoardId
        ) {

            jobBoardService.deleteJobBoard(jobBoardId);
            return ResponseEntity.noContent().build();
        }
    }
