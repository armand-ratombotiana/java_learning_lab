package com.capstone.agent;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class AgentMemoryTest {
    private AgentMemory memory;

    @BeforeEach
    void setUp() { memory = new AgentMemory(); }

    @Test void testShortTermMemory() {
        memory.addToShortTerm("first observation");
        memory.addToShortTerm("second observation");
        assertEquals(2, memory.shortTermSize());
    }

    @Test void testLongTermMemory() {
        memory.addToLongTerm("important experience");
        assertEquals(1, memory.longTermSize());
    }

    @Test void testRecall() {
        memory.addToShortTerm("the sky is blue");
        memory.addToLongTerm("oceans are blue");
        var results = memory.recall("blue");
        assertTrue(results.size() >= 2);
    }

    @Test void testConsolidation() {
        for (int i = 0; i < 150; i++) memory.addToShortTerm("item " + i);
        assertTrue(memory.shortTermSize() < 150);
        assertTrue(memory.longTermSize() > 0);
    }

    @Test void testEpisodicMemory() {
        memory.addToLongTerm("visited Paris in summer");
        var episodes = memory.getEpisodicMemory("visited Paris in summ");
        assertEquals(1, episodes.size());
    }

    @Test void testClear() {
        memory.addToShortTerm("test");
        memory.addToLongTerm("test");
        memory.clear();
        assertEquals(0, memory.shortTermSize());
        assertEquals(0, memory.longTermSize());
    }
}
