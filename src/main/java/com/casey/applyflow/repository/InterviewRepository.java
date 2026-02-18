package com.casey.applyflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.casey.applyflow.domain.Interview;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

public interface InterviewRepository extends JpaRepository<Interview, Long> {
    List<Interview> findByDateBetween(LocalDateTime start, LocalDateTime end);
    List<Interview> findByDateAfter(LocalDateTime date);
    List<Interview> findByDateBefore(LocalDateTime date);
    Optional<Interview> findByIdAndApplicationUserId(Long interviewId, Long userId);

    List<Interview> findAllByApplicationIdAndApplicationUserId(Long applicationId, Long userId);
    boolean existsByIdAndApplicationId(Long interviewId, Long applicationId);
}
