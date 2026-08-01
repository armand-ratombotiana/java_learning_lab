# Lab 08: Problem Walkthrough — Compliance Audit Engine

## Problem Statement

Implement a compliance audit engine with continuous control monitoring. The engine must:

1. Maintain a **control catalog**: executable checks mapped to frameworks (SOC 2, PCI-DSS, HIPAA), each with a severity and a definition of compliance.
2. **Evaluate** controls continuously (hourly cycles) over resources that carry scope tags, producing per-(control, resource, hour) verdicts of `PASS`, `FAIL`, or `UNKNOWN` (unknown when evidence is missing or stale).
3. Compute **time-weighted compliance scores** per framework over the last N evaluation cycles — a control counts as compliant for the fraction of cycles it passed (with suppressions counting as compliant).
4. Support **waivers/suppressions**: time-bound, owner-attributed, auto-expiring records that flip a FAIL to compliant in scoring while remaining visible in reports.
5. Detect **stale evidence**: if a resource's evidence is older than a freshness budget, its verdict becomes UNKNOWN rather than a stale PASS.
6. Produce a per-framework **compliance report**: score, failing controls, waivers, and unknowns.

**Constraints**

- Verdicts must be deterministic: same evidence, same report.
- A control's verdict for a resource is FAIL if any of its check clauses fails (all must pass).
- All code must compile under Java 21+.

---

## Walkthrough

### Step 1: Model frameworks, checks, and controls

A check is a predicate over a resource snapshot: `(ResourceSnapshot) -> boolean`. A control is a set of checks plus framework scoping. The canonical-control design (single check implementation, multiple framework mappings) is represented by controls carrying a framework.

```java
package com.cloud.deep.lab08;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ComplianceAuditEngine {

    public enum Framework { SOC2, PCI_DSS, HIPAA }

    public enum Severity { LOW, MEDIUM, HIGH, CRITICAL }

    public enum Verdict { PASS, FAIL, UNKNOWN }

    @FunctionalInterface
    public interface Check {
        boolean evaluate(ResourceSnapshot snapshot);
    }

    public record Control(String id, String name, Framework framework, Severity severity,
                          List<Check> checks) {
        Verdict evaluate(ResourceSnapshot s) {
            for (Check c : checks) {
                if (!c.evaluate(s)) return Verdict.FAIL;
            }
            return Verdict.PASS;
        }
    }
```

### Step 2: Model resources, snapshots, and scope

A resource has a type, attributes, scope tags (which frameworks apply), and an evidence trail: the history of snapshots with timestamps. The *freshness* of the latest snapshot determines whether a verdict is trustworthy.

```java
    public record ResourceSnapshot(String resourceId, String type,
                                   Map<String, String> attributes, Instant capturedAt) {}

    public static final class Resource {
        private final String id;
        private final String type;
        private final Map<String, String> attributes;
        private final Map<String, String> scopeTags;
        private final List<ResourceSnapshot> evidence = new ArrayList<>();

        Resource(String id, String type, Map<String, String> attributes,
                 Map<String, String> scopeTags) {
            this.id = id;
            this.type = type;
            this.attributes = attributes;
            this.scopeTags = scopeTags;
        }

        void capture(Instant at) {
            evidence.add(new ResourceSnapshot(id, type, Map.copyOf(attributes), at));
        }

        ResourceSnapshot latest() { return evidence.get(evidence.size() - 1); }

        boolean inScope(Framework f) {
            return switch (f) {
                case SOC2 -> Boolean.parseBoolean(scopeTags.getOrDefault("soc2", "false"));
                case PCI_DSS -> Boolean.parseBoolean(scopeTags.getOrDefault("pci", "false"));
                case HIPAA -> Boolean.parseBoolean(scopeTags.getOrDefault("hipaa", "false"));
            };
        }

        String id() { return id; }
    }
```

### Step 3: Implement the checks

Four executable checks power the demo: **encryption-at-rest**, **no-public-access**, **mfa-enabled**, and **audit-logging-enabled** — each a pure function over snapshot attributes.

