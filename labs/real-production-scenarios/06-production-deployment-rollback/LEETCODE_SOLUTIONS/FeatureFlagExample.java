package com.prod.solutions.deployment;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Demonstrates a simple feature flag system that can be used to
 * gradually roll out features and immediately disable them if issues arise.
 *
 * In production, use LaunchDarkly, Flagsmith, or a custom feature flag
 * service with: targeting rules, percentage rollouts, A/B testing,
 * and kill switches.
 */
public class FeatureFlagExample {

    static class FeatureFlag {
        private boolean enabled;
        private int rolloutPercentage;
        private final String description;

        FeatureFlag(String description, boolean enabled, int rolloutPercentage) {
            this.description = description;
            this.enabled = enabled;
            this.rolloutPercentage = rolloutPercentage;
        }

        boolean isEnabled(String userId) {
            if (!enabled) return false;
            // Check if user is in the rollout percentage
            int userBucket = Math.abs(userId.hashCode()) % 100;
            return userBucket < rolloutPercentage;
        }

        void setEnabled(boolean enabled) { this.enabled = enabled; }
        void setRolloutPercentage(int pct) { this.rolloutPercentage = pct; }
    }

    private final Map<String, FeatureFlag> flags = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        FeatureFlagExample ff = new FeatureFlagExample();

        // Create feature flags
        ff.registerFlag("new-payment-flow", "New payment processing pipeline", false, 0);
        ff.registerFlag("v2-search", "Improved search algorithm", true, 10);
        ff.registerFlag("dark-mode", "Dark mode UI", true, 100);

        System.out.println("=== Feature Flag System Demo ===");
        System.out.println("Feature flags allow gradual rollout and instant kill switches.\n");

        // Simulate requests
        String[] users = {"user-001", "user-002", "user-003", "user-004", "user-005"};

        for (String user : users) {
            System.out.printf("User: %s%n", user);
            System.out.printf("  new-payment-flow: %s%n", ff.isEnabled("new-payment-flow", user));
            System.out.printf("  v2-search:        %s%n", ff.isEnabled("v2-search", user));
            System.out.printf("  dark-mode:        %s%n", ff.isEnabled("dark-mode", user));
        }

        // Simulate: errors detected on v2-search → kill switch
        System.out.println("\n>>> Error rate spike detected on v2-search!");
        System.out.println(">>> Flipping kill switch to OFF...");
        ff.disableFlag("v2-search");

        String user = "user-002";
        System.out.printf("User %s v2-search after kill switch: %s%n",
                user, ff.isEnabled("v2-search", user));

        // Graduate after stabilization
        System.out.println("\n>>> Bug fixed, graduating to 100% rollout...");
        ff.setRollout("v2-search", 100);

        System.out.println("\n=== Feature flags prevent deployment disasters ===");
    }

    void registerFlag(String name, String description, boolean enabled, int pct) {
        flags.put(name, new FeatureFlag(description, enabled, pct));
    }

    boolean isEnabled(String name, String userId) {
        FeatureFlag flag = flags.get(name);
        return flag != null && flag.isEnabled(userId);
    }

    void disableFlag(String name) {
        FeatureFlag flag = flags.get(name);
        if (flag != null) flag.setEnabled(false);
        System.out.printf("  [KILL SWITCH] Feature '%s' disabled globally%n", name);
    }

    void setRollout(String name, int pct) {
        FeatureFlag flag = flags.get(name);
        if (flag != null) {
            flag.setEnabled(true);
            flag.setRolloutPercentage(pct);
            System.out.printf("  [ROLLOUT] Feature '%s' set to %d%% rollout%n", name, pct);
        }
    }
}
