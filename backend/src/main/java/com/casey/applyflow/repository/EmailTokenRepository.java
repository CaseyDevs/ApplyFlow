package com.casey.applyflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.casey.applyflow.domain.EmailVerificationToken;

public interface EmailTokenRepository extends JpaRepository<EmailVerificationToken, Long> {}
