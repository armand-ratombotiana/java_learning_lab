package com.learning.backend02;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory service layer for User management.
 *
 * @Service marks this as a Spring-managed bean. In production this would
 * delegate to a JPA repository, but for demonstration we use a concurrent map.
 */
@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public UserService() {
        // Pre-populate with a sample user
        save(new User(null, "Alice", "alice@example.com"));
    }

    public java.util.List<User> findAll() {
        log.info("Fetching all users");
        return java.util.List.copyOf(users.values());
    }

    public User findById(Long id) {
        log.info("Fetching user with id={}", id);
        User user = users.get(id);
        if (user == null) {
            throw new UserNotFoundException("User not found with id: " + id);
        }
        return user;
    }

    public User save(User user) {
        if (user.getId() == null) {
            user.setId(idGenerator.getAndIncrement());
        }
        users.put(user.getId(), user);
        log.info("Saved user: {}", user);
        return user;
    }

    public User update(Long id, User updated) {
        if (!users.containsKey(id)) {
            throw new UserNotFoundException("Cannot update. User not found with id: " + id);
        }
        updated.setId(id);
        users.put(id, updated);
        log.info("Updated user: {}", updated);
        return updated;
    }

    public void delete(Long id) {
        if (users.remove(id) == null) {
            throw new UserNotFoundException("Cannot delete. User not found with id: " + id);
        }
        log.info("Deleted user with id={}", id);
    }
}
