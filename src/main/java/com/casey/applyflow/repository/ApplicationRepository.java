package com.casey.applyflow.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.casey.applyflow.domain.Application;
import com.casey.applyflow.domain.User;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    Optional<Application> findByTitle(String title);
    List<Application> findByUser(User user);
    List<Application> findByUserId(Long userId);
}
