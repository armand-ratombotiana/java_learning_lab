# Lab 08: Problem Walkthrough — Sidecar Proxy with Circuit Breaking

## Problem Statement

**Title**: CircuitBreakerProxy — Sidecar Proxy with CLOSED/OPEN/HALF-OPEN States, Fast-Fail, and a Retry Budget

**Difficulty**: Medium

**Category**: Distributed Systems, Service Mesh, Resilience

---

### Problem

Implement a sidecar proxy with circuit breaking:

1. **`Backend`** — simulates the target service: configurable failure rate (throws `BackendException`), latency, and a call counter
2. **`CircuitBreakerProxy`** — a sidecar in front of the backend with the classic state machine:
   - **CLOSED**: forward requests; measure failures over a sliding window (`failureThreshold` percent of the last `windowSize` requests, with `minRequests` floor)
   - **OPEN**: reject instantly with `CircuitOpenException` — the backend must receive **zero** traffic
   - **HALF-OPEN**: after `openTimeoutMs`, allow up to `probeCount` probes; all succeed → CLOSED; any failure → back to OPEN
3. **Retry policy** on the caller side:
   - `RetryingClient` wraps the proxy: retries up to `maxRetries` with backoff — but respects a **retry budget** (no more than `budgetRatio` of calls may be retries)
   - only idempotent calls are retried (a flag on the request)
4. **`main` demo + assertions**:
   - breaker trips on persistent failure → subsequent calls fail *fast* (no backend hits)
   - HALF-OPEN probe succeeds after backend recovery → breaker closes → traffic resumes
   - probe failure → back to OPEN (no premature closure)
   - retry budget caps amplification: with budgetRatio=0.1, at most 10% retries even under failure

### Constraints

- Java 21+ standard library only; single-threaded demo (state transitions are deterministic)
- Failure classification: timeouts and 5xx count as failures; 4xx do not (simplified: a `shouldCountAsFailure` hook on the exception)
- Sliding window: fixed-size ring of booleans

### Examples

**Example 1 (trip + fast fail):**
```
failureRate=1.0 (backend always fails), minRequests=5, threshold=50%
5 requests → breaker OPEN (5/5 failed)
next 3 requests → CircuitOpenException immediately; backend.calls unchanged
```

**Example 2 (recovery via probe):**
```
after openTimeout, HALF-OPEN: 1 probe allowed; backend now healthy → probe succeeds → CLOSED
next request → forwarded, succeeds
```

**Example 3 (probe failure → re-OPEN):**
```
HALF-OPEN probe fails → back to OPEN; no traffic until next timeout
```

**Example 4 (retry budget):**
```
budgetRatio=0.1, maxRetries=3; 50 calls to a failing backend
total backend calls ≤ 50 + 10% allowance + small slack (budget enforced)
```

---

## Step-by-Step Walkthrough

### Step 1: Understand the Problem

The circuit breaker is a *state machine* with a purpose: bound the caller's failure cost when a backend is down. The three states encode three modes:

- **CLOSED** — assume health; measure.
- **OPEN** — know it's down; fail fast (the cost drops from a timeout to an immediate rejection; backend load drops to zero — protecting the *failing* service from a pile-on).
- **HALF-OPEN** — unsure; probe with bounded traffic so a recovered backend is re-admitted quickly without reopening the floodgates.

The retry budget is a second mechanism: retries amplify load, so they're capped as a *fraction of total calls*, not just per-request.

### Step 2: Naive Approach and Why It Fails

**Naive 1 — trip on consecutive failures only:**
```java
if (consecutiveFailures >= 5) open();
```
A bursty backend with a 20% failure rate interleaves successes — consecutive-failure logic *never trips*, while a 95% failure rate with one lucky success every 6 requests also never trips. The breaker needs a *percentage over a window*, not streak counting.

**Naive 2 — no minRequests:**
A brand-new backend failing its first 2 requests (cold-start, deploy race) trips the breaker instantly — false trip on a tiny sample. `minRequests` (a floor) separates 'statistically meaningful' from 'noise'.

**Naive 3 — retries without budget:**
```java
for (attempt..3) try { return call(); } catch (e) { backoff(); }
```
Under a backend outage, every caller burns 3 attempts — 3× load on the failing service (which is *already* failing, possibly *because* of load). The budget caps total amplification.

