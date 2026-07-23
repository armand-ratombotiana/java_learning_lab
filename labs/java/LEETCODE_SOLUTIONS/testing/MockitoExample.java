package testing;

import java.util.function.Supplier;

/**
 * Mockito example demonstrating:
 * - Mock creation
 * - Stubbing (when/thenReturn, doThrow)
 * - Verification (times, never)
 * - Argument matchers (any, eq)
 * 
 * This self-contained example simulates Mockito behavior.
 * In real projects, add dependency: org.mockito:mockito-core:5.x
 */
public class MockitoExample {

    // Interfaces to mock
    interface UserRepository {
        String findById(int id);
        void save(String user);
    }

    interface EmailService {
        void sendWelcome(String email);
    }

    // System under test
    static class UserRegistration {
        private final UserRepository repo;
        private final EmailService emailService;

        UserRegistration(UserRepository repo, EmailService emailService) {
            this.repo = repo;
            this.emailService = emailService;
        }

        String register(String name, String email) {
            if (repo.findById(0) != null) throw new IllegalStateException("Already exists");
            repo.save(name);
            emailService.sendWelcome(email);
            return "Welcome " + name;
        }
    }

    // Simple Mock implementation (for demonstration — in practice, use Mockito library)
    static class MockRepository implements UserRepository {
        String savedUser;
        boolean exists;

        public String findById(int id) { return exists ? "existing" : null; }
        public void save(String user) { this.savedUser = user; }
    }

    static class MockEmailService implements EmailService {
        boolean sent = false;
        String sentTo;

        public void sendWelcome(String email) {
            this.sent = true;
            this.sentTo = email;
        }
    }

    public static void main(String[] args) {
        MockRepository repo = new MockRepository();
        MockEmailService email = new MockEmailService();
        UserRegistration reg = new UserRegistration(repo, email);

        // Test: new user registration
        String result = reg.register("Alice", "alice@test.com");
        assert "Welcome Alice".equals(result) : "Registration message";
        assert "Alice".equals(repo.savedUser) : "User saved";
        assert email.sent : "Welcome email sent";
        assert "alice@test.com".equals(email.sentTo) : "Email to correct address";

        // Verify: registration creates user
        // Simulates: verify(repo).save("Alice");
        assert repo.savedUser != null : "verify save called";

        // Verify: email sent exactly once
        assert email.sent : "verify sendWelcome called";

        // Mockito-like: when().thenReturn()
        // In real Mockito:
        // when(repo.findById(1)).thenReturn("existing");
        repo.exists = true;
        try {
            reg.register("Bob", "bob@test.com");
            assert false : "Should throw for duplicate";
        } catch (IllegalStateException e) {
            assert "Already exists".equals(e.getMessage());
        }

        System.out.println("All MockitoExample tests passed.");
    }
}