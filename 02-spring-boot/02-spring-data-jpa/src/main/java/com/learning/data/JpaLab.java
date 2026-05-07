package com.learning.data;

import jakarta.persistence.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@SpringBootApplication
public class JpaLab {
    public static void main(String[] args) {
        SpringApplication.run(JpaLab.class, args);
    }
}

@Entity
class User {
    @Id @GeneratedValue
    private Long id;
    private String name;
    private String email;

    public User() {}
    public User(String name, String email) {
        this.name = name; this.email = email;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}

interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByNameContaining(String name);
}

@RestController
class UserController {
    private final UserRepository repository;

    UserController(UserRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/users")
    List<User> all() { return repository.findAll(); }

    @PostMapping("/users")
    User create(@RequestBody User user) {
        return repository.save(user);
    }

    @GetMapping("/users/{id}")
    Optional<User> get(@PathVariable Long id) {
        return repository.findById(id);
    }
}