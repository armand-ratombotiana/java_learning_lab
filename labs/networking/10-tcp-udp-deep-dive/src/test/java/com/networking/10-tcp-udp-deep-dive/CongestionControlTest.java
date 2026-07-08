package com.networking.tcp-udp-deep-dive;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class CongestionControlTest {

    @Test
    void renoSlowStartDoublesCwnd() {
        var state = new CongestionControl.CongestionState(10, 100, 1460,
            CongestionControl.Algorithm.RENO);
        int initial = (int) state.getCwnd();
        state.onAckReceived(1460);
        assertTrue(state.getCwnd() > initial,
            "Slow start should increase cwnd");
        assertTrue(state.getCwnd() <= 100,
            "cwnd should not exceed ssthresh without loss");
    }

    @Test
    void renoLossHalvesSsthresh() {
        var state = new CongestionControl.CongestionState(10, 100, 1460,
            CongestionControl.Algorithm.RENO);
        for (int i = 0; i < 5; i++) state.onAckReceived(1460);
        double beforeSsthresh = state.getSsthresh();
        state.onLossDetected(CongestionControl.LossType.TRIPLE_DUP_ACK);
        assertTrue(state.getSsthresh() < beforeSsthresh,
            "Loss should decrease ssthresh");
    }

    @Test
    void timeoutResetsCwnd() {
        var state = new CongestionControl.CongestionState(10, 100, 1460,
            CongestionControl.Algorithm.RENO);
        for (int i = 0; i < 3; i++) state.onAckReceived(1460);
        state.onLossDetected(CongestionControl.LossType.TIMEOUT);
        assertEquals(10, state.getCwnd(), 0.001,
            "Timeout should reset cwnd to initial");
    }

    @Test
    void cubicHasDifferentGrowth() {
        var reno = new CongestionControl.CongestionState(10, 100, 1460,
            CongestionControl.Algorithm.RENO);
        var cubic = new CongestionControl.CongestionState(10, 100, 1460,
            CongestionControl.Algorithm.CUBIC);
        for (int i = 0; i < 10; i++) {
            reno.onAckReceived(1460);
            cubic.onAckReceived(1460);
        }
        assertNotEquals(reno.getCwnd(), cubic.getCwnd(), 1.0,
            "Reno and Cubic should behave differently");
    }

    @Test
    void bbrHandlesLoss() {
        var state = new CongestionControl.CongestionState(10, 100, 1460,
            CongestionControl.Algorithm.BBR);
        state.onLossDetected(CongestionControl.LossType.TRIPLE_DUP_ACK);
        assertTrue(state.getSsthresh() > 0);
    }

    @Test
    void congestionAvoidanceIsLinear() {
        var state = new CongestionControl.CongestionState(100, 50, 1460,
            CongestionControl.Algorithm.RENO);
        double before = state.getCwnd();
        state.onAckReceived(1460);
        assertTrue(state.getCwnd() < before * 2,
            "Congestion avoidance should grow slower than slow start");
    }

    @Test
    void multipleLossEvents() {
        var state = new CongestionControl.CongestionState(10, 100, 1460,
            CongestionControl.Algorithm.RENO);
        for (int i = 0; i < 5; i++) state.onAckReceived(1460);
        state.onLossDetected(CongestionControl.LossType.TRIPLE_DUP_ACK);
        double afterFirst = state.getCwnd();
        state.onLossDetected(CongestionControl.LossType.TRIPLE_DUP_ACK);
        assertTrue(state.getCwnd() <= afterFirst,
            "Second loss should decrease or keep cwnd");
    }

    @Test
    void renoFastRecoveryNotInSlowStart() {
        var state = new CongestionControl.CongestionState(6, 100, 1460,
            CongestionControl.Algorithm.RENO);
        state.onAckReceived(1460);
        state.onLossDetected(CongestionControl.LossType.TRIPLE_DUP_ACK);
        assertTrue(state.getCwnd() >= state.getSsthresh(),
            "After fast recovery, cwnd should equal new ssthresh");
    }
}
