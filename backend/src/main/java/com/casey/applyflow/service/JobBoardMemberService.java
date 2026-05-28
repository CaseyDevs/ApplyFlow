package com.casey.applyflow.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.casey.applyflow.exception.InsufficientPermissionException;
import com.casey.applyflow.exception.NotAMemberException;
import com.casey.applyflow.model.JobBoard;
import com.casey.applyflow.model.JobBoardMember;
import com.casey.applyflow.model.User;
import com.casey.applyflow.model.enums.Role;
import com.casey.applyflow.repository.JobBoardApplicationStatusRepository;
import com.casey.applyflow.repository.EmailTokenRepository;
import com.casey.applyflow.repository.JobBoardMemberRepository;
import com.casey.applyflow.repository.JobBoardRepository;
import com.casey.applyflow.repository.UserRepository;

@Service
public class JobBoardMemberService {
    private static final Logger log = LoggerFactory.getLogger(JobBoardMemberService.class);
    private final CurrentUserProvider currentUserProvider;
    private final JobBoardAuthorizationService jobBoardAuthorizationService;
    private final JobBoardApplicationStatusRepository jobBoardApplicationStatusRepository;
    private final JobBoardRepository jobBoardRepository;
    private final JobBoardMemberRepository jobBoardMemberRepository;

    public JobBoardMemberService(
        CurrentUserProvider currentUserProvider,
        EmailService emailService,
        EmailTokenRepository emailTokenRepository,
        UserRepository userRepository,
        JobBoardApplicationStatusRepository jobBoardApplicationStatusRepository,
        JobBoardAuthorizationService jobBoardAuthorizationService,
        JobBoardMemberRepository jobBoardMemberRepository,
        JobBoardRepository jobBoardRepository
    ) {
        this.currentUserProvider = currentUserProvider;
        this.jobBoardApplicationStatusRepository = jobBoardApplicationStatusRepository;
        this.jobBoardAuthorizationService = jobBoardAuthorizationService;
        this.jobBoardMemberRepository = jobBoardMemberRepository;
        this.jobBoardRepository = jobBoardRepository;
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
        JobBoard jobBoard = jobBoardAuthorizationService.getJobBoardForOwner(jobBoardId, currentUser.getId());

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
    public void leaveJobBoard(Long jobBoardId) {
        if (jobBoardId == null) {
            throw new IllegalArgumentException("Job board ID cannot be null");
        }

        User currentUser = currentUserProvider.getCurrentUser();

        JobBoard jobBoard = jobBoardAuthorizationService.getJobBoardForMember(jobBoardId, currentUser.getId());

        JobBoardMember member = jobBoardMemberRepository.findByJobBoardIdAndUserId(jobBoardId, currentUser.getId())
            .orElseThrow(() -> new NotAMemberException("You are not a member of this job board"));

        if (member.getRole() == Role.OWNER) {
            throw new InsufficientPermissionException("Owner cannot leave the job board. Transfer ownership first.");
        }

        log.info("Removing member {} from job board {}", member.getId(), jobBoard.getId());

        handleDeleteAllUserStatusEntities(jobBoard, member);
        jobBoard.removeMember(member);
        jobBoardMemberRepository.delete(member);
        jobBoardRepository.save(jobBoard);
    }

    @Transactional
    public void handleDeleteAllUserStatusEntities(JobBoard jobBoard, JobBoardMember jobBoardMember) {
        int deletedCount = jobBoardApplicationStatusRepository.deleteAllByJobBoardIdAndUserId(
            jobBoard.getId(),
            jobBoardMember.getUser().getId()
        );
        log.debug("Deleted {} application statuses for user {} on job board {}", 
                  deletedCount, jobBoardMember.getUser().getId(), jobBoard.getId());
    }
}
