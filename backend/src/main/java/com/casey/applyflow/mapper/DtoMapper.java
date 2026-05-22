package com.casey.applyflow.mapper;

import org.springframework.stereotype.Component;

import com.casey.applyflow.dto.ApplicationResponseDto;
import com.casey.applyflow.dto.CompanyResponseDto;
import com.casey.applyflow.dto.JobBoardApplicationResponseDto;
import com.casey.applyflow.dto.JobBoardMemberDto;
import com.casey.applyflow.dto.JobBoardResponseDto;
import com.casey.applyflow.dto.JobBoardStatusResponseDto;
import com.casey.applyflow.dto.UserResponseDto;
import com.casey.applyflow.model.Application;
import com.casey.applyflow.model.Company;
import com.casey.applyflow.model.JobBoard;
import com.casey.applyflow.model.JobBoardApplication;
import com.casey.applyflow.model.JobBoardApplicationStatus;
import com.casey.applyflow.model.JobBoardMember;
import com.casey.applyflow.model.User;

@Component
public class DtoMapper {
    
    public DtoMapper() {}

    public ApplicationResponseDto toApplicationResponseDto(Application application) {
        if (application == null) {
            return null;
        }

        return new ApplicationResponseDto (
            application.getId(),
            application.getTitle(),
            application.getUrl(),
            application.getStatus(),
            application.getLocation(),
            application.getCompany() != null ? application.getCompany().getId() : null,
            application.getCreatedAt()
        );
    }

    public CompanyResponseDto toCompanyResponseDto(Company company) {
        if (company == null) {
            return null;
        }

        return new CompanyResponseDto(
            company.getId(),
            company.getName(),
            company.getRating()
        );
    }

    public JobBoardResponseDto toJobBoardResponseDto(JobBoard jobBoard) {
        return new JobBoardResponseDto(
            jobBoard.getId(),
            jobBoard.getTitle(),
            jobBoard.getOwner().getId(),
            jobBoard.getMembers().stream().map(this::toJobBoardMemberDto).toList(),
            jobBoard.getApplications().stream().map(this::toJobBoardApplicationResponseDto).toList()
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

    public JobBoardStatusResponseDto toJobBoardStatusResponseDto(JobBoardApplicationStatus status) {
        return new JobBoardStatusResponseDto (
            status.getId(),
            status.getJobBoardApplication().getId(),
            status.getUser().getId(),
            status.getUser().getEmail(),
            status.getStatus(),
            status.getUpdatedAt(),
            status.getUpdatedBy()
        );
    }

    public JobBoardApplicationResponseDto toJobBoardApplicationResponseDto(JobBoardApplication jba) {
        return new JobBoardApplicationResponseDto(
            jba.getId(),
            toApplicationResponseDto(jba.getApplication()),
            jba.getAddedAt(),
            jba.getJobBoardMember().getUser().getEmail(),
            jba.getStatusList().stream()
                .map(this::toJobBoardStatusResponseDto)
                .toList()
        );    
    }
}