```java
    static final class Checks {
        static boolean encryptionAtRest(ResourceSnapshot s) {
            return Boolean.parseBoolean(s.attributes().getOrDefault("encrypted", "false"));
        }

        static boolean noPublicAccess(ResourceSnapshot s) {
            return !"public-read".equals(s.attributes().get("acl"));
        }

        static boolean mfaEnabled(ResourceSnapshot s) {
            return Boolean.parseBoolean(s.attributes().getOrDefault("mfa", "false"));
        }

        static boolean auditLoggingEnabled(ResourceSnapshot s) {
            return Boolean.parseBoolean(s.attributes().getOrDefault("audit_logging", "false"));
        }
    }
```

### Step 4: Implement waivers

A waiver suppresses a FAIL for a control on a resource for a bounded window. It carries an owner and a reason for auditability, and expires automatically (an expired waiver is simply not applied).

```java
    public record Waiver(String controlId, String resourceId, Instant expiresAt,
                         String owner, String reason) {
        boolean activeAt(Instant at) { return !at.isAfter(expiresAt); }
    }
```

### Step 5: The evaluation engine

The engine runs one evaluation cycle per hour per (control, resource) pair:

1. Skip resources out of scope for the control's framework.
2. If no evidence exists at all → `UNKNOWN` (never assume compliance).
3. If the latest evidence is older than the freshness budget → `UNKNOWN` (stale evidence).
4. Otherwise evaluate the control's checks → `PASS` or `FAIL`.
5. Store the verdict with the cycle timestamp.

```java
    public record VerdictRecord(String controlId, String resourceId, Verdict verdict,
                                Instant cycleAt) {}

    public static final class Evaluator {
        private final List<Control> controls;
        private final Duration freshnessBudget;
        private final List<VerdictRecord> verdicts = new ArrayList<>();

        public Evaluator(List<Control> controls, Duration freshnessBudget) {
            this.controls = controls;
            this.freshnessBudget = freshnessBudget;
        }

        public void cycle(List<Resource> resources, Instant at) {
            for (Control control : controls) {
                for (Resource r : resources) {
                    if (!r.inScope(control.framework())) continue;
                    if (r.evidence.isEmpty()) {
                        verdicts.add(new VerdictRecord(control.id(), r.id(),
                                Verdict.UNKNOWN, at));
                        continue;
                    }
                    ResourceSnapshot latest = r.latest();
                    boolean fresh = Duration.between(latest.capturedAt(), at)
                            .compareTo(freshnessBudget) <= 0;
                    if (!fresh) {
                        verdicts.add(new VerdictRecord(control.id(), r.id(),
                                Verdict.UNKNOWN, at));
                        continue;
                    }
                    verdicts.add(new VerdictRecord(control.id(), r.id(),
                            control.evaluate(latest), at));
                }
            }
        }

        public List<VerdictRecord> verdicts() { return List.copyOf(verdicts); }
    }
```

### Step 6: Scoring and reporting

The reporter computes, per framework over all cycles:

- **Score**: percentage of verdicts that are PASS or FAIL-with-active-waiver — waivers count as compliant (risk accepted, not forgotten).
- **Failing controls**: FAIL verdicts with no waiver, listed with resource IDs.
- **Unknowns**: UNKNOWN verdicts and their counts — surfaced, never hidden.
- **Waivers**: active waivers masking failures, for the audit trail.

