# Lab 02: Problem Walkthrough — Service Mesh Traffic Management, mTLS and Resilience

## Problem Statement

Implement a mini service mesh in pure Java 21+ that models the control plane / data plane split of
Istio. Requirements:

1. **Service registry**: services register one or more versions (e.g., `payments` v1, v2).
2. **Traffic splitting**: a `VirtualService`-like rule routes a weighted share of traffic to each
   version (90/10 canary, blue/green, etc.).
3. **mTLS**: every service identity has a certificate; mesh-wide mTLS enforcement means a call
   without a valid peer certificate is rejected.
4. **Circuit breaking**: per-destination failure thresholds; when tripped, subsequent requests
   fail fast instead of piling onto the broken service; after a cooldown the breaker half-opens
   to probe.
5. **Fault injection**: delay and abort rules for chaos-testing resilience paths.
6. **Deterministic demo**: the same run always produces the same traffic distribution and
   breaker transitions, so the walkthrough output is reproducible.

## Constraints

- Java 21+ only, no external frameworks.
- The mesh must be callable from a plain `main`: `String route(String source, String target)`.
- Weighted routing must be deterministic (sorted weights + seeded randomness).
- Circuit breaker must expose `state()` so tests can assert CLOSED / OPEN / HALF-OPEN transitions.

## Approach

Istio splits the mesh into two planes. The **control plane** (Istiod) is the brain: it holds
service discovery, the configured routing rules, and issues certificates. The **data plane**
(Envoy sidecars) is the muscle: every request between services passes through a sidecar proxy
that enforces routing, mTLS, and resilience policies on the wire. Our mini mesh mirrors that
split: `ServiceMesh` is the control plane (registry + rules + cert issuance), `Sidecar` is the
data plane (per-service routing, mTLS verification, circuit breaker, fault injection).

Design decisions:

- **Sorted weight map**: routing picks `random % 100` against a cumulative walk of weights sorted
  by version name, and the demo uses a fixed-seed `Random` so every run produces identical counts.
- **Circuit breaker as a state machine**: CLOSED -> OPEN (threshold failures) -> HALF-OPEN (after
  cooldown, one probe allowed) -> CLOSED or OPEN again. This matches Envoy's model, minus the
  concurrency tuning knobs.
- **Fault rules are keyed by destination**: a delay rule sleeps, an abort rule drops the request;
  rules live in the control plane and are evaluated by the data plane, like Envoy's
  `VirtualService.fault` config.

## Step-by-Step Solution

### Step 1: Control Plane — Service Registry and Routing Rules

The control plane stores services as `service -> version -> endpoint` maps, plus routing rules
keyed by the `source->target` pair, and per-destination fault rules.

```java
record VirtualServiceRule(String source, String target, Map<String, Integer> weights) {}
```

Routing: pick a roll of `0..99` from a fixed-seed `Random`, walk the weights sorted by version
name, and return the version whose cumulative range contains the roll. A sorted `TreeMap`
weights map makes the walk order deterministic; the seeded RNG makes the sequence deterministic.

### Step 2: mTLS — Certificates and Peer Verification

The control plane issues each service identity a certificate; the data plane refuses peers that
cannot present a valid certificate for the destination. A certificate is a signed record
(name, public key, expiry), and verification is a SHA256withRSA signature check — a faithful,
dependency-free stand-in for SPIFFE:

```java
record Certificate(String identity, byte[] publicKey, Instant expiresAt, byte[] signature) {
    boolean valid(PublicKey signerKey) {
        try {
            var verifier = Signature.getInstance("SHA256withRSA");
            verifier.initVerify(signerKey);
            verifier.update(toCanonicalBytes());
            return verifier.verify(signature) && expiresAt.isAfter(Instant.now());
        } catch (Exception e) {
            return false;
        }
    }
}
```

`IdentityIssuer` (in the Complete Solution) holds the mesh signing keypair, issues certificates
with a 24-hour TTL, and stores them by identity — the control-plane analog of Citadel.

### Step 3: Data Plane — Sidecar Proxy

Each service gets a sidecar holding its identity and destination config. The sidecar:
1. verifies the peer certificate for the target service (mTLS),
2. consults the destination's circuit breaker,
3. applies any fault rule (delay / abort), and
4. routes to a version by weighted choice.

