# Lab 05: Problem Walkthrough — IaC Engine with State Management and Plan/Apply

## Problem Statement

Implement a Terraform-like infrastructure-as-code engine with state management and a plan/apply workflow. The engine must:

1. Accept a **config** of resource declarations (type, name, attributes) — `vm`, `bucket`, `network`.
2. Maintain **state**: a serial-numbered snapshot of applied resources.
3. Compute a **plan**: a diff between the desired config and the current state, producing `CREATE`, `UPDATE`, `DELETE`, or `NOOP` actions per resource, with attribute-level changes for updates. Computed attributes (e.g., auto-assigned IDs) must be excluded from diffs.
4. **Apply** the plan against a fake cloud provider (in-memory), honoring dependencies (a VM depends on its network), then persist updated state with a bumped serial.
5. Enforce **optimistic concurrency**: apply rejects a stale state serial (the read-modify-write race), and the state lock prevents concurrent applies.
6. Support **refresh**: re-read actual resources from the provider and merge into state.

**Constraints**

- Plan must be read-only and deterministic: same config + same state → same plan.
- Update semantics: changed attributes → UPDATE; `forceNew` attributes → REPLACE (delete + create).
- All code must compile under Java 21+.

---

## Walkthrough

### Step 1: Model config resources, attributes, and schemas

A resource is (type, name, attributes map). Each resource type has a schema describing which attributes are computed (assigned by the cloud) and which are `forceNew` (changing them forces a replacement).

```java
package com.cloud.deep.lab05;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

public final class IaCEngine {

    public record Resource(String type, String name, Map<String, String> attributes) {
        String address() { return type + "." + name; }
    }

    public record Schema(Set<String> computed, Set<String> forceNew) {
        static final Schema VM = new Schema(Set.of("instance_id"), Set.of("zone"));
        static final Schema BUCKET = new Schema(Set.of("bucket_arn"), Set.of());
        static final Schema NETWORK = new Schema(Set.of("network_id"), Set.of());
    }
```

### Step 2: Model state and the provider interface

State carries a serial and a map of address → resource (the last applied attributes). The provider is the world: it stores live resources and assigns computed values.

```java
    public static final class State {
        private long serial = 0;
        private final Map<String, Resource> resources = new LinkedHashMap<>();

        State copy() {
            State c = new State();
            c.serial = serial;
            c.resources.putAll(resources);
            return c;
        }
        long serial() { return serial; }
        Map<String, Resource> resources() { return Map.copyOf(resources); }
        void put(Resource r) { resources.put(r.address(), r); }
        void remove(String address) { resources.remove(address); }
        void bumpSerial() { serial++; }
    }

    public interface Provider {
        Resource create(Resource r);
        Resource read(String address);
        Resource update(Resource r);
        void delete(String address);
        List<Resource> list(String type);
    }
```

### Step 3: Implement the fake provider

The fake provider simulates the cloud: it stores live resources, generates computed values (instance_id, network_id, bucket_arn) at create, and honors the address index. This is what makes the whole engine testable without real infrastructure.

```java
    public static final class FakeCloud implements Provider {
        private final Map<String, Resource> live = new ConcurrentHashMap<>();
        private final AtomicLong counter = new AtomicLong(1);

        @Override public Resource create(Resource r) {
            Map<String, String> attrs = new LinkedHashMap<>(r.attributes());
            String computedKey = switch (r.type()) {
                case "vm" -> "instance_id";
                case "bucket" -> "bucket_arn";
                default -> "network_id";
            };
            attrs.put(computedKey, "res-" + counter.getAndIncrement());
            Resource created = new Resource(r.type(), r.name(), Map.copyOf(attrs));
            live.put(created.address(), created);
            return created;
        }

        @Override public Resource read(String address) { return live.get(address); }

        @Override public Resource update(Resource r) {
            live.put(r.address(), r);
            return r;
        }

        @Override public void delete(String address) { live.remove(address); }

        @Override public List<Resource> list(String type) {
            return live.values().stream().filter(r -> r.type().equals(type)).toList();
        }

        int liveCount() { return live.size(); }
    }
```

### Step 4: Implement the plan builder

Plan construction:

