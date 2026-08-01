# Problem Walkthrough: Stream Processor with Tumbling Windows

## Problem Statement

A pipeline ingests click events from mobile SDKs. Each event carries a
**client-side (event-time) timestamp** and a numeric value. Downstream
consumers need per-minute aggregates — count, sum, average — one result per
window, computed over the window's *event time*, not arrival time. Events
arrive late (SDK buffering, retries, clock skew), so the processor must decide
when a window is "done" and what happens to events that arrive after their
window was emitted.

Implement a stream processor with **tumbling windows**: epoch-aligned,
non-overlapping fixed windows; accumulation per window; **watermark-driven
emission** (a window closes once observed event time passes its end plus a
lateness margin); and a metered late-event policy.

## Requirements

- **Event-time windowing:** `windowStart = (eventTime / windowSize) * windowSize`;
  arrival time never influences which window an event belongs to.
- **One aggregate per window:** count, sum, and average, emitted exactly once per
  closed window (at-least-once delivery contract with a stable window key).
- **Bounded emission delay:** a window is not emitted at its end; emission waits
  for the watermark — the newest observed event time — to pass `window.end +
  latenessMargin`.
- **Late events:** events whose window already closed are dropped from
  aggregation but *metered* (counter + log), with a repair path in production.
- **Bounded state:** open windows close on schedule; memory is bounded by
  window count, not stream length.
- **Deterministic testing:** injectable clock and explicit timestamps.

## Constraints & Assumptions

- Single-process in-memory processor (the algorithm); distributed state and
  checkpointing covered in Operations.
- Window size 1 minute, lateness margin 2 minutes in the demo (production:
  measured from the arrival-vs-event-time tail, e.g., p99.9).
- Static watermark (observed event time) — dynamic per-source watermarks noted
  as the Flink/Dataflow-style upgrade.
- Values are doubles; aggregation is count + sum + avg (any associative
  aggregate generalizes).

## Window Semantics: Why Tumbling

| Window type | Shape | Cost | When it fits |
|-------------|-------|------|--------------|
| **Tumbling** | Fixed, non-overlapping, epoch-aligned | O(1) amortized per event; one emission per window | **Chosen** — scheduled metrics per minute |
| Sliding | Overlapping ("every minute, last 5") | Each event joins multiple windows; emission ×overlap | Trending curves |
| Session | Closes on inactivity gaps | Data-dependent boundaries; merge on close | User-behavior analytics |

**Epoch alignment is the key property:** `windowStart` is a pure function of the
timestamp, so every processor in the fleet derives identical boundaries. That
makes distributed aggregation a merge of per-shard partials (sum counts, sum
sums) instead of a reshuffle.

## Solution Overview

```
Event(e: ts=10:00:00.500, v=3)  ->  windowStart = floor(ts / 60s) * 60s = 10:00:00
                                    accumulate into Window(10:00:00) {count=1, sum=3}

Emission rule (watermark W = max observed event time):
    close window S  when  S + windowSize + latenessMargin <= W

Late event (ts inside an already-closed window):
    dropped from aggregate; lateEvents++ ; logged for repair path
```

### The late-event tension

A window cannot wait forever (results would be useless) and late events can
arrive forever (mobile retries). The resolution is **bounded lateness**:

1. Emission is deferred past window end by `latenessMargin` — sized from the
   measured arrival-delay tail (p99.9), not vibes.
2. Events past the margin are metered, not silently swallowed.
3. A production repair path re-runs the affected windows from buffered input if
   the margin undershoots (the counter is the trigger signal).

### The watermark

- **Static watermark** (implemented): `W = max event time observed`. A window is
  emitted when `W >= window.end + margin`. Simple, honest, right for roughly
  in-order streams.
- **Dynamic watermark** (production): per-source progress signals; emission waits
  for all sources to pass the threshold — robust to one slow source, needs
  ingestion-side metadata.

## Step-by-Step Solution

### Step 1: Define the event and window result

`Event(long tsMillis, String key, double value)` and
`WindowResult(long windowStart, long windowEnd, int count, double sum, double avg,
Instant emittedAt)` — the result carries a stable key (`windowStart`) for
downstream dedupe.

### Step 2: Accumulate into open windows

`TreeMap<Long, WindowAccumulator> windows` keyed by window start.
`computeIfAbsent(windowStart, ...)` then accumulate count and sum. TreeMap keeps
windows sorted so "close everything before X" is a range operation.

