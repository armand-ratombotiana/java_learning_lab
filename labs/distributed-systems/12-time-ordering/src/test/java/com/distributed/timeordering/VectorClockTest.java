package com.distributed.timeordering;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VectorClockTest {

    @Test
    void testTickIncrementsOwnEntry() {
        VectorClock vc = new VectorClock(3, 1);
        vc.tick();
        int[] val = vc.getValue();
        assertEquals(1, val[1]);
        assertEquals(0, val[0]);
        assertEquals(0, val[2]);
    }

    @Test
    void testHappensBefore() {
        VectorClock v1 = new VectorClock(3, 0);
        VectorClock v2 = new VectorClock(3, 1);
        v1.tick();
        v1.send();
        int[] sent = v1.getValue();
        v2.receive(sent, System.currentTimeMillis());
        assertTrue(v1.happensBefore(v2));
    }

    @Test
    void testConcurrentEvents() {
        VectorClock v1 = new VectorClock(2, 0);
        VectorClock v2 = new VectorClock(2, 1);
        v1.tick();
        v2.tick();
        assertTrue(v1.isConcurrent(v2));
    }

    @Test
    void testReceiveMergesCorrectly() {
        VectorClock v1 = new VectorClock(2, 0);
        VectorClock v2 = new VectorClock(2, 1);
        v1.tick();
        v1.tick();
        int[] v1Val = v1.send();
        v2.receive(v1Val, System.currentTimeMillis());
        int[] v2Val = v2.getValue();
        assertEquals(2, v2Val[0]);
        assertEquals(1, v2Val[1]);
    }
}
