package com.casey;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.casey.applyflow.domain.JobBoard;
import com.casey.applyflow.domain.JobBoardMember;
import com.casey.applyflow.domain.User;
import com.casey.applyflow.domain.enums.Role;
import com.casey.applyflow.dto.JobBoardRequestDto;
import com.casey.applyflow.dto.JobBoardResponseDto;
import com.casey.applyflow.repository.ApplicationRepository;
import com.casey.applyflow.repository.JobBoardMemberRepository;
import com.casey.applyflow.repository.JobBoardRepository;
import com.casey.applyflow.repository.UserRepository;
import com.casey.applyflow.service.ApplicationService;
import com.casey.applyflow.service.CurrentUserProvider;
import com.casey.applyflow.service.JobBoardService;

/**
 * Unit tests for {@link JobBoardService}.
 *
 * These tests use Mockito to mock out the database repositories and the
 * CurrentUserProvider so we can test the service's business logic in isolation
 * — no Spring context or database required.
 */
@ExtendWith(MockitoExtension.class) // tells JUnit to activate Mockito annotations
class JobBoardServiceTests {

    // --- mocks: fake versions of the dependencies ---

    @Mock
    private JobBoardRepository jobBoardRepository;

    @Mock
    private JobBoardMemberRepository jobBoardMemberRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @Mock
    private ApplicationService applicationService;

    // The real service under test — Mockito injects the mocks above via the constructor
    @InjectMocks
    private JobBoardService jobBoardService;

    // A reusable fake user for the tests
    private User testUser;

    private JobBoard testJobBoard;
    private JobBoardMember testJobBoardMember;

    @BeforeEach
    void setUp() {
        testUser = new User("Casey", "casey@example.com", "hashed-password");
        testJobBoardMember = new JobBoardMember(testUser, null);
        testJobBoard = new JobBoard("Summer", testJobBoardMember, null);
    }

    // ==================== HAPPY-PATH TESTS ====================

    @Test
    void createJobBoard_WithValidTitle_ReturnsResponseWithCorrectTitle() {
        // arrange
        String title = "My Summer 2026 Job Hunt";
        JobBoardRequestDto request = new JobBoardRequestDto(title, null, null);

        // When anything asks for the current user, return our test user
        when(currentUserProvider.getCurrentUser()).thenReturn(testUser);

        when(jobBoardRepository.save(any(JobBoard.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // act
        JobBoardResponseDto response = jobBoardService.createJobBoard(request);

        // assert
        assertNotNull(response, "Response should not be null");
        assertEquals(title, response.title(), "The title in the response should match what we sent");
        assertNotNull(response.members(), "Members list should not be null");
        assertFalse(response.members().isEmpty(), "The creator should be in the members list");
        assertTrue(response.members().stream().anyMatch(m -> m.getRole() == Role.OWNER), "The creator should be the owner");

        // verify that the repository's save method was actually called once
        verify(jobBoardRepository, times(1)).save(any(JobBoard.class));
    }

    @Test
    void setMembers_WithValidInput_UpdatesMembersCorrectly() {
        // arrange
        JobBoardMember member1 = new JobBoardMember(new User("C", "G", "C"), null);
        JobBoardMember member2 = new JobBoardMember(new User("C", "G", "C"), null);

        // act
        testJobBoard.addMember(member1);
        testJobBoard.addMember(member2);
        jobBoardRepository.save(testJobBoard);

        // assert
        assertTrue(testJobBoard.getMembers().size() == 3);
    }

    // ==================== SAD-PATH TESTS ====================

    @Test
    void createJobBoard_WithNullTitle_ThrowsIllegalArgumentException() {
        // A null title should be rejected by the service's validateTitle() method
        JobBoardRequestDto request = new JobBoardRequestDto(null, null, null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> jobBoardService.createJobBoard(request)
        );

        assertEquals("Job board title cannot be empty", exception.getMessage());

        // The repository should never have been called because validation failed first
        verify(jobBoardRepository, never()).save(any());
    }

    @Test
    void createJobBoard_WithEmptyTitle_ThrowsIllegalArgumentException() {
        // empty/blank title should also be rejected
        JobBoardRequestDto request = new JobBoardRequestDto("   ", null, null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> jobBoardService.createJobBoard(request)
        );

        assertEquals("Job board title cannot be empty", exception.getMessage());
        verify(jobBoardRepository, never()).save(any());
    }
}
