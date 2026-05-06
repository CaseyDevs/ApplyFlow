package com.casey.applyflow.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.casey.applyflow.dto.CompanyRequestDto;
import com.casey.applyflow.dto.CompanyResponseDto;
import com.casey.applyflow.exception.CompanyInUseException;
import com.casey.applyflow.exception.CompanyNotFoundException;
import com.casey.applyflow.model.Company;
import com.casey.applyflow.repository.CompanyRepository;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;
    private static final Logger log = LoggerFactory.getLogger(CompanyService.class);

    CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Transactional(readOnly = true)
    public Page<CompanyResponseDto> getAllCompanies(Pageable pageable) {
        log.debug("Fetching all known companies");

        return companyRepository.findAll(pageable)
            .map(this::toCompanyResponseDto);
    }

    @Transactional(readOnly = true)
    public CompanyResponseDto getCompanyById(Long companyId) {
        if (companyId == null) return null;

        log.debug("Fetching company {}", companyId);

        return companyRepository.findById(companyId)
            .map(this::toCompanyResponseDto)
            .orElseThrow(() -> new CompanyNotFoundException("Company does not exist"));
    }

    @Transactional
    public CompanyResponseDto createCompany(CompanyRequestDto request) {
        Company company = new Company(
            request.name(), 
            request.location(), 
            request.rating()
        );
        
        Company savedCompany = companyRepository.save(company);

        log.info("Company - {} - created successfully.", company.getName());

        return toCompanyResponseDto(savedCompany);
    }

    @Transactional
    public CompanyResponseDto updateCompany(Long companyId, CompanyRequestDto request) {
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new CompanyNotFoundException("Company not found."));

        company.setName(request.name());
        company.setLocation(request.location());
        company.setRating(request.rating());

        log.info("Company - {} - updated successfully.", company.getName());

        return toCompanyResponseDto(company);
    }

    private CompanyResponseDto toCompanyResponseDto(Company company) {
        if (company == null) {
            return null;
        }

        return new CompanyResponseDto(
            company.getId(),
            company.getName(),
            company.getLocation(),
            company.getRating()
        );
    }

    @Transactional
    public void deleteCompany(Long id) {
        Company company = companyRepository.findById(id)
            .orElseThrow(() -> new CompanyNotFoundException("Company not found"));

        // ensure no applications currently use this company
        if(!company.getApplications().isEmpty()) {
            throw new CompanyInUseException("Cannot delete company with existing applications");
        }

        companyRepository.delete(company);
        log.info("Company {} deleted successfully!", company.getName());
    }

}
