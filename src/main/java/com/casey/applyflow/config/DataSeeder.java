package com.casey.applyflow.config;

import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.casey.applyflow.domain.Application;
import com.casey.applyflow.domain.Company;
import com.casey.applyflow.domain.Interview;
import com.casey.applyflow.domain.User;
import com.casey.applyflow.domain.enums.Status;
import com.casey.applyflow.repository.ApplicationRepository;
import com.casey.applyflow.repository.CompanyRepository;
import com.casey.applyflow.repository.InterviewRepository;
import com.casey.applyflow.repository.UserRepository;

@Configuration
public class DataSeeder {
    
    @Bean
    CommandLineRunner initDatabase(
            UserRepository userRepo,
            CompanyRepository companyRepo, 
            InterviewRepository interviewRepo,
            ApplicationRepository applicationRepo) {
        return args -> {
            // Only seed if database is empty
            if (userRepo.count() == 0) {
                
                // Create test user
                User testUser = new User("Test User", "test@example.com", "password123");
                testUser = userRepo.save(testUser);
                
                // Create companies
                Company company1 = new Company("Example Corp", "San Francisco, CA", 4.5);
                company1 = companyRepo.save(company1);
                
                Company company2 = new Company("Tech Solutions Inc", "New York, NY", 4.2);
                company2 = companyRepo.save(company2);
                
                // Create interviews
                Interview interview1 = new Interview(
                    LocalDateTime.of(2026, 2, 15, 10, 0), 
                    "PHONE", 
                    null
                );
                interview1 = interviewRepo.save(interview1);
                
                Interview interview2 = new Interview(
                    LocalDateTime.of(2026, 2, 20, 14, 30), 
                    "TECHNICAL", 
                    null
                );
                interview2 = interviewRepo.save(interview2);
                
                // Create applications
                Application app1 = new Application(
                    "Software Engineer",
                    "https://www.example.com/jobs/123",
                    company1,
                    interview1,
                    Status.INTERVIEWING
                );
                app1.setUser(testUser);
                applicationRepo.save(app1);
                
                Application app2 = new Application(
                    "Senior Developer",
                    "https://www.techsolutions.com/careers/456",
                    company2,
                    interview2,
                    Status.APPLIED
                );
                app2.setUser(testUser);
                applicationRepo.save(app2);
                
                Application app3 = new Application(
                    "Full Stack Developer",
                    "https://www.example.com/jobs/789",
                    company1,
                    null,
                    Status.INTERESTED
                );
                app3.setUser(testUser);
                applicationRepo.save(app3);
                
                System.out.println("✅ Database seeded with test data!");
                System.out.println("   - 1 user created");
                System.out.println("   - 2 companies created");
                System.out.println("   - 2 interviews created");
                System.out.println("   - 3 applications created");
            }
        };
    }
}
