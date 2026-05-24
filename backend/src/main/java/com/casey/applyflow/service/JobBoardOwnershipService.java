package com.casey.applyflow.service;

import com.casey.applyflow.repository.JobBoardMemberRepository;
import com.casey.applyflow.repository.JobBoardRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.casey.applyflow.exception.NotAMemberException;
import com.casey.applyflow.model.JobBoard;
import com.casey.applyflow.model.JobBoardMember;
import com.casey.applyflow.model.User;

@Service
public class JobBoardOwnershipService {
    private final Logger log = LoggerFactory.getLogger(JobBoardOwnershipService.class);
    private final CurrentUserProvider currentUserProvider;
    private final JobBoardAuthorizationService jobBoardAuthorizationService;
    private final JobBoardMemberRepository jobBoardMemberRepository;
    private final JobBoardRepository jobBoardRepository;

    public JobBoardOwnershipService(
        CurrentUserProvider currentUserProvider, 
        JobBoardAuthorizationService jobBoardAuthorizationService, 
        JobBoardMemberRepository jobBoardMemberRepository, 
        JobBoardRepository jobBoardRepository
    ) {
        this.currentUserProvider = currentUserProvider;
        this.jobBoardAuthorizationService = jobBoardAuthorizationService;
        this.jobBoardMemberRepository = jobBoardMemberRepository;
        this.jobBoardRepository = jobBoardRepository;
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
        JobBoard jobBoard = jobBoardAuthorizationService.getJobBoardForOwner(jobBoardId, currentUser.getId());

        JobBoardMember newOwner = jobBoardMemberRepository.findByIdAndJobBoardId(jobBoardMemberId, jobBoardId)
            .orElseThrow(() -> new NotAMemberException("User is not a member of this job board"));
        
        JobBoardMember oldOwner = jobBoard.getOwner();
        log.info("Transferring ownership of job board {} from member {} to member {}", 
                 jobBoardId, oldOwner.getId(), jobBoardMemberId);
        
        jobBoard.setOwner(newOwner);
        jobBoardRepository.save(jobBoard);
        
        log.info("Ownership of job board {} transferred successfully", jobBoardId);
    }
}
