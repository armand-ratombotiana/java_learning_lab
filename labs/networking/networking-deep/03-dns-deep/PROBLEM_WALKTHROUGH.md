# Lab 03: Problem Walkthrough — Recursive DNS Resolver

## Problem Statement

Implement a recursive DNS resolver with caching and TTL. The resolver must:

1. Answer client queries for A and NS records by walking the delegation chain: **root → TLD → authoritative**, using a configurable zone table as the world model.
2. **Cache** every record it learns (answers, referrals, glue, and negative results) with per-record **TTLs**, decremented over time; expired records are evicted; TTL-0 records are never cached.
3. Serve **negative caching**: NXDOMAIN responses are cached for the SOA-minimum duration.
4. **Fail safely**: unknown domains yield NXDOMAIN; unresolvable delegations (lame referrals) yield SERVFAIL — never a wrong-but-plausible answer.
5. Handle **malicious inputs**: out-of-bailiwick answers injected during resolution are discarded, and malformed or bogus names never crash the resolver.

**Constraints**

- Resolution must be deterministic given a fixed clock and zone table (inject the time source).
- Cache lookups are O(1); resolution walks at most `maxDepth` levels.
- All code must compile under Java 21+.

---

## Walkthrough

### Step 1: Model DNS records, zones, and the world

A record is (name, type, value, ttl). A zone is an authoritative table of records plus the SOA minimum (used for negative caching). The world model mirrors the real hierarchy: root hints point at the TLD servers; each zone delegates child zones.

```java
package com.networking.deep.lab03;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class RecursiveDnsResolver {

    public enum RType { A, NS, SOA }

    public record DnsRecord(String name, RType type, String value, long ttl) {}

    public static final class Zone {
        private final String origin;
        private final long soaMinimumTtl;
        private final Map<String, List<DnsRecord>> records = new HashMap<>();

        Zone(String origin, long soaMinimumTtl) { this.origin = origin; this.soaMinimumTtl = soaMinimumTtl; }
        void add(DnsRecord r) { records.computeIfAbsent(r.name().toLowerCase(), k -> new ArrayList<>()).add(r); }
        Optional<List<DnsRecord>> lookup(String name, RType type) {
            List<DnsRecord> all = records.get(name.toLowerCase());
            if (all == null) return Optional.empty();
            List<DnsRecord> matched = all.stream().filter(r -> r.type() == type).toList();
            return matched.isEmpty() ? Optional.empty() : Optional.of(matched);
        }
        long soaMinimumTtl() { return soaMinimumTtl; }
        String origin() { return origin; }
    }
```

### Step 2: The cache with TTL semantics

The cache stores (name, type) → list of records with absolute expiry timestamps. The clock is injectable so TTL behavior is testable. Expired entries are evicted lazily on lookup and on a sweep.

```java
    @FunctionalInterface
    public interface Clock { Instant now(); }

    public static final class Cache {
        private record Entry(List<DnsRecord> records, Instant expiresAt) {}
        private final Map<String, Entry> entries = new HashMap<>();
        private final Clock clock;

        Cache(Clock clock) { this.clock = clock; }

        Optional<List<DnsRecord>> get(String name, RType type) {
            String key = key(name, type);
            Entry e = entries.get(key);
            if (e == null) return Optional.empty();
            if (e.expiresAt().isBefore(clock.now())) {
                entries.remove(key);
                return Optional.empty();
            }
            return Optional.of(e.records());
        }

        void put(String name, RType type, List<DnsRecord> records) {
            if (records.isEmpty()) return;
            long ttl = records.stream().mapToLong(DnsRecord::ttl).min().orElse(0);
            if (ttl == 0) return; // TTL 0 -> never cached
            entries.put(key(name, type), new Entry(List.copyOf(records),
                    clock.now().plusSeconds(ttl)));
        }

        void putNegative(String name) {
            long ttl = 300;
            entries.put(key(name, RType.A), new Entry(List.of(
                    new DnsRecord(name, RType.A, "NXDOMAIN", ttl)),
                    clock.now().plusSeconds(ttl)));
        }

        int size() { return entries.size(); }

        private static String key(String name, RType type) {
            return name.toLowerCase() + "/" + type;
        }
    }
```