### Step 3: Track the watermark and close eligible windows

`process(event)` updates the watermark (`W = max(W, event.ts)`) and, after
accumulating, closes every window with `start + size + margin <= W` via a
`headMap(cutoff)` scan. Emitted results append to an `emitted` list. The demo
calls `closeElapsed(now)` explicitly so tests drive the schedule; production
would run the same check on a timer tick.

### Step 4: Handle late events

If an event's window was already emitted — its start is below
`lastClosedBoundary`, the highest window start the processor has closed — it is
dropped from aggregation and counted in `lateEvents`. The boundary tracker makes
the check correct even when **no windows are currently open** (an empty
`windows` map is not "everything is late"); the `firstKey()` comparison alone
would miss late events arriving after the last window closed. The counter is the
contract: the operator sees how often the margin undershoots and can tune it or
trigger the repair path.

### Step 5: Verify with a timeline

Demo timeline (window = 100 ms, lateness margin = 150 ms for readability):

```
event t=+0    (window 0)      event t=+120 (window 100)
event t=+40   (window 0)      event t=+180 (window 100)
closeElapsed(now=+600)        -> watermark advances to 600; cutoff = 600-150-100+1 = 351
                               -> windows 0 and 100 emit (count=2, sums 8.0 and 6.0)
event t=+90   (window 0)      -> window 0 already emitted -> LATE, metered, not counted
event t=+260  (window 200)    -> accumulates; no closure yet (cutoff = 260-250+1 = 11)
closeElapsed(now=+900)        -> cutoff = 651 -> window 200 emits (count=1, sum=6.0)
final state: openWindows=0, emitted=3, lateEvents=1
```

Closure is driven by the **watermark** (the newest observed event time) plus the
margin — never by a timer at window end — so late events inside the margin still
count toward their window, and the metered tail is visible in `lateEvents`.

## Java 21+ Implementation

