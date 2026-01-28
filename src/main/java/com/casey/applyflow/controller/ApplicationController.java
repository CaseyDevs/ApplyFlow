package com.casey.applyflow.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.casey.applyflow.dto.ApplicationRequestDto;
import com.casey.applyflow.dto.ApplicationResponseDto;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api")
public class ApplicationController {
    
    @GetMapping("/applications")
    public ResponseEntity<ApplicationResponseDto> getApplications(@RequestParam String param) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/applications")
    public ResponseEntity<ApplicationResponseDto> addApplication(@Valid @RequestBody ApplicationRequestDto requestDto) {
        //TODO: process POST request
        

        
        return ResponseEntity.ok().build();

    }

    @PutMapping("applications/{id}")
    public ResponseEntity<ApplicationResponseDto> updateApplication(@PathVariable String id, @RequestBody String entity) {
        //TODO: process PUT request
        
        return ResponseEntity.ok().build();

    }
    
    @PatchMapping("applications/{id}")
    public ResponseEntity<ApplicationResponseDto> updateApplicationField(@PathVariable String id, @RequestBody String entity) {
        //TODO: process PATCH request

        return ResponseEntity.ok().build();

    }
}
