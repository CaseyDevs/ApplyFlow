package com.casey.applyflow.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.casey.applyflow.model.JobBoardApplication;
import com.casey.applyflow.model.JobBoardApplicationStatus;

public interface JobBoardApplicationStatusRepository extends JpaRepository<JobBoardApplicationStatus, Long> {
    List<JobBoardApplicationStatus> findAllByJobBoardApplication(JobBoardApplication jobBoardApplication);

    Optional<JobBoardApplicationStatus> findByJobBoardApplicationIdAndUserId(
        Long jobBoardApplicationId,
        Long userId
    );
}