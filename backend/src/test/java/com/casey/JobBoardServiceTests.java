package com.casey;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.casey.applyflow.dto.JobBoardRequestDto;
import com.casey.applyflow.dto.JobBoardResponseDto;
import com.casey.applyflow.model.JobBoard;
import com.casey.applyflow.model.JobBoardMember;
import com.casey.applyflow.model.User;
import com.casey.applyflow.model.enums.Role;
import com.casey.applyflow.repository.ApplicationRepository;
import com.casey.applyflow.repository.JobBoardMemberRepository;
import com.casey.applyflow.repository.JobBoardRepository;
import com.casey.applyflow.repository.UserRepository;
import com.casey.applyflow.service.ApplicationService;
import com.casey.applyflow.service.CurrentUserProvider;
import com.casey.applyflow.service.JobBoardMemberService;
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

    @InjectMocks
    private JobBoardMemberService jobBoardMemberService;

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
        assertTrue(response.members().stream().anyMatch(m -> m.role() == Role.OWNER), "The creator should be the owner");

        // verify that the repository's save method was actually called once
        verify(jobBoardRepository, times(1)).save(any(JobBoard.class));
    }

    @Test
    void setMembers_WithValidInput_UpdatesMembersCorrectly() {
        // arrange
        JobBoardMember member1 = new JobBoardMember(new User("C", "G", "C"), Role.MEMBER);

        // act
        testJobBoard.addMember(member1);

        // assert
        assertTrue(testJobBoard.getMembers().size() > 1);
        assertTrue(testJobBoard.getMembers().get(1).getRole() == Role.MEMBER);
    }

    @Test
    void setNewOwner_UpdatesMembersCorrectly() throws Exception {
        // arrange — set IDs via reflection since JPA @GeneratedValue won't run in unit tests
        setId(testUser, 100L);

        User newOwnerUser = new User("C", "G", "C");
        setId(newOwnerUser, 200L);

        JobBoardMember member1 = new JobBoardMember(newOwnerUser, Role.MEMBER);
        setId(member1, 10L);

        // The owner (testJobBoardMember) must already be in the board
        setId(testJobBoardMember, 20L);
        testJobBoardMember.setRole(Role.OWNER);
        testJobBoard.addMember(testJobBoardMember);
        testJobBoard.addMember(member1);

        // Mock the calls that setNewOwner makes internally
        when(currentUserProvider.getCurrentUser()).thenReturn(testUser);
        when(jobBoardRepository.findById(1L)).thenReturn(Optional.of(testJobBoard));
        when(jobBoardMemberRepository.findByJobBoardIdAndUserId(1L, 100L))
                .thenReturn(Optional.of(testJobBoardMember));
        when(jobBoardMemberRepository.findByIdAndJobBoardId(10L, 1L))
                .thenReturn(Optional.of(member1));
        when(jobBoardRepository.save(any(JobBoard.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // act
        jobBoardMemberService.setNewOwner(1L, 10L);

        // assert — the new member should now be OWNER, and the old owner should be MEMBER
        assertEquals(Role.OWNER, member1.getRole());
        assertEquals(Role.MEMBER, testJobBoardMember.getRole());
        verify(jobBoardRepository, times(1)).save(testJobBoard);
    }

    /** Helper to set the private `id` field on JPA entities in tests. */
    private void setId(Object entity, Long id) throws Exception {
        Field idField = entity.getClass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(entity, id);
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
