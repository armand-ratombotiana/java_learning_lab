# Mock Interview Transcript: Stream Processor with Tumbling Windows

| Field | Detail |
|-------|--------|
| **Level** | Senior Backend / Streaming Engineer |
| **Duration** | 45 minutes |
| **Format** | Whiteboard + implementation |
| **Problem** | "Implement a stream processor with tumbling windows: count and sum event values per fixed time window, emit each window exactly once, and decide what to do with late events." |

---

## Part 0: Scene Setting (2 minutes)

**Interviewer (I):** We ingest click events from a fleet of mobile SDKs. Each
event carries a client-side timestamp and a value. Downstream wants aggregates —
count, sum, average — per fixed 1-minute window, one result per window, on time.
Events can arrive late: mobile devices buffer and retry, clocks skew. Implement a
tumbling-window stream processor and tell me how you handle the late ones.

**Candidate (C):** One-minute tumbling windows with one emitted aggregate per
window — and late events, which is where all the interesting decisions live. Let
me pin the semantics first.

---

## Part 1: Clarifying Questions (5 minutes)

**C:** Four questions. One: what timestamp decides a window — the event's own
(processing-independent) timestamp, or arrival time? Two: how late is "late" —
what's the realistic tail? Three: is exactly-once emission required, or is
at-least-once with dedupe acceptable? Four: what should late events do — join
their window's aggregate, or get dropped?

**I:** Event timestamp — a window is about *when the click happened*, not when we
received it. Tail: minutes, occasionally hours after SDK retries. One emission
per window, duplicates are tolerable but must be detectable. Late events should
still be counted — they're real clicks.

**C:** That's the standard shape, and it pins the design: **event-time windows
with a bounded late buffer**, emission triggered by **watermark-like progress**
— "we are confident no more events for window W will arrive, because we've seen
event time beyond W's end plus a lateness margin." Events arriving after that
are either dropped or sent to a repair path; I'll implement the dropped-plus-
counted behavior and discuss the repair path.

---

## Part 2: Window Semantics (7 minutes)

**C:** A **tumbling window** is a fixed-size, non-overlapping, contiguous
alignment of time: [0:00-0:01), [0:01-0:02), ... Every event maps to exactly one
window by `windowStart = (eventTime / windowSize) * windowSize`. Because windows
are aligned to the epoch, all processors in the fleet agree on the same
boundaries — that's the property that lets downstream consumers join results
across producers.

**I:** And versus sliding or session windows?

