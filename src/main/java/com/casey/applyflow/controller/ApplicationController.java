package com.casey.applyflow.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.casey.applyflow.dto.ApplicationRequestDto;
import com.casey.applyflow.dto.ApplicationResponseDto;
import com.casey.applyflow.dto.UpdateApplicationFieldRequestDto;
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
    public ResponseEntity<List<ApplicationResponseDto>> getApplications(
        @RequestParam(required = false) String companyName,
        @RequestParam(required = false) Long companyId,
        @RequestParam(required = false) Boolean hasInterview
    ) {

        return ResponseEntity.ok(applicationService.getAllApplications(
            companyName,
            companyId,
            hasInterview
        ));
    }

    @GetMapping("/applications/{title}")
    public ResponseEntity<ApplicationResponseDto> getApplicationById(
        @PathVariable Long id
    ) {

        return ResponseEntity.ok(applicationService.getApplicationById(id));
    }

    @PostMapping("/applications")
    public ResponseEntity<ApplicationResponseDto> addApplication(
            @Valid @RequestBody ApplicationRequestDto request
        ) {

        ApplicationResponseDto response = applicationService.createApplication(request);

        String uri = "/api/applications/" + request.title().toLowerCase().replaceAll(" ", "-");

        return ResponseEntity.created(URI.create(uri)).body(response); 

    }

    @PutMapping("applications/{id}")
    public ResponseEntity<ApplicationResponseDto> updateApplication(
        @PathVariable Long id, 
        @RequestBody ApplicationRequestDto request
    ) {

        ApplicationResponseDto response = applicationService.updateApplication(id, request);

        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("applications/{id}")
    public ResponseEntity<ApplicationResponseDto> updateApplicationField(
        @PathVariable Long id, 
        @RequestBody UpdateApplicationFieldRequestDto request
    ) {

        ApplicationResponseDto response = applicationService.updateApplicationField(id, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("applications/{id}")
    public ResponseEntity<ApplicationResponseDto> removeApplication(
        @PathVariable Long id
    ) {
        ApplicationResponseDto response = applicationService.removeApplication(id);
        return ResponseEntity.ok(response);
    }
}
