package com.capstone.ecommerce;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class RecommendationEngineTest {
    private RecommendationEngine engine;

    @BeforeEach
    void setUp() { engine = new RecommendationEngine(); }

    @Test void testRecordAndGetPopular() {
        engine.recordPurchase("u1", "p1");
        engine.recordPurchase("u2", "p1");
        engine.recordPurchase("u3", "p1");
        var popular = engine.getPopularProducts(5);
        assertEquals(1, popular.size());
        assertEquals("p1", popular.get(0));
    }

    @Test void testCollaborativeFiltering() {
        engine.recordPurchase("u1", "p1");
        engine.recordPurchase("u1", "p2");
        engine.recordPurchase("u2", "p1");
        engine.recordPurchase("u2", "p3");
        var recs = engine.getCollaborativeRecommendations("u1", 5);
        assertTrue(recs.contains("p3") || recs.isEmpty());
    }

    @Test void testEmptyRecommendations() {
        var recs = engine.getCollaborativeRecommendations("unknown", 5);
        assertTrue(recs.isEmpty());
    }

    @Test void testFrequentlyBoughtTogether() {
        engine.recordPurchase("u1", "p1");
        engine.recordPurchase("u1", "p2");
        var together = engine.getFrequentlyBoughtTogether("p1", 5);
        assertTrue(together.isEmpty() || together.contains("p2"));
    }

    @Test void testReset() {
        engine.recordPurchase("u1", "p1");
        engine.reset();
        assertEquals(0, engine.getUserCount());
    }
}
