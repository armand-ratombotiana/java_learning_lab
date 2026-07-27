# Problem Walkthrough: 08-Tries

## Problem 1: Implement Trie (Prefix Tree) (LC 208) — Google

### Interview Scenario
"Google interviewer: 'Implement a trie with insert, search, and startsWith methods.'"

### The Problem
A trie (prefix tree) stores strings. Search checks exact match. startsWith checks if any word starts with the given prefix.

### Step 1: Clarify (30 seconds)
- **Q:** Characters allowed? **A:** Lowercase English letters (a-z).
- **Q:** Empty string insert? **A:** Yes, treating the root node as a word end.
- **Q:** Duplicate insert? **A:** No duplicates guaranteed.
- **Q:** Case sensitivity? **A:** All lowercase.
- **Edge cases:** Empty string, single character, long strings, prefix longer than any word, searching for a word that is a prefix of another.

### Step 2: Brute Force (2 min)
- Store words in a HashSet. search is O(1). startsWith requires iterating all words and checking prefix — O(n * k).
- **Time:** O(n * k) for startsWith — not acceptable for prefix-heavy workloads.

### Step 3: Optimize (5 min)
- "Build a trie: each node has an array of 26 child references and a boolean flag for word end. Insert by traversing character by character, creating nodes as needed. Search and startsWith traverse similarly — search checks the flag, startsWith checks that the path exists."
- insert: O(k) where k is word length. search: O(k). startsWith: O(k). Space: O(total characters).
- **Why Google values this:** The trie is a Google-scale data structure. Used in search autocomplete, spell check, IP routing (longest prefix match), and DNA sequence matching.

### Step 4: Code (10 min)

```java
/**
 * Trie (prefix tree) for lowercase English letters.
 * <p>
 * Insert: O(k) | Search: O(k) | StartsWith: O(k) | Space: O(n * k)
 */
public class Trie {
    private class TrieNode {
        TrieNode[] children = new TrieNode[26];
        boolean isEnd;
    }

    private TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    public void insert(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            int idx = c - 'a';
            if (node.children[idx] == null) {
                node.children[idx] = new TrieNode();
            }
            node = node.children[idx];
        }
        node.isEnd = true;
    }

    public boolean search(String word) {
        TrieNode node = traverse(word);
        return node != null && node.isEnd;
    }

    public boolean startsWith(String prefix) {
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

### Step 5: Test (3 min)
- insert("apple"), search("apple") → true, search("app") → false, startsWith("app") → true
- insert("app"), search("app") → true
- **Edge:** search("") → depends on implementation (should handle empty string)
- **Edge:** startsWith("z") → false when no 'z' words exist
- Walk through the trie state after each insert.

### Step 6: Follow-ups
- "Delete a word?" — Traverse to node, set isEnd = false. Optionally prune nodes with no children.
- "Count words with a given prefix?" — Store count at each node during insert.
- "Word with longest common prefix?" — BFS/navigate to find deepest node with isEnd.
- "Autocomplete suggestions?" — DFS from the prefix node, collect all words.
- **What Google looks for:** Can they extend the trie to real applications? Do they understand the pointer/reference overhead?

### Company Evaluation Criteria
- **Google:** Clean implementation and understanding of tree traversal. They'll push on the delete operation and memory analysis.
- **Amazon:** Would ask about radix tree (PATRICIA trie) for memory optimization.
- **Meta:** Would ask about building a trie from a dictionary file.

---

## Problem 2: Word Search II (LC 212) — Amazon

### Interview Scenario
"Amazon interviewer: 'Given an m x n board of letters and a list of words, find all words on the board. A word can be formed by adjacent letters (horizontal/vertical, can't reuse the same cell).'"

### The Problem
Find all dictionary words that exist on the board as paths.

### Step 1: Clarify (30 seconds)
- **Q:** Can revisit cells? **A:** No, each cell can be used at most once per word.
- **Q:** Diagonal adjacency? **A:** No, only horizontal and vertical.
- **Q:** All lowercase? **A:** Yes, lowercase English.
- **Q:** Duplicate words in list? **A:** No.
- **Edge cases:** Empty board, empty word list, 1x1 board, no matches, all words on board, overlapping words.

### Step 2: Brute Force (2 min)
- For each word, DFS on the board to see if it exists (like Word Search I). O(n * m * 4^k) per word.
- **Time:** O(w * m * n * 4^k) — prohibitively slow for many words.

### Step 3: Optimize (5 min)
- "Build a trie of all words. Then DFS on the board. At each step, if the current path forms a prefix in the trie, continue; if it forms a complete word, add to result. Mark visited cells to avoid reuse."
- O(m * n * 4^k) total — the trie prunes invalid paths for all words simultaneously.
- Why prune works: when no word starts with "zx", we don't explore further after "zx".
- **Why Amazon values this:** Trie + backtracking is used for autocomplete, search suggestions, and product lookups in Amazon's search bar.

### Step 4: Code (10 min)

```java
import java.util.ArrayList;
import java.util.List;

