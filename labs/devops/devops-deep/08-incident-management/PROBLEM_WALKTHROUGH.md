# Lab 08: Problem Walkthrough — Incident Management: Lifecycle, Escalation and RCA

## Problem Statement

Implement an incident management system in pure Java 21+. Requirements:

1. **Incident lifecycle**: an incident moves through states — Detected -> Investigating ->
   Resolved -> Closed — with guards on illegal transitions (e.g., resolving before
   acknowledgment).
2. **Roles**: the commander (IC), deputy, scribe, and subject-matter experts (SMEs) are
   assigned to an incident and recorded.
3. **Timeline**: every action is a timestamped event — alert received, paged, acknowledged,
   mitigated, resolved — the incident's audit trail.
4. **Escalation policy**: each severity has an acknowledgment timeout; if the incident is not
   acknowledged in time, escalation is required — the safety net for missed alerts.
5. **Root cause analysis**: contributing factors and action items with owners and due dates,
   produced after the incident closes.
6. **Deterministic demo**: fixed timestamps drive every decision, so the escalation and
   lifecycle outputs are reproducible.

## Constraints

- Java 21+ only, no external frameworks.
- The lifecycle is a small state machine — transitions throw on invalid moves.
- `Incident` and `RootCauseAnalysis` are separate, so a postmortem can be authored by a
  different team than the one that handled the incident.

## Approach

Incident management is process encoded in state: the lifecycle guards *what may happen next*,
the escalation policy guards *what must happen by when*, and the timeline preserves *what
actually happened*. The design mirrors the industry standard (PagerDuty, incident.io): the
commander is the single decision-maker, roles are explicit, and the postmortem is a separate
artifact with its own lifecycle — analysis happens after the incident is resolved, with
action items tracked to completion.

Design decisions:

- **State machine with guards**: `acknowledge` requires `DETECTED`, `resolve` requires
  `INVESTIGATING` (acknowledged), `close` requires `RESOLVED` — invalid transitions throw, so
  the process cannot be skipped accidentally.
- **Escalation as a pure function**: `needsEscalation(severity, detectedAt, acknowledgedAt,
  now)` has no state of its own — the same inputs always produce the same answer, which makes
  the policy unit-testable and the demo deterministic.
- **Timeline as append-only strings with instants**: the `logEvent` method stamps everything;
  the timeline is printed in insertion order, which is chronological by construction.
- **RCA detached from the incident**: `RootCauseAnalysis` takes the incident id and writes its
  own report — blameless postmortems belong to the org, not to the incident's on-call team.

## Step-by-Step Solution

### Step 1: The Lifecycle State Machine

```java
class Incident {
    enum Status { DETECTED, INVESTIGATING, RESOLVED, CLOSED }

    void acknowledge(Instant at) {
        if (status != Status.DETECTED) {
            throw new IllegalStateException("Only a detected incident can be acknowledged");
        }
        status = Status.INVESTIGATING;
        acknowledgedAt = at;
    }

    void resolve(Instant at, String summary) {
        if (status != Status.INVESTIGATING) {
            throw new IllegalStateException("Only an acknowledged incident can be resolved");
        }
        status = Status.RESOLVED;
        resolvedAt = at;
        events.add("[" + at + "] RESOLVED: " + summary);
    }
}
```

### Step 2: Escalation Policy

```java
class EscalationPolicy {
    private final Map<String, Duration> timeouts = new ConcurrentHashMap<>();

    void setSeverityTimeout(String severity, Duration timeout) {
        timeouts.put(severity, timeout);
    }

    boolean needsEscalation(String severity, Instant detectedAt,
                            Instant acknowledgedAt, Instant now) {
        if (acknowledgedAt != null) return false;
        var timeout = timeouts.getOrDefault(severity, Duration.ofMinutes(15));
        return now.isAfter(detectedAt.plus(timeout));
    }
}
```

### Step 3: RCA

The postmortem records the root cause, contributing factors (not a single 'the one mistake'),
and action items with owners and due dates — the walkthrough prints the report; production
tracks the action items to completion.

