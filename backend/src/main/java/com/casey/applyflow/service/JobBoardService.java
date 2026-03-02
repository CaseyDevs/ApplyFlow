package com.casey.applyflow.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.casey.applyflow.domain.Application;
import com.casey.applyflow.domain.Company;
import com.casey.applyflow.domain.JobBoard;
import com.casey.applyflow.domain.JobBoardMember;
import com.casey.applyflow.domain.User;
import com.casey.applyflow.domain.enums.Role;
import com.casey.applyflow.dto.ApplicationRequestDto;
import com.casey.applyflow.dto.ApplicationResponseDto;
import com.casey.applyflow.dto.JobBoardRequestDto;
import com.casey.applyflow.dto.JobBoardResponseDto;
import com.casey.applyflow.dto.JobBoardStatsDto;
import com.casey.applyflow.exception.ApplicationNotFoundException;
import com.casey.applyflow.exception.CompanyNotFoundException;
import com.casey.applyflow.exception.JobBoardNotFoundException;
import com.casey.applyflow.exception.UserNotFoundException;
import com.casey.applyflow.exception.MemberAlreadyExistsException;
import com.casey.applyflow.exception.NotAMemberException;
import com.casey.applyflow.exception.InsufficientPermissionException;
import com.casey.applyflow.repository.ApplicationRepository;
import com.casey.applyflow.repository.CompanyRepository;
import com.casey.applyflow.repository.JobBoardMemberRepository;
import com.casey.applyflow.repository.JobBoardRepository;
import com.casey.applyflow.repository.UserRepository;

@Service
public class JobBoardService {
    private static final Logger log = LoggerFactory.getLogger(JobBoardService.class);
    
    private JobBoardRepository jobBoardRepository;
    private JobBoardMemberRepository jobBoardMemberRepository;
    private UserRepository userRepository;
    private ApplicationRepository applicationRepository;
    private CompanyRepository companyRepository;
    private CurrentUserProvider currentUserProvider;
    private ApplicationService applicationService;

    public JobBoardService(
        JobBoardRepository jobBoardRepository, 
        JobBoardMemberRepository jobBoardMemberRepository,
        UserRepository userRepository,
        ApplicationRepository applicationRepository,
        CompanyRepository companyRepository,
        CurrentUserProvider currentUserProvider,
        ApplicationService applicationService
    ) {
        this.jobBoardRepository = jobBoardRepository;
        this.jobBoardMemberRepository = jobBoardMemberRepository;
        this.userRepository = userRepository;
        this.applicationRepository = applicationRepository;
        this.companyRepository = companyRepository;
        this.currentUserProvider = currentUserProvider;
        this.applicationService = applicationService;
    }

    @Transactional(readOnly = true)
    public Page<JobBoardResponseDto> getAllJobBoards(Pageable pageable) {
        User currentUser = currentUserProvider.getCurrentUser();

        log.info("Getting job boards for user {}", currentUser.getId());

        return jobBoardRepository.findAllByUserId(currentUser.getId(), pageable)
            .map(this::toJobBoardResponseDto);
    }

    @Transactional(readOnly = true)
    public Page<ApplicationResponseDto> getAllJobBoardApplications(Long jobBoardId, Pageable pageable) {
        if (jobBoardId == null) {
            throw new IllegalArgumentException("Job board ID cannot be null");
        }

        User currentUser = currentUserProvider.getCurrentUser();

        log.info("Fetching applications for job board {} for user {}", jobBoardId, currentUser.getId());

        getJobBoardForMember(jobBoardId, currentUser.getId());
        
        return applicationRepository.findAllByJobBoardId(jobBoardId, pageable)
            .map(applicationService::toApplicationResponseDto);
    }

    @Transactional
    public JobBoardResponseDto createJobBoard(JobBoardRequestDto request) {
        validateTitle(request.title());
        
        User currentUser = currentUserProvider.getCurrentUser();
        log.info("Creating job board '{}' for user {}", request.title(), currentUser.getId());
        
        JobBoardMember owner = toJobBoardMember(currentUser);
        owner.setRole(Role.OWNER);

        JobBoard jobBoard = new JobBoard(
            request.title(), 
            owner,
            null
        );
        
        jobBoard.addMember(owner);
        jobBoardRepository.save(jobBoard);
        
        log.info("Job board '{}' created successfully with ID {}", jobBoard.getTitle(), jobBoard.getId());
        return toJobBoardResponseDto(jobBoard);
    }