/**
 * Finds all words from the dictionary that exist on the board.
 * <p>
 * Time: O(m * n * 4^k) | Space: O(total chars in words)
 */
public class Solution {
    private class TrieNode {
        TrieNode[] children = new TrieNode[26];
        String word;
    }

    public List<String> findWords(char[][] board, String[] words) {
        TrieNode root = buildTrie(words);
        List<String> result = new ArrayList<>();

        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[0].length; c++) {
                dfs(board, r, c, root, result);
            }
        }

        return result;
    }

    private void dfs(char[][] board, int r, int c, TrieNode node, List<String> result) {
        if (r < 0 || r >= board.length || c < 0 || c >= board[0].length) return;

        char ch = board[r][c];
        if (ch == '#' || node.children[ch - 'a'] == null) return;

        node = node.children[ch - 'a'];
        if (node.word != null) {
            result.add(node.word);
            node.word = null; // avoid duplicates
        }

        board[r][c] = '#';
        dfs(board, r - 1, c, node, result);
        dfs(board, r + 1, c, node, result);
        dfs(board, r, c - 1, node, result);
        dfs(board, r, c + 1, node, result);
        board[r][c] = ch;
    }

    private TrieNode buildTrie(String[] words) {
        TrieNode root = new TrieNode();
        for (String w : words) {
            TrieNode node = root;
            for (char c : w.toCharArray()) {
                int idx = c - 'a';
                if (node.children[idx] == null) {
                    node.children[idx] = new TrieNode();
                }
                node = node.children[idx];
            }
            node.word = w;
        }
        return root;
    }
}
```

### Step 5: Test (3 min)
- board = [["o","a","a","n"],["e","t","a","e"],["i","h","k","r"],["i","f","l","v"]], words = ["oath","pea","eat","rain"] → ["eat","oath"]
- **Edge:** board = [["a"]], words = ["a"] → ["a"]
- **Edge:** board = [["a"]], words = ["b"] → []
- **Edge:** board = [["a","a"]], words = ["aa"] → ["aa"]
- Show how the trie prunes the search space at each step.

### Step 6: Follow-ups
- "Can you see the same word appearing multiple times?" — Set node.word = null after finding to deduplicate.
- "What if the board is very large?" — Parallelize starting DFS from multiple cells.
- "What about 8-directional search (including diagonals)?" — Add 4 more direction vectors.
- **What Amazon looks for:** Optimization at scale. How does the trie reduce the search space? Can you quantify the improvement?

### Company Evaluation Criteria
- **Amazon:** Correctness and performance. The trie + backtracking is a complex solution — they want to see it work end-to-end.
- **Google:** Would ask about memory optimization (compressed trie).
- **Meta:** Would ask about Word Search I (single word search) first, then extend to multiple words.

---

## Problem 3: Word Break II (LC 140) — Meta

### Interview Scenario
"Meta interviewer: 'Given a string s and a dictionary of words, add spaces to s to construct a sentence where each word is in the dictionary. Return all possible sentences.'"

### The Problem
Split a string into all possible sequences of valid dictionary words.

### Step 1: Clarify (30 seconds)
- **Q:** Can the same dictionary word be used multiple times? **A:** Yes, unlimited usage.
- **Q:** All lowercase? **A:** Yes.
- **Q:** Can we return sentences in any order? **A:** Yes.
- **Q:** Empty string? **A:** Return empty list.
- **Edge cases:** No possible segmentation, single character, entire string is one word, overlapping solutions, repeated substrings causing exponential blow-up.

### Step 2: Brute Force (2 min)
- Recursively try every possible split point. If prefix is a word, recurse on the suffix.
- **Time:** Exponential O(2^n) — each character can be a split point.
- **Space:** O(n) for recursion depth.

### Step 3: Optimize (5 min)
- "Use a trie for O(k) dictionary lookups instead of O(k²) with HashSet substring. Add memoization (HashMap of index -> List<String>) to cache results for each starting position, avoiding recomputation of overlapping subproblems."
- With trie + memoization, we only visit each starting position once, and the work per position is proportional to the number of words starting at that position.
- **Why Meta values this:** Sentence segmentation is used in Meta's NLP pipelines, chat systems, and search query parsing.

### Step 4: Code (10 min)

```java
import java.util.*;

