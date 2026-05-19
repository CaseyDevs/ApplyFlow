package com.casey.applyflow.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.casey.applyflow.dto.JobBoardStatusResponseDto;
import com.casey.applyflow.exception.ApplicationNotFoundException;
import com.casey.applyflow.exception.NotAMemberException;
import com.casey.applyflow.model.JobBoardApplication;
import com.casey.applyflow.model.JobBoardApplicationStatus;
import com.casey.applyflow.model.User;
import com.casey.applyflow.model.enums.Status;
import com.casey.applyflow.repository.ApplicationRepository;
import com.casey.applyflow.repository.JobBoardApplicationRepository;
import com.casey.applyflow.repository.JobBoardApplicationStatusRepository;
import com.casey.applyflow.repository.JobBoardMemberRepository;
import com.casey.applyflow.repository.JobBoardRepository;

@Service
public class JobBoardApplicationStatusService {
    private final JobBoardApplicationStatusRepository jobBoardApplicationStatusRepository;
    private final CurrentUserProvider currentUserProvider;
    private final JobBoardApplicationRepository jobBoardApplicationRepository;
    private final JobBoardMemberRepository jobBoardMemberRepository;

    public JobBoardApplicationStatusService(
        ApplicationRepository applicationRepository,
        CurrentUserProvider currentUserProvider,
        JobBoardApplicationStatusRepository jobBoardApplicationStatusRepository,
        JobBoardRepository jobBoardRepository,
        JobBoardApplicationRepository jobBoardApplicationRepository,
        JobBoardMemberRepository jobBoardMemberRepository
    ) {
        this.jobBoardApplicationStatusRepository = jobBoardApplicationStatusRepository;
        this.currentUserProvider = currentUserProvider;
        this.jobBoardApplicationRepository = jobBoardApplicationRepository;
        this.jobBoardMemberRepository = jobBoardMemberRepository;
    }

    @Transactional(readOnly = true)
    public List<JobBoardStatusResponseDto> getAllJobBoardApplicationStatuses(Long jobBoardApplicationId) {
        // Get the JobBoardApplication
        JobBoardApplication jobBoardApp = jobBoardApplicationRepository.findById(jobBoardApplicationId)
            .orElseThrow(() -> new ApplicationNotFoundException("Job board application does not exist"));
        
        // Verify current user is a member of this board
        User currentUser = currentUserProvider.getCurrentUser();
        jobBoardMemberRepository.findByJobBoardIdAndUserId(jobBoardApp.getJobBoard().getId(), currentUser.getId())
            .orElseThrow(() -> new NotAMemberException("User is not a member of this job board"));
        
        // Get the statuses
        return jobBoardApplicationStatusRepository
            .findAllByJobBoardApplication(jobBoardApp)
            .stream()
            .map(this::toJobBoardStatusResponseDto)
            .toList();
    }

    @Transactional
    public JobBoardStatusResponseDto updateApplicationStatus(Long jobBoardApplicationId, String status) {
        // Get the JobBoardApplication
        JobBoardApplication jobBoardApp = jobBoardApplicationRepository.findById(jobBoardApplicationId)
            .orElseThrow(() -> new ApplicationNotFoundException("Job board application does not exist"));
        
        // Verify current user is a member of this board
        User currentUser = currentUserProvider.getCurrentUser();
        jobBoardMemberRepository.findByJobBoardIdAndUserId(jobBoardApp.getJobBoard().getId(), currentUser.getId())
            .orElseThrow(() -> new NotAMemberException("User is not a member of this job board"));
        
        // Find existing status or create new one
        JobBoardApplicationStatus appStatus = jobBoardApplicationStatusRepository
            .findByJobBoardApplicationIdAndUserId(jobBoardApplicationId, currentUser.getId())
            .orElseGet(() -> {
                JobBoardApplicationStatus newStatus = new JobBoardApplicationStatus(currentUser, Status.valueOf(status));
                newStatus.setJobBoardApplication(jobBoardApp);
                return newStatus;
            });
        
        // Update the status
        appStatus.setStatus(Status.valueOf(status));
        appStatus.setUpdatedAt(java.time.LocalDateTime.now());
        appStatus.setUpdatedBy(currentUser.getEmail());
        
        jobBoardApplicationStatusRepository.save(appStatus);
        return toJobBoardStatusResponseDto(appStatus);
    }

    // dto mapper
    public JobBoardStatusResponseDto toJobBoardStatusResponseDto(JobBoardApplicationStatus status) {
        return new JobBoardStatusResponseDto (
            status.getId(),
            status.getJobBoardApplication().getId(),
            status.getUser().getId(),
            status.getUser().getEmail(),
            status.getStatus(),
            status.getUpdatedAt(),
            status.getUpdatedBy()
        );
    }
}
