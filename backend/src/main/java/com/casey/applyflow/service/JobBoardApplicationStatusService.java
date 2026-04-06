package com.casey.applyflow.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.casey.applyflow.exception.ApplicationNotFoundException;
import com.casey.applyflow.exception.JobBoardNotFoundException;
import com.casey.applyflow.model.JobBoardApplicationStatus;
import com.casey.applyflow.model.JobBoard;
import com.casey.applyflow.model.User;
import com.casey.applyflow.repository.ApplicationRepository;
import com.casey.applyflow.repository.JobBoardApplicationStatusRepository;
import com.casey.applyflow.repository.JobBoardRepository;

@Service
public class JobBoardApplicationStatusService {
    private ApplicationRepository applicationRepository;
    private CurrentUserProvider currentUserProvider;
    private JobBoardRepository jobBoardRepository;
    private JobBoardApplicationStatusRepository jobBoardApplicationStatusRepository;


    public JobBoardApplicationStatusService(
        ApplicationRepository applicationRepository,
        CurrentUserProvider currentUserProvider,
        JobBoardApplicationStatusRepository jobBoardApplicationStatusRepository,
        JobBoardRepository jobBoardRepository
    ) {
        this.applicationRepository = applicationRepository;
        this.currentUserProvider = currentUserProvider;
        this.jobBoardApplicationStatusRepository = jobBoardApplicationStatusRepository;
        this.jobBoardRepository = jobBoardRepository;
    }

    // TODO: RETURN A DTO
    @Transactional(readOnly = true)
    public List<JobBoardApplicationStatus> getAllJobBoardApplicationStatuses(Long jobBoardId, Long applicationId) {
        if (jobBoardId == null) {
            throw new IllegalArgumentException("Job board ID cannot be null");
        }

        if (applicationId == null) {
            throw new IllegalArgumentException("Application ID cannot be null");
        }

        User currentUser = currentUserProvider.getCurrentUser();

        // check user is a member of the board
        JobBoard jobBoard = jobBoardRepository.findByIdAndMembersUserId(jobBoardId, currentUser.getId())
            .orElseThrow(() -> new JobBoardNotFoundException("You are not a member of this job board!"));

        applicationRepository.findById(applicationId)
            .orElseThrow(() -> new ApplicationNotFoundException("Application does not exist with id: " + applicationId));

        // check job board contains the given application
        boolean applicationBelongsToJobBoard = jobBoard.getApplications().stream()
            .anyMatch(application -> application.getId().equals(applicationId));

        if (!applicationBelongsToJobBoard) {
            throw new ApplicationNotFoundException("Application is not on this job board.");
        }

        return jobBoardApplicationStatusRepository
            .findAllByJobBoardIdAndApplicationId(jobBoardId, applicationId)
            .orElseGet(List::of); // Empty list 
    }
}
