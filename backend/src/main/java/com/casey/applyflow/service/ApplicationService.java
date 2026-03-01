package com.casey.applyflow.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.casey.applyflow.domain.Application;
import com.casey.applyflow.domain.Company;
import com.casey.applyflow.domain.Interview;
import com.casey.applyflow.domain.User;
import com.casey.applyflow.repository.ApplicationRepository;
import com.casey.applyflow.repository.ApplicationSpecification;
import com.casey.applyflow.repository.CompanyRepository;
import com.casey.applyflow.repository.InterviewRepository;
import com.casey.applyflow.dto.ApplicationResponseDto;
import com.casey.applyflow.dto.ApplicationRequestDto;
import com.casey.applyflow.dto.UpdateApplicationFieldRequestDto;
import com.casey.applyflow.exception.ApplicationNotFoundException;
import com.casey.applyflow.exception.CompanyNotFoundException;

@Service
public class ApplicationService {
    private final CurrentUserProvider currentUserProvider;
    private final ApplicationRepository applicationRepository;
    private final CompanyRepository companyRepository;
    private static final Logger log = LoggerFactory.getLogger(ApplicationService.class);

    ApplicationService(
        ApplicationRepository applicationRepository,
        CompanyRepository companyRepository,
        InterviewRepository interviewRepository, 
        CurrentUserProvider currentUserProvider
    ) {
        this.applicationRepository = applicationRepository;
        this.companyRepository = companyRepository;
        this.currentUserProvider = currentUserProvider;
    }
    
    // Get Applications (Get)
    @Transactional(readOnly = true)
    public Page<ApplicationResponseDto> getAllApplications(String companyName, Long companyId, Boolean hasInterview,  Pageable pageable) {
        User user = currentUserProvider.getCurrentUser();
        
        Specification<Application> spec = Specification
            .where(ApplicationSpecification.belongsToUser(user))
            .and(ApplicationSpecification.companyName(companyName))
            .and(ApplicationSpecification.companyId(companyId))
            .and(ApplicationSpecification.hasInterview(hasInterview));

        log.debug("Fetching applications for user {}", user);
 
        return applicationRepository.findAll(spec, pageable)
            .map(this::toApplicationResponseDto);
    }

    // Get Application by...
    public ApplicationResponseDto getApplicationById(Long id) {
        User user = currentUserProvider.getCurrentUser();

        Application application = applicationRepository.findByIdAndUserId(id, user.getId())
            .orElseThrow(() -> new ApplicationNotFoundException("Application not found with id: " + id));

        log.debug("Fetching application {} for user {}", application.getTitle(), user);

        return toApplicationResponseDto(application);
    }

    // Create Application (Post)
    @Transactional
    public ApplicationResponseDto createApplication(ApplicationRequestDto request) {
        User user = currentUserProvider.getCurrentUser();

        Company company = companyRepository.findById(request.companyId())
            .orElseThrow(() -> new CompanyNotFoundException("Company not found"));

        Application application = new Application(request.title(), request.url(), company, request.status());


        user.addApplication(application);  // save new application to users application list
        Application savedApplication = applicationRepository.save(application);

        // log success
        log.info("Created application {} for user {}", request.title(), user.getName());
        
        return toApplicationResponseDto(savedApplication);
    }

    // Update Applicaiton (Put)
    @Transactional
    public ApplicationResponseDto updateApplication(Long applicationId, ApplicationRequestDto request) {
        User user = currentUserProvider.getCurrentUser();

        Application application = applicationRepository.findByIdAndUserId(applicationId, user.getId())
            .orElseThrow(() -> new ApplicationNotFoundException("Application not found with id: " + applicationId));
        Company company = companyRepository.findById(request.companyId())
            .orElseThrow(() -> new CompanyNotFoundException("Company not found"));
        
        // Update all fields
        application.setTitle(request.title());
        application.setUrl(request.url());
        application.setCompany(company);
        application.setStatus(request.status());
        
        log.info("Updated application {} for user {}", application.getId(), application.getUser());
        
        return toApplicationResponseDto(application);
    }

    protected ApplicationResponseDto toApplicationResponseDto(Application application) {
        if (application == null) {
            return null;
        }

        return new ApplicationResponseDto (
            application.getId(),
            application.getTitle(),
            application.getUrl(),
            application.getStatus(),
            application.getCompany() != null ? application.getCompany().getId() : null,
            getAllInterviewIds(application)
        );
    }

    // Update Apllication (Patch)
    @Transactional
    public ApplicationResponseDto updateApplicationField(Long applicationId, UpdateApplicationFieldRequestDto request) {
        User user = currentUserProvider.getCurrentUser();

        Application application = applicationRepository.findByIdAndUserId(applicationId, user.getId())
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

        log.info("Patched application {} for user {}", application.getTitle(), application.getUser());

        return toApplicationResponseDto(application);
    }

    // Remove application (DELETE)
    @Transactional
    public ApplicationResponseDto removeApplication(Long applicationId) {
        User user = currentUserProvider.getCurrentUser();
        
        Application application = applicationRepository.findByIdAndUserId(applicationId, user.getId())
            .orElseThrow(() -> new ApplicationNotFoundException("Application does not exist with id:" + applicationId));
        
        user.removeApplication(application);
        applicationRepository.delete(application);
        log.info("Application has been removed! ID: {}", applicationId);

        return toApplicationResponseDto(application);
    }

    private List<Long> getAllInterviewIds(Application application) {
        return application.getInterviews() != null
        ? application.getInterviews()
            .stream()
            .map(Interview::getId)
            .toList()
        : null;
    }
}
