package solution;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestingSolution {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    public static class User {
        private final Long id;
        private final String name;
        private final String email;

        public User(Long id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }

        public Long getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
    }

    public static class UserService {
        public Optional<User> findById(Long id) {
            return Optional.empty();
        }

        public List<User> findAll() {
            return List.of();
        }

        public User save(User user) {
            return user;
        }

        public void delete(Long id) {}

        public CompletableFuture<User> findByIdAsync(Long id) {
            return CompletableFuture.completedFuture(new User(1L, "Test", "test@test.com"));
        }
    }

    public static class UserController {
        private final UserService userService;

        public UserController(UserService userService) {
            this.userService = userService;
        }

        public User getUser(Long id) {
            return userService.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
        }

        public List<User> getAllUsers() {
            return userService.findAll();
        }

        public User createUser(String name, String email) {
            return userService.save(new User(null, name, email));
        }
    }

    // Unit test examples
    @Test
    void testFindUser() {
        User mockUser = new User(1L, "John", "john@test.com");
        when(userService.findById(1L)).thenReturn(Optional.of(mockUser));

        User result = userController.getUser(1L);
        assertEquals("John", result.getName());
        verify(userService).findById(1L);
    }

    @Test
    void testUserNotFound() {
        when(userService.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userController.getUser(1L));
    }

    @Test
    void testCreateUser() {
        User newUser = new User(null, "Jane", "jane@test.com");
        User savedUser = new User(1L, "Jane", "jane@test.com");
        when(userService.save(any(User.class))).thenReturn(savedUser);

        User result = userController.createUser("Jane", "jane@test.com");
        assertNotNull(result.getId());
        verify(userService).save(any(User.class));
    }

    @Test
    void testVerifyInteractions() {
        when(userService.findAll()).thenReturn(List.of(
            new User(1L, "A", "a@test.com"),
            new User(2L, "B", "b@test.com")
        ));

        List<User> users = userController.getAllUsers();
        assertEquals(2, users.size());
        verify(userService, times(1)).findAll();
        verify(userService, never()).delete(any());
    }

    @Test
    void testAsyncOperation() throws Exception {
        CompletableFuture<User> future = userService.findByIdAsync(1L);
        User user = future.get(5, TimeUnit.SECONDS);
        assertNotNull(user);
    }

    @ParameterizedTest
    @ValueSource(strings = {"test@test.com", "user@domain.com", "admin@company.org"})
    void testEmailValidation(String email) {
        assertTrue(email.contains("@"));
    }

    @Test
    void testExceptionHandling() {
        when(userService.findById(99L)).thenThrow(new RuntimeException("Database error"));
        assertThrows(RuntimeException.class, () -> userController.getUser(99L));
    }

    @Test
    void testVerifyVoidMethod() {
        userController.createUser("Test", "test@test.com");
        verify(userService).save(any(User.class));
    }

    @Test
    void testArgumentCaptor() {
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        userController.createUser("Captured", "captured@test.com");
        verify(userService).save(userCaptor.capture());
        assertEquals("Captured", userCaptor.getValue().getName());
    }

    @Test
    void testSpy() {
        UserService spyService = spy(new UserService());
        when(spyService.findAll()).thenReturn(List.of(new User(1L, "Spy", "spy@test.com")));

        List<User> users = spyService.findAll();
        assertEquals(1, users.size());
        verify(spyService).findAll();
    }
}