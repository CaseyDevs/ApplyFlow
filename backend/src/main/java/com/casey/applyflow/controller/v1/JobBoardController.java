package com.casey.applyflow.controller.v1;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.casey.applyflow.dto.ApplicationResponseDto;
import com.casey.applyflow.dto.AddJobBoardMemberRequestDto;
import com.casey.applyflow.dto.JobBoardRequestDto;
import com.casey.applyflow.dto.JobBoardResponseDto;
import com.casey.applyflow.dto.JobBoardStatsDto;
import com.casey.applyflow.service.JobBoardService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

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

    @GetMapping("/job-boards/{jobBoardId}")
    public ResponseEntity<JobBoardResponseDto> getJobBoardById(
        @PathVariable @Min(1) Long jobBoardId
    ) {
        
        return ResponseEntity.ok(jobBoardService.getJobBoardById(jobBoardId));
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
    
    @DeleteMapping("/job-boards/{jobBoardId}")
    public ResponseEntity<Void> deleteJobBoard(
        @PathVariable Long jobBoardId
    ) {

        jobBoardService.deleteJobBoard(jobBoardId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/job-boards/{jobBoardId}/members")
    public ResponseEntity<Void> addJobBoardMember(
        @PathVariable Long jobBoardId,
        @Valid @RequestBody AddJobBoardMemberRequestDto request
    ) {

        jobBoardService.addMember(jobBoardId, request.email());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/job-boards/{jobBoardId}/invitation")
    public ResponseEntity<String> handleInvitation(
        @PathVariable @Min(1) Long jobBoardId,
        @RequestParam String token
    ) {

        try {
            jobBoardService.acceptInvitation(jobBoardId, token);
            return ResponseEntity.ok("Job board invitation accepted!");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
    

    @DeleteMapping("/job-boards/{jobBoardId}/members/{jobBoardMemberId}")
    public ResponseEntity<Void> removeJobBoardMember(
        @PathVariable @Min(1) Long jobBoardId,
        @PathVariable @Min(1) Long jobBoardMemberId
    ) {

        jobBoardService.removeMember(jobBoardId, jobBoardMemberId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/job-boards/{jobBoardId}/members/{jobBoardMemberId}")
    public ResponseEntity<Void> setNewJobBoardOwner(
        @PathVariable @Min(1) Long jobBoardId,
        @PathVariable @Min(1) Long jobBoardMemberId
    ) {

        jobBoardService.setNewOwner(jobBoardId, jobBoardMemberId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/job-boards/{jobBoardId}/applications/{applicationId}")
    public ResponseEntity<Void> addApplicationToJobBoard(
        @PathVariable @Min(1) Long jobBoardId,
        @PathVariable @Min(1) Long applicationId
    ) {

        jobBoardService.addApplicationToJobBoard(jobBoardId, applicationId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/job-boards/{jobBoardId}/applications/{applicationId}")
    public ResponseEntity<Void> removeApplicationFromJobBoard(
        @PathVariable @Min(1) Long jobBoardId,
        @PathVariable @Min(1) Long applicationId
    ) {

        jobBoardService.removeApplicationFromJobBoard(jobBoardId, applicationId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/job-boards/{jobBoardId}")
    public ResponseEntity<Void> leaveJobBoard(
        @PathVariable @Min(1) Long jobBoardId
    ) {

        jobBoardService.leaveJobBoard(jobBoardId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/job-boards/{jobBoardId}/stats")
    public ResponseEntity<JobBoardStatsDto> getJobBoardStats(
        @PathVariable @Min(1) Long jobBoardId
    ) {

        return ResponseEntity.ok(jobBoardService.getJobBoardStats(jobBoardId));
    }
    
    
}
