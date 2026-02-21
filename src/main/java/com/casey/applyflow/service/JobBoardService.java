package com.casey.applyflow.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.casey.applyflow.domain.Application;
import com.casey.applyflow.domain.JobBoard;
import com.casey.applyflow.domain.JobBoardMember;
import com.casey.applyflow.domain.User;
import com.casey.applyflow.domain.enums.Role;
import com.casey.applyflow.dto.ApplicationResponseDto;
import com.casey.applyflow.dto.JobBoardRequestDto;
import com.casey.applyflow.dto.JobBoardResponseDto;
import com.casey.applyflow.exception.ApplicationNotFoundException;
import com.casey.applyflow.exception.JobBoardNotFoundException;
import com.casey.applyflow.exception.UserNotFoundException;
import com.casey.applyflow.exception.MemberAlreadyExistsException;
import com.casey.applyflow.exception.NotAMemberException;
import com.casey.applyflow.exception.InsufficientPermissionException;
import com.casey.applyflow.repository.ApplicationRepository;
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
    private CurrentUserProvider currentUserProvider;
    private ApplicationService applicationService;

    public JobBoardService(
        JobBoardRepository jobBoardRepository, 
        JobBoardMemberRepository jobBoardMemberRepository,
        UserRepository userRepository,
        ApplicationRepository applicationRepository,
        CurrentUserProvider currentUserProvider,
        ApplicationService applicationService
    ) {
        this.jobBoardRepository = jobBoardRepository;
        this.jobBoardMemberRepository = jobBoardMemberRepository;
        this.userRepository = userRepository;
        this.applicationRepository = applicationRepository;
        this.currentUserProvider = currentUserProvider;
        this.applicationService = applicationService;
    }

    @Transactional(readOnly = true)
    public List<JobBoardResponseDto> getAllJobBoards() { // users can only have 1 job board for now
        User currentUser = currentUserProvider.getCurrentUser();

        log.info("Getting job boards for user {}", currentUser.getId());

        // Check user is a member of board
        List<JobBoard> jobBoards = jobBoardRepository.findAllByUserId(currentUser.getId())
            .orElseThrow(() -> new NotAMemberException("User is not a member of any job boards"));

        return jobBoards.stream()
            .map(jb -> new JobBoardResponseDto (
                jb.getId(),
                jb.getTitle(),
                jb.getOwner().getId(),
                jb.getMembers()
            )).toList();
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponseDto> getAllJobBoardApplications(Long jobBoardId) {
        if (jobBoardId == null) {
            throw new IllegalArgumentException("Job board ID cannot be null");
        }

        User currentUser = currentUserProvider.getCurrentUser();

        log.info("Fetching applications for job board {} for user {}", jobBoardId, currentUser.getId());

        JobBoard jobBoard = jobBoardRepository.findByIdAndUserId(jobBoardId, currentUser.getId())
            .orElseThrow(() -> new NotAMemberException("User is not a member of this job board"));
        
        return jobBoard.getApplications().stream()
            .map(applicationService::toApplicationResponseDto)
            .toList();
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
    public void addMember(Long jobBoardId, Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        
        JobBoard jobBoard = jobBoardRepository.findById(jobBoardId)
            .orElseThrow(() -> new JobBoardNotFoundException("Job board does not exist."));
        
        // Verify current user has permission (must be owner)
        User currentUser = currentUserProvider.getCurrentUser();
        verifyIsOwner(jobBoard, currentUser.getId());

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User does not exist."));
        
        jobBoardMemberRepository.findByJobBoardIdAndUserId(jobBoardId, userId)
            .ifPresent(m -> {
                throw new MemberAlreadyExistsException("This user is already a member.");
            });
        
        log.info("Adding user {} to job board {}", userId, jobBoardId);
        JobBoardMember member = toJobBoardMember(user);
        
        jobBoard.addMember(member);
        jobBoardMemberRepository.save(member);
        jobBoardRepository.save(jobBoard);
        
        log.info("User {} added to job board {} successfully", userId, jobBoardId);
    }

    @Transactional
    public void removeMember(Long jobBoardId, Long jobBoardMemberId) {
        if (jobBoardMemberId == null) {
            throw new IllegalArgumentException("Member ID cannot be null");
        }
        
        JobBoard jobBoard = jobBoardRepository.findById(jobBoardId)
            .orElseThrow(() -> new JobBoardNotFoundException("Job board does not exist."));
        
        // Verify current user has permission (must be owner)
        User currentUser = currentUserProvider.getCurrentUser();
        verifyIsOwner(jobBoard, currentUser.getId());

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
        if (jobBoardMemberId == null) {
            throw new IllegalArgumentException("Member ID cannot be null");
        }
        
        JobBoard jobBoard = jobBoardRepository.findById(jobBoardId)
            .orElseThrow(() -> new JobBoardNotFoundException("Job board does not exist."));
        
        // Verify current user is the current owner
        User currentUser = currentUserProvider.getCurrentUser();
        verifyIsOwner(jobBoard, currentUser.getId());

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
    public void addApplicationToJobBoard(Long jobBoardId, Long applicationId) {
        if (applicationId == null) {
            throw new IllegalArgumentException("Application ID cannot be null");
        }
        
        User currentUser = currentUserProvider.getCurrentUser();

        JobBoard jobBoard = jobBoardRepository.findById(jobBoardId)
            .orElseThrow(() -> new JobBoardNotFoundException("Job board does not exist."));

        // Check if user is a member of the job board
        jobBoardMemberRepository.findByJobBoardIdAndUserId(jobBoardId, currentUser.getId())
            .orElseThrow(() -> new NotAMemberException("User is not a member of this job board."));

        Application application = applicationRepository.findByIdAndUserId(applicationId, currentUser.getId())
            .orElseThrow(() -> new ApplicationNotFoundException("Application does not exist"));
        
        log.info("Adding application {} to job board {}", applicationId, jobBoardId);
        jobBoard.addApplication(application);
        jobBoardRepository.save(jobBoard);
        
        log.info("Application {} added to job board {} successfully", applicationId, jobBoardId);
    }

    @Transactional
    public void removeApplicationFromJobBoard(Long jobBoardId, Long applicationId) {
        if (applicationId == null) {
            throw new IllegalArgumentException("Application ID cannot be null");
        }
        
        User currentUser = currentUserProvider.getCurrentUser();
        
        JobBoard jobBoard = jobBoardRepository.findById(jobBoardId)
            .orElseThrow(() -> new JobBoardNotFoundException("Job board does not exist."));

        // Check if user is a member of the job board
        jobBoardMemberRepository.findByJobBoardIdAndUserId(jobBoardId, currentUser.getId())
            .orElseThrow(() -> new NotAMemberException("User is not a member of this job board."));

        Application application = applicationRepository.findByIdAndUserId(applicationId, currentUser.getId())
            .orElseThrow(() -> new ApplicationNotFoundException("Application does not exist"));
        
        log.info("Removing application {} from job board {}", applicationId, jobBoardId);
        jobBoard.removeApplication(application);
        jobBoardRepository.save(jobBoard);
        
        log.info("Application {} removed from job board {} successfully", applicationId, jobBoardId);
    }

    @Transactional
    public void deleteJobBoard(Long jobBoardId) {
        if (jobBoardId == null) {
            throw new IllegalArgumentException("Job board ID cannot be null");
        }
        
        JobBoard jobBoard = jobBoardRepository.findById(jobBoardId)
            .orElseThrow(() -> new JobBoardNotFoundException("Job board does not exist."));

        User currentUser = currentUserProvider.getCurrentUser();
        verifyIsOwner(jobBoard, currentUser.getId());
        
        log.info("Deleting job board {} owned by user {}", jobBoardId, currentUser.getId());
        
        // Detach all applications from the job board before deletion
        jobBoard.getApplications().forEach(app -> app.setJobBoard(null));
        
        // Members will be cascade deleted
        jobBoardRepository.delete(jobBoard);
        
        log.info("Job board {} deleted successfully", jobBoardId);
    }

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
    
    private void verifyIsOwner(JobBoard jobBoard, Long userId) {
        JobBoardMember owner = jobBoard.getOwner();
        jobBoardMemberRepository.findByIdAndJobBoardId(owner.getId(), jobBoard.getId())
            .filter(member -> member.getRole() == Role.OWNER)
            .flatMap(member -> userRepository.findById(userId)
                .filter(user -> user.getId().equals(userId)))
            .orElseThrow(() -> new InsufficientPermissionException("Only the owner can perform this action."));
    }
    
    private void validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Job board title cannot be empty");
        }
    }
}
