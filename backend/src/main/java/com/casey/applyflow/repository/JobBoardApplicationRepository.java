package com.casey.applyflow.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.casey.applyflow.model.JobBoardApplication;

public interface JobBoardApplicationRepository extends JpaRepository<JobBoardApplication, Long> {
    List<JobBoardApplication> findAllByJobBoardId(Long jobBoardId);
    
    JobBoardApplication findByIdAndJobBoardId(Long id, Long jobBoardId);
}