```java
    public static final class Reporter {
        private final List<Control> controls;
        private final List<Waiver> waivers;

        public Reporter(List<Control> controls, List<Waiver> waivers) {
            this.controls = controls;
            this.waivers = waivers;
        }

        public void report(List<VerdictRecord> verdicts, Instant asOf) {
            for (Framework f : Framework.values()) {
                List<VerdictRecord> scoped = verdicts.stream()
                        .filter(v -> control(v.controlId()).framework() == f)
                        .toList();

                if (scoped.isEmpty()) {
                    System.out.printf("[%s] no in-scope resources evaluated%n", f);
                    continue;
                }

                long total = scoped.size();
                long passed = scoped.stream().filter(v -> v.verdict() == Verdict.PASS).count();
                long waived = scoped.stream().filter(v -> v.verdict() == Verdict.FAIL)
                        .filter(v -> hasActiveWaiver(v, asOf)).count();
                long unknown = scoped.stream().filter(v -> v.verdict() == Verdict.UNKNOWN).count();
                long failed = total - passed - waived - unknown;

                double score = (passed + waived) * 100.0 / Math.max(total, 1);

                System.out.printf("%n=== %s (score %.1f%%) ===%n", f, score);
                System.out.printf("  pass=%d fail=%d waived=%d unknown=%d%n",
                        passed, failed, waived, unknown);

                scoped.stream().filter(v -> v.verdict() == Verdict.FAIL)
                        .filter(v -> !hasActiveWaiver(v, asOf))
                        .forEach(v -> System.out.printf("    FAILING: %s on %s%n",
                                v.controlId(), v.resourceId()));

                scoped.stream().filter(v -> v.verdict() == Verdict.UNKNOWN)
                        .forEach(v -> System.out.printf("    UNKNOWN: %s on %s (stale/no evidence)%n",
                                v.controlId(), v.resourceId()));

                waivers.stream().filter(w -> w.activeAt(asOf))
                        .forEach(w -> System.out.printf("    WAIVER: %s on %s (owner=%s until %s)%n",
                                w.controlId(), w.resourceId(), w.owner(), w.expiresAt()));
            }
        }

        private boolean hasActiveWaiver(VerdictRecord v, Instant asOf) {
            return waivers.stream().anyMatch(w -> w.activeAt(asOf)
                    && w.controlId().equals(v.controlId())
                    && w.resourceId().equals(v.resourceId()));
        }

        private Control control(String id) {
            return controls.stream().filter(c -> c.id().equals(id)).findFirst().orElseThrow();
        }
    }
```

### Step 7: Demo — the continuous monitoring timeline

The demo builds three resources (a PCI-scoped S3 bucket, a SOC2-scoped IAM user, a HIPAA-scoped database), defines four controls, and runs four hourly cycles with a scripted incident: the bucket goes public (cycle 2), a waiver is granted, then it is fixed (cycle 4). One resource stops producing evidence (collector failure) to demonstrate the UNKNOWN path.

```java
    public static void main(String[] args) {
        List<Control> controls = List.of(
                new Control("encryption-at-rest", "Encryption at rest", Framework.PCI_DSS,
                        Severity.CRITICAL, List.of(Checks::encryptionAtRest)),
                new Control("no-public-access", "No public access", Framework.PCI_DSS,
                        Severity.HIGH, List.of(Checks::noPublicAccess)),
                new Control("mfa-required", "MFA required", Framework.SOC2,
                        Severity.HIGH, List.of(Checks::mfaEnabled)),
                new Control("audit-logging", "Audit logging enabled", Framework.HIPAA,
                        Severity.MEDIUM, List.of(Checks::auditLoggingEnabled)));

        Resource bucket = new Resource("bucket-cards", "s3", new HashMap<>(Map.of(
                "encrypted", "true", "acl", "private")), Map.of("pci", "true"));
        Resource user = new Resource("user-deploy", "iam", new HashMap<>(Map.of(
                "mfa", "true")), Map.of("soc2", "true"));
        Resource db = new Resource("db-clinical", "rds", new HashMap<>(Map.of(
                "encrypted", "false", "audit_logging", "true")), Map.of("hipaa", "true"));
        List<Resource> resources = List.of(bucket, user, db);

        List<Waiver> waivers = new ArrayList<>();
        Evaluator evaluator = new Evaluator(controls, Duration.ofHours(3));

        Instant start = Instant.parse("2026-07-01T00:00:00Z");
        System.out.println("=== Compliance Audit Engine Demo ===\n");

        for (int cycle = 0; cycle < 4; cycle++) {
            Instant at = start.plusSeconds(cycle * 3600L);

            // Scripted timeline
            if (cycle == 2) {
                bucket.attributes.put("acl", "public-read");      // incident: bucket goes public
                db.attributes.put("encrypted", "true");           // db fixed after cycle-1 finding
                waivers.add(new Waiver("no-public-access", "bucket-cards",
                        start.plusSeconds(10 * 3600L), "sec-lead",
                        "remediation in progress, ETA 8h"));
            }
            if (cycle == 3) {
                bucket.attributes.put("acl", "private");          // remediated
                user.attributes.put("mfa", "false");              // new finding: MFA removed
            }

            // Evidence collection: db stops reporting after cycle 2 (collector failure)
            if (cycle < 3) {
                for (Resource r : resources) r.capture(at);
            } else {
                bucket.capture(at);
                user.capture(at);
                // db intentionally missing -> its verdicts become UNKNOWN
            }

            evaluator.cycle(resources, at);
            System.out.println("-- Cycle " + cycle + " (t+" + (cycle * 60) + "m) --");
            evaluator.verdicts().stream()
                    .filter(v -> v.cycleAt().equals(at))
                    .forEach(v -> System.out.printf("  %-20s %-13s %s%n",
                            v.controlId(), v.resourceId(), v.verdict()));
        }

        System.out.println("\n=== Compliance report (as of t+3h) ===");
        new Reporter(controls, waivers).report(evaluator.verdicts(),
                start.plusSeconds(3 * 3600L));
    }
}
```

