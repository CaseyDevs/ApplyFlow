package com.casey.applyflow.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.casey.applyflow.dto.InterviewResponseDto;
import com.casey.applyflow.repository.InterviewRepository;

import jakarta.persistence.EntityNotFoundException;

import com.casey.applyflow.domain.Interview;

@Service
public class InterviewService {
    private InterviewRepository interviewRepository;
    private Logger log = LoggerFactory.getLogger(InterviewService.class);

    InterviewService(InterviewRepository interviewRepository) {
        this.interviewRepository = interviewRepository;
    }


    @Transactional(readOnly = true)
    public InterviewResponseDto getInterview(Long interviewId) {
        Interview interview = interviewRepository.findById(interviewId)
            .orElseThrow(() -> new EntityNotFoundException());

        log.debug("Fetching interview");

        // return response dto
        return new InterviewResponseDto(
            interview.getDate(),
            interview.getType(),
            interview.getInterviewer()
        );

    }
}
