package com.casey.applyflow.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.casey.applyflow.domain.Application;
import com.casey.applyflow.domain.User;

public interface ApplicationRepository extends JpaRepository<Application, Long>, JpaSpecificationExecutor<Application> {
    List<Application> findByUser(User user);
    List<Application> findByUserId(Long userId);

    Optional<Application> findByIdAndUserId(Long applicationId, Long userId);
    boolean existsByIdAndUserId(Long applicationId, Long userId);

    Page<Application> findAllByJobBoardId(Long jobBoardId, Pageable pageable);
}
