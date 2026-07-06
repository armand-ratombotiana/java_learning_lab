package com.javaacademy.lab31.testing;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(long id);
    User save(User user);
    boolean updateEmail(long id, String email);
    boolean deleteById(long id);
    long countActive();
    boolean existsByEmail(String email);
}
