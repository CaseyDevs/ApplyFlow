package com.casey.applyflow.service;

import java.util.List;

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
import com.casey.applyflow.domain.User;

@Service
public class InterviewService {

    private final CurrentUserProvider currentUserProvider;
    private final InterviewRepository interviewRepository;
    private final ApplicationRepository applicationRepository;
    private final ContactRepository contactRepository;
    private final Logger log = LoggerFactory.getLogger(InterviewService.class);

    public InterviewService(
        InterviewRepository interviewRepository, 
        ApplicationRepository applicationRepository,
        ContactRepository contactRepository,
        CurrentUserProvider currentUserProvider
    ) {
        this.interviewRepository = interviewRepository;
        this.applicationRepository = applicationRepository;
        this.contactRepository = contactRepository;
        this.currentUserProvider = currentUserProvider;
    }


    @Transactional(readOnly = true)
    public InterviewResponseDto getInterview(Long applicationId, Long interviewId) {
        User user = currentUserProvider.getCurrentUser();

        Interview interview = interviewRepository.findByIdAndApplicationUserId(interviewId, user.getId())
            .orElseThrow(() -> new InterviewNotFoundException("Interview not found"));

        log.debug("Fetching interview {}", interviewId);

        return toInterviewResponseDto(interview);
    }

    @Transactional(readOnly = true)
    public List<InterviewResponseDto> getAllInterviews(Long applicationId) {
        User user = currentUserProvider.getCurrentUser();

        List<Interview> interviews = interviewRepository.findAllByApplicationIdAndApplicationUserId(applicationId, user.getId());

        log.debug("Fetching interviews");

        return interviews.stream()
            .map(this::toInterviewResponseDto)
            .toList();
    }

    @Transactional
    public InterviewResponseDto createInterview(Long applicationId, InterviewRequestDto request) {
        User user = currentUserProvider.getCurrentUser();

        Application application = applicationRepository.findByIdAndUserId(applicationId, user.getId())
            .orElseThrow(() -> new ApplicationNotFoundException("Application does not exist"));

        Contact interviewer = null;
        if (request.interviewerId() != null) {
            interviewer = contactRepository.findById(request.interviewerId())
                .orElseThrow(() -> new ContactNotFoundException("Contact does not exist"));
        }

        Interview interview = new Interview(
            request.date(), 
            request.type(), 
            interviewer,
            application,
            null
        );

        interviewRepository.save(interview);
        application.addInterview(interview);

        log.info("Interview {} saved to database + application {}", interview.getId(), applicationId);

        return toInterviewResponseDto(interview);
    }

    @Transactional
    public InterviewResponseDto updateInterview(Long applicationId, Long interviewId, InterviewRequestDto request) {
        User user = currentUserProvider.getCurrentUser();

        Interview interview = interviewRepository.findByIdAndApplicationIdAndApplicationUserId(applicationId, interviewId, user.getId())
            .orElseThrow(() -> new InterviewNotFoundException("Interview not found!"));
        
        Contact interviewer = null;
        if (request.interviewerId() != null) {
            interviewer = contactRepository.findById(request.interviewerId())
                .orElseThrow(() -> new ContactNotFoundException("Contact does not exist"));
        }

        interview.setDate(request.date());
        interview.setType(request.type());
        interview.setInterviewer(interviewer);

        log.info("Interview {} updated", interviewId);

        return toInterviewResponseDto(interview);
    }

    private InterviewResponseDto toInterviewResponseDto(Interview interview) {
        return new InterviewResponseDto(
            interview.getId(),
            interview.getDate(),
            interview.getType(),
            toContactResponseDto(interview.getInterviewer()),
            null
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
        User user = currentUserProvider.getCurrentUser();

        Application application = applicationRepository.findByIdAndUserId(applicationId, user.getId())
            .orElseThrow(() -> new ApplicationNotFoundException("Application not found!"));

        Interview interview = interviewRepository.findByIdAndApplicationUserId(interviewId, user.getId())
            .orElseThrow(() -> new InterviewNotFoundException("Interview not found!"));

        interviewRepository.delete(interview);
        application.removeInterview(interview);

        log.info("Interview {} removed from application {}", interviewId, applicationId);
    }
}
