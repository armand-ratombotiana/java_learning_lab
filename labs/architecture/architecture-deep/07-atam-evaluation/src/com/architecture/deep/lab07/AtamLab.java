package com.architecture.deep.lab07;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class AtamLab {
    public static void main(String[] args) {
        var evaluation = new AtamEvaluation("E-Commerce Platform v2");
        evaluation.addDriver("Scale to 10M active users");
        evaluation.addDriver("99.99% uptime");
        evaluation.addDriver("Sub-200ms p95 latency");
        evaluation.addDriver("Deploy 5x per day");

        evaluation.addScenario("Performance", "HighLoad",
            "Stimulus: 100K concurrent users during flash sale",
            "Response: p95 latency under 200ms within 30s of spike");

        evaluation.addScenario("Availability", "RegionFailure",
            "Stimulus: Entire AWS us-east-1 region goes down",
            "Response: System fails over to us-west-2 within 60s with zero data loss");

        evaluation.addScenario("Security", "DataBreach",
            "Stimulus: Attacker gains network access",
            "Response: All sensitive data encrypted at rest and in transit; intrusion detected within 5s");

        evaluation.addApproach("Microservices + Event-Driven", "Scalability+",
            "Complexity-", "Improves scalability and deployability but increases operational complexity");
        evaluation.addApproach("CQRS + Read Replicas", "Performance+",
            "Consistency-", "Improves read performance but introduces eventual consistency");
        evaluation.addApproach("Database Sharding", "Scalability+",
            "QueryComplexity-", "Horizontal scaling works but cross-shard queries are complex");

        System.out.println(evaluation.report());
    }
}

record QualityScenario(String category, String name, String stimulus, String response) {}
record ArchitecturalDecision(String name, String pros, String cons, String tradeoff) {}

class AtamEvaluation {
    private final String systemName;
    private final List<String> drivers = new ArrayList<>();
    private final List<QualityScenario> scenarios = new ArrayList<>();
    private final List<ArchitecturalDecision> decisions = new ArrayList<>();

    AtamEvaluation(String systemName) { this.systemName = systemName; }

    void addDriver(String driver) { drivers.add(driver); }
    void addScenario(String category, String name, String stimulus, String response) {
        scenarios.add(new QualityScenario(category, name, stimulus, response));
    }
    void addApproach(String name, String pros, String cons, String tradeoff) {
        decisions.add(new ArchitecturalDecision(name, pros, cons, tradeoff));
    }

    String report() {
        var sb = new StringBuilder();
        sb.append("ATAM Evaluation: ").append(systemName).append("\n");
        sb.append("=" .repeat(45)).append("\n\n");
        sb.append("Business Drivers:\n");
        drivers.forEach(d -> sb.append("  - ").append(d).append("\n"));
        sb.append("\nUtility Tree (Scenarios):\n");
        scenarios.forEach(s -> sb.append("  [" + s.category() + "] " + s.name() + "\n    Stimulus: " + s.stimulus() + "\n    Response: " + s.response() + "\n\n"));
        sb.append("Architectural Decisions & Tradeoffs:\n");
        decisions.forEach(d -> sb.append("  " + d.name() + "\n    +: " + d.pros() + "\n    -: " + d.cons() + "\n    Tradeoff: " + d.tradeoff() + "\n\n"));
        return sb.toString();
    }
}
