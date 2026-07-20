package com.learning.lab15;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

class FunctionalProgrammingUltraDeepTest {

    @Test
    void optionalChainingWithFlatMap() {
        UserOptional user = new UserOptional("alice", "alice@example.com");
        String domain = Optional.ofNullable(user)
            .flatMap(UserOptional::getEmail)
            .filter(e -> e.contains("@"))
            .map(e -> e.substring(e.indexOf("@") + 1))
            .orElse("Unknown");
        assertEquals("example.com", domain);
    }

    @Test
    void optionalEmptyFallback() {
        UserOptional noEmail = new UserOptional("bob", null);
        String domain = Optional.ofNullable(noEmail)
            .flatMap(UserOptional::getEmail)
            .map(e -> e.substring(e.indexOf("@") + 1))
            .orElse("No email");
        assertEquals("No email", domain);
    }

    @Test
    void optionalFilterMapChain() {
        Optional<String> opt = Optional.of("  Hello  ");
        String result = opt
            .map(String::trim)
            .map(String::toUpperCase)
            .filter(s -> s.startsWith("H"))
            .orElse("DEFAULT");
        assertEquals("HELLO", result);
    }

    @Test
    void pureFunctionImmutabilityPreserved() {
        List<Integer> original = List.of(1, 2, 3);
        List<Integer> doubled = PureFunctionExample.applyToEach(original, n -> n * 2);
        assertEquals(List.of(1, 2, 3), original);
        assertEquals(List.of(2, 4, 6), doubled);
    }

    @Test
    void functionCompositionWithMultipleSteps() {
        java.util.function.Function<Integer, Integer> add1 = n -> n + 1;
        java.util.function.Function<Integer, Integer> multiplyBy2 = n -> n * 2;
        java.util.function.Function<Integer, Integer> subtract3 = n -> n - 3;
        var pipeline = add1.andThen(multiplyBy2).andThen(subtract3);
        assertEquals(7, pipeline.apply(4));
    }
}

class UserOptional {
    private String username;
    private String email;
    public UserOptional(String username, String email) {
        this.username = username;
        this.email = email;
    }
    public Optional<String> getEmail() {
        return Optional.ofNullable(email);
    }
}
