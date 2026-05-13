package com.casey.applyflow.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.casey.applyflow.dto.ApplicationResponseDto;
import com.casey.applyflow.exception.ApplicationNotFoundException;
import com.casey.applyflow.exception.JobBoardNotFoundException;
import com.casey.applyflow.exception.NotAMemberException;
import com.casey.applyflow.model.Application;
import com.casey.applyflow.model.JobBoard;
import com.casey.applyflow.model.User;
import com.casey.applyflow.repository.ApplicationRepository;
import com.casey.applyflow.repository.JobBoardMemberRepository;
import com.casey.applyflow.repository.JobBoardRepository;

@Service
public class JobBoardApplicationService {
    private static final Logger log = LoggerFactory.getLogger(JobBoardApplicationService.class);
    
    private final JobBoardRepository jobBoardRepository;
    private final JobBoardMemberRepository jobBoardMemberRepository;
    private final ApplicationRepository applicationRepository;
    private final CurrentUserProvider currentUserProvider;
    private final ApplicationService applicationService;

    public JobBoardApplicationService(
        JobBoardRepository jobBoardRepository,
        JobBoardMemberRepository jobBoardMemberRepository,
        ApplicationRepository applicationRepository,
        CurrentUserProvider currentUserProvider,
        ApplicationService applicationService
    ) {
        this.jobBoardRepository = jobBoardRepository;
        this.jobBoardMemberRepository = jobBoardMemberRepository;
        this.applicationRepository = applicationRepository;
        this.currentUserProvider = currentUserProvider;
        this.applicationService = applicationService;
    }

    @Transactional(readOnly = true)
    public Page<ApplicationResponseDto> getAllJobBoardApplications(Long jobBoardId, Pageable pageable) {
        if (jobBoardId == null) {
            throw new IllegalArgumentException("Job board ID cannot be null");
        }

        User currentUser = currentUserProvider.getCurrentUser();

        log.info("Fetching applications for job board {} for user {}", jobBoardId, currentUser.getId());

        JobBoard jobBoard = getJobBoardForMember(jobBoardId, currentUser.getId());

        return new PageImpl<>(
            jobBoard.getApplications().stream()
                .map(applicationService::toApplicationResponseDto)
                .toList(),
            pageable,
            jobBoard.getApplications().size()
        );
    }

    @Transactional
    public void addApplicationToJobBoard(Long jobBoardId, Long applicationId) {
        if (jobBoardId == null) {
            throw new IllegalArgumentException("Job board ID cannot be null");
        }
        if (applicationId == null) {
            throw new IllegalArgumentException("Application ID cannot be null");
        }

        User currentUser = currentUserProvider.getCurrentUser();

        Application application = applicationRepository.findByIdAndUserId(applicationId, currentUser.getId())
            .orElseThrow(() -> new ApplicationNotFoundException("Application not found"));

        JobBoard jobBoard = getJobBoardForMember(jobBoardId, currentUser.getId());

        log.info("Adding application {} to job board {}", applicationId, jobBoardId);
        jobBoard.addApplication(application);
        jobBoardRepository.save(jobBoard);

        log.info("Application {} added to job board {} successfully", applicationId, jobBoardId);
    }

    @Transactional
    public void removeApplicationFromJobBoard(Long jobBoardId, Long applicationId) {
        if (jobBoardId == null) {
            throw new IllegalArgumentException("Job board ID cannot be null");
        }
        if (applicationId == null) {
            throw new IllegalArgumentException("Application ID cannot be null");
        }
        
        User currentUser = currentUserProvider.getCurrentUser();

        JobBoard jobBoard = jobBoardRepository.findByIdAndMembersUserId(jobBoardId, currentUser.getId())
            .orElseThrow(() -> new JobBoardNotFoundException("Job board does not exist."));

        // Verify user is an owner before allowing removal
        verifyIsOwner(jobBoard, currentUser.getId());

        Application application = jobBoard.getApplications().stream()
            .filter(app -> app.getId().equals(applicationId))
            .findFirst()
            .orElseThrow(() -> new ApplicationNotFoundException("Application is not on this job board"));
        
        log.info("Removing application {} from job board {}", applicationId, jobBoardId);
        jobBoard.removeApplication(application);
        jobBoardRepository.save(jobBoard);
        
        log.info("Application {} removed from job board {} successfully", applicationId, jobBoardId);
    }

    // HELPER METHODS

    private JobBoard getJobBoardForMember(Long jobBoardId, Long userId) {
        if (jobBoardId == null) {
            throw new IllegalArgumentException("Job board ID cannot be null");
        }
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        JobBoard jobBoard = jobBoardRepository.findById(jobBoardId)
            .orElseThrow(() -> new JobBoardNotFoundException("Job board does not exist."));

        jobBoardMemberRepository.findByJobBoardIdAndUserId(jobBoardId, userId)
            .orElseThrow(() -> new NotAMemberException("User is not a member of this job board."));

        return jobBoard;
    }

    private void verifyIsOwner(JobBoard jobBoard, Long userId) {
        var owner = jobBoard.getOwner();
        if (owner == null || owner.getUser() == null || !owner.getUser().getId().equals(userId)) {
            throw new com.casey.applyflow.exception.InsufficientPermissionException("Only the owner can perform this action.");
        }
    }
}
