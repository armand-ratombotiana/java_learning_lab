package com.javaacademy.lab31.testing;

import java.util.Optional;

public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final NotificationService notificationService;

    public UserServiceImpl(UserRepository repository, NotificationService notificationService) {
        this.repository = repository;
        this.notificationService = notificationService;
    }

    @Override
    public Optional<User> findById(long id) {
        return repository.findById(id);
    }

    @Override
    public User createUser(String email, String name) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email must not be blank");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name must not be blank");
        }
        if (!isEmailAvailable(email)) {
            throw new IllegalStateException("Email already taken: " + email);
        }
        User user = new User(System.nanoTime(), email, name);
        return repository.save(user);
    }

    @Override
    public boolean updateEmail(long userId, String newEmail) {
        if (newEmail == null || newEmail.isBlank()) {
            throw new IllegalArgumentException("Email must not be blank");
        }
        return repository.updateEmail(userId, newEmail);
    }

    @Override
    public void deleteUser(long id) {
        repository.deleteById(id);
    }

    @Override
    public long countActiveUsers() {
        return repository.countActive();
    }

    @Override
    public boolean isEmailAvailable(String email) {
        return !repository.existsByEmail(email);
    }

    @Override
    public void notifyUser(long userId, String message) {
        Optional<User> user = repository.findById(userId);
        if (user.isPresent()) {
            notificationService.send(user.get().getEmail(), message);
        }
    }
}
