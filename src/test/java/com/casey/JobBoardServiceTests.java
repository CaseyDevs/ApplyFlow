package com.casey;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.casey.applyflow.ApplyFlowApplication;
import com.casey.applyflow.domain.User;
import com.casey.applyflow.dto.JobBoardRequestDto;
import com.casey.applyflow.dto.JobBoardResponseDto;
import com.casey.applyflow.repository.UserRepository;
import com.casey.applyflow.service.CurrentUserProvider;
import com.casey.applyflow.service.JobBoardService;

@SpringBootTest(classes = ApplyFlowApplication.class)
public class JobBoardServiceTests {

    @Autowired
    private JobBoardService jobBoardService;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private CurrentUserProvider currentUserProvider;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("John Doe", "john@example.com", "password123");
        testUser = userRepository.save(testUser);
        when(currentUserProvider.getCurrentUser()).thenReturn(testUser);
    }

    @Test
    @DisplayName("Should create a job board with a valid title")
    void testCreateJobBoardWithValidTitle() {
        // Arrange
        String validTitle = "My First Job Board";
        JobBoardRequestDto request = new JobBoardRequestDto(validTitle, null, null);

        // Act
        JobBoardResponseDto response = jobBoardService.createJobBoard(request);

        // Assert
        assertNotNull(response);
        assertEquals(validTitle, response.title());
        assertNotNull(response.ownerId());
        assertNotNull(response.id());
    }
}
