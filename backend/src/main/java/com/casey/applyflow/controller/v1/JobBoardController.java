package com.casey.applyflow.controller.v1;

import com.casey.applyflow.service.JobBoardInvitationService;
import com.casey.applyflow.service.JobBoardOwnershipService;
import com.casey.applyflow.service.RateLimitingService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.casey.applyflow.dto.AddJobBoardMemberRequestDto;
import com.casey.applyflow.dto.InvitationDetailsDto;
import com.casey.applyflow.dto.JobBoardApplicationRequestDto;
import com.casey.applyflow.dto.JobBoardApplicationResponseDto;
import com.casey.applyflow.dto.JobBoardRequestDto;
import com.casey.applyflow.dto.JobBoardResponseDto;
import com.casey.applyflow.dto.JobBoardStatsDto;
import com.casey.applyflow.dto.JobBoardStatusResponseDto;
import com.casey.applyflow.service.JobBoardApplicationService;
import com.casey.applyflow.service.JobBoardApplicationStatusService;
import com.casey.applyflow.service.JobBoardMemberService;
import com.casey.applyflow.service.JobBoardService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import java.util.Map;

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
    private final JobBoardOwnershipService jobBoardOwnershipService;
    private final JobBoardInvitationService jobBoardInvitationService;
    private final RateLimitingService rateLimitingService;
    private final JobBoardService jobBoardService;
    private final JobBoardApplicationService jobBoardApplicationService;
    private final JobBoardApplicationStatusService jobBoardApplicationStatusService;
    private final JobBoardMemberService jobBoardMemberService;

    public JobBoardController(
        JobBoardService jobBoardService,
        JobBoardApplicationService jobBoardApplicationService,
        JobBoardApplicationStatusService jobBoardApplicationStatusService,
        JobBoardMemberService jobBoardMemberService, 
        RateLimitingService rateLimitingService, 
        JobBoardInvitationService jobBoardInvitationService, 
        JobBoardOwnershipService jobBoardOwnershipService
    ) {
        this.jobBoardService = jobBoardService;
        this.jobBoardApplicationService = jobBoardApplicationService;
        this.jobBoardApplicationStatusService = jobBoardApplicationStatusService;
        this.jobBoardInvitationService = jobBoardInvitationService;
        this.jobBoardMemberService = jobBoardMemberService;
        this.jobBoardOwnershipService = jobBoardOwnershipService;
        this.rateLimitingService = rateLimitingService;
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
    public ResponseEntity<Void> inviteJobBoardMember(
        HttpServletRequest httpRequest,
        @PathVariable Long jobBoardId,
        @Valid @RequestBody AddJobBoardMemberRequestDto request
    ) {
        // limit requests via client IP
        rateLimitingService.checkRateLimit(httpRequest, "invitations", 6, 4, 1);

        jobBoardInvitationService.inviteMember(jobBoardId, request.email());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/job-boards/{jobBoardId}/invitation")
    public ResponseEntity<InvitationDetailsDto> handleGetInvitation(
        @PathVariable @Min(1) Long jobBoardId,
        @RequestParam String token
    ) {
        return ResponseEntity.ok(jobBoardInvitationService.getInvitation(jobBoardId, token));
    }

    @PostMapping("/job-boards/{jobBoardId}/invitation/accept")
    public ResponseEntity<Void> handleAcceptInvitation(
        @PathVariable @Min(1) Long jobBoardId,
        @RequestParam String token
    ) {
        jobBoardInvitationService.acceptInvitation(jobBoardId, token);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/job-boards/{jobBoardId}/invitation")
    public ResponseEntity<Void> handleRejectInvitation(
        @PathVariable @Min(1) Long jobBoardId,
        @RequestParam String token
    ) {
        jobBoardInvitationService.rejectInvitation(token);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping("/job-boards/{jobBoardId}/members/{jobBoardMemberId}")
    public ResponseEntity<Void> removeJobBoardMember(
        @PathVariable @Min(1) Long jobBoardId,
        @PathVariable @Min(1) Long jobBoardMemberId
    ) {

        jobBoardMemberService.removeMember(jobBoardId, jobBoardMemberId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/job-boards/{jobBoardId}/members/{jobBoardMemberId}")
    public ResponseEntity<Void> setNewJobBoardOwner(
        @PathVariable @Min(1) Long jobBoardId,
        @PathVariable @Min(1) Long jobBoardMemberId
    ) {

        jobBoardOwnershipService.setNewOwner(jobBoardId, jobBoardMemberId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/job-boards/{jobBoardId}/applications")
    public ResponseEntity<Page<JobBoardApplicationResponseDto>> getJobBoardApplications(
        Pageable pageable,
        @PathVariable Long jobBoardId
    ) {

        return ResponseEntity.ok(jobBoardApplicationService.getAllJobBoardApplications(jobBoardId, pageable));
    }

    @GetMapping("job-boards/{jobBoardId}/applications/{jobBoardApplicationId}")
    public ResponseEntity<JobBoardApplicationResponseDto> getJobBoardApplicationById (
        @PathVariable @Min(1) Long jobBoardId,
        @PathVariable @Min(1) Long jobBoardApplicationId
    ) {

        return ResponseEntity.ok(jobBoardApplicationService.getJobBoardApplicationById(jobBoardApplicationId, jobBoardId));
    }
    
    @PostMapping("/job-boards/{jobBoardId}/applications/{applicationId}")
    public ResponseEntity<Void> addApplicationToJobBoard(
        @PathVariable @Min(1) Long jobBoardId,
        @PathVariable @Min(1) Long applicationId
    ) {

        JobBoardApplicationRequestDto request = new JobBoardApplicationRequestDto(applicationId, jobBoardId);

        jobBoardApplicationService.addApplicationToJobBoard(request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/job-boards/{jobBoardId}/applications/{applicationId}")
    public ResponseEntity<Void> removeApplicationFromJobBoard(
        @PathVariable @Min(1) Long jobBoardId,
        @PathVariable @Min(1) Long applicationId
    ) {

        jobBoardApplicationService.removeApplicationFromJobBoard(jobBoardId, applicationId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/job-boards/{jobBoardId}/applications/{jobBoardApplicationId}/status")
    public ResponseEntity<JobBoardStatusResponseDto> updateApplicationStatus(
        @PathVariable @Min(1) Long jobBoardId,
        @PathVariable @Min(1) Long jobBoardApplicationId,
        @RequestBody Map<String, String> request
    ) {
        String status = request.get("status");
        return ResponseEntity.ok(jobBoardApplicationStatusService.updateApplicationStatus(jobBoardApplicationId, status));
    }

    @PostMapping("/job-boards/{jobBoardId}")
    public ResponseEntity<Void> leaveJobBoard(
        @PathVariable @Min(1) Long jobBoardId
    ) {

        jobBoardMemberService.leaveJobBoard(jobBoardId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/job-boards/{jobBoardId}/stats")
    public ResponseEntity<JobBoardStatsDto> getJobBoardStats(
        @PathVariable @Min(1) Long jobBoardId
    ) {

        return ResponseEntity.ok(jobBoardService.getJobBoardStats(jobBoardId));
    }
    
    
}
