# Lab 05: Problem Walkthrough — Feature Flag Engine with Targeting and Lifecycle

## Problem Statement

Implement a feature flag engine in pure Java 21+ that supports the full flag lifecycle.
Requirements:

1. **Flag definition**: each flag has a key, description, default variation, and owner.
2. **Targeting rules**: percentage rollouts (deterministic per user), user segments, and
   environment overrides; rules compose with AND semantics.
3. **Evaluation**: `evaluate(flagKey, userKey, context)` returns a boolean; context carries
   environment and user attributes so the same user can resolve differently in staging vs
   production.
4. **Kill switch**: an operator can hard-disable a flag — evaluation returns the default
   variation regardless of targeting (the emergency off switch).
5. **Lifecycle**: flags are created, can be archived, and archived flags disappear from active
   lists but still resolve deterministically.
6. **Deterministic demo**: the same users always get the same variations across runs.

## Constraints

- Java 21+ only, no external frameworks.
- No network: the engine is a library; the demo drives it from `main`.
- Percentage rollout must be sticky per user (same user always in the same bucket) — hashing,
  not a random call per evaluation.

## Approach

A feature flag engine is a routing problem with an escape hatch. The evaluation pipeline:
(1) kill switch — if on, return the default; (2) compile the targeting rules for the flag; (3)
if no rules, return the default; (4) otherwise evaluate every rule against the user context and
AND the results. Targeting rules are small strategy objects behind an interface, so new
strategies (server-side sticky, gradual ramps, attribute matching) plug in without touching the
engine.

Design decisions:

- **Deterministic rollout via bucketing**: `userKey.hashCode() % 100` with a fixed mod is
  stable per user across evaluations — the standard "consistent hashing for flags" trick. It
  makes the demo reproducible and the bucket assignment sticky.
- **Context as a record**: `EvaluationContext(environment, Map<String, String> attributes)`
  — rules read only what they need; the environment override rule matches `environment.equals`.
- **Kill switch beats targeting**: implemented as a per-flag flag checked first — the operator
  does not have to remember which rules exist, just flip one boolean.
- **Archived flags resolve, not crash**: evaluation returns the default for archived flags
  (they were already shipped everywhere), while `activeFlags()` filters them out of the
  management view.

## Step-by-Step Solution

### Step 1: Rule Strategies

```java
interface TargetingRule {
    boolean evaluate(String userKey, EvaluationContext context);
}

record PercentageRollout(int percentage) implements TargetingRule {
    @Override
    public boolean evaluate(String userKey, EvaluationContext context) {
        return Math.floorMod(userKey.hashCode(), 100) < percentage;
    }
}

record UserSegmentTargeting(Set<String> allowedUsers) implements TargetingRule {
    @Override
    public boolean evaluate(String userKey, EvaluationContext context) {
        return allowedUsers.contains(userKey);
    }
}
```

`Math.floorMod` (not `%`) guarantees a non-negative bucket even for negative hash codes — a
classic one-line bug in naive implementations.

### Step 2: Evaluation Pipeline

The engine evaluates kill switch -> rules -> default, in that order. Rules compose with AND:
a user gets the new behavior only when every rule passes, which is how you combine
"10% rollout" with "internal users first".

### Step 3: Lifecycle Operations

`archiveFlag` keeps the flag resolvable but removes it from `activeFlags()`; creating a flag
with an existing key updates the definition but never resets an active kill switch — the
operator's emergency state is preserved.

## Complete Solution

The full compilable file, `FeatureFlagsLab.java` in package `com.devops.deep.lab05`:

