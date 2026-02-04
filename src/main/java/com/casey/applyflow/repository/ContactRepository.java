package com.casey.applyflow.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.casey.applyflow.domain.Contact;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    Optional<Contact> findfindByName(String name);
}
