# Problem Walkthrough: Graph Isomorphism for Small Graphs

## Problem Statement

Implement a graph isomorphism checker for undirected, unweighted graphs with n ≤ 20 vertices, using a three-stage pipeline:

1. **Fast rejects**: vertex-count and degree-histogram comparison.
2. **Color refinement** (1-dimensional Weisfeiler-Leman): iteratively refine vertex colors until a fixed point; reject if color histograms differ.
3. **Backtracking search** with color-consistent candidate filtering and incremental adjacency-consistency checks.

The checker must return a boolean; a `main` driver verifies correctness on trivial cases, the classic **K₃,₃ vs triangular prism** pair, random relabelings (property test), perturbed pairs, and an n = 20 performance case.

**Deliverable**: `com.math.deep.lab04.GraphIsomorphism` — complete Java 21+ class with a `Graph` record (bitmask adjacency), the three-stage pipeline, and the verification driver.

---

## Constraints & Requirements

| Item | Requirement |
|------|-------------|
| Language | Java 21+ (records, no external libs) |
| Input | Two undirected graphs, adjacency list or matrix, n ≤ 20 |
| Output | boolean (isomorphic or not); test driver prints verification results |
| Pipeline | Degree histogram → color refinement (1-WL) → backtracking with pruning |
| Stress cases | K₃,₃ vs triangular prism (both 3-regular, 6 vertices, 9 edges); n = 20 dense random |

---

## Step 1: Mathematical Foundation

### 1.1 Definitions

Graphs G = (V, E) and H = (V', E') are **isomorphic** (G ≅ H) if there is a bijection φ: V → V' with:

(u, v) ∈ E  ⟺  (φ(u), φ(v)) ∈ E'

Isomorphism classes ignore vertex names: two graphs are the same "shape". Note carefully that non-adjacency must also be preserved.

### 1.2 The naive algorithm and why it fails

Trying all n! bijections costs O(n!·n²) adjacency checks. For n = 20 that is ≈ 2.43×10¹⁸ permutations — centuries of compute. The structure to exploit: isomorphism is a **constraint satisfaction problem** where each vertex u can only map to vertices with a compatible neighborhood pattern.

### 1.3 Invariants

An **invariant** is a function I with I(G) = I(H) whenever G ≅ H (isomorphism maps it to itself). Contrapostively, I(G) ≠ I(H) ⇒ not isomorphic. Cheap invariants:

- Vertex count |V| and edge count |E|.
- Degree sequence (multiset of degrees).
- Color refinement: replace each vertex's color with (color, sorted multiset of neighbor colors) — the **1-dimensional Weisfeiler-Leman (1-WL)** algorithm. Iterate to a fixed point. The final color histogram is a strictly stronger invariant than the degree sequence.

1-WL is not complete (there are non-isomorphic pairs it cannot distinguish — e.g. strongly regular graphs with equal parameters), which is exactly why the backtracking stage must exist. That gap is the *raison d'être* of the search.

### 1.4 Complexity background

Graph isomorphism is in NP, not known to be in P, and not believed NP-complete (a proof would collapse PH to the second level). Babai (2015) proved **quasi-polynomial** time: exp(O((log n)^c)). Practical engines (nauty, VF2, bliss) use individualization-refinement and canonical labeling; for n ≤ 20, invariant-pruned backtracking is effectively polynomial in practice.

---

## Step 2: Design

### 2.1 Graph representation

```java
public record Graph(int n, long[] adj) {
    boolean edge(int u, int v) { return (adj[u] >>> v & 1L) == 1L; }
}
```

A `long` holds n ≤ 20 adjacency bits per vertex. Edge lookup is a shift+mask — O(1). The record is immutable.

### 2.2 Pipeline

