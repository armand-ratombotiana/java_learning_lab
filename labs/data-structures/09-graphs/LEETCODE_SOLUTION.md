# Clone Graph (LeetCode 133)

**Problem:** Given a reference of a node in a **connected undirected graph**, return a **deep copy** (clone) of the graph. Each node in the graph contains a value (`int`) and a list (`List[Node]`) of its neighbors.

Test case format:

- The graph is represented as an adjacency list.
- Adjacency list is a mapping of nodes to their neighbors.
- For simplicity, each node's value is the same as the node's index (1-indexed).

## Java Solution

```java
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;

/**
 * Deep copies a connected undirected graph using BFS traversal and a HashMap
 * to track visited nodes and their clones.
 *
 * <h2>Complexity Analysis</h2>
 * <ul>
 *   <li><b>cloneGraph(node)</b> — O(V + E) time</li>
 *   <li><b>cloneGraphDFS(node)</b> — O(V + E) time</li>
 * </ul>
 *
 * <b>Space:</b> O(V) for the visited map and queue/stack
 */
public class CloneGraph {

    static class Node {
        public int val;
        public List<Node> neighbors;

        public Node() {
            this(0, new ArrayList<>());
        }

        public Node(int val) {
            this(val, new ArrayList<>());
        }

        public Node(int val, List<Node> neighbors) {
            this.val = val;
            this.neighbors = neighbors;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Node other)) return false;
            return val == other.val && neighbors.size() == other.neighbors.size();
        }

        @Override
        public int hashCode() {
            return Objects.hash(val);
        }

        @Override
        public String toString() {
            return "Node(" + val + ")";
        }
    }

    /**
     * Clones a graph using BFS (iterative).
     *
     * @param node the starting node of the graph to clone
     * @return the cloned starting node
     */
    public Node cloneGraph(Node node) {
        if (node == null) return null;

        Map<Node, Node> visited = new HashMap<>();
        Queue<Node> queue = new ArrayDeque<>();

        // Clone the starting node
        Node clone = new Node(node.val);
        visited.put(node, clone);
        queue.offer(node);

        while (!queue.isEmpty()) {
            Node curr = queue.poll();
            Node currClone = visited.get(curr);

            for (Node neighbor : curr.neighbors) {
                Node neighborClone = visited.get(neighbor);
                if (neighborClone == null) {
                    // Not yet cloned — create and enqueue
                    neighborClone = new Node(neighbor.val);
                    visited.put(neighbor, neighborClone);
                    queue.offer(neighbor);
                }
                currClone.neighbors.add(neighborClone);
            }
        }
        return clone;
    }

    /**
     * Clones a graph using DFS (recursive).
     *
     * @param node the starting node of the graph to clone
     * @return the cloned starting node
     */
    public Node cloneGraphDFS(Node node) {
        if (node == null) return null;
        Map<Node, Node> visited = new HashMap<>();
        return dfs(node, visited);
    }

    private Node dfs(Node node, Map<Node, Node> visited) {
        // Return the clone if already visited
        Node existing = visited.get(node);
        if (existing != null) return existing;

        // Clone this node
        Node clone = new Node(node.val);
        visited.put(node, clone);

        // Recursively clone all neighbors
        for (Node neighbor : node.neighbors) {
            clone.neighbors.add(dfs(neighbor, visited));
        }
        return clone;
    }
}
```

## Test Cases

```java
/**
 * Unit tests for CloneGraph.
 */
public class CloneGraphTest {

    public static void main(String[] args) {
        CloneGraph cloner = new CloneGraph();

        // --- Test 1: Null graph ---
        Node result = cloner.cloneGraph(null);
        assert result == null : "null input should return null";

        // --- Test 2: Single node ---
        Node single = new Node(1);
        Node clonedSingle = cloner.cloneGraph(single);
        assert clonedSingle != null : "should not be null";
        assert clonedSingle.val == 1 : "value should be 1";
        assert clonedSingle.neighbors.isEmpty() : "should have no neighbors";
        assert clonedSingle != single : "should be a different object";

        // --- Test 3: Two connected nodes ---
        Node n1 = new Node(1);
        Node n2 = new Node(2);
        n1.neighbors.add(n2);
        n2.neighbors.add(n1);

        Node cloned = cloner.cloneGraph(n1);
        assert cloned.val == 1 : "root value should be 1";
        assert cloned.neighbors.size() == 1 : "should have 1 neighbor";
        assert cloned.neighbors.get(0).val == 2 : "neighbor value should be 2";
        assert cloned.neighbors.get(0) != n2 : "should be a clone, not original";
        assert cloned.neighbors.get(0).neighbors.get(0) == cloned : "should be bidirectional";

        // --- Test 4: Triangle (3 nodes fully connected) ---
        Node a = new Node(1);
        Node b = new Node(2);
        Node c = new Node(3);
        a.neighbors.add(b); a.neighbors.add(c);
        b.neighbors.add(a); b.neighbors.add(c);
        c.neighbors.add(a); c.neighbors.add(b);

        Node clonedTriangle = cloner.cloneGraph(a);
        assert clonedTriangle.val == 1 : "root should be 1";
        assert clonedTriangle.neighbors.size() == 2 : "should have 2 neighbors";
        // Verify connectivity: both neighbors should be connected to each other
        Node clonedB = clonedTriangle.neighbors.get(0);
        Node clonedC = clonedTriangle.neighbors.get(1);
        assert clonedB.neighbors.contains(clonedTriangle) : "B should be connected to root";
        assert clonedB.neighbors.contains(clonedC) : "B should be connected to C";
        assert clonedC.neighbors.contains(clonedTriangle) : "C should be connected to root";
        assert clonedC.neighbors.contains(clonedB) : "C should be connected to B";

        // --- Test 5: DFS version ---
        Node dfsCloned = cloner.cloneGraphDFS(a);
        assert dfsCloned.val == 1 : "DFS clone root should be 1";
        assert dfsCloned != a : "should be a clone";
        assert dfsCloned.neighbors.size() == 2 : "should have 2 neighbors";

        // --- Test 6: Larger graph (star) ---
        Node center = new Node(1);
        java.util.List<Node> leaves = new java.util.ArrayList<>();
        for (int i = 2; i <= 10; i++) {
            Node leaf = new Node(i);
            leaf.neighbors.add(center);
            center.neighbors.add(leaf);
            leaves.add(leaf);
        }
        Node clonedStar = cloner.cloneGraph(center);
        assert clonedStar.neighbors.size() == 9 : "center should have 9 neighbors";
        for (Node leafClone : clonedStar.neighbors) {
            assert leafClone.neighbors.size() == 1 : "leaf should have 1 neighbor";
            assert leafClone.neighbors.get(0) == clonedStar : "leaf neighbor should be center";
        }

        // --- Test 7: Self-loop ---
        Node self = new Node(1);
        self.neighbors.add(self);
        Node clonedSelf = cloner.cloneGraph(self);
        assert clonedSelf.neighbors.size() == 1 : "self-loop should have 1 neighbor";
        assert clonedSelf.neighbors.get(0) == clonedSelf : "neighbor should point to clone";

        System.out.println("All CloneGraph tests passed!");
    }
}
```
