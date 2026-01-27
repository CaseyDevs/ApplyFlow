package com.casey.applyflow;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Date;
import java.time.LocalDateTime;

import com.casey.applyflow.domain.Application;
import com.casey.applyflow.domain.Company;
import com.casey.applyflow.domain.Contact;
import com.casey.applyflow.domain.Interview;
import com.casey.applyflow.domain.User;
import com.casey.applyflow.domain.enums.Status;



public class DomainTests {
    @Test
    void userApplicationsAreIsolated() {
        Company company = new Company("Google", "London", 5.0);
        Contact contact = new Contact("John", "example@company.com", "+44 00000000000", company);
        Interview interview = new Interview(LocalDateTime.of(2007, 10, 2, 13, 45, 30, 123456789), "Se", contact);

        Application application = new Application("Backend Developer", "www.fakeurl.com", Status.APPLIED, company, interview);
        User user = new User("John Doe", "johndoe@example.com", "password");
        User user2 = new User("Doe John", "doejohn@example.com", "password");
        

        user.addApplication(application);

        assertTrue(user.getApplications().contains(application));
        assertFalse(user2.getApplications().contains(application));
    }
}
