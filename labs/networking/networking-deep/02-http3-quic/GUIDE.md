# GUIDE — HTTP/3 and QUIC

## Step 1: QUIC Connection Model
```java
public record QuicConnection(String connectionId, String sourceAddress, String destAddress, QuicState state) {}
public enum QuicState { INIT, HANDSHAKE, ESTABLISHED, MIGRATED, CLOSED }
```

## Step 2: 0-RTT Handshake
- Client sends initial packet with TLS 1.3 early data
- Server validates and processes immediately
- Replay protection with anti-replay window

## Step 3: Stream Multiplexing
```java
QuicStream stream = conn.openStream(true); // bidirectional
stream.send(data);
```

## Step 4: Connection Migration
- New path probe with connection ID
- Path validation via PATH_CHALLENGE/PATH_RESPONSE
- Seamless continuation on new path

## Step 5: Performance Comparison
- Measure latency under packet loss
- Compare HTTP/2 vs HTTP/3 HOL blocking behavior

## Step 6: Exercises
1. Implement 0-RTT replay protection with monotonic timestamps
2. Build a connection migration simulator with path switch
3. Create a benchmark comparing HTTP/2 and HTTP/3 throughput under 5% packet loss
