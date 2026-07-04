# Refactoring Tries

## Replace Map with Array (Fixed Alphabet)

```java
// Before — HashMap (any character)
Map<Character, TrieNode> children = new HashMap<>();

// After — array (lowercase English)
TrieNode[] children = new TrieNode[26];
int index = ch - 'a';
```

## Extract Trie Node as Record (Java 16+)

```java
// Before
static class TrieNode {
    Map<Character, TrieNode> children = new HashMap<>();
    boolean isEndOfWord = false;
}

// After (if no mutations needed after construction)
// Records don't work well here due to mutability
// Better to keep as class
```

## Use DFS Helper for Traversal

```java
// Before — repeated DFS logic
void findWords(TrieNode node, StringBuilder sb, List<String> results) {
    if (node.isEndOfWord) results.add(sb.toString());
    for (char c : node.children.keySet()) {
        sb.append(c);
        findWords(node.children.get(c), sb, results);
        sb.deleteCharAt(sb.length()-1);
    }
}

// After — reusable collector
interface WordCollector {
    void accept(String word);
}

void traverse(TrieNode node, StringBuilder prefix, WordCollector collector) {
    if (node.isEndOfWord) collector.accept(prefix.toString());
    node.children.forEach((ch, child) -> {
        prefix.append(ch);
        traverse(child, prefix, collector);
        prefix.deleteCharAt(prefix.length()-1);
    });
}
```

## Use Streams for Autocomplete

```java
// Before — manual list building
List<String> results = new ArrayList<>();
dfs(node, prefix, results);

// After — stream-based
Stream<String> collect(TrieNode node, String prefix) {
    Stream<String> self = node.isEndOfWord ? Stream.of(prefix) : Stream.empty();
    Stream<String> children = node.children.entrySet().stream()
        .flatMap(e -> collect(e.getValue(), prefix + e.getKey()));
    return Stream.concat(self, children);
}
```

## Separate Trie Traversal from Logic

```java
// Before — mixed
void autocomplete(String prefix) {
    TrieNode node = searchPrefix(prefix);
    List<String> results = new ArrayList<>();
    collectAll(node, new StringBuilder(prefix), results);
    return results;
}

// After — visitor pattern
interface TrieVisitor {
    void visit(String word);
}

void traverse(String prefix, TrieVisitor visitor) {
    TrieNode node = searchPrefix(prefix);
    if (node != null) traverse(node, new StringBuilder(prefix), visitor);
}
```
