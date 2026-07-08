package com.cloud.awssec;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class IAMPolicyEngine {

    public record PolicyStatement(String effect, List<String> actions, List<String> resources, Map<String, String> conditions) {}

    public record Policy(String policyName, List<PolicyStatement> statements) {}

    public static class PolicyEvaluator {
        public boolean evaluate(Policy policy, String action, String resource, Map<String, String> context) {
            boolean allow = false;
            for (PolicyStatement stmt : policy.statements()) {
                boolean actionMatch = stmt.actions().stream().anyMatch(a -> wildcardMatch(a, action));
                boolean resourceMatch = stmt.resources().stream().anyMatch(r -> wildcardMatch(r, resource));
                boolean conditionMatch = evaluateConditions(stmt.conditions(), context);

                if (actionMatch && resourceMatch && conditionMatch) {
                    if ("DENY".equalsIgnoreCase(stmt.effect())) return false;
                    if ("ALLOW".equalsIgnoreCase(stmt.effect())) allow = true;
                }
            }
            return allow;
        }

        private boolean wildcardMatch(String pattern, String value) {
            if (pattern.equals("*")) return true;
            if (pattern.endsWith("*")) return value.startsWith(pattern.substring(0, pattern.length() - 1));
            return pattern.equals(value);
        }

        private boolean evaluateConditions(Map<String, String> conditions, Map<String, String> context) {
            if (conditions.isEmpty()) return true;
            return conditions.entrySet().stream().allMatch(e -> {
                String contextVal = context.get(e.getKey());
                return contextVal != null && contextVal.equals(e.getValue());
            });
        }
    }

    public record KmsKey(String keyId, String keySpec, boolean enabled) {}

    public static class KmsSimulator {
        private final Map<String, KmsKey> keys = new ConcurrentHashMap<>();

        public KmsKey createKey(String keySpec) {
            String keyId = "arn:aws:kms:us-east-1:123456789012:key/" + UUID.randomUUID();
            KmsKey key = new KmsKey(keyId, keySpec, true);
            keys.put(keyId, key);
            return key;
        }

        public byte[] encrypt(String keyId, byte[] plaintext) {
            if (!keys.containsKey(keyId)) throw new IllegalArgumentException("Key not found");
            byte[] ciphertext = new byte[plaintext.length];
            for (int i = 0; i < plaintext.length; i++) {
                ciphertext[i] = (byte) (plaintext[i] ^ keyId.hashCode() & 0xFF);
            }
            return Base64.getEncoder().encode(ciphertext);
        }

        public byte[] decrypt(String keyId, byte[] ciphertext) {
            if (!keys.containsKey(keyId)) throw new IllegalArgumentException("Key not found");
            byte[] decoded = Base64.getDecoder().decode(ciphertext);
            byte[] plaintext = new byte[decoded.length];
            for (int i = 0; i < decoded.length; i++) {
                plaintext[i] = (byte) (decoded[i] ^ keyId.hashCode() & 0xFF);
            }
            return plaintext;
        }
    }

    public static class WafEngine {
        public enum Action { ALLOW, BLOCK, COUNT }

        public record WafRule(String name, String condition, Action action) {}

        private final List<WafRule> rules = new CopyOnWriteArrayList<>();

        public void addRule(WafRule rule) { rules.add(rule); }

        public Action evaluate(String sourceIp, String uri, String body) {
            return rules.stream()
                .filter(r -> matches(r.condition(), sourceIp, uri, body))
                .findFirst()
                .map(WafRule::action)
                .orElse(Action.ALLOW);
        }

        private boolean matches(String condition, String ip, String uri, String body) {
            return switch (condition) {
                case "XSS" -> body != null && body.contains("<script>");
                case "SQLI" -> body != null && body.toLowerCase().contains("union select");
                case "RATE_LIMIT" -> true;
                default -> false;
            };
        }
    }

    public static void main(String[] args) {
        PolicyEvaluator evaluator = new PolicyEvaluator();
        Policy policy = new Policy("S3Access", List.of(
            new PolicyStatement("ALLOW", List.of("s3:GetObject"), List.of("arn:aws:s3:::my-bucket/*"), Map.of()),
            new PolicyStatement("DENY", List.of("s3:*"), List.of("arn:aws:s3:::my-bucket/secret/*"), Map.of())
        ));
        boolean result = evaluator.evaluate(policy, "s3:GetObject", "arn:aws:s3:::my-bucket/data.txt", Map.of());
        System.out.println("Access allowed: " + result);
    }
}
