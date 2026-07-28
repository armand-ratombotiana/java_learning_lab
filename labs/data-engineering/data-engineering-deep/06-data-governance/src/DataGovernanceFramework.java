package com.dataengineering.deep.lab06;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

public class DataGovernanceFramework {

    public record User(String id, String name, List<String> roles, boolean active) {}

    public record Role(String name, List<Permission> permissions) {}

    public record Permission(String resourceType, String action, String effect) {}

    public enum Classification { PUBLIC, INTERNAL, CONFIDENTIAL, PII, RESTRICTED }

    public record AuditEvent(String userId, String resourceId, String action, Instant timestamp,
                             String ipAddress, boolean granted, String reason) {}

    public static class AuditLogger {
        private final List<AuditEvent> events = new CopyOnWriteArrayList<>();
        public void log(AuditEvent event) { events.add(event); }
        public List<AuditEvent> queryByUser(String userId, Instant from, Instant to) {
            return events.stream()
                .filter(e -> e.userId().equals(userId) && e.timestamp().isAfter(from) && e.timestamp().isBefore(to))
                .toList();
        }
        public List<AuditEvent> queryByResource(String resourceId) {
            return events.stream().filter(e -> e.resourceId().equals(resourceId)).toList();
        }
        public long countByGranted(boolean granted) {
            return events.stream().filter(e -> e.granted() == granted).count();
        }
    }

    public static class DataClassifier {
        private final Map<Pattern, Classification> rules = new LinkedHashMap<>();
        public void addRule(Pattern pattern, Classification classification) { rules.put(pattern, classification); }
        public Classification classify(String columnName) {
            return rules.entrySet().stream()
                .filter(e -> e.getKey().matcher(columnName).matches())
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(Classification.PUBLIC);
        }
    }

    public static class DataMasker {
        public String mask(String value, Classification classification) {
            if (value == null) return null;
            return switch (classification) {
                case PII -> value.length() <= 4 ? "****"
                    : value.substring(0, 2) + "****" + value.substring(value.length() - 2);
                case CONFIDENTIAL, RESTRICTED -> "***REDACTED***";
                default -> value;
            };
        }
    }

    public record AccessRequest(User user, String datasetId, String queryType, List<String> columns,
                                String purpose, String ipAddress) {}

    public static class PolicyEngine {
        private final Map<String, List<Permission>> rolePermissions = new ConcurrentHashMap<>();
        private final Map<String, Classification> columnClassification = new ConcurrentHashMap<>();
        private final Set<String> allowedPurposes = new HashSet<>(Arrays.asList("analytics", "reporting", "ml_training"));

        public void assignPermission(String role, Permission permission) {
            rolePermissions.computeIfAbsent(role, k -> new ArrayList<>()).add(permission);
        }

        public void classifyColumn(String column, Classification c) { columnClassification.put(column, c); }

        public boolean evaluate(AccessRequest request) {
            if (!request.user().active()) return false;
            var roles = rolePermissions.entrySet().stream()
                .filter(e -> request.user().roles().contains(e.getKey()))
                .flatMap(e -> e.getValue().stream())
                .toList();
            boolean hasAccess = roles.stream().anyMatch(p ->
                p.resourceType().equals("dataset")
                && p.action().equals(request.queryType())
                && p.effect().equals("ALLOW"));
            if (!hasAccess) return false;
            for (String col : request.columns()) {
                Classification c = columnClassification.getOrDefault(col, Classification.PUBLIC);
                if (c == Classification.RESTRICTED && !request.user().roles().contains("admin")) return false;
            }
            if (!allowedPurposes.contains(request.purpose())) return false;
            return true;
        }
    }

    public static class PolicyEnforcementProxy {
        private final PolicyEngine engine;
        private final DataMasker masker;
        private final DataClassifier classifier;
        private final AuditLogger logger;

        public PolicyEnforcementProxy(PolicyEngine engine, DataMasker masker, DataClassifier classifier, AuditLogger logger) {
            this.engine = engine; this.masker = masker; this.classifier = classifier; this.logger = logger;
        }

        public Map<String, String> execute(AccessRequest request, Map<String, String> rawData) {
            boolean granted = engine.evaluate(request);
            logger.log(new AuditEvent(request.user().id(), request.datasetId(), request.queryType(),
                Instant.now(), request.ipAddress(), granted, granted ? "ALLOWED" : "DENIED"));
            if (!granted) throw new RuntimeException("Access denied for user: " + request.user().id());
            Map<String, String> masked = new HashMap<>();
            for (var entry : rawData.entrySet()) {
                Classification c = classifier.classify(entry.getKey());
                masked.put(entry.getKey(), masker.mask(entry.getValue(), c));
            }
            return masked;
        }
    }

    public static void main(String[] args) {
        var user = new User("u1", "Alice", List.of("analyst", "admin"), true);
        var request = new AccessRequest(user, "ds_customers", "READ", List.of("name", "email", "ssn", "age"),
            "analytics", "10.0.0.1");
        var engine = new PolicyEngine();
        engine.assignPermission("analyst", new Permission("dataset", "READ", "ALLOW"));
        engine.classifyColumn("email", Classification.PII);
        engine.classifyColumn("ssn", Classification.RESTRICTED);
        engine.classifyColumn("age", Classification.INTERNAL);
        var classifier = new DataClassifier();
        classifier.addRule(Pattern.compile(".*email.*"), Classification.PII);
        classifier.addRule(Pattern.compile(".*ssn.*"), Classification.RESTRICTED);
        var proxy = new PolicyEnforcementProxy(engine, new DataMasker(), classifier, new AuditLogger());
        var data = Map.of("name", "Alice Smith", "email", "alice@example.com", "ssn", "123-45-6789", "age", "30");
        var result = proxy.execute(request, data);
        System.out.println("Masked result: " + result);
        System.out.println("Audit denials: " + proxy.logger.countByGranted(false));
    }
}
