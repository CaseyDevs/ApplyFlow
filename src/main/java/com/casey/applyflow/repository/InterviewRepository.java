package com.casey.applyflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.casey.applyflow.domain.Interview;
import java.util.List;
import java.time.LocalDateTime;

public interface InterviewRepository extends JpaRepository<Interview, Long> {
    List<Interview> findByDateBetween(LocalDateTime start, LocalDateTime end);
    List<Interview> findByDateAfter(LocalDateTime date);
    List<Interview> findByDateBefore(LocalDateTime date);
}
