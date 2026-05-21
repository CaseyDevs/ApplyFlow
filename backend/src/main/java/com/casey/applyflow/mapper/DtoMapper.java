package com.casey.applyflow.mapper;

import org.springframework.stereotype.Component;

import com.casey.applyflow.dto.JobBoardMemberDto;
import com.casey.applyflow.dto.JobBoardResponseDto;
import com.casey.applyflow.dto.UserResponseDto;
import com.casey.applyflow.model.JobBoard;
import com.casey.applyflow.model.JobBoardMember;
import com.casey.applyflow.model.User;
import com.casey.applyflow.service.JobBoardApplicationService;

@Component
public class DtoMapper {
    private final JobBoardApplicationService jobBoardApplicationService;
    
    public DtoMapper(JobBoardApplicationService jobBoardApplicationService) {
        this.jobBoardApplicationService = jobBoardApplicationService;
    }

    public JobBoardResponseDto toJobBoardResponseDto(JobBoard jobBoard) {
        return new JobBoardResponseDto(
            jobBoard.getId(),
            jobBoard.getTitle(),
            jobBoard.getOwner().getId(),
            jobBoard.getMembers().stream().map(this::toJobBoardMemberDto).toList(),
            jobBoard.getApplications().stream().map(jobBoardApplicationService::toJobBoardApplicationResponseDto).toList()
        );
    }

    private JobBoardMemberDto toJobBoardMemberDto(JobBoardMember member) {
        return new JobBoardMemberDto(
            member.getId(),
            toUserResponseDto(member.getUser()),
            member.getRole()
        );
    }

    private UserResponseDto toUserResponseDto(User user) {
        return new UserResponseDto(
            user.getId(),
            user.getName(),
            user.getEmail()
        );
    }

}
