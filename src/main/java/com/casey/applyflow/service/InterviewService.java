package com.casey.applyflow.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.casey.applyflow.dto.InterviewRequestDto;
import com.casey.applyflow.dto.InterviewResponseDto;
import com.casey.applyflow.exception.ApplicationNotFoundException;
import com.casey.applyflow.exception.ContactNotFoundException;
import com.casey.applyflow.exception.InterviewNotFoundException;
import com.casey.applyflow.repository.ApplicationRepository;
import com.casey.applyflow.repository.ContactRepository;
import com.casey.applyflow.repository.InterviewRepository;
import com.casey.applyflow.domain.Application;
import com.casey.applyflow.domain.Contact;
import com.casey.applyflow.domain.Interview;

@Service
public class InterviewService {
    private final InterviewRepository interviewRepository;
    private final ApplicationRepository applicationRepository;
    private final ContactRepository contactRepository;
    private final Logger log = LoggerFactory.getLogger(InterviewService.class);

    public InterviewService(
        InterviewRepository interviewRepository, 
        ApplicationRepository applicationRepository,
        ContactRepository contactRepository
    ) {
        this.interviewRepository = interviewRepository;
        this.applicationRepository = applicationRepository;
        this.contactRepository = contactRepository;
    }


    @Transactional(readOnly = true)
    public InterviewResponseDto getInterview(Long interviewId) {
        Interview interview = interviewRepository.findById(interviewId)
            .orElseThrow(() -> new InterviewNotFoundException("Interview not found!"));

        log.debug("Fetching interview {}", interviewId);

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
        Contact interviewer = contactRepository.findById(request.interviewerId())
            .orElseThrow(() -> new ContactNotFoundException("Contact does not exist"));

        Interview interview = new Interview(
            request.date(), 
            request.type(), 
            interviewer
        );

        interviewRepository.save(interview);
        application.setInterview(interview);
        applicationRepository.save(application);

        log.info("Interview {} saved to database + application {}", interview.getId(), applicationId);

        return new InterviewResponseDto(
            interview.getDate(),
            interview.getType(),
            interview.getInterviewer()
        );
    }

    @Transactional
    public InterviewResponseDto updateInterview(Long interviewId, InterviewRequestDto request) {
        Interview interview = interviewRepository.findById(interviewId)
            .orElseThrow(() -> new InterviewNotFoundException("Interview not found!"));
        Contact interviewer = contactRepository.findById(request.interviewerId())
            .orElseThrow(() -> new ContactNotFoundException("Contact does not exist"));

        interview.setDate(request.date());
        interview.setType(request.type());
        interview.setInterviewer(interviewer);

        log.info("Interview {} updated", interviewId);

        return new InterviewResponseDto(
            interview.getDate(),
            interview.getType(),
            interview.getInterviewer()
        );
    }

    @Transactional
    public void deleteInterview(Long applicationId, Long interviewId) {
        Interview interview = interviewRepository.findById(interviewId)
            .orElseThrow(() -> new InterviewNotFoundException("Interview not found!"));
        Application application = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new ApplicationNotFoundException("Application not found"));

        // check interview matches the entity in the application
        if (!interview.equals(application.getInterview())) {
            throw new InterviewNotFoundException("Interview does not belong to application");
        }

        application.setInterview(null);
        applicationRepository.save(application);
        interviewRepository.delete(interview);

        log.info("Interview {} removed from application {}", interviewId, applicationId);
    }
}