## Complete Solution

The full compilable file, `IncidentLab.java` in package `com.devops.deep.lab08`:

```java
package com.devops.deep.lab08;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IncidentLab {
    public static void main(String[] args) {
        var policy = new EscalationPolicy();
        policy.setSeverityTimeout("SEV1", Duration.ofMinutes(5));
        policy.setSeverityTimeout("SEV2", Duration.ofMinutes(15));

        var detectedAt = Instant.parse("2026-07-30T10:00:00Z");
        var incident = new Incident("INC-2026-0033", "SEV1",
            "Payments API 502 spike", detectedAt);

        incident.assignCommander("Alice (IC)");
        incident.assignRole("Deputy", "Bob");
        incident.assignRole("Scribe", "Charlie");
        incident.addSme("Payments Team", "David");
        var alertAt = Instant.parse("2026-07-30T10:00:01Z");
        incident.logEvent(alertAt, "Alert: 5xx rate > 5% on payments-api");

        var ackAt = Instant.parse("2026-07-30T10:02:00Z");
        incident.acknowledge(ackAt);
        incident.logEvent(ackAt, "IC acknowledged, paged payments team");

        var now = Instant.parse("2026-07-30T10:03:00Z");
        System.out.println("Escalation needed 1m after ack deadline? "
            + policy.needsEscalation("SEV1", detectedAt, ackAt, now));

        var nowLate = Instant.parse("2026-07-30T10:06:00Z");
        System.out.println("Escalation needed with no ack at 10:06? "
            + policy.needsEscalation("SEV1", detectedAt, null, nowLate));

        var mitigatedAt = Instant.parse("2026-07-30T10:40:00Z");
        incident.logEvent(mitigatedAt, "David identified misconfigured Redis maxmemory-policy");
        incident.logEvent(mitigatedAt, "Fixed Redis config, traffic restored");
        incident.resolve(Instant.parse("2026-07-30T10:45:00Z"),
            "Redis eviction policy caused connection churn");
        incident.close();

        var rca = new RootCauseAnalysis(incident.id(),
            "Misconfigured Redis maxmemory-policy (noeviction vs allkeys-lru)");
        rca.addFactor("Redis config change deployed without review");
        rca.addFactor("No canary for Redis config changes");
        rca.addFactor("Missing alert on Redis eviction rate");
        rca.addActionItem("Add config review to change management", "SRE", "2026-08-15");
        rca.addActionItem("Deploy config canary tooling", "Payments Team", "2026-09-01");

        System.out.println("\n=== Incident Timeline ===");
        incident.timeline().forEach(e -> System.out.println("  " + e));

        System.out.println("\n=== RCA ===");
        System.out.println(rca.report());

        try {
            incident.acknowledge(Instant.parse("2026-07-30T11:00:00Z"));
        } catch (IllegalStateException e) {
            System.out.println("\nGuard: " + e.getMessage());
        }
    }
}

class EscalationPolicy {
    private final Map<String, Duration> timeouts = new ConcurrentHashMap<>();

    void setSeverityTimeout(String severity, Duration timeout) {
        timeouts.put(severity, timeout);
    }

    boolean needsEscalation(String severity, Instant detectedAt,
                            Instant acknowledgedAt, Instant now) {
        if (acknowledgedAt != null) return false;
        var timeout = timeouts.getOrDefault(severity, Duration.ofMinutes(15));
        return now.isAfter(detectedAt.plus(timeout));
    }
}

class Incident {
    enum Status { DETECTED, INVESTIGATING, RESOLVED, CLOSED }

    private final String id;
    private final String severity;
    private final String title;
    private final Instant detectedAt;
    private final Map<String, String> roles = new LinkedHashMap<>();
    private final List<String> smes = new ArrayList<>();
    private final List<String> events = new ArrayList<>();
    private String commander;
    private Instant acknowledgedAt;
    private Instant resolvedAt;
    private Status status = Status.DETECTED;

    Incident(String id, String severity, String title, Instant detectedAt) {
        this.id = id;
        this.severity = severity;
        this.title = title;
        this.detectedAt = detectedAt;
        events.add("[" + detectedAt + "] DETECTED: " + title);
    }

    String id() {
        return id;
    }

    void assignCommander(String commander) {
        this.commander = commander;
    }

    void assignRole(String role, String person) {
        roles.put(role, person);
    }

    void addSme(String team, String person) {
        smes.add(person + " (" + team + ")");
    }

    void logEvent(Instant at, String description) {
        events.add("[" + at + "] " + description);
    }

    void acknowledge(Instant at) {
        if (status != Status.DETECTED) {
            throw new IllegalStateException("Only a detected incident can be acknowledged");
        }
        status = Status.INVESTIGATING;
        acknowledgedAt = at;
        events.add("[" + at + "] ACKNOWLEDGED by " + commander);
    }

    void resolve(Instant at, String summary) {
        if (status != Status.INVESTIGATING) {
            throw new IllegalStateException("Only an acknowledged incident can be resolved");
        }
        status = Status.RESOLVED;
        resolvedAt = at;
        events.add("[" + at + "] RESOLVED: " + summary);
    }

    void close() {
        if (status != Status.RESOLVED) {
            throw new IllegalStateException("Only a resolved incident can be closed");
        }
        status = Status.CLOSED;
        events.add("[" + resolvedAt + "] CLOSED: postmortem scheduled");
    }

    List<String> timeline() {
        var timeline = new ArrayList<String>();
        timeline.add(id + " (" + severity + ") - " + title + " [" + status + "]");
        timeline.add("  Commander: " + commander);
        roles.forEach((role, person) -> timeline.add("  " + role + ": " + person));
        smes.forEach(sme -> timeline.add("  SME: " + sme));
        timeline.add("--- Timeline ---");
        timeline.addAll(events);
        return timeline;
    }
}

class RootCauseAnalysis {
    private final String incidentId;
    private final String rootCause;
    private final List<String> factors = new ArrayList<>();
    private final List<String> actionItems = new ArrayList<>();

    RootCauseAnalysis(String incidentId, String rootCause) {
        this.incidentId = incidentId;
        this.rootCause = rootCause;
    }

    void addFactor(String factor) {
        factors.add(factor);
    }

    void addActionItem(String item, String owner, String dueDate) {
        actionItems.add(item + " (owner: " + owner + ", due: " + dueDate + ")");
    }

    String report() {
        var sb = new StringBuilder();
        sb.append("RCA for ").append(incidentId).append("\n");
        sb.append("Root cause: ").append(rootCause).append("\n");
        sb.append("Contributing factors:\n");
        factors.forEach(f -> sb.append("  - ").append(f).append("\n"));
        sb.append("Action items:\n");
        actionItems.forEach(a -> sb.append("  - [ ] ").append(a).append("\n"));
        return sb.toString().stripTrailing();
    }
}
```

