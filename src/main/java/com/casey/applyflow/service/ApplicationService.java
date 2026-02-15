package com.casey.applyflow.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import com.casey.applyflow.repository.UserRepository;
import com.casey.applyflow.dto.ApplicationResponseDto;
import com.casey.applyflow.dto.ApplicationRequestDto;
import com.casey.applyflow.dto.UpdateApplicationFieldRequestDto;
import com.casey.applyflow.exception.ApplicationNotFoundException;
import com.casey.applyflow.exception.CompanyNotFoundException;
import com.casey.applyflow.exception.InterviewNotFoundException;
import com.casey.applyflow.exception.UserNotFoundException;

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

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
    
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
    
    // Get Applications (Get)
    @Transactional(readOnly = true)
    public Page<ApplicationResponseDto> getAllApplications(String companyName, Long companyId, Boolean hasInterview,  Pageable pageable) {

        User user = getCurrentUser();
        
        Specification<Application> spec = Specification
            .where(ApplicationSpecification.belongsToUser(user))
            .and(ApplicationSpecification.companyName(companyName))
            .and(ApplicationSpecification.companyId(companyId))
            .and(ApplicationSpecification.hasInterview(hasInterview));

        log.debug("Fetching applications for user {}", user);
 
        return applicationRepository.findAll(spec, pageable)
            .map(application -> new ApplicationResponseDto(
                application.getId(),
                application.getTitle(),
                application.getUrl(),
                application.getStatus(),
                application.getCompany() != null ? application.getCompany().getId() : null,
                application.getInterviews().stream().map(Interview::getId).toList()
            ));
    }

    // Get Application by...
    public ApplicationResponseDto getApplicationById(Long id) {

        User user = getCurrentUser();


        Application application = applicationRepository.findById(id)
            .orElseThrow(() -> new ApplicationNotFoundException("Application not found with id: " + id));

        log.debug("Fetching application {} for user {}", application.getTitle(), user);

        return new ApplicationResponseDto(
            application.getId(),
            application.getTitle(),
            application.getUrl(),
            application.getStatus(),
            application.getCompany().getId(),
            application.getInterviews().stream().map(Interview::getId).toList()
        );
    }

    // Create Application (Post)
    @Transactional
    public ApplicationResponseDto createApplication(ApplicationRequestDto request) {
        Company company = companyRepository.findById(request.companyId())
            .orElseThrow(() -> new CompanyNotFoundException("Company not found"));
        Application application = new Application(request.title(), request.url(), company, request.status());

        User user = getCurrentUser();

        user.addApplication(application);  // save new application to users application list
        Application savedApplication = applicationRepository.save(application);

        if (request.interviewIds() != null) {
            List<Interview> interviews = getInterviews(request.interviewIds());
            savedApplication.setInterviews(interviews);
            for (Interview interview : interviews) {
                interviewRepository.save(interview);
            }
        }

        // log success
        log.info("Created application {} for user {}", request.title(), user.getName());
        
        return new ApplicationResponseDto(
            savedApplication.getId(),
            savedApplication.getTitle(),
            savedApplication.getUrl(),
            savedApplication.getStatus(),
            savedApplication.getCompany() != null ? application.getCompany().getId() : null,
            savedApplication.getInterviews().stream().map(Interview::getId).toList()
        );
    }

    // Update Applicaiton (Put)
    @Transactional
    public ApplicationResponseDto updateApplication(Long applicationId, ApplicationRequestDto request) {
        Application application = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new ApplicationNotFoundException("Application not found with id: " + applicationId));
        Company company = companyRepository.findById(request.companyId())
            .orElseThrow(() -> new CompanyNotFoundException("Company not found"));
        
        // Update all fields
        application.setTitle(request.title());
        application.setUrl(request.url());
        application.setCompany(company);
        application.setStatus(request.status());

        if (request.interviewIds() != null) {
            application.setInterviews(getInterviews(request.interviewIds()));
        }
        
        log.info("Updated application {} for user {}", application.getId(), application.getUser());
        
        return new ApplicationResponseDto(
            application.getId(),
            application.getTitle(),
            application.getUrl(),
            application.getStatus(),
            application.getCompany() != null ? application.getCompany().getId() : null,
            application.getInterviews().stream().map(Interview::getId).toList()
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

        if(request.interviewIds() != null) {
            application.setInterviews(getInterviews(request.interviewIds()));
        }

        log.info("Patched application {} for user {}", application.getTitle(), application.getUser());

        return new ApplicationResponseDto(
            application.getId(),
            application.getTitle(),
            application.getUrl(),
            application.getStatus(),
            application.getCompany() != null ? application.getCompany().getId() : null,
            application.getInterviews().stream().map(Interview::getId).toList()
        );

    }

    // Remove application (DELETE)
    @Transactional
    public ApplicationResponseDto removeApplication(Long applicationId) {
        User user = getCurrentUser();
        
        Application application = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new ApplicationNotFoundException("Application does not exist with id:" + applicationId));
        
        if (user.getApplications().contains(application)) {
            user.removeApplication(application);
            applicationRepository.delete(application);
            log.info("Application has been removed! ID: {}", applicationId);

            return new ApplicationResponseDto(
                application.getId(),
                application.getTitle(),
                application.getUrl(),
                application.getStatus(),
                application.getCompany() != null ? application.getCompany().getId() : null,
                application.getInterviews().stream().map(Interview::getId).toList()
            );
        } else {
            throw new ApplicationNotFoundException("User does not own application with id: " + applicationId);
        }
    }

    private List<Interview> getInterviews(List<Long> interviewIds) {
        if (interviewIds == null) {
            return null;
        }

        List<Interview> interviews = new ArrayList<>();
        for (Long interviewId : interviewIds) {
            Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new InterviewNotFoundException("Interview with id:" + interviewId + "does not exist"));
            interviews.add(interview);
        }

        return interviews;
    }
}
