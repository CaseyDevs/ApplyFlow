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
import com.casey.applyflow.domain.enums.Status;
import com.casey.applyflow.dto.ApplicationResponseDto;

import jakarta.persistence.EntityNotFoundException;
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
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // TODO: Add specification & filters
        
        log.debug("Fetching applications for user {}", user);

        return applicationRepository.findAll().stream()
            .map(application -> new ApplicationResponseDto(
                application.getTitle(),
                application.getUrl(),
                application.getStatus(),
                application.getCompany(),
                application.getInterview()
            ))
            .collect(Collectors.toList());
    }

    // Get Application by...
    public ApplicationResponseDto getApplicationByTitle(String title) {

        User user = userRepository.findByEmail("test@example.com")
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Application application = applicationRepository.findByTitle(title)
            .orElseThrow(() -> new EntityNotFoundException("Application not found with title: " + title));

        log.debug("Fetching application {} for user {}", application.getTitle(), user);

        return new ApplicationResponseDto(
            application.getTitle(),
            application.getUrl(),
            application.getStatus(),
            application.getCompany(),
            application.getInterview()
        );
    }

    // Create Application (Post)
    @Transactional
    public ApplicationResponseDto createApplication(String title, String url, Long companyId, Long interviewId, Status status) {
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new EntityNotFoundException("Company not found"));
        Interview interview = interviewRepository.findById(interviewId)
            .orElseThrow(() -> new EntityNotFoundException("Interview not found"));

        Application application = new Application(title, url, company, interview, status);

        // TODO: Replace with authenticated user
        User user = userRepository.findByEmail("test@example.com")
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

        user.addApplication(application);  // save new application to users application list
        Application savedApplication = applicationRepository.save(application);

        // log success
        log.info("Created application {} for user {}", title, user.getName());
        
        return new ApplicationResponseDto(
            savedApplication.getTitle(),
            savedApplication.getUrl(),
            savedApplication.getStatus(),
            savedApplication.getCompany(),
            savedApplication.getInterview()
        );
    }

    @Transactional
    public ApplicationResponseDto updateApplication(Long applicationId, String title, String url, Long companyId, Long interviewId, Status status) {
        Application application = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new EntityNotFoundException("Application not found with id: " + applicationId));
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new EntityNotFoundException("Company not found"));
        Interview interview = interviewRepository.findById(interviewId)
            .orElseThrow(() -> new EntityNotFoundException("Interview not found"));
        
        // Update all fields
        application.setTitle(title);
        application.setUrl(url);
        application.setCompany(company);
        application.setInterview(interview);
        application.setStatus(status);
        
        log.info("Updated application {}", applicationId);
        
        return new ApplicationResponseDto(
            application.getTitle(),
            application.getUrl(),
            application.getStatus(),
            application.getCompany(),
            application.getInterview()
        );
    }

    // Update Applicaiton (Put)

    // Update Apllication (Patch)
}