/**
 * Returns all possible sentences formed by splitting s using dictionary words.
 * Uses a trie for fast prefix lookups and memoization.
 * <p>
 * Time: O(n * k + total sentences) | Space: O(n + total words)
 */
public class Solution {
    private class TrieNode {
        TrieNode[] children = new TrieNode[26];
        boolean isEnd;
    }

    public List<String> wordBreak(String s, List<String> wordDict) {
        TrieNode root = buildTrie(wordDict);
        Map<Integer, List<String>> memo = new HashMap<>();
        return dfs(s, 0, root, memo);
    }

    private List<String> dfs(String s, int start, TrieNode root,
                              Map<Integer, List<String>> memo) {
        if (start == s.length()) {
            List<String> base = new ArrayList<>();
            base.add("");
            return base;
        }

        if (memo.containsKey(start)) return memo.get(start);

        List<String> result = new ArrayList<>();
        TrieNode node = root;

        for (int i = start; i < s.length(); i++) {
            int idx = s.charAt(i) - 'a';
            if (node.children[idx] == null) break;
            node = node.children[idx];

            if (node.isEnd) {
                String word = s.substring(start, i + 1);
                List<String> suffixes = dfs(s, i + 1, root, memo);
                for (String suffix : suffixes) {
                    result.add(word + (suffix.isEmpty() ? "" : " " + suffix));
                }
            }
        }

        memo.put(start, result);
        return result;
    }

    private TrieNode buildTrie(List<String> dict) {
        TrieNode root = new TrieNode();
        for (String w : dict) {
            TrieNode node = root;
            for (char c : w.toCharArray()) {
                int idx = c - 'a';
                if (node.children[idx] == null) {
                    node.children[idx] = new TrieNode();
                }
                node = node.children[idx];
            }
            node.isEnd = true;
        }
        return root;
    }
}
```

### Step 5: Test (3 min)
- s = "catsanddog", dict = ["cat","cats","and","sand","dog"] → ["cats and dog","cat sand dog"]
- **Edge:** s = "a", dict = [] → []
- **Edge:** s = "", dict = ["a"] → []
- **Edge:** s = "pineapplepenapple", dict = ["apple","pen","applepen","pine","pineapple"] → multiple sentences
- Show how memoization prevents repeated computation.

### Step 6: Follow-ups
- "Return only one valid sentence (LC 139, Word Break I)?" — Use DP boolean array or early exit from recursion.
- "What if the dictionary is very large (10⁶ words)?" — Trie works well, memory could be an issue — use compressed trie or prefix hash set.
- "What about case sensitivity?" — Normalize to lowercase.
- **What Meta looks for:** Handling of overlapping subproblems. Do you recognize that simple recursion is exponential and needs memoization?

### Company Evaluation Criteria
- **Meta:** Completeness — can you handle memoization correctly? Do you build sentences efficiently without creating excessive intermediate strings?
- **Google:** Would ask about Word Break I (boolean) and discuss DP vs. recursion trade-offs.
- **Amazon:** Would ask about printing one valid sentence for breaking a document.

---

## Study Notes

### Key Patterns
- **Trie construction:** 26-array children, boolean flag for word end
- **Trie with DFS backtracking:** Word Search II — prune invalid paths using trie
- **Trie with memoization:** Word Break II — cache results at each index
- **Trie for prefix matching:** Autocomplete, spell check, longest prefix
- **Trie for sorting:** Lexicographic ordering of strings (DFS in-order)

### Common Mistakes
- Using HashMap instead of array[26] for children — array is faster for fixed alphabet
- Forgetting to mark visited cells in board search (infinite loop)
- Not setting node.word = null after finding (duplicate results)
- Rebuilding substring objects repeatedly instead of tracking indices
- Forgetting to prune the trie — checking prefix existence before recursing

### Time Complexity Cheat Sheet
| Pattern | Time | Space |
|---|---|---|
| Trie insert/search | O(k) | O(n * k) |
| Word Search II (trie) | O(m * n * 4^k) | O(total chars) |
| Word Break II (trie + memo) | O(n * k + sentences) | O(n + total words) |
| Autocomplete (DFS from prefix) | O(k + results) | O(k) |
| Compressed trie (radix tree) | O(k) | O(n) |
