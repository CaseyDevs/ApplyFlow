package com.casey.applyflow.service;

import org.springframework.stereotype.Service;

import com.casey.applyflow.exception.InsufficientPermissionException;
import com.casey.applyflow.exception.JobBoardNotFoundException;
import com.casey.applyflow.exception.NotAMemberException;
import com.casey.applyflow.model.JobBoard;
import com.casey.applyflow.model.JobBoardMember;
import com.casey.applyflow.model.User;
import com.casey.applyflow.repository.JobBoardMemberRepository;
import com.casey.applyflow.repository.JobBoardRepository;

@Service
public class JobBoardAuthorizationService {
    private final JobBoardRepository jobBoardRepository;
    private final JobBoardMemberRepository jobBoardMemberRepository;

    protected JobBoardAuthorizationService(
        JobBoardRepository jobBoardRepository,
        JobBoardMemberRepository jobBoardMemberRepository
    ) {
        this.jobBoardRepository = jobBoardRepository;
        this.jobBoardMemberRepository= jobBoardMemberRepository;
    }

    protected void verifyIsOwner(JobBoard jobBoard, Long userId) {
        JobBoardMember owner = jobBoard.getOwner();
        if (owner == null || owner.getUser() == null || !owner.getUser().getId().equals(userId)) {
            throw new InsufficientPermissionException("Only the owner can perform this action.");
        }
    }

    public JobBoard getJobBoardForOwner(Long jobBoardId, Long userId) {
        JobBoard jobBoard = getJobBoardForMember(jobBoardId, userId);
        verifyIsOwner(jobBoard, userId);
        return jobBoard;
    }

    protected JobBoard getJobBoardForMember(Long jobBoardId, Long userId) {
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

    protected void verifyIsInvitedUser(User currentUser, User invitedUser) {
        if (!currentUser.getId().equals(invitedUser.getId())) {
            throw new InsufficientPermissionException("This invitation is not for your account.");
        }
    }
}
