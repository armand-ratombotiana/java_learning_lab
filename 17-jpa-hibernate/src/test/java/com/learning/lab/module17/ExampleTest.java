package com.learning.lab.module17;

import jakarta.persistence.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DataJpaTest
class ExampleTest {

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setName("John Doe");
        testUser.setEmail("john@example.com");
    }

    @Test
    @DisplayName("Test JPA entity creation and persistence")
    void testEntityPersistence() {
        User persisted = entityManager.persist(testUser);
        entityManager.flush();

        assertNotNull(persisted.getId(), "Persisted entity should have generated ID");
    }

    @Test
    @DisplayName("Test findById returns user when exists")
    void testFindByIdFound() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> result = userRepository.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Test User", result.get().getName());
    }

    @Test
    @DisplayName("Test findById returns empty when not found")
    void testFindByIdNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<User> result = userRepository.findById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Test findAll returns all users")
    void testFindAll() {
        List<User> users = Arrays.asList(
            createUser("User 1", "user1@example.com"),
            createUser("User 2", "user2@example.com"),
            createUser("User 3", "user3@example.com")
        );

        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userRepository.findAll();

        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("Test save creates new entity")
    void testSaveNewEntity() {
        User newUser = new User();
        newUser.setName("New User");
        newUser.setEmail("new@example.com");

        when(userRepository.save(newUser)).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        User saved = userRepository.save(newUser);

        assertNotNull(saved.getId());
        assertEquals("New User", saved.getName());
    }

    @Test
    @DisplayName("Test save updates existing entity")
    void testSaveExistingEntity() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setName("Old Name");

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("New Name");

        when(userRepository.save(updatedUser)).thenReturn(updatedUser);

        User result = userRepository.save(updatedUser);

        assertEquals("New Name", result.getName());
    }

    @Test
    @DisplayName("Test deleteById removes entity")
    void testDeleteById() {
        doNothing().when(userRepository).deleteById(1L);

        userRepository.deleteById(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Test findByEmail queries work correctly")
    void testFindByEmail() {
        User user = createUser("Test", "test@example.com");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        Optional<User> result = userRepository.findByEmail("test@example.com");

        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
    }

    @Test
    @DisplayName("Test entity relationships are mapped correctly")
    void testEntityRelationships() {
        Department dept = new Department();
        dept.setName("Engineering");

        User user = new User();
        user.setName("John");
        user.setDepartment(dept);

        assertNotNull(user.getDepartment());
        assertEquals("Engineering", user.getDepartment().getName());
    }

    @Test
    @DisplayName("Test @PrePersist callback sets timestamp")
    void testPrePersistCallback() {
        User user = new User();
        user.setName("Test User");

        entityManager.persist(user);
        entityManager.flush();

        assertNotNull(user.getCreatedAt());
    }

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    @Entity
    @Table(name = "users")
    static class User {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false, length = 100)
        private String name;

        @Column(unique = true)
        private String email;

        @Column(name = "created_at")
        private LocalDateTime createdAt;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "department_id")
        private Department department;

        @PrePersist
        void onCreate() {
            createdAt = LocalDateTime.now();
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public Department getDepartment() { return department; }
        public void setDepartment(Department department) { this.department = department; }
    }

    @Entity
    @Table(name = "departments")
    static class Department {
        @Id
        @GeneratedValue
        private Long id;
        private String name;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    interface UserRepository extends JpaRepository<User, Long> {
        Optional<User> findByEmail(String email);
    }
}