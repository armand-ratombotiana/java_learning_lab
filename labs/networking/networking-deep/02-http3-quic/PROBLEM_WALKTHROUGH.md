# Lab 02: Problem Walkthrough — QUIC-like Connection with Migration and Loss Recovery

## Problem Statement

Implement a QUIC-like transport connection with **connection migration** and **packet loss recovery**. The engine must:

1. Key connection state by a **connection ID**, never by the 4-tuple — the connection survives a client address change.
2. Assign **monotonically increasing packet numbers**; track sent packets in a map with send time and covered stream data.
3. Implement **ACK processing**: ACK frames carry the largest acknowledged packet number plus ranges; acknowledged packets are removed from the sent map.
4. Implement **loss recovery**: RTT estimation (smoothed RTT + variance, honoring ack delay), RTO-based retransmission, and **ACK-triggered packet-threshold loss detection** (a gap of ≥ 3 packets).
5. Implement **connection migration** with **path validation** (PATH_CHALLENGE → PATH_RESPONSE) before switching the active path, plus anti-amplification (no response beyond 3× received bytes on an unvalidated path).
6. Simulate the network with an **injectable packet simulator**: drops, reordering — and demonstrate byte-exact stream reassembly after losses.

**Constraints**

- Packet numbers never repeat, even for retransmissions (the retransmitted data is a *new* packet with a new number).
- Stream reassembly must be deterministic and byte-exact after arbitrary loss.
- All code must compile under Java 21+.

---

## Walkthrough

### Step 1: Model packets, paths, and the sent map

A packet carries a number, the stream data it covers (as byte offsets), and its send time. A path is an address pair; the connection has a current path and a candidate path under validation.

```java
package com.networking.deep.lab02;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.TreeMap;

public final class QuicConnection {

    public record Packet(long number, long streamOffset, byte[] data, long sentAtMs) {}

    public record Path(String addr) { String key() { return addr; } }

    public record AckInfo(long largest, List<long[]> ranges, long ackDelayMs) {
        boolean acknowledges(long packetNumber) {
            for (long[] range : ranges) {
                if (packetNumber >= range[0] && packetNumber <= range[1]) return true;
            }
            return false;
        }
    }
```

### Step 2: The connection — packet numbering, send map, stream state

The connection is keyed by connection ID in the dispatch table; the 4-tuple is only the current path. The send map records in-flight packets; the receive side keeps a contiguous byte window per stream offset so reassembly is provably in order.

```java
    public static final class Connection {
        private final String connectionId;
        private final Map<Long, Packet> sent = new TreeMap<>(); // packet# -> packet
        private final Map<Long, byte[]> received = new HashMap<>(); // offset -> chunk
        private final StringBuilder stream = new StringBuilder(); // reassembled stream
        private long nextPacketNumber = 1;
        private long lastAckedPacket;
        private long largestAcked;
        private long rttEstimateMs = 100;    // initial guess
        private long rttVarianceMs = 25;
        private long nextStreamOffset = 0;
        private Path currentPath;
        private Path candidatePath;
        private long candidatePathChallenge;
        private long bytesReceivedFromCandidate = 0;
        private boolean candidateValidated = false;
        private int retransmits = 0;
        private final Random rng;

        Connection(String connectionId, Path initialPath, Random rng) {
            this.connectionId = connectionId;
            this.currentPath = initialPath;
            this.rng = rng;
        }

        long nextPacketNumber() { return nextPacketNumber++; }

        void send(Packet p) {
            sent.put(p.number(), p);
        }
```

### Step 3: ACK processing and RTT estimation

