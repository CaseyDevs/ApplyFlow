package com.casey.applyflow.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.casey.applyflow.domain.Company;
import com.casey.applyflow.domain.Contact;
import com.casey.applyflow.dto.ContactRequestDto;
import com.casey.applyflow.dto.ContactResponseDto;
import com.casey.applyflow.exception.CompanyNotFoundException;
import com.casey.applyflow.repository.CompanyRepository;
import com.casey.applyflow.repository.ContactRepository;

@Service
public class ContactService {
    private final CompanyRepository companyRepository;
    private final ContactRepository contactRepository;

    ContactService(CompanyRepository companyRepository, ContactRepository contactRepository) {
        this.companyRepository = companyRepository;
        this.contactRepository = contactRepository;
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

    @Transactional
    public ContactResponseDto createContact(ContactRequestDto request) {
        Company company = companyRepository.findById(request.companyId())
            .orElseThrow(() -> new CompanyNotFoundException("Company does not exist."));
        
        Contact contact = new Contact(
            request.name(), request.email(), request.phoneNumber(), company
        );

        Contact savedContact = contactRepository.save(contact);
        company.addInterviewer(savedContact);

        

        return new ContactResponseDto(
            savedContact.getId(),
            savedContact.getName(),
            savedContact.getEmail(),
            savedContact.getPhoneNumber()
        );
    }
}
