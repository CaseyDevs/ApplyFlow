package com.casey.applyflow.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.casey.applyflow.dto.JobBoardApplicationRequestDto;
import com.casey.applyflow.dto.JobBoardApplicationResponseDto;
import com.casey.applyflow.dto.JobBoardResponseDto;
import com.casey.applyflow.exception.ApplicationNotFoundException;
import com.casey.applyflow.exception.JobBoardNotFoundException;
import com.casey.applyflow.exception.NotAMemberException;
import com.casey.applyflow.model.Application;
import com.casey.applyflow.model.JobBoard;
import com.casey.applyflow.model.JobBoardApplication;
import com.casey.applyflow.model.JobBoardMember;
import com.casey.applyflow.model.User;
import com.casey.applyflow.repository.ApplicationRepository;
import com.casey.applyflow.repository.JobBoardApplicationRepository;
import com.casey.applyflow.repository.JobBoardMemberRepository;
import com.casey.applyflow.repository.JobBoardRepository;

@Service
public class JobBoardApplicationService {
    private static final Logger log = LoggerFactory.getLogger(JobBoardApplicationService.class);
    
    private final JobBoardRepository jobBoardRepository;
    private final JobBoardMemberRepository jobBoardMemberRepository;
    private final ApplicationRepository applicationRepository;
    private final JobBoardApplicationRepository jobBoardApplicationRepository;
    private final CurrentUserProvider currentUserProvider;
    private final ApplicationService applicationService;
    private final JobBoardApplicationStatusService jobBoardApplicationStatusService;

    public JobBoardApplicationService(
        JobBoardRepository jobBoardRepository,
        JobBoardMemberRepository jobBoardMemberRepository,
        ApplicationRepository applicationRepository,
        JobBoardApplicationRepository jobBoardApplicationRepository,
        CurrentUserProvider currentUserProvider,
        ApplicationService applicationService,
        JobBoardApplicationStatusService jobBoardApplicationStatusService
    ) {
        this.jobBoardRepository = jobBoardRepository;
        this.jobBoardMemberRepository = jobBoardMemberRepository;
        this.applicationRepository = applicationRepository;
        this.jobBoardApplicationRepository = jobBoardApplicationRepository;
        this.currentUserProvider = currentUserProvider;
        this.applicationService = applicationService;
        this.jobBoardApplicationStatusService = jobBoardApplicationStatusService;
    }

    @Transactional(readOnly = true)
    public Page<JobBoardApplicationResponseDto> getAllJobBoardApplications(Long jobBoardId, Pageable pageable) {
        if (jobBoardId == null) {
            throw new IllegalArgumentException("Job board ID cannot be null");
        }

        User currentUser = currentUserProvider.getCurrentUser();

        log.info("Fetching applications for job board {} for user {}", jobBoardId, currentUser.getId());

        JobBoard jobBoard = getJobBoardForMember(jobBoardId, currentUser.getId());

        return new PageImpl<>(
            jobBoard.getApplications().stream()
                .map(this::toJobBoardApplicationResponseDto)
                .toList(),
            pageable,
            jobBoard.getApplications().size()
        );
    }

    @Transactional(readOnly = true)
    public JobBoardApplicationResponseDto getJobBoardApplicationById(Long id, Long jobBoardId) {
        if (id == null || jobBoardId == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        User currentUser = currentUserProvider.getCurrentUser();

        getJobBoardForMember(jobBoardId, currentUser.getId());

        log.info("Fetching job board application with id {} for user {}", id, currentUser.getId());
        
        JobBoardApplication jobBoardApplication = jobBoardApplicationRepository
            .findByIdAndJobBoardId(id, jobBoardId)
            .orElseThrow(() -> new ApplicationNotFoundException("Job board application does not exist with id: " + id));

        return toJobBoardApplicationResponseDto(jobBoardApplication);
    }

    @Transactional
        public JobBoardApplicationResponseDto addApplicationToJobBoard(JobBoardApplicationRequestDto request) {
        if (request.jobBoardId() == null || request.applicationId() == null) {
            throw new IllegalArgumentException("Job board and application IDs cannot be null");
        }

        Long jobBoardId = request.jobBoardId(); 
        Long applicationId = request.applicationId();

        User currentUser = currentUserProvider.getCurrentUser();
        
        // Get the application (user must own it)
        Application application = applicationRepository.findByIdAndUserId(applicationId, currentUser.getId())
            .orElseThrow(() -> new ApplicationNotFoundException("Application not found"));
        
        // Get the job board and verify user is a member
        JobBoard jobBoard = getJobBoardForMember(jobBoardId, currentUser.getId());
        
        // Get the current user's JobBoardMember
        JobBoardMember member = jobBoardMemberRepository.findByJobBoardIdAndUserId(jobBoardId, currentUser.getId())
            .orElseThrow(() -> new NotAMemberException("User is not a member of this job board"));
        
        // Create the JobBoardApplication
        JobBoardApplication jobBoardApp = new JobBoardApplication(member, application, jobBoard);
        jobBoardApplicationRepository.save(jobBoardApp);
        
        // Automatically set the owner's status to match the application's status
        jobBoardApplicationStatusService.updateApplicationStatus(jobBoardApp.getId(), application.getStatus().toString());
        
        log.info("Application {} added to job board {} by user {}", applicationId, jobBoardId, currentUser.getId());
        
        return toJobBoardApplicationResponseDto(jobBoardApp);
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

        JobBoardApplication application = jobBoard.getApplications().stream()
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

    public JobBoardApplicationResponseDto toJobBoardApplicationResponseDto(JobBoardApplication jba) {
    return new JobBoardApplicationResponseDto(
        jba.getId(),
        applicationService.toApplicationResponseDto(jba.getApplication()),
        jba.getAddedAt(),
        jba.getJobBoardMember().getUser().getEmail(),
        jba.getStatusList().stream()
            .map(jobBoardApplicationStatusService::toJobBoardStatusResponseDto)
            .toList()
    );    
    }
}
