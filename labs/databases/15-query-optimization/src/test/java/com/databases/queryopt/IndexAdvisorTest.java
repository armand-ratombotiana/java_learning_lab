package com.databases.queryopt;

import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class IndexAdvisorTest {
    private IndexAdvisor advisor;
    @BeforeEach void setUp() { advisor = new IndexAdvisor(); }

    @Test void shouldRecommendIndexes() {
        advisor.analyzeTable("orders", 1000000, Map.of(
            "customer_id", new Object[]{false, 500000L, false},
            "status", new Object[]{false, 5L, false},
            "created_at", new Object[]{true, 100000L, false}
        ));
        var recs = advisor.recommendIndexes();
        assertTrue(recs.stream().anyMatch(r -> r.contains("customer_id")));
        assertTrue(recs.stream().noneMatch(r -> r.contains("status")));
        assertTrue(recs.stream().noneMatch(r -> r.contains("created_at")));
    }

    @Test void shouldCheckSelectivity() {
        advisor.analyzeTable("t", 1000, Map.of(
            "high", new Object[]{false, 500L, false},
            "low", new Object[]{false, 2L, false}
        ));
        assertTrue(advisor.getStats("t").getColumns().get("high").isHighSelectivity());
        assertFalse(advisor.getStats("t").getColumns().get("low").isHighSelectivity());
    }

    @Test void shouldNotRecommendAlreadyIndexed() {
        advisor.analyzeTable("t", 1000, Map.of("col", new Object[]{true, 500L, false}));
        assertTrue(advisor.recommendIndexes().isEmpty());
    }
}
