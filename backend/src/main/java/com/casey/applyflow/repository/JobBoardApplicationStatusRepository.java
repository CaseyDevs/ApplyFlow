package com.casey.applyflow.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.casey.applyflow.model.JobBoardApplicationStatus;

public interface JobBoardApplicationStatusRepository extends JpaRepository<JobBoardApplicationStatus, Long> {
    Optional<List<JobBoardApplicationStatus>> findAllByJobBoardIdAndApplicationId(
        Long jobBoardId,
        Long applicationId
    );

    Optional<JobBoardApplicationStatus> findByJobBoardIdAndApplicationIdAndUserId(
        Long jobBoardId,
        Long applicationId,
        Long userId
    );
}
