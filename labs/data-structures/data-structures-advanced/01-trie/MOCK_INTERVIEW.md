# Mock Interview: Trie (Prefix Tree)

## Setting

- **Round**: First technical phone screen
- **Duration**: 45 minutes
- **Platform**: Zoom + shared CoderPad
- **Role**: Senior Software Engineer (L5/E5 equivalent)

---

## Transcript

### Part 1: Warm-up (5 min)

**Interviewer:** Welcome. Let's start with a warm-up. Can you implement a basic trie with insert, search, and startsWith methods?

**Candidate:** Sure. I'll create a TrieNode class with a 26-element array for lowercase letters and a boolean for end-of-word. The trie itself will have a root node and delegate to it.

```java
class TrieNode {
    TrieNode[] children = new TrieNode[26];
    boolean isEnd;
}

class Trie {
    TrieNode root = new TrieNode();

    void insert(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            int idx = c - 'a';
            if (node.children[idx] == null)
                node.children[idx] = new TrieNode();
            node = node.children[idx];
        }
        node.isEnd = true;
    }

    boolean search(String word) {
        TrieNode node = traverse(word);
        return node != null && node.isEnd;
    }

    boolean startsWith(String prefix) {
        return traverse(prefix) != null;
    }

    private TrieNode traverse(String s) {
        TrieNode node = root;
        for (char c : s.toCharArray()) {
            int idx = c - 'a';
            if (node.children[idx] == null) return null;
            node = node.children[idx];
        }
        return node;
    }
}
```

**Interviewer:** Good. What's the time complexity?

**Candidate:** Insert and search are both O(L) where L is the string length. startsWith is also O(L). Space is O(N·L·26) worst-case where N is the number of words and there's no shared prefix.

**Interviewer:** How would you handle uppercase or Unicode characters?

**Candidate:** I'd either normalise to lowercase, or use a HashMap<Character, TrieNode> instead of a fixed array. For Unicode, I'd use HashMap<Integer, TrieNode> where the key is the code point, enabling any character including emoji.

---

### Part 2: Core Problem — Word Search II (25 min)

**Interviewer:** Let's move to the main problem. You're given a 2D board of letters and a dictionary of words. Find all dictionary words that exist on the board by traversing adjacent cells (up, down, left, right). No cell reuse per word.

**Candidate:** I see this is a classic graph search problem with a string constraint. Let me first understand: what are the constraints on board size and dictionary size?

**Interviewer:** Board up to 12×12, dictionary up to 30,000 words, each word up to 10 characters.

**Candidate:** With 30K words, a naive approach of checking each word separately would be 30K × 4^10 ≈ 3×10^13 operations — far too slow. The key insight is that many words share prefixes, so we can use a trie to prune our search.

**Approach:**
1. Build a trie from all dictionary words
2. DFS from each cell on the board
3. At each step, follow the board character in the trie
4. When we hit a trie node representing a complete word, record it
5. Mark visited cells to prevent reuse

**Interviewer:** Walk me through the trie build. What optimisation did you add?

**Candidate:** Instead of a separate isEndOfWord flag, I store the complete word string at each terminal node. This way, when we find a word during DFS, we can immediately add it to results without reconstructing the string from the path.

```java
class TrieNode {
    TrieNode[] children = new TrieNode[26];
    String word;  // null for non-terminal nodes
}
```

**Interviewer:** Good. Now implement the full solution.

**Candidate:**

```java
public List<String> findWords(char[][] board, String[] words) {
    // Build trie
    TrieNode root = new TrieNode();
    for (String w : words) {
        TrieNode node = root;
        for (char c : w.toCharArray()) {
            int idx = c - 'a';
            if (node.children[idx] == null)
                node.children[idx] = new TrieNode();
            node = node.children[idx];
        }
        node.word = w;
    }

    List<String> result = new ArrayList<>();
    int m = board.length, n = board[0].length;

    for (int i = 0; i < m; i++) {
        for (int j = 0; j < n; j++) {
            if (root.children[board[i][j] - 'a'] != null)
                dfs(board, i, j, root, result);
        }
    }
    return result;
}

void dfs(char[][] board, int i, int j, TrieNode node, List<String> result) {
    char c = board[i][j];
    TrieNode child = node.children[c - 'a'];
    if (child == null) return;

    if (child.word != null) {
        result.add(child.word);
        child.word = null; // avoid duplicates
    }

    board[i][j] = '#';

    int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
    for (int[] d : dirs) {
        int ni = i + d[0], nj = j + d[1];
        if (ni >= 0 && ni < board.length &&
            nj >= 0 && nj < board[0].length &&
            board[ni][nj] != '#') {
            dfs(board, ni, nj, child, result);
        }
    }

    board[i][j] = c; // restore
}
```

**Interviewer:** Walk me through the DFS with an example.

**Candidate:** Let's trace for board `[['o','a'],['a','t']]` and words `["oat","oath"]`. The trie has words "oat" and "oath":