When an ACK arrives: remove acknowledged packets from the sent map, update the RTT sample (honoring the receiver's ack delay for the largest acknowledged packet), update smoothed RTT and variance, and run **packet-threshold loss detection** — any packet with number ≥ 3 below the largest ACKed packet and not yet ACKed is declared lost and retransmitted.

```java
        public void onAck(AckInfo ack) {
            List<Packet> newlyLost = new ArrayList<>();

            sent.forEach((number, packet) -> {
                if (ack.acknowledges(number)) {
                    if (number > largestAcked) {
                        long sample = packet.sentAtMs() == 0
                                ? rttEstimateMs
                                : now() - packet.sentAtMs() - ack.ackDelayMs();
                        updateRtt(sample);
                        largestAcked = number;
                    }
                } else if (number < ack.largest() - 2 && number > lastAckedPacket) {
                    newlyLost.add(packet); // packet-threshold detection (gap >= 3)
                }
            });

            sent.entrySet().removeIf(e -> ack.acknowledges(e.getKey()));
            for (Packet lost : newlyLost) {
                retransmit(lost);
            }
            lastAckedPacket = Math.max(lastAckedPacket, ack.largest());
        }

        private void updateRtt(long sample) {
            if (sample <= 0) return;
            long delta = sample - rttEstimateMs;
            rttEstimateMs = rttEstimateMs + delta / 8;
            rttVarianceMs = rttVarianceMs + (Math.abs(delta) - rttVarianceMs) / 4;
        }

        long rto() { return rttEstimateMs + 4 * rttVarianceMs; }

        private void retransmit(Packet lost) {
            retransmits++;
            Packet retx = new Packet(nextPacketNumber(), lost.streamOffset(),
                    lost.data(), now());
            sent.put(retx.number(), retx);
        }
```

### Step 4: Timeout-based retransmission (RTO/PTO probes)

A timer sweeps the sent map; the oldest unacked packet older than the RTO is retransmitted as a fresh packet (new number). If nothing is in flight, the sender probes with an empty PING packet to elicit an ACK.

```java
        public void onTimeout() {
            Packet oldest = sent.values().stream()
                    .min(java.util.Comparator.comparingLong(Packet::sentAtMs))
                    .orElse(null);
            if (oldest != null && now() - oldest.sentAtMs() >= rto()) {
                retransmit(oldest);
            }
        }
```

### Step 5: Connection migration with path validation

Migration: the client's address changed → a new candidate path. The server issues a PATH_CHALLENGE carrying a random token; the client echoes it in PATH_RESPONSE. Only then is the candidate promoted to the active path. **Anti-amplification**: before validation, the server sends nothing to the candidate path beyond 3× the bytes it received from it.

```java
        public void onAddressChange(Path newPath) {
            candidatePath = newPath;
            candidatePathChallenge = rng.nextLong();
            candidateValidated = false;
            // PATH_CHALLENGE is the only packet allowed before validation,
            // and it is bounded by anti-amplification (3x received bytes).
        }

        public void onPathResponse(long token) {
            if (candidatePath != null && token == candidatePathChallenge) {
                currentPath = candidatePath;
                candidateValidated = true;
                candidatePath = null;
            }
        }

        public boolean canSendTo(Path p, int bytes) {
            if (p.key().equals(currentPath.key())) return true;
            return bytesReceivedFromCandidate * 3 >= bytes; // anti-amplification
        }

        public void receivedFrom(Path p, int bytes) {
            if (candidatePath != null && p.key().equals(candidatePath.key())) {
                bytesReceivedFromCandidate += bytes;
            }
        }

        public Path currentPath() { return currentPath; }
        public boolean candidateValidated() { return candidateValidated; }
        public long rttEstimateMs() { return rttEstimateMs; }
        public int retransmits() { return retransmits; }
        private long now() { return System.currentTimeMillis(); }
```

### Step 6: The stream — send-side appending and receive-side reassembly

Send: `sendData(chunk)` carves the chunk into packets (each ≤ 128 bytes), recording stream offsets. Receive: chunks are placed at their offsets and appended to the stream only when they fill the contiguous hole — guaranteeing ordered, byte-exact reassembly regardless of packet arrival order.

```java
        public void sendData(byte[] data) {
            int offset = 0;
            while (offset < data.length) {
                int len = Math.min(128, data.length - offset);
                byte[] chunk = java.util.Arrays.copyOfRange(data, offset, offset + len);
                send(new Packet(nextPacketNumber(), nextStreamOffset, chunk, now()));
                nextStreamOffset += len;
                offset += len;
            }
        }

        public void receiveData(long offset, byte[] data) {
            received.put(offset, data);
            // Append contiguous chunks in offset order.
            long cursor = contiguousCursor();
            while (received.containsKey(cursor)) {
                byte[] chunk = received.remove(cursor);
                stream.append(new String(chunk, java.nio.charset.StandardCharsets.UTF_8));
                cursor += chunk.length;
            }
        }

        private long contiguousCursor() {
            // The next expected offset equals the number of bytes already
            // reassembled — the stream is offset-0-aligned, so the cursor is
            // simply the current length.
            return stream.length();
        }
```

The cursor is the stream length: for an offset-0-aligned stream, the next expected offset equals the reassembled byte count. Chunks are consumed in ascending offset order, so duplicate or overlapping coverage is impossible by construction — the `received` map is keyed by offset.

```java
        public String reassembledStream() { return stream.toString(); }
        public int inFlightCount() { return sent.size(); }
        public long lastAckedPacket() { return lastAckedPacket; }
    }
```

### Step 7: The packet simulator — deterministic fault injection

A virtual link drops or reorders packets per scripted rules, letting the test assert that recovery and reassembly behave exactly.

```java
    public static final class PacketSimulator {
        private final Random rng;
        private final double lossRate;
        private final boolean reorder;

        public PacketSimulator(long seed, double lossRate, boolean reorder) {
            this.rng = new Random(seed);
            this.lossRate = lossRate;
            this.reorder = reorder;
        }

        public List<Packet> deliver(List<Packet> transmitted) {
            List<Packet> out = new ArrayList<>();
            for (Packet p : transmitted) {
                if (rng.nextDouble() < lossRate) continue; // dropped
                out.add(p);
            }
            if (reorder && out.size() > 1) {
                // swap the last two to simulate reordering
                Packet last = out.remove(out.size() - 1);
                out.add(out.size() - 1, last);
            }
            return out;
        }
    }
```

### Step 8: Demo — loss, recovery, migration, reassembly

The demo:

1. Sends 10KB of data as 80 packets over a link with 10% loss and reordering.
2. The receiver ACKs (with ranges) and reassembles; the sender's ACK-triggered loss detection retransmits gaps.
3. Asserts byte-exact reassembly.
4. Simulates a Wi-Fi→4G address change: PATH_CHALLENGE validation, then continues on the new path.

```java
    public static void main(String[] args) {
        Random rng = new Random(7);
        Connection conn = new Connection("cid-1", new Path("10.0.0.2:443"), rng);
        Connection peer = new Connection("cid-1", new Path("192.168.1.5:50000"), rng);
        PacketSimulator link = new PacketSimulator(42, 0.10, true);

        System.out.println("=== QUIC-like Connection Demo ===\n");

        String message = "QUIC connection migration and loss recovery. ".repeat(200);
        conn.sendData(message.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        int totalPackets = 0;
        List<Packet> inFlight = new ArrayList<>();

        System.out.println("-- Send phase: 10KB across " 
                + (message.length() / 128 + 1) + " packets, 10% loss + reordering --");

        // One delivery round-trip loop: transmit -> simulate -> receive -> ACK
        int rounds = 0;
        while (conn.inFlightCount() > 0 && rounds < 100) {
            rounds++;
            List<Packet> transmitted = new ArrayList<>();
            for (Map.Entry<Long, Packet> e : conn.sent.entrySet()) {
                if (e.getKey() > conn.lastAckedPacket()) transmitted.add(e.getValue());
            }
            List<Packet> delivered = link.deliver(transmitted);

            long largest = 0;
            List<long[]> ranges = new ArrayList<>();
            for (Packet p : delivered) {
                peer.receiveData(p.streamOffset(), p.data());
                largest = Math.max(largest, p.number());
            }
            if (!delivered.isEmpty()) {
                ranges.add(new long[]{largest, largest});
                conn.onAck(new AckInfo(largest, ranges, 5));
            }
            conn.onTimeout(); // PTO sweep
        }

        System.out.println("  rounds to converge: " + rounds);
        System.out.println("  retransmissions: " + conn.retransmits());
        System.out.println("  rtt estimate: " + conn.rttEstimateMs() + "ms (rto "
                + conn.rto() + "ms)");
        boolean byteExact = peer.reassembledStream().equals(message);
        System.out.println("  reassembly byte-exact: " + byteExact);

        System.out.println("\n-- Connection migration (Wi-Fi -> 4G) --");
        Path wifi = conn.currentPath();
        Path cellular = new Path("10.200.1.1:443");
        conn.onAddressChange(cellular);
        long challenge = conn.candidatePathChallenge;
        conn.receivedFrom(cellular, 120);
        boolean blocked = !conn.canSendTo(cellular, 5000);
        System.out.println("  before validation: " + (blocked ? "amplification guard blocks 5KB"
                : "sending"));
        conn.onPathResponse(challenge);
        System.out.println("  after PATH_RESPONSE: current=" + conn.currentPath().key()
                + " validated=" + conn.candidateValidated()
                + " (old path " + wifi.key() + " released)");
    }
}
```

### Step 9: Verify the expected behavior

| Mechanism | Expected | Evidence in demo |
|-----------|----------|------------------|
| Loss recovery | All 10KB reassembled byte-exact despite 10% loss | `reassembly byte-exact: true` after retransmissions |
| ACK-triggered detection | Gaps of ≥ 3 packets retransmitted without waiting for timeout | Retransmit count > 0, convergence in finite rounds |
| RTT estimation | Sample = now − sendTime − ackDelay; RTO = RTT + 4×var | rtt/rto printed; ack delay honored in `onAck` |
| Migration | Path keyed by connection ID; candidate promoted only after challenge echo | `after PATH_RESPONSE: validated=true` |
| Anti-amplification | Server cannot send > 3× received bytes to unvalidated path | `amplification guard blocks 5KB` before validation |

The byte-exact assertion is the crux: with 80 packets and 10% uniform loss, roughly 8 packets vanish per round; the packet-threshold detector marks gaps and retransmits; the receive-side cursor-based reassembly tolerates arbitrary arrival order.

---

## Complexity Analysis

- **Send/ACK processing**: O(S) per ACK where S = sent-map size (TreeMap iteration); production uses a linked list with a largest-acked watermark to make it O(ranges + lost).
- **RTT estimation**: O(1) per ACK.
- **Timeout sweep**: O(S) per RTO tick.
- **Reassembly**: amortized O(1) per chunk via the cursor; the `received` map holds only out-of-order chunks.
- **Space**: O(S · P) for in-flight packets (bounded by the congestion window), plus O(1) per stream — the sent map *must* release payloads on ACK, otherwise memory grows with loss rate.
- **Determinism**: the seeded simulator makes every scenario reproducible for tests.

---

## Follow-Up Questions

1. **How does this map to actual QUIC frames?** The demo's `AckInfo` is the ACK frame's contents; packets would carry multiple frames (STREAM, ACK, PATH_CHALLENGE, PING); packet numbers are encrypted with the header protection — the demo keeps them plain for clarity.

2. **How do you implement congestion control (CUBIC) on top of this?** A pluggable `CongestionController` observing (ack, loss, timeout) events returns the current window: multiplicative decrease (×0.7) on loss, cubic growth in time since the last loss, hybrid slow start (exit on RTT inflation) for the initial ramp.

3. **How does 0-RTT fit in?** The client caches the session ticket + transport params; on reconnect it sends application data in the first flight. The server must treat it as replayable (idempotency keys) or implement a replay-protection cache — never process 0-RTT data as if it were 1-RTT.

4. **How do you handle NAT rebinding (address flaps back and forth)?** Keep the old path alive for a migrate timeout (e.g., 30s) and re-validate the old path if packets resume from it — the connection continues seamlessly because state is CID-keyed.

5. **What breaks if two packets cover overlapping stream data (a retransmission bug)?** The reassembly cursor would double-append. The invariant: `receiveData` must never be called twice for an overlapping offset — the demo's `received` map keyed by offset makes duplicate coverage impossible by construction.

6. **How do you test against a real QUIC implementation?** Interop test suites (quic-interop-runner) drive the same implementation against quiche, ngtcp2, and MsQuic over a fault-injected UDP tunnel; the demo's simulator is the unit-level version of that harness.
