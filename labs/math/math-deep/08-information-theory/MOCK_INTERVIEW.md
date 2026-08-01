# Mock Interview: Huffman Coding with Entropy Analysis

> Coding mock interview — 45-minute session, Java 21+, whiteboard style.

---

## Interview Setup

**Role**: Compression Engineer (Storage / Data Platform Team)
**Candidate Level**: Senior Engineer
**Focus Area**: Information theory, greedy algorithms, prefix-free codes, data structures
**Problem**: Implement Huffman coding (frequency build → tree → prefix-free codes → encode/decode) with an entropy analysis comparing the code's average length against the source entropy.
**Language**: Java 21+ (records, PriorityQueue, Map/Streams allowed)

---

## Interviewer Cheat Sheet (Prepared Questions)

1. State Huffman's algorithm and prove its optimality claim (the exchange argument).
2. Define the entropy H and the Kraft inequality. What is the redundancy of a code?
3. Why is Huffman optimal among prefix-free codes, and what's the gap to entropy?
4. How do you represent the tree so decoding works without a symbol table?
5. What breaks in a streaming setting — and what does the classic DEFLATE stack do instead?
6. Follow-up: canonical Huffman, adaptive Huffman, arithmetic coding.

---

## Transcript

### Part 1: Problem Clarification (5 minutes)

**Interviewer**: "We store tens of petabytes of log files with highly skewed symbol frequencies. I want a Huffman coder with an entropy analysis — prove to me the code is actually near-optimal on our data. Clarify."

**Candidate**: "Three questions. Input model: symbol alphabet and frequency map — I'll assume a `Map<Character, Long>` or byte-based alphabet. Second, what's the output contract: a bitset encoding + the code table so decode works — I'll return a small record with the encoded bit stream, the tree (or canonical table), and stats. Third: do you want the entropy comparison computed as part of the output? I think yes — the average code length must be compared against the source entropy H and the bound H ≤ L̄ < H + 1."

**Interviewer**: "Yes — that bound is the heart of it. Implement the full stack."

**Candidate**: "Then the plan: build the Huffman tree with a priority queue merging the two lowest-frequency nodes; derive the prefix-free codes by walking the tree (left 0, right 1); encode; decode by walking the tree from the bits; and compute H = -Σ pᵢ log₂ pᵢ, the average length L̄ = Σ pᵢ ℓᵢ, and the redundancy L̄ - H."

### Part 2: Theory (10 minutes)

**Interviewer**: "Prove Huffman is optimal."

**Candidate**: "Two lemmas. Lemma 1 (exchange): in an optimal code, the two least-probable symbols have codes that differ only in the last bit — they're siblings at the deepest level. If not, swapping them into that position can only decrease the expected length. Lemma 2 (induction): merging the two least-probable symbols into a single symbol with the sum probability — their internal codes differ in the last bit by construction — gives a smaller instance; by induction the greedy merge builds the optimal code for the merged instance, and unmerging preserves optimality. Together: greedy merge at each step is optimal — Huffman's algorithm is a correct greedy algorithm, proven by induction with the exchange argument."

**Interviewer**: "Now the entropy bound. Why can't we do better, and how good is this?"

**Candidate**: "The Kraft inequality: any prefix-free code over alphabet sizes d satisfies Σ 2^{-ℓᵢ} ≤ 1 — the lengths are the leaves of a tree. Minimizing Σ pᵢℓᵢ subject to Kraft gives, by Lagrange multipliers, ℓᵢ ≈ -log₂ pᵢ and the minimum equals the entropy H = -Σ pᵢ log₂ pᵢ. Since lengths must be integers, the optimum is trapped between H and H + 1: H ≤ L̄ < H + 1. Huffman achieves exactly the minimum of the integer problem, so it lands in that band — typically within a few percent for real data. The redundancy L̄ - H is at most 1 bit per symbol, and with block coding (group k symbols) it shrinks to at most 1/k — the theoretical path to Shannon's limit."

**Interviewer**: "What does the redundancy mean for our log data?"

**Candidate**: "If H = 3.2 bits and L̄ = 3.4 bits, we're at 93.8% of the information-theoretic limit — the remaining gap is mostly the per-symbol 1-bit ceiling plus model mismatch (we estimated frequencies from the sample). The analysis printout tells us *how much headroom is left*: if L̄ is near H, a more complex coder (arithmetic) buys almost nothing; if L̄ ≈ H + 0.9, blocking or arithmetic coding could recover ~0.9 bits/symbol — that's the business decision the analysis supports."

### Part 3: Design (8 minutes)

**Interviewer**: "Design the data structures."

**Candidate**: "A `Node` sealed hierarchy: `Leaf(char symbol, long weight)` and `Internal(weight, left, right)` — actually I'll use a single record with nullable children, or a sealed interface. A `PriorityQueue<Node>` keyed by weight — the classic greedy frontier. For ties, I need a deterministic tie-break (insertion counter) so the tree shape is reproducible. Codes: a `Map<Character, String>` of '0'/'1' strings, or a `Map<Character, BitSet>`-style bit array; for decode, I keep the tree — walk from root consuming bits. The output record: `CodeResult(Map<Character,String> codes, String encodedBits, Node tree, double entropy, double avgLength, double redundancy)` — or a compact bit vector. For the lab I'll use a StringBuilder of '0'/'1' — readable, and the analysis section focuses on the theory, not bit packing."

**Interviewer**: "Why is the deterministic tie-break important in an interview setting?"

**Candidate**: "Because Huffman trees are not unique — different tie-breaking produces different (equally optimal) codes. If my tests hardcode a tree shape, a legitimate alternate implementation fails them. So I test invariants instead: optimality (no other prefix-free code is shorter), prefix-freeness (no code is a prefix of another — verified by a trie walk), and decode(encode(s)) == s. Deterministic tie-breaking just makes the output reproducible."

