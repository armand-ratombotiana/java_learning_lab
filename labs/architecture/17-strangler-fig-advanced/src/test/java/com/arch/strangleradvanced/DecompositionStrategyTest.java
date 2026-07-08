package com.arch.strangleradvanced;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DecompositionStrategyTest {
    @Test
    void shouldFindMatchingUnit() {
        DecompositionStrategy ds = new DecompositionStrategy();
        ds.addUnit("orders", f -> f.contains("order"), 1, "commerce");
        var result = ds.findUnit("create-order");
        assertTrue(result.isPresent());
        assertEquals("orders", result.get().name());
    }

    @Test
    void shouldReturnEmptyForNoMatch() {
        DecompositionStrategy ds = new DecompositionStrategy();
        var result = ds.findUnit("unknown");
        assertTrue(result.isEmpty());
    }
}