    @Transactional
    public JobBoardResponseDto updateJobBoard(Long jobBoardId, JobBoardRequestDto request) {
        if (jobBoardId == null) {
            throw new IllegalArgumentException("Job board ID cannot be null");
        }

        User currentUser = currentUserProvider.getCurrentUser();
        JobBoard jobBoard = getJobBoardForOwner(jobBoardId, currentUser.getId());

        if (request.title() != null) {
            validateTitle(request.title());

            log.info("Updating job board {} title", jobBoard.getId());
            jobBoard.setTitle(request.title());
            jobBoardRepository.save(jobBoard);
        }

        return toJobBoardResponseDto(jobBoard);
    }
    
    @Transactional
    public void addMember(Long jobBoardId, String userEmail) {
        if (jobBoardId == null) {
            throw new IllegalArgumentException("Job board ID cannot be null");
        }
        if (userEmail.isEmpty()) { // TODO: verify email is valid
            throw new IllegalArgumentException("User email can not be empty");
        }
        
        User currentUser = currentUserProvider.getCurrentUser();
        JobBoard jobBoard = getJobBoardForOwner(jobBoardId, currentUser.getId());

        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new UserNotFoundException("User does not exist."));
        
        jobBoardMemberRepository.findByJobBoardIdAndUserId(jobBoardId, user.getId())
            .ifPresent(m -> {
                throw new MemberAlreadyExistsException("This user is already a member.");
            });
        
        log.info("Adding user {} to job board {}", user.getId(), jobBoardId);
        JobBoardMember member = toJobBoardMember(user);
        
        jobBoard.addMember(member);
        jobBoardMemberRepository.save(member);
        jobBoardRepository.save(jobBoard);
        
