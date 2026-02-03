package com.casey.applyflow.service;

import org.springframework.stereotype.Service;

import com.casey.applyflow.repository.CompanyRepository;

@Service
public class CompnayService {
    private final CompanyRepository companyRepository;

    CompnayService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }
}
