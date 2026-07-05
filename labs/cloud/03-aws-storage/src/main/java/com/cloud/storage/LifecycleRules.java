package com.cloud.storage;

import java.util.*;
import java.util.concurrent.*;

public class LifecycleRules {

    public enum StorageClass { STANDARD, STANDARD_IA, INTELLIGENT_TIERING, GLACIER, DEEP_ARCHIVE }

    public static class LifecycleTransition {
        public final StorageClass targetClass;
        public final int daysAfterCreation;

        public LifecycleTransition(StorageClass targetClass, int days) {
            this.targetClass = targetClass;
            this.daysAfterCreation = days;
        }

        @Override
        public String toString() {
            return "-> " + targetClass + " after " + daysAfterCreation + " days";
        }
    }

    public static class LifecycleRule {
        public final String id;
        public final String prefix;
        public final List<LifecycleTransition> transitions = new ArrayList<>();
        public Integer expirationDays;

        public LifecycleRule(String id, String prefix) {
            this.id = id;
            this.prefix = prefix;
        }

        public LifecycleRule addTransition(StorageClass target, int days) {
            transitions.add(new LifecycleTransition(target, days));
            return this;
        }

        public LifecycleRule setExpiration(int days) {
            this.expirationDays = days;
            return this;
        }

        @Override
        public String toString() {
            return "Rule '" + id + "' for " + prefix + "*: " + transitions
                + (expirationDays != null ? " expire after " + expirationDays + " days" : "");
        }
    }

    public static class LifecycleManager {
        private final List<LifecycleRule> rules = new ArrayList<>();
        private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        public void addRule(LifecycleRule rule) {
            rules.add(rule);
            System.out.println("Added: " + rule);
        }

        public void start() {
            scheduler.scheduleAtFixedRate(this::evaluate, 0, 10, TimeUnit.SECONDS);
        }

        private void evaluate() {
            System.out.println("\nEvaluating lifecycle rules...");
            for (LifecycleRule rule : rules) {
                System.out.println("  Checking: " + rule.id);
            }
        }

        public List<LifecycleRule> getApplicableRules(String objectKey) {
            List<LifecycleRule> applicable = new ArrayList<>();
            for (LifecycleRule rule : rules) {
                if (objectKey.startsWith(rule.prefix)) {
                    applicable.add(rule);
                }
            }
            return applicable;
        }

        public void shutdown() { scheduler.shutdown(); }
    }

    public static void main(String[] args) {
        LifecycleManager manager = new LifecycleManager();

        manager.addRule(new LifecycleRule("logs-transition", "logs/")
            .addTransition(StorageClass.STANDARD_IA, 30)
            .addTransition(StorageClass.GLACIER, 90)
            .setExpiration(365));

        manager.addRule(new LifecycleRule("temp-files", "tmp/")
            .setExpiration(7));

        manager.addRule(new LifecycleRule("media-files", "media/")
            .addTransition(StorageClass.INTELLIGENT_TIERING, 0)
            .addTransition(StorageClass.DEEP_ARCHIVE, 180));

        System.out.println("\n=== Lifecycle Rules ===");
        rules.forEach(System.out::println);

        System.out.println("\nRules for 'logs/app.log':");
        manager.getApplicableRules("logs/app.log").forEach(r -> System.out.println("  " + r.id));

        manager.shutdown();
    }
}
