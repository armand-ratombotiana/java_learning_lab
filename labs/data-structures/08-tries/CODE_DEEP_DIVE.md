# Code Deep Dive: Trie Applications

## Word Search in Grid (LeetCode 212)

```java
public List<String> findWords(char[][] board, String[] words) {
    Trie trie = new Trie();
    for (String word : words) trie.insert(word);

    int m = board.length, n = board[0].length;
    List<String> result = new ArrayList<>();
    for (int i = 0; i < m; i++) {
        for (int j = 0; j < n; j++) {
            dfs(board, i, j, trie.root, new StringBuilder(), result);
        }
    }
    return result;
}

private void dfs(char[][] board, int i, int j, TrieNode node,
        StringBuilder sb, List<String> result) {
    if (i < 0 || i >= board.length || j < 0 || j >= board[0].length) return;
    char ch = board[i][j];
    if (ch == '#' || !node.children.containsKey(ch)) return;

    TrieNode child = node.children.get(ch);
    sb.append(ch);
    if (child.isEndOfWord) {
        result.add(sb.toString());
        child.isEndOfWord = false;  // deduplicate
    }

    board[i][j] = '#';  // mark visited
    dfs(board, i+1, j, child, sb, result);
    dfs(board, i-1, j, child, sb, result);
    dfs(board, i, j+1, child, sb, result);
    dfs(board, i, j-1, child, sb, result);
    board[i][j] = ch;   // restore

    sb.deleteCharAt(sb.length() - 1);
}
```

## Longest Common Prefix

```java
public String longestCommonPrefix(String[] strs) {
    Trie trie = new Trie();
    for (String str : strs) trie.insert(str);

    StringBuilder prefix = new StringBuilder();
    TrieNode current = trie.root;
    while (current.children.size() == 1 && !current.isEndOfWord) {
        Map.Entry<Character, TrieNode> entry =
            current.children.entrySet().iterator().next();
        prefix.append(entry.getKey());
        current = entry.getValue();
    }
    return prefix.toString();
}
```

## Word Break (Dynamic Programming + Trie)

```java
public boolean wordBreak(String s, List<String> wordDict) {
    Trie trie = new Trie();
    for (String word : wordDict) trie.insert(word);

    int n = s.length();
    boolean[] dp = new boolean[n + 1];
    dp[0] = true;

    for (int i = 0; i < n; i++) {
        if (!dp[i]) continue;
        TrieNode current = trie.root;
        for (int j = i; j < n; j++) {
            char ch = s.charAt(j);
            current = current.children.get(ch);
            if (current == null) break;
            if (current.isEndOfWord) dp[j + 1] = true;
        }
    }
    return dp[n];
}
```

## Map Sum Pairs (Sum of Values for Prefix)

```java
class MapSum {
    private TrieNode root;

    public void insert(String key, int val) {
        TrieNode current = root;
        for (char ch : key.toCharArray()) {
            current = current.children.computeIfAbsent(ch, c -> new TrieNode());
        }
        current.value = val;
    }

    public int sum(String prefix) {
        TrieNode node = searchPrefix(prefix);
        if (node == null) return 0;
        return sumValues(node);
    }

    private int sumValues(TrieNode node) {
        int sum = node.value;
        for (TrieNode child : node.children.values()) {
            sum += sumValues(child);
        }
        return sum;
    }
}
```
