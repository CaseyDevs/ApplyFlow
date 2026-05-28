package com.casey.applyflow.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.casey.applyflow.dto.JobBoardStatusResponseDto;
import com.casey.applyflow.exception.ApplicationNotFoundException;
import com.casey.applyflow.exception.NotAMemberException;
import com.casey.applyflow.mapper.DtoMapper;
import com.casey.applyflow.model.JobBoardApplication;
import com.casey.applyflow.model.JobBoardApplicationStatus;
import com.casey.applyflow.model.User;
import com.casey.applyflow.model.enums.Status;
import com.casey.applyflow.repository.JobBoardApplicationRepository;
import com.casey.applyflow.repository.JobBoardApplicationStatusRepository;
import com.casey.applyflow.repository.JobBoardMemberRepository;

@Service
public class JobBoardApplicationStatusService {
    private final CurrentUserProvider currentUserProvider;
    private final DtoMapper dtoMapper;
    private final JobBoardApplicationStatusRepository jobBoardApplicationStatusRepository;
    private final JobBoardApplicationRepository jobBoardApplicationRepository;
    private final JobBoardMemberRepository jobBoardMemberRepository;

    public JobBoardApplicationStatusService(
        CurrentUserProvider currentUserProvider,
        DtoMapper dtoMapper,
        JobBoardApplicationStatusRepository jobBoardApplicationStatusRepository,
        JobBoardApplicationRepository jobBoardApplicationRepository,
        JobBoardMemberRepository jobBoardMemberRepository
    ) {
        this.currentUserProvider = currentUserProvider;
        this.dtoMapper = dtoMapper;
        this.jobBoardApplicationStatusRepository = jobBoardApplicationStatusRepository;
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
            .map(dtoMapper::toJobBoardStatusResponseDto)
            .toList();
    }

    // force propagation
    @Transactional(propagation = Propagation.REQUIRED)
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
        return dtoMapper.toJobBoardStatusResponseDto(appStatus);
    }
}
