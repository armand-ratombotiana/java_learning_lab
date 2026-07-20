package com.learning.lab25;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

class OptionalUltraDeepTest {

    @Test
    void optionalStreamMethod() {
        var items = List.of(Optional.of("a"), Optional.empty(), Optional.of("b"));
        var result = items.stream().flatMap(Optional::stream).toList();
        assertEquals(List.of("a", "b"), result);
    }

    @Test
    void optionalOrElseVsOrElseGetSemantics() {
        Optional<String> present = Optional.of("real");
        // orElse evaluates even if value is present
        String result = present.orElse(computeExpensiveDefault());
        assertEquals("real", result);
    }

    private String computeExpensiveDefault() {
        return "expensive";
    }

    @Test
    void optionalOfNullThrows() {
        assertThrows(NullPointerException.class, () -> Optional.of(null));
    }

    @Test
    void optionalEqualsAndHashCode() {
        Optional<String> a = Optional.of("test");
        Optional<String> b = Optional.of("test");
        Optional<String> c = Optional.of("other");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
    }

    @Test
    void optionalEmptyEquals() {
        assertEquals(Optional.empty(), Optional.empty());
    }

    @Test
    void optionalMapOnEmptyReturnsEmpty() {
        Optional<String> empty = Optional.empty();
        assertTrue(empty.map(String::length).isEmpty());
    }

    @Test
    void optionalFilterOnEmptyReturnsEmpty() {
        Optional<String> empty = Optional.empty();
        assertTrue(empty.filter(s -> true).isEmpty());
    }

    @Test
    void optionalOrElseThrowCustomException() {
        Optional<String> empty = Optional.empty();
        assertThrows(IllegalArgumentException.class,
            () -> empty.orElseThrow(() -> new IllegalArgumentException("missing")));
    }

    @Test
    void optionalFlatMapChainingWithUser() {
        User user = new User("alice", "alice@example.com");
        String domain = Optional.ofNullable(user)
            .flatMap(User::getEmail)
            .map(e -> e.substring(e.indexOf("@") + 1))
            .orElse("unknown");
        assertEquals("example.com", domain);
    }

    @Test
    void optionalOfNullableWithNonNull() {
        assertTrue(Optional.ofNullable("nonnull").isPresent());
        assertTrue(Optional.ofNullable(null).isEmpty());
    }
}
