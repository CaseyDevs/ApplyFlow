package com.casey.applyflow;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.casey.applyflow.domain.Application;
import com.casey.applyflow.domain.User;
import com.casey.applyflow.domain.enums.Status;



public class DomainTests {
    @Test
    void userApplicationsAreIsolated() {
        Application application = new Application("Backend Developer", "www.fakeurl.com", Status.APPLIED);
        User user = new User("John Doe", "johndoe@example.com", "password");
        User user2 = new User("Doe John", "doejohn@example.com", "password");
        

        user.addApplication(application);

        assertTrue(user.getApplications().contains(application));
        assertFalse(user2.getApplications().contains(application));
    }
}
