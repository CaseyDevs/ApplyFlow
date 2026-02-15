package com.casey.applyflow.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.casey.applyflow.dto.ContactResponseDto;
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
    public InterviewResponseDto getInterview(Long applicationId, Long interviewId) {
        Application application = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new ApplicationNotFoundException("Application does not exist"));

        Interview interview = interviewRepository.findById(interviewId)
            .orElseThrow(() -> new InterviewNotFoundException("Interview not found!"));

        log.debug("Fetching interview {}", interviewId);

        if (interview.getApplication() == null
            || !interview.getApplication().getId().equals(application.getId())) {
            throw new InterviewNotFoundException("Interview does not belong to application");
        }

        return toResponseDto(interview);
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
            interviewer,
            application
        );

        application.addInterview(interview);
        interviewRepository.save(interview);

        log.info("Interview {} saved to database + application {}", interview.getId(), applicationId);

        return toResponseDto(interview);
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

        return toResponseDto(interview);
    }

    private InterviewResponseDto toResponseDto(Interview interview) {
        return new InterviewResponseDto(
            interview.getId(),
            interview.getDate(),
            interview.getType(),
            toContactResponseDto(interview.getInterviewer())
        );
    }

    private ContactResponseDto toContactResponseDto(Contact contact) {
        if (contact == null) {
            return null;
        }

        return new ContactResponseDto(
            contact.getId(),
            contact.getName(),
            contact.getEmail(),
            contact.getPhoneNumber()
        );
    }

    @Transactional
    public void deleteInterview(Long applicationId, Long interviewId) {
        Interview interview = interviewRepository.findById(interviewId)
            .orElseThrow(() -> new InterviewNotFoundException("Interview not found!"));
        Application application = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new ApplicationNotFoundException("Application not found"));

        // check interview matches the entity in the application
        if (interview.getApplication() == null
            || !interview.getApplication().getId().equals(application.getId())) {
            throw new InterviewNotFoundException("Interview does not belong to application");
        }

        application.removeInterview(interview);
        interviewRepository.delete(interview);

        log.info("Interview {} removed from application {}", interviewId, applicationId);
    }
}