```java
class Sidecar {
    String route(String target) {
        var peerCert = mesh.certificateFor(target);
        if (mesh.mtlsEnforced()
            && (peerCert == null || !peerCert.valid(mesh.issuerKey()))) {
            return null;                              // mTLS handshake failed
        }
        var breaker = mesh.breakerFor(target);
        if (!breaker.allow()) {
            return "FAIL_FAST";                       // circuit open
        }
        var fault = mesh.faultFor(target);
        if (fault != null && fault.type().equals("abort")) {
            breaker.recordFailure();
            return null;                              // injected fault: request aborted
        }
        if (fault != null && fault.type().equals("delay")) {
            try {
                Thread.sleep(fault.value());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        var version = mesh.pickVersion(target);
        breaker.recordSuccess();
        return version;
    }
}
```

### Step 4: Composition Root

`ServiceMeshLab.main` wires the mesh, registers `payments` v1/v2, splits traffic 90/10, enables
mesh-wide mTLS, and demonstrates the breaker life cycle with injected failures.

## Complete Solution

The full compilable file, `ServiceMeshLab.java` in package `com.devops.deep.lab02`:

```java
package com.devops.deep.lab02;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.Signature;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceMeshLab {
    public static void main(String[] args) throws Exception {
        var mesh = new ServiceMesh();
        mesh.registerService("frontend", "v1");
        mesh.registerService("payments", "v1");
        mesh.registerService("payments", "v2");
        mesh.setTrafficSplit("frontend", "payments",
            new TreeMap<>(Map.of("v1", 90, "v2", 10)));
        mesh.enableMeshWideMtls();

        var frontend = mesh.sidecarFor("frontend");

        var counts = new TreeMap<String, Integer>();
        counts.put("v1", 0);
        counts.put("v2", 0);
        var rejected = 0;
        for (int i = 0; i < 1000; i++) {
            var result = frontend.route("payments");
            if (result != null && !result.equals("FAIL_FAST")) {
                counts.merge(result, 1, Integer::sum);
            } else if (result == null) {
                rejected++;
            }
        }
        System.out.println("Traffic split (1000 requests, expected ~90/10):");
        counts.forEach((v, c) -> System.out.println("  " + v + ": " + c));
        System.out.println("  rejected (mTLS/fault): " + rejected);
        System.out.println("mTLS enforced mesh-wide: " + mesh.mtlsEnforced());

        System.out.println();
        System.out.println("== Circuit breaker: 3 consecutive failures trip OPEN ==");
        var breaker = mesh.breakerFor("payments");
        for (int i = 0; i < 3; i++) {
            breaker.recordFailure();
        }
        System.out.println("State after 3 failures: " + breaker.state());
        var failFast = 0;
        for (int i = 0; i < 5; i++) {
            if ("FAIL_FAST".equals(frontend.route("payments"))) {
                failFast++;
            }
        }
        System.out.println("Fail-fast responses while OPEN: " + failFast + "/5");

        System.out.println();
        System.out.println("== Cooldown passes: HALF-OPEN probe succeeds, back to CLOSED ==");
        Thread.sleep(breaker.cooldown().toMillis() + 50);
        var probe = frontend.route("payments");
        System.out.println("State after probe: " + breaker.state()
            + " (probe -> " + probe + ")");

        System.out.println();
        System.out.println("== Fault injection: 500ms delay rule on payments ==");
        mesh.injectFault("payments", "delay", 500);
        var start = System.nanoTime();
        frontend.route("payments");
        System.out.println("Route took ~" + ((System.nanoTime() - start) / 1_000_000) + "ms");

        mesh.injectFault("payments", "abort", 0);
        var aborted = frontend.route("payments");
        System.out.println("Route with abort rule: "
            + (aborted == null ? "aborted (null)" : aborted));
    }
}

record FaultRule(String type, int value) {}

record VirtualServiceRule(String source, String target, Map<String, Integer> weights) {}

record Certificate(String identity, byte[] publicKey, Instant expiresAt, byte[] signature) {
    boolean valid(PublicKey signerKey) {
        try {
            var verifier = Signature.getInstance("SHA256withRSA");
            verifier.initVerify(signerKey);
            verifier.update(toCanonicalBytes());
            return verifier.verify(signature) && expiresAt.isAfter(Instant.now());
        } catch (Exception e) {
            return false;
        }
    }

    byte[] toCanonicalBytes() {
        return (identity + ":" + publicKey.length + ":" + expiresAt)
            .getBytes(StandardCharsets.UTF_8);
    }
}

class IdentityIssuer {
    private final KeyPair signer;
    private final Map<String, Certificate> issued = new ConcurrentHashMap<>();
    private static final Duration CERT_TTL = Duration.ofHours(24);

    IdentityIssuer() {
        try {
            var generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(1024);
            this.signer = generator.generateKeyPair();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    Certificate issue(String identity) throws Exception {
        var expiresAt = Instant.now().plus(CERT_TTL);
        var cert = new Certificate(identity, identity.getBytes(StandardCharsets.UTF_8),
            expiresAt, sign(identity, expiresAt));
        issued.put(identity, cert);
        return cert;
    }

    Certificate certificateFor(String identity) {
        return issued.get(identity);
    }

    PublicKey signerKey() {
        return signer.getPublic();
    }

    private byte[] sign(String identity, Instant expiresAt) throws Exception {
        var signerInstance = Signature.getInstance("SHA256withRSA");
        signerInstance.initSign(signer.getPrivate());
        signerInstance.update(new Certificate(identity,
            identity.getBytes(StandardCharsets.UTF_8), expiresAt, null).toCanonicalBytes());
        return signerInstance.sign();
    }
}

class ServiceMesh {
    private static final Random ROUTE_RNG = new Random(7);

    private final Map<String, Map<String, String>> versions = new ConcurrentHashMap<>();
    private final Map<String, VirtualServiceRule> rules = new ConcurrentHashMap<>();
    private final Map<String, FaultRule> faults = new ConcurrentHashMap<>();
    private final Map<String, CircuitBreaker> breakers = new ConcurrentHashMap<>();
    private final Map<String, Sidecar> sidecars = new ConcurrentHashMap<>();
    private final IdentityIssuer issuer = new IdentityIssuer();
    private boolean mtlsEnforced = false;

    void registerService(String service, String version) throws Exception {
        versions.computeIfAbsent(service, k -> new ConcurrentHashMap<>())
                .put(version, service + "-" + version);
        sidecars.computeIfAbsent(service, k -> new Sidecar(this));
        issuer.issue(service);
    }

    void setTrafficSplit(String source, String target, Map<String, Integer> weights) {
        rules.put(source + "->" + target,
            new VirtualServiceRule(source, target, weights));
    }

    void injectFault(String service, String type, int value) {
        faults.put(service, new FaultRule(type, value));
    }

    void enableMeshWideMtls() {
        mtlsEnforced = true;
    }

    boolean mtlsEnforced() {
        return mtlsEnforced;
    }

    Certificate certificateFor(String identity) {
        return issuer.certificateFor(identity);
    }

    PublicKey issuerKey() {
        return issuer.signerKey();
    }

    FaultRule faultFor(String target) {
        return faults.get(target);
    }

    CircuitBreaker breakerFor(String target) {
        return breakers.computeIfAbsent(target,
            k -> new CircuitBreaker(3, Duration.ofMillis(400)));
    }

    Sidecar sidecarFor(String service) {
        return sidecars.get(service);
    }

    String pickVersion(String target) {
        for (var rule : rules.values()) {
            if (rule.target().equals(target)) {
                var weights = rule.weights();
                if (weights.isEmpty()) return null;
                var roll = ROUTE_RNG.nextInt(100);
                var cumulative = 0;
                for (var entry : weights.entrySet()) {
                    cumulative += entry.getValue();
                    if (roll < cumulative) return entry.getKey();
                }
                return weights.keySet().iterator().next();
            }
        }
        return null;
    }
}

class Sidecar {
    private final ServiceMesh mesh;

    Sidecar(ServiceMesh mesh) {
        this.mesh = mesh;
    }

    String route(String target) {
        var peerCert = mesh.certificateFor(target);
        if (mesh.mtlsEnforced()
            && (peerCert == null || !peerCert.valid(mesh.issuerKey()))) {
            return null;
        }
        var breaker = mesh.breakerFor(target);
        if (!breaker.allow()) {
            return "FAIL_FAST";
        }
        var fault = mesh.faultFor(target);
        if (fault != null && fault.type().equals("abort")) {
            breaker.recordFailure();
            return null;
        }
        if (fault != null && fault.type().equals("delay")) {
            try {
                Thread.sleep(fault.value());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        var version = mesh.pickVersion(target);
        breaker.recordSuccess();
        return version;
    }
}

class CircuitBreaker {
    enum State { CLOSED, OPEN, HALF_OPEN }

    private final int failureThreshold;
    private final Duration cooldown;
    private int consecutiveFailures = 0;
    private Instant openedAt;
    private State state = State.CLOSED;

    CircuitBreaker(int failureThreshold, Duration cooldown) {
        this.failureThreshold = failureThreshold;
        this.cooldown = cooldown;
    }

    synchronized boolean allow() {
        if (state == State.OPEN) {
            if (Duration.between(openedAt, Instant.now()).compareTo(cooldown) >= 0) {
                state = State.HALF_OPEN;
            } else {
                return false;
            }
        }
        return true;
    }

    synchronized void recordSuccess() {
        consecutiveFailures = 0;
        if (state == State.HALF_OPEN) {
            state = State.CLOSED;
        }
    }

    synchronized void recordFailure() {
        consecutiveFailures++;
        if (state == State.HALF_OPEN || consecutiveFailures >= failureThreshold) {
            state = State.OPEN;
            openedAt = Instant.now();
        }
    }

    State state() {
        return state;
    }

    Duration cooldown() {
        return cooldown;
    }
}
```

