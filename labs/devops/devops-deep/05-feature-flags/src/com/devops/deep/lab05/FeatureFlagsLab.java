package com.devops.deep.lab05;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class FeatureFlagsLab {
    public static void main(String[] args) {
        var flagEngine = new FlagEngine();

        flagEngine.createFlag("new-checkout", "New checkout flow", true);
        flagEngine.createFlag("dark-mode", "Dark mode UI", false);
        flagEngine.createFlag("recommendation-engine", "AI recommendations", false);

        flagEngine.addTargeting("new-checkout", new PercentageRollout(50));
        flagEngine.addTargeting("recommendation-engine", new UserSegmentTargeting(Set.of("user-alpha", "user-beta")));

        var users = List.of("user-alpha", "user-beta", "user-gamma", "user-delta");
        for (var user : users) {
            var checkoutEnabled = flagEngine.evaluate("new-checkout", user);
            var darkMode = flagEngine.evaluate("dark-mode", user);
            var recommendations = flagEngine.evaluate("recommendation-engine", user);
            System.out.println(user + " | checkout=" + checkoutEnabled + " dark=" + darkMode + " recommendations=" + recommendations);
        }

        flagEngine.archiveFlag("dark-mode");
        System.out.println("\nFlags after archive: " + flagEngine.activeFlags());
    }
}

record FlagDefinition(String key, String description, boolean defaultVariation) {}

interface TargetingRule {
    boolean evaluate(String userKey);
}

record PercentageRollout(int percentage) implements TargetingRule {
    public boolean evaluate(String userKey) {
        return Math.abs(userKey.hashCode()) % 100 < percentage;
    }
}

record UserSegmentTargeting(Set<String> allowedUsers) implements TargetingRule {
    public boolean evaluate(String userKey) {
        return allowedUsers.contains(userKey);
    }
}

class FlagEngine {
    private final Map<String, FlagDefinition> flags = new ConcurrentHashMap<>();
    private final Map<String, List<TargetingRule>> targeting = new ConcurrentHashMap<>();
    private final Set<String> archived = ConcurrentHashMap.newKeySet();

    void createFlag(String key, String description, boolean defaultVariation) {
        flags.put(key, new FlagDefinition(key, description, defaultVariation));
    }

    void addTargeting(String flagKey, TargetingRule rule) {
        targeting.computeIfAbsent(flagKey, k -> new ArrayList<>()).add(rule);
    }

    boolean evaluate(String flagKey, String userKey) {
        if (archived.contains(flagKey)) return true;
        var def = flags.get(flagKey);
        if (def == null) return false;
        var rules = targeting.get(flagKey);
        if (rules == null) return def.defaultVariation();
        return rules.stream().allMatch(r -> r.evaluate(userKey));
    }

    void archiveFlag(String flagKey) { archived.add(flagKey); }

    Set<String> activeFlags() {
        var active = new HashSet<>(flags.keySet());
        active.removeAll(archived);
        return active;
    }
}
