package com.casey.applyflow.service;

import org.springframework.stereotype.Service;

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

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;



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
    
    // Get Application (Get)
    
    // Get Application by...

    // Create Application (Post)

    @Transactional
    public void createApplication(String title, String url, Long companyId, Long interviewId, Status status) {
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new EntityNotFoundException("Company not found"));
        Interview interview = interviewRepository.findById(interviewId)
            .orElseThrow(() -> new EntityNotFoundException("Interview not found"));

        Application application = new Application(title, url, company, interview, status);

        // TODO: Replace with authenticated user
        User user = new User("Test", "test@example.com", "password");

        user.addApplication(application);  // save new application to users application list
        applicationRepository.save(application);

        // log success
        log.info("Created application {} for user {}", title, user.getName());
    }

    // Update Applicaiton (Put)

    // Update Apllication (Patch)
}
