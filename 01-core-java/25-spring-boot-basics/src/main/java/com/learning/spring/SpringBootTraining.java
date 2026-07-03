package com.learning.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@SpringBootApplication
public class SpringBootTraining {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootTraining.class, args);
        System.out.println("\n=== Spring Boot Basics Training Application Started ===");
        System.out.println("Visit the following endpoints to explore Spring Boot concepts:");
        System.out.println(" - GET  http://localhost:8080/api/users");
        System.out.println(" - GET  http://localhost:8080/api/greeting");
        System.out.println(" - POST http://localhost:8080/api/users (with JSON body { \"name\": \"YourName\" })\n");
    }
}

// 1. Dependency Injection: Service Layer
@Service
class UserService {
    private final Map<Long, User> userDatabase = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public UserService() {
        // Pre-populate some data
        userDatabase.put(idGenerator.getAndIncrement(), new User(1L, "Alice"));
        userDatabase.put(idGenerator.getAndIncrement(), new User(2L, "Bob"));
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(userDatabase.values());
    }

    public Optional<User> getUserById(Long id) {
        return Optional.ofNullable(userDatabase.get(id));
    }

    public User createUser(String name) {
        Long newId = idGenerator.getAndIncrement();
        User newUser = new User(newId, name);
        userDatabase.put(newId, newUser);
        return newUser;
    }
}

// 2. Data Model with Validation
record User(Long id, String name) {}

record UserCreationRequest(@NotBlank(message = "Name cannot be blank") String name) {}

// 3. REST Controller showcasing HTTP methods and Configuration Injection
@RestController
@RequestMapping("/users")
class UserController {

    // Constructor Injection (Recommended practice)
    private final UserService userService;
    
    // Injecting external configuration from application.yml
    @Value("${app.greeting.message:Default Greeting}")
    private String greetingMessage;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // GET endpoint
    @GetMapping
    public List<User> getUsers() {
        return userService.getAllUsers();
    }

    // GET with path variable
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST endpoint with validation
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody UserCreationRequest request) {
        User createdUser = userService.createUser(request.name());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
}

// 4. Configuration and Environment showcase
@RestController
@RequestMapping("/greeting")
class GreetingController {

    private final String greetingMessage;
    private final boolean advancedMode;

    public GreetingController(
            @Value("${app.greeting.message:Hello!}") String greetingMessage,
            @Value("${app.feature-flags.advanced-mode:false}") boolean advancedMode) {
        this.greetingMessage = greetingMessage;
        this.advancedMode = advancedMode;
    }

    @GetMapping
    public Map<String, Object> getGreeting() {
        return Map.of(
            "message", greetingMessage,
            "advancedMode", advancedMode,
            "timestamp", new Date()
        );
    }
}