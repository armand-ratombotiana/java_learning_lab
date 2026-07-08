package com.distributed.idgeneration;

import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class UuidV7GeneratorTest {

    @Test
    void testGenerateReturnsUuid() {
        UuidV7Generator gen = new UuidV7Generator();
        UUID id = gen.generate();
        assertNotNull(id);
        assertEquals(7, id.version());
    }

    @Test
    void testUniqueness() {
        UuidV7Generator gen = new UuidV7Generator();
        Set<UUID> ids = new HashSet<>();
        for (int i = 0; i < 10000; i++) {
            assertTrue(ids.add(gen.generate()));
        }
    }

    @Test
    void testExtractTimestamp() {
        UuidV7Generator gen = new UuidV7Generator();
        UUID id = gen.generate();
        long ts = gen.extractTimestamp(id);
        long now = System.currentTimeMillis();
        assertTrue(ts <= now && ts > now - 1000);
    }
}
