package com.casey.applyflow.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.casey.applyflow.dto.ContactRequestDto;
import com.casey.applyflow.dto.ContactResponseDto;
import com.casey.applyflow.exception.CompanyNotFoundException;
import com.casey.applyflow.exception.ContactNotFoundException;
import com.casey.applyflow.exception.ContactNotInCompanyException;
import com.casey.applyflow.model.Company;
import com.casey.applyflow.model.Contact;
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
            .map(this::toContactResponseDto)
            .toList();
    }

    @Transactional
    public ContactResponseDto createContact(Long companyId, ContactRequestDto request) {
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new CompanyNotFoundException("Company does not exist."));
        
        Contact contact = new Contact(
            request.name(), 
            request.email(), 
            request.phoneNumber(), 
            company
        );

        Contact savedContact = contactRepository.save(contact);
        company.addInterviewer(savedContact);

        log.info("Contact created and saved to company {}", company.getName());

        return toContactResponseDto(savedContact);
    }

    @Transactional
    public ContactResponseDto updateContact(Long companyId, Long id, ContactRequestDto request) {
        Contact contact = contactRepository.findByIdAndCompanyId(id, companyId)
            .orElseThrow(() -> new ContactNotFoundException("Contact does not exist."));

        contact.setName(request.name());
        contact.setEmail(request.email());
        contact.setPhoneNumber(request.phoneNumber());

        log.info("Contact {} updated", contact.getName());

        return toContactResponseDto(contact);
    }

    private ContactResponseDto toContactResponseDto(Contact contact) {
        if (contact == null) {
            return null;
        }

        return new ContactResponseDto(
            contact.getId(),
            contact.getName(),
            contact.getEmail(),
            contact.getPhoneNumber()
        );
    }

    @Transactional
    public void deleteContact(Long companyId, Long contactId) {
        Contact contact = contactRepository.findByIdAndCompanyId(contactId, companyId)
            .orElseThrow(() -> new ContactNotInCompanyException("Contact does not exist in this company."));
        
        contact.getCompany().removeInterviewer(contact);
        contactRepository.delete(contact);

        log.info("Removed contact {}", contact.getName());
    }
}
