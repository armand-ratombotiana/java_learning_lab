package com.capstone.kafka;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class ConsumerGroupTest {
    private ConsumerGroup group;

    @BeforeEach
    void setUp() { group = new ConsumerGroup("test-group"); }

    @Test void testJoinAndLeave() {
        group.join("consumer-1");
        group.join("consumer-2");
        assertEquals(2, group.memberCount());
        assertTrue(group.hasMember("consumer-1"));
        group.leave("consumer-1");
        assertEquals(1, group.memberCount());
    }

    @Test void testPartitionAssignment() {
        group.join("c1");
        group.join("c2");
        group.rebalance(List.of(0, 1, 2, 3));
        var assignments = group.getAllAssignments();
        assertEquals(4, assignments.size());
    }

    @Test void testLeaderElection() {
        group.join("c1");
        assertEquals("c1", group.getLeader());
        group.join("c2");
        group.leave("c1");
        assertEquals("c2", group.getLeader());
    }

    @Test void testNoMembers() {
        group.rebalance(List.of(0, 1, 2));
        assertTrue(group.getAllAssignments().isEmpty());
    }

    @Test void testAssignPartition() {
        group.assignPartition(0, "c1");
        assertEquals("c1", group.getAssignment(0));
    }
}
