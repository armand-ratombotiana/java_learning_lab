package com.databases.replication;

import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class ConflictResolverTest {
    private ConflictResolver r;
    @BeforeEach void setUp() { r = new ConflictResolver(ConflictResolver.ConflictStrategy.LAST_WRITE_WINS); }

    @Test void shouldResolveLastWriteWins() {
        var v1 = new ConflictResolver.VersionedValue("old", 100, "n1", 1);
        var v2 = new ConflictResolver.VersionedValue("new", 200, "n2", 2);
        assertEquals("new", r.resolve("k", List.of(v1, v2)));
    }

    @Test void shouldReturnSingleValue() {
        var v = new ConflictResolver.VersionedValue("only", 100, "n1", 1);
        assertEquals("only", r.resolve("k", List.of(v)));
    }

    @Test void shouldHandleTimestampBased() {
        var r2 = new ConflictResolver(ConflictResolver.ConflictStrategy.TIMESTAMP_BASED);
        var v1 = new ConflictResolver.VersionedValue("a", 1000, "n1", 1);
        var v2 = new ConflictResolver.VersionedValue("b", 2000, "n2", 2);
        assertEquals("b", r2.resolve("k", List.of(v1, v2)));
    }

    @Test void shouldMergeCRDT() {
        var r2 = new ConflictResolver(ConflictResolver.ConflictStrategy.CRDT_MERGE);
        var v1 = new ConflictResolver.VersionedValue("x,y", 100, "n1", 1);
        var v2 = new ConflictResolver.VersionedValue("y,z", 200, "n2", 2);
        assertEquals("x,y,z", r2.resolve("k", List.of(v1, v2)));
    }

    @Test void shouldLogAndResolveManually() {
        var v1 = new ConflictResolver.VersionedValue("a", 100, "n1", 1);
        var v2 = new ConflictResolver.VersionedValue("b", 200, "n2", 2);
        r.resolve("k", List.of(v1, v2));
        assertFalse(r.getAllConflicts().isEmpty());
        r.resolveManually("k");
        assertTrue(r.getConflicts("k").isEmpty());
    }
}
