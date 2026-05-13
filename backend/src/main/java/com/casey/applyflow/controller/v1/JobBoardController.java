package com.casey.applyflow.controller.v1;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.casey.applyflow.dto.ApplicationResponseDto;
import com.casey.applyflow.dto.AddJobBoardMemberRequestDto;
import com.casey.applyflow.dto.InvitationDetailsDto;
import com.casey.applyflow.dto.JobBoardRequestDto;
import com.casey.applyflow.dto.JobBoardResponseDto;
import com.casey.applyflow.dto.JobBoardStatsDto;
import com.casey.applyflow.exception.RateLimitExceededException;
import com.casey.applyflow.service.JobBoardApplicationService;
import com.casey.applyflow.service.JobBoardMemberService;
import com.casey.applyflow.service.JobBoardService;
import com.casey.applyflow.utils.ClientIpProvider;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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
    private final JobBoardApplicationService jobBoardApplicationService;
    private final JobBoardMemberService jobBoardMemberService;
    private final Map<String, Bucket> invitationBuckets = new ConcurrentHashMap<>();
    private final Bandwidth invitationLimit;
    private final ClientIpProvider clientIpProvider;

    public JobBoardController(
        JobBoardService jobBoardService,
        JobBoardApplicationService jobBoardApplicationService,
        JobBoardMemberService jobBoardMemberService,
        ClientIpProvider clientIpProvider
    ) {
        this.jobBoardService = jobBoardService;
        this.jobBoardApplicationService = jobBoardApplicationService;
        this.jobBoardMemberService = jobBoardMemberService;
        this.clientIpProvider = clientIpProvider;

        this.invitationLimit = Bandwidth.builder()
            .capacity(6)
            .refillGreedy(2, Duration.ofMinutes(1))
            .build();
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

        return ResponseEntity.ok(jobBoardApplicationService.getAllJobBoardApplications(jobBoardId, pageable));
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
        // limit requests via clietn IP
        String clientIp = clientIpProvider.getClientIp(httpRequest);
        Bucket invitationBucket = invitationBuckets.computeIfAbsent(
            clientIp,
            ip -> Bucket.builder().addLimit(invitationLimit).build()
        );

        // return 429 if bucket is empty
        if (!invitationBucket.tryConsume(1)) {
            throw new RateLimitExceededException("Too many invitation requests, try again later.");
        }

        jobBoardMemberService.inviteMember(jobBoardId, request.email());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/job-boards/{jobBoardId}/invitation")
    public ResponseEntity<InvitationDetailsDto> handleGetInvitation(
        @PathVariable @Min(1) Long jobBoardId,
        @RequestParam String token
    ) {

        try {
            return ResponseEntity.ok(jobBoardMemberService.getInvitation(jobBoardId, token));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/job-boards/{jobBoardId}/invitation/accept")
    public ResponseEntity<Void> handleAcceptInvitation(
        @PathVariable @Min(1) Long jobBoardId,
        @RequestParam String token
    ) {

        try {
            jobBoardMemberService.acceptInvitation(jobBoardId, token);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/job-boards/{jobBoardId}/invitation")
    public ResponseEntity<Void> handleRejectInvitation(
        @PathVariable @Min(1) Long jobBoardId,
        @RequestParam String token
    ) {

        try {
            jobBoardMemberService.rejectInvitation(token);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
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

        jobBoardMemberService.setNewOwner(jobBoardId, jobBoardMemberId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/job-boards/{jobBoardId}/applications/{applicationId}")
    public ResponseEntity<Void> addApplicationToJobBoard(
        @PathVariable @Min(1) Long jobBoardId,
        @PathVariable @Min(1) Long applicationId
    ) {

        jobBoardApplicationService.addApplicationToJobBoard(jobBoardId, applicationId);
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
