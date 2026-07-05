package com.cloud.storage;

import java.util.*;

public class S3BucketPolicy {

    public enum Effect { ALLOW, DENY }

    public static class PolicyStatement {
        public final Effect effect;
        public final String principal;
        public final String action;
        public final String resource;
        public final Map<String, String> conditions;

        public PolicyStatement(Effect effect, String principal, String action,
                               String resource, Map<String, String> conditions) {
            this.effect = effect;
            this.principal = principal;
            this.action = action;
            this.resource = resource;
            this.conditions = conditions;
        }

        public boolean matches(String requester, String reqAction, String reqResource,
                               Map<String, String> reqContext) {
            boolean principalMatch = principal.equals("*") || principal.equals(requester);
            boolean actionMatch = action.equals("*") || action.equals(reqAction);
            boolean resourceMatch = resource.equals("*") || resource.equals(reqResource);
            boolean conditionsMatch = true;
            if (conditions != null) {
                for (Map.Entry<String, String> c : conditions.entrySet()) {
                    if (!c.getValue().equals(reqContext.get(c.getKey()))) {
                        conditionsMatch = false;
                        break;
                    }
                }
            }
            return principalMatch && actionMatch && resourceMatch && conditionsMatch;
        }

        @Override
        public String toString() {
            return effect + " " + principal + " " + action + " on " + resource + " cond=" + conditions;
        }
    }

    public static class BucketPolicy {
        private final String bucketName;
        private final List<PolicyStatement> statements = new ArrayList<>();

        public BucketPolicy(String bucketName) { this.bucketName = bucketName; }

        public void addStatement(PolicyStatement stmt) {
            statements.add(stmt);
        }

        public boolean evaluate(String requester, String action, String resource,
                                Map<String, String> context) {
            boolean allowed = false;
            for (PolicyStatement stmt : statements) {
                if (stmt.matches(requester, action, resource, context)) {
                    if (stmt.effect == Effect.DENY) {
                        System.out.println("  DENY by: " + stmt);
                        return false;
                    }
                    allowed = true;
                }
            }
            System.out.println("  " + (allowed ? "ALLOW" : "DENY (default)") + " " + requester
                + " " + action + " on " + resource);
            return allowed;
        }
    }

    public static void main(String[] args) {
        BucketPolicy policy = new BucketPolicy("my-app-bucket");

        policy.addStatement(new PolicyStatement(Effect.ALLOW, "*", "s3:GetObject",
            "arn:aws:s3:::my-app-bucket/public/*", null));

        policy.addStatement(new PolicyStatement(Effect.DENY, "*", "s3:DeleteObject",
            "arn:aws:s3:::my-app-bucket/*",
            Map.of("aws:SourceIp", "0.0.0.0/0")));

        policy.addStatement(new PolicyStatement(Effect.ALLOW, "arn:aws:iam::123456:role/AppRole",
            "s3:*", "arn:aws:s3:::my-app-bucket/*", null));

        System.out.println("=== S3 Bucket Policy ===");
        policy.evaluate("anonymous", "s3:GetObject",
            "arn:aws:s3:::my-app-bucket/public/readme.txt", Map.of());
        policy.evaluate("arn:aws:iam::123456:role/AppRole", "s3:PutObject",
            "arn:aws:s3:::my-app-bucket/data/file.txt", Map.of());
        policy.evaluate("hacker", "s3:DeleteObject",
            "arn:aws:s3:::my-app-bucket/data/file.txt", Map.of("aws:SourceIp", "10.0.0.1"));
    }
}