```java
package com.devops.deep.lab05;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class FeatureFlagsLab {
    public static void main(String[] args) {
        var engine = new FlagEngine();

        engine.createFlag("new-checkout", "New checkout flow", true, "platform");
        engine.createFlag("dark-mode", "Dark mode UI", false, "frontend");
        engine.createFlag("recommendation-engine", "AI recommendations", false, "ml");

        engine.addTargeting("new-checkout", new PercentageRollout(50));
        engine.addTargeting("recommendation-engine",
            new UserSegmentTargeting(Set.of("user-alpha", "user-beta")));
        engine.addTargeting("dark-mode", new EnvironmentOverride("staging", true));

        var prod = new EvaluationContext("production", Map.of("plan", "free"));
        var staging = new EvaluationContext("staging", Map.of("plan", "free"));

        System.out.println("Production evaluations:");
        for (var user : List.of("user-alpha", "user-beta", "user-gamma", "user-delta")) {
            System.out.println("  " + user
                + " | checkout=" + engine.evaluate("new-checkout", user, prod)
                + " dark=" + engine.evaluate("dark-mode", user, prod)
                + " recs=" + engine.evaluate("recommendation-engine", user, prod));
        }

        System.out.println("\nStaging evaluations (dark-mode forced on):");
        for (var user : List.of("user-alpha", "user-beta")) {
            System.out.println("  " + user
                + " | checkout=" + engine.evaluate("new-checkout", user, staging)
                + " dark=" + engine.evaluate("dark-mode", user, staging)
                + " recs=" + engine.evaluate("recommendation-engine", user, staging));
        }

        System.out.println("\nKill switch on recommendation-engine:");
        engine.setKillSwitch("recommendation-engine", true);
        System.out.println("  recs for user-alpha (in segment): "
            + engine.evaluate("recommendation-engine", "user-alpha", prod));

        engine.archiveFlag("dark-mode");
        System.out.println("\nActive flags after archiving dark-mode: "
            + engine.activeFlags());
    }
}

record EvaluationContext(String environment, Map<String, String> attributes) {}

interface TargetingRule {
    boolean evaluate(String userKey, EvaluationContext context);
}

record PercentageRollout(int percentage) implements TargetingRule {
    @Override
    public boolean evaluate(String userKey, EvaluationContext context) {
        return Math.floorMod(userKey.hashCode(), 100) < percentage;
    }
}

record UserSegmentTargeting(Set<String> allowedUsers) implements TargetingRule {
    @Override
    public boolean evaluate(String userKey, EvaluationContext context) {
        return allowedUsers.contains(userKey);
    }
}

record EnvironmentOverride(String environment, boolean enabled) implements TargetingRule {
    @Override
    public boolean evaluate(String userKey, EvaluationContext context) {
        return context.environment().equals(environment) && enabled;
    }
}

record FlagDefinition(String key, String description, boolean defaultVariation, String owner) {}

class FlagEngine {
    private final Map<String, FlagDefinition> flags = new LinkedHashMap<>();
    private final Map<String, List<TargetingRule>> targeting = new ConcurrentHashMap<>();
    private final Set<String> killSwitches = ConcurrentHashMap.newKeySet();
    private final Set<String> archived = ConcurrentHashMap.newKeySet();

    void createFlag(String key, String description, boolean defaultVariation, String owner) {
        flags.put(key, new FlagDefinition(key, description, defaultVariation, owner));
    }

    void addTargeting(String flagKey, TargetingRule rule) {
        targeting.computeIfAbsent(flagKey, k -> new ArrayList<>()).add(rule);
    }

    void setKillSwitch(String flagKey, boolean on) {
        if (on) {
            killSwitches.add(flagKey);
        } else {
            killSwitches.remove(flagKey);
        }
    }

    boolean evaluate(String flagKey, String userKey, EvaluationContext context) {
        if (killSwitches.contains(flagKey) || archived.contains(flagKey)) {
            return defaultOf(flagKey);
        }
        var rules = targeting.get(flagKey);
        if (rules == null || rules.isEmpty()) {
            return defaultOf(flagKey);
        }
        for (var rule : rules) {
            if (!rule.evaluate(userKey, context)) {
                return false;
            }
        }
        return true;
    }

    private boolean defaultOf(String flagKey) {
        var definition = flags.get(flagKey);
        return definition == null ? false : definition.defaultVariation();
    }

    void archiveFlag(String flagKey) {
        archived.add(flagKey);
    }

    Set<String> activeFlags() {
        var active = new LinkedHashSet<>(flags.keySet());
        active.removeAll(archived);
        return active;
    }
}
```

