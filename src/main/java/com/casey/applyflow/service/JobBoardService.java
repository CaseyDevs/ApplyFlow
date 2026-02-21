package com.casey.applyflow.service;

import org.springframework.stereotype.Service;

import com.casey.applyflow.domain.JobBoard;
import com.casey.applyflow.domain.JobBoardMember;
import com.casey.applyflow.domain.User;
import com.casey.applyflow.domain.enums.Role;
import com.casey.applyflow.dto.JobBoardRequestDto;
import com.casey.applyflow.dto.JobBoardResponseDto;
import com.casey.applyflow.exception.JobBoardNotFoundException;
import com.casey.applyflow.exception.UserNotFoundException;
import com.casey.applyflow.exception.MemberAlreadyExistsException;
import com.casey.applyflow.exception.NotAMemberException;
import com.casey.applyflow.repository.JobBoardMemberRepository;
import com.casey.applyflow.repository.JobBoardRepository;
import com.casey.applyflow.repository.UserRepository;

import jakarta.transaction.Transactional;

// TODO: ADD LOGGING, SET NEW OWNER, GET APPLICATIONS ETC...

@Service
public class JobBoardService {
    private JobBoardRepository jobBoardRepository;
    private JobBoardMemberRepository jobBoardMemberRepository;
    private UserRepository userRepository;
    private CurrentUserProvider currentUserProvider;

    public JobBoardService(
        JobBoardRepository jobBoardRepository, 
        JobBoardMemberRepository jobBoardMemberRepository,
        UserRepository userRepository,
        CurrentUserProvider currentUserProvider
    ) {
        this.jobBoardRepository = jobBoardRepository;
        this.jobBoardMemberRepository = jobBoardMemberRepository;
        this.userRepository = userRepository;
        this.currentUserProvider = currentUserProvider;
    }

    @Transactional
    public JobBoardResponseDto createJobBoard(JobBoardRequestDto request) {
        User user = currentUserProvider.getCurrentUser();
        JobBoardMember owner = toJobBoardMember(user);
        owner.setRole(Role.OWNER);

        JobBoard jobBoard = new JobBoard(
            request.title(), 
            owner,
            request.members()    
        );

        jobBoardRepository.save(jobBoard);

        return toJobBoardResponseDto(jobBoard, owner);
    }
    
    @Transactional
    public void addMember(Long jobBoardId, Long userId) {
        JobBoard jobBoard = jobBoardRepository.findById(jobBoardId)
            .orElseThrow(() -> new JobBoardNotFoundException("Job board does not exist."));

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User does not exist."));
        
        jobBoardMemberRepository.findByJobBoardIdAndUserId(jobBoardId, userId)
            .ifPresent(m -> {
                throw new MemberAlreadyExistsException("This user is already a member.");
            });
        
        JobBoardMember member = toJobBoardMember(user);
        
        jobBoard.addMember(member);
        jobBoardMemberRepository.save(member);
        jobBoardRepository.save(jobBoard);
    }

    @Transactional
    public void removeMember(Long jobBoardId, Long jobBoardMemberId) {
        JobBoard jobBoard = jobBoardRepository.findById(jobBoardId)
            .orElseThrow(() -> new JobBoardNotFoundException("Job board does not exist."));

        JobBoardMember member = jobBoardMemberRepository.findByIdAndJobBoardId(jobBoardMemberId, jobBoardId)
            .orElseThrow(() -> new NotAMemberException("User is not a member of this job board."));

        jobBoard.removeMember(member);
        jobBoardMemberRepository.delete(member);
        jobBoardRepository.save(jobBoard);
    }

    @Transactional
    public void setNewOwner(Long jobBoardId, Long jobBoardMemberId) {
        JobBoard jobBoard = jobBoardRepository.findById(jobBoardId)
            .orElseThrow(() -> new JobBoardNotFoundException("Job board does not exist."));

        JobBoardMember member = jobBoardMemberRepository.findByIdAndJobBoardId(jobBoardId, jobBoardMemberId)
            .orElseThrow(() -> new NotAMemberException("User is not a member of this job board"));

        jobBoard.setOwner(member);
        jobBoardRepository.save(jobBoard);
    }

    private JobBoardMember toJobBoardMember(User member) {
        return new JobBoardMember(member, Role.MEMBER);
    }

    private JobBoardResponseDto toJobBoardResponseDto(JobBoard jobBoard, JobBoardMember member) {
        return new JobBoardResponseDto(
            jobBoard.getTitle(),
            member.getId(),
            jobBoard.getMembers()
        );
    }
}
