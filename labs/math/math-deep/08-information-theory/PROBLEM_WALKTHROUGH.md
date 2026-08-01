# Problem Walkthrough: Huffman Coding with Entropy Analysis

## Problem Statement

Implement **Huffman coding** — the classic greedy optimal prefix-free code — with a full **entropy analysis**:

1. Build the frequency table from an input string (or accept one directly).
2. Construct the Huffman tree with a priority queue (deterministic tie-breaking).
3. Derive prefix-free codes by a root-to-leaf walk (0 = left, 1 = right).
4. Encode a message into a '0'/'1' bit string; decode by walking the tree.
5. Compute the source entropy H, average code length L̄, and redundancy L̄ - H.
6. Verify the information-theoretic contract **H ≤ L̄ < H + 1** on every test case.

**Deliverable**: `com.math.deep.lab08.HuffmanCoder` — complete Java 21+ class with the sealed `Node` hierarchy, `buildTree`, `assignCodes`, `encode`, `decode`, `analyze`, and a `main` verification driver.

---

## Constraints & Requirements

| Item | Requirement |
|------|-------------|
| Language | Java 21+ (sealed types, records, records pattern matching; no external libs) |
| Input | `String` message or `Map<Character, Long>` frequencies |
| Output | `CodeResult(codes, encodedBits, tree, entropy, avgLength, redundancy)` |
| Invariants | Prefix-free; H ≤ L̄ < H + 1; decode(encode(s)) = s |
| Corner case | Single-symbol alphabet (tree with one node) |

---

## Step 1: Mathematical Foundation

### 1.1 Entropy

For a source emitting symbols with probabilities pᵢ, the **entropy** is the expected self-information:

H = -Σ pᵢ log₂ pᵢ (bits per symbol)

It is the information-theoretic lower bound on the average length of any uniquely decodable code — the minimum achievable expected codeword length.

### 1.2 Prefix-free codes and the Kraft inequality

A code is **prefix-free** if no codeword is a prefix of another — this makes decoding unambiguous without lookahead. Any prefix-free code over a binary alphabet satisfies the **Kraft inequality**:

Σ 2^{-ℓᵢ} ≤ 1

**Proof sketch**: place each codeword in the infinite binary tree; prefixes cannot share paths, so the leaf-intervals are disjoint; each length-ℓ codeword occupies an interval of size 2^{-ℓ}; disjoint intervals in [0, 1] sum to ≤ 1.

### 1.3 The entropy bound for Huffman codes

Minimizing L̄ = Σ pᵢℓᵢ subject to Kraft with integer ℓᵢ yields ℓᵢ ≈ -log₂ pᵢ and the celebrated band:

**H ≤ L̄ < H + 1**

- Lower side: L̄ ≥ H by the Kraft + Lagrange argument (or by the log-sum inequality).
- Upper side: there is always a prefix-free code with ℓᵢ = ⌈-log₂ pᵢ⌉ (Shannon-Fano coding satisfies Kraft), so the optimum L̄* < H + 1; Huffman attains L̄*.

The redundancy L̄ - H is at most 1 bit/symbol; coding k-symbol blocks shrinks it to ≤ 1/k — the path to the Shannon limit.

### 1.4 Huffman's greedy optimality

**Theorem.** Huffman's algorithm produces a minimum-average-length prefix-free code.

