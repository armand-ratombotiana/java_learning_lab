package solution;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;

import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestingSolutionTest {

    @Test
    void testMockitoAnnotations() {
        TestingSolution.UserService mockService = mock(TestingSolution.UserService.class);
        TestingSolution.User mockUser = new TestingSolution.User(1L, "Test", "test@test.com");
        when(mockService.findById(1L)).thenReturn(java.util.Optional.of(mockUser));

        TestingSolution.UserController controller = new TestingSolution.UserController(mockService);
        TestingSolution.User result = controller.getUser(1L);

        assertEquals("Test", result.getName());
    }

    @Test
    void testVerifyInteractions() {
        TestingSolution.UserService mockService = mock(TestingSolution.UserService.class);
        when(mockService.findAll()).thenReturn(List.of(
            new TestingSolution.User(1L, "A", "a@test.com")
        ));

        TestingSolution.UserController controller = new TestingSolution.UserController(mockService);
        List<TestingSolution.User> users = controller.getAllUsers();

        assertEquals(1, users.size());
        verify(mockService).findAll();
    }

    @Test
    void testExceptionThrowing() {
        TestingSolution.UserService mockService = mock(TestingSolution.UserService.class);
        when(mockService.findById(99L)).thenReturn(java.util.Optional.empty());

        TestingSolution.UserController controller = new TestingSolution.UserController(mockService);
        assertThrows(RuntimeException.class, () -> controller.getUser(99L));
    }

    @Test
    void testArgumentCaptor() {
        TestingSolution.UserService mockService = mock(TestingSolution.UserService.class);
        when(mockService.save(any())).thenReturn(new TestingSolution.User(1L, "Saved", "saved@test.com"));

        TestingSolution.UserController controller = new TestingSolution.UserController(mockService);
        controller.createUser("NewUser", "new@test.com");

        ArgumentCaptor<TestingSolution.User> captor = ArgumentCaptor.forClass(TestingSolution.User.class);
        verify(mockService).save(captor.capture());
        assertEquals("NewUser", captor.getValue().getName());
    }

    @Test
    void testAsyncFuture() throws Exception {
        TestingSolution.UserService mockService = mock(TestingSolution.UserService.class);
        when(mockService.findByIdAsync(1L)).thenReturn(
            java.util.concurrent.CompletableFuture.completedFuture(
                new TestingSolution.User(1L, "Async", "async@test.com")
            )
        );

        java.util.concurrent.CompletableFuture<TestingSolution.User> future = mockService.findByIdAsync(1L);
        TestingSolution.User user = future.get();
        assertEquals("Async", user.getName());
    }

    @ParameterizedTest
    @ValueSource(strings = {"test@test.com", "user@domain.org"})
    void testParameterizedEmails(String email) {
        assertTrue(email.contains("@"));
    }
}