# Lab 01: Problem Walkthrough — GitOps Reconciler with Drift Detection

## Problem Statement

Implement a GitOps reconciler for a Kubernetes-style cluster. Requirements:

1. **Git is the source of truth**: the desired state lives in a "git repository" (in-memory manifest store).
2. **Reconciliation loop**: a reconciler periodically compares desired state against actual cluster state.
3. **Drift detection**: any difference between desired and actual must be classified — `ADDED` (in git, not in cluster), `MISSING` (in cluster, not in git), or `MODIFIED` (in both, but spec differs).
4. **Sync policies**: `AUTO` applies the drift automatically; `MANUAL` only reports it; `PHASED` applies in waves (CRDs -> namespaces -> apps).
5. **Drift report**: each cycle emits a structured report with the diff per resource.

## Constraints

- Java 21+ only.
- Resource identity is `kind/name/namespace`; the reconciler must handle renames and deletions.
- Comparing specs must be order-independent (use a canonical checksum, not `equals` on mutable maps).
- The reconciler must be safe to run repeatedly (idempotent) — applying an already-synced state changes nothing.

## Approach

GitOps in one loop: **compare -> classify -> reconcile**. The reconciler holds three collaborators:

| Component | Role |
|---|---|
| `GitRepository` | Desired state; emulates `git pull` (returns a snapshot with a commit hash) |
| `ClusterState` | Actual state; emulates kubectl apply/delete |
| `GitOpsReconciler` | The control loop: diff + sync policy + report |

Key design decisions:

- **Canonical checksum** for specs: serialize the spec map sorted by key and hash it. Order-insensitive comparison means a spec reordering in git doesn't count as drift.
- **Diff by identity key**: `kind + "/" + namespace + "/" + name` — exactly how kube-apiserver addresses resources.
- **Reconcile cycle = `desired` vs `actual`**, both captured at the same instant for consistency.
- **Manual policy still detects drift** — it just doesn't act; that's how GitOps supports approvals (sync waves, PR-approval flows).

## Step-by-Step Solution

### Step 1: Resource Model

The resource carries identity (`kind/name/namespace`) plus a spec map:

```java
record Resource(String kind, String name, String namespace, Map<String, Object> spec) {
    String identity() {
        return kind + "/" + (namespace == null ? "" : namespace) + "/" + name;
    }
}
```
### Step 2: Spec Checksum (Canonical Serialization)

Sorting keys recursively gives an order-independent fingerprint.

A spec map is serialized with keys sorted at every nesting level, then hashed
with SHA-256. Key ordering inside the map no longer matters: two specs that
differ only in insertion order produce the same digest.

A spec is fingerprinted with a canonical SHA-256 digest: keys sorted at every
nesting level, flattened to `key=value;` pairs. This makes comparison
order-independent — two specs differing only in insertion order are equal ?
and cheap to store alongside each resource. The full `SpecDigest`
implementation (including nested-map recursion) is in the Complete Solution.
### Step 3: Git Repository and Cluster State

Both expose a snapshot of their resources keyed by identity.

Both sides expose a snapshot of their resources keyed by identity. The git
repository additionally tracks the commit hash it is synced to; the cluster
state can also be mutated directly to simulate human/agent drift.

Both sides expose a snapshot of their resources keyed by identity. The git
repository tracks the commit hash it is synced to; the cluster state can be
mutated directly to simulate human/agent drift. Their full implementations
appear in the Complete Solution below — both are thin `ConcurrentHashMap`
wrappers with `snapshot()` returning an immutable copy.
### Step 4: Drift Report

Drift classification and the report — a small record and enum:

```java
enum DriftType { ADDED, MISSING, MODIFIED }

record Drift(DriftType type, String identity, String desiredSpec, String actualSpec) {
    String describe() {
        return type + " " + identity;
    }
}

record DriftReport(String commit, List<Drift> drifts, boolean reconciled) {
    boolean hasDrift() {
        return !drifts.isEmpty();
    }
}
```

### Step 5: The Reconciler

The heart of GitOps. One `reconcile()` call: capture both snapshots, diff, then apply policy.