1. For each resource in config: if absent from state → CREATE; if present and attributes (excluding computed) differ → UPDATE (or REPLACE when a forceNew attribute changed); identical → NOOP.
2. For each resource in state but absent from config → DELETE.
3. Dependency ordering: a `vm` references its network via `network_id` attribute (implicitly); the plan sorts actions so creates/updates of dependencies come first and deletes come last.

The diff compares only non-computed attributes, so auto-assigned values never cause phantom updates.

```java
    public enum Action { CREATE, UPDATE, DELETE, REPLACE, NOOP }

    public record PlanAction(Action action, String address, Resource resource,
                             List<String> changedAttributes, int order) {}

    public record Plan(long serial, List<PlanAction> actions) {
        boolean isEmpty() { return actions.stream().allMatch(a -> a.action() == Action.NOOP); }
    }

    public static final class Planner {
        private final Map<String, Schema> schemas;

        public Planner(Map<String, Schema> schemas) { this.schemas = schemas; }

        public Plan plan(List<Resource> config, State state) {
            List<PlanAction> actions = new ArrayList<>();

            Map<String, Resource> configByAddress = new LinkedHashMap<>();
            for (Resource r : config) configByAddress.put(r.address(), r);

            for (Resource desired : config) {
                Resource current = state.resources().get(desired.address());
                if (current == null) {
                    actions.add(new PlanAction(Action.CREATE, desired.address(), desired, List.of(), 0));
                } else {
                    Schema schema = schemas.get(desired.type());
                    List<String> changed = diffAttributes(current, desired, schema.computed());
                    if (changed.isEmpty()) {
                        actions.add(new PlanAction(Action.NOOP, desired.address(), desired, List.of(), 0));
                    } else if (changed.stream().anyMatch(schema.forceNew()::contains)) {
                        actions.add(new PlanAction(Action.REPLACE, desired.address(), desired, changed, 0));
                    } else {
                        actions.add(new PlanAction(Action.UPDATE, desired.address(), desired, changed, 0));
                    }
                }
            }

            for (Map.Entry<String, Resource> e : state.resources().entrySet()) {
                if (!configByAddress.containsKey(e.getKey())) {
                    actions.add(new PlanAction(Action.DELETE, e.getKey(), e.getValue(), List.of(), 0));
                }
            }

            List<PlanAction> ordered = orderByDependencies(actions);
            return new Plan(state.serial(), ordered);
        }

        private List<String> diffAttributes(Resource current, Resource desired, Set<String> computed) {
            List<String> changed = new ArrayList<>();
            for (Map.Entry<String, String> e : desired.attributes().entrySet()) {
                if (computed.contains(e.getKey())) continue;
                if (!Objects.equals(e.getValue(), current.attributes().get(e.getKey()))) {
                    changed.add(e.getKey());
                }
            }
            return changed;
        }

        private List<PlanAction> orderByDependencies(List<PlanAction> actions) {
            Map<String, Integer> depth = new HashMap<>();
            for (PlanAction a : actions) depth.put(a.address(), a.action() == Action.DELETE ? 2 : 0);

            boolean changed = true;
            while (changed) {
                changed = false;
                for (PlanAction a : actions) {
                    for (String dep : dependencies(a.resource())) {
                        if (depth.containsKey(dep) && depth.get(dep) >= depth.get(a.address())) {
                            depth.put(a.address(), depth.get(dep) + 1);
                            changed = true;
                        }
                    }
                }
            }

            List<PlanAction> sorted = new ArrayList<>(actions);
            sorted.sort((x, y) -> {
                int dx = depth.get(x.address());
                int dy = depth.get(y.address());
                if (dx != dy) return Integer.compare(dx, dy);
                return x.address().compareTo(y.address());
            });
            return sorted;
        }

        private List<String> dependencies(Resource r) {
            if (r.type().equals("vm")) {
                String net = r.attributes().get("network");
                if (net != null) return List.of("network." + net);
            }
            return List.of();
        }
    }
```

Note on ordering semantics: deletes get the highest depth (2), so destroys run after creates/updates — children (`vm`) are deleted before parents (`network`) because the parent's depth stays 2 while the child's depth becomes 3 via the dependency edge. This mirrors "destroy last, reverse order."

### Step 5: Implement apply with locking and optimistic concurrency

