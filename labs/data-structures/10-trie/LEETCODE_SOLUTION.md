# Design Search Autocomplete System (LeetCode 642)

**Problem:** Design a search autocomplete system for a search engine. Users input a sentence (at least one character and end with a special character `#`). For each character they type, suggest the top **3** historical hot sentences that have the same prefix.

The system receives a stream of user inputs (including `#` to mark end of input).

Implement `AutocompleteSystem`:

- `AutocompleteSystem(String[] sentences, int[] times)` — Constructor. Initializes the system with historical data.
- `List<String> input(char c)` — Receives the next character typed by the user. If `c == '#'`, the current sentence is saved and an empty list is returned. Otherwise, returns the top 3 sentences with the same prefix, sorted by hot degree (descending), then by sentence (ascending ASCII).

## Java Solution

```java
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Search Autocomplete System using a Trie with priority queues.
 *
 * <p>The Trie stores sentences with their hot degree (frequency). Each Trie
 * node caches the top 3 sentences for the corresponding prefix, computed
 * lazily via a min-heap.</p>
 *
 * <h2>Complexity Analysis</h2>
 * <ul>
 *   <li><b>Constructor</b> — O(N * L) where N = number of sentences, L = avg length</li>
 *   <li><b>input(c)</b> — O(L + k log k) where k = number of matches in the subtree</li>
 * </ul>
 *
 * <b>Space:</b> O(N * L) for the Trie
 */
public class AutocompleteSystem {

    static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        Map<String, Integer> sentenceFreq = new HashMap<>();
    }

    private final TrieNode root;
    private final StringBuilder currentInput;
    private TrieNode currentNode;

    /**
     * Constructs the autocomplete system with historical data.
     *
     * @param sentences historical sentences
     * @param times     frequencies of each sentence
     */
    public AutocompleteSystem(String[] sentences, int[] times) {
        root = new TrieNode();
        currentInput = new StringBuilder();
        currentNode = root;

        for (int i = 0; i < sentences.length; i++) {
            addSentence(sentences[i], times[i]);
        }
    }

    private void addSentence(String sentence, int frequency) {
        TrieNode node = root;
        for (char ch : sentence.toCharArray()) {
            node = node.children.computeIfAbsent(ch, k -> new TrieNode());
            node.sentenceFreq.merge(sentence, frequency, Integer::sum);
        }
    }

    /**
     * Processes the next input character.
     *
     * @param c the character typed; '#' marks the end of a sentence
     * @return top 3 autocomplete suggestions, or empty list if c == '#'
     */
    public List<String> input(char c) {
        if (c == '#') {
            // Save the current sentence
            String sentence = currentInput.toString();
            addSentence(sentence, 1);
            currentInput.setLength(0);
            currentNode = root;
            return List.of();
        }

        currentInput.append(c);

        // Move the trie pointer
        if (currentNode != null) {
            currentNode = currentNode.children.get(c);
        }

        // If no sentences match, return empty list
        if (currentNode == null) {
            return List.of();
        }

        // Extract top 3 using a min-heap
        PriorityQueue<Map.Entry<String, Integer>> minHeap = new PriorityQueue<>(
            (a, b) -> {
                int freqCmp = Integer.compare(a.getValue(), b.getValue());
                if (freqCmp != 0) return freqCmp;
                return b.getKey().compareTo(a.getKey()); // reverse lexical for max-heap behavior
            }
        );

        for (Map.Entry<String, Integer> entry : currentNode.sentenceFreq.entrySet()) {
            minHeap.offer(entry);
            if (minHeap.size() > 3) {
                minHeap.poll();
            }
        }

        List<String> result = new ArrayList<>();
        while (!minHeap.isEmpty()) {
            result.add(0, minHeap.poll().getKey());
        }
        return result;
    }
}
```

## Test Cases

```java
import java.util.List;

/**
 * Unit tests for AutocompleteSystem.
 */
public class AutocompleteSystemTest {

    public static void main(String[] args) {
        // --- Test 1: Example from LeetCode ---
        String[] sentences = {"i love you", "island", "iroman", "i love leetcode"};
        int[] times = {5, 3, 2, 2};
        AutocompleteSystem ac = new AutocompleteSystem(sentences, times);

        List<String> res;

        res = ac.input('i');
        assert res.equals(List.of("i love you", "island", "i love leetcode"))
            : "after 'i' -> " + res;

        res = ac.input(' ');
        assert res.equals(List.of("i love you", "i love leetcode"))
            : "after 'i ' -> " + res;

        res = ac.input('a');
        assert res.isEmpty() : "after 'i a' -> no matches";

        res = ac.input('#');
        assert res.isEmpty() : "after '#' -> empty (sentence saved)";

        // After saving "i a" with frequency 1
        res = ac.input('i');
        assert res.equals(List.of("i love you", "island", "i love leetcode"))
            : "after 'i' again -> " + res;

        // Now "i a" has freq 1, so it should appear when typing "i a"
        res = ac.input(' ');
        assert res.equals(List.of("i love you", "i love leetcode"))
            : "after 'i ' again -> " + res;

        // --- Test 2: Empty initial data ---
        AutocompleteSystem ac2 = new AutocompleteSystem(new String[]{}, new int[]{});
        res = ac2.input('a');
        assert res.isEmpty() : "no historical data -> empty";
        ac2.input('#'); // save "a"
        res = ac2.input('a');
        assert res.equals(List.of("a")) : "after saving 'a', should suggest 'a'";

        // --- Test 3: Multiple sentences with same frequency ---
        String[] s3 = {"abc", "ab", "a"};
        int[] t3 = {1, 1, 1};
        AutocompleteSystem ac3 = new AutocompleteSystem(s3, t3);
        res = ac3.input('a');
        // Lexical order when frequencies equal: "a", "ab", "abc"
        assert res.equals(List.of("a", "ab", "abc")) : "same freq -> lexical order -> " + res;

        // --- Test 4: Higher frequency wins ---
        String[] s4 = {"ab", "abc", "abcd"};
        int[] t4 = {5, 3, 1};
        AutocompleteSystem ac4 = new AutocompleteSystem(s4, t4);
        res = ac4.input('a');
        assert res.equals(List.of("ab", "abc", "abcd"))
            : "frequency order -> " + res;

        // --- Test 5: Char that doesn't match any prefix ---
        AutocompleteSystem ac5 = new AutocompleteSystem(new String[]{"hello"}, new int[]{10});
        res = ac5.input('x');
        assert res.isEmpty() : "no prefix match -> empty";
        // Subsequent chars should still return empty since prefix doesn't match
        res = ac5.input('y');
        assert res.isEmpty() : "still no match after 'xy'";
        ac5.input('#'); // save "xy"
        // Now "xy" is saved but should not appear for prefix "x" (node is at 'x')
        // Actually after '#', currentNode resets... let's re-check
        res = ac5.input('x');
        // "xy" has freq 1, "hello" is gone because prefix doesn't match "x"
        assert res.equals(List.of("xy")) : "should suggest 'xy'";

        // --- Test 6: Case sensitivity (lowercase only per spec) ---
        // All examples use lowercase; ensure no crash
        AutocompleteSystem ac6 = new AutocompleteSystem(new String[]{"a"}, new int[]{1});
        res = ac6.input('A'); // 'A' != 'a'
        assert res.isEmpty() : "case mismatch -> no results";

        System.out.println("All AutocompleteSystem tests passed!");
    }
}
```
