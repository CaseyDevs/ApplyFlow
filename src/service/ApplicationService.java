import java.io.ObjectInputFilter.Status;

import org.springframework.stereotype.Service;

import com.casey.applyflow.domain.User;
import com.casey.applyflow.dto.ApplicationRequestDto;
import com.casey.applyflow.repository.ApplicationRepository;
import com.casey.applyflow.repository.UserRepository;

@Service
public class ApplicationService {

    private ApplicationRepository applicationRepository;
    private UserRepository userRepository;

    ApplicationService(ApplicationRepository applicationRepository, UserRepository userRepository) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
    }
    
    // Get Application (Get)
    
    // Get Application by...

    // Create Application (Post)
    public void createApplication(String title, String url, Long companyId, Long interviewId, Status status) {
        Application application = new Application(title, url, companyId, interviewId, status);
        User user = new User("Test", "test@example.com", "password");

        user.addApplication(application);
        application.save(application);
    }

    // Update Applicaiton (Put)

    // Update Apllication (Patch)
}
