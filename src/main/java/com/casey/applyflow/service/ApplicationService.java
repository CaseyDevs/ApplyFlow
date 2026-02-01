package com.casey.applyflow.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.casey.applyflow.domain.Application;
import com.casey.applyflow.domain.Company;
import com.casey.applyflow.domain.Interview;
import com.casey.applyflow.domain.User;
import com.casey.applyflow.repository.ApplicationRepository;
import com.casey.applyflow.repository.CompanyRepository;
import com.casey.applyflow.repository.InterviewRepository;
import com.casey.applyflow.repository.UserRepository;
import com.casey.applyflow.dto.ApplicationResponseDto;
import com.casey.applyflow.dto.ApplicationRequestDto;
import com.casey.applyflow.dto.UpdateApplicationFieldRequestDto;
import com.casey.applyflow.exception.ApplicationNotFoundException;
import com.casey.applyflow.exception.CompanyNotFoundException;
import com.casey.applyflow.exception.InterviewNotFoundException;
import com.casey.applyflow.exception.UserNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApplicationService {
    private ApplicationRepository applicationRepository;
    private UserRepository userRepository;
    private CompanyRepository companyRepository;
    private InterviewRepository interviewRepository;
    private static final Logger log = LoggerFactory.getLogger(ApplicationService.class);

    ApplicationService(
        ApplicationRepository applicationRepository,
        UserRepository userRepository,
        CompanyRepository companyRepository,
        InterviewRepository interviewRepository
    ) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.interviewRepository = interviewRepository;
    }
    
    // Get Applications (Get)
    @Transactional(readOnly = true)
    public List<ApplicationResponseDto> getAllApplications() {

        // TODO: Replace with authenticated user
        User user = userRepository.findByEmail("test@example.com")
            .orElseThrow(() -> new UserNotFoundException("User not found"));

        // TODO: Add specification & filters
        
        log.debug("Fetching applications for user {}", user);

        return applicationRepository.findAll().stream()
            .map(application -> new ApplicationResponseDto(
                application.getId(),
                application.getTitle(),
                application.getUrl(),
                application.getStatus(),
                application.getCompany(),
                application.getInterview()
            ))
            .collect(Collectors.toList());
    }

    // Get Application by...
    public ApplicationResponseDto getApplicationById(Long id) {

        User user = userRepository.findByEmail("test@example.com")
            .orElseThrow(() -> new UserNotFoundException("User not found"));

        Application application = applicationRepository.findById(id)
            .orElseThrow(() -> new ApplicationNotFoundException("Application not found with id: " + id));

        log.debug("Fetching application {} for user {}", application.getTitle(), user);

        return new ApplicationResponseDto(
            application.getId(),
            application.getTitle(),
            application.getUrl(),
            application.getStatus(),
            application.getCompany(),
            application.getInterview()
        );
    }

    // Create Application (Post)
    @Transactional
    public ApplicationResponseDto createApplication(ApplicationRequestDto request) {
        Company company = companyRepository.findById(request.companyId())
            .orElseThrow(() -> new CompanyNotFoundException("Company not found"));
        Interview interview = interviewRepository.findById(request.interviewId())
            .orElseThrow(() -> new InterviewNotFoundException("Interview not found"));

        Application application = new Application(request.title(), request.url(), company, interview, request.status());

        // TODO: Replace with authenticated user
        User user = userRepository.findByEmail("test@example.com")
            .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.addApplication(application);  // save new application to users application list
        Application savedApplication = applicationRepository.save(application);

        // log success
        log.info("Created application {} for user {}", request.title(), user.getName());
        
        return new ApplicationResponseDto(
            savedApplication.getId(),
            savedApplication.getTitle(),
            savedApplication.getUrl(),
            savedApplication.getStatus(),
            savedApplication.getCompany(),
            savedApplication.getInterview()
        );
    }

    // Update Applicaiton (Put)
    @Transactional
    public ApplicationResponseDto updateApplication(Long applicationId, ApplicationRequestDto request) {
        Application application = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new ApplicationNotFoundException("Application not found with id: " + applicationId));
        Company company = companyRepository.findById(request.companyId())
            .orElseThrow(() -> new CompanyNotFoundException("Company not found"));
        Interview interview = interviewRepository.findById(request.interviewId())
            .orElseThrow(() -> new InterviewNotFoundException("Interview not found"));
        
        // Update all fields
        application.setTitle(request.title());
        application.setUrl(request.url());
        application.setCompany(company);
        application.setInterview(interview);
        application.setStatus(request.status());
        
        log.info("Updated application {} for user {}", application.getId(), application.getUser());
        
        return new ApplicationResponseDto(
            application.getId(),
            application.getTitle(),
            application.getUrl(),
            application.getStatus(),
            application.getCompany(),
            application.getInterview()
        );
    }


    // Update Apllication (Patch)
    @Transactional
    public ApplicationResponseDto updateApplicationField(Long applicationId, UpdateApplicationFieldRequestDto request) {
        Application application = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new ApplicationNotFoundException("Application with id: " + applicationId + "does not exist"));

        if(request.title() != null) {
            application.setTitle(request.title());
        }
        
        if(request.url() != null) {
            application.setUrl(request.url());
        }

        if(request.status() != null) {
            application.setStatus(request.status());
        }

        if(request.companyId() != null) {
            Company company = companyRepository.findById(request.companyId())
                .orElseThrow(() -> new CompanyNotFoundException("Company with id:" + request.companyId() + "does not exist"));
            application.setCompany(company);
        }

        if(request.interviewId() != null) {
            Interview interview = interviewRepository.findById(request.interviewId())
            .orElseThrow(() -> new InterviewNotFoundException("Interview with id:" + request.interviewId() + "does not exist"));
            application.setInterview(interview);
        }

        log.info("Patched application {} for user {}", application.getTitle(), application.getUser());

        return new ApplicationResponseDto(
            application.getId(),
            application.getTitle(),
            application.getUrl(),
            application.getStatus(),
            application.getCompany(),
            application.getInterview()
        );

    }
}
