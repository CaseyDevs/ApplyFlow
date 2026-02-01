package com.casey.applyflow.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.casey.applyflow.domain.Application;
import com.casey.applyflow.domain.User;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByUser(User user);
    List<Application> findByUserId(Long userId);
}