```java
package com.systemdesign.deep.lab08;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Lab 08: Stream Processor with Tumbling Windows.
 * Demonstrates: event-time tumbling windows, watermark-driven emission with a
 * lateness margin, metered late events, and bounded open-window state.
 */
public class TumblingWindowLab {

    /** A stream event: client-side timestamp + value. */
    public record Event(long tsMillis, String key, double value) {}

    /** The aggregate emitted once per closed window. Stable key: windowStart. */
    public record WindowResult(long windowStart, long windowEnd, int count,
                               double sum, double avg, Instant emittedAt) {}

    /** Per-window accumulator. */
    static final class WindowAccumulator {
        int count;
        double sum;

        void add(Event e) {
            count++;
            sum += e.value();
        }

        WindowResult toResult(long start, long windowMillis) {
            return new WindowResult(start, start + windowMillis, count, sum,
                    count == 0 ? 0 : sum / count, Instant.now());
        }
    }

    /** Tumbling-window processor: accumulate, watermark, close, meter late events. */
    public static final class TumblingWindowProcessor {
        private final long windowMillis;
        private final long latenessMarginMillis;
        private final TreeMap<Long, WindowAccumulator> windows = new TreeMap<>();
        private final List<WindowResult> emitted = new ArrayList<>();
        private final AtomicLong lateEvents = new AtomicLong();
        private long watermarkMillis = Long.MIN_VALUE;
        private long lastClosedBoundary = Long.MIN_VALUE;  // windows below this are emitted

        public TumblingWindowProcessor(Duration window, Duration latenessMargin) {
            this.windowMillis = window.toMillis();
            this.latenessMarginMillis = latenessMargin.toMillis();
        }

        public long windowStart(long ts) { return (ts / windowMillis) * windowMillis; }

        /** The emission cutoff for the watermark: only windows fully before it close. */
        private long emissionCutoff(long watermark) {
            return watermark - latenessMarginMillis - windowMillis + 1;
        }

        /** Ingest one event: accumulate, advance watermark, close eligible windows. */
        public void process(Event e) {
            watermarkMillis = Math.max(watermarkMillis, e.tsMillis());
            long start = windowStart(e.tsMillis());

            if (start < lastClosedBoundary
                    || (!windows.isEmpty() && start < windows.firstKey())) {
                lateEvents.incrementAndGet();          // window already emitted: metered drop
                return;
            }
            windows.computeIfAbsent(start, k -> new WindowAccumulator()).add(e);
            closeWindowsUpTo(emissionCutoff(watermarkMillis));
        }

        /** Explicit close (timer tick in production): windows past the cutoff emit. */
        public List<WindowResult> closeElapsed(Instant now) {
            long cutoff = emissionCutoff(now.toEpochMilli());
            List<WindowResult> results = closeWindowsUpTo(cutoff);
            return results;
        }

        private List<WindowResult> closeWindowsUpTo(long cutoff) {
            List<WindowResult> results = new ArrayList<>();
            Iterator<Map.Entry<Long, WindowAccumulator>> it = windows.headMap(cutoff).entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Long, WindowAccumulator> e = it.next();
                results.add(e.getValue().toResult(e.getKey(), windowMillis));
                lastClosedBoundary = e.getKey();       // track the highest emitted window start
                it.remove();
            }
            emitted.addAll(results);
            return results;
        }

        public int openWindows() { return windows.size(); }
        public long lateEvents() { return lateEvents.get(); }
        public long watermark() { return watermarkMillis; }
        public List<WindowResult> emitted() { return List.copyOf(emitted); }
    }

    public static void main(String[] args) throws InterruptedException {
        long base = Instant.parse("2026-01-01T00:00:00Z").toEpochMilli();
        Duration window = Duration.ofMillis(100);
        Duration margin = Duration.ofMillis(150);

        TumblingWindowProcessor processor = new TumblingWindowProcessor(window, margin);

        // Window 0: two events at t=0 and t=40
        processor.process(new Event(base + 0, "mobile-1", 3.0));
        processor.process(new Event(base + 40, "mobile-2", 5.0));
        System.out.println("after 2 events: open windows=" + processor.openWindows());

        // Window 100: two events at t=120 and t=180
        processor.process(new Event(base + 120, "mobile-1", 2.0));
        processor.process(new Event(base + 180, "mobile-2", 4.0));

        // Advance watermark with an event at t=600: cutoff = 600-150-100+1 = 351
        // -> windows 0 and 100 both close and emit
        List<WindowResult> closed = processor.closeElapsed(Instant.ofEpochMilli(base + 600));
        System.out.println("closed " + closed.size() + " windows on watermark advance:");
        closed.forEach(r -> System.out.printf("  [%d-%d) count=%d sum=%.1f avg=%.2f%n",
                r.windowStart() - base, r.windowEnd() - base, r.count(), r.sum(), r.avg()));

        // Late event: belongs to window 0 (t=90), already emitted -> metered drop
        processor.process(new Event(base + 90, "mobile-3", 7.0));
        System.out.println("late events metered: " + processor.lateEvents());

        // Window 200: an event at t=260; close at watermark t=900 -> emits count=1
        processor.process(new Event(base + 260, "mobile-1", 6.0));
        List<WindowResult> closed2 = processor.closeElapsed(Instant.ofEpochMilli(base + 900));
        System.out.println("closed " + closed2.size() + " more windows on next advance:");
        closed2.forEach(r -> System.out.printf("  [%d-%d) count=%d sum=%.1f avg=%.2f%n",
                r.windowStart() - base, r.windowEnd() - base, r.count(), r.sum(), r.avg()));

        System.out.printf("final: openWindows=%d, emitted=%d, late=%d, watermark advanced to t=%d%n",
                processor.openWindows(), processor.emitted().size(),
                processor.lateEvents(), processor.watermark() - base);
    }
}
```

## Complexity Analysis

| Operation | Time | Space | Notes |
|-----------|------|-------|-------|
| `process` (accumulate) | O(log w) | O(1) | TreeMap lookup/insert; w = open windows |
| `process` (close scan) | O(k log w) amortized | O(1) per window | k = windows closing this watermark step; each closes once |
| `closeElapsed` | O(k log w) | O(k) | headMap range delete + result materialization |
| Open-window memory | — | O(w × 1) | Bounded by window count; windows close on schedule |
| Late-event check | O(1) | O(1) | Comparison against the lowest open window |

**Amortized per-event cost is O(log w)** — each event accumulates once, and each
window is closed exactly once. The watermark stall (no new events) is the only
way memory grows; production adds a processing-time watchdog that force-closes
windows older than a hard bound.

## Edge Cases & Failure Modes

