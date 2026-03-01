package com.casey.applyflow.controller.v1;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.casey.applyflow.dto.JobBoardResponseDto;
import com.casey.applyflow.service.JobBoardService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1")
public class JobBoardController {
    private final JobBoardService jobBoardService;

    public JobBoardController(
        JobBoardService jobBoardService
    ) {
        this.jobBoardService = jobBoardService;
    }

    @GetMapping("/job-boards")
    public ResponseEntity<Page<JobBoardResponseDto>> getJobBoards(
        Pageable pageable
    ) {

        return ResponseEntity.ok(jobBoardService.getAllJobBoards(pageable));
    }    
}
