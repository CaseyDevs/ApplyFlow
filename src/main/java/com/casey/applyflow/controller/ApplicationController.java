package com.casey.applyflow.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.casey.applyflow.dto.ApplicationRequestDto;
import com.casey.applyflow.dto.ApplicationResponseDto;
import com.casey.applyflow.service.ApplicationService;

import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;


@RestController
@RequestMapping("/api")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }
    
    @GetMapping("/applications")
    public ResponseEntity<List<ApplicationResponseDto>> getApplications() {

        return ResponseEntity.ok(applicationService.getAllApplications());
    }


    @GetMapping("/applications/{title}")
    public ResponseEntity<ApplicationResponseDto> getApplicationByTitle(
        @PathVariable String title
    ) {

        return ResponseEntity.ok(applicationService.getApplicationByTitle(title));
    }

    @PostMapping("/applications")
    public ResponseEntity<ApplicationResponseDto> addApplication(
            @Valid @RequestBody ApplicationRequestDto request
        ) {

        // Pass data to service
        ApplicationResponseDto response = applicationService.createApplication(
            request.title(), 
            request.url(), 
            request.companyId(), 
            request.interviewId(), 
            request.status()
        );

        String uri = "/api/applications/" + request.title().toLowerCase().replaceAll(" ", "-");

        // Generate URI and return created application
        return ResponseEntity.created(URI.create(uri)).body(response); 

    }

    @PutMapping("applications/{id}")
    public ResponseEntity<ApplicationResponseDto> updateApplication(@PathVariable Long id, @RequestBody ApplicationRequestDto request) {

        ApplicationResponseDto response = applicationService.updateApplication(
            id, 
            request.title(),
            request.url(),
            request.companyId(),
            request.interviewId(),
            request.status()
        );

        // Return updated application data
        return ResponseEntity.ok().body(response);
    }
    
    @PatchMapping("applications/{id}")
    public ResponseEntity<ApplicationResponseDto> updateApplicationField(@PathVariable String id, @RequestBody String entity) {
        //TODO: process PATCH request

        return ResponseEntity.ok().build();

    }
}
