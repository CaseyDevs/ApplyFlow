package com.casey.applyflow.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.casey.applyflow.dto.CompanyRequestDto;
import com.casey.applyflow.dto.CompanyResponseDto;
import com.casey.applyflow.service.CompanyService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api")
public class CompanyController {
    private final CompanyService companyService;

    CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping("/companies")
    public ResponseEntity<Page<CompanyResponseDto>> getCompanies(
        Pageable pageable
    ) {

        return ResponseEntity.ok(companyService.getAllCompanies(pageable));
    }

    @PostMapping("/companies")
    public ResponseEntity<CompanyResponseDto> createCompany(
        @Valid @RequestBody CompanyRequestDto request
    ) {
        CompanyResponseDto response = companyService.createCompany(request);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/companies/{id}")
    public ResponseEntity<CompanyResponseDto> updateCompany(
        @PathVariable Long id, 
        @RequestBody CompanyRequestDto request
    ) {
        CompanyResponseDto response = companyService.updateCompany(id, request);
        
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/companies/{id}") 
    public ResponseEntity<Void> deleteCompany(
        @PathVariable Long id
    ) {

        companyService.deleteCompany(id);
        return ResponseEntity.noContent().build(); // 204 no content
    }
}
