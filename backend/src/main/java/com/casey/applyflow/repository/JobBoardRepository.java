package com.casey.applyflow.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.casey.applyflow.domain.JobBoard;

public interface JobBoardRepository extends JpaRepository<JobBoard, Long> {
    Page<JobBoard> findAllByUserId(Long userId, Pageable pageable);
    Optional<JobBoard> findByIdAndUserId(Long jobBoardId, Long userId);

    // Find all job boards where the user is a member
    Page<JobBoard> findAllByMembersUserId(Long userId, Pageable pageable);
}
