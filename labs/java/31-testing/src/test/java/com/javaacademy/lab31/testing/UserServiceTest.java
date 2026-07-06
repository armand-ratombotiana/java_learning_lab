package com.javaacademy.lab31.testing;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User(1L, "alice@example.com", "Alice");
    }

    @Test
    @DisplayName("Find user by ID returns user when found")
    void findByIdWhenFound() {
        when(repository.findById(1L)).thenReturn(Optional.of(testUser));
        Optional<User> result = userService.findById(1L);
        assertTrue(result.isPresent());
        assertEquals("Alice", result.get().getName());
        verify(repository).findById(1L);
    }

    @Test
    @DisplayName("Find user by ID returns empty when not found")
    void findByIdWhenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        Optional<User> result = userService.findById(99L);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Create user saves and returns new user")
    void createUserSuccess() {
        when(repository.existsByEmail("new@example.com")).thenReturn(false);
        when(repository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        User created = userService.createUser("new@example.com", "New User");
        assertNotNull(created);
        assertEquals("new@example.com", created.getEmail());
        verify(repository).save(any(User.class));
    }

    @Test
    @DisplayName("Create user with duplicate email throws exception")
    void createUserDuplicateEmail() {
        when(repository.existsByEmail("taken@example.com")).thenReturn(true);
        assertThrows(IllegalStateException.class,
            () -> userService.createUser("taken@example.com", "Dup"));
    }

    @Test
    @DisplayName("Create user with blank email throws exception")
    void createUserBlankEmail() {
        assertThrows(IllegalArgumentException.class,
            () -> userService.createUser("", "Nobody"));
    }

    @Test
    @DisplayName("Update email delegates to repository")
    void updateEmail() {
        when(repository.updateEmail(1L, "new@example.com")).thenReturn(true);
        assertTrue(userService.updateEmail(1L, "new@example.com"));
        verify(repository).updateEmail(1L, "new@example.com");
    }

    @Test
    @DisplayName("Delete user delegates to repository")
    void deleteUser() {
        userService.deleteUser(1L);
        verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("Count active users returns correct count")
    void countActiveUsers() {
        when(repository.countActive()).thenReturn(5L);
        assertEquals(5L, userService.countActiveUsers());
    }

    @Test
    @DisplayName("Is email available returns correct boolean")
    void isEmailAvailable() {
        when(repository.existsByEmail("free@example.com")).thenReturn(false);
        assertTrue(userService.isEmailAvailable("free@example.com"));
        when(repository.existsByEmail("taken@example.com")).thenReturn(true);
        assertFalse(userService.isEmailAvailable("taken@example.com"));
    }

    @Test
    @DisplayName("Notify user sends notification when user exists")
    void notifyUserExists() {
        when(repository.findById(1L)).thenReturn(Optional.of(testUser));
        userService.notifyUser(1L, "Welcome!");
        verify(notificationService).send("alice@example.com", "Welcome!");
    }

    @Test
    @DisplayName("Notify user does nothing when user does not exist")
    void notifyUserNotExists() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        userService.notifyUser(99L, "Hello");
        verify(notificationService, never()).send(anyString(), anyString());
    }

    @Test
    @DisplayName("Verify exact call count")
    void verifyCallCount() {
        when(repository.findById(1L)).thenReturn(Optional.of(testUser));
        userService.findById(1L);
        userService.findById(1L);
        verify(repository, times(2)).findById(1L);
    }
}
