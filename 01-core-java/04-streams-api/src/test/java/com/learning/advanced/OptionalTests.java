package com.learning.advanced;

import org.junit.jupiter.api.Test;
import java.util.*;
import java.util.stream.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Comprehensive tests for Optional.
 */
public class OptionalTests {
    
    @Test
    void testOptionalOf() {
        Optional<String> opt = Optional.of("Hello");
        assertThat(opt).isPresent().contains("Hello");
    }
    
    @Test
    void testOptionalEmpty() {
        Optional<String> opt = Optional.empty();
        assertThat(opt).isEmpty();
    }
    
    @Test
    void testOptionalOfNullable() {
        Optional<String> opt1 = Optional.ofNullable("Hello");
        Optional<String> opt2 = Optional.ofNullable(null);
        assertThat(opt1).isPresent();
        assertThat(opt2).isEmpty();
    }
    
    @Test
    void testOptionalIfPresent() {
        List<String> values = new ArrayList<>();
        Optional.of("Test").ifPresent(values::add);
        assertThat(values).contains("Test");
    }
    
    @Test
    void testOptionalOrElse() {
        String result = Optional.<String>empty().orElse("Default");
        assertThat(result).isEqualTo("Default");
    }
    
    @Test
    void testOptionalOrElseGet() {
        String result = Optional.<String>empty().orElseGet(() -> "Generated");
        assertThat(result).isEqualTo("Generated");
    }
    
    @Test
    void testOptionalMap() {
        Optional<Integer> length = Optional.of("Hello")
            .map(String::length);
        assertThat(length).contains(5);
    }
    
    @Test
    void testOptionalFlatMap() {
        Optional<String> result = Optional.of("test")
            .flatMap(s -> Optional.of(s.toUpperCase()));
        assertThat(result).contains("TEST");
    }
    
    @Test
    void testOptionalFilter() {
        Optional<Integer> result = Optional.of(10)
            .filter(n -> n > 5);
        assertThat(result).isPresent();
    }
    
    @Test
    void testOptionalFilterRejects() {
        Optional<Integer> result = Optional.of(3)
            .filter(n -> n > 5);
        assertThat(result).isEmpty();
    }
}