Apply validates the state serial (optimistic concurrency), takes a lock, executes actions against the provider, and writes back the new state with a bumped serial. A `replace` executes delete-then-create atomically (in this simulation).

```java
    public static final class Applier {
        private final Provider provider;
        private final Map<String, Schema> schemas;
        private final Object lock = new Object();
        private boolean locked = false;
        private final AtomicLong applies = new AtomicLong();

        public Applier(Provider provider, Map<String, Schema> schemas) {
            this.provider = provider;
            this.schemas = schemas;
        }

        public State apply(Plan plan, State state) {
            synchronized (lock) {
                if (locked) throw new IllegalStateException("State already locked");
                locked = true;
            }
            try {
                if (plan.serial() != state.serial()) {
                    throw new IllegalStateException("Stale plan: plan built on serial "
                            + plan.serial() + ", state is " + state.serial());
                }
                State next = state.copy();
                for (PlanAction a : plan.actions()) {
                    switch (a.action()) {
                        case CREATE -> next.put(provider.create(a.resource()));
                        case UPDATE -> next.put(provider.update(withComputed(a.resource(), state)));
                        case DELETE -> {
                            provider.delete(a.address());
                            next.remove(a.address());
                        }
                        case REPLACE -> {
                            provider.delete(a.address());
                            next.remove(a.address());
                            next.put(provider.create(a.resource()));
                        }
                        case NOOP -> { /* nothing to do */ }
                    }
                }
                next.bumpSerial();
                applies.incrementAndGet();
                return next;
            } finally {
                synchronized (lock) { locked = false; }
            }
        }

        private Resource withComputed(Resource desired, State state) {
            Resource current = state.resources().get(desired.address());
            if (current == null) return desired;
            Map<String, String> merged = new LinkedHashMap<>(desired.attributes());
            for (String key : schemas.get(desired.type()).computed()) {
                String v = current.attributes().get(key);
                if (v != null) merged.put(key, v);
            }
            return new Resource(desired.type(), desired.name(), Map.copyOf(merged));
        }

        long applies() { return applies.get(); }
    }
```

### Step 6: Implement refresh

Refresh re-reads live resources from the provider and rebuilds state — detecting drift (resources deleted out-of-band or modified).

```java
    public static final class Refresher {
        private final Provider provider;

        public Refresher(Provider provider) { this.provider = provider; }

        public State refresh(State state) {
            State next = state.copy();
            for (String address : new ArrayList<>(next.resources().keySet())) {
                Resource live = provider.read(address);
                if (live == null) {
                    next.remove(address); // drifted away — deleted out-of-band
                } else {
                    next.put(live);       // drift corrected — take the live truth
                }
            }
            return next;
        }
    }
```

### Step 7: Demo — the full lifecycle plus the concurrency race

The demo runs: apply config v1 → plan shows NOOP (idempotence) → drift a bucket out-of-band → refresh detects it → apply config v2 (new VM + changed attribute + removed bucket) → plan shows the exact action set → a second concurrent apply against the stale serial is rejected.

