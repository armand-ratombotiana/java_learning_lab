# Interview Questions on Graph Theory

## Easy

1. Implement BFS and DFS on adjacency list.
2. Detect a cycle in an undirected graph.
3. Find if there is a path between two nodes.

## Medium

4. Clone a graph (deep copy with adjacency).
5. Topological sort (course schedule problem).
6. Detect cycle in a directed graph.
7. Number of connected components in an undirected graph.

## Hard

8. Word ladder II (BFS + backtracking).
9. Find bridges and articulation points (Tarjan's algorithm).
10. Dijkstra's shortest path with path reconstruction.
11. Alien dictionary — topological sort from given word ordering.

## Java: Clone Graph (DFS)

```java
public Node cloneGraph(Node node) {
    if (node == null) return null;
    Map<Node, Node> map = new HashMap<>();
    return clone(node, map);
}

private Node clone(Node node, Map<Node, Node> map) {
    if (map.containsKey(node)) return map.get(node);
    Node copy = new Node(node.val);
    map.put(node, copy);
    for (Node neighbor : node.neighbors)
        copy.neighbors.add(clone(neighbor, map));
    return copy;
}
```

## Java: Word Ladder I

```java
public int ladderLength(String beginWord, String endWord, List<String> wordList) {
    Set<String> dict = new HashSet<>(wordList);
    if (!dict.contains(endWord)) return 0;
    Queue<String> q = new LinkedList<>();
    q.add(beginWord);
    int steps = 1;
    while (!q.isEmpty()) {
        int size = q.size();
        for (int i = 0; i < size; i++) {
            String cur = q.poll();
            if (cur.equals(endWord)) return steps;
            char[] arr = cur.toCharArray();
            for (int j = 0; j < arr.length; j++) {
                char orig = arr[j];
                for (char c = 'a'; c <= 'z'; c++) {
                    arr[j] = c;
                    String next = new String(arr);
                    if (dict.remove(next)) q.add(next);
                }
                arr[j] = orig;
            }
        }
        steps++;
    }
    return 0;
}
```
