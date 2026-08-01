# Mock Interview: Graph Isomorphism

> Coding mock interview — 45-minute session, Java 21+, whiteboard style.

---

## Interview Setup

**Role**: Graph Algorithms Engineer (Social Network / Fraud Detection Team)
**Candidate Level**: Senior Engineer
**Focus Area**: Graph theory, backtracking, invariants, complexity
**Problem**: Implement graph isomorphism checking for small graphs (n ≤ 20) with invariant-based pruning.
**Language**: Java 21+ (records, sealed types, pattern matching allowed)

---

## Interviewer Cheat Sheet (Prepared Questions)

1. Define graph isomorphism. When are two graphs "the same"?
2. What is the naive algorithm, and what's its complexity?
3. What invariants can you compute cheaply? Why do they prune the search?
4. Why is the general problem hard — what's the complexity class situation?
5. How does degree-ordered backtracking work?
6. Follow-up: Weisfeiler-Leman, canonical labeling (nauty), and what happens with labeled vs unlabeled graphs.

---

## Transcript

### Part 1: Problem Clarification (5 minutes)

**Interviewer**: "In fraud detection we match a suspicious transaction graph against known fraud patterns. The graphs are small — at most twenty vertices — but we match thousands of pairs per second. I need graph isomorphism for undirected, unweighted graphs. Clarify."

**Candidate**: "First: do the vertex labels matter? I'll assume not — isomorphism ignores labels, which is the point. Second: what do you mean by 'match' — exact isomorphism, or subgraph isomorphism (pattern inside a larger graph)? Those are very different problems: subgraph isomorphism is NP-complete, exact isomorphism is in quasi-polynomial and much easier in practice. Third: is there any structure, like bounded degree or planarity, that I can exploit?"

**Interviewer**: "Exact isomorphism, n ≤ 20, but I need it fast and deterministic."

**Candidate**: "For n ≤ 20, the right approach is backtracking with invariant pruning: compute a degree-based invariant first, refine it (that's the Weisfeiler-Leman style 1-dimensional color refinement), and only then run a backtracking search over candidate vertex mappings. That handles adversarial inputs in milliseconds at this size."

### Part 2: Theory (10 minutes)

**Interviewer**: "Define it formally."

**Candidate**: "Two graphs G = (V_G, E_G) and H = (V_H, E_H) are isomorphic if there exists a bijection φ: V_G -> V_H preserving adjacency: (u, v) ∈ E_G iff (φ(u), φ(v)) ∈ E_H. So the graphs are 'the same up to renaming vertices.' Deciding existence of φ is graph isomorphism (GI). For our small graphs, we can search the space of bijections directly."

**Interviewer**: "What's the naive approach and its cost?"

**Candidate**: "Try every bijection: n! permutations, each checked in O(n²) adjacency lookups — O(n! · n²). For n = 20 that's ~2.4×10¹⁸ permutations — hopeless. The key observation is that isomorphism is a *constraint satisfaction problem*: φ must map each vertex to a vertex with a compatible adjacency pattern, and we can prune aggressively."

**Interviewer**: "What about complexity class — the audience always asks."

**Candidate**: "GI is one of the famous 'in-between' problems: it's in NP, and it's known to be in quasi-polynomial time QP = exp(O((log n)^c)) since Babai's 2015/2016 breakthrough — but it's not known to be in P, and it's not known to be NP-complete. Believing it's NP-complete would collapse the polynomial hierarchy, so the consensus is it's strictly between, if P ≠ NP. For practical purposes the fast algorithms are: backtracking with Weisfeiler-Leman refinement for small n (our case), and canonical labeling (nauty/Traces) for large structured graphs."

### Part 3: Design (8 minutes)

**Interviewer**: "Design the algorithm."

**Candidate**: "Three stages. Stage 1 — cheap invariants: vertex degrees; if the degree multisets differ, reject immediately. Stage 2 — color refinement (1-WL): iteratively refine colors where a vertex's color becomes (old color, sorted multiset of neighbor colors) until a fixed point; if the color histograms differ, reject. This catches almost all non-isomorphic pairs without any search. Stage 3 — backtracking with symmetry breaking: if refinement reached a fixed point, the colors give a partition; map vertices of the same color class to each other, ordering the candidates by color class so the search tree is minimal. At each partial mapping, verify adjacency consistency incrementally."

**Interviewer**: "What pruning makes the search fast in practice?"

**Candidate**: "Three layers. (1) Degree/color pruning before search — the histogram test rejects most pairs outright. (2) Within the search: when mapping vertex u of G to candidate v of H, check all already-mapped neighbors: every edge (u, u') must correspond to an edge (v, v'), and every non-edge to a non-edge — the 'adjacency consistency' check, O(deg(u)) per candidate. (3) Symmetry: since isomorphic graphs can have automorphisms, I use a canonical ordering of the color classes and never branch on symmetric choices."

### Part 4: Implementation (18 minutes)

**Interviewer**: "Code the core."

**Candidate**: "The adjacency matrix is a long-bitmask per vertex — n ≤ 20 fits in one long. Backtracking over vertices of G in a fixed (color-refined) order, maintaining `map` and `inverse`, checking consistency incrementally."

