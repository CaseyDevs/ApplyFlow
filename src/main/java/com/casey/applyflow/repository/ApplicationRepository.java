package com.casey.applyflow.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.casey.applyflow.domain.Application;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    Optional<Application> findApplicationByTitle(String applicationTitle);
}
