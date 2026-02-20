package com.casey.applyflow.service;

import org.springframework.stereotype.Service;

import com.casey.applyflow.domain.JobBoard;
import com.casey.applyflow.domain.JobBoardMember;
import com.casey.applyflow.domain.User;
import com.casey.applyflow.domain.enums.Role;
import com.casey.applyflow.exception.JobBoardNotFoundException;
import com.casey.applyflow.repository.JobBoardMemberRepository;
import com.casey.applyflow.repository.JobBoardRepository;

import jakarta.transaction.Transactional;

@Service
public class JobBoardService {
    private JobBoardRepository jobBoardRepository;
    private JobBoardMemberRepository jobBoardMemberRepository;

    public JobBoardService(
        JobBoardRepository jobBoardRepository, 
        JobBoardMemberRepository jobBoardMemberRepository
    ) {
        this.jobBoardRepository = jobBoardRepository;
        this.jobBoardMemberRepository = jobBoardMemberRepository;
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

    public JobBoardMember toJobBoardMember(User member) {
        return new JobBoardMember(member, Role.MEMBER);
    }
}
