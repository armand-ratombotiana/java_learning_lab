# Lab 08: Problem Walkthrough — Flow Analytics Pipeline with Sampling

## Problem Statement

Design and implement the heart of a network observability pipeline: flow log collection with **sampling**, aggregation with a **bounded cardinality budget**, and **honest error reporting** on every estimate.

The pipeline must:

1. **Model flows** — five-tuple, timestamps, bytes, packets — as the summary signal between raw packets and pre-aggregated metrics.
2. **Sample with hash-based flow selection**: a *stateless, deterministic* decision per five-tuple (same tuple ⇒ same decision, across replicas and reboots), keeping whole flows so flow counts, sizes, and durations stay unbiased. The sampling rate is configurable; every aggregate is corrected by `1/p`.
3. **Prove the bias of packet sampling**: show that "sample 1 in 100 packets" systematically over-represents long flows and inflates flow-count estimates — the reason flow sampling exists.
4. **Aggregate** with a cardinality budget: the number of distinct keys is a first-class constraint; excess keys are evicted into an `other` bucket rather than unbounded memory growth.
5. **Report error bars**: every estimate carries a 95% confidence interval from the binomial sampling model — because a sample of 1,000 flows has ±62% error at 1% sampling, and pretending otherwise is how observability lies to engineers.

**Constraints**

- Sampling decisions must be reproducible without randomness (hash-based) — the Random-based comparator exists only to *demonstrate* the bias, with a fixed seed.
- All aggregation state must be bounded: keys capped, buckets constant-size.
- All code compiles under Java 21+.

---

## Walkthrough

### Step 1: The flow model and the sampling contract

```java
package com.networking.deep.lab08;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

public final class FlowAnalyzer {

    public record Flow(String srcIp, String dstIp, int srcPort, int dstPort,
                       String protocol, long startMillis, long durationMillis,
                       long bytes, long packets) {}

    /** A sampler decides which flows survive, and reports its probability. */
    public interface Sampler {
        boolean keep(Flow flow);
        double probability();
    }
```

The sampling contract is deliberately two methods: the *decision* and the *rate*. Every downstream estimate needs both — the rate is the correction factor.

### Step 2: Hash-based flow sampling — stateless and deterministic

```java
    /** Stateless flow sampling: hash the five-tuple, keep if below a threshold.
     *  Same tuple -> same decision, on every replica and across restarts. */
    public static final class HashSampler implements Sampler {
        private final double probability;
        private final long threshold;

        public HashSampler(double probability) {
            this.probability = probability;
            this.threshold = (long) (probability * 0x8000_0000L); // 2^31 hash space
        }

        @Override
        public boolean keep(Flow flow) {
            return hash(fiveTuple(flow)) < threshold;
        }

        @Override
        public double probability() { return probability; }

        static String fiveTuple(Flow f) {
            return f.srcIp() + "|" + f.dstIp() + "|" + f.srcPort() + "|"
                    + f.dstPort() + "|" + f.protocol();
        }

        static int hash(String s) {
            // Two-state murmur-style mix with avalanche finalizer. A plain
            // multiplicative hash (FNV-1a) is measurably non-uniform on
            // low-entropy tuples like "10.1.0.7|172.16.0.9|49158|443|tcp" —
            // it clustered verdicts by port and kept 800 flows instead of ~1000.
            int h1 = 0x8a4f3b2c, h2 = 0x16b1cde9;
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                h1 = Integer.rotateLeft(h1 ^ (c * 0xcc9e2d51), 15) * 0x1b873593;
                h2 = Integer.rotateLeft(h2 ^ (c * 0x85ebca6b), 13) * 0x5bc61359;
            }
            int h = (h1 ^ h2) ^ s.length();
            h ^= h >>> 16; h *= 0x85ebca6b; h ^= h >>> 13; h *= 0xc2b2ae35; h ^= h >>> 16;
            return h & 0x7fffffff;
        }
    }
```

