package com.databases.replication;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class ReplicationManagerTest {
    private ReplicationManager m;
    @BeforeEach void setUp() { m = new ReplicationManager(ReplicationManager.ReplicationStrategy.QUORUM); }

    @Test void shouldAddNodes() {
        m.addNode("leader", true); m.addNode("f1", false); m.addNode("f2", false);
        assertEquals(3, m.getAllNodes().size());
        assertEquals(2, m.getFollowers().size());
    }

    @Test void shouldReplicate() {
        m.addNode("leader", true); m.addNode("f1", false); m.addNode("f2", false);
        assertTrue(m.replicate("test-data"));
    }

    @Test void shouldHandleLeaderFailure() {
        m.addNode("leader", true); m.addNode("f1", false); m.addNode("f2", false);
        var old = m.getLeader();
        assertTrue(m.handleLeaderFailure());
        assertNotSame(old, m.getLeader());
    }

    @Test void shouldPromoteFollower() {
        m.addNode("leader", true); m.addNode("candidate", false);
        m.promoteFollower("candidate");
        assertEquals("candidate", m.getLeader().getNodeId());
    }

    @AfterEach void tearDown() { m.shutdown(); }
}
