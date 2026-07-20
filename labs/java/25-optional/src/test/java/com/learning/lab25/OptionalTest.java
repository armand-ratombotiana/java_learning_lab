package com.learning.lab25;

import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class OptionalTest {

    @Test
    @DisplayName("Optional.of creates non-empty optional")
    void ofCreatesNonEmpty() {
        Optional<String> opt = Optional.of("value");
        assertTrue(opt.isPresent());
        assertEquals("value", opt.get());
    }

    @Test
    @DisplayName("Optional.ofNullable handles null")
    void ofNullableHandlesNull() {
        Optional<String> opt = Optional.ofNullable(null);
        assertTrue(opt.isEmpty());
    }

    @Test
    @DisplayName("Optional.empty creates empty optional")
    void emptyCreatesEmpty() {
        Optional<String> opt = Optional.empty();
        assertTrue(opt.isEmpty());
    }

    @Test
    @DisplayName("Optional.map transforms value")
    void mapTransforms() {
        Optional<String> opt = Optional.of("  hello  ");
        Optional<String> mapped = opt.map(String::trim).map(String::toUpperCase);
        assertEquals("HELLO", mapped.get());
    }

    @Test
    @DisplayName("Optional.filter retains matching values")
    void filterRetainsMatching() {
        Optional<String> opt = Optional.of("Java");
        assertTrue(opt.filter(s -> s.startsWith("J")).isPresent());
        assertTrue(opt.filter(s -> s.startsWith("X")).isEmpty());
    }

    @Test
    @DisplayName("Optional.orElse returns default when empty")
    void orElseDefault() {
        Optional<String> empty = Optional.empty();
        assertEquals("default", empty.orElse("default"));
    }

    @Test
    @DisplayName("Optional.orElseGet lazy evaluation")
    void orElseGetLazy() {
        Optional<String> empty = Optional.empty();
        assertEquals("generated", empty.orElseGet(() -> "generated"));
    }

    @Test
    @DisplayName("Optional.orElseThrow throws on empty")
    void orElseThrowThrows() {
        Optional<String> empty = Optional.empty();
        assertThrows(NoSuchElementException.class, () -> empty.orElseThrow());
    }

    @Test
    @DisplayName("Optional.ifPresentOrElse handles both cases")
    void ifPresentOrElseHandlesBoth() {
        var result = new StringBuilder();
        Optional<String> present = Optional.of("value");
        present.ifPresentOrElse(
            v -> result.append("present:").append(v),
            () -> result.append("absent")
        );
        assertEquals("present:value", result.toString());
    }

    @Test
    @DisplayName("Optional.flatMap chains Optional-returning functions")
    void flatMapChains() {
        Optional<String> opt = Optional.of("42");
        Optional<Integer> parsed = opt.flatMap(s -> {
            try { return Optional.of(Integer.parseInt(s)); }
            catch (NumberFormatException e) { return Optional.empty(); }
        });
        assertEquals(42, parsed.get().intValue());
    }

    @Test
    @DisplayName("Optional.flatMap returns empty for invalid input")
    void flatMapInvalid() {
        Optional<String> opt = Optional.of("not-a-number");
        Optional<Integer> parsed = opt.flatMap(s -> {
            try { return Optional.of(Integer.parseInt(s)); }
            catch (NumberFormatException e) { return Optional.empty(); }
        });
        assertTrue(parsed.isEmpty());
    }

    @Test
    @DisplayName("OptionalBestPractices findUser returns correct values")
    void findUserReturns() {
        assertTrue(OptionalBestPracticesExample.findUser(1).isPresent());
        assertTrue(OptionalBestPracticesExample.findUser(999).isEmpty());
    }

    @Test
    @DisplayName("OptionalCreationExample.getValue returns value or null")
    void getValueReturns() {
        assertNotNull(OptionalCreationExample.getValue(true));
        assertNull(OptionalCreationExample.getValue(false));
    }
}