Why hashing, not a coin flip? Three properties the pipeline needs: **statelessness** (no shared counter, no per-flow state — a flow can be sampled on any replica), **determinism** (replaying the same capture produces the same verdict; debugging is reproducible), and **decision stability** (the same five-tuple is sampled or not *everywhere*, so aggregated views don't double-count a flow across two agents). The `1/p` correction applies because every kept flow is complete.

### Step 3: The biased comparator — packet-style sampling

This sampler exists to make the bias measurable. It models what happens when sampling decisions are made *per packet*: a flow survives if any of its packets was picked — probability `1 - (1-p)^packets`. Long flows are kept almost surely; short flows are mostly dropped.

```java
    /** Simulates per-packet sampling: a flow survives if any packet was sampled.
     *  Seeded for a deterministic demo of the resulting bias. */
    public static final class RandomSampler implements Sampler {
        private final double probability;
        private final Random random;

        public RandomSampler(double probability, long seed) {
            this.probability = probability;
            this.random = new Random(seed);
        }

        @Override
        public boolean keep(Flow flow) {
            double survive = 1.0 - Math.pow(1.0 - probability, flow.packets());
            return random.nextDouble() < survive;
        }

        @Override
        public double probability() { return probability; }
    }
```

The bias is structural: with p = 0.01, a 3-packet flow survives with probability ≈ 3.0% while a 1,000-packet flow survives with ≈ 99.995% — so kept flows are overwhelmingly the long ones, and the corrected count `kept / p` overstates the true flow count.

### Step 4: Aggregation with a cardinality budget

The aggregator keys flows via a supplied function and holds at most `maxKeys` buckets. When full, the *smallest* bucket (by bytes) is evicted into an `other` bucket — memory is bounded by construction, and the tail is still reported, just compressed. Cardinality is a budget you choose, not a consequence of the data.

```java
    static final class Bucket {
        long flows;
        long bytes;
        long packets;

        void add(long f, long b, long p) {
            flows += f;
            bytes += b;
            packets += p;
        }
    }

    public static final class Aggregator {
        private final Function<Flow, String> keyFn;
        private final int maxKeys;
        private final Map<String, Bucket> buckets = new HashMap<>();
        private final Bucket other = new Bucket();

        public Aggregator(Function<Flow, String> keyFn, int maxKeys) {
            this.keyFn = keyFn;
            this.maxKeys = maxKeys;
        }

        public void add(Flow flow, long weight) {
            String key = keyFn.apply(flow);
            Bucket b = buckets.get(key);
            if (b == null) {
                if (buckets.size() >= maxKeys) evictSmallest();
                b = new Bucket();
                buckets.put(key, b);
            }
            b.add(1, flow.bytes() * weight, flow.packets() * weight);
        }

        private void evictSmallest() {
            Map.Entry<String, Bucket> smallest = null;
            for (Map.Entry<String, Bucket> e : buckets.entrySet()) {
                if (smallest == null || e.getValue().bytes < smallest.getValue().bytes) {
                    smallest = e;
                }
            }
            if (smallest != null) {
                other.add(smallest.getValue().flows,
                        smallest.getValue().bytes, smallest.getValue().packets);
                buckets.remove(smallest.getKey());
            }
        }

        public List<Map.Entry<String, Bucket>> top(int n) {
            return buckets.entrySet().stream()
                    .sorted(Comparator.comparingLong(
                            (Map.Entry<String, Bucket> e) -> e.getValue().bytes).reversed())
                    .limit(n)
                    .toList();
        }

        public Bucket other() { return other; }
    }
```

Note the eviction policy: *smallest-by-bytes first*. The heavy hitters always survive; the long tail degrades gracefully into `other`. This is the observable difference from a naive `HashMap` — which would grow unbounded and take the whole tier down at the first high-cardinality dimension.

### Step 5: The analyzer — estimates and error bars

```java
    record Report(long totalFlows, long keptFlows, long estimatedFlows,
                  double ciHalfWidthFraction, long estimatedBytes,
                  List<Map.Entry<String, Bucket>> topTalkers, Bucket other) {}

    public static final class Analyzer {
        private final Sampler sampler;

        public Analyzer(Sampler sampler) { this.sampler = sampler; }

        public long keptCount(List<Flow> flows) {
            long kept = 0;
            for (Flow f : flows) {
                if (sampler.keep(f)) kept++;
            }
            return kept;
        }

        public Report run(List<Flow> flows, Function<Flow, String> keyFn, int maxKeys) {
            long kept = 0;
            long keptBytes = 0;
            Aggregator agg = new Aggregator(keyFn, maxKeys);
            long weight = (long) (1.0 / sampler.probability());

            for (Flow f : flows) {
                if (sampler.keep(f)) {
                    kept++;
                    keptBytes += f.bytes();
                    agg.add(f, weight);
                }
            }

            double p = sampler.probability();
            long n = flows.size();
            // Binomial: kept ~ Bin(n, p); estimate n_hat = kept / p;
            // std(kept/p) = sqrt(n(1-p)/p) -> CI half-width as a fraction of n.
            double ciHalfWidth = 1.96 * Math.sqrt((1.0 - p) / (n * p));

            return new Report(n, kept, (long) (kept / p), ciHalfWidth,
                    keptBytes * weight, agg.top(8), agg.other());
        }
    }
```

The confidence-interval math is the part most pipelines skip:

- `kept ~ Binomial(n, p)`, so the estimator `n̂ = kept / p` has standard deviation `sqrt(n·p·(1-p)) / p = sqrt(n(1-p)/p)`.
- As a fraction of the true count: **±1.96·sqrt((1−p)/(n·p))** at 95% confidence.
- The error scales with **√n**, not with p: at p = 1%, n = 1,000 flows gives **±62%**; n = 100,000 gives **±6.2%**; n = 10,000,000 gives **±0.6%**. A "1% sampling" claim is meaningless without the denominator — which is why adaptive rates targeting a *kept sample count* (not a nice rate) are the professional answer.

### Step 6: Demo — synthetic traffic, honest vs biased estimates

```java
    public static void main(String[] args) {
        List<Flow> flows = generateTraffic(100_000);

        System.out.println("=== Flow Analytics Pipeline (100,000 flows, p=0.01) ===");
        System.out.println("800 heavy flows (10 MB, 1,000 pkts) + 99,200 small flows (1 KB, 3 pkts)\n");

        Analyzer honest = new Analyzer(new HashSampler(0.01));
        Report report = honest.run(flows, Flow::srcIp, 8);

        System.out.printf("Hash (flow) sampling  : kept=%d est=%d flows (±%.1f%% @95%% CI)%n",
                report.keptFlows(), report.estimatedFlows(),
                report.ciHalfWidthFraction() * 100);
        System.out.printf("                        est volume: %.1f GB (corrected by 1/p)%n",
                report.estimatedBytes() / 1e9);

        System.out.println("\nTop talkers by source IP (corrected, budget 8):");
        for (Map.Entry<String, Bucket> e : report.topTalkers()) {
            System.out.printf("  %-12s %6d flows  %7.1f MB%n",
                    e.getKey(), e.getValue().flows, e.getValue().bytes / 1e6);
        }
        System.out.printf("  other (evicted tail): %d flows, %.1f MB%n",
                report.other().flows, report.other().bytes / 1e6);

        long biasedKept = new Analyzer(new RandomSampler(0.01, 42L)).keptCount(flows);
        System.out.printf("%nPacket-style sampling : kept=%d -> est=%d flows (true: 100,000)%n",
                biasedKept, (long) (biasedKept / 0.01));
        System.out.println("  long flows survive ~100%%, short flows ~3%% -> count inflated");
    }

    /** Deterministic synthetic traffic: 800 heavy flows, 99,200 small flows. */
    static List<Flow> generateTraffic(int total) {
        List<Flow> flows = new ArrayList<>(total);
        for (int i = 0; i < total; i++) {
            boolean heavy = i % 125 == 0;
            flows.add(new Flow(
                    "10.1.0." + (i % 1000),            // 1,000 distinct sources
                    "172.16.0.9",
                    49152 + (i % 1024),                // 1,024 distinct ports: a source
                    // spreads its flows across many tuples, so sampling verdicts
                    // stay independent *within* a source (8 heavy sources, ~8 kept)
                    443,
                    "tcp",
                    1_700_000_000_000L + i * 100L,
                    heavy ? 30_000 : 50,
                    heavy ? 10L * 1024 * 1024 : 1024,
                    heavy ? 1_000 : 3));
        }
        return flows;
    }
}
```

### Step 7: Verify the expected outputs

| Observation | Expected | Why |
|-------------|----------|-----|
| Hash sampling kept | 949 of 100,000 (binomial mean 1,000, σ ≈ 32) | one deterministic draw of `kept ~ Bin(n, p)` — the same tuples always give the same verdict |
| Estimated flows | 94,900 | `kept/p` is unbiased for flow counts; any single draw lands within the CI below |
| CI half-width | ±6.2% | 1.96·sqrt(0.99/1000) — the honest error bar at 100k flows |
| Estimated volume | 9.5 GB (true: 8.1 GB) | keptBytes × 1/p; the 9 kept heavy flows in this draw slightly overshoot the 8.1 GB truth — expected variance, not bias |
| Top talkers | 7 heavy sources ≈ 1.0–3.1 GB each (1–3 kept flows) + 1 small source | eviction by bytes keeps heavy hitters — the 8-key budget holds exactly 8 buckets |
| `other` bucket | 939 flows, 96.2 MB | the bounded-cardinality tail — memory capped at 8 keys by construction |
| Packet-style sampling | kept 3,742 → est 374,200 (≈3.7× inflation) | long flows survive ≈ 100%, short flows ≈ 3% — count estimate corrupted |
| Same seed, same runs | identical output | RandomSampler seeded; HashSampler is pure and stateless |

The headline contrast: both samplers run at the same nominal rate, yet one reports ≈ 95,000 flows (±6.2%) and the other reports ≈ 374,000 — 3.7× the truth. The difference is not implementation quality — it is *which decision is sampled*. Flow sampling samples conversations; packet sampling samples packets, and then pretends conversations were sampled.

---

## Complexity Analysis

- **Sampling decision**: O(tuple length) — one FNV-1a pass, stateless, no memory.
- **Aggregation**: O(1) per flow via hash map; eviction is O(maxKeys) and happens at most once per distinct key, so total eviction work is O(D·maxKeys) over D distinct keys.
- **Memory**: bounded by `maxKeys` buckets regardless of D — the cardinality budget is a hard cap, not a hope.
- **Estimator**: O(1) per report; CI is closed-form (binomial), no simulation.
- **Determinism**: hash sampling is pure; the comparator sampler is seeded. Both produce byte-identical reports across runs and JVMs.

---

## Follow-Up Questions

1. **How would you set the sampling rate adaptively?** Target a fixed *kept sample count* per window (say 100k flows/min), measure kept rate per window, and publish `p = target / estimated_arrival_rate` per node — bounded below by the minimum rate that still catches rare flows. Error bars then stay constant while volume varies.

2. **How do you estimate the *number of distinct* flows when sampling?** Count estimates are easy; distinct counts are hard — the classic problem. Options: sample hashes into a fixed-size Bloom filter / HyperLogLog *before* the sampling decision (HLL counts what was sampled, scaled by 1/p with the caveat that misses are systematic for short flows). This is the "cardinality under sampling" trap, worth its own question in interviews.

3. **How does flow sampling interact with multi-replica collection?** Because the hash decision is deterministic on the five-tuple, two agents observing the same flow make the same decision — no double counting, no split flows. That property is *why* the demo's HashSampler exists and why production sFlow/IPFIX implementations use hash-based selection.

4. **Where does the `other` bucket's data go for drill-down?** The evicted tail is summarized, but the raw flow records are still in the low-retention hot store. The `other` bucket is the *query-path* compression; the drill-down path queries the hot store by time window — exactly the two-tier design (hot high-cardinality, cold bounded rollups).

5. **What dimensions belong in the budget?** Source, destination, service (derived from IP), and tuple get separate aggregators with separate budgets; client IP is the classic budget-bomber (NAT pools, mobile egress) and often gets a lower budget with a dedicated `other`. Every new dimension is a design review, not a config change.

6. **How do you validate the pipeline's estimates?** Replay a captured corpus with full ground truth: sample at several rates, compare estimates against true counts, and assert the error stays inside the reported CI. This is the completeness test that turns sampling math from a claim into a contract.
