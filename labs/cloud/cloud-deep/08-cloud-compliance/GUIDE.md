# GUIDE — Cloud Compliance

## Step 1: Compliance Framework Model
```java
public record Control(String id, String framework, String category, String description, boolean implemented) {}
public record ComplianceFramework(String name, String version, List<Control> controls) {}
```

## Step 2: Immutable Audit Log
```java
public record AuditEntry(String id, Instant timestamp, String principal, String action, String resource, byte[] hash, byte[] previousHash) {}
```
Implement hash chain for tamper evidence.

## Step 3: Data Residency Engine
- Classify data by sensitivity (public, internal, confidential, restricted)
- Enforce geographic storage constraints per classification
- Block operations that violate residency rules

## Step 4: Evidence Collection
```java
EvidenceCollector collector = new EvidenceCollector();
collector.collect("IAM-01", "IAM roles configured");
collector.collect("LOG-03", "Audit logging enabled");
```

## Step 5: Compliance Dashboard
- Control implementation status per framework
- Gap analysis and remediation tracking
- Auditor-ready report generation

## Step 6: Exercises
1. Implement a GDPR data subject request handler (access, delete, portability)
2. Build a PCI DSS scope validation tool for cardholder data environments
3. Create a FedRAMP continuous monitoring metrics collector