```java
enum SyncPolicy { AUTO, MANUAL, PHASED }

class GitOpsReconciler {
    private final GitRepository git;
    private final ClusterState cluster;
    private final SyncPolicy policy;

    GitOpsReconciler(GitRepository git, ClusterState cluster, SyncPolicy policy) {
        this.git = git;
        this.cluster = cluster;
        this.policy = policy;
    }

    DriftReport reconcile() {
        var desired = git.snapshot();
        var actual = cluster.snapshot();
        var drifts = new ArrayList<Drift>();

        var desiredDigests = new HashMap<String, String>();
        var actualDigests = new HashMap<String, String>();
        desired.values().forEach(r -> desiredDigests.put(r.identity(), SpecDigest.of(r.spec())));
        actual.values().forEach(r -> actualDigests.put(r.identity(), SpecDigest.of(r.spec())));

        for (var identity : desired.keySet()) {
            if (!actual.containsKey(identity)) {
                drifts.add(new Drift(DriftType.ADDED, identity, desiredDigests.get(identity), null));
            } else if (!desiredDigests.get(identity).equals(actualDigests.get(identity))) {
                drifts.add(new Drift(DriftType.MODIFIED, identity,
                    desiredDigests.get(identity), actualDigests.get(identity)));
            }
        }
        for (var identity : actual.keySet()) {
            if (!desired.containsKey(identity)) {
                drifts.add(new Drift(DriftType.MISSING, identity, null, actualDigests.get(identity)));
            }
        }
        drifts.sort(Comparator.comparing(Drift::identity));

        boolean reconciled = apply(desired, drifts);
        return new DriftReport(git.head(), List.copyOf(drifts), reconciled);
    }

    private boolean apply(Map<String, Resource> desired, List<Drift> drifts) {
        // AUTO: apply/deletes in order; MANUAL: report only (returns false);
        // PHASED: waves by kind (CRD -> Namespace -> Deployment -> Service).
        // Full switch implementation in the Complete Solution below.
        return switch (policy) {
            case AUTO -> applyAuto(desired, drifts);
            case MANUAL -> false;
            case PHASED -> applyPhased(desired, drifts);
        };
    }
}
```

### Step 6: Main — The Full Story

Initial sync, drift injection, auto-reconcile, manual policy.

```java
public class GitOpsLab {
    public static void main(String[] args) {
        var git = new GitRepository();
        git.add(new Resource("Namespace", "payments", null, Map.of("labels", Map.of("team", "payments"))));
        git.add(new Resource("Deployment", "payment-service", "payments",
            Map.of("replicas", 3, "image", "payments/api:1.2.0")));
        git.add(new Resource("Service", "payment-service", "payments",
            Map.of("port", 8080, "selector", Map.of("app", "payment-service"))));
        git.commit("abc1234");

        var cluster = new ClusterState();
        var reconciler = new GitOpsReconciler(git, cluster, SyncPolicy.AUTO);

        var first = reconciler.reconcile();
        first.drifts().forEach(d -> System.out.println("[drift] " + d.describe()));
        System.out.println("Synced to " + first.commit() + ": " + first.reconciled());

        cluster.simulateDrift(new Resource("Deployment", "payment-service", "payments",
            Map.of("replicas", 1, "image", "payments/api:1.2.0")));

        var repaired = reconciler.reconcile();
        repaired.drifts().forEach(d -> System.out.println("[drift] " + d.describe()));
        System.out.println("Reconciled after drift: " + repaired.reconciled());

        var manualCluster = new ClusterState();
        var manualReconciler = new GitOpsReconciler(git, manualCluster, SyncPolicy.MANUAL);
        var manualReport = manualReconciler.reconcile();
        manualReport.drifts().forEach(d -> System.out.println("[drift] " + d.describe()));
        System.out.println("Manual policy reconciled: " + manualReport.reconciled());

        var second = reconciler.reconcile();
        System.out.println("Drift after repair: " + second.hasDrift() + " (expected false)");
    }
}
```
## Complete Solution

The full compilable file, `GitOpsLab.java` in package `com.devops.deep.lab01`:

```java
package com.devops.deep.lab01;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GitOpsLab {
    public static void main(String[] args) {
        var git = new GitRepository();
        git.add(new Resource("Namespace", "payments", null, Map.of("labels", Map.of("team", "payments"))));
        git.add(new Resource("Deployment", "payment-service", "payments",
            Map.of("replicas", 3, "image", "payments/api:1.2.0")));
        git.add(new Resource("Service", "payment-service", "payments",
            Map.of("port", 8080, "selector", Map.of("app", "payment-service"))));
        git.commit("abc1234");

        var cluster = new ClusterState();
        var reconciler = new GitOpsReconciler(git, cluster, SyncPolicy.AUTO);

        System.out.println("== First sync ==");
        var first = reconciler.reconcile();
        first.drifts().forEach(d -> System.out.println("[drift] " + d.describe()));
        System.out.println("Synced to " + first.commit() + ": " + first.reconciled());

        System.out.println("== A human edits the Deployment in the cluster (drift) ==");
        cluster.simulateDrift(new Resource("Deployment", "payment-service", "payments",
            Map.of("replicas", 1, "image", "payments/api:1.2.0")));

        System.out.println("== Reconciler notices and repairs ==");
        var repaired = reconciler.reconcile();
        repaired.drifts().forEach(d -> System.out.println("[drift] " + d.describe()));
        System.out.println("Reconciled: " + repaired.reconciled());

        System.out.println("== Now with MANUAL policy, drift is reported but not applied ==");
        var manualCluster = new ClusterState();
        var manualReconciler = new GitOpsReconciler(git, manualCluster, SyncPolicy.MANUAL);
        var manualReport = manualReconciler.reconcile();
        manualReport.drifts().forEach(d -> System.out.println("[drift] " + d.describe()));
        System.out.println("Reconciled: " + manualReport.reconciled());

        System.out.println("== Idempotency: reconcile again, nothing changes ==");
        var second = reconciler.reconcile();
        System.out.println("Drift after repair: " + second.hasDrift() + " (expected false)");
    }
}

record Resource(String kind, String name, String namespace, Map<String, Object> spec) {
    String identity() {
        return kind + "/" + (namespace == null ? "" : namespace) + "/" + name;
    }
}

class SpecDigest {
    static String of(Map<String, Object> spec) {
        try {
            var buffer = new StringBuffer();
            appendSorted(buffer, spec);
            var digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(buffer.toString().getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void appendSorted(StringBuffer buffer, Map<String, Object> map) {
        map.keySet().stream().sorted().forEach(key -> {
            buffer.append(key).append('=');
            appendValue(buffer, map.get(key));
            buffer.append(';');
        });
    }

    private static void appendValue(StringBuffer buffer, Object value) {
        if (value instanceof Map<?, ?> nested) {
            @SuppressWarnings("unchecked")
            var stringMap = (Map<String, Object>) nested;
            appendSorted(buffer, stringMap);
        } else {
            buffer.append(value);
        }
    }
}

class GitRepository {
    private final Map<String, Resource> desired = new ConcurrentHashMap<>();
    private String commit = "0000000";

    void add(Resource resource) {
        desired.put(resource.identity(), resource);
    }

    void commit(String commitHash) {
        this.commit = commitHash;
    }

    Map<String, Resource> snapshot() {
        return Map.copyOf(desired);
    }

    String head() {
        return commit;
    }
}

class ClusterState {
    private final Map<String, Resource> actual = new ConcurrentHashMap<>();

    void apply(Resource resource) {
        actual.put(resource.identity(), resource);
        System.out.println("[cluster] applied " + resource.identity());
    }

    void delete(String identity) {
        actual.remove(identity);
        System.out.println("[cluster] deleted " + identity);
    }

    Map<String, Resource> snapshot() {
        return Map.copyOf(actual);
    }

    void simulateDrift(Resource drifted) {
        actual.put(drifted.identity(), drifted);
        System.out.println("[cluster] DRIFT injected: " + drifted.identity()
            + " changed by human/agent");
    }
}

enum DriftType { ADDED, MISSING, MODIFIED }

record Drift(DriftType type, String identity, String desiredSpec, String actualSpec) {
    String describe() {
        return type + " " + identity;
    }
}

record DriftReport(String commit, List<Drift> drifts, boolean reconciled) {
    boolean hasDrift() {
        return !drifts.isEmpty();
    }
}

enum SyncPolicy { AUTO, MANUAL, PHASED }

class GitOpsReconciler {
    private final GitRepository git;
    private final ClusterState cluster;
    private final SyncPolicy policy;

    GitOpsReconciler(GitRepository git, ClusterState cluster, SyncPolicy policy) {
        this.git = git;
        this.cluster = cluster;
        this.policy = policy;
    }

    DriftReport reconcile() {
        var desired = git.snapshot();
        var actual = cluster.snapshot();
        var drifts = new ArrayList<Drift>();

        var desiredDigests = new HashMap<String, String>();
        for (var resource : desired.values()) {
            desiredDigests.put(resource.identity(), SpecDigest.of(resource.spec()));
        }
        var actualDigests = new HashMap<String, String>();
        for (var resource : actual.values()) {
            actualDigests.put(resource.identity(), SpecDigest.of(resource.spec()));
        }

        for (var identity : desired.keySet()) {
            if (!actual.containsKey(identity)) {
                drifts.add(new Drift(DriftType.ADDED, identity, desiredDigests.get(identity), null));
            } else if (!desiredDigests.get(identity).equals(actualDigests.get(identity))) {
                drifts.add(new Drift(DriftType.MODIFIED, identity,
                    desiredDigests.get(identity), actualDigests.get(identity)));
            }
        }
        for (var identity : actual.keySet()) {
            if (!desired.containsKey(identity)) {
                drifts.add(new Drift(DriftType.MISSING, identity, null, actualDigests.get(identity)));
            }
        }
        drifts.sort(Comparator.comparing(Drift::identity));

        boolean reconciled = apply(desired, drifts);
        return new DriftReport(git.head(), List.copyOf(drifts), reconciled);
    }

    private boolean apply(Map<String, Resource> desired, List<Drift> drifts) {
        if (drifts.isEmpty()) return true;
        switch (policy) {
            case AUTO -> {
                for (var drift : drifts) {
                    if (drift.type() == DriftType.MISSING) {
                        cluster.delete(drift.identity());
                    } else {
                        cluster.apply(desired.get(drift.identity()));
                    }
                }
                return true;
            }
            case MANUAL -> {
                System.out.println("[reconciler] drift detected, manual approval required; no changes applied");
                return false;
            }
            case PHASED -> {
                var order = List.of("CRD", "Namespace", "Deployment", "Service");
                for (String phase : order) {
                    for (var drift : drifts) {
                        if (drift.identity().startsWith(phase + "/") && drift.type() != DriftType.MISSING) {
                            cluster.apply(desired.get(drift.identity()));
                        }
                    }
                }
                for (var drift : drifts) {
                    if (drift.type() == DriftType.MISSING) {
                        cluster.delete(drift.identity());
                    }
                }
                return true;
            }
        }
        return false;
    }
}
```

