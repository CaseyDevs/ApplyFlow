package com.casey.applyflow.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.casey.applyflow.dto.ContactRequestDto;
import com.casey.applyflow.dto.ContactResponseDto;
import com.casey.applyflow.service.ContactService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
    
    @GetMapping("/contact")
    public ResponseEntity<List<ContactResponseDto>> getCompanyContacts(
        @Valid @RequestParam Long companyId
    ) {
        List<ContactResponseDto> response = contactService.getCompanyContacts(companyId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/contact")
    public ResponseEntity<ContactResponseDto> createContact(
        @Valid @RequestBody ContactRequestDto request
    ) {
        ContactResponseDto response = contactService.createContact(request);

        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/contact/{id}")
    public ResponseEntity<ContactResponseDto> updateContact(
        @PathVariable Long id,
        @Valid @RequestBody ContactRequestDto request
    ) {
        ContactResponseDto response = contactService.updateContact(id, request);

        return ResponseEntity.ok(response);
    }
    
}
