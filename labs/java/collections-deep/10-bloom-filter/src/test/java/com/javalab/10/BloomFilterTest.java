package com.javalab.10;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BloomFilter")
class BloomFilterTest {

    private BloomFilter<String> filter;

    @BeforeEach
    void setUp() {
        filter = new BloomFilter<>(100, 0.01);
    }

    @Test
    @DisplayName("new filter has zero insertions")
    void newFilterHasZeroInsertions() {
        assertEquals(0, filter.insertions());
    }

    @Test
    @DisplayName("mightContain returns false for absent element")
    void absentElement() {
        assertFalse(filter.mightContain("absent"));
    }

    @Test
    @DisplayName("added element is reported as present")
    void addedElement() {
        filter.add("hello");
        assertTrue(filter.mightContain("hello"));
    }

    @Test
    @DisplayName("all added elements are found")
    void allAddedElements() {
        String[] items = {"apple", "banana", "cherry", "date", "elderberry"};
        for (String item : items) {
            filter.add(item);
        }
        for (String item : items) {
            assertTrue(filter.mightContain(item));
        }
        assertEquals(items.length, filter.insertions());
    }

    @Test
    @DisplayName("false positive rate is estimated")
    void falsePositiveEstimate() {
        BloomFilter<Integer> intFilter = new BloomFilter<>(1000, 0.05);
        for (int i = 0; i < 500; i++) {
            intFilter.add(i);
        }
        double fpr = intFilter.estimatedFalsePositiveRate();
        assertTrue(fpr >= 0.0 && fpr < 0.5);
    }

    @Test
    @DisplayName("clear resets filter")
    void clear() {
        filter.add("test");
        filter.clear();
        assertEquals(0, filter.insertions());
        assertFalse(filter.mightContain("test"));
    }

    @Test
    @DisplayName("null element throws NPE")
    void nullElement() {
        assertThrows(NullPointerException.class, () -> filter.add(null));
        assertThrows(NullPointerException.class, () -> filter.mightContain(null));
    }

    @Test
    @DisplayName("handles many insertions")
    void manyInsertions() {
        for (int i = 0; i < 500; i++) {
            filter.add("key-" + i);
        }
        assertEquals(500, filter.insertions());
        for (int i = 0; i < 500; i++) {
            assertTrue(filter.mightContain("key-" + i));
        }
    }

    @Test
    @DisplayName("bitSize and numHashFunctions are positive")
    void parameters() {
        assertTrue(filter.bitSize() > 0);
        assertTrue(filter.numHashFunctions() > 0);
    }

    @Test
    @DisplayName("invalid parameters throw")
    void invalidParameters() {
        assertThrows(IllegalArgumentException.class,
                () -> new BloomFilter<>(0, 0.01));
        assertThrows(IllegalArgumentException.class,
                () -> new BloomFilter<>(100, 0.0));
        assertThrows(IllegalArgumentException.class,
                () -> new BloomFilter<>(100, 1.0));
    }
}
