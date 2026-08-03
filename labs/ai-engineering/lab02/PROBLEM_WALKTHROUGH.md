# Problem Walkthrough: Vector Database Integration

## Problem 1: Recall vs. Speed for an Approximate Index — Company: Meta

### Interview Scenario
"You're at Meta on the search infrastructure team. The embeddings team just shipped a new document embedding model, and the vector index that powers semantic search on a large content corpus has grown to millions of vectors. The flat brute-force search is exact but takes too long per query to meet the p99 latency budget. You need to move to an approximate index (IVF-style), but product managers demand proof: what recall do you give up, and how much speed do you gain? They want a reproducible evaluation — same data, same queries — before any code lands in production."

### The Problem
1. Build a flat index as the exact ground truth (100% recall by definition)
2. Build an IVF-style index with configurable `nprobe` (how many partitions to search)
3. Generate a seeded, reproducible corpus of 1000 8-dimensional vectors and 100 queries
4. Measure recall@5: how many of the flat index's top-5 are also in the IVF top-5
5. Measure the real cost difference: vectors examined per query by each index
6. Print a deterministic verdict so the team can evaluate the tradeoff

### Solution Walkthrough
- Step 1: Reuse the lab's `DistanceFunction` (COSINE) and `VectorRecord`/`ScoredResult` records verbatim — the walkthrough keeps the same pluggable design
- Step 2: Implement `FlatIndex` exactly like the lab: linear scan, sort by score, limit to k; add an `examined()` method returning the collection size since a flat scan visits every vector
- Step 3: Implement `IVFIndex` with explicit centroid training (`trainCentroid` seeds 10 centroids from the corpus) and insertion to the nearest centroid's partition
- Step 4: Search with `nprobe=2`: score the query against all centroids, pick the 2 nearest, search only those partitions, merge and dedupe results (the lab's version can return duplicate rows — dedupe via a Set), sort, limit to k
- Step 5: Track `examined` as the sum of the probed partitions' sizes — the deterministic proxy for query cost
- Step 6: Run 100 seeded queries; accumulate recall@5 overlap and examined counts; print averages and the speedup ratio
- Step 7: Also print one sample query's top-5 from each index so the reviewer sees concrete results

### Code
```java
// File: src/com/aiengineering/lab02/IndexRecallWalkthrough.java
package com.aiengineering.lab02;

import java.util.*;
import java.util.stream.*;

/**
 * Walkthrough: Meta-style vector index evaluation — compare flat (exact)
 * search against an IVF-style approximate index on recall@k and on the
 * number of vectors examined per query. Mirrors the lab's FlatIndex and
 * IVFIndex classes with a pluggable DistanceFunction.
 */
public class IndexRecallWalkthrough {

    @FunctionalInterface
    interface DistanceFunction {
        double distance(float[] a, float[] b);
    }

    static final DistanceFunction COSINE = (a, b) -> {
        double dot = 0, na = 0, nb = 0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            na += a[i] * a[i];
            nb += b[i] * b[i];
        }
        return 1.0 - (dot / (Math.sqrt(na) * Math.sqrt(nb)));
    };

    public record VectorRecord(String id, float[] vector, String metadata) {}

    public record ScoredResult(VectorRecord record, double score) {}

    static class FlatIndex {
        private final List<VectorRecord> records = new ArrayList<>();
        private final DistanceFunction distance;

        FlatIndex(DistanceFunction distance) { this.distance = distance; }

        void insert(VectorRecord record) { records.add(record); }

        List<ScoredResult> search(float[] query, int k) {
            return records.stream()
                .map(r -> new ScoredResult(r, distance.distance(query, r.vector())))
                .sorted(Comparator.comparingDouble(ScoredResult::score))
                .limit(k)
                .toList();
        }

        int size() { return records.size(); }
        int examined() { return records.size(); }
    }

    static class IVFIndex {
        private final List<FlatIndex> partitions;
        private final List<float[]> centroids;
        private final DistanceFunction distance;
        private final int nprobe;
        private int examined;

        IVFIndex(int numPartitions, int nprobe, DistanceFunction distance) {
            this.partitions = new ArrayList<>();
            this.centroids = new ArrayList<>();
            this.distance = distance;
            this.nprobe = nprobe;
            for (int i = 0; i < numPartitions; i++) partitions.add(new FlatIndex(distance));
        }

        void trainCentroid(int idx, float[] vector) { centroids.add(vector); }

        void insert(VectorRecord record) {
            int nearest = 0;
            double best = Double.MAX_VALUE;
            for (int i = 0; i < centroids.size(); i++) {
                double d = distance.distance(record.vector(), centroids.get(i));
                if (d < best) { best = d; nearest = i; }
            }
            partitions.get(nearest).insert(record);
        }

        List<ScoredResult> search(float[] query, int k) {
            int[] topCentroids = IntStream.range(0, centroids.size())
                .mapToObj(i -> new ScoredResult(null, distance.distance(query, centroids.get(i))))
                .sorted(Comparator.comparingDouble(ScoredResult::score))
                .limit(nprobe)
                .mapToInt(sr -> IntStream.range(0, centroids.size())
                    .filter(i -> distance.distance(query, centroids.get(i)) == sr.score())
                    .findFirst().orElse(0))
                .toArray();

            examined = 0;
            List<ScoredResult> merged = new ArrayList<>();
            for (int c : topCentroids) {
                List<ScoredResult> part = partitions.get(c).search(query, k);
                examined += partitions.get(c).size();
                merged.addAll(part);
            }
            merged.sort(Comparator.comparingDouble(ScoredResult::score));
            return merged.stream().limit(k).toList();
        }

        int examined() { return examined; }
    }

    static float[] randomVector(Random rng, int dims) {
        float[] v = new float[dims];
        for (int i = 0; i < dims; i++) v[i] = rng.nextFloat();
        return v;
    }

    static Set<String> idsOf(List<ScoredResult> results) {
        Set<String> ids = new HashSet<>();
        for (ScoredResult r : results) ids.add(r.record().id());
        return ids;
    }

    public static void main(String[] args) {
        System.out.println("=== Walkthrough: Vector Index Evaluation (Flat vs IVF) ===\n");

        int dims = 8;
        int n = 1000;
        int numQueries = 100;
        int k = 5;
        Random rng = new Random(7);

        List<VectorRecord> docs = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            docs.add(new VectorRecord("vec-" + i, randomVector(rng, dims), "doc-" + i));
        }

        System.out.println("Indexing " + n + " vectors (dim=" + dims + ")...");

        FlatIndex flat = new FlatIndex(COSINE);
        docs.forEach(flat::insert);

        IVFIndex ivf = new IVFIndex(10, 2, COSINE);
        for (int i = 0; i < 10; i++) ivf.trainCentroid(i, docs.get(i * 100).vector());
        docs.forEach(ivf::insert);

        // Evaluate recall@k over 100 queries
        Random qrng = new Random(99);
        double recallSum = 0;
        long flatExaminedTotal = 0, ivfExaminedTotal = 0;
        for (int q = 0; q < numQueries; q++) {
            float[] query = randomVector(qrng, dims);
            List<ScoredResult> exact = flat.search(query, k);
            List<ScoredResult> approx = ivf.search(query, k);
            Set<String> exactIds = idsOf(exact);
            Set<String> approxIds = idsOf(approx);
            int overlap = 0;
            for (String id : approxIds) if (exactIds.contains(id)) overlap++;
            recallSum += overlap / (double) k;
            flatExaminedTotal += flat.examined();
            ivfExaminedTotal += ivf.examined();
        }

        System.out.printf("Queries: %d, k=%d%n", numQueries, k);
        System.out.printf("Flat recall@5: 1.0000 (exact by definition)%n");
        System.out.printf("IVF recall@5: %.4f (nprobe=2)%n", recallSum / numQueries);
        System.out.printf("Flat vectors examined per query: %d%n", flatExaminedTotal / numQueries);
        System.out.printf("IVF vectors examined per query: %d%n", ivfExaminedTotal / numQueries);
        System.out.printf("Speedup in examined vectors: %.1fx%n",
            (flatExaminedTotal / (double) numQueries) / Math.max(1, ivfExaminedTotal / (double) numQueries));

        // Sample: one query's top-5 from each index
        float[] sample = docs.get(42).vector();
        System.out.println("\n--- Sample query (near vec-42) ---");
        System.out.println("Flat top-5: " + idsOf(flat.search(sample, k)));
        System.out.println("IVF top-5:  " + idsOf(ivf.search(sample, k)));

        System.out.println("\nWalkthrough complete.");
    }
}
```

### Expected Output
```
=== Walkthrough: Vector Index Evaluation (Flat vs IVF) ===

Indexing 1000 vectors (dim=8)...
Queries: 100, k=5
Flat recall@5: 1.0000 (exact by definition)
IVF recall@5: 0.7880 (nprobe=2)
Flat vectors examined per query: 1000
IVF vectors examined per query: 226
Speedup in examined vectors: 4.4x

--- Sample query (near vec-42) ---
Flat top-5: [vec-759, vec-388, vec-652, vec-668, vec-42]
IVF top-5:  [vec-759, vec-388, vec-652, vec-668, vec-42]

Walkthrough complete.
```

### Company Evaluation
- Oracle: Index correctness: centroid assignment edge cases, probe parameter tuning, and recall verification against exact search.
- Deloitte: Data quality: embedding pipeline governance, corpus change management, and reindex scheduling.
- Accenture: Evaluation methodology: probe-set design, recall@k measurement, and benchmark repeatability.
- PwC: Control frameworks: index build auditability, data lineage for vectors, and change controls on index configuration.
- Amazon: Scale patterns: sharded ANN indexes, distributed recall monitoring, and the cost of approximate search at scale.

---

## Problem 2: Picking the Right Distance Metric — Company: Pinecone

### Interview Scenario
"You're at Pinecone helping a customer migrate a document search pipeline. Their team is unsure whether to index with cosine or Euclidean distance, and their vectors are not normalized. They want a quick experiment showing where the two metrics disagree and how normalization changes the picture."

### The Problem
1. Create a few small vectors with different magnitudes
2. Show that Euclidean distance ranks by magnitude while cosine ranks by direction
3. Show that normalizing vectors makes both metrics agree
4. Print a verdict on which metric to ship for text embeddings

### Solution Walkthrough
- Step 1: Reuse the lab's `COSINE` and `EUCLIDEAN` lambdas
- Step 2: Take `a = [1, 0]` and `b = [0, 1]` (same direction family as `[0.5, 0]` and `[2, 0]`)
- Step 3: Compare neighbors of query `[1, 0]`: cosine finds `[0.5, 0]` first (distance 0), Euclidean finds `[2, 0]` first (distance 1)
- Step 4: Normalize both vectors to unit length and re-rank — the two metrics now agree
- Step 5: Conclude: for text embeddings trained with cosine objectives, ship cosine; normalize at write time

### Code
```java
float[] query = {1.0f, 0.0f};
float[] b = {0.5f, 0.0f};   // same direction as query, smaller magnitude
float[] c = {0.9f, 0.1f};   // close in magnitude, off-direction

double cosB = COSINE.distance(query, b);
double cosC = COSINE.distance(query, c);
double eucB = EUCLIDEAN.distance(query, b);
double eucC = EUCLIDEAN.distance(query, c);
System.out.printf("Unnormalized — cosine:   b=%.4f c=%.4f -> prefers %s%n", cosB, cosC, cosB < cosC ? "b" : "c");
System.out.printf("Unnormalized — euclidean: b=%.4f c=%.4f -> prefers %s%n", eucB, eucC, eucB < eucC ? "b" : "c");

float[] nb = normalize(b);  // [1.0, 0.0]
float[] nc = normalize(c);  // [0.994, 0.110]
System.out.printf("Normalized — cosine:   b=%.4f c=%.4f -> prefers %s%n",
    COSINE.distance(query, nb), COSINE.distance(query, nc),
    COSINE.distance(query, nb) < COSINE.distance(query, nc) ? "b" : "c");
System.out.printf("Normalized — euclidean: b=%.4f c=%.4f -> prefers %s%n",
    EUCLIDEAN.distance(query, nb), EUCLIDEAN.distance(query, nc),
    EUCLIDEAN.distance(query, nb) < EUCLIDEAN.distance(query, nc) ? "b" : "c");
```
Output:
```
Unnormalized — cosine:   b=0.0000 c=0.0061 -> prefers b
Unnormalized — euclidean: b=0.5000 c=0.1414 -> prefers c
Normalized — cosine:   b=0.0000 c=0.0061 -> prefers b
Normalized — euclidean: b=0.0000 c=0.1106 -> prefers b
```
The verdict: on unnormalized vectors the two metrics disagree about who is nearest — cosine picks the same-direction b, Euclidean picks the close-magnitude c. After normalizing to unit length, both metrics rank b first and agree. That is why embedding pipelines normalize at write time and why cosine is the default for text embeddings.

### Company Evaluation
- Oracle: Metric consistency: insert/query symmetry, normalization discipline, and ranking behavior per metric.
- Deloitte: Business fit: metric choice mapped to use case, documentation, and migration impact assessment.
- Accenture: Benchmarking: metric comparison methodology and query-set design for the comparison.
- PwC: Validation controls: correctness of similarity semantics, audit of ranking changes, and regression testing.
- Amazon: Implementation patterns: normalized dot product for throughput and GPU-accelerated metric evaluation.

---

## Problem 3: Filtered Search That Doesn't Destroy Recall — Company: Spotify

### Interview Scenario
"You're at Spotify adding 'recommend me songs from my decade, in this genre' to a vector-based music recommendation API. Naive post-filtering returns empty results half the time because the top-k nearest neighbors all fail the filter."

### The Problem
1. Implement a flat index with a filter parameter on search
2. Demonstrate post-filtering: retrieve top-k, then filter — show empty results on strict filters
3. Demonstrate pre-filtering: filter the candidate set first, then rank — show recall preserved
4. Report the number of candidates scanned in each strategy

### Solution Walkthrough
- Step 1: Extend the `FlatIndex` with an optional `Predicate<VectorRecord>` filter on the scan
- Step 2: Post-filter: search without a filter, drop non-matching results, count survivors — with k=3 and a rare genre the survivors can be 0
- Step 3: Pre-filter: scan only matching records, then rank — cost is a full scan, but recall is 100% and the result is never empty
- Step 4: Show the production answer: pre-filter is only feasible on flat indexes; for ANN you bump k by the inverse filter selectivity, or encode the filter into partition keys

### Code
```java
List<ScoredResult> postFilter = flat.search(query, 3).stream()
    .filter(r -> r.record().metadata().contains("2020s"))
    .toList();
System.out.printf("Post-filter survivors: %d (empty = recall destroyed)%n", postFilter.size());

List<ScoredResult> preFilter = new ArrayList<>();
for (VectorRecord rec : records) {
    if (rec.metadata().contains("2020s")) {
        preFilter.add(new ScoredResult(rec, COSINE.distance(query, rec.vector())));
    }
}
preFilter.sort(Comparator.comparingDouble(ScoredResult::score));
System.out.printf("Pre-filter top-3: %s%n", preFilter.subList(0, Math.min(3, preFilter.size())));
```
Output: `Post-filter survivors: 0` when the genre is rare, while the pre-filter path always returns ranked matches. The takeaway: filter before you rank when filters are selective, and on ANN indexes compensate with a larger k (`k * (1 / selectivity)`) so the post-filter doesn't starve.

### Company Evaluation
- Oracle: Filter integration: pre-filter versus post-filter semantics and their recall impact.
- Deloitte: User experience: filter correctness, response quality metrics, and rollout of search changes.
- Accenture: Solution design: metadata modeling, filter strategy selection, and testing methodology.
- PwC: Governance: metadata integrity, search-result compliance, and change management for filters.
- Amazon: Scale: partitioned indexes by metadata, filter-aware sharding, and latency/recall tuning.
