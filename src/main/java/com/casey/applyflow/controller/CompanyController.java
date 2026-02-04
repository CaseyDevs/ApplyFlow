package com.casey.applyflow.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.casey.applyflow.dto.CompanyResponseDto;
import com.casey.applyflow.service.CompanyService;

import org.apache.catalina.connector.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



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
    public ResponseEntity<CompanyResponseDto> createCompany() {
        
        
        return entity;
    }
    
    
}
