# Guide: Trie (Prefix Tree)

## Overview

A **trie** (pronounced "try"), also called a **prefix tree** or **digital tree**, is an ordered tree data structure used to store a dynamic set of strings, keyed by character prefixes. Unlike a binary search tree, no node stores the entire key — instead, each node represents a single character, and the path from the root to a node spells out the prefix associated with that node.

This structure is fundamental in string processing: autocomplete, spell checkers, IP routing (longest prefix matching), and dictionary implementations.

### Why Not Just Use a HashSet?

| Aspect | HashSet<String> | Trie |
|--------|----------------|------|
| Prefix query | O(n·L) — must iterate all keys | O(L) — walk prefix path |
| Memory (shared prefixes) | No sharing | Shared prefix nodes save space |
| Ordered iteration | Not sorted | Lexicographically sorted DFS |
| Auto-complete | Must filter all keys | DFS from prefix node |

**Key Insight**: When your workload is dominated by prefix queries (startsWith, autoComplete), a trie is the correct choice. For exact-match-only workloads, a HashSet is simpler and often faster.

---

## ASCII Diagram

```
                    root
                 [   ]  (empty string)
               /    |    \
             a      b      c
           /   \    |      |
          n     p   e      a
         /      |   |      |
        t       p   t      t
       /        |   |      |
      /        l   t      t
    "ant"      e            |
              /              s
            /              "cats"
          "apple"
```

Each node has:
- `children[26]` (or Map<Character, Node> for arbitrary alphabet)
- `isEndOfWord` boolean marking terminal nodes
- `prefixCount` for delete optimisation

---

## Source Code Walkthrough

The implementation is in `src/Trie.java` (package `com.ds.advanced.lab01`).

### Node Structure (lines 6-10)

```java
private static class TrieNode {
    TrieNode[] children = new TrieNode[26];
    boolean isEndOfWord;
    int prefixCount;
}
```

- **children[26]**: Fixed-size array for lowercase English letters. Index 0 → 'a', index 25 → 'z'.
- **isEndOfWord**: `true` if this node terminates a complete word.
- **prefixCount**: Number of words that pass through this node. Used for efficient delete.

**Trade-off**: Array gives O(1) child lookup but O(A) space per node (A=26 here). For larger alphabets (Unicode), use `HashMap<Character, TrieNode>` sacrificing speed for memory.

### Root (line 12)

```java
private final TrieNode root = new TrieNode();
```

Root is an empty node representing the empty prefix. It is never deleted.

### insert(String word) — lines 14-23

```java
public void insert(String word) {
    TrieNode node = root;
    for (char c : word.toCharArray()) {
        int idx = c - 'a';
        if (node.children[idx] == null) node.children[idx] = new TrieNode();
        node = node.children[idx];
        node.prefixCount++;
    }
    node.isEndOfWord = true;
}
```

**Walkthrough with `insert("ant")`:**

```
Step 0: root = TrieNode()
Step 1: c = 'a' (idx=0)
        children[0] = new TrieNode()
        prefixCount = 1
Step 2: c = 'n' (idx=13)
        children[13] = new TrieNode()
        prefixCount = 1
Step 3: c = 't' (idx=19)
        children[19] = new TrieNode()
        prefixCount = 1
Step 4: isEndOfWord = true
```

**Complexity**: O(L) time where L = word length. O(L) new nodes created in worst case.

### search(String word) — lines 25-28

```java
public boolean search(String word) {
    TrieNode node = findNode(word);
    return node != null && node.isEndOfWord;
}
```

Delegates to `findNode`. Important: must check `isEndOfWord` — otherwise `search("ant")` would return `true` even if "ant" is only a prefix of "antelope".

### startsWith(String prefix) — lines 30-32

```java
public boolean startsWith(String prefix) {
    return findNode(prefix) != null;
}
```

Only checks that the prefix path exists — no `isEndOfWord` check needed. This is the killer feature of tries.

### delete(String word) — lines 34-55

```java
public boolean delete(String word) {
    if (!search(word)) return false;
    deleteHelper(root, word, 0);
    return true;
}

private boolean deleteHelper(TrieNode node, String word, int depth) {
    if (depth == word.length()) {
        if (!node.isEndOfWord) return false;
        node.isEndOfWord = false;
        return node.prefixCount == 0;
    }
    int idx = word.charAt(depth) - 'a';
    TrieNode child = node.children[idx];
    if (child == null) return false;
    boolean shouldDelete = deleteHelper(child, word, depth + 1);
    if (shouldDelete) {
        node.children[idx] = null;
    }
    node.prefixCount--;
    return node.prefixCount == 0 && !node.isEndOfWord;
}
```

**Logic**: Recursive post-order traversal. After recursing on child, check if child's subtree can be deleted:
- `prefixCount == 0` — no other words pass through
- `!isEndOfWord` — this node doesn't terminate a word

