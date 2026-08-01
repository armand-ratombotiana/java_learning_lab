# Lab 01: Problem Walkthrough — HTTP/2 Frame Multiplexing and Stream Prioritization

## Problem Statement

Implement an HTTP/2-like frame multiplexing and stream prioritization engine. The engine must:

1. Model **frames** (type, flags, stream ID, payload) with the HTTP/2 frame taxonomy: HEADERS, DATA, WINDOW_UPDATE, RST_STREAM, SETTINGS, PING, GOAWAY.
2. Model **streams** with the state machine `idle → open → half-closed(remote) → closed`, carrying per-stream flow-control windows.
3. **Multiplex** frames from many streams into a single ordered output sequence, driven by a **weighted priority scheduler**: streams are weighted, and the scheduler emits frames proportionally to weight using **deficit round-robin** (DRR).
4. Enforce the **connection-level flow-control window** in addition to per-stream windows — DATA in flight across all streams is bounded.
5. Handle **flow-control credit** via WINDOW_UPDATE accounting (receipt of credit increases the window; sending DATA consumes it).
6. Implement **stream lifecycle rules**: DATA on a closed stream is a protocol error; RST_STREAM closes a stream early; GOAWAY drains the connection.

**Constraints**

- The multiplexer must be deterministic: same inputs, same output frame order.
- The scheduler must be fair: an idle stream must not accumulate starvation of its peers.
- All code must compile under Java 21+.

---

## Walkthrough

### Step 1: Model frames and streams

A `Frame` is the wire unit: type, flags, stream ID, payload (simulated bytes count). Streams carry a weight, a send window, and a state.

```java
package com.networking.deep.lab01;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public final class Http2Multiplexer {

    public enum FrameType { HEADERS, DATA, WINDOW_UPDATE, RST_STREAM, SETTINGS, PING, GOAWAY }

    public record Frame(FrameType type, int streamId, String payload) {
        int size() { return payload.length(); }
    }

    public enum StreamState { IDLE, OPEN, HALF_CLOSED_REMOTE, CLOSED }

    public static final class Stream {
        private final int id;
        private final int weight;
        private StreamState state = StreamState.IDLE;
        private long sendWindow;   // per-stream flow control credit
        private long deficit;      // DRR scheduler credit

        Stream(int id, int weight, long initialWindow) {
            this.id = id;
            this.weight = weight;
            this.sendWindow = initialWindow;
        }

        void open() { state = StreamState.OPEN; }
        void halfClose() { state = StreamState.HALF_CLOSED_REMOTE; }
        void close() { state = StreamState.CLOSED; }

        boolean canSend(int bytes) { return sendWindow >= bytes && state != StreamState.CLOSED; }
        void consumeWindow(int bytes) { sendWindow -= bytes; }
        void addWindow(int bytes) { sendWindow += bytes; }

        int id() { return id; }
        int weight() { return weight; }
        long sendWindow() { return sendWindow; }
        StreamState state() { return state; }
        long deficit() { return deficit; }
        void addDeficit(long d) { deficit += d; }
        void spendDeficit(long d) { deficit -= d; }
        void resetDeficit() { deficit = 0; }
    }
```

### Step 2: The frame queue and flow-control accounting

The multiplexer keeps per-stream FIFO queues. The connection window bounds total in-flight DATA across all streams — this is the memory-safety property of the protocol.