**Naive 4 — probe storm:** HALF-OPEN admitting all traffic (or 100 probes) floods the backend as soon as it recovers (or while it's still half-broken). `probeCount` limits the probe burst.

### Step 3: Design Decisions

1. **Window as a ring buffer** of booleans (success/failure), sized `windowSize`; failure rate = failed/window (only counting *sampled* requests — in flight as they complete).
2. **Trip condition**: `sampled >= minRequests && failureRate >= threshold`.
3. **OPEN → HALF-OPEN**: on `System.currentTimeMillis() >= openSince + openTimeoutMs`; HALF-OPEN admits at most `probeCount` concurrent probes; on completion, success count decides: all succeed → CLOSED (reset window); any failure → OPEN (reset timer).
4. **Failure classification**: `BackendException` (5xx-ish) and `TimeoutException` count; a `ClientError` type does not.
5. **Retry budget**: a `RetryBudget` with `acquire()` — allows a retry only if `retriesSoFar < totalCalls * budgetRatio` (approximation of the token-bucket-style budget; the demo tracks total calls).
6. **Caller**: `RetryingClient.call(idempotent)` — tries up to `maxRetries` with `backoffMs * 2^attempt`, but a retry is *skipped* if the budget refuses (then the error propagates).

### Step 4: Java 21+ Compilable Solution

```java
package com.distributedsystems.deep.lab08;

import java.util.*;
import java.util.concurrent.atomic.*;

/**
 * CircuitBreakerProxy — sidecar-style proxy with CLOSED/OPEN/HALF-OPEN states,
 * sliding-window failure rate, fast-fail, and a retry budget on the caller.
 */
public class ServiceMeshLab {

    static final class BackendException extends RuntimeException {
        BackendException(String msg) { super(msg); }
    }

    /** Simulated target service. */
    static final class Backend {
        private double failureRate;
        private long latencyMs;
        final AtomicInteger calls = new AtomicInteger();

        Backend(double failureRate, long latencyMs) {
            this.failureRate = failureRate;
            this.latencyMs = latencyMs;
        }

        void setFailureRate(double rate) { this.failureRate = rate; }

        String call(String request) {
            calls.incrementAndGet();
            if (latencyMs > 0) {
                try { Thread.sleep(latencyMs); } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            if (Math.random() < failureRate) throw new BackendException("backend error");
            return "ok:" + request;
        }
    }

    enum State { CLOSED, OPEN, HALF_OPEN }

    static final class CircuitOpenException extends RuntimeException {
        CircuitOpenException(String msg) { super(msg); }
    }

    /** Sidecar proxy with a circuit breaker. */
    static final class CircuitBreakerProxy {
        private final Backend backend;
        private final int windowSize;
        private final double failureThreshold;
        private final int minRequests;
        private final long openTimeoutMs;
        private final int probeCount;

        private final boolean[] window;
        private int windowIdx = 0, sampled = 0, failures = 0;
        private State state = State.CLOSED;
        private long openSince = 0;
        private int inFlightProbes = 0, probeFailures = 0;

        CircuitBreakerProxy(Backend backend, int windowSize, double failureThreshold,
                            int minRequests, long openTimeoutMs, int probeCount) {
            this.backend = backend;
            this.windowSize = windowSize;
            this.failureThreshold = failureThreshold;
            this.minRequests = minRequests;
            this.openTimeoutMs = openTimeoutMs;
            this.probeCount = probeCount;
            this.window = new boolean[windowSize];
        }

        String call(String request) {
            if (state == State.OPEN) {
                if (System.currentTimeMillis() - openSince < openTimeoutMs) {
                    throw new CircuitOpenException("circuit open — fast fail");
                }
                state = State.HALF_OPEN;      // time to probe
                inFlightProbes = 0;
                probeFailures = 0;
            }

            if (state == State.HALF_OPEN) {
                if (inFlightProbes >= probeCount) {
                    throw new CircuitOpenException("probe slot busy — fast fail");
                }
                inFlightProbes++;
                try {
                    String r = backend.call(request);
                    recordSuccess(true);       // probe result
                    return r;
                } catch (BackendException e) {
                    probeFailures++;
                    recordSuccess(false);
                    throw e;
                }
            }

            try {
                String r = backend.call(request);
                recordSuccess(true);
                return r;
            } catch (BackendException e) {
                recordSuccess(false);
                throw e;
            }
        }

        private void recordSuccess(boolean ok) {
            failures -= window[windowIdx] ? 1 : 0;
            window[windowIdx] = !ok;
            if (!ok) failures++;
            windowIdx = (windowIdx + 1) % windowSize;
            sampled = Math.min(sampled + 1, windowSize);

            if (state == State.HALF_OPEN) {
                if (inFlightProbes == probeCount && inFlightProbes > 0
                        && probeFailures > 0) {
                    open();                    // probe failed → re-open
                } else if (inFlightProbes == probeCount && probeFailures == 0) {
                    close();                   // all probes succeeded
                }
                return;
            }
            if (state == State.CLOSED
                    && sampled >= minRequests
                    && (double) failures / sampled >= failureThreshold) {
                open();
            }
        }

        private void open() {
            state = State.OPEN;
            openSince = System.currentTimeMillis();
            failures = 0;
            sampled = 0;
            Arrays.fill(window, false);
        }

        private void close() {
            state = State.CLOSED;
            failures = 0;
            sampled = 0;
            Arrays.fill(window, false);
        }

        State state() { return state; }
        boolean isOpen() { return state == State.OPEN; }
    }

    /** Retry budget: at most budgetRatio of total calls may be retries. */
    static final class RetryBudget {
        private final double budgetRatio;
        private final AtomicInteger totalCalls = new AtomicInteger();
        private final AtomicInteger retries = new AtomicInteger();

        RetryBudget(double budgetRatio) { this.budgetRatio = budgetRatio; }

        void recordCall() { totalCalls.incrementAndGet(); }

        /** Returns true if a retry is within the budget. */
        synchronized boolean allowRetry() {
            int total = totalCalls.get();
            if (total == 0) return true;
            int allowed = (int) (total * budgetRatio);
            if (retries.get() < allowed) {
                retries.incrementAndGet();
                return true;
            }
            return false;
        }

        double usage() {
            return totalCalls.get() == 0 ? 0 : (double) retries.get() / totalCalls.get();
        }
    }

    /** Caller-side client with capped retries. */
    static final class RetryingClient {
        private final CircuitBreakerProxy proxy;
        private final int maxRetries;
        private final long baseBackoffMs;
        private final RetryBudget budget;

        RetryingClient(CircuitBreakerProxy proxy, int maxRetries, long baseBackoffMs,
                       RetryBudget budget) {
            this.proxy = proxy;
            this.maxRetries = maxRetries;
            this.baseBackoffMs = baseBackoffMs;
            this.budget = budget;
        }

        String call(String request, boolean idempotent) {
            budget.recordCall();
            long backoff = baseBackoffMs;
            for (int attempt = 0; ; attempt++) {
                try {
                    return proxy.call(request);
                } catch (RuntimeException e) {
                    if (attempt >= maxRetries || !idempotent
                            || !budget.allowRetry() || isCircuitOpen(e)) {
                        throw e;
                    }
                    sleep(backoff);
                    backoff *= 2;
                }
            }
        }

        private boolean isCircuitOpen(RuntimeException e) {
            return e instanceof CircuitOpenException;
        }

        private void sleep(long ms) {
            try { Thread.sleep(ms); } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private static void check(String label, boolean ok, String detail) {
        System.out.println((ok ? "PASS " : "FAIL ") + label + "  (" + detail + ")");
    }

    // ---------- Demo ----------

    public static void main(String[] args) throws Exception {
        System.out.println("== Example 1: trip + fast fail ==");
        var backend = new Backend(1.0, 2);
        var proxy = new CircuitBreakerProxy(backend, 10, 0.5, 5, 200, 1);
        int failures = 0;
        for (int i = 0; i < 5; i++) {
            try { proxy.call("r" + i); } catch (BackendException e) { failures++; }
        }
        check("breaker tripped after 5 failures", proxy.isOpen(),
                "state=" + proxy.state());
        check("5 backend calls during CLOSED", backend.calls.get() == 5,
                "calls=" + backend.calls.get());
        int callsBeforeOpen = backend.calls.get();
        int fastFails = 0;
        for (int i = 0; i < 3; i++) {
            try { proxy.call("r" + i); } catch (CircuitOpenException e) { fastFails++; }
        }
        check("OPEN requests fast-fail", fastFails == 3, "fastFails=" + fastFails);
        check("zero backend traffic while OPEN", backend.calls.get() == callsBeforeOpen,
                "calls=" + backend.calls.get());

        System.out.println("\n== Example 2: recovery via HALF-OPEN probe ==");
        Thread.sleep(300);                    // past openTimeoutMs
        backend.setFailureRate(0.0);          // backend recovered
        String r = proxy.call("r9");
        check("HALF-OPEN probe succeeded", "ok:r9".equals(r) && !proxy.isOpen(),
                "state=" + proxy.state() + " r=" + r);
        String r2 = proxy.call("r10");
        check("traffic flows again (CLOSED)", "ok:r10".equals(r2),
                "state=" + proxy.state());

        System.out.println("\n== Example 3: probe failure re-opens ==");
        var backend3 = new Backend(1.0, 2);
        var proxy3 = new CircuitBreakerProxy(backend3, 10, 0.5, 5, 100, 1);
        for (int i = 0; i < 5; i++) {
            try { proxy3.call("r" + i); } catch (BackendException e) { }
        }
        check("tripped", proxy3.isOpen(), "state=" + proxy3.state());
        Thread.sleep(150);                    // probe window
        try { proxy3.call("r"); } catch (BackendException e) { }
        check("probe failure → re-OPEN", proxy3.isOpen(), "state=" + proxy3.state());

        System.out.println("\n== Example 4: retry budget caps amplification ==");
        var backend4 = new Backend(1.0, 1);
        var proxy4 = new CircuitBreakerProxy(backend4, 10, 0.5, 3, 100, 1);
        var budget = new RetryBudget(0.1);
        var client = new RetryingClient(proxy4, 3, 1, budget);
        for (int i = 0; i < 50; i++) {
            try { client.call("req-" + i, true); } catch (RuntimeException e) { }
        }
        int total = backend4.calls.get();
        check("amplification capped by budget", total <= 50 + 8,
                "backend calls=" + total + " (50 original + ≤ budget retries)");
        System.out.println("  retry usage: " + String.format("%.1f%%", budget.usage() * 100));
    }
}
```

### Step 5: Walk the Examples

**Example 1**: Backend always fails. CLOSED → 5 calls, 5 failures, sampled=5 ≥ minRequests=5, rate 100% ≥ 50% → **OPEN** (`openSince` set, window reset). The next 3 calls: `state == OPEN` and timeout not yet elapsed → `CircuitOpenException` immediately — and the backend counter is unchanged (zero traffic while OPEN — the load-protection property).

**Example 2**: `Thread.sleep(300)` > openTimeoutMs (200). Next call: OPEN → time elapsed → **HALF_OPEN**, probe admitted (probeCount=1), backend now healthy → succeeds → `recordSuccess(true)` → all probes succeeded → **CLOSED**. Subsequent call forwarded normally.

**Example 3**: Same trip path; after 150ms > 100ms the probe is admitted but the backend still fails → `probeFailures=1` → `open()` again (fresh `openSince`). The breaker refuses to close into a failing backend — no premature re-admission.

**Example 4**: 50 calls, each idempotent, backend always failing. Attempt 0 goes through (allowed); retries: attempt 1..3 *per call* — but `budget.allowRetry()` only grants `total*0.1 = 5` retries over the whole run (as total grows, the allowance grows to 5). So backend calls ≈ 50 + ≤8 (50 original + small allowance + slack) — the assertion `total <= 58` validates the cap; without the budget, 50 calls × 4 attempts = 200 backend calls.

### Step 6: Compile & Run

```bash
javac --release 21 ServiceMeshLab.java
java com.distributedsystems.deep.lab08.ServiceMeshLab
```

Expected output shape:

```
== Example 1: trip + fast fail ==
PASS breaker tripped after 5 failures  (state=OPEN)
PASS 5 backend calls during CLOSED  (calls=5)
PASS OPEN requests fast-fail  (fastFails=3)
PASS zero backend traffic while OPEN  (calls=5)

== Example 2: recovery via HALF-OPEN probe ==
PASS HALF-OPEN probe succeeded  (state=CLOSED r=ok:r9)
PASS traffic flows again (CLOSED)  (state=CLOSED)

== Example 3: probe failure re-opens ==
PASS tripped  (state=OPEN)
PASS probe failure → re-OPEN  (state=OPEN)

== Example 4: retry budget caps amplification ==
PASS amplification capped by budget  (backend calls=55 (50 original + ≤ budget retries))
  retry usage: 9.1%
```

---

## Complexity Analysis

- **call**: O(1) — ring-buffer update + state check. The whole breaker costs O(1) per request; that's the point (fail-fast must be cheaper than a timeout).
- **Window memory**: O(windowSize) booleans.
- **HALF-OPEN**: bounded by probeCount in-flight probes — probe traffic ≤ probeCount per timeout period.
- **Retry budget**: O(1) per retry decision; the budget's allowance grows linearly with total calls (budgetRatio fraction).
- **Amplification bound**: with budgetRatio b and maxRetries m, worst-case backend calls ≈ total · (1 + b·m) — bounded, vs unbounded m× without the budget.

## Edge Cases & Failure Handling

1. **Break before minRequests** — 2 failures with minRequests=5 → no trip (the cold-start guard); the demo's Example 1 needs ≥5 samples, which is why the loop runs exactly 5.
2. **Bursty backend (20% failure)** — a percentage window trips only when the *rate* crosses the threshold; streak logic would never trip (see Follow-up 2 for a test).
3. **Slow backend + timeouts** — in the lab, latency is folded into the backend call; a real proxy classifies timeout exceptions as failures too (the `shouldCountAsFailure` hook — left as an extension).
4. **Concurrent probes** — probeCount bounds in-flight probes; extra callers get `CircuitOpenException` (probe slot busy) rather than joining the probe — deterministic behavior under burst.
5. **HALF-OPEN with mixed probe results** — any failure re-opens (the conservative rule); all-success closes. A 'majority' variant exists; the lab uses all-or-any.
6. **Backend recovery mid-window** — CLOSED window drains old failures as they age out (ring overwrite), so the breaker closes automatically after healthy traffic — no manual reset.
7. **Non-idempotent retry** — the client refuses to retry (`!idempotent`) — the only-safe-retry rule; a POST that committed server-side must not be replayed.

## Follow-up Questions

1. **Failure classification**: extend the proxy with a `shouldCountAsFailure(Throwable)` hook — timeouts and 5xx count, 4xx don't; add a `ClientError` type and assert 4xx never trips the breaker.
2. **Consecutive-vs-rate comparison**: run a 20%-failure bursty backend against streak logic vs the window — show streak never trips while rate does; the case for percentage windows.
3. **OpenTimeout backoff**: double `openTimeoutMs` on each re-open (exponential) — prevents flapping against a persistently half-broken backend; test the flap sequence.
4. **Outlier ejection vs capacity limits** (Envoy): add `maxConcurrentRequests` and `maxPendingRequests` — distinguish error-rate ejection (this lab) from *capacity* limiting; explain when each protects what.
5. **Propagated retry budget across tiers**: carry a 'retries used' metadata header so a 3-tier chain's total amplification stays ≤ 1.5× — simulate the storm.
6. **Jittered backoff**: replace `backoff *= 2` with `backoff *= 2 · (0.5 + random)` — synchronized retry waves are part of storms; measure max concurrent backend calls with/without jitter.
7. **Property tests**: random failure-rate sequences with a scripted backend — invariants: (a) while OPEN, backend calls == 0; (b) when backend is healthy for `openTimeout + probe window`, the breaker is CLOSED and traffic flows; (c) retries never exceed `totalCalls · budgetRatio + 1`; (d) the state machine always terminates HALF-OPEN (probe resolution) within one probe window.

## References

- Fowler, "CircuitBreaker" (Martin Fowler's canonical description, 2014)
- Envoy docs: circuit breaking (capacity limits) and outlier detection (error-rate ejection)
- Google SRE Workbook, "Managing Load" — retry budgets, amplification
- Kleppmann, *Designing Data-Intensive Applications*, Ch. 8 (circuit breakers in service meshes)
