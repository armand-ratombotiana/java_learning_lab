package com.networking.tcp-udp-deep-dive;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TcpStateMachineTest {
    private TcpStateMachine fsm;

    @BeforeEach
    void setUp() {
        fsm = new TcpStateMachine();
    }

    @Test
    void initialStateIsClosed() {
        assertEquals(TcpStateMachine.TcpState.CLOSED, fsm.getState());
    }

    @Test
    void activeOpenTransitionsToSynSent() {
        fsm.handleEvent(TcpStateMachine.TcpEvent.ACTIVE_OPEN);
        assertEquals(TcpStateMachine.TcpState.SYN_SENT, fsm.getState());
    }

    @Test
    void passiveOpenTransitionsToListening() {
        fsm.handleEvent(TcpStateMachine.TcpEvent.PASSIVE_OPEN);
        assertEquals(TcpStateMachine.TcpState.LISTEN, fsm.getState());
    }

    @Test
    void fullConnectionScenario() {
        fsm.handleEvent(TcpStateMachine.TcpEvent.ACTIVE_OPEN);
        assertEquals(TcpStateMachine.TcpState.SYN_SENT, fsm.getState());
        fsm.handleEvent(TcpStateMachine.TcpEvent.RECEIVE_SYN_ACK);
        fsm.handleEvent(TcpStateMachine.TcpEvent.RECEIVE_ACK);
        assertEquals(TcpStateMachine.TcpState.ESTABLISHED, fsm.getState());
    }

    @Test
    void establishedToFinWait1OnClose() {
        fsm.handleEvent(TcpStateMachine.TcpEvent.ACTIVE_OPEN);
        fsm.handleEvent(TcpStateMachine.TcpEvent.RECEIVE_SYN_ACK);
        fsm.handleEvent(TcpStateMachine.TcpEvent.RECEIVE_ACK);
        fsm.handleEvent(TcpStateMachine.TcpEvent.CLOSE);
        assertEquals(TcpStateMachine.TcpState.FIN_WAIT_1, fsm.getState());
    }

    @Test
    void establishedToCloseWaitOnReceiveFin() {
        fsm.handleEvent(TcpStateMachine.TcpEvent.ACTIVE_OPEN);
        fsm.handleEvent(TcpStateMachine.TcpEvent.RECEIVE_SYN_ACK);
        fsm.handleEvent(TcpStateMachine.TcpEvent.RECEIVE_ACK);
        fsm.handleEvent(TcpStateMachine.TcpEvent.RECEIVE_FIN);
        assertEquals(TcpStateMachine.TcpState.CLOSE_WAIT, fsm.getState());
    }

    @Test
    void fullConnectionAndTermination() {
        completeConnection();
        initiateClose();
        completeClose();
    }

    private void completeConnection() {
        fsm.handleEvent(TcpStateMachine.TcpEvent.ACTIVE_OPEN);
        fsm.handleEvent(TcpStateMachine.TcpEvent.RECEIVE_SYN_ACK);
        fsm.handleEvent(TcpStateMachine.TcpEvent.RECEIVE_ACK);
        assertEquals(TcpStateMachine.TcpState.ESTABLISHED, fsm.getState());
    }

    private void initiateClose() {
        fsm.handleEvent(TcpStateMachine.TcpEvent.CLOSE);
        assertEquals(TcpStateMachine.TcpState.FIN_WAIT_1, fsm.getState());
    }

    private void completeClose() {
        fsm.handleEvent(TcpStateMachine.TcpEvent.RECEIVE_ACK);
        assertEquals(TcpStateMachine.TcpState.FIN_WAIT_2, fsm.getState());
        fsm.handleEvent(TcpStateMachine.TcpEvent.RECEIVE_FIN);
        assertEquals(TcpStateMachine.TcpState.TIME_WAIT, fsm.getState());
        fsm.handleEvent(TcpStateMachine.TcpEvent.TIMEOUT);
        assertEquals(TcpStateMachine.TcpState.CLOSED, fsm.getState());
    }

    @Test
    void invalidTransitionThrowsException() {
        assertThrows(IllegalStateException.class, () ->
            fsm.handleEvent(TcpStateMachine.TcpEvent.RECEIVE_FIN));
    }

    @Test
    void passiveOpenThenReceiveSyn() {
        fsm.handleEvent(TcpStateMachine.TcpEvent.PASSIVE_OPEN);
        assertEquals(TcpStateMachine.TcpState.LISTEN, fsm.getState());
        fsm.handleEvent(TcpStateMachine.TcpEvent.RECEIVE_SYN);
        assertEquals(TcpStateMachine.TcpState.SYN_RCVD, fsm.getState());
        fsm.handleEvent(TcpStateMachine.TcpEvent.RECEIVE_ACK);
        assertEquals(TcpStateMachine.TcpState.ESTABLISHED, fsm.getState());
    }

    @Test
    void listenToClosedOnClose() {
        fsm.handleEvent(TcpStateMachine.TcpEvent.PASSIVE_OPEN);
        fsm.handleEvent(TcpStateMachine.TcpEvent.CLOSE);
        assertEquals(TcpStateMachine.TcpState.CLOSED, fsm.getState());
    }

    @Test
    void passiveCloseScenario() {
        completeConnection();
        fsm.handleEvent(TcpStateMachine.TcpEvent.RECEIVE_FIN);
        assertEquals(TcpStateMachine.TcpState.CLOSE_WAIT, fsm.getState());
        fsm.handleEvent(TcpStateMachine.TcpEvent.CLOSE);
        assertEquals(TcpStateMachine.TcpState.LAST_ACK, fsm.getState());
        fsm.handleEvent(TcpStateMachine.TcpEvent.RECEIVE_ACK);
        assertEquals(TcpStateMachine.TcpState.CLOSED, fsm.getState());
    }

    @Test
    void simultaneousClose() {
        completeConnection();
        fsm.handleEvent(TcpStateMachine.TcpEvent.CLOSE);
        fsm.handleEvent(TcpStateMachine.TcpEvent.RECEIVE_FIN);
        assertEquals(TcpStateMachine.TcpState.CLOSING, fsm.getState());
        fsm.handleEvent(TcpStateMachine.TcpEvent.RECEIVE_ACK);
        assertEquals(TcpStateMachine.TcpState.TIME_WAIT, fsm.getState());
    }

    @Test
    void establishedAllowsDataTransmission() {
        completeConnection();
        fsm.handleEvent(TcpStateMachine.TcpEvent.DATA_RECEIVED);
        assertEquals(TcpStateMachine.TcpState.ESTABLISHED, fsm.getState());
    }
}