**C:** Sliding windows give overlapping coverage (e.g., "last 5 minutes, every
minute") — better for trending, more expensive: each event belongs to multiple
windows, so emission multiplies. Session windows are data-driven — they close on
inactivity gaps — which is right for user-behavior analytics but is *not* a
fixed schedule. Tumbling is the cheapest and most predictable: O(1) amortized
per event, one aggregate per window, perfect for "metrics per minute."

---

## Part 3: The Late-Event Problem (8 minutes)

**C:** The core tension: **a window can't wait forever, and events can arrive
forever.** If we emit W's aggregate when W ends, we miss every late event; if we
wait until the end of time, the aggregate is useless. The resolution is
**bounded lateness**: emission is *deferred* past the window's end by a
lateness margin, and any event whose event time falls inside an already-emitted
window is handled by policy.

**I:** How do you know a window is "done"? This is the watermark question.

**C:** Two levels. A **static watermark** is the simple, honest version: emit a
window when we observe an event whose *event time* is past `window.end +
latenessMargin`. It assumes the stream is roughly in order. A **dynamic
watermark** tracks per-source progress — e.g., each SDK reports its high-water
mark, or we track the minimum of observed "idle" signals — and emits when all
sources pass the threshold. Dynamic watermarks are what Flink/Dataflow do, and
they're robust to one slow source; but they need per-source metadata from
ingestion, which we don't have. For this system I'll implement the static
watermark: emit-on-observation, with the margin sized to the observed
late-tail distribution — say 2 minutes for a 1-minute window.

**I:** Sizing the margin?

**C:** From the data, not from vibes: measure the p99.9 of
`arrivalTime - eventTime` over the last week — that's the margin. Too small:
frequent late events spill into the repair path. Too large: results are
systematically delayed. The margin is a latency-vs-completeness slider, and the
repair path is the safety net that makes a small margin acceptable.

---

## Part 4: Implementation Walkthrough (10 minutes)

**C:** (writing) The processor keeps a `TreeMap<Long, WindowAccumulator>` — open
windows keyed by start — plus an injectable clock so tests don't sleep. Per
event: compute `windowStart`, accumulate count and sum into the map
(`computeIfAbsent`). On each call — and on a periodic tick — we close every
window whose *emission condition* holds: `windowStart + windowSize + margin <=
currentWatermark`, where the watermark is the newest event time we've seen. A
closed window becomes a `WindowResult` (start, end, count, sum, avg) in the
emitted list.

**I:** Why TreeMap?

**C:** Two reasons. It keeps open windows sorted by start, so closing is a
`headMap(watermarkCutoff)` scan — the same range-delete pattern as the sliding
window rate limiter — and it makes "which windows are still open" O(log w).
A hash map can't answer "all windows before X" in bulk.

**C:** (writing) Late events: when an event's window is already closed, it's
dropped from aggregation — but counted in a `lateEvents` counter, so the
operator sees the rate. The counter is the contract: we don't silently swallow
data, we meter the policy's cost.

**I:** Exactly-once emission?

**C:** The honest statement: the processor emits at-least-once — a crash between
accumulating and recording the emission can re-emit. Downstream dedupes by
`windowStart` (the result carries a stable, monotonic key). True exactly-once in
a streaming system is an end-to-end contract — transactional sink writes plus
idempotent downstream — and I'd scope it there, not in the windowing logic
itself.

---

## Part 5: State, Recovery, and Scale (6 minutes)

**I:** Where does the state live, and what happens on a crash?

**C:** In production this processor is stateful across the cluster — open
windows are *sharded state* (by hash of window or by key), stored in a
stateful store (RocksDB in Flink, or a shared KV) with periodic checkpoints. On
recovery, the processor restores open windows from the last checkpoint and
replays buffered input since then — at-least-once again, hence the dedupe. The
in-memory demo is the algorithm; the checkpointing is the production glue.

**I:** Scaling?

**C:** Windows are naturally partitionable: aggregate per key shard, and every
processor's window boundaries are identical (epoch-aligned), so partial results
per shard combine with a trivial merge — sum the counts, sum the sums. That's
the tumbling-window gift: the alignment makes distributed aggregation a
divide-and-conquer merge instead of a shuffle. Also: memory is bounded by
`windowsOpen × keys`, and windows close on schedule — no unbounded growth as
long as the watermark advances.

**I:** What if the watermark *stops* advancing — a silent source?

**C:** Then windows stay open and memory grows — the classic streaming failure.
Mitigations: a processing-time watchdog that force-closes windows older than a
hard bound (with a metric), plus ingestion-side idle detection. "Never emit" is
a worse failure than "emit slightly early."

---

## Part 6: Closing and Feedback (3 minutes)

**I:** Wrap up.

**C:** Tumbling windows align event time to fixed epoch boundaries, so every node
aggregates identically and results merge trivially. Emission is watermark-driven
— window end plus a data-sized lateness margin — and late events are metered,
not ignored: counted, dropped from the aggregate, and recoverable through a
repair path if the margin undershoots. The contract to the downstream is
at-least-once with a stable window key for dedupe.

**I:** Strong. You went straight to the watermark decision, correctly insisted
the margin comes from the data's late-tail distribution, and gave the
crash-recovery story without being asked. Two notes: name the *repair path* out
loud earlier — a late event that missed its window can be replayed through a
bounded re-run (that's what the counter feeds), and consider the out-of-order
*within* a window case (sorting inside the accumulator is a trivial detail, but
calling it out shows you've operated a real pipeline).

---

## Evaluation Scorecard

| Dimension | Observation | Score (1-5) |
|-----------|-------------|-------------|
| Clarifying questions | Event-time vs processing-time asked first — the decisive question | 5 |
| Window semantics | Tumbling vs sliding vs session trade-offs | 5 |
| Watermark reasoning | Static vs dynamic; margin sized from late-tail data | 5 |
| Implementation | TreeMap range-close, injectable clock, late counter | 5 |
| Failure handling | Watermark stall watchdog, bounded memory, crash recovery | 5 |
| Delivery contract | At-least-once + dedupe by window key; exactly-once scoped end-to-end | 5 |
| Breadth | Repair path and in-window ordering mentioned late | 4 |

**Overall: Strong Hire** — complete streaming semantics coverage with production
operational awareness.

## Common Pitfalls Candidates Hit

- Windowing by *processing* time and calling it a window — no, that's batching.
- Emitting at window end and claiming lateness "doesn't happen."
- No watermark concept at all; windows "close when the timer fires."
- Silently dropping late events with no counter, no repair path.
- Claiming exactly-once emission from a single machine's in-memory state.
- Unbounded open-window memory when the watermark stalls.
