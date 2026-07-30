package com.devops.deep.lab08;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class IncidentLab {
    public static void main(String[] args) {
        var escalation = new EscalationPolicy();
        escalation.setSeverityTimeout("SEV1", Duration.ofMinutes(5));
        escalation.setSeverityTimeout("SEV2", Duration.ofMinutes(15));

        var incident = new Incident("INC-20240730-001", "SEV1", "Payment service returning 502 errors");
        incident.assignCommander("Alice (IC)");
        incident.assignRole("Deputy", "Bob");
        incident.assignRole("Scribe", "Charlie");
        incident.addSME("Payment Team", "David");

        incident.logEvent("Alert received from Prometheus: 5xx rate > 5%");
        incident.logEvent("IC declared SEV1, paged payment team");
        incident.logEvent("David identified misconfigured Redis cluster");
        incident.logEvent("Fixed Redis config, traffic restored");

        var rca = new RootCauseAnalysis(incident.id(), "Misconfigured Redis maxmemory-policy");
        rca.addFactor("Recent Redis config change was not reviewed");
        rca.addFactor("No canary for Redis config changes");
        rca.addFactor("Missing alert on Redis eviction rate");
        rca.addActionItem("Add Redis config change review to change management", "SRE", "2024-08-15");
        rca.addActionItem("Deploy Redis config canary tooling", "Payment Team", "2024-09-01");

        System.out.println("=== Incident Timeline ===");
        incident.timeline().forEach(e -> System.out.println("  " + e));

        System.out.println("\n=== RCA ===");
        System.out.println(rca.report());
    }
}

record IncidentEvent(Instant timestamp, String description) {}

class Incident {
    private final String id;
    private final String severity;
    private final String title;
    private final Map<String, String> roles = new ConcurrentHashMap<>();
    private final List<String> smes = new CopyOnWriteArrayList<>();
    private final List<String> events = new CopyOnWriteArrayList<>();
    private String commander;

    Incident(String id, String severity, String title) {
        this.id = id;
        this.severity = severity;
        this.title = title;
    }

    void assignCommander(String commander) { this.commander = commander; }
    void assignRole(String role, String person) { roles.put(role, person); }
    void addSME(String team, String person) { smes.add(person + " (" + team + ")"); }
    void logEvent(String description) { events.add("[" + Instant.now() + "] " + description); }

    List<String> timeline() {
        var tl = new ArrayList<String>();
        tl.add("Incident: " + id + " (" + severity + ") - " + title);
        tl.add("Commander: " + commander);
        roles.forEach((r, p) -> tl.add("  " + r + ": " + p));
        smes.forEach(s -> tl.add("  SME: " + s));
        tl.add("--- Timeline ---");
        tl.addAll(events);
        return tl;
    }
}

class EscalationPolicy {
    private final Map<String, Duration> severityTimeouts = new ConcurrentHashMap<>();

    void setSeverityTimeout(String severity, Duration timeout) {
        severityTimeouts.put(severity, timeout);
    }

    Duration getTimeout(String severity) {
        return severityTimeouts.getOrDefault(severity, Duration.ofMinutes(15));
    }
}

class RootCauseAnalysis {
    private final String incidentId;
    private final String rootCause;
    private final List<String> contributingFactors = new ArrayList<>();
    private final List<String> actionItems = new ArrayList<>();

    RootCauseAnalysis(String incidentId, String rootCause) {
        this.incidentId = incidentId;
        this.rootCause = rootCause;
    }

    void addFactor(String factor) { contributingFactors.add(factor); }
    void addActionItem(String item, String owner, String dueDate) {
        actionItems.add(item + " (owner: " + owner + ", due: " + dueDate + ")");
    }

    String report() {
        var sb = new StringBuilder();
        sb.append("RCA for ").append(incidentId).append("\n");
        sb.append("Root Cause: ").append(rootCause).append("\n");
        sb.append("Contributing Factors:\n");
        contributingFactors.forEach(f -> sb.append("  - ").append(f).append("\n"));
        sb.append("Action Items:\n");
        actionItems.forEach(a -> sb.append("  - [ ] ").append(a).append("\n"));
        return sb.toString();
    }
}