## Complexity Analysis

- **Lifecycle transitions**: O(1) — a status field check and timestamped append.
- **Escalation check**: O(1) — map lookup plus one instant comparison.
- **Timeline / RCA**: O(E) and O(F + A) over events, factors, and action items; both are
  small per incident.
- **Space**: O(E + F + A) per incident — a real system persists these to the incident
  platform and only pages them in while the incident is active.

## Test Cases

| Scenario | Expected |
|---|---|
| Acknowledge from DETECTED | Status becomes INVESTIGATING, timestamp recorded |
| Resolve without acknowledging | `IllegalStateException` (guard) |
| Resolve after acknowledge | Status RESOLVED with summary event |
| Close a resolved incident | Status CLOSED |
| Acknowledge a closed incident | `IllegalStateException` (guard) |
| Escalation with ack within 5m | `false` |
| Escalation with no ack after 5m | `true` |
| Timeline contents | Header, roles, SMEs, all events in order |

Example run:

```
Escalation needed 1m after ack deadline? false
Escalation needed with no ack at 10:06? true

=== Incident Timeline ===
INC-2026-0033 (SEV1) - Payments API 502 spike [CLOSED]
  Commander: Alice (IC)
  Deputy: Bob
  Scribe: Charlie
  SME: David (Payments Team)
--- Timeline ---
[2026-07-30T10:00:00Z] DETECTED: Payments API 502 spike
[2026-07-30T10:00:01Z] Alert: 5xx rate > 5% on payments-api
[2026-07-30T10:02:00Z] ACKNOWLEDGED by Alice (IC)
[2026-07-30T10:02:00Z] IC acknowledged, paged payments team
[2026-07-30T10:40:00Z] David identified misconfigured Redis maxmemory-policy
[2026-07-30T10:40:00Z] Fixed Redis config, traffic restored
[2026-07-30T10:45:00Z] RESOLVED: Redis eviction policy caused connection churn
[2026-07-30T10:45:00Z] CLOSED: postmortem scheduled

=== RCA ===
RCA for INC-2026-0033
Root cause: Misconfigured Redis maxmemory-policy (noeviction vs allkeys-lru)
Contributing factors:
  - Redis config change deployed without review
  - No canary for Redis config changes
  - Missing alert on Redis eviction rate
Action items:
  - [ ] Add config review to change management (owner: SRE, due: 2026-08-15)
  - [ ] Deploy config canary tooling (owner: Payments Team, due: 2026-09-01)

Guard: Only a detected incident can be acknowledged
```