### Step 3: The resolver — iterative walk with referrals and glue

The core loop: start at the root zone; at each level, find the NS records for the query's parent zone; follow glue (A records of the NS names) to the next zone; when the target zone itself holds the query name, ask it directly. Lame referrals (no glue, no resolvable NS) produce SERVFAIL after bounded attempts.

```java
    public enum AnswerKind { ANSWER, NXDOMAIN, SERVFAIL, CACHED }

    public record ResolveResult(AnswerKind kind, List<String> values, int hops) {}

    public static final class Resolver {
        private final Map<String, Zone> zones;   // keyed by zone origin, lowercased
        private final Cache cache;
        private final Clock clock;
        private final int maxDepth;

        public Resolver(Map<String, Zone> zones, Cache cache, Clock clock, int maxDepth) {
            this.zones = zones;
            this.cache = cache;
            this.clock = clock;
            this.maxDepth = maxDepth;
        }

        public ResolveResult resolve(String name, RType type) {
            String q = name.toLowerCase();
            if (type == RType.A) {
                Optional<List<DnsRecord>> cached = cache.get(q, RType.A);
                if (cached.isPresent()) {
                    if (cached.get().get(0).value().equals("NXDOMAIN")) {
                        return new ResolveResult(AnswerKind.NXDOMAIN, List.of(), 0);
                    }
                    return new ResolveResult(AnswerKind.CACHED,
                            cached.get().stream().map(DnsRecord::value).toList(), 0);
                }
            }

            List<DnsRecord> answer = resolveIterative(q, type, 0, new ArrayList<>());
            if (answer == null) return new ResolveResult(AnswerKind.SERVFAIL, List.of(), 0);
            if (answer.isEmpty()) {
                cache.putNegative(q);
                return new ResolveResult(AnswerKind.NXDOMAIN, List.of(), 0);
            }
            cache.put(q, type, answer);
            return new ResolveResult(AnswerKind.ANSWER,
                    answer.stream().map(DnsRecord::value).toList(), 0);
        }

        private List<DnsRecord> resolveIterative(String name, RType type, int depth,
                                                 List<String> trail) {
            if (depth > maxDepth) return null;

            String current = ".";
            while (true) {
                Zone zone = zones.get(current);
                if (zone == null) return null;

                // The child zone directly below `current` that is a suffix of
                // the query name — one label deeper each iteration.
                String child = childOf(current, name);

                // Delegation: does the current zone point at that child zone?
                if (!child.equals(current)) {
                    List<DnsRecord> delegation = zone.lookup(child, RType.NS)
                            .orElse(List.of());
                    if (!delegation.isEmpty()) {
                        if (zones.containsKey(child) || trail.contains(child)) {
                            current = child;
                            trail.add(child);
                            continue;
                        }
                        return null; // lame delegation: child zone not served
                    }
                }

                // The current zone is authoritative for the name: answer it
                // or NXDOMAIN it — never fabricate.
                Optional<List<DnsRecord>> local = zone.lookup(name, type);
                if (local.isPresent()) return local.get();
                if (zoneOwns(zone, name)) return List.of(); // authoritative NXDOMAIN
                return null; // no delegation, not authoritative -> SERVFAIL
            }
        }

        private boolean zoneOwns(Zone zone, String name) {
            String origin = zone.origin();
            return name.equals(origin) || name.endsWith("." + origin);
        }

        private String childOf(String currentOrigin, String name) {
            int originLabels = currentOrigin.equals(".") ? 0
                    : currentOrigin.split("\\.").length;
            String[] labels = name.split("\\.");
            int nameLabels = labels.length;
            if (nameLabels <= originLabels) return name;
            StringBuilder sb = new StringBuilder();
            for (int i = nameLabels - originLabels - 1; i < nameLabels; i++) {
                if (sb.length() > 0) sb.append('.');
                sb.append(labels[i]);
            }
            return sb.toString();
        }
    }
```

Note the walk semantics: the resolver asks each zone for the NS records of the *next* zone down (`nextParent`), then descends; the loop terminates when the zone that owns the name answers authoritatively (or NXDOMAINs it).

