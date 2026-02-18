package com.casey.applyflow.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.casey.applyflow.dto.ContactRequestDto;
import com.casey.applyflow.dto.ContactResponseDto;
import com.casey.applyflow.service.ContactService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api")
public class ContactController {
    private final ContactService contactService;

    ContactController(ContactService contactService) {
        this.contactService = contactService;
    }
    
    @GetMapping("/companies/{companyId}/contacts")
    public ResponseEntity<List<ContactResponseDto>> getCompanyContacts(
        @Valid @PathVariable Long companyId
    ) {
        List<ContactResponseDto> response = contactService.getCompanyContacts(companyId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/contacts")
    public ResponseEntity<ContactResponseDto> createContact(
        @Valid @RequestBody ContactRequestDto request
    ) {
        ContactResponseDto response = contactService.createContact(request);

        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/companies/{companyId}/contacts/{id}")
    public ResponseEntity<ContactResponseDto> updateContact(
        @PathVariable @Min(1) Long companyId,
        @PathVariable @Min(1) Long id,
        @Valid @RequestBody ContactRequestDto request
    ) {
        ContactResponseDto response = contactService.updateContact(companyId, id, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/companies/{companyId}/contacts/{contactId}")
    public ResponseEntity<Void> deleteContact(
        @PathVariable Long companyId,
        @PathVariable Long contactId
    ) {
        contactService.deleteContact(companyId, contactId);

        return ResponseEntity.noContent().build();
    }
    
}
