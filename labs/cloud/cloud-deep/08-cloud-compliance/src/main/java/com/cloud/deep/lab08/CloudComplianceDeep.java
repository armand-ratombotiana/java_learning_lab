package com.cloud.deep.lab08;

import java.security.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class CloudComplianceDeep {

    public record Control(String id, String framework, String category, String description, boolean implemented, Instant implementedAt) {}
    public record ComplianceFramework(String name, String version, List<Control> controls) {}

    public record AuditEntry(String id, Instant timestamp, String principal, String action, String resource, String hash, String previousHash) {
        public static String computeHash(String data) {
            try {
                var md = MessageDigest.getInstance("SHA-256");
                return Base64.getEncoder().encodeToString(md.digest(data.getBytes()));
            } catch (NoSuchAlgorithmException e) { throw new RuntimeException(e); }
        }
    }

    public record DataClassification(String label, int level, List<String> allowedRegions) {}
    public record DataAsset(String id, String name, DataClassification classification, String currentRegion) {}

    public static class ImmutableAuditLog {
        private final List<AuditEntry> entries = new CopyOnWriteArrayList<>();
        private volatile String lastHash = AuditEntry.computeHash("genesis");

        public AuditEntry log(String principal, String action, String resource) {
            var id = UUID.randomUUID().toString();
            var entry = new AuditEntry(id, Instant.now(), principal, action, resource,
                AuditEntry.computeHash(id + principal + action + resource + lastHash), lastHash);
            lastHash = entry.hash();
            entries.add(entry);
            return entry;
        }

        public boolean verifyIntegrity() {
            String prev = AuditEntry.computeHash("genesis");
            for (var entry : entries) {
                var expected = AuditEntry.computeHash(entry.id() + entry.principal() + entry.action() + entry.resource() + prev);
                if (!expected.equals(entry.hash())) return false;
                prev = entry.hash();
            }
            return true;
        }

        public List<AuditEntry> getEntries() { return List.copyOf(entries); }
    }

    public static class DataResidencyEngine {
        private final List<DataClassification> classifications = List.of(
            new DataClassification("Public", 1, List.of("US", "EU", "APAC")),
            new DataClassification("Internal", 2, List.of("US", "EU")),
            new DataClassification("Confidential", 3, List.of("US")),
            new DataClassification("Restricted", 4, List.of("US-East"))
        );

        public boolean isStorageAllowed(DataAsset asset, String targetRegion) {
            return asset.classification().allowedRegions().contains(targetRegion);
        }

        public DataClassification classifyByLevel(int level) {
            return classifications.stream().filter(c -> c.level() == level).findFirst().orElse(classifications.get(0));
        }
    }

    public static class EvidenceCollector {
        private final Map<String, List<String>> evidence = new ConcurrentHashMap<>();

        public void collect(String controlId, String evidenceDetail) {
            evidence.computeIfAbsent(controlId, k -> new CopyOnWriteArrayList<>()).add(evidenceDetail);
        }

        public Map<String, List<String>> getEvidence() { return Map.copyOf(evidence); }

        public String generateReport() {
            var sb = new StringBuilder();
            sb.append("Compliance Evidence Report\n");
            sb.append("==========================\n");
            for (var entry : evidence.entrySet()) {
                sb.append("Control ").append(entry.getKey()).append(":\n");
                for (var detail : entry.getValue()) {
                    sb.append("  - ").append(detail).append("\n");
                }
            }
            return sb.toString();
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Compliance Frameworks ===");
        var soc2 = new ComplianceFramework("SOC 2", "2024", List.of(
            new Control("CC1.1", "SOC 2", "Security", "Board oversight", true, Instant.now()),
            new Control("CC6.1", "SOC 2", "Security", "Logical access controls", true, Instant.now()),
            new Control("CC7.2", "SOC 2", "Security", "Monitoring of controls", false, Instant.now())
        ));
        long implemented = soc2.controls().stream().filter(Control::implemented).count();
        System.out.printf("SOC 2: %d/%d controls implemented%n", implemented, soc2.controls().size());

        System.out.println("\n=== Immutable Audit Log ===");
        var audit = new ImmutableAuditLog();
        audit.log("admin@corp.com", "CREATE", "s3://bucket/config.json");
        audit.log("ci-bot", "MODIFY", "iam/role/ProdAdmin");
        audit.log("admin@corp.com", "DELETE", "s3://bucket/old-backup.zip");
        System.out.println("Audit entries: " + audit.getEntries().size());
        System.out.println("Log integrity: " + audit.verifyIntegrity());

        System.out.println("\n=== Data Residency ===");
        var engine = new DataResidencyEngine();
        var confidential = engine.classifyByLevel(3);
        var asset = new DataAsset("asset-001", "customer-db", confidential, "US");
        System.out.println("Can store " + asset.name() + " in EU? " + engine.isStorageAllowed(asset, "EU"));
        System.out.println("Can store " + asset.name() + " in US? " + engine.isStorageAllowed(asset, "US"));

        System.out.println("\n=== Evidence Report ===");
        var evidence = new EvidenceCollector();
        evidence.collect("CC6.1", "MFA enabled for all console users");
        evidence.collect("CC6.1", "Access keys rotated every 90 days");
        evidence.collect("CC7.2", "CloudTrail enabled in all regions");
        System.out.println(evidence.generateReport());
    }
}