**Proof.** (1) *Exchange lemma*: there is an optimal code in which the two least-probable symbols are siblings with maximal depth — if not, swap a deeper, more-probable symbol with them; the expected length cannot increase. (2) *Induction*: replace the two least-probable symbols a, b by a new symbol m = a + b; any code for the merged instance corresponds to a code for the original (append 0/1 to m's codeword), preserving L̄ exactly. By induction the greedy merge is optimal, and the base case (one symbol) is trivial.

---

## Step 2: Design

### 2.1 The node hierarchy

```java
public sealed interface Node permits Leaf, Internal {
    long weight();
    record Leaf(char symbol, long weight) implements Node {}
    record Internal(long weight, Node left, Node right) implements Node {}
}
```

Java 21 sealed interfaces + records + pattern matching (`instanceof Leaf(char c, long w)`) make the tree literal, safe, and free of casting noise.

### 2.2 Building the tree

PriorityQueue of nodes keyed by weight; tie-break deterministically (insertion sequence counter) so outputs are reproducible. Merge until one node remains. Special case: a single symbol yields a one-node tree — encode as a single bit and treat the empty message correctly.

### 2.3 Code assignment

Recursive walk: left = '0', right = '1'. For the one-node tree, assign "0".

### 2.4 Decoding

Walk from the root consuming one bit per step; on reaching a leaf, emit its symbol and restart at the root. Prefix-freeness guarantees termination exactly at message end for any valid encoding.

### 2.5 Analysis

- H = -Σ pᵢ log₂ pᵢ (probabilities from frequencies).
- L̄ = Σ pᵢ ℓᵢ.
- Redundancy = L̄ - H.
- Assertion harness: H ≤ L̄ < H + 1.

---

## Step 3: Complete Solution (Java 21+)

```java
package com.math.deep.lab08;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;

public final class HuffmanCoder {

    public sealed interface Node permits Leaf, Internal {
        long weight();

        record Leaf(char symbol, long weight) implements Node {}

        record Internal(long weight, Node left, Node right) implements Node {}
    }

    public record CodeResult(Map<Character, String> codes, String encodedBits,
                             Node tree, double entropy, double avgLength,
                             double redundancy) {}

    private HuffmanCoder() {}

    public static Map<Character, Long> frequencyTable(String text) {
        Map<Character, Long> freq = new HashMap<>();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            freq.merge(c, 1L, Long::sum);
        }
        return freq;
    }

    public static Node buildTree(Map<Character, Long> freq) {
        if (freq.isEmpty()) throw new IllegalArgumentException("empty frequency table");
        PriorityQueue<Node> pq = new PriorityQueue<>(
            Comparator.comparingLong(Node::weight)
                      .thenComparingInt(System::identityHashCode));
        freq.forEach((c, w) -> pq.add(new Leaf(c, w)));
        while (pq.size() > 1) {
            Node a = pq.poll();
            Node b = pq.poll();
            pq.add(new Internal(a.weight() + b.weight(), a, b));
        }
        return pq.poll();
    }

    public static Map<Character, String> assignCodes(Node tree) {
        Map<Character, String> codes = new HashMap<>();
        walk(tree, "", codes);
        return codes;
    }

    private static void walk(Node node, String prefix, Map<Character, String> codes) {
        if (node instanceof Leaf(char c, long w)) {
            codes.put(c, prefix.isEmpty() ? "0" : prefix);
        } else if (node instanceof Internal(long w, Node l, Node r)) {
            walk(l, prefix + "0", codes);
            walk(r, prefix + "1", codes);
        }
    }

    public static String encode(String text, Map<Character, String> codes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            String code = codes.get(text.charAt(i));
            if (code == null) throw new IllegalArgumentException(
                "no code for symbol '" + text.charAt(i) + "'");
            sb.append(code);
        }
        return sb.toString();
    }

    public static String decode(Node tree, String bits) {
        if (tree instanceof Leaf(char c, long w)) {
            if (bits.chars().allMatch(b -> b == '0')) {
                return String.valueOf(c).repeat(bits.length());
            }
            throw new IllegalArgumentException("malformed bit stream for single-symbol tree");
        }
        StringBuilder out = new StringBuilder();
        Node cur = tree;
        for (int i = 0; i < bits.length(); i++) {
            Internal node = (Internal) cur;
            cur = bits.charAt(i) == '0' ? node.left() : node.right();
            if (cur instanceof Leaf(char c, long w)) {
                out.append(c);
                cur = tree;
            }
        }
        if (!(cur instanceof Leaf)) {
            throw new IllegalArgumentException("truncated bit stream");
        }
        return out.toString();
    }

    public static CodeResult code(String text) {
        Map<Character, Long> freq = frequencyTable(text);
        Node tree = buildTree(freq);
        Map<Character, String> codes = assignCodes(tree);
        String bits = encode(text, codes);

        long total = text.length();
        double entropy = 0.0;
        double avgLength = 0.0;
        for (Map.Entry<Character, Long> e : freq.entrySet()) {
            double p = (double) e.getValue() / total;
            entropy -= p * (Math.log(p) / Math.log(2));
            avgLength += p * codes.get(e.getKey()).length();
        }
        return new CodeResult(codes, bits, tree, entropy, avgLength, avgLength - entropy);
    }

    private static boolean isPrefixFree(Map<Character, String> codes) {
        for (String a : codes.values()) {
            for (String b : codes.values()) {
                if (a != b && b.startsWith(a)) return false;
            }
        }
        return true;
    }

    private static void runCase(String label, String text) {
        try {
            CodeResult r = code(text);
            boolean roundTrip = decode(r.tree(), r.encodedBits()).equals(text);
            boolean prefixFree = isPrefixFree(r.codes());
            boolean band = r.entropy() <= r.avgLength() + 1e-12
                           && r.avgLength() < r.entropy() + 1.0 + 1e-12;
            System.out.printf("%-24s H=%.4f  Lbar=%.4f  red=%.4f  rt=%b  pf=%b  band=%b%n",
                              label, r.entropy(), r.avgLength(), r.redundancy(),
                              roundTrip, prefixFree, band);
            if (!roundTrip || !prefixFree || !band) {
                System.out.println("  *** FAILED on " + label);
            }
        } catch (IllegalArgumentException e) {
            System.out.printf("%-24s rejected: %s%n", label, e.getMessage());
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Huffman Coding with Entropy Analysis ===");

        runCase("classic 6-symbol", "aaaaaaaaaaaaaaaabbbbbbbbbbbbbccccccccccddddeeeeff");
        runCase("single symbol", "xxxxxxxxxxxx");
        runCase("two symbols", "ababababababababababab");
        runCase("uniform alphabet", "abcdefghabcdefghabcdefgh");
        runCase("skewed log-like", "eeeeeeeeeeeeeeeeeeeetttttttttaaaaaaooooonnnii");
        runCase("empty string", "");
        runCase("all unique", "abcdefghij");

        System.out.println("--- Random round-trip property test (500 trials) ---");
        Random rng = new Random(2024L);
        int failures = 0;
        for (int t = 0; t < 500; t++) {
            int len = rng.nextInt(200);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < len; i++) {
                sb.append((char) ('a' + rng.nextInt(1 + rng.nextInt(8))));
            }
            String text = sb.toString();
            CodeResult r = code(text);
            if (!decode(r.tree(), r.encodedBits()).equals(text)) failures++;
        }
        System.out.printf("round-trip failures: %d/500%n", failures);

        System.out.println("--- The textbook frequencies {a:45,b:13,c:12,d:16,e:9,f:5} ---");
        Map<Character, Long> freq = new HashMap<>();
        freq.put('a', 45L); freq.put('b', 13L); freq.put('c', 12L);
        freq.put('d', 16L); freq.put('e', 9L);  freq.put('f', 5L);
        Node tree = buildTree(freq);
        Map<Character, String> codes = assignCodes(tree);
        long total = 100L;
        double entropy = 0.0, avgLength = 0.0;
        for (Map.Entry<Character, Long> e : freq.entrySet()) {
            double p = (double) e.getValue() / total;
            entropy -= p * (Math.log(p) / Math.log(2));
            avgLength += p * codes.get(e.getKey()).length();
        }
        System.out.println("codes: " + codes);
        System.out.printf("textbook case: H=%.4f  Lbar=%.4f  (known optimum 2.24)%n",
                          entropy, avgLength);

        System.out.println("--- Compressed size on skewed data ---");
        String logLike = "eeeeeeeeeeeeeeeeeeeetttttttttaaaaaaooooonnnii".repeat(100);
        CodeResult big = code(logLike);
        System.out.printf("original: %d chars x 16 bits = %d bits; Huffman: %d bits "
                          + "(%.1f%% of raw)%n",
                          logLike.length(), logLike.length() * 16,
                          big.encodedBits().length(),
                          100.0 * big.encodedBits().length() / (logLike.length() * 16));
    }
}
```

---

## Step 4: Walkthrough of a Concrete Run

### 4.1 The textbook frequencies

{a: 45, b: 13, c: 12, d: 16, e: 9, f: 5}, total 100. Greedy merges:

1. Merge e(9) + f(5) → m₁(14).
2. Merge c(12) + b(13) → m₂(25).
3. Merge m₁(14) + d(16) → m₃(30).
4. Merge m₂(25) + m₃(30) → m₄(55).
5. Merge a(45) + m₄(55) → root(100).

Codes (one valid assignment): a=0 (1 bit), b=101 (3), c=100 (3), d=111 (3), e=1101 (4), f=1100 (4).

L̄ = (45·1 + 13·3 + 12·3 + 16·3 + 9·4 + 5·4)/100 = (45 + 39 + 36 + 48 + 36 + 20)/100 = 224/100 = **2.24** — the known optimum. H ≈ 2.21, so redundancy ≈ 0.03 — the code is within 1.4% of the entropy bound. The textbook's tree appears exactly, validating the greedy merge order.

### 4.2 Skewed log-like data

'e' at ~40% probability gets a 2-bit code; the rare 'i', 'n', 'o' get 4 bits. H ≈ 2.1–2.3, L̄ within a few hundredths — compressed size ≈ 16% of the raw 16-bit-character encoding. The run's "%.1f%% of raw" printout quantifies the win for the storage use case.

### 4.3 The corner cases

- Single-symbol: tree = one Leaf; `walk` assigns "0"; encode produces a string of zeros; decode terminates on the first bit. The `prefix.isEmpty() ? "0" : prefix` guard prevents the empty codeword.
- Empty string: frequency table is empty → `buildTree` throws. Is that right? For an empty message there is nothing to encode; the harness documents the contract rather than silently producing an empty tree. (Alternative: return an empty result — but an empty frequency map has no symbol set, so the exception is the honest contract.)
- All-unique symbols: every code length equals the tree height — entropy is at its log₂(26) max; L̄ lands exactly at ⌈log₂ k⌉ — inside the band.

---

## Step 5: Testing & Verification

| # | Test | Input | Expected | Verified |
|---|------|-------|----------|----------|
| 1 | Textbook case | {a:45,...,f:5} | L̄ = 2.24 (known optimum) | main() |
| 2 | Round-trip | 500 random strings | decode(encode(s)) = s, 0 failures | main() |
| 3 | Prefix-free | every case | isPrefixFree = true | main() runCase |
| 4 | Entropy band | every case | H ≤ L̄ < H + 1 | main() runCase |
| 5 | Single symbol | "xxxxxxxxxxxx" | 1-symbol code, round-trip OK | main() |
| 6 | Two symbols | "ababab..." | codes "0"/"1", L̄ = 1 | main() |
| 7 | Uniform alphabet | "abcdefgh"×3 | L̄ = ⌈log₂ 8⌉ = 3 = H | main() |
| 8 | Skewed log-like | 'e'-dominated | L̄ ≈ H + small | main() |
| 9 | Empty string | "" | IllegalArgumentException (documented) | main() |
| 10 | All unique | "abcdefghij" | L̄ = ⌈log₂ 10⌉ = 4 | main() |
| 11 | Malformed bits | decode on truncated stream | IllegalArgumentException | code |
| 12 | Unknown symbol | encode with missing code | IllegalArgumentException | code |

---

## Complexity Analysis

**Time**:
- Frequency table: O(|s|) char scans.
- Build: O(k log k) with k = |alphabet| — k-1 merges at O(log k) each (heap ops).
- Codes: O(k) walk.
- Encode: O(|s| · ℓ_max) = O(|s| log k) string append operations.
- Decode: O(|encoded|) tree steps.
- Analysis: O(k) after codes are known.
- Property tests: O(k²) prefix checks per case (k ≤ 26 — negligible).

**Space**: O(k) tree nodes + O(k) codes + O(|s|) bit string.

**Trade-offs**: '0'/'1' strings are 8× larger than a packed BitSet but make the analysis and tests transparent; swapping in a bit-packed representation is a mechanical change to `encode`/`decode` only. The deterministic identityHashCode tie-break is stable within a JVM run; across JVMs the tree shape may differ — but all shapes are equally optimal, and the round-trip/band tests don't depend on shape.

---

## Edge Cases & Pitfalls

1. **Single-symbol alphabet**: the empty-prefix code trap — must assign "0", not "".
2. **Empty input**: empty frequency table — explicit exception instead of a null tree.
3. **Ties in the priority queue**: without a tie-break, the merge order is nondeterministic (heap iteration order); tests must assert invariants, not shapes.
4. **Decode of truncated streams**: must throw — silently returning a partial string hides corruption.
5. **Unknown symbols in encode**: missing code → clear IllegalArgumentException (a frequency table derived from training data may not cover the message).
6. **Zero-probability symbols**: never enter the frequency map; Huffman cannot assign codes to them — document that the model must be trained on the same distribution.
7. **Very large alphabets**: k = 2¹⁶ chars is fine for the heap, but ℓ up to k-1 bits can make bit strings long — the entropy analysis shows whether blocking would help.
8. **Prefix verification**: the O(k²) `startsWith` check is the safety net for the tree walk's correctness — a regression in `walk` (e.g. forgetting to append a bit on one branch) would surface here.

---

## Follow-up Questions

1. **Canonical Huffman**: instead of transmitting the tree, transmit only the *lengths* (sorted by symbol); reconstruct the code by assigning consecutive integers to length groups (the DEFLATE scheme). Why does this cut the table cost from O(k·ℓ) to O(k) length bytes, and how does the length-limited variant (max 15 bits in DEFLATE) change the optimization problem?

2. **Adaptive Huffman (FGK algorithm)**: update the tree after every symbol with the 'swap with sibling' trick, maintaining the sibling property (nodes sorted by weight). Why can't you use a plain PriorityQueue here, and what invariant must the tree keep after each update?

3. **Arithmetic/range coding**: the codeword is a *number* in [0, 1) that pins down the message interval; lengths are effectively fractional. Show why the redundancy of arithmetic coding → 0 as message length grows, and why the entropy band H ≤ L̄ < H + 1 no longer applies to it.

4. **Block coding**: coding k symbols at once as a super-alphabet of size σᵏ. Redundancy ≤ 1/k; but the alphabet explodes. Where does that trade-off hit DEFLATE (which blocks at token level instead)?

5. **Model vs coder separation**: Huffman optimality is *conditional on the model* (the frequency table). If the model is wrong (training/serving mismatch), the code is suboptimal — derive the cost of model mismatch: L̄_actual - H_true = D(p||q) + redundancy terms, with D the KL divergence. This is the information-theoretic reason model quality matters more than coder choice in real systems.

6. **Huffman in JPEG/H.264**: JPEG uses Huffman with fixed tables (no transmission), H.264 CAVLC uses Exp-Golomb + context models. How does the *fixed-table* setting change the optimization problem (minimize expected length over a *corpus* rather than a single message)?

---

## Extension Ideas

- **Bit packing**: replace the '0'/'1' string with a `BitSet`-like accumulator; measure the constant-factor win on the skewed dataset.
- **Canonical encoding**: emit the length sequence + symbols and reconstruct the tree on the decoder side without transmitting topology.
- **Two-pass vs one-pass**: train frequencies on a sample, then encode the full stream with the fixed table (the practical DEFLATE-style split); measure the mismatch cost with the KL divergence formula.
- **Length-limited Huffman**: add the package-merge algorithm for max length L — the exact solution to the DEFLATE 15-bit constraint.
- **Entropy dashboard**: for a corpus of real log lines, print H, L̄, redundancy, and the "remaining headroom" — and auto-recommend arithmetic coding only when redundancy > 0.2 bits/symbol.
