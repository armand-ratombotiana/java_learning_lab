package com.learning.backend17.service;

import com.learning.backend17.model.UserV2;
import com.learning.backend17.model.CreateUserRequest;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserServiceV2 {

    private static final Logger log = LoggerFactory.getLogger(UserServiceV2.class);
    private final Map<Long, UserV2> users = new ConcurrentHashMap<>();

    @PostConstruct
    void init() {
        users.put(1L, new UserV2(1L, "John", "Doe", "john@example.com",
            "+1-555-0100", LocalDateTime.now().minusDays(30), true));
        users.put(2L, new UserV2(2L, "Jane", "Smith", "jane@example.com",
            "+1-555-0101", LocalDateTime.now().minusDays(15), true));
        users.put(3L, new UserV2(3L, "Bob", "Wilson", "bob@example.com",
            "+1-555-0102", LocalDateTime.now().minusDays(7), false));
    }

    public List<UserV2> findAll() {
        return List.copyOf(users.values());
    }

    public UserV2 findById(Long id) {
        return users.get(id);
    }

    public UserV2 create(CreateUserRequest request) {
        long id = users.size() + 1L;
        var user = new UserV2(id, request.firstName(), request.lastName(),
            request.email(), request.phone(), LocalDateTime.now(), true);
        users.put(id, user);
        log.info("Created V2 user: {}", user);
        return user;
    }

    public UserV2 update(Long id, CreateUserRequest request) {
        var existing = users.get(id);
        if (existing == null) return null;
        var updated = new UserV2(id, request.firstName(), request.lastName(),
            request.email(), request.phone(), existing.createdAt(), existing.active());
        users.put(id, updated);
        return updated;
    }

    public boolean delete(Long id) {
        return users.remove(id) != null;
    }
}
