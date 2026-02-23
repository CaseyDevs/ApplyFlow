package com.casey.applyflow.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.casey.applyflow.domain.JobBoard;

public interface JobBoardRepository extends JpaRepository<JobBoard, Long> {
    Optional<List<JobBoard>> findAllByUserId(Long userId);
    Optional<JobBoard> findByIdAndUserId(Long jobBoardId, Long userId);
}
