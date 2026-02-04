package com.casey.applyflow.service;

import org.springframework.stereotype.Service;

import com.casey.applyflow.domain.Company;
import com.casey.applyflow.dto.CompanyRequestDto;
import com.casey.applyflow.dto.CompanyResponseDto;
import com.casey.applyflow.exception.CompanyNotFoundException;
import com.casey.applyflow.repository.CompanyRepository;

import jakarta.transaction.Transactional;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;

    CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Transactional
    public CompanyResponseDto createCompany(CompanyRequestDto request) {
        
        Company company = new Company(
            request.name(), 
            request.location(), 
            request.rating()
        );
        
        Company savedCompany = companyRepository.save(company);

        return new CompanyResponseDto(
            savedCompany.getName(), 
            savedCompany.getLocation(), 
            savedCompany.getRating()
        );
    }

    @Transactional
    public CompanyResponseDto updateCompanyResponseDto(Long companyId, CompanyRequestDto request) {
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new CompanyNotFoundException("Company not found."));

        company.setName(request.name());
        company.setLocation(request.location());
        company.setRating(request.rating());

        return new CompanyResponseDto(
            company.getName(),
            company.getLocation(),
            company.getRating()
        );
    }


}
