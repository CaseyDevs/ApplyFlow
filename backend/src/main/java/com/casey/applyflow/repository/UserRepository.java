package com.casey.applyflow.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.casey.applyflow.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
} 