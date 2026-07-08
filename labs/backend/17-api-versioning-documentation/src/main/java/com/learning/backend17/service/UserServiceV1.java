package com.learning.backend17.service;

import com.learning.backend17.model.UserV1;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserServiceV1 {

    private static final Logger log = LoggerFactory.getLogger(UserServiceV1.class);
    private final Map<Long, UserV1> users = new ConcurrentHashMap<>();

    @PostConstruct
    void init() {
        users.put(1L, new UserV1(1L, "John Doe", "john@example.com"));
        users.put(2L, new UserV1(2L, "Jane Smith", "jane@example.com"));
        users.put(3L, new UserV1(3L, "Bob Wilson", "bob@example.com"));
    }

    public List<UserV1> findAll() {
        return List.copyOf(users.values());
    }

    public UserV1 findById(Long id) {
        return users.get(id);
    }

    public UserV1 save(UserV1 user) {
        long id = users.size() + 1L;
        var newUser = new UserV1(id, user.name(), user.email());
        users.put(id, newUser);
        log.info("Created V1 user: {}", newUser);
        return newUser;
    }

    public UserV1 update(Long id, UserV1 user) {
        var updated = new UserV1(id, user.name(), user.email());
        users.put(id, updated);
        return updated;
    }

    public boolean delete(Long id) {
        return users.remove(id) != null;
    }
}
