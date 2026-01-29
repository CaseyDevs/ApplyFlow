package com.casey.applyflow;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.casey.applyflow.domain.Application;
import com.casey.applyflow.domain.Company;
import com.casey.applyflow.domain.Interview;
import com.casey.applyflow.domain.enums.Status;
import com.casey.applyflow.repository.ApplicationRepository;
import com.casey.applyflow.repository.CompanyRepository;
import com.casey.applyflow.repository.InterviewRepository;
import com.casey.applyflow.service.ApplicationService;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class ApplicationServiceTests {
    
    @Mock
    private ApplicationRepository applicationRepository;
    
    @Mock
    private CompanyRepository companyRepository;
    
    @Mock
    private InterviewRepository interviewRepository;
    
    @InjectMocks
    private ApplicationService applicationService;
    
    private Company testCompany;
    private Interview testInterview;

    @BeforeEach
    void setUp() {
        testCompany = new Company("Tech Corp", "tech.com", 4.5);
        testInterview = new Interview(LocalDateTime.now(), "Technical", null);
    }

    @Test
    @DisplayName("Should create application successfully when valid data provided")
    void createApplication_Success() {
        // Given
        when(companyRepository.findById(1L)).thenReturn(Optional.of(testCompany));
        when(interviewRepository.findById(1L)).thenReturn(Optional.of(testInterview));
        
        // When
        applicationService.createApplication("Software Engineer", "https://apply.com", 1L, 1L, Status.APPLIED);
        
        // Then
        verify(applicationRepository).save(any(Application.class));
    }
    
    @Test
    @DisplayName("Should throw exception when company not found")
    void createApplication_CompanyNotFound() {
        // Given
        when(companyRepository.findById(999L)).thenReturn(Optional.empty());
        
        // When/Then
        assertThrows(EntityNotFoundException.class, () -> 
            applicationService.createApplication("Software Engineer", "https://apply.com", 999L, 1L, Status.APPLIED)
        );
    }
}