```java
public static boolean areIsomorphic(Graph g, Graph h) {
    if (g.n() != h.n()) return false;
    if (!degreeHistogramsEqual(g, h)) return false;
    int[][] colors = refineColors(g, h);  // colors[0] for g, colors[1] for h
    if (!colorHistogramsEqual(colors[0], colors[1])) return false;
    int[] order = canonicalOrder(colors[0]);  // group vertices by color
    return backtrack(g, h, colors, order, 0, new int[g.n()], new boolean[g.n()]);
}

private static boolean backtrack(Graph g, Graph h, int[][] colors, int[] order,
                                 int idx, int[] map, boolean[] used) {
    if (idx == order.length) return true;
    int u = order[idx];
    for (int v = 0; v < h.n(); v++) {
        if (used[v]) continue;
        if (colors[0][u] != colors[1][v]) continue;   // invariant: same color
        if (!adjacencyConsistent(g, h, u, v, map)) continue;
        map[u] = v; used[v] = true;
        if (backtrack(g, h, colors, order, idx + 1, map, used)) return true;
        map[u] = -1; used[v] = false;
    }
    return false;
}
```

**Interviewer**: "What does `adjacencyConsistent` check?"

**Candidate**: "For every already-mapped vertex u' ≠ u: edge(u, u') must equal edge(v, v') where v' = map[u']. If all already-mapped neighbors are consistent, the partial mapping extends. Since we check as we go, a full mapping at the last step is automatically an isomorphism — I don't need a separate O(n²) verification pass."

**Interviewer**: "Why check the non-edge side too?"

**Candidate**: "Because the mapping must preserve *both* presence and absence of edges. A common bug checks only 'if (u, u') is an edge then (v, v') is an edge' — but that admits mappings that add phantom edges. The symmetric check is cheap — a couple of bit tests on the adjacency longs."

### Part 5: Testing (5 minutes)

**Interviewer**: "How do you test it?"

**Candidate**: "Five categories. (1) Trivials: same graph object, different n, empty graphs, complete graphs K_n vs K_n — isomorphic for all n. (2) Classic pairs: K_{3,3} vs the triangular prism — both 6 vertices, 9 edges, regular of degree 3 — NOT isomorphic, and this is the classic test where degree histograms alone are insufficient (both are 3-regular!) — the color refinement or search must decide. (3) Random graphs with random relabeling: generate G, apply a random permutation π, assert isomorphic — the property test. (4) Perturbed pairs: relabel and then add/remove one edge — assert non-isomorphic. (5) Performance: n = 20 with the hardest case, a pair that passes color refinement but needs deep search."

**Interviewer**: "Why is the K_{3,3} vs prism pair the canonical stress test?"

**Candidate**: "Because it defeats every naive invariant: equal n, equal edges, equal degree sequence (all degree 3). Vertex degrees won't split it. Color refinement will — after a few rounds the 1-WL colors distinguish the two graphs because their 2-neighborhood structures differ. It's the perfect unit test for 'invariants are not enough' — the search must actually run, and this input exercises it."

### Part 6: Follow-ups (5 minutes)

**Interviewer**: "Where does this show up beyond fraud?"

**Candidate**: "Chemistry: two molecules are the same compound iff their molecular graphs are isomorphic — canonical SMILES strings are computed via canonical labeling. Compiler theory: detecting structurally identical subexpressions. Databases: canonical forms for deduplication. And subgraph isomorphism — pattern matching in social graphs, metabolic networks — is the hard variant, NP-complete, usually attacked with VF2 or index-based filtering."

**Interviewer**: "And the practical tooling?"

**Candidate**: "For production I'd use the nauty/Traces library or the networkx `is_isomorphic` backend (VF2) rather than hand-rolling — both are battle-tested. But the interview version — invariants + refinement + backtracking — is exactly how they work internally, just faster and with more cleverness (individualization-refinement)."

---

## Extended Q&A: Follow-up Round

**Q: When is 1-dimensional color refinement (1-WL) insufficient?**

**A**: For regular graphs with equal degrees, refinement can reach a fixed point where both color histograms agree even though the graphs differ structurally — the K₃,₃ vs prism pair is the canonical example, and strongly regular graphs push it further. The escalation paths are higher-dimensional WL (2-WL operates on ordered vertex pairs) and individualization-refinement (nauty's approach): fix a vertex, refine, and branch on the resulting partition.

**Q: How does canonical labeling differ from this decision procedure?**

**A**: The decision procedure answers yes/no for one pair at a time. Canonical labeling (nauty/Traces) computes a canonical form for *any* graph, reducing isomorphism to string equality and enabling hashing, deduplication, and indexed lookup — exactly what chemistry needs to recognize the same molecule under arbitrary atom orderings. This lab's backtracking core is the pedagogical seed of that machinery.

---

## Scoring Rubric

| Area | Excellent (3) | Good (2) | Needs Work (1) |
|------|---------------|----------|----------------|
| Theory | States quasi-polynomial result, NP/coNP status, why not NP-complete | Defines isomorphism correctly | Confuses with subgraph isomorphism |
| Invariants | Degree histogram + color refinement + symmetry breaking | Degree histogram only | No pruning at all |
| Implementation | Incremental consistency, bitmask adjacency, canonical order | Correct but O(n²) per node | Brute-force permutations |
| Testing | K_{3,3} vs prism, random relabeling property test | Happy path only | No negative cases |

## Red Flags
- Only checking edge-presence, not edge-absence (phantom edges).
- Claiming GI is NP-complete.
- Skipping the n-inequality and degree-histogram fast rejects.
- O(n!) with no pruning for n = 20 and calling it done.

## Key Takeaways
- Isomorphism = bijection preserving adjacency and non-adjacency.
- Cheap invariants (degrees, refined colors) reject most pairs before search.
- Backtracking with incremental consistency is fast for n ≤ 20.
- GI is in QP — a rare, celebrated 'in-between' complexity result.
