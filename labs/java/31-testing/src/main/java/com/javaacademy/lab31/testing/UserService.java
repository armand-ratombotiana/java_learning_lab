package com.javaacademy.lab31.testing;

import java.util.Optional;

public interface UserService {
    Optional<User> findById(long id);
    User createUser(String email, String name);
    boolean updateEmail(long userId, String newEmail);
    void deleteUser(long id);
    long countActiveUsers();
    boolean isEmailAvailable(String email);
    void notifyUser(long userId, String message);
}
