package com.networking.tcp-udp-deep-dive;

public class TcpStateMachine {
    public enum TcpState {
        CLOSED, LISTEN, SYN_SENT, SYN_RCVD, ESTABLISHED,
        FIN_WAIT_1, FIN_WAIT_2, CLOSE_WAIT, CLOSING, LAST_ACK, TIME_WAIT
    }

    public enum TcpEvent {
        PASSIVE_OPEN, ACTIVE_OPEN, SEND_SYN, RECEIVE_SYN,
        SEND_SYN_ACK, RECEIVE_SYN_ACK, RECEIVE_ACK, CLOSE,
        RECEIVE_FIN, SEND_FIN, TIMEOUT, DATA_RECEIVED
    }

    private TcpState state = TcpState.CLOSED;

    public TcpState getState() { return state; }

    public void handleEvent(TcpEvent event) {
        TcpState newState = computeNextState(event);
        if (newState == null) {
            throw new IllegalStateException(
                "Invalid transition: state=" + state + ", event=" + event);
        }
        System.out.println("State: " + state + " --[" + event + "]--> " + newState);
        state = newState;
    }

    private TcpState computeNextState(TcpEvent event) {
        return switch (state) {
            case CLOSED -> switch (event) {
                case PASSIVE_OPEN -> TcpState.LISTEN;
                case ACTIVE_OPEN -> {
                    System.out.println("Sent SYN");
                    yield TcpState.SYN_SENT;
                }
                default -> null;
            };
            case LISTEN -> switch (event) {
                case RECEIVE_SYN -> {
                    System.out.println("Received SYN, sent SYN+ACK");
                    yield TcpState.SYN_RCVD;
                }
                case CLOSE -> TcpState.CLOSED;
                default -> null;
            };
            case SYN_SENT -> switch (event) {
                case RECEIVE_SYN -> {
                    System.out.println("Received SYN+ACK, sent ACK");
                    yield TcpState.SYN_RCVD;
                }
                case RECEIVE_SYN_ACK -> {
                    System.out.println("Received SYN+ACK, sent ACK");
                    yield TcpState.ESTABLISHED;
                }
                default -> null;
            };
            case SYN_RCVD -> switch (event) {
                case RECEIVE_ACK -> TcpState.ESTABLISHED;
                case CLOSE -> {
                    System.out.println("Sent FIN");
                    yield TcpState.FIN_WAIT_1;
                }
                default -> null;
            };
            case ESTABLISHED -> switch (event) {
                case CLOSE -> {
                    System.out.println("Sent FIN");
                    yield TcpState.FIN_WAIT_1;
                }
                case RECEIVE_FIN -> {
                    System.out.println("Received FIN, sent ACK");
                    yield TcpState.CLOSE_WAIT;
                }
                default -> TcpState.ESTABLISHED;
            };
            case FIN_WAIT_1 -> switch (event) {
                case RECEIVE_ACK -> TcpState.FIN_WAIT_2;
                case RECEIVE_FIN -> {
                    System.out.println("Received FIN, sent ACK");
                    yield TcpState.CLOSING;
                }
                default -> null;
            };
            case FIN_WAIT_2 -> switch (event) {
                case RECEIVE_FIN -> {
                    System.out.println("Received FIN, sent ACK");
                    System.out.println("Timer: TIME_WAIT (2MSL)");
                    yield TcpState.TIME_WAIT;
                }
                default -> null;
            };
            case CLOSE_WAIT -> switch (event) {
                case CLOSE -> {
                    System.out.println("Sent FIN");
                    yield TcpState.LAST_ACK;
                }
                default -> null;
            };
            case CLOSING -> switch (event) {
                case RECEIVE_ACK -> {
                    System.out.println("Timer: TIME_WAIT (2MSL)");
                    yield TcpState.TIME_WAIT;
                }
                default -> null;
            };
            case LAST_ACK -> switch (event) {
                case RECEIVE_ACK -> TcpState.CLOSED;
                default -> null;
            };
            case TIME_WAIT -> switch (event) {
                case TIMEOUT -> TcpState.CLOSED;
                default -> null;
            };
        };
    }

    public static void main(String[] args) {
        TcpStateMachine fsm = new TcpStateMachine();
        System.out.println("=== TCP Connection Scenario ===");
        fsm.handleEvent(TcpEvent.ACTIVE_OPEN);
        fsm.handleEvent(TcpEvent.RECEIVE_SYN_ACK);
        fsm.handleEvent(TcpEvent.RECEIVE_ACK);
        System.out.println("=== Data Transfer ===");
        fsm.handleEvent(TcpEvent.DATA_RECEIVED);
        System.out.println("=== Termination ===");
        fsm.handleEvent(TcpEvent.CLOSE);
        fsm.handleEvent(TcpEvent.RECEIVE_ACK);
        fsm.handleEvent(TcpEvent.RECEIVE_FIN);
        fsm.handleEvent(TcpEvent.TIMEOUT);
        System.out.println("Final state: " + fsm.getState());
    }
}
