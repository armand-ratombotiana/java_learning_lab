# LeetCode Solution: Evaluate Boolean Expression Tree (Pattern Matching)

**Problem:** [2331. Evaluate Boolean Binary Tree](https://leetcode.com/problems/evaluate-boolean-binary-tree/)

Uses record patterns to evaluate a full binary tree where leaf nodes are values and internal nodes are boolean operators.

## Approach

Model the tree nodes as records, use a sealed interface, and evaluate with a nested record pattern match.

## Java 21 Solution

```java
sealed interface BoolNode {}
record ValueNode(boolean val) implements BoolNode {}
record OrNode(BoolNode left, BoolNode right) implements BoolNode {}
record AndNode(BoolNode left, BoolNode right) implements BoolNode {}

class Solution {
    public boolean evaluateTree(BoolNode root) {
        return switch (root) {
            case ValueNode(boolean v) -> v;
            case OrNode(BoolNode l, BoolNode r) ->
                evaluateTree(l) || evaluateTree(r);
            case AndNode(BoolNode l, BoolNode r) ->
                evaluateTree(l) && evaluateTree(r);
        };
    }
}
```

## Traditional Recursive Solution (without pattern matching)

```java
class Solution {
    public boolean evaluateTree(TreeNode root) {
        return switch (root.val) {
            case 0 -> false;  // leaf false
            case 1 -> true;   // leaf true
            case 2 -> evaluateTree(root.left) || evaluateTree(root.right); // OR
            case 3 -> evaluateTree(root.left) && evaluateTree(root.right); // AND
            default -> throw new IllegalStateException();
        };
    }
}
```

## Key Takeaway

Record patterns + sealed types make the tree evaluation **declarative and obviously correct** — the structure of the code mirrors the structure of the data.