### Step 8: Verify the expected timeline

| Cycle | Incident | Expected verdicts |
|-------|----------|-------------------|
| 0 | Clean start | bucket: encryption PASS, public PASS | user: MFA PASS | db: encryption FAIL |
| 1 | db encrypted | db: encryption PASS | everything clean |
| 2 | Bucket goes **public**; waiver granted; db collector dies | bucket: public FAIL (waived) | db: UNKNOWN (stale) |
| 3 | Bucket fixed; user MFA removed; db still silent | bucket: public PASS | user: MFA FAIL | db: UNKNOWN |

Report expectations (4 cycles):

- **PCI_DSS**: 8 verdicts (bucket × 2 controls × 4 cycles): 7 PASS + 1 FAIL-with-waiver → score 100%.
- **SOC2**: 4 verdicts (user MFA): P,P,P,F → score 75%, one FAILING entry.
- **HIPAA**: 4 verdicts (db): F,P,U,U → score 25%, two UNKNOWN entries — the collector failure is visible, not masked as PASS.

The waived failure is invisible to the score but visible in the report — the audit trail stays honest.

---

## Complexity Analysis

- **Evaluation cycle**: O(C · R) per cycle — each control runs its check clauses over each in-scope resource; check clauses are O(K) in attribute count.
- **Scoring**: O(V) over stored verdicts; report groups per framework in a single pass.
- **Space**: O(C · R · N) verdict records over N cycles, plus O(R · S) snapshots where S = snapshots per resource. Evidence grows monotonically by design (append-only); retention policies tier it to cold storage after the audit window.
- **Freshness check**: O(1) per verdict — compare latest capture time to the cycle time.
- **Determinism**: all evaluation paths are pure functions of (evidence, waivers, clock) — replaying the same evidence bundle yields identical reports.

---

## Follow-Up Questions

1. **How do you make the evaluation scale to millions of resources?** Partition by (framework, resource-type): checks are per-type (S3 checks never run on IAM users), and evaluation workers consume sharded verdict topics; the per-type partitioning removes 90% of the work.

2. **How do you audit the audit engine itself?** Evaluation is deterministic and replayable; every evaluation run records its input digest (evidence hashes + waiver set + engine version), and a signed hash-chain over evidence writes makes the evidence bundle tamper-evident.

3. **What is the difference between a waiver and a false positive?** A false positive is a check bug — it must be *fixed*, not suppressed; a waiver is an accepted, time-bound risk decision. The engine tracks check-fire rates to surface likely false positives for review.

4. **How do you handle evidence gaps from a dead collector?** The freshness budget converts stale evidence to UNKNOWN; a separate 'evidence coverage' metric pages the platform team when coverage drops below 99% for in-scope resources.

5. **How do you map framework requirements to canonical controls in code?** A `ControlMap` table: framework requirement IDs (PCI 10.2, SOC 2 CC7.3, HIPAA 164.312(b)) each reference one canonical control — one verdict per canonical control, multiple requirement views.

6. **How do you support manual evidence (policies, sign-offs)?** A `ManualEvidence` record attached to a control with a document hash, uploader, and review expiry — manual evidence needs re-certification (e.g., annually) to stay valid, and its verdict carries a lower confidence weight.
