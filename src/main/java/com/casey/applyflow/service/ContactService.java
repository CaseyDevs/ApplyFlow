package com.casey.applyflow.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.casey.applyflow.domain.Company;
import com.casey.applyflow.domain.Contact;
import com.casey.applyflow.dto.ContactRequestDto;
import com.casey.applyflow.dto.ContactResponseDto;
import com.casey.applyflow.exception.CompanyNotFoundException;
import com.casey.applyflow.exception.ContactNotFoundException;
import com.casey.applyflow.repository.ContactRepository;

import com.casey.applyflow.repository.CompanyRepository;

@Service
public class ContactService {
    private final CompanyRepository companyRepository;
    private final ContactRepository contactRepository;
    private final Logger log = LoggerFactory.getLogger(ContactService.class);

    ContactService(CompanyRepository companyRepository, ContactRepository contactRepository) {
        this.companyRepository = companyRepository;
        this.contactRepository = contactRepository;
    }
    
    @Transactional(readOnly = true)
    public List<ContactResponseDto> getCompanyContacts(Long companyId) {
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new CompanyNotFoundException("Company does not exist."));

        log.debug("Fetching company {} contacts", company.getName());

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

        log.info("Contact created and saved to company {}", company.getName());

        return new ContactResponseDto(
            savedContact.getId(),
            savedContact.getName(),
            savedContact.getEmail(),
            savedContact.getPhoneNumber()
        );
    }

    @Transactional
    public ContactResponseDto updateContact(Long id, ContactRequestDto request) {
        Contact contact = contactRepository.findById(id)
            .orElseThrow(() -> new ContactNotFoundException("Contact does not exist."));

        contact.setName(request.name());
        contact.setEmail(request.email());
        contact.setPhoneNumber(request.phoneNumber());

        log.info("Contact {} updated", contact.getName());

        return new ContactResponseDto(
            contact.getId(),
            contact.getName(),
            contact.getEmail(),
            contact.getPhoneNumber()
        );
        
    }

    @Transactional
    public void deleteContact(Long id, ContactRequestDto request) {
        Contact contact = contactRepository.findById(id)
            .orElseThrow(() -> new ContactNotFoundException("Contact does not exist."));
        
        Company company = companyRepository.findById(request.companyId())
            .orElseThrow(() -> new CompanyNotFoundException("Company does not exist."));
        
        // Ensure company has contact to prevent deletion of contacts in other companies
        if (company.getInterviewers().contains(contact)) {
            company.removeInterviewer(contact);
            contactRepository.delete(contact);

            log.info("Removed contact {}", contact.getName());
        } else {
            throw new Error("ILL ADD A CUSTOM EXCEPTION HERE");
        }
    }
}
