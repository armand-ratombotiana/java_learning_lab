package com.learning.lab25;

import java.util.*;

/**
 * Demonstrates flatMap() for chaining Optional-producing operations.
 */
public class OptionalFlatMapExample {

    public static void showOptionalFlatMap() {
        System.out.println("=== Optional flatMap ===");

        User user = new User("alice", "alice@example.com");

        String emailDomain = Optional.ofNullable(user)
            .flatMap(User::getEmail)
            .filter(e -> e.contains("@"))
            .map(e -> e.substring(e.indexOf("@") + 1))
            .orElse("Unknown domain");
        System.out.println("Email domain: " + emailDomain);

        User noEmailUser = new User("bob", null);
        String domain2 = Optional.ofNullable(noEmailUser)
            .flatMap(User::getEmail)
            .map(e -> e.substring(e.indexOf("@") + 1))
            .orElse("No email");
        System.out.println("Second user domain: " + domain2);

        Optional<Integer> result = Optional.of("42")
            .flatMap(s -> {
                try {
                    return Optional.of(Integer.parseInt(s));
                } catch (NumberFormatException e) {
                    return Optional.empty();
                }
            });
        System.out.println("Parsed number: " + result.orElse(-1));
    }
}

class User {
    private String username;
    private String email;

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public Optional<String> getEmail() {
        return Optional.ofNullable(email);
    }
}