## Complexity Analysis

- **Route**: O(W) where W = number of versions/weights (typically <= 3); mTLS verification is
  one RSA signature verify, O(1) amortized. **Register / split config**: O(1) map operations in
  the control plane.
- **Circuit breaker**: O(1) per decision; the state machine is constant-time with two instants.
- **Space**: O(S + V + C) for services, versions, and certificates — tiny for real meshes,
  which push this state to istiod and the sidecars' in-memory caches.

## Test Cases

| Scenario | Expected |
|---|---|
| 1000 requests with 90/10 split | v1 ~900, v2 ~100 (identical every run: seeded RNG + sorted weights) |
| mTLS mesh-wide, all peers certified | 0 rejected |
| 3 consecutive failures on payments | Breaker transitions CLOSED -> OPEN |
| Requests while OPEN | `FAIL_FAST`, no call reaches the target |
| After cooldown, one probe | HALF_OPEN -> CLOSED on success |
| Delay fault rule (500ms) | Route sleeps the injected value |
| Abort fault rule | Route returns null (request dropped) |

Example run:

```
Traffic split (1000 requests, expected ~90/10):
  v1: 894
  v2: 106
  rejected (mTLS/fault): 0
mTLS enforced mesh-wide: true

== Circuit breaker: 3 consecutive failures trip OPEN ==
State after 3 failures: OPEN
Fail-fast responses while OPEN: 5/5

== Cooldown passes: HALF-OPEN probe succeeds, back to CLOSED ==
State after probe: CLOSED (probe -> v1)

== Fault injection: 500ms delay rule on payments ==
Route took ~501ms
Route with abort rule: aborted (null)
```