### Part 4: Implementation (18 minutes)

**Interviewer**: "Code the build and the encode/decode."

**Candidate**:

```java
public sealed interface Node permits Leaf, Internal {
    record Leaf(char symbol, long weight) implements Node {}
    record Internal(long weight, Node left, Node right) implements Node {}
    long weight();
}

public static Node buildTree(Map<Character, Long> freq) {
    PriorityQueue<Node> pq = new PriorityQueue<>(
        Comparator.comparingLong(Node::weight).thenComparingInt(n -> System.identityHashCode(n)));
    freq.forEach((c, w) -> pq.add(new Leaf(c, w)));
    while (pq.size() > 1) {
        Node a = pq.poll(), b = pq.poll();
        pq.add(new Internal(a.weight() + b.weight(), a, b));
    }
    return pq.poll();
}

public static void assignCodes(Node node, String prefix, Map<Character, String> codes) {
    if (node instanceof Leaf(char c, long w)) {
        codes.put(c, prefix.isEmpty() ? "0" : prefix);
    } else if (node instanceof Internal(long w, Node l, Node r)) {
        assignCodes(l, prefix + "0", codes);
        assignCodes(r, prefix + "1", codes);
    }
}
```

**Interviewer**: "The single-symbol corner case — what happens with a one-symbol alphabet?"

**Candidate**: "The queue has one node, the loop doesn't run, and the tree is a single leaf. `assignCodes` with an empty prefix would produce the empty code — which is valid (zero bits for the only symbol) but awkward for a decoder expecting to consume bits. I special-case it: encode as a single '0' bit and decode the empty input as that symbol. It's the classic 'Huffman on one symbol' interview trap — most candidates forget the tree with one node."

**Interviewer**: "Decode?"

**Candidate**:

```java
public static String decode(Node tree, String bits) {
    StringBuilder out = new StringBuilder();
    Node cur = tree;
    for (char b : bits.toCharArray()) {
        cur = (b == '0') ? ((Internal) cur).left() : ((Internal) cur).right();
        if (cur instanceof Leaf(char c, long w)) {
            out.append(c);
            cur = tree;
        }
    }
    return out.toString();
}
```

### Part 5: Testing (5 minutes)

**Interviewer**: "Test plan?"

**Candidate**: "Six layers. (1) Single-symbol alphabet — the corner case above. (2) Round-trip on random strings over small alphabets — decode(encode(s)) == s for 1,000 trials; this catches tree-shape bugs. (3) The prefix-free invariant — no code is a prefix of another, checked with a trie walk; this is the defining property that makes decode unambiguous. (4) Optimality spot-check on the classic frequencies {a:45, b:13, c:12, d:16, e:9, f:5} — the textbook tree has a specific shape; I verify L̄ matches the known optimal 2.24. (5) The entropy band: for every trial, assert H ≤ L̄ < H + 1 — the theory contract. (6) Skewed data (log-file-like: 'e' dominates) — L̄ should be near 1.05 with H ≈ 0.99, showing the code's efficiency on exactly the data the product sees."

### Part 6: Follow-ups (5 minutes)

**Interviewer**: "Where does Huffman sit in the real compression stack?"

**Candidate**: "In DEFLATE (gzip, zlib, PNG, HTTP), Huffman codes the *LZ77 match/literal tokens*, not raw symbols — the frequencies are computed from the actual token stream and the code table is transmitted as a *canonical Huffman* (lengths only, no tree — the tree is reconstructible from the length sequence, which costs much less to encode). The lengths in DEFLATE are themselves Huffman-coded (the famous 'two Huffman trees'). The incremental-window version keeps the code optimal *for the block*, recomputing per block — that's the practical compromise between adaptivity and table-transmission cost."

**Interviewer**: "And when Huffman is not good enough?"

**Candidate**: "Arithmetic coding: a single codeword for the whole message, lengths can be fractional, redundancy → 0 asymptotically, and it adapts symbol-by-symbol. Range coding is the modern arithmetic variant (LZMA, brotli uses it? brotli uses prefix codes; LZMA uses range coding). The price: slower, more complex, and patent history made it historically awkward. The entropy analysis tells you when the upgrade pays: only if Huffman's ≤1-bit/symbol gap is material."

---

## Scoring Rubric

| Area | Excellent (3) | Good (2) | Needs Work (1) |
|------|---------------|----------|----------------|
| Theory | Proves optimality (exchange + induction), states Kraft + H ≤ L̄ < H+1 | States the entropy bound | No optimality argument |
| Implementation | Sealed node types, PQ build, prefix walk, decode via tree | Works but muddled | Encode only |
| Edge cases | Single-symbol alphabet, empty input, ties | Single-symbol | None |
| Analysis | Entropy/avg-length/redundancy printed and interpreted | Computes H | No analysis |
| Testing | Round-trip property tests + invariant checks | Single example | No tests |

## Red Flags
- Encoding without a decode path (half the contract).
- Empty code for a non-single alphabet (prefix violation).
- Treating Huffman as unique — and hardcoding tree shapes in tests.
- Claiming Huffman reaches the Shannon limit exactly.

## Key Takeaways
- Greedy merge of the two least-likely symbols is optimal (exchange + induction).
- Kraft inequality ⇒ H ≤ L̄ < H + 1 for any prefix-free code; Huffman achieves it.
- Prefix-free decode = walk the tree; single-symbol alphabet needs a special case.
- Redundancy analysis drives the build-vs-buy decision for arithmetic coding.