```java
    public static final class Multiplexer {
        private final Map<Integer, Stream> streams = new HashMap<>();
        private final Map<Integer, Queue<Frame>> pending = new HashMap<>();
        private final List<Frame> output = new ArrayList<>();
        private final long connectionWindowLimit;
        private long connectionWindow;
        private int nextStreamId = 1;

        public Multiplexer(long connectionWindowLimit) {
            this.connectionWindowLimit = connectionWindowLimit;
            this.connectionWindow = connectionWindowLimit;
        }

        public int openStream(int weight) {
            int id = nextStreamId;
            nextStreamId += 2;
            Stream s = new Stream(id, weight, 65_535);
            s.open();
            streams.put(id, s);
            pending.put(id, new ArrayDeque<>());
            return id;
        }

        public void enqueue(Frame f) {
            Stream s = streams.get(f.streamId());
            if (s == null) throw new IllegalStateException("Unknown stream " + f.streamId());
            if (f.type() == FrameType.DATA && s.state() == StreamState.CLOSED) {
                throw new IllegalStateException("Protocol error: DATA on closed stream " + f.streamId());
            }
            if (f.type() == FrameType.DATA) {
                // sendWindow was already reduced by every accepted enqueue, so the
                // check is simply "does the frame fit in the remaining credit".
                if (f.size() > s.sendWindow()) {
                    throw new IllegalStateException("Per-stream window exceeded on stream " + f.streamId());
                }
                if (f.size() > connectionWindow) {
                    throw new IllegalStateException("Connection window exceeded");
                }
                s.consumeWindow(f.size());
                connectionWindow -= f.size();
            }
            pending.get(f.streamId()).add(f);
        }

        public void sendWindowUpdate(int streamId, int credit) {
            Stream s = streams.get(streamId);
            if (s == null) return;
            s.addWindow(credit);
            connectionWindow = Math.min(connectionWindowLimit, connectionWindow + credit);
            output.add(new Frame(FrameType.WINDOW_UPDATE, streamId, "+" + credit));
        }
```

### Step 3: The deficit round-robin scheduler

DRR serves streams in round-robin order, granting each stream `deficit += weight * quantum` per visit; the stream may emit frames until its debt exceeds its deficit. This gives proportional fairness: a stream with weight 2 gets twice the bandwidth of a stream with weight 1, averaged over time — while an empty stream simply passes its turn.

```java
        private static final long QUANTUM = 4096;

        public List<Frame> multiplexOneRound() {
            List<Frame> round = new ArrayList<>();
            List<Stream> active = streams.values().stream()
                    .filter(s -> !pending.get(s.id()).isEmpty())
                    .sorted(Comparator.comparingInt(Stream::id))
                    .toList();
            for (Stream s : active) {
                Queue<Frame> q = pending.get(s.id());
                if (q.isEmpty()) continue;
                s.addDeficit((long) s.weight() * QUANTUM);
                while (!q.isEmpty()) {
                    Frame head = q.peek();
                    if (head.type() == FrameType.DATA) {
                        if (head.size() > s.deficit()) break;
                        s.spendDeficit(head.size());   // credit is consumed per frame
                    }
                    round.add(q.poll());
                }
            }
            output.addAll(round);
            return round;
        }

        public List<Frame> multiplex() {
            List<Frame> all = new ArrayList<>();
            List<Frame> round;
            do {
                round = multiplexOneRound();
                all.addAll(round);
            } while (!round.isEmpty());
            return List.copyOf(all);
        }
```

Note the bookkeeping semantics: DATA bytes are deducted from both windows at `enqueue` time — this is the hard bound that makes buffer overrun impossible by construction. `rstStream` refunds the connection-window credit of discarded pending DATA frames (the receiver will never consume them).

### Step 4: Stream lifecycle operations

RST_STREAM closes a stream early and frees its pending frames; GOAWAY marks the connection for graceful drain; closing returns the connection-window credit of the discarded DATA frames (the receiver will never see them).