```
Root → 'o' → 'a' → 't' (word: "oat", prefixCount=2)
                       → 'h' (word: "oath")
```

DFS starts at (0,0)='o':
- child = root.children['o'-'a'] exists
- board[0][0] = '#'
- Check neighbors: (0,1)='a', (1,0)='a'
- DFS (0,1)='a': child = 'o'→'a' node
  - board[0][1] = '#'
  - Check neighbors: (0,2) out, (0,0) visited, (1,1)='t'
  - DFS (1,1)='t': child = 'a'→'t' node, child.word = "oat"
    - Add "oat", set child.word = null
    - board[1][1] = '#'
    - Check neighbors: (0,1) visited, (1,0)='a'
    - DFS (1,0)='a': child = 't'→'a'? No, 'a' isn't a child of 't'. Return
    - Restore board[1][1] = 't'
  - Restore board[0][1] = 'a'
- DFS (1,0)='a': same path, since "oat" was already found, child.word is null. Continue.
- Restore board[0][0] = 'o'

Result: ["oat"]. "oath" never found because "t"→"h" path requires board cell after "t" which doesn't exist.

**Interviewer:** What's the complexity?

**Candidate:** Time is O(N·M·4^L) where L=10 max word length. The trie pruning means in practice it's much faster — we only follow paths that lead to dictionary words. Space is O(W·L·26) for the trie plus O(L) recursion stack.

**Interviewer:** How would you handle duplicate words in the result?

**Candidate:** I set `child.word = null` after adding to the result. This naturally deduplicates — subsequent DFS paths hitting the same node won't re-add.

**Interviewer:** What if a word appears multiple times on the board?

**Candidate:** Same answer. The trie node only stores one word reference, and we null it on first find. So we return the word once even if it appears in multiple locations.

---

### Part 3: Follow-up (10 min)

**Interviewer:** What if the board is 1000×1000 with sparse dictionary?

**Candidate:** The O(4^L) DFS becomes the bottleneck. Several optimisations:
1. **Topological pruning**: If current depth + remaining word length > board cells, return
2. **Iterative DFS**: Avoid stack overflow from deep recursion
3. **Heuristic ordering**: Sort starting cells by frequency — start from rarest letters first (prunes early)
4. **Parallelise**: Partition board into overlapping regions, run independent searches on different threads

**Interviewer:** How would you handle a streaming dictionary (words added over time)?

**Candidate:** Use a trie that supports dynamic insertion. For concurrent reads during writes, use a copy-on-write approach: build a new trie from the old trie plus new word, then atomically swap the root reference. This gives lock-free reads.

Alternatively, use a lock-free trie with CAS operations on the children array using `AtomicReferenceArray`.

**Interviewer:** How would you extend to support fuzzy matching (one character mismatch)?

**Candidate:** Modify DFS to track remaining edits. The search function takes a `remainingEdits` parameter:
- Current char matches: recurse with same edits
- Current char doesn't match: if remainingEdits > 0, recurse through all children with remainingEdits - 1
- Skip character (insertion in pattern): recurse on same pattern index, next trie node
- This turns the problem into Levenshtein-automaton traversal with bounded edits.

---

### Part 4: System Design Tie-in (5 min)

**Interviewer:** How would you design a real-time autocomplete system for a search engine?

**Candidate:** I'd use a tiered architecture:
1. **Bloom filter per shard**: Quick check if a prefix has any results (avoids unnecessary trie lookups)
2. **Trie with top-k cache**: Each trie node stores precomputed top 5 suggestions by frequency
3. **Redis cache**: Hot prefixes (top 1% of traffic) served directly from Redis
4. **Background rebuild**: Hadoop/Spark job processes query logs every 15 min, builds new trie
5. **Rolling deploy**: New trie serialised and pushed to servers, atomically swapped

The trie is the core data structure because it supports O(L) prefix lookup and can return top-k suggestions in O(L + k) time.

**Interviewer:** Good. We're out of time. Any questions for me?

**Candidate:** Yes — how does your team currently handle search suggestions, and what's the latency budget?

**[Interview continues with candidate questions]**

---

## Debrief

### What Went Well
- Clean trie implementation with minimal bugs
- Identified the trie optimisation for LC 212 without prompting
- Handled deduplication with `word = null` trick
- Clear complexity analysis
- Good follow-up handling: streaming, fuzzy matching, system design

### Areas for Growth
- Could have mentioned `prefixCount` for delete optimisation (warm-up)
- DFS explanation could be tighter — used whiteboard analogy
- System design tie-in could include more numbers (QPS, memory)

### Score
| Category | Score (1-5) |
|----------|-------------|
| Problem Understanding | 5 |
| Data Structure Choice | 5 |
| Code Quality | 4 |
| Complexity Analysis | 5 |
| Follow-up Handling | 4 |
| Communication | 4 |
| **Overall** | **4.5 / 5** |