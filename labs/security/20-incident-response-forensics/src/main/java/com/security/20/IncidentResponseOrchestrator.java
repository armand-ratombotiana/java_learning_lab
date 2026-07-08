package com.security20;

import java.security.MessageDigest;
import java.time.Instant;
import java.time.Duration;
import java.util.*;

public class IncidentResponseOrchestrator {

    public Incident createIncident(String type, String source, String description) {
        Incident incident = new Incident();
        incident.id = UUID.randomUUID().toString();
        incident.type = type;
        incident.source = source;
        incident.description = description;
        incident.severity = classifySeverity(type);
        incident.status = IncidentStatus.DETECTED;
        incident.createdAt = Instant.now();
        return incident;
    }

    public String classifySeverity(String type) {
        return switch (type.toLowerCase()) {
            case "ransomware", "data_breach", "unauthorized_access" -> "CRITICAL";
            case "malware", "phishing_campaign" -> "HIGH";
            case "policy_violation", "suspicious_activity" -> "MEDIUM";
            default -> "LOW";
        };
    }

    public ContainmentPlan executeContainment(Incident incident) {
        ContainmentPlan plan = new ContainmentPlan();
        plan.incidentId = incident.id;
        plan.steps = new ArrayList<>();
        plan.steps.add(new ContainmentStep("ISOLATE_HOST", incident.source, 60));
        plan.steps.add(new ContainmentStep("DISABLE_USER", incident.affectedUsers.get(0), 30));
        plan.steps.add(new ContainmentStep("BLOCK_INDICATORS", "c2_domain", 10));
        plan.steps.add(new ContainmentStep("CAPTURE_EVIDENCE", incident.source, 300));
        return plan;
    }

    public ForensicEvidence collectEvidence(String source, EvidenceType type) throws Exception {
        ForensicEvidence evidence = new ForensicEvidence();
        evidence.id = UUID.randomUUID().toString();
        evidence.source = source;
        evidence.type = type;
        evidence.collectedAt = Instant.now();
        evidence.collector = "IR-Automation";
        byte[] content = ("Simulated forensic data from " + source).getBytes();
        evidence.hash = hashContent(content);
        evidence.content = Base64.getEncoder().encodeToString(content);
        evidence.status = ChainOfCustodyStatus.COLLECTED;
        return evidence;
    }

    private String hashContent(byte[] content) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return Base64.getEncoder().encodeToString(md.digest(content));
    }

    public enum IncidentStatus { DETECTED, ANALYZING, CONTAINING, ERADICATING, RECOVERED, CLOSED }
    public enum EvidenceType { MEMORY_DUMP, DISK_IMAGE, NETWORK_CAPTURE, LOG_EXPORT }
    public enum ChainOfCustodyStatus { COLLECTED, TRANSFERRED, ANALYZED, STORED, RETURNED }

    public static class Incident {
        public String id, type, source, description, severity;
        public IncidentStatus status;
        public Instant createdAt;
        public List<String> affectedUsers = new ArrayList<>();
    }

    public static class ContainmentPlan {
        public String incidentId;
        public List<ContainmentStep> steps;
    }

    public record ContainmentStep(String action, String target, int timeoutSeconds) {}

    public static class ForensicEvidence {
        public String id, source, collector, hash, content;
        public EvidenceType type;
        public Instant collectedAt;
        public ChainOfCustodyStatus status;
    }

    public static void main(String[] args) throws Exception {
        IncidentResponseOrchestrator ir = new IncidentResponseOrchestrator();
        Incident incident = ir.createIncident("RANSOMWARE", "server-01", "Files encrypted with .enc extension detected");
        System.out.println("Incident: " + incident.id + " Severity: " + incident.severity);
        ContainmentPlan plan = ir.executeContainment(incident);
        plan.steps.forEach(s -> System.out.println("  " + s.action() + " -> " + s.target()));
        ForensicEvidence evidence = ir.collectEvidence("server-01", EvidenceType.MEMORY_DUMP);
        System.out.println("Evidence hash: " + evidence.hash);
    }
}
