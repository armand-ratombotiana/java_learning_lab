package com.learning.incident;

import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

public class Lab {

    enum Severity { SEV1, SEV2, SEV3, SEV4 }
    enum Status { DETECTED, TRIAGING, MITIGATING, MONITORING, RESOLVED }

    record Incident(
        String id, String title, Severity severity, Status status,
        Instant detectedAt, String assignedTo, String summary
    ) {
        Incident mitigate() { return new Incident(id, title, severity, Status.MITIGATING, detectedAt, assignedTo, summary); }
        Incident resolve() { return new Incident(id, title, severity, Status.RESOLVED, detectedAt, assignedTo, summary); }

        Duration timeToDetect() { return Duration.between(detectedAt, Instant.now()); }

        static Incident create(String title, Severity sev, String assignee) {
            var id = "INC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            return new Incident(id, title, sev, Status.DETECTED, Instant.now(), assignee, "");
        }
    }

    static class IncidentManager {
        private final List<Incident> incidents = new CopyOnWriteArrayList<>();
        private final List<String> timeline = new CopyOnWriteArrayList<>();

        Incident report(String title, Severity sev, String assignee) {
            var inc = Incident.create(title, sev, assignee);
            incidents.add(inc);
            timeline.add("[" + inc.id() + "] " + sev + " " + title + " - assigned to " + assignee);
            return inc;
        }

        void escalate(Incident inc, String newAssignee) {
            timeline.add("[" + inc.id() + "] Escalated to " + newAssignee);
        }

        void resolve(Incident inc) {
            inc.resolve();
            timeline.add("[" + inc.id() + "] Resolved");
        }

        void printTimeline() { timeline.forEach(System.out::println); }

        List<Incident> openIncidents() {
            return incidents.stream().filter(i -> i.status() != Status.RESOLVED).toList();
        }
    }

    static class Runbook {
        private final String name;
        private final List<String> steps = new ArrayList<>();

        Runbook(String name) { this.name = name; }

        Runbook addStep(String step) { steps.add(step); return this; }

        void execute() {
            System.out.println("  Executing runbook: " + name);
            for (int i = 0; i < steps.size(); i++) {
                System.out.println("    Step " + (i + 1) + ": " + steps.get(i));
                try { Thread.sleep(50); } catch (InterruptedException e) {}
            }
            System.out.println("  Runbook complete");
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Incident Response Lab ===\n");

        incidentLifecycle();
        severityClassification();
        runbooks();
        postmortem();
        communication();
    }

    static void incidentLifecycle() {
        System.out.println("--- Incident Lifecycle ---");
        var manager = new IncidentManager();

        var inc1 = manager.report("High CPU on payment service", Severity.SEV2, "Alice");
        var inc2 = manager.report("Database connection pool exhausted", Severity.SEV1, "Bob");
        var inc3 = manager.report("Deployment failed on staging", Severity.SEV4, "Carol");

        manager.escalate(inc2, "Dave (on-call lead)");
        manager.resolve(inc1);
        manager.resolve(inc2);

        System.out.println("  Timeline:");
        manager.printTimeline();
        System.out.println("  Open incidents: " + manager.openIncidents().size());

        System.out.println("""
  Lifecycle: Detect -> Triage -> Mitigate -> Resolve -> Follow-up
  Detection: monitoring alerts, user reports, synthetic checks
  Triage: severity classification, assignee, initial assessment
  Mitigate: stop the bleeding (rollback, circuit break, scale up)
  Resolve: apply permanent fix, verify recovery
  Follow-up: postmortem, action items
    """);
    }

    static void severityClassification() {
        System.out.println("\n--- Severity Classification ---");
        System.out.println("""
  SEV1 (Critical):
  - Full service outage affecting all users
  - Data loss or corruption
  - Security breach
  Response: <15min, immediate escalation, war room
  SLA resolution: <1 hour

  SEV2 (High):
  - Major feature unavailable for subset of users
  - Performance degradation >50%
  - Partial data loss
  Response: <30min, assign senior engineer
  SLA resolution: <4 hours

  SEV3 (Medium):
  - Minor feature unavailable
  - Performance degradation <50%
  - Cosmetic issues
  Response: <2 hours, next business day fix
  SLA resolution: <24 hours

  SEV4 (Low):
  - Bug with workaround
  - Non-production environment issue
  - Documentation error
  Response: next sprint, best effort
    """);
    }

    static void runbooks() {
        System.out.println("\n--- Runbooks ---");

        var highCpu = new Runbook("High CPU / OOM Kill")
            .addStep("Check incident details and affected service")
            .addStep("Check Grafana dashboard for CPU/Memory trends")
            .addStep("Check recent deployments (last 1 hour)")
            .addStep("Check if auto-scaling is working")
            .addStep("Increase replica count if needed")
            .addStep("Check logs for error spike")
            .addStep("Rollback deployment if recent change caused issue")
            .addStep("Escalate to SRE if unresolved");

        highCpu.execute();

        System.out.println("""
  Runbooks are version-controlled (Git) and automated when possible
  Runbook automation: Rundeck, StackStorm, Ansible
    """);
    }

    static void postmortem() {
        System.out.println("\n--- Postmortem ---");
        System.out.println("""
  Postmortem structure (blameless):

  Title: [Date] Service outage postmortem
  Severity: SEV1
  Duration: 2026-05-07 14:30 - 15:45 UTC (75 min)

  Timeline:
    14:30 - PagerDuty alert: error rate spike to 25%
    14:32 - Engineer acknowledges
    14:35 - Identified: database connection pool exhaustion
    14:40 - Increased max_connections from 100 to 500
    14:45 - Error rate returns to normal
    15:00 - Root cause found in code review (connection leak)
    15:30 - Fix deployed and verified
    15:45 - Incident closed

  Root cause:
    Missing connection.close() in finally block for report generation.

  Action items:
    [P0] Add connection leak detection in staging tests
    [P1] Implement HikariCP connection leak detection
    [P2] Add database connection count dashboard alert
    [P3] Update runbook for connection pool exhaustion

  No blame - system improvement mindset
    """);
    }

    static void communication() {
        System.out.println("\n--- Incident Communication ---");
        System.out.println("""
  During incident:

  1. Initial notification (PagerDuty / OpsGenie):
     "[SEV1] Payment service down - error rate 100%"

  2. Status page update (Statuspage.io):
     "Investigating: Users may experience errors processing payments"

  3. Internal war room (Slack / Teams):
     #incident-payment-sev1 channel
     Regular status updates every 15min

  4. Stakeholder update (email / Slack):
     "We have identified the issue as a database connection pool
      exhaustion and are scaling up capacity. ETA 30 min."

  5. Resolution:
     "The issue has been resolved. A postmortem will follow."

  Best practices:
  - 1 incident commander (coordinator)
  - 1 scribe (timeline/documentation)
  - Regular updates even if no new info
  - Clear ownership: who is working on what
    """);
    }
}
