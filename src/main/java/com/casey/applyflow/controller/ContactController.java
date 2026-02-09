package com.casey.applyflow.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.casey.applyflow.dto.ContactResponseDto;
import com.casey.applyflow.service.ContactService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;




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
    
}