```java
        public void rstStream(int streamId, String reason) {
            Stream s = streams.get(streamId);
            if (s == null) return;
            long freed = pending.get(streamId).stream()
                    .filter(f -> f.type() == FrameType.DATA).mapToLong(Frame::size).sum();
            connectionWindow = Math.min(connectionWindowLimit, connectionWindow + freed);
            pending.get(streamId).clear();
            s.close();
            output.add(new Frame(FrameType.RST_STREAM, streamId, reason));
        }

        public void goaway(int lastProcessedStream) {
            output.add(new Frame(FrameType.GOAWAY, 0, "last=" + lastProcessedStream));
            for (Stream s : streams.values()) {
                if (s.state() == StreamState.OPEN) s.halfClose();
            }
        }

        public void closeStream(int streamId) {
            Stream s = streams.get(streamId);
            if (s != null) s.close();
        }

        public long connectionWindow() { return connectionWindow; }
        public long sendWindow(int streamId) { return streams.get(streamId).sendWindow(); }
        public List<Frame> output() { return List.copyOf(output); }
    }
```

### Step 5: Demo — proving proportional fairness and window enforcement

The demo opens three streams (weights 4, 2, 1), enqueues 16 KB of DATA on each plus an overflow flood on the heavy stream, multiplexes, and verifies:

1. **Window enforcement**: enqueueing beyond the per-stream window is rejected (deferred DATA never unboundedly buffers).
2. **Proportional fairness**: stream 1 emits exactly 4× stream 3's bytes per scheduling round (16 : 4 frames of 1024 B).
3. **Deficit accounting**: credit is consumed per frame — a stream emits frames only while its deficit covers them, and unspent credit carries no debt into the next round.

```java
    public static void main(String[] args) {
        Multiplexer mux = new Multiplexer(256_000);

        int heavy = mux.openStream(4);
        int medium = mux.openStream(2);
        int light = mux.openStream(1);

        System.out.println("=== HTTP/2 Multiplexer Demo ===\n");
        System.out.println("Streams: heavy(w=4), medium(w=2), light(w=1)");

        for (int i = 0; i < 16; i++) {
            mux.enqueue(new Frame(FrameType.DATA, heavy, pad("H" + i, 1024)));
            mux.enqueue(new Frame(FrameType.DATA, medium, pad("M" + i, 1024)));
            mux.enqueue(new Frame(FrameType.DATA, light, pad("L" + i, 1024)));
        }

        System.out.println("-- Enforce per-stream window: overflow beyond 65535 bytes --");
        int accepted = 0;
        try {
            for (int i = 0; i < 10000; i++) {
                mux.enqueue(new Frame(FrameType.DATA, heavy, "overflow-" + i));
                accepted++;
            }
        } catch (IllegalStateException e) {
            System.out.println("  Rejected after " + accepted + " overflow frames: " + e.getMessage());
        }

        System.out.println("-- Round 1: weighted scheduling (DRR) --");
        List<Frame> round1 = mux.multiplexOneRound();
        long h1 = round1.stream().filter(f -> f.streamId() == heavy).count();
        long m1 = round1.stream().filter(f -> f.streamId() == medium).count();
        long l1 = round1.stream().filter(f -> f.streamId() == light).count();
        System.out.printf("  round-1 frames: heavy=%d medium=%d light=%d%n", h1, m1, l1);
        System.out.printf("  fairness ratio (heavy/light): %.2f (ideal 4.00)%n",
                h1 / (double) Math.max(l1, 1));

        System.out.println("-- Drain remaining queues --");
        List<Frame> rest = mux.multiplex();
        long hTot = rest.stream().filter(f -> f.streamId() == heavy).count();
        long mTot = rest.stream().filter(f -> f.streamId() == medium).count();
        long lTot = rest.stream().filter(f -> f.streamId() == light).count();
        System.out.printf("  drained: heavy=%d medium=%d light=%d (all streams fully served)%n",
                hTot, mTot, lTot);
        System.out.println("  sendWindow(heavy)=" + mux.sendWindow(heavy)
                + " (16 x 1024B + " + accepted + " overflow frames spent)");

        System.out.println("-- Stream lifecycle: RST on light, then GOAWAY --");
        mux.rstStream(light, "cancelled");
        mux.goaway(heavy);
        mux.output().stream()
                .filter(f -> f.type() != FrameType.DATA)
                .forEach(f -> System.out.println("  " + f.type() + " sid=" + f.streamId()
                        + " [" + f.payload() + "]"));
    }

    /** Deterministic payload of exactly `width` bytes. */
    private static String pad(String tag, int width) {
        String s = tag + "x".repeat(Math.max(0, width - tag.length()));
        return s.substring(0, Math.min(width, s.length()));
    }
}
```