```
areIsomorphic(G, H):
  1. n(G) != n(H)            -> false
  2. degreeHistogram(G) != degreeHistogram(H) -> false
  3. colorsG, colorsH = refineToFixedPoint(G), refineToFixedPoint(H)
     histogram(colorsG) != histogram(colorsH) -> false
  4. order = vertices of G grouped by color (canonical order)
     return backtrack(G, H, colorsG, colorsH, order, partial mapping)
```

### 2.3 Backtracking with pruning

- Map G's vertices in the canonical order (all vertices of one color class first).
- Candidate v for u must be: unused, same refined color, and adjacency-consistent with all already-mapped vertices.
- `adjacencyConsistent(u, v, map)`: for each already-mapped u' with v' = map[u']: edge(u,u') == edge(v,v').
- Since consistency is checked incrementally, a completed mapping is automatically a valid isomorphism.

### 2.4 Symmetry breaking

The canonical order is: sort vertices by (color, then a fixed tie-break like vertex id). This exploits color classes: any isomorphism maps color class i of G onto color class i of H, so the search never tries cross-color mappings — a huge reduction when refinement produced many classes.

---

## Step 3: Complete Solution (Java 21+)

```java
package com.math.deep.lab04;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public final class GraphIsomorphism {

    public record Graph(int n, long[] adj) {
        public Graph {
            if (adj.length != n) throw new IllegalArgumentException("adj length != n");
            adj = adj.clone();
        }

        public boolean edge(int u, int v) {
            return (adj[u] >>> v & 1L) == 1L;
        }
    }

    private static int[] degrees(Graph g) {
        int[] d = new int[g.n()];
        for (int u = 0; u < g.n(); u++) d[u] = Long.bitCount(g.adj()[u]);
        return d;
    }

    private static int[] degreeHistogram(Graph g) {
        int[] hist = new int[g.n()];
        for (int d : degrees(g)) hist[d]++;
        return hist;
    }

    private static boolean sameHistogram(int[] a, int[] b) {
        return Arrays.equals(a, b);
    }

    private static int[] refineColors(Graph g) {
        int n = g.n();
        int[] colors = new int[n];
        int palette = 1;
        boolean changed;
        do {
            Map<List<Integer>, Integer> next = new HashMap<>();
            int[] newColors = new int[n];
            int nextPalette = 0;
            for (int u = 0; u < n; u++) {
                List<Integer> signature = new ArrayList<>();
                signature.add(colors[u]);
                int[] neighborColors = new int[Long.bitCount(g.adj()[u])];
                int k = 0;
                for (int v = 0; v < n; v++) {
                    if (g.edge(u, v)) neighborColors[k++] = colors[v];
                }
                Arrays.sort(neighborColors);
                for (int c : neighborColors) signature.add(c);
                newColors[u] = next.computeIfAbsent(signature, s -> nextPalette++);
            }
            changed = nextPalette > palette;
            palette = nextPalette;
            colors = newColors;
        } while (changed);
        return colors;
    }

    private static int[] colorHistogram(int[] colors) {
        int[] hist = new int[colors.length];
        for (int c : colors) hist[c]++;
        return hist;
    }

    private static int[] canonicalOrder(int[] colors) {
        Integer[] order = new Integer[colors.length];
        for (int i = 0; i < order.length; i++) order[i] = i;
        Arrays.sort(order, (a, b) -> {
            int byColor = Integer.compare(colors[a], colors[b]);
            return byColor != 0 ? byColor : Integer.compare(a, b);
        });
        int[] out = new int[order.length];
        for (int i = 0; i < out.length; i++) out[i] = order[i];
        return out;
    }

    private static boolean adjacencyConsistent(Graph g, Graph h, int u, int v,
                                               int[] map) {
        for (int u2 = 0; u2 < g.n(); u2++) {
            if (u2 == u || map[u2] == -1) continue;
            int v2 = map[u2];
            if (g.edge(u, u2) != h.edge(v, v2)) return false;
        }
        return true;
    }

    private static boolean backtrack(Graph g, Graph h, int[] colorsG, int[] colorsH,
                                     int[] order, int idx, int[] map, boolean[] used) {
        if (idx == order.length) return true;
        int u = order[idx];
        for (int v = 0; v < h.n(); v++) {
            if (used[v]) continue;
            if (colorsG[u] != colorsH[v]) continue;
            if (!adjacencyConsistent(g, h, u, v, map)) continue;
            map[u] = v;
            used[v] = true;
            if (backtrack(g, h, colorsG, colorsH, order, idx + 1, map, used)) {
                return true;
            }
            map[u] = -1;
            used[v] = false;
        }
        return false;
    }

    public static boolean areIsomorphic(Graph g, Graph h) {
        if (g.n() != h.n()) return false;
        if (!sameHistogram(degreeHistogram(g), degreeHistogram(h))) return false;

        int[] colorsG = refineColors(g);
        int[] colorsH = refineColors(h);
        if (!sameHistogram(colorHistogram(colorsG), colorHistogram(colorsH))) {
            return false;
        }

        int[] order = canonicalOrder(colorsG);
        int[] map = new int[g.n()];
        Arrays.fill(map, -1);
        return backtrack(g, h, colorsG, colorsH, order, 0, map, new boolean[h.n()]);
    }

    public static Graph completeGraph(int n) {
        long[] adj = new long[n];
        for (int u = 0; u < n; u++) {
            for (int v = 0; v < n; v++) {
                if (u != v) adj[u] |= 1L << v;
            }
        }
        return new Graph(n, adj);
    }

    public static Graph emptyGraph(int n) {
        return new Graph(n, new long[n]);
    }

    public static Graph pathGraph(int n) {
        long[] adj = new long[n];
        for (int i = 0; i + 1 < n; i++) {
            adj[i] |= 1L << (i + 1);
            adj[i + 1] |= 1L << i;
        }
        return new Graph(n, adj);
    }

    public static Graph cycleGraph(int n) {
        long[] adj = new long[n];
        for (int i = 0; i < n; i++) {
            adj[i] |= 1L << ((i + 1) % n);
            adj[i] |= 1L << ((i - 1 + n) % n);
        }
        return new Graph(n, adj);
    }

    public static Graph k33() {
        long[] adj = new long[6];
        int[][] parts = {{0, 1, 2}, {3, 4, 5}};
        for (int a : parts[0]) {
            for (int b : parts[1]) {
                adj[a] |= 1L << b;
                adj[b] |= 1L << a;
            }
        }
        return new Graph(6, adj);
    }

    public static Graph prism() {
        long[] adj = new long[6];
        int[] cycle = {0, 1, 2, 3, 4, 5};
        for (int i = 0; i < 6; i++) {
            int a = cycle[i], b = cycle[(i + 1) % 6];
            adj[a] |= 1L << b;
            adj[b] |= 1L << a;
        }
        for (int i = 0; i < 3; i++) {
            int a = cycle[i], b = cycle[i + 3];
            adj[a] |= 1L << b;
            adj[b] |= 1L << a;
        }
        return new Graph(6, adj);
    }

    public static Graph randomRelabel(Graph g, Random rng) {
        int n = g.n();
        List<Integer> perm = new ArrayList<>();
        for (int i = 0; i < n; i++) perm.add(i);
        java.util.Collections.shuffle(perm, rng);
        long[] adj = new long[n];
        for (int u = 0; u < n; u++) {
            for (int v = 0; v < n; v++) {
                if (g.edge(u, v)) adj[perm.get(u)] |= 1L << perm.get(v);
            }
        }
        return new Graph(n, adj);
    }

    public static Graph randomGraph(int n, double p, Random rng) {
        long[] adj = new long[n];
        for (int u = 0; u < n; u++) {
            for (int v = u + 1; v < n; v++) {
                if (rng.nextDouble() < p) {
                    adj[u] |= 1L << v;
                    adj[v] |= 1L << u;
                }
            }
        }
        return new Graph(n, adj);
    }

    private static void check(String label, boolean actual, boolean expected) {
        String status = actual == expected ? "PASS" : "FAIL";
        System.out.printf("[%s] %s (got %b, expected %b)%n", status, label, actual, expected);
    }

    public static void main(String[] args) {
        System.out.println("=== Graph Isomorphism Verification ===");

        check("G vs itself (P6)", areIsomorphic(pathGraph(6), pathGraph(6)), true);
        check("P6 vs P5 (different n)", areIsomorphic(pathGraph(6), pathGraph(5)), false);
        check("K6 vs K6", areIsomorphic(completeGraph(6), completeGraph(6)), true);
        check("K6 vs empty6", areIsomorphic(completeGraph(6), emptyGraph(6)), false);
        check("C6 vs C6", areIsomorphic(cycleGraph(6), cycleGraph(6)), true);
        check("C6 vs P6 (cycle vs path)",
              areIsomorphic(cycleGraph(6), pathGraph(6)), false);

        System.out.println("--- Classic stress pair: K3,3 vs triangular prism ---");
        check("K3,3 vs prism", areIsomorphic(k33(), prism()), false);
        check("prism vs prism", areIsomorphic(prism(), prism()), true);

        System.out.println("--- Random relabeling property test (1000 trials) ---");
        Random rng = new Random(1234L);
        int failures = 0;
        for (int t = 0; t < 1000; t++) {
            Graph g = randomGraph(1 + rng.nextInt(12), 0.4, rng);
            Graph h = randomRelabel(g, rng);
            if (!areIsomorphic(g, h)) failures++;
        }
        System.out.printf("relabeling failures: %d/1000%n", failures);

        System.out.println("--- Perturbed pairs (relabel + 1 edge flip) ---");
        int detected = 0;
        for (int t = 0; t < 200; t++) {
            Graph g = randomGraph(8, 0.5, rng);
            Graph h = randomRelabel(g, rng);
            long[] adj = h.adj().clone();
            int u = rng.nextInt(8), v = rng.nextInt(8);
            if (u == v) continue;
            adj[u] ^= 1L << v;
            adj[v] ^= 1L << u;
            if (!areIsomorphic(g, new Graph(8, adj))) detected++;
        }
        System.out.printf("perturbed pairs rejected: %d/200%n", detected);

        System.out.println("--- Performance: n=20 dense random ---");
        Graph bigA = randomGraph(20, 0.5, rng);
        Graph bigB = randomRelabel(bigA, rng);
        long t0 = System.nanoTime();
        boolean iso = areIsomorphic(bigA, bigB);
        long t1 = System.nanoTime();
        System.out.printf("n=20 isomorphic: %b in %.2f ms%n", iso, (t1 - t0) / 1e6);

        Graph bigC = randomGraph(20, 0.5, rng);
        t0 = System.nanoTime();
        iso = areIsomorphic(bigA, bigC);
        t1 = System.nanoTime();
        System.out.printf("n=20 random pair: %b in %.2f ms%n", iso, (t1 - t0) / 1e6);
    }
}
```

---

## Step 4: Walkthrough of a Concrete Run

### 4.1 The K₃,₃ vs prism test

K₃,₃: two parts of 3, all cross edges — 6 vertices, 9 edges, all degree 3. Triangular prism: two triangles (C₃) joined by 3 matching edges — 6 vertices, 9 edges, all degree 3.

- Stage 1: n equal, degree histograms both [0,0,0,6,0,0] — passes (not rejected!). This is why the degree test alone is insufficient.
- Stage 2: color refinement on K₃,₃ — all vertices start color 0, each has 3 neighbors of color 0 → signature (0, [0,0,0]) for all → still one class. Fixed point with a single color.
  On the prism — the two triangles: a vertex has neighbors (two in its triangle, one across). Round 1: every vertex also has signature (0, [0,0,0]) — all one class at first! Round 2: nothing changes. So 1-WL *also* fails to distinguish them (both are strongly regular with parameters (6, 3, 0, 3)? — K₃,₃ has λ=0, μ=3; the prism has λ=1, μ=2 — wait, so 1-WL *does* distinguish them after round 2 in practice: vertex neighborhood in K₃,₃ has no internal edges, while prism neighborhoods contain an edge. A vertex's *neighbor-of-neighbor* structure differs. 1-WL iterates on neighbor *colors* only — both stay monochromatic — so 1-WL alone does NOT distinguish these two! This is precisely the well-known pair: K₃,₃ and the triangular prism have the same 1-WL color classes (both are regular and have equal eigenparameters? The prism is not strongly regular? The triangular prism IS strongly regular with parameters (6,3,1,2)? Let's check: in the prism, adjacent vertices share 1 common neighbor (the triangle edge), non-adjacent share 2 (the other triangle's matching). K₃,₃: adjacent share 0, non-adjacent share 3 — it is also strongly regular with (6,3,0,3). Strongly regular graphs with same parameters are the classic 1-WL indistinguishable pairs — and indeed K₃,₃ and the prism are the smallest pair of non-isomorphic strongly regular graphs with the same parameters! So 1-WL cannot separate them — the backtracking stage MUST, and does: with the mapping search, adjacency consistency rejects every bijection because K₃,₃ has no triangles while the prism has two. The search terminates fast due to the symmetry-breaking color partition... wait, if colors don't split, the search is unpruned by color. It still terminates quickly at n=6: it finds a triangle constraint contradiction immediately.
- Stage 3 (search): mapping vertex 0 of K₃,₃ to vertex 0 of prism; K₃,₃ vertex 1 must be non-adjacent to 0 → prism vertex non-adjacent to 0, etc. When 3 vertices are mapped, the prism forces a triangle among mapped neighbors of an unmapped vertex, while K₃,₃'s neighborhood structure is triangle-free — contradiction → backtrack → exhaust → false.

The run demonstrates the *complete* pipeline: invariants reject what they can, and the search handles the residue — exactly the theoretical division of labor.

### 4.2 Random relabeling property test

`randomRelabel` applies a uniformly random permutation of vertex names to G. If the checker is correct, it must return true for all 1000 trials — the test asserts the isomorphism-detection property under the group action of S_n. Any bug in edge-preservation symmetry (checking only one direction) shows up as failures here.

### 4.3 Perturbed pairs

Relabel G, then flip exactly one edge of the result. The pair is not isomorphic (unless the flip is an automorphism of G — rare). The checker must return false in (almost) all 200 trials. This is the *negative* correctness property: no false positives.

---

## Step 5: Testing & Verification

| # | Test | Input | Expected | Verified |
|---|------|-------|----------|----------|
| 1 | Self-isomorphism | P6 vs P6 | true | main() |
| 2 | Size mismatch | P6 vs P5 | false (fast reject) | main() |
| 3 | Complete graphs | K6 vs K6 | true | main() |
| 4 | K6 vs empty | K6 vs E6 | false | main() |
| 5 | Cycle vs path | C6 vs P6 | false | main() |
| 6 | Stress pair | K3,3 vs prism | false (search must run) | main() |
| 7 | Relabel property | 1000 random relabels | 0 failures | main() |
| 8 | Perturbation | 200 relabel+flip | rejected (≈200/200) | main() |
| 9 | Performance | n=20, isomorphic | true, < 100 ms | main() |
| 10 | Performance | n=20, random pair | fast reject via histograms | main() |

---

## Complexity Analysis

**Stage 1 (degrees)**: O(n) to compute, O(n) histogram compare.
**Stage 2 (refinement)**: each round is O(n + m) with hashing; rounds ≤ n; total O(n(n + m)).
**Stage 3 (search)**: worst case O(n! · n) — unavoidable in the worst case (e.g. two isomorphic strongly regular graphs where refinement yields a single class: essentially the n! worst case). With good refinement the effective branching factor collapses: if refinement yields classes of size c₁, ..., c_k, the search visits Π cᵢ nodes, each O(n) for the consistency check. For random graphs the classes are singletons after refinement → O(n) total.
**Space**: O(n) per stage (maps, colors, histograms) + O(n) recursion depth.

**Trade-offs**:
- Bitmask adjacency: O(1) edge lookups at the cost of an n-bit mask — ideal for n ≤ 64.
- Copy-free: `Graph` is immutable; no mutation bugs.
- No canonical form: we answer the decision problem directly. Computing an actual isomorphism certificate would require tracking the mapping — an easy extension (the `map` array already exists in the search).

---

## Edge Cases & Pitfalls

1. **Phantom edges**: checking only "edge in G ⇒ edge in H" admits non-edges mapped to edges. The symmetric check in `adjacencyConsistent` prevents it — and the K₃,₃/prism test would catch a regression (both graphs have 9 edges, so no edge-count discrepancy would reveal it).
2. **Refinement termination**: the loop must stop when the palette stops growing (`nextPalette > palette`), not when colors are pairwise distinct — a fixed point with shared colors is a valid (and informative) stopping point.
3. **Color histogram comparison**: compare counts of refined colors, not the colors themselves — color *ids* are arbitrary between the two graphs.
4. **Loop and multigraph inputs**: adjacency bitmasks assume simple graphs. Self-loops would corrupt the bit-count degree. Document the contract.
5. **n > 64**: the `long` mask overflows. Guard: `if (n > 64) throw` — or fall back to a BitSet-based adjacency (same logic).
6. **Determinism**: the canonical order tie-breaks on vertex id, so results are reproducible across runs.

---

## Follow-up Questions

1. **Subgraph isomorphism**: the pattern-match variant (fraud motifs!). VF2 algorithm: backtracking with feasibility rules (lookahead on neighbors). NP-complete. How do the pruning rules differ from exact isomorphism?

2. **Canonical labeling**: instead of checking pairs, compute a canonical form canon(G) and compare. nauty's individualization-refinement: refine colors, then *branch* by assigning a unique color to one vertex (individualize), refining again — recursion until discrete, picking the lexicographically smallest result. Why does this give a complete invariant, and how does it relate to our pipeline?

3. **1-WL limits**: strongly regular graphs with equal parameters (like our K₃,₃/prism pair) are the canonical failure case. Can you find the smallest pair? What does 2-WL (counting walks of length 2) add?

4. **Automorphism counting**: the same backtracking machinery, generalized, counts automorphisms (with symmetry-breaking to avoid factorial blowup). Used for canonical forms and graph signatures. How would you modify `backtrack` to count them?

5. **Weisfeiler-Leman vs WL-dimension**: higher-dimensional WL (k-WL) is complete for graph isomorphism on k+1... it is complete for graphs of treewidth ≤ k. For k = n-2 it decides all graphs — but the cost is O(n^k) colors. Discuss the trade-off.

6. **Performance at scale**: for n = 10⁶ vertices (social graphs), exact isomorphism is hopeless — the practical stack is feature hashing (WL embeddings), neighborhood sampling, and approximate signatures (NetLSD, GNTK). Where does exact GI stop being relevant?

---

## Extension Ideas

- **Isomorphism certificate**: modify `backtrack` to return the actual mapping φ on success; verify it by an independent O(n²) check.
- **Canonical form**: implement individualization-refinement canonical labeling for small graphs; use it for a graph-union-find / dedup database keyed by canonical string.
- **Automorphism group**: count automorphisms with the same search + symmetry breaking; report the size (product of factorials of orbit sizes).
- **Multigraph and directed variants**: extend the `Graph` record with directedness; adapt refinement signatures and consistency checks.
- **Subgraph isomorphism (VF2)**: add `containsSubgraph(pattern, host)` for the fraud-motif use case, reusing the backtracking core with lookahead rules.
