package com.casey.applyflow.service;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.casey.applyflow.domain.Company;
import com.casey.applyflow.dto.CompanyResponseDto;
import com.casey.applyflow.dto.ContactResponseDto;
import com.casey.applyflow.exception.CompanyNotFoundException;
import com.casey.applyflow.repository.CompanyRepository;

@Service
public class ContactService {
    private final CompanyRepository companyRepository;

    ContactService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }
    
    @Transactional(readOnly = true)
    public List<ContactResponseDto> getCompanyContacts(Long companyId) {
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new CompanyNotFoundException("Company does not exist."));

        return company.getInterviewers().stream()
            .map(contact -> new ContactResponseDto(
                contact.getId(),
                contact.getName(),
                contact.getEmail(),
                contact.getPhoneNumber()
            )).collect(Collectors.toList());
    }
}