## Complexity Analysis

- **Reconcile**: O(D + A) to digest all desired and actual resources; O(D + A) for the diff loops; O(K log K) for sorting drifts.
- **Digest**: O(S log S) per resource where S = spec keys (sorting).
- **Space**: O(D + A) for snapshots.

## Test Cases

| Scenario | Expected |
|---|---|
| First sync (AUTO) | All 3 resources applied, `reconciled=true`, no drift remaining |
| Human drift (replicas 3 -> 1) | `MODIFIED Deployment/payments/payment-service`; auto-repair restores 3 replicas |
| Manual policy, empty cluster | Drifts reported, nothing applied, `reconciled=false` |
| Spec reordering | Same digest — no false-positive drift |
| Resource removed from git | `MISSING` drift; AUTO deletes it from the cluster |
| Reconcile after repair | No drifts (idempotent) |

Example run:

```
== First sync ==
[cluster] applied Deployment/payments/payment-service
[cluster] applied Namespace//payments
[cluster] applied Service/payments/payment-service
[drift] ADDED Deployment/payments/payment-service
[drift] ADDED Namespace//payments
[drift] ADDED Service/payments/payment-service
Synced to abc1234: true
== A human edits the Deployment in the cluster (drift) ==
[cluster] DRIFT injected: Deployment/payments/payment-service changed by human/agent
== Reconciler notices and repairs ==
[cluster] applied Deployment/payments/payment-service
[drift] MODIFIED Deployment/payments/payment-service
Reconciled: true
== Now with MANUAL policy, drift is reported but not applied ==
[reconciler] drift detected, manual approval required; no changes applied
[drift] ADDED Deployment/payments/payment-service
[drift] ADDED Namespace//payments
[drift] ADDED Service/payments/payment-service
Reconciled: false
== Idempotency: reconcile again, nothing changes ==
Drift after repair: false (expected false)
```
## Follow-Up Questions

1. **How is this different from ArgoCD/Flux?** They add the Kubernetes API integration, webhooks, and UI; this lab is the core comparison+apply loop that both tools implement. Flux pulls from git via a source controller; ArgoCD pushes the comparison via its application controller.
2. **How do you handle spec comparison for lists and nulls?** Extend `appendSorted` to sort lists and canonicalize null; the digest approach generalizes.
3. **How do you prevent the reconciler from fighting legitimate cluster changes (e.g., HPA scaling replicas)?** Exclude live-adjustable fields (like `replicas` under HPA) from the digest, or use server-side apply with field ownership.
4. **How do you implement sync waves properly?** Resource annotations like `argocd.argoproj.io/sync-wave: "0"` define the order; here `PHASED` approximates waves by kind.
5. **How do you surface the drift report in production?** Emit it as a metric/event: drift count by kind, last sync commit, time since last successful sync — page when drift persists longer than the SLO.
6. **How do you handle secrets in git?** Don't commit them: use SOPS-encrypted manifests or sealed secrets; the reconciler decrypts at apply time with keys from the cluster.

