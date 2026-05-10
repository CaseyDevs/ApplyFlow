package com.casey.applyflow.service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.casey.applyflow.exception.InsufficientPermissionException;
import com.casey.applyflow.exception.JobBoardNotFoundException;
import com.casey.applyflow.exception.MemberAlreadyExistsException;
import com.casey.applyflow.exception.MemberLimitException;
import com.casey.applyflow.exception.NotAMemberException;
import com.casey.applyflow.exception.UserNotFoundException;
import com.casey.applyflow.model.EmailVerificationToken;
import com.casey.applyflow.model.JobBoard;
import com.casey.applyflow.model.JobBoardMember;
import com.casey.applyflow.model.User;
import com.casey.applyflow.model.enums.Role;
import com.casey.applyflow.repository.EmailTokenRepository;
import com.casey.applyflow.repository.JobBoardMemberRepository;
import com.casey.applyflow.repository.JobBoardRepository;
import com.casey.applyflow.repository.UserRepository;

@Service
public class JobBoardMemberService {
    private static final Logger log = LoggerFactory.getLogger(JobBoardMemberService.class);
    
    private final JobBoardRepository jobBoardRepository;
    private final JobBoardMemberRepository jobBoardMemberRepository;
    private final UserRepository userRepository;
    private final CurrentUserProvider currentUserProvider;
    private final EmailService emailService;
    private final EmailTokenRepository emailTokenRepository;

    public JobBoardMemberService(
        JobBoardRepository jobBoardRepository,
        JobBoardMemberRepository jobBoardMemberRepository,
        UserRepository userRepository,
        CurrentUserProvider currentUserProvider,
        EmailService emailService,
        EmailTokenRepository emailTokenRepository
    ) {
        this.jobBoardRepository = jobBoardRepository;
        this.jobBoardMemberRepository = jobBoardMemberRepository;
        this.userRepository = userRepository;
        this.currentUserProvider = currentUserProvider;
        this.emailService = emailService;
        this.emailTokenRepository = emailTokenRepository;
    }

    @Transactional
    public void addMember(Long jobBoardId, String userEmail) {
        if (jobBoardId == null) {
            throw new IllegalArgumentException("Job board ID cannot be null");
        }
        
        User currentUser = currentUserProvider.getCurrentUser();
        JobBoard jobBoard = getJobBoardForOwner(jobBoardId, currentUser.getId());

        if (jobBoard.getMembers().size() >= 4) {
            throw new MemberLimitException("Job Board Full! You cannot have more than 4 members.");
        }

        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new UserNotFoundException("User does not exist."));
        
        jobBoardMemberRepository.findByJobBoardIdAndUserId(jobBoardId, user.getId())
            .ifPresent(m -> {
                throw new MemberAlreadyExistsException("This user is already a member.");
            });

        // send invitation email
        emailService.sendInvitationEmail(user, jobBoardId);
        log.info("Sending invitation to {} for job board {}", user.getEmail(), jobBoardId);
    }

    @Transactional
    public void acceptInvitation(Long jobBoardId, String token) {
        if (jobBoardId == null) {
            throw new IllegalArgumentException("Job board ID cannot be null");
        }
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid token");
        }

        EmailVerificationToken verificationToken = emailTokenRepository.findByToken(token)
            .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token expired");
        }

        User user = verificationToken.getUser();

        JobBoard jobBoard = jobBoardRepository.findById(jobBoardId)
            .orElseThrow(() -> new JobBoardNotFoundException("Job board not found!"));

        // ensure member is not already a user
        if (jobBoard.getMembers().stream().anyMatch(member -> member.getUser().getId().equals(user.getId()))) {
            throw new MemberAlreadyExistsException("This user is already a member.");
        }

        // prevent job board member count > 4
        if (jobBoard.getMembers().size() >= 4) {
            throw new MemberLimitException("Job Board Full! You cannot have more than 4 members.");
        }

        jobBoard.addMember(toJobBoardMember(user));
        jobBoardRepository.save(jobBoard);
        emailTokenRepository.delete(verificationToken); // remove temp token
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

    // HELPER METHODS

    public JobBoardMember toJobBoardMember(User member) {
        return new JobBoardMember(member, Role.MEMBER);
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
}