If both true, the node is safe to prune (set parent's child pointer to null).

**Crucial detail**: `prefixCount` is decremented even when we don't delete the node, because one fewer word passes through.

### autoComplete(String prefix) — lines 57-63

```java
public List<String> autoComplete(String prefix) {
    List<String> results = new ArrayList<>();
    TrieNode node = findNode(prefix);
    if (node == null) return results;
    dfs(node, new StringBuilder(prefix), results);
    return results;
}
```

DFS from the prefix node, collecting every word in the subtree. StringBuilder for efficient string building (avoids O(L²) string concatenation).

### dfs(...) — lines 65-74

```java
private void dfs(TrieNode node, StringBuilder sb, List<String> results) {
    if (node.isEndOfWord) results.add(sb.toString());
    for (int i = 0; i < 26; i++) {
        if (node.children[i] != null) {
            sb.append((char) (i + 'a'));
            dfs(node.children[i], sb, results);
            sb.deleteCharAt(sb.length() - 1);
        }
    }
}
```

**Backtracking pattern**: Append char → recurse → remove last char. Produces results in lexicographic order because children are visited in alphabetical order.

### findNode(String str) — lines 76-84

```java
private TrieNode findNode(String str) {
    TrieNode node = root;
    for (char c : str.toCharArray()) {
        int idx = c - 'a';
        if (node.children[idx] == null) return null;
        node = node.children[idx];
    }
    return node;
}
```

Returns node at end of string path, or null if path doesn't exist. Used by search, startsWith, and autoComplete.

---

## Complexity Table

| Operation | Time | Space | Notes |
|-----------|------|-------|-------|
| Insert | O(L) | O(L) per new word | O(L·A) worst-case for new nodes |
| Search | O(L) | O(1) | No extra allocation |
| Delete | O(L) | O(L) recursion stack | Recursive depth = L |
| startsWith | O(L) | O(1) | Best-case: early exit if prefix missing |
| Auto-complete | O(L + R) | O(R) | R = results count. DFS visits all result nodes |

**L** = word length, **A** = alphabet size, **R** = number of autocomplete results

### Amortised Analysis

Insert of N words with average length L:
- Total time: O(N·L)
- Total nodes created: at most N·L (no shared prefixes) to N + L (maximum sharing)
- Amortised nodes per word: between 1 and L

### Worst-Case vs Average-Case

| Metric | Worst Case | Average Case |
|--------|------------|--------------|
| Time (insert) | O(L) | O(L) |
| Time (search) | O(L) — all characters present | O(L) — typically early exit on unknown character |
| Space | O(N·L·A) — no prefix sharing | O(N·L·A) with compression factor based on overlap |
| Tree height | L (depth of longest word) | ~log(N) for dictionary with prefix overlap |

### Space Optimisations

| Variant | Memory | Trade-off |
|---------|--------|-----------|
| Array-based (26 slots) | 26 pointers per node (208 bytes) | Fast, fixed alphabet |
| HashMap-based | ~O(children) pointers | Slower, Unicode support |
| Compressed (Radix Tree) | Fewer nodes | More complex, harder delete |
| Double-array trie | Two arrays, compact | Complex build, fast query |
| HAT-trie | Cache-friendly | Hybrid of trie + hash |

---

## Comparison with Alternatives

| Feature | Trie | Binary Search Tree | HashSet | Suffix Tree |
|---------|------|-------------------|---------|-------------|
| Prefix query | O(L) | O(n·L) | O(n·L) | O(L) |
| Exact search | O(L) | O(L·log n) | O(L) avg | O(L) |
| Lexicographic order | O(n·L) DFS | In-order O(n) | Not sorted | O(n) |
| Memory | O(N·L·A) | O(N) | O(N·L) | O(N·L) |
| Delete | O(L) | O(log n) | O(1) avg | Complex |
| Wildcard search | DFS + backtracking | Not supported | Not supported | O(L·Σ) |
| Longest prefix match | O(L) | Not directly | Not directly | O(L) |

**When NOT to use a trie:**
- Small N (use HashSet): overhead of node objects outweighs benefits
- Long strings with no common prefixes (space waste)
- Need ordered operations beyond lexicographic (use BST)
- Memory-constrained (use Bloom filter for membership, not prefix)

---

## Use Cases

### 1. Search Autocomplete
**System**: Google Search's typeahead, Facebook's friend search, IDE code completion
**Why trie**: Must find all strings with a given prefix in <10ms
**Scale**: Millions of phrases, 100K+ QPS

**Architecture**:
```
[User types] → [HTTP request] → [Prefix extraction]
                                     ↓
                        [Cache: hot prefixes in Redis]
                                     ↓ (miss)
                            [Trie shard by locale]
                                     ↓
                          [Top-k results by frequency]
```

### 2. IP Routing (Longest Prefix Match)
**System**: Router forwarding information base (FIB)
**Structure**: Patricia trie (compressed binary trie)
**Why trie**: Must match IP prefix of variable length (e.g., 192.168.0.0/16)
**Key op**: Find the most specific prefix that matches a given IP

### 3. Spell Checker / Dictionary
**System**: Microsoft Word, mobile keyboards
**Why trie**: O(L) word lookup, prefix-based suggestions, edit-distance traversal possible
**Extension**: Add word frequency for ranking suggestions

### 4. Genome Sequence Matching
**System**: Bioinformatics pattern matching
**Structure**: Suffix trie → suffix tree → suffix array (evolution of the idea)
**Why**: DNA sequences have high prefix overlap

### 5. Phone Directory
**System**: Contact search autocomplete
**Why**: Multi-field prefix (name + number)
**Variant**: Trie per field, or combined trie with special delimiter

### 6. Compiler Symbol Table
**System**: Compilers (lexing phase, identifier resolution)
**Why**: Fast lookup of keywords, variable names
**Trick**: Store language keywords in trie, lexer walks trie while scanning

### 7. Redis Key Matching
**System**: Redis `KEYS` and `SCAN` commands
**Why**: Prefix-based key iteration (e.g., `user:*`)

---

## Common Pitfalls

### 1. Forgetting `isEndOfWord`
```java
// WRONG — returns true for "app" when only "apple" exists
public boolean search(String word) {
    return findNode(word) != null;
}
```

### 2. Case Sensitivity
Trie is case-sensitive: "Apple" and "apple" are different words. In autocomplete, you usually lowercase before insert/search.

### 3. Unicode / Emoji
Fixed array of 26 fails for non-ASCII. Use `HashMap<Integer, TrieNode>` where key is Unicode code point.

### 4. Memory Overhead in Java
Each `TrieNode` object has header overhead (~16 bytes) plus references. For 1M words avg length 10, naive trie can use 200MB+. Solutions:
- **HashMap-based children**: only store used characters
- **Array shrinking**: store only used portion
- **Off-heap storage**: direct ByteBuffer for children

### 5. Concurrent Access
Trie is not thread-safe. Options:
- Copy-on-write: publish new root after batch updates
- Lock striping: per-node or per-level locks
- Persistent trie: immutable, new root for every update (used in functional languages)

### 6. Stack Overflow in DFS
Deep trie (e.g., 100K-long string) causes stack overflow in recursive DFS. Use iterative stack or tail recursion.

---

## Advanced Variants

### Radix Tree (Compressed Trie)
Merges nodes with single child into one edge labeled with substring. Reduces node count dramatically. Used in Linux kernel for IPv4 routing.

### Suffix Trie → Suffix Tree → Suffix Array
Suffix trie of string S contains all suffixes of S. Compressed → suffix tree. Array-based → suffix array (see Lab 03).

### Double-Array Trie
Compresses trie into two arrays (base, check). Used in Japanese IME (input method editors) and Darts library.

### HAT-Trie (Cache-Friendly)
Array of 256 tries (one per first character). Reduces pointer chasing. 2-3x faster than standard trie for large datasets.

### Burst Trie
Adaptive: starts as list of words, "bursts" into trie when threshold exceeded. Fast for moderate-sized dictionaries.

### Ternary Search Trie
Hybrid of trie and BST. Each node has 3 children (less, equal, greater). Less memory than trie, more than BST.

---

## Testing the Implementation

```java
public static void main(String[] args) {
    Trie trie = new Trie();

    trie.insert("apple");
    trie.insert("app");
    trie.insert("application");
    trie.insert("apt");
    trie.insert("bat");

    assert trie.search("apple") == true;
    assert trie.search("app") == true;
    assert trie.search("ap") == false;  // prefix but not word
    assert trie.startsWith("ap") == true;

    assert trie.autoComplete("ap").equals(List.of("app", "apple", "application", "apt"));
    // Note: lexicographically sorted due to DFS order

    trie.delete("app");
    assert trie.search("app") == false;
    assert trie.search("apple") == true;  // still exists
    assert trie.startsWith("ap") == true; // other words share prefix
}
```

### Edge Case Tests
```java
// Empty string
trie.insert("");
assert trie.search("") == true;

// Single character
trie.insert("a");
assert trie.search("a") == true;
assert trie.autoComplete("").size() > 0;

// Non-existent prefix
assert trie.autoComplete("zzz").isEmpty();

// Delete non-existent word
assert trie.delete("xyz") == false;

// Repeated insert
trie.insert("apple");
trie.insert("apple");  // should this be idempotent?
// Current implementation: prefixCount incremented twice
// isEndOfWord stays true
assert trie.search("apple") == true;
```

---

## Key Interview Takeaways

1. **Trie is for prefix problems.** If the problem mentions "prefix", "autocomplete", "startsWith", trie is likely the answer.

2. **Space-time trade-off**: Trie trades memory for speed on prefix queries. Always mention this.

3. **Delete is tricky**: Recursive post-order with pruning requires tracking how many words share a node.

4. **Wildcard search**: If '.' matches any character, the search becomes DFS with backtracking — this is LC 211 (Design Add and Search Words Data Structure).

5. **Word Search II (LC 212)**: Trie + DFS on grid. The trie prunes the search space dramatically compared to HashSet.

6. **Variants**: Know that compressed trie (radix tree), suffix tree, and double-array trie exist, and when each is appropriate.