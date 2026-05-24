package com.casey.applyflow.service;

import com.casey.applyflow.repository.EmailTokenRepository;
import com.casey.applyflow.repository.JobBoardMemberRepository;
import com.casey.applyflow.repository.JobBoardRepository;
import com.casey.applyflow.repository.UserRepository;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.casey.applyflow.dto.InvitationDetailsDto;
import com.casey.applyflow.exception.JobBoardNotFoundException;
import com.casey.applyflow.exception.MemberAlreadyExistsException;
import com.casey.applyflow.exception.MemberLimitException;
import com.casey.applyflow.exception.UserNotFoundException;
import com.casey.applyflow.model.EmailVerificationToken;
import com.casey.applyflow.model.JobBoard;
import com.casey.applyflow.model.JobBoardMember;
import com.casey.applyflow.model.User;
import com.casey.applyflow.model.builder.JobBoardMemberBuilder;
import com.casey.applyflow.model.enums.Role;

import jakarta.transaction.Transactional;

@Service
public class JobBoardInvitationService {
    private final JobBoardRepository jobBoardRepository;
    private final EmailTokenRepository emailTokenRepository;
    private final Logger log = LoggerFactory.getLogger(JobBoardInvitationService.class);
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final JobBoardMemberRepository jobBoardMemberRepository;
    private final JobBoardAuthorizationService jobBoardAuthorizationService;
    private final CurrentUserProvider currentUserProvider;

    public JobBoardInvitationService(
        CurrentUserProvider currentUserProvider, 
        EmailService emailService,
        EmailTokenRepository emailTokenRepository,
        JobBoardAuthorizationService jobBoardAuthorizationService,
        JobBoardMemberRepository jobBoardMemberRepository, 
        JobBoardRepository jobBoardRepository,
        UserRepository userRepository
    ) {
        this.currentUserProvider = currentUserProvider;
        this.emailService = emailService;
        this.emailTokenRepository = emailTokenRepository;
        this.jobBoardAuthorizationService = jobBoardAuthorizationService;
        this.jobBoardMemberRepository = jobBoardMemberRepository;
        this.jobBoardRepository = jobBoardRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public InvitationDetailsDto getInvitation(Long jobBoardId, String token) {
        if (jobBoardId == null) {
            throw new IllegalArgumentException("Job board ID cannot be null");
        }
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid token");
        }

        User currentUser = currentUserProvider.getCurrentUser();

        EmailVerificationToken verificationToken = emailTokenRepository.findByToken(token)
            .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token expired");
        }

        jobBoardAuthorizationService.verifyIsInvitedUser(currentUser, verificationToken.getUser());

        JobBoard jobBoard = jobBoardRepository.findById(jobBoardId)
            .orElseThrow(() -> new JobBoardNotFoundException("Job board not found!"));

        return new InvitationDetailsDto(
            jobBoard.getId(),
            jobBoard.getTitle(),
            currentUser.getName(),
            verificationToken.getUser().getEmail()
        );
    }

    @Transactional
    public void inviteMember(Long jobBoardId, String userEmail) {
        if (jobBoardId == null) {
            throw new IllegalArgumentException("Job board ID cannot be null");
        }
        
        User currentUser = currentUserProvider.getCurrentUser();
        JobBoard jobBoard = jobBoardAuthorizationService.getJobBoardForOwner(jobBoardId, currentUser.getId());

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

        JobBoardMember member = new JobBoardMemberBuilder()
            .withUser(user)
            .withRole(Role.MEMBER)
            .build();

        jobBoard.addMember(member);
        jobBoardRepository.save(jobBoard);
        emailTokenRepository.delete(verificationToken); // remove temp token
    }

    @Transactional
    public void rejectInvitation(String token) {
        // ensure token is valid
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid token");
        }

        EmailVerificationToken verificationToken = emailTokenRepository.findByToken(token)
            .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token expired");
        }

        // remove token
        emailTokenRepository.delete(verificationToken);
    }
}
