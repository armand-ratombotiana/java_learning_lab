package com.javaacademy.lab40.bestpractices;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

class BestPracticesUltraDeepTest {

    @Test
    void immutableCollectionsProtectFromModification() {
        List<String> list = List.of("a", "b", "c");
        assertThrows(UnsupportedOperationException.class, () -> list.add("d"));
    }

    @Test
    void optionalPreventsNullPointer() {
        Map<String, String> map = new HashMap<>();
        String value = Optional.ofNullable(map.get("missing"))
            .orElse("default");
        assertEquals("default", value);
    }

    @Test
    void stringBuilderEfficientConcatenation() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            sb.append(i);
        }
        assertEquals(100, sb.length());
    }

    @Test
    void tryWithResourcesEnsuresCleanup() {
        var resource = new TestCloseable();
        try (resource) {
            assertFalse(resource.isClosed());
        }
        assertTrue(resource.isClosed());
    }

    @Test
    void defensiveCopiesPreventMutation() {
        List<String> internal = new ArrayList<>(List.of("original"));
        List<String> copy = new ArrayList<>(internal);
        copy.add("modified");
        assertEquals(1, internal.size());
        assertEquals(2, copy.size());
    }
}

class TestCloseable implements AutoCloseable {
    private boolean closed = false;
    public boolean isClosed() { return closed; }
    public void close() { closed = true; }
}
