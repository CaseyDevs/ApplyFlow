package com.casey.applyflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.casey.applyflow.domain.JobBoard;

public interface JobBoardRepository extends JpaRepository<JobBoard, Long> {}
