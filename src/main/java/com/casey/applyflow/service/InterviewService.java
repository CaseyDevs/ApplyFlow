package com.casey.applyflow.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.casey.applyflow.dto.InterviewRequestDto;
import com.casey.applyflow.dto.InterviewResponseDto;
import com.casey.applyflow.exception.ApplicationNotFoundException;
import com.casey.applyflow.repository.ApplicationRepository;
import com.casey.applyflow.repository.InterviewRepository;

import jakarta.persistence.EntityNotFoundException;

import com.casey.applyflow.domain.Application;
import com.casey.applyflow.domain.Interview;

@Service
public class InterviewService {
    private final InterviewRepository interviewRepository;
    private final ApplicationRepository applicationRepository;
    private final Logger log = LoggerFactory.getLogger(InterviewService.class);

    public InterviewService(InterviewRepository interviewRepository, ApplicationRepository applicationRepository) {
        this.interviewRepository = interviewRepository;
        this.applicationRepository = applicationRepository;
    }


    @Transactional(readOnly = true)
    public InterviewResponseDto getInterview(Long interviewId) {
        Interview interview = interviewRepository.findById(interviewId)
            .orElseThrow(() -> new EntityNotFoundException());

        log.debug("Fetching interview");

        // return response dto
        return new InterviewResponseDto(
            interview.getDate(),
            interview.getType(),
            interview.getInterviewer()
        );
    }

    @Transactional
    public InterviewResponseDto createInterview(Long applicationId, InterviewRequestDto request) {
        Application application = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new ApplicationNotFoundException("Application does not exist"));
        
        Interview interview = new Interview(
            request.date(), 
            request.type(), 
            request.interviewer()
        );

        interviewRepository.save(interview);
        application.setInterview(interview);

        log.info("Interview {} saved to database + application {}", interview.getId(), applicationId);

        return new InterviewResponseDto(
            interview.getDate(),
            interview.getType(),
            interview.getInterviewer()
        );
    }
}