| Scenario | Behavior | Why it's correct |
|----------|----------|------------------|
| Event exactly at a boundary (t=100) | Belongs to window [100, 200) | `floor(ts / size) * size` is unambiguous |
| Event for an already-emitted window | Dropped, `lateEvents++` | Metered policy — visible, tunable, repairable |
| Clock skew (event in the "future") | Window opens ahead of the watermark; closes when the watermark catches up | Event-time semantics tolerate skew by construction |
| Watermark stalls (silent source) | Windows stay open | Production watchdog force-closes past a hard bound + alert |
| Crash mid-window | At-least-once re-emission after checkpoint restore | Downstream dedupes by `windowStart` |
| Two events in one window from one key | Both counted; per-key grouping is a follow-up | Current processor aggregates globally |
| Empty windows | Never emitted (no trigger) | Emission is data-driven; missing windows are a completeness signal, not a bug |

## Verification Walkthrough

1. **Window membership:** events at t=0, 40 land in window [0-100); t=120, 180 in
   [100-200) — event time, not arrival, decides.
2. **Watermark-driven closure:** nothing emits until a watermark advance past
   `end + margin`; the first advance closes windows 0 and 100 together, each
   with the correct count (2) and sum (8.0 / 6.0).
3. **Metered late events:** the t=90 event (window 0, already emitted) does not
   alter the emitted aggregate; `lateEvents` rises to 1 — the policy is
   observable, not silent.
4. **Bounded state:** after the final advance, `openWindows` is 0 and emitted
   count equals closed count — every window emits exactly once.
5. **Determinism:** fixed epoch base + explicit timestamps → reproducible
   aggregation regardless of when the demo runs.

## Operations: Production Shape

- **Sharding:** partition events by key hash; each shard runs this processor;
  epoch-aligned boundaries make partial results mergeable (sum counts, sum sums,
  recompute avg).
- **State + recovery:** open windows persist to a stateful store with periodic
  checkpoints; on crash, restore windows and replay buffered input — the
  at-least-once contract that downstream dedupe absorbs.
- **Repair path:** when the late-event counter crosses a threshold, a bounded
  replay job re-runs affected windows from the raw input log and emits corrected
  results (a correction topic, keyed by window).
- **Watchdog:** processing-time force-close past a hard bound prevents unbounded
  open-window memory when sources go silent.
- **Metrics:** emitted windows/sec, late-event rate, watermark lag
  (`now - watermark`) — watermark lag rising is the leading indicator of a
  stalled pipeline.

## Follow-Up Questions

1. **Sliding windows:** same accumulator, membership in `overlap` windows;
  emission cost multiplies — the tumbling choice is the throughput win.
2. **Dynamic watermarks:** per-source progress (idle signals or SDK-reported
  high-water marks); robust to one slow source at the cost of ingestion-side
  metadata.
3. **Keyed aggregation:** accumulate per (window, key) — the map key becomes a
  composite; merge logic unchanged (associative aggregates).
4. **Exactly-once end-to-end:** transactional sink writes (Kafka transactions or
  idempotent key-value puts keyed by windowStart) + replay-tolerant consumers;
  the processor alone can only promise at-least-once.
5. **Out-of-order within a window:** the accumulator is order-independent for
  count/sum/avg — for min/max or quantiles, buffer-and-sort per window before
  emit.
6. **Triggers beyond watermarks:** Flink-style triggers could emit early partials
  (e.g., "current count at 30 s") followed by the final result — the late policy
  generalizes to per-trigger timing.
7. **Session windows:** replace the aligned start with "gap-since-last-event"
  boundaries and merge overlapping sessions on close — the same accumulator, a
  different partition of the timeline.

## Summary

- **Tumbling windows align event time to fixed epoch boundaries** — every
  processor derives identical windows, making distributed aggregation a merge
  rather than a shuffle.
- **Emission is watermark-driven, not timer-driven:** a window closes when
  observed event time passes `end + latenessMargin` — the margin is measured
  from the data's late tail, trading latency against completeness.
- **Late events are metered, never silent:** dropped from the aggregate, counted,
  and recoverable through the repair path.
- **The contract is at-least-once with a stable window key** (`windowStart`);
  exactly-once is an end-to-end property, scoped to the sink and the consumers.
- **State is bounded by windows, not by stream length** — with a watchdog for
  watermark stalls, the processor is safe under silence as well as flood.
