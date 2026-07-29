# LeetCode 127 — Word Ladder

## Problem

A **transformation sequence** from word `beginWord` to `endWord` using a dictionary `wordList` is a sequence where:
- Every adjacent pair differs by exactly one letter.
- Every word (except the first) is in `wordList`.

Return the **length** of the shortest transformation sequence, or `0` if none exists.

**Constraints:**
- `1 <= beginWord.length <= 10`
- `wordList.length <= 5000`

---

## Solution: Bidirectional BFS

BFS from both `beginWord` and `endWord` simultaneously, meeting in the middle. This reduces the branching factor from `b^d` to approximately `2 * b^(d/2)`.

```java
import java.util.*;

/**
 * LeetCode 127 — Word Ladder
 *
 * Bidirectional BFS for optimal shortest-path search.
 *
 * Time: O(M^2 * N) where M = word length, N = word list size
 * Space: O(M * N)
 */
public class WordLadder {

    public int ladderLength(String beginWord, String endWord, List<String> wordList) {
        Set<String> dict = new HashSet<>(wordList);
        if (!dict.contains(endWord)) return 0;

        Set<String> beginSet = new HashSet<>();
        Set<String> endSet = new HashSet<>();
        beginSet.add(beginWord);
        endSet.add(endWord);

        // Remove start/end from dict to avoid revisiting
        dict.remove(beginWord);
        dict.remove(endWord);

        int steps = 1;

        while (!beginSet.isEmpty() && !endSet.isEmpty()) {
            // Always expand the smaller set for efficiency
            if (beginSet.size() > endSet.size()) {
                Set<String> tmp = beginSet;
                beginSet = endSet;
                endSet = tmp;
            }

            Set<String> nextSet = new HashSet<>();

            for (String word : beginSet) {
                char[] chars = word.toCharArray();
                for (int i = 0; i < chars.length; i++) {
                    char original = chars[i];
                    for (char c = 'a'; c <= 'z'; c++) {
                        if (c == original) continue;
                        chars[i] = c;
                        String transformed = new String(chars);

                        if (endSet.contains(transformed)) {
                            return steps + 1;
                        }

                        if (dict.contains(transformed)) {
                            nextSet.add(transformed);
                            dict.remove(transformed);
                        }
                    }
                    chars[i] = original;
                }
            }

            beginSet = nextSet;
            steps++;
        }

        return 0;
    }

    public static void main(String[] args) {
        WordLadder s = new WordLadder();

        // Test 1: Standard case
        List<String> dict1 = Arrays.asList("hot","dot","dog","lot","log","cog");
        int r1 = s.ladderLength("hit", "cog", dict1);
        System.out.println("Test 1: " + r1 + " (expected: 5)");

        // Test 2: No path
        List<String> dict2 = Arrays.asList("hot","dot","dog","lot","log");
        int r2 = s.ladderLength("hit", "cog", dict2);
        System.out.println("Test 2: " + r2 + " (expected: 0)");

        // Test 3: Direct transformation
        List<String> dict3 = Arrays.asList("a","b","c");
        int r3 = s.ladderLength("a", "c", dict3);
        System.out.println("Test 3: " + r3 + " (expected: 2)");

        // Test 4: Single step
        List<String> dict4 = Arrays.asList("hot");
        int r4 = s.ladderLength("hot", "hot", dict4);
        System.out.println("Test 4: " + r4 + " (expected: 1)");
    }
}
```

---

## Complexity Analysis

| Aspect | Value |
|--------|-------|
| Time Complexity | O(M^2 * N) — For each word of length M, we try 26 * M transformations, each costing O(M) to build the string |
| Space Complexity | O(M * N) — The dictionary and BFS sets hold up to N words of length M |

### Why Bidirectional BFS?

- Standard BFS from `beginWord` expands `26 * M` neighbors per level
- If shortest path has depth `d`, total explored ≈ `(26*M)^d`
- Bidirectional BFS explores ≈ `2 * (26*M)^(d/2)`, a massive reduction
- The smaller-set-expansion heuristic further optimizes memory and time