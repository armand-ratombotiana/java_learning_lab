# TCP/UDP Deep Dive — Refactoring

## 1. Refactoring the Congestion Control Simulator

### Problem 1: Monolithic CongestionControl class
The original class handles slow start, congestion avoidance, and fast recovery in a single class with conditional logic.

**Refactored Design:**
```java
// Strategy pattern for congestion control algorithms
public interface CongestionAlgorithm {
    void onAck(CongestionState state, int ackedBytes);
    void onLoss(CongestionState state, LossType type);
    String getName();
}

public class SlowStart implements CongestionAlgorithm {
    @Override
    public void onAck(CongestionState state, int ackedBytes) {
        int newCwnd = state.getCwnd() + ackedBytes;
        int ssthresh = state.getSsthresh();
        if (newCwnd >= ssthresh) {
            state.setAlgorithm(new CongestionAvoidance());
            newCwnd = ssthresh;
        }
        state.setCwnd(newCwnd);
    }

    @Override
    public void onLoss(CongestionState state, LossType type) {
        state.setSsthresh(Math.max(state.getCwnd() / 2, 2));
        if (type == LossType.TIMEOUT) {
            state.setCwnd(10);
        }
    }

    @Override
    public String getName() { return "Slow Start"; }
}
```

### Problem 2: Hard-coded constants
Magic numbers for initial window, ssthresh, and MSS made the code inflexible.

**Refactored:**
```java
public record TcpConfig(int mss, int initialWindow, int initialSsthresh) {
    public static final TcpConfig DEFAULT = new TcpConfig(1460, 10, 65535);
    public static final TcpConfig LOW_LATENCY = new TcpConfig(1460, 10, 10240);
    public static final TcpConfig HIGH_BDP = new TcpConfig(1460, 10, 262144);
}
```

## 2. Refactoring the Reliable UDP Implementation

### Problem 3: Mixed concerns in ReliableUdpSocket
The class handled sending, receiving, retransmission, and reordering — violating Single Responsibility Principle.

**Refactored:**
```java
public class ReliableUdpSocket {
    private final UdpSender sender;
    private final UdpReceiver receiver;
    private final RetransmissionManager retransmitter;
    private final ReorderBuffer reorderBuffer;

    public ReliableUdpSocket(int port, InetSocketAddress peer) {
        DatagramSocket socket = new DatagramSocket(port);
        this.sender = new UdpSender(socket, peer);
        this.receiver = new UdpReceiver(socket);
        this.retransmitter = new RetransmissionManager(sender, 100);
        this.reorderBuffer = new ReorderBuffer();
    }

    public void send(byte[] data) {
        sender.send(data, retransmitter::track);
    }

    public Optional<byte[]> receive() {
        return receiver.receive()
            .flatMap(reorderBuffer::add)
            .map(UdpPacket::getData);
    }
}
```

## 3. Refactoring the TCP State Machine

### Problem 4: State machine with if-else chain
The original used a monolithic if-else chain for state transitions.

**Refactored:**
```java
// State pattern
public interface TcpStateHandler {
    TcpState onEvent(TcpEvent event, TcpConnection context);
}

public class ClosedState implements TcpStateHandler {
    @Override
    public TcpState onEvent(TcpEvent event, TcpConnection context) {
        return switch (event) {
            case ACTIVE_OPEN -> TcpState.SYN_SENT;
            case PASSIVE_OPEN -> TcpState.LISTEN;
            default -> throw new IllegalStateException("Invalid event: " + event);
        };
    }
}

public class EstablishedState implements TcpStateHandler {
    @Override
    public TcpState onEvent(TcpEvent event, TcpConnection context) {
        return switch (event) {
            case CLOSE -> {
                context.sendFin();
                yield TcpState.FIN_WAIT_1;
            }
            case RECEIVE_FIN -> {
                context.sendAck();
                yield TcpState.CLOSE_WAIT;
            }
            default -> TcpState.ESTABLISHED;
        };
    }
}
```

## 4. Refactoring the Socket Configuration

### Problem 5: Scattered socket configuration logic
Socket tuning parameters were spread across multiple files.

**Refactored:**
```java
@FunctionalInterface
public interface SocketConfigurer {
    void configure(Socket socket) throws SocketException;

    static SocketConfigurer forThroughput() {
        return s -> {
            s.setReceiveBufferSize(2 * 1024 * 1024);
            s.setSendBufferSize(2 * 1024 * 1024);
            s.setTcpNoDelay(false);
        };
    }

    static SocketConfigurer forLowLatency() {
        return s -> {
            s.setReceiveBufferSize(65536);
            s.setSendBufferSize(65536);
            s.setTcpNoDelay(true);
            s.setPerformancePreferences(2, 0, 1);
        };
    }

    static SocketConfigurer combined(SocketConfigurer... configurers) {
        return s -> {
            for (var c : configurers) c.configure(s);
        };
    }
}
```

## 5. Refactoring the Benchmark Code

### Problem 6: Duplicated benchmark logic
TCP and UDP benchmarks shared significant code.

**Refactored:**
```java
public abstract class NetworkBenchmark {
    protected final int dataSize;
    protected final int packetSize;

    public NetworkBenchmark(int dataSize, int packetSize) {
        this.dataSize = dataSize;
        this.packetSize = packetSize;
    }

    public final BenchmarkResult run() throws Exception {
        long start = System.nanoTime();
        transmit();
        long elapsed = System.nanoTime() - start;
        return new BenchmarkResult(dataSize, elapsed);
    }

    protected abstract void transmit() throws Exception;
}
```

## Summary
These refactorings improve:
- **Testability**: Each component can be tested independently
- **Extensibility**: New algorithms can be added without changing existing code
- **Maintainability**: Clear separation of concerns
- **Readability**: Self-documenting code structure