```java
    public static void main(String[] args) {
        Map<String, Schema> schemas = new HashMap<>();
        schemas.put("vm", Schema.VM);
        schemas.put("bucket", Schema.BUCKET);
        schemas.put("network", Schema.NETWORK);

        FakeCloud cloud = new FakeCloud();
        Planner planner = new Planner(schemas);
        Applier applier = new Applier(cloud, schemas);
        Refresher refresher = new Refresher(cloud);
        State state = new State();

        List<Resource> configV1 = List.of(
                new Resource("network", "main", Map.of("cidr", "10.0.0.0/16")),
                new Resource("vm", "web", Map.of("network", "main", "size", "t3.small")),
                new Resource("bucket", "logs", Map.of("region", "us-east-1")));

        System.out.println("=== IaC Engine Demo ===\n");

        System.out.println("-- Apply config v1 --");
        state = applyCycle(planner, applier, configV1, state);

        System.out.println("-- Re-plan: expect all NOOP (idempotent) --");
        state = applyCycle(planner, applier, configV1, state);

        System.out.println("-- Simulate drift: bucket 'logs' deleted out-of-band --");
        cloud.delete("bucket.logs");

        state = refresher.refresh(state);
        System.out.println("State after refresh: " + state.resources().keySet() + "\n");

        List<Resource> configV2 = List.of(
                new Resource("network", "main", Map.of("cidr", "10.0.0.0/16")),
                new Resource("vm", "web", Map.of("network", "main", "size", "t3.medium")),
                new Resource("vm", "worker", Map.of("network", "main", "size", "t3.small")));

        System.out.println("-- Plan config v2: expect UPDATE web, CREATE worker, CREATE logs(?) --");
        Plan plan2 = planner.plan(configV2, state);
        plan2.actions().forEach(a -> System.out.printf("  %-8s %-24s %s%n", a.action(),
                a.address(), a.changedAttributes()));

        System.out.println("\n-- Apply config v2 --");
        state = applier.apply(plan2, state);
        System.out.println("Applied. State serial=" + state.serial()
                + " resources=" + state.resources().keySet()
                + " live objects=" + cloud.liveCount());

        System.out.println("\n-- Optimistic concurrency: re-apply stale plan --");
        try {
            applier.apply(plan2, state);
            System.out.println("  (unexpected success)");
        } catch (IllegalStateException e) {
            System.out.println("  Rejected: " + e.getMessage());
        }
    }

    private static State applyCycle(Planner planner, Applier applier,
                                    List<Resource> config, State state) {
        Plan plan = planner.plan(config, state);
        plan.actions().forEach(a -> System.out.printf("  %-8s %-24s %s%n", a.action(),
                a.address(), a.changedAttributes()));
        State next = applier.apply(plan, state);
        System.out.println("  -> serial=" + next.serial() + " live=" + next.resources().keySet() + "\n");
        return next;
    }
}
```

### Step 8: Expected output walkthrough

| Phase | Actions | Explanation |
|-------|---------|-------------|
| Apply v1 | CREATE network.main, CREATE vm.web, CREATE bucket.logs | Fresh state; network sorts before vm (dependency) |
| Re-plan v1 | 3 × NOOP | Idempotence — computed attrs excluded from diff |
| Refresh | bucket.logs removed from state | Out-of-band delete detected |
| Plan v2 | UPDATE vm.web (size), CREATE vm.worker, CREATE bucket.logs | ForceNew `zone` untouched → update not replace |
| Apply v2 | serial 1→2 | Optimistic concurrency check passes on first apply |
| Stale re-apply | `IllegalStateException` | Plan serial (1) ≠ current state serial (2) — race rejected |

---

## Complexity Analysis

- **Plan**: O(C + S + A log A) where C = config resources, S = state resources, A = actions — attribute diffs are O(K) per resource (K = attributes, small).
- **Dependency ordering**: O(A²) worst case with the iterative depth relaxation (Bellman-Ford style); a real engine uses an explicit DAG with topological sort — O(V + E).
- **Apply**: O(A) provider calls — each provider call is O(1) in the fake cloud.
- **Refresh**: O(S) reads.
- **Space**: O(S + L) — state plus live objects in the fake provider.
- **Concurrency**: serialized via the applier lock; optimistic concurrency adds O(1) per apply.

---

## Follow-Up Questions

1. **How do you make apply resumable after a crash?** Journal each action before executing (intent log); on restart, reconcile: for each journaled intent, read the live resource and fix state accordingly — the journal plus refresh gives exactly-once-ish semantics.

2. **How do you support remote state with locking in production?** A `StateBackend` interface with implementations for S3+DynamoDB (lock table with lease TTL), Azure Storage+Blob lease, or Consul. The engine calls `acquireLock(owner, ttl)` / `releaseLock` and uses compare-and-swap on the serial.

3. **How would you add a destroy-only mode (`terraform destroy`)?** A flag that treats every state resource as deleted from config: plan → all DELETE actions; the demo's delete ordering (children first) already handles it.

4. **How do you model `depends_on` for dependencies the engine cannot infer?** Add an explicit `dependsOn` attribute to the config resource model; `dependencies()` merges implicit reference edges with explicit edges.

5. **How do you detect a dependency cycle?** During topological sort, if the sorted size < action count, a cycle exists — report the strongly connected component (Tarjan's) for a clear error.

6. **How do you handle `plan` showing changes for attributes the user never set?** That's provider-driven defaults appearing in state — the engine should normalize: attributes with no config value and no state value are excluded from diffs, and a `plan -refresh-only` mode distinguishes drift from intent.