        log.info("User {} added to job board {} successfully", user.getId(), jobBoardId);
    }

    @Transactional
    public void removeMember(Long jobBoardId, Long jobBoardMemberId) {
        if (jobBoardId == null) {
            throw new IllegalArgumentException("Job board ID cannot be null");
        }
        if (jobBoardMemberId == null) {
            throw new IllegalArgumentException("Member ID cannot be null");
        }
        
        User currentUser = currentUserProvider.getCurrentUser();
        JobBoard jobBoard = getJobBoardForOwner(jobBoardId, currentUser.getId());

        JobBoardMember member = jobBoardMemberRepository.findByIdAndJobBoardId(jobBoardMemberId, jobBoardId)
            .orElseThrow(() -> new NotAMemberException("User is not a member of this job board."));

        // prevent removal of owners
        if (member.getRole() == Role.OWNER) {
            throw new InsufficientPermissionException("Cannot remove the owner. Transfer ownership first.");
        }

        log.info("Removing member {} from job board {}", jobBoardMemberId, jobBoardId);
        jobBoard.removeMember(member);
        jobBoardMemberRepository.delete(member);
        jobBoardRepository.save(jobBoard);
        
        log.info("Member {} removed from job board {} successfully", jobBoardMemberId, jobBoardId);
    }

    @Transactional
    public void setNewOwner(Long jobBoardId, Long jobBoardMemberId) {
        if (jobBoardId == null) {
            throw new IllegalArgumentException("Job board ID cannot be null");
        }
        if (jobBoardMemberId == null) {
            throw new IllegalArgumentException("Member ID cannot be null");
        }
        
        User currentUser = currentUserProvider.getCurrentUser();
        JobBoard jobBoard = getJobBoardForOwner(jobBoardId, currentUser.getId());

        JobBoardMember newOwner = jobBoardMemberRepository.findByIdAndJobBoardId(jobBoardMemberId, jobBoardId)
            .orElseThrow(() -> new NotAMemberException("User is not a member of this job board"));
        
        JobBoardMember oldOwner = jobBoard.getOwner();
        log.info("Transferring ownership of job board {} from member {} to member {}", 
                 jobBoardId, oldOwner.getId(), jobBoardMemberId);
        
        jobBoard.setOwner(newOwner);
        jobBoardRepository.save(jobBoard);
        
        log.info("Ownership of job board {} transferred successfully", jobBoardId);
    }

    @Transactional
    public void addApplicationToJobBoard(Long jobBoardId, ApplicationRequestDto request) {
        if (jobBoardId == null) {
            throw new IllegalArgumentException("Job board ID cannot be null");
        }

        Company company = companyRepository.findById(request.companyId())
            .orElseThrow(() -> new CompanyNotFoundException("Company not found"));

        // Create & save application
        Application application = new Application(request.title(), request.url(), company, request.status());
        Application savedApplication = applicationRepository.save(application);
        
        User currentUser = currentUserProvider.getCurrentUser();
        JobBoard jobBoard = getJobBoardForMember(jobBoardId, currentUser.getId());
        
        log.info("Adding application {} to job board {}", savedApplication.getId(), jobBoardId);
        jobBoard.addApplication(savedApplication);
        jobBoardRepository.save(jobBoard);
        
        log.info("Application {} added to job board {} successfully", savedApplication.getId(), jobBoardId);
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

        JobBoard jobBoard = getJobBoardForMember(jobBoardId, currentUser.getId());

        Application application = applicationRepository.findByIdAndUserId(applicationId, currentUser.getId())
            .orElseThrow(() -> new ApplicationNotFoundException("Application does not exist"));
        
        log.info("Removing application {} from job board {}", applicationId, jobBoardId);
        jobBoard.removeApplication(application);
        jobBoardRepository.save(jobBoard);
        
        log.info("Application {} removed from job board {} successfully", applicationId, jobBoardId);
    }

    @Transactional
    public void leaveJobBoard(Long jobBoardId) {
        if (jobBoardId == null) {
            throw new IllegalArgumentException("Job board ID cannot be null");
        }

        User currentUser = currentUserProvider.getCurrentUser();

        JobBoard jobBoard = getJobBoardForMember(jobBoardId, currentUser.getId());

        JobBoardMember member = jobBoardMemberRepository.findByJobBoardIdAndUserId(jobBoardId, currentUser.getId())
            .orElseThrow(() -> new NotAMemberException("You are not a member of this job board"));

        if (member.getRole() == Role.OWNER) {
            throw new InsufficientPermissionException("Owner cannot leave the job board. Transfer ownership first.");
        }

        log.info("Removing member {} from job board {}", member.getId(), jobBoard.getId());

        jobBoard.removeMember(member);
        jobBoardMemberRepository.delete(member);
        jobBoardRepository.save(jobBoard);
    }

    @Transactional
    public void deleteJobBoard(Long jobBoardId) {
        if (jobBoardId == null) {
            throw new IllegalArgumentException("Job board ID cannot be null");
        }

        User currentUser = currentUserProvider.getCurrentUser();
        JobBoard jobBoard = getJobBoardForOwner(jobBoardId, currentUser.getId());
        
        log.info("Deleting job board {} owned by user {}", jobBoardId, currentUser.getId());
        
        // Detach all applications from the job board before deletion
        jobBoard.getApplications().forEach(app -> app.setJobBoard(null));
        
        // Members will be cascade deleted
        jobBoardRepository.delete(jobBoard);
        
        log.info("Job board {} deleted successfully", jobBoardId);
    }

    @Transactional(readOnly = true)
    public JobBoardStatsDto getJobBoardStats(Long jobBoardId) {
        if (jobBoardId == null) {
            throw new IllegalArgumentException("Job board ID cannot be null");
        }

        User currentUser = currentUserProvider.getCurrentUser();
        JobBoard jobBoard = getJobBoardForMember(jobBoardId, currentUser.getId());
        
        return new JobBoardStatsDto(
            jobBoard.getOwner(),
            jobBoard.getApplications().size(),
            jobBoard.getMembers()
        );
    }

    // HELPER METHODS

    private JobBoardMember toJobBoardMember(User member) {
        return new JobBoardMember(member, Role.MEMBER);
    }

    private JobBoardResponseDto toJobBoardResponseDto(JobBoard jobBoard) {
        return new JobBoardResponseDto(
            jobBoard.getId(),
            jobBoard.getTitle(),
            jobBoard.getOwner().getId(),
            jobBoard.getMembers()
        );
    }
    
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

    private JobBoard getJobBoardForOwner(Long jobBoardId, Long userId) {
        JobBoard jobBoard = getJobBoardForMember(jobBoardId, userId);
        verifyIsOwner(jobBoard, userId);
        return jobBoard;
    }

    private void verifyIsOwner(JobBoard jobBoard, Long userId) {
        JobBoardMember owner = jobBoard.getOwner();
        if (owner == null || owner.getUser() == null || !owner.getUser().getId().equals(userId)) {
            throw new InsufficientPermissionException("Only the owner can perform this action.");
        }
    }
    
    private void validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Job board title cannot be empty");
        }
    }
        
}