## Complexity Analysis

- **evaluate**: O(R) over the flag's targeting rules; each rule is O(1) (hash, set lookup, or
  string compare) — sub-microsecond per evaluation, cache-friendly for hot paths.
- **createFlag / addTargeting / setKillSwitch**: O(1) map operations.
- **activeFlags**: O(F) over flags; called by management UIs, not the request path.
- **Space**: O(F + R) for flags and rules; archived flags stay in the map (they must resolve)
  — cleanup is a separate TTL policy, not part of evaluation.

## Test Cases

| Scenario | Expected |
|---|---|
| `new-checkout` at 50% | Stable per-user buckets (here 1 of 4 demo users land in the first 50%) |
| Same user evaluated twice | Identical result (sticky hashing, no RNG) |
| `recommendation-engine` segment | Only `user-alpha`, `user-beta` see it |
| `dark-mode` in production | `false` (no rule matches) |
| `dark-mode` in staging | `true` (EnvironmentOverride matches) |
| Kill switch on | `recommendation-engine` returns default `false` even for segment members |
| Archive `dark-mode` | Evaluation returns default; `activeFlags()` excludes it |
| Unknown flag key | Evaluates `false` (fail closed for unregistered keys) |

Example run:

```
Production evaluations:
  user-alpha | checkout=false dark=false recs=true
  user-beta | checkout=true dark=false recs=true
  user-gamma | checkout=false dark=false recs=false
  user-delta | checkout=false dark=false recs=false

Staging evaluations (dark-mode forced on):
  user-alpha | checkout=false dark=true recs=true
  user-beta | checkout=true dark=true recs=true

Kill switch on recommendation-engine:
  recs for user-alpha (in segment): false

Active flags after archiving dark-mode: [new-checkout, recommendation-engine]
```

## Follow-Up Questions

1. **Percentage rollout by hash — what are the failure modes?** `hashCode()` is not stable
   across JVM versions, so production systems hash on a stable ID (user id, tenant id) with a
   fixed algorithm and a salt, and re-hash only when the rollout target changes. `floorMod`
   avoids negative buckets; adding a salt per flag prevents all users landing in the same
   bucket when hash distributions are skewed.
2. **Why AND semantics instead of first-match?** First-match (priority) is better when rules
   overlap with different intents; AND is safer for combining independent constraints — a
   rollout AND a segment says "10% of these users". Most teams start with AND and add priority
   when rules conflict; document which one your engine uses.
3. **What happens to flags after they are 100% shipped?** Clean them up: remove the flag
   branches from code, then delete the flag. Leaving flags forever means dead branches and a
   flag surface nobody understands; the industry rule is flags are temporary by default, and
   permanent flags are the exception (operational toggles).
4. **How does a kill switch differ from just flipping the default?** Semantics and reach: the
   default only matters when no rules apply; the kill switch bypasses rules entirely. If a
   rollout is at 60% and something breaks, flipping the default does nothing for the 60% —
   the kill switch is the emergency brake that everyone knows to pull.
5. **Who should be able to toggle flags in production?** The platform, with audit and
   approval for high-risk flags; self-service toggling for low-risk flags is fine but must be
   logged. The dangerous pattern is flags exposed to UI with no access control — that is a
   permissions bug, not a feature.
6. **How do you test with flags?** Matrix the flag states in CI (flag off, on, kill-switched)
   against a representative user set; test the targeting rules with golden users whose buckets
   are known; and in staging, exercise the environment override path. Flag-testing mistakes
   usually come from testing only the default state.
7. **How is this different from LaunchDarkly-style SaaS?** The engine is the same; the SaaS
   adds the distribution network (edge flags, offline caching), the UI, audit, and the SDK
   ecosystem. Self-hosted engines (Unleash, this lab) trade those for full control and no per-
   request network dependency — which is why evaluation stays a local library call either way.