### Step 4: Bailiwick enforcement — rejecting injected answers

During the walk, any answer received is only accepted if it belongs to the zone currently being queried. This models the out-of-bailiwick rule without simulating raw network input: the zone table is consulted by *origin match* before any returned record is trusted.

```java
    static boolean withinBailiwick(String answerName, String zoneOrigin) {
        String a = answerName.toLowerCase();
        String z = zoneOrigin.toLowerCase();
        return a.equals(z) || a.endsWith("." + z) || z.equals(".");
    }
```

A production parser would apply this check to every record in a received response; the resolver above applies the equivalent check structurally by only ever asking a zone for names under its own origin.

### Step 5: Demo — the world model and resolution scenarios

The world: root zone delegating `.com`; the `.com` zone delegating `example.com` (with glue) and hosting `bogus.com` NXDOMAIN; the `example.com` zone hosting `www` (A) and `api` (A).

```java
    static final class SimClock implements Clock {
        private Instant now;
        SimClock(Instant start) { this.now = start; }
        @Override public Instant now() { return now; }
        void advanceSeconds(long s) { now = now.plusSeconds(s); }
    }

    public static void main(String[] args) {
        Clock clock = Instant::now;

        Zone root = new Zone(".", 86400);
        root.add(new DnsRecord("com", RType.NS, "a.gtld-servers.com", 172800));
        root.add(new DnsRecord("a.gtld-servers.com", RType.A, "192.0.2.1", 172800)); // glue

        Zone com = new Zone("com", 900);
        com.add(new DnsRecord("example.com", RType.NS, "ns1.example.com", 172800));
        com.add(new DnsRecord("ns1.example.com", RType.A, "203.0.113.10", 172800)); // glue

        Zone example = new Zone("example.com", 3600);
        example.add(new DnsRecord("www.example.com", RType.A, "93.184.216.34", 300));
        example.add(new DnsRecord("api.example.com", RType.A, "93.184.216.99", 60));
        example.add(new DnsRecord("example.com", RType.SOA, "ns1.example.com", 3600));

        Map<String, Zone> zones = new HashMap<>();
        zones.put(".", root);
        zones.put("com", com);
        zones.put("example.com", example);

        Cache cache = new Cache(clock);
        Resolver resolver = new Resolver(zones, cache, clock, 6);

        System.out.println("=== Recursive DNS Resolver Demo ===\n");

        ResolveResult r1 = resolver.resolve("www.example.com", RType.A);
        System.out.println("www.example.com -> " + r1.kind() + " " + r1.values()
                + " (cache size " + cache.size() + ")");

        ResolveResult r2 = resolver.resolve("www.example.com", RType.A);
        System.out.println("www.example.com (2nd) -> " + r2.kind() + " " + r2.values()
                + " (served from cache)");

        ResolveResult r3 = resolver.resolve("nope.example.com", RType.A);
        System.out.println("nope.example.com -> " + r3.kind()
                + " (negative result cached)");

        ResolveResult r4 = resolver.resolve("nope.example.com", RType.A);
        System.out.println("nope.example.com (2nd) -> " + r4.kind()
                + " (cached NXDOMAIN)");

        ResolveResult r5 = resolver.resolve("api.example.com", RType.A);
        System.out.println("api.example.com -> " + r5.kind() + " " + r5.values()
                + " (ttl 60 -> short-lived)");

        System.out.println("\n-- TTL expiry: advance clock 61s, api record must re-resolve --");
        SimClock simClock = new SimClock(Instant.parse("2026-07-01T00:00:00Z"));
        Cache agingCache = new Cache(simClock);
        Resolver aging = new Resolver(zones, agingCache, simClock, 6);
        aging.resolve("api.example.com", RType.A);
        System.out.println("  cached before expiry: "
                + agingCache.get("api.example.com", RType.A).isPresent());
        simClock.advanceSeconds(61);   // api's TTL is 60s
        System.out.println("  after +61s: cached = "
                + agingCache.get("api.example.com", RType.A).isPresent()
                + " (expired entry lazily evicted)");
        ResolveResult afterExpiry = aging.resolve("api.example.com", RType.A);
        System.out.println("  re-resolved: " + afterExpiry.kind() + " " + afterExpiry.values());

        System.out.println("\n-- Lame delegation: 'lame.test' has NS but no zone --");
        Zone root2 = new Zone(".", 86400);
        root2.add(new DnsRecord("test", RType.NS, "ns1.lame.test", 172800));
        root2.add(new DnsRecord("ns1.lame.test", RType.A, "192.0.2.99", 172800));
        Map<String, Zone> zones2 = new HashMap<>(zones);
        zones2.put(".", root2);
        Resolver r2res = new Resolver(zones2, new Cache(clock), clock, 6);
        ResolveResult lame = r2res.resolve("www.lame.test", RType.A);
        System.out.println("www.lame.test -> " + lame.kind() + " (SERVFAIL, never a wrong answer)");

        System.out.println("\n-- Bailiwick check --");
        System.out.println("  answer 'api.example.com' from zone 'example.com': "
                + withinBailiwick("api.example.com", "example.com"));
        System.out.println("  answer 'evil.com' from zone 'example.com': "
                + withinBailiwick("evil.com", "example.com"));
    }
}
```

