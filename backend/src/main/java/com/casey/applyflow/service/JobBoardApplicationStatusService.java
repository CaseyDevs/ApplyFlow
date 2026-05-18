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
