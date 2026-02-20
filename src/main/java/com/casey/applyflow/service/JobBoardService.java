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
import com.casey.applyflow.repository.JobBoardMemberRepository;
import com.casey.applyflow.repository.JobBoardRepository;
import com.casey.applyflow.repository.UserRepository;

import jakarta.transaction.Transactional;

// TODO: ADD LOGGING, MEMBER REMOVAL, SET NEW OWNER, GET APPLICATIONS ETC...

@Service
public class JobBoardService {
    private JobBoardRepository jobBoardRepository;
    private JobBoardMemberRepository jobBoardMemberRepository;
    private UserRepository userRepository;

    public JobBoardService(
        JobBoardRepository jobBoardRepository, 
        JobBoardMemberRepository jobBoardMemberRepository
    ) {
        this.jobBoardRepository = jobBoardRepository;
        this.jobBoardMemberRepository = jobBoardMemberRepository;
    }

    @Transactional
    public JobBoardResponseDto createJobBoard(JobBoardRequestDto request) {
        User user = userRepository.findById(request.userId())
            .orElseThrow(() -> new UserNotFoundException("User does not exist"));

        JobBoardMember owner = toJobBoardMember(user);

        JobBoard jobBoard = new JobBoard(
            request.title(), 
            owner,
            request.members()    
        );

        return toJobBoardResponseDto(jobBoard, owner);
    }
    
    @Transactional
    public void addMember(Long jobBoardId, User newMember) {
        JobBoard jobBoard = jobBoardRepository.findById(jobBoardId)
            .orElseThrow(() -> new JobBoardNotFoundException("Job board does not exist."));
        
        JobBoardMember member = toJobBoardMember(newMember);

        if(jobBoard.getMembers().contains(member)) {
            throw new RuntimeException("This user is already a member."); // TODO: Change exception
        }

        jobBoard.addMember(member);
        jobBoardMemberRepository.save(member);
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