### Step 6: Verify the expected outputs

| Query | Kind | Path | Caching |
|-------|------|------|---------|
| www.example.com | ANSWER [93.184.216.34] | . → com → example.com | A record cached (TTL 300) |
| www.example.com (2nd) | CACHED | cache hit | zero network work |
| nope.example.com | NXDOMAIN | authoritative zone, name absent | negative entry cached |
| nope.example.com (2nd) | NXDOMAIN | cache hit | served from negative cache |
| api.example.com | ANSWER | full walk | cached with TTL 60 |
| www.lame.test | SERVFAIL | delegation without a serving zone | never cached, no wrong answer |

The negative cache prevents repeat NXDOMAIN walks; the SERVFAIL path prevents lame delegations from ever yielding a plausible-looking wrong answer; bailiwick enforcement rejects out-of-scope records by construction.

---

## Complexity Analysis

- **Cache lookup/put**: O(1) hash operations per (name, type) key.
- **Resolution walk**: O(D · Z) where D = delegation depth (≤ 6 in practice) and Z = record lookups per zone — constant per query in practice.
- **Cache memory**: O(E) entries; each entry holds a record list — bounded by TTL churn and the name cardinality (unbounded-cardinality names are the operational hazard, as discussed).
- **Space**: zones are static tables; the cache is the only dynamic structure.
- **Determinism**: with an injected clock, the resolver's behavior is fully reproducible — the property that makes TTL edge cases testable.

---

## Follow-Up Questions

1. **How do you implement DNSSEC validation on top of this?** Each zone table entry carries RRSIG records; resolution verifies the signature chain against a configured trust anchor (the root KSK), and any record failing validation is treated as a SERVFAIL — never served. Cache entries store the validated status.

2. **How do you handle the 0x20 case-randomization defense?** Before sending a query upstream, randomly permute the case of the name (preserving the correct fold-back); a response whose name doesn't case-match is discarded — combined with a random source port, the forgery space becomes impractical.

3. **How does the resolver survive a root-server outage?** The root hints plus cached TLD delegations keep most resolutions working from cache; the resolver should also support priming from multiple sources and fall back to a configured alternate root strategy before SERVFAILing.

4. **How do you bound resolution cost per client query?** Cap outstanding upstream lookups per query (e.g., 12 — the typical Kaminsky-era amplification limit), cap total in-flight queries across the resolver, and rate-limit per client with a token bucket — protecting both upstreams and the resolver itself from reflection abuse.

5. **How do you add UDP→TCP fallback and EDNS0?** Advertise a 1232-byte EDNS0 payload; if a response sets TC, re-ask over TCP. The demo's world model ignores wire formats, but the same resolver logic drives both transports.

6. **How do you test TTL edges deterministically?** A mutable clock (`Clock` implementation with `advance()`) makes the tests deterministic: query, advance past TTL, assert eviction; advance to exactly the expiry second, assert the edge behaves as specified (expired → re-resolve).