## Follow-Up Questions

1. **Why is the commander a single role?** Decisions during an incident need one accountable
   owner, or they fragment — the IC decides severity, priorities, and when to declare; the
   deputy shadows and can take over; the scribe records. The roles exist because the worst
   incident behavior is five engineers improvising with no one deciding. The timeline proves
   whether the role model worked.
2. **Why escalate on no-acknowledgment instead of no-resolution?** Acknowledgment is the
   first commitment: the alert was seen and someone owns it. A missed ack is unambiguous —
   either the on-call is unreachable or the alert wasn't routed; both are actionable by
   escalation. Resolution deadlines matter too, but they need business context; the ack
   timeout is the pure, always-safe check.
3. **Should the escalation be automatic or human-confirmed?** Automatic with a human audit
   trail. The whole point of the ack timeout is that humans aren't reliably available at 3am;
   the policy escalates to the secondary, then the tertiary, and pages the incident manager —
   all automatic, all logged. What shouldn't be automatic: declaring the incident resolved or
   reverting traffic without a human decision — those are judgment calls.
4. **When does the postmortem get written — and by whom?** After resolution, while the
   details are fresh — within a few days, not weeks. Written by the responders, facilitated
   by an SRE who wasn't involved, so the summary has a neutral reviewer. The incident's
   on-call team writes the timeline; the org owns the action items. Waiting too long is the
   most common failure — memory decays and action items lose their urgency.
5. **What makes an action item actually get done?** Three properties: specific enough to
   verify ('deploy canary tooling' is not; 'add a config-review step to the change template'
   is), an owner who is a person, not a team alias with no champion, and a due date with a
   review cadence — the walkthrough's report is the starting artifact; the tracking system
   that pages the owner on missed dates is what converts it into change.
6. **How do you connect the incident to the error budget?** The incident consumed error
   budget; the postmortem's action items are the payment. The concrete loop: incidents that
   exhaust the budget pause deploys until fixed; action items from postmortems feed the
   reliability backlog and their completion is a leadership metric. The SLO program and the
   incident program are two views of the same reliability ledger.
7. **What does the timeline tell you that metrics don't?** Metrics tell you the system's
   behavior; the timeline tells you the *response* behavior — time to acknowledgment, time to
   mitigation, whether escalation worked, whether the right people were involved. Most
   incident reviews fail not on the technical root cause but on the response: detection delay,
   ack delay, or escalation gaps — and those are only visible in the timeline. The pattern to
   look for: a long gap between two events with no log entry is itself a finding.
