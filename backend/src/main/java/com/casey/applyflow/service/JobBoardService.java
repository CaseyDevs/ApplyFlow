package com.casey.applyflow.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.casey.applyflow.dto.JobBoardRequestDto;
import com.casey.applyflow.dto.JobBoardResponseDto;
import com.casey.applyflow.dto.JobBoardStatsDto;
import com.casey.applyflow.exception.JobBoardNotFoundException;
import com.casey.applyflow.mapper.DtoMapper;
import com.casey.applyflow.model.JobBoard;
import com.casey.applyflow.model.JobBoardMember;
import com.casey.applyflow.model.User;
import com.casey.applyflow.model.builder.JobBoardBuilder;
import com.casey.applyflow.model.enums.Role;
import com.casey.applyflow.repository.JobBoardRepository;

@Service
public class JobBoardService {
    private static final Logger log = LoggerFactory.getLogger(JobBoardService.class);
    private final JobBoardRepository jobBoardRepository;
    private final CurrentUserProvider currentUserProvider;
    private final JobBoardMemberService jobBoardMemberService;
    private final JobBoardAuthorizationService jobBoardAuthorizationService;
    private final DtoMapper dtoMapper;

    public JobBoardService(
        CurrentUserProvider currentUserProvider,
        DtoMapper dtoMapper,
        JobBoardRepository jobBoardRepository,
        JobBoardApplicationService jobBoardApplicationService,
        JobBoardMemberService jobBoardMemberService,
        JobBoardAuthorizationService jobBoardAuthorizationService
    ) {
        this.dtoMapper = dtoMapper;
        this.currentUserProvider = currentUserProvider;
        this.jobBoardRepository = jobBoardRepository;
        this.jobBoardMemberService = jobBoardMemberService;
        this.jobBoardAuthorizationService = jobBoardAuthorizationService;
    }

    @Transactional(readOnly = true)
    public Page<JobBoardResponseDto> getAllJobBoards(Pageable pageable) {
        User currentUser = currentUserProvider.getCurrentUser();

        log.info("Getting job boards for user {} (as member)", currentUser.getId());

        // Fetch job boards where user is a member (not just owner)
        return jobBoardRepository.findAllByMembersUserId(currentUser.getId(), pageable)
            .map(dtoMapper::toJobBoardResponseDto);
    }

    @Transactional(readOnly = true)
    public JobBoardResponseDto getJobBoardById(Long jobBoardId) {
        User currentUser = currentUserProvider.getCurrentUser();

        log.info("Getting job board {} for user {}", jobBoardId, currentUser.getId());

        JobBoard jobBoard = jobBoardRepository.findByIdAndMembersUserId(jobBoardId, currentUser.getId())
            .orElseThrow(() -> new JobBoardNotFoundException("Job Board does not exist."));
        
        return dtoMapper.toJobBoardResponseDto(jobBoard);
    }

    @Transactional
    public JobBoardResponseDto createJobBoard(JobBoardRequestDto request) {
        User currentUser = currentUserProvider.getCurrentUser();
        log.info("Creating job board '{}' for user {}", request.title(), currentUser.getId());
        
        JobBoardMember owner = jobBoardMemberService.toJobBoardMember(currentUser);
        owner.setRole(Role.OWNER);

        JobBoard jobBoard = new JobBoardBuilder()
            .withTitle(request.title())
            .withOwner(owner)
            .addMember(owner)
            .build();
        
        jobBoardRepository.save(jobBoard);
        
        log.info("Job board '{}' created successfully with ID {}", jobBoard.getTitle(), jobBoard.getId());
        return dtoMapper.toJobBoardResponseDto(jobBoard);
    }

    @Transactional
    public JobBoardResponseDto updateJobBoard(Long jobBoardId, JobBoardRequestDto request) {
        if (jobBoardId == null) {
            throw new IllegalArgumentException("Job board ID cannot be null");
        }

        User currentUser = currentUserProvider.getCurrentUser();
        JobBoard jobBoard = jobBoardAuthorizationService.getJobBoardForOwner(jobBoardId, currentUser.getId());

        log.info("Updating job board {} title", jobBoard.getId());
        jobBoard.setTitle(request.title());
        jobBoardRepository.save(jobBoard);

        return dtoMapper.toJobBoardResponseDto(jobBoard);
    }

    @Transactional
    public void deleteJobBoard(Long jobBoardId) {
        if (jobBoardId == null) {
            throw new IllegalArgumentException("Job board ID cannot be null");
        }

        User currentUser = currentUserProvider.getCurrentUser();
        JobBoard jobBoard = jobBoardAuthorizationService.getJobBoardForOwner(jobBoardId, currentUser.getId());
        
        log.info("Deleting job board {} owned by user {}", jobBoardId, currentUser.getId());
        
        // Detach all applications from the job board before deletion
        jobBoard.getApplications().forEach(app -> app.setJobBoard(jobBoard));

        // Members will be cascade deleted
        jobBoardRepository.delete(jobBoard);
        
        log.info("Job board {} deleted successfully", jobBoardId);
    }

    @Transactional(readOnly = true)
    public JobBoardStatsDto getJobBoardStats(Long jobBoardId) {
        if (jobBoardId == null) {
            throw new IllegalArgumentException("Job board ID cannot be null");
        }

        User currentUser = currentUserProvider.getCurrentUser();
        JobBoard jobBoard = jobBoardAuthorizationService.getJobBoardForMember(jobBoardId, currentUser.getId());
        
        return new JobBoardStatsDto(
            jobBoard.getOwner(),
            jobBoard.getApplications().size(),
            jobBoard.getMembers()
        );
    }   
}