### Step 6: Verify the expected behavior

| Check | Expected | Mechanism |
|-------|----------|-----------|
| 3 streams × 16 × 1024B frames | Round 1: heavy 16, medium 8, light 4 | DRR deficit = weight × 4096 per round, spent per frame |
| Heavy vs light round-1 ratio | 4.00 | 16 × 1024 = 16384 = 4 × 4096 exactly |
| Overflow flood on heavy | Rejected after exactly 3,866 small frames | Per-stream window (65535) enforced at enqueue: 16×1024 + 49,148 B of 10–13 B payloads fills it, frame 3,867th doesn't fit |
| Drain | All remaining frames emitted (heavy 3,866, medium 8, light 12) | DRR converges — fairness is bounded-lag, not starvation |
| RST_STREAM | Light stream closed, credit refunded | Lifecycle + window accounting |
| GOAWAY | Remaining streams half-closed, drain marker emitted | Graceful shutdown |

The round-1 ratio lands at exactly 4.00: each round grants `weight × 4096` bytes of deficit, heavy emits 16 frames (16384 B), medium 8 (8192 B), light 4 (4096 B), and each frame *consumes* that much credit — the deficit never accumulates unspent, which is what keeps the schedule proportional across rounds.

---

## Complexity Analysis

- **Enqueue**: O(Q) amortized for the in-flight window scan (Q = pending frames per stream) — in production the window is tracked incrementally, making it O(1).
- **Multiplex**: O(R · S) rounds × streams visited, where each round emits at least one frame — total O(F + S·R) for F frames, with R bounded by the number of deficit replenishments (≤ F in practice).
- **Window accounting**: O(1) per frame.
- **Space**: O(F) pending frames across streams; O(F) output list. Bounded by the flow-control windows — a sender can never buffer more than the negotiated windows can carry.
- **Fairness**: DRR guarantees proportional fairness with bounded lag (each stream's service error is bounded by its quantum).

---

## Follow-Up Questions

1. **How does the priority *tree* (RFC 7540) differ from this weighted scheduler?** The tree adds dependency semantics: a child's frames are scheduled only after its parent completes; siblings share the parent's budget by weight. Implementation: a second pass that orders streams by dependency depth before DRR within each subtree.

2. **How do you extend to RFC 9218 extensible prioritization (urgency + incremental)?** Replace the tree with an urgency value (0-7) and an incremental flag: non-incremental streams are served before lower-urgency streams; incremental streams of the same urgency share bandwidth round-robin — a strict simplification of this DRR engine.

3. **What happens on the receive side (de-multiplexing)?** Frames are parsed from the byte stream in order, dispatched per stream ID, and each stream's payload is reassembled; the receive window tracks unconsumed bytes and WINDOW_UPDATE is emitted when the application consumes past half the window.

4. **How do you defend against the small-frame CPU attack?** Cap frame sizes at the SETTINGS maximum (16MB but typically 16KB), enforce a minimum useful DATA size per stream before scheduling (the scheduler skips sub-quantum frames), and rate-limit HEADERS frames.

5. **How does HPACK's dynamic table interact with multiplexing?** The header block decoder state is shared across streams — decoding must happen in frame order; a corrupted block stalls subsequent HEADERS. Mitigations: periodic table eviction and strict error handling — a senior answer notes this is why QPACK split the table across separate streams in QUIC.

6. **How do you integrate this with TCP's backpressure?** The TCP send buffer is the outer window: the multiplexer stops *emitting* when the socket buffer is full; streams keep accepting frames up to their flow-control windows, which bounds memory — the layering is windows inside windows.