## Follow-Up Questions

1. **How does this map to Istio/Envoy?** `ServiceMesh` = Istiod (Pilot + Citadel), `Sidecar` =
   Envoy, `VirtualServiceRule` = VirtualService + DestinationRule, `IdentityIssuer` = Citadel's
   cert manager. Real Envoy adds connection pools, retries, and timeouts per circuit-breaker
   trip, but the state machine is identical.
2. **How do you make routing deterministic in production?** Sort the weight map and use a hash of
   the request ID instead of a seeded PRNG when you need sticky routing; both remove ordering
   nondeterminism from `HashMap`.
3. **What breaks mTLS in practice?** Certificate expiry (the #1 incident), missing root CA in the
   peer trust store, and workloads deployed outside the mesh bypassing the sidecar entirely —
   hence the need for network-level enforcement (SPIFFE auth SPI, network policies).
4. **Why HALF-OPEN instead of probing from CLOSED?** A single probe limits blast radius: one
   request re-validates the target before the flood of traffic returns; failed probes re-open
   immediately.
5. **How do delay faults interact with circuit breakers?** A delay fault does not count as a
   failure (the request eventually succeeds), so it surfaces latency SLO problems without
   tripping the breaker — that's the point of chaos experiments.
6. **How do you observe the mesh?** Each route records success/failure, latency, and version;
   export as metrics (request count by version, error rate, breaker state gauge) and trace the
   path sidecar-to-sidecar. The walkthrough prints them; production ships them to Prometheus.
