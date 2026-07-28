# LeetCode Solution: Expression Tree with Sealed Classes

**Problem:** [1628. Design an Expression Tree with Evaluate Function](https://leetcode.com/problems/design-an-expression-tree-with-evaluate-function/)

Uses a sealed hierarchy to model a type-safe expression tree.

## Approach

Model each node type as a permitted subclass of a sealed `Node`. Use an exhaustive switch to evaluate.

## Java 21 Solution

```java
import java.util.*;

sealed interface Node {
    int evaluate();
}

record NumberNode(int value) implements Node {
    public int evaluate() { return value; }
}

record AddNode(Node left, Node right) implements Node {
    public int evaluate() { return left.evaluate() + right.evaluate(); }
}

record SubtractNode(Node left, Node right) implements Node {
    public int evaluate() { return left.evaluate() - right.evaluate(); }
}

record MultiplyNode(Node left, Node right) implements Node {
    public int evaluate() { return left.evaluate() * right.evaluate(); }
}

record DivideNode(Node left, Node right) implements Node {
    public int evaluate() { return left.evaluate() / right.evaluate(); }
}

class TreeBuilder {
    Node buildTree(String[] postfix) {
        Deque<Node> stack = new ArrayDeque<>();
        for (String token : postfix) {
            switch (token) {
                case "+" -> { Node r = stack.pop(); Node l = stack.pop(); stack.push(new AddNode(l, r)); }
                case "-" -> { Node r = stack.pop(); Node l = stack.pop(); stack.push(new SubtractNode(l, r)); }
                case "*" -> { Node r = stack.pop(); Node l = stack.pop(); stack.push(new MultiplyNode(l, r)); }
                case "/" -> { Node r = stack.pop(); Node l = stack.pop(); stack.push(new DivideNode(l, r)); }
                default  -> stack.push(new NumberNode(Integer.parseInt(token)));
            }
        }
        return stack.pop();
    }
}
```

## Key Takeaway

Sealed interfaces make the AST **exhaustive and type-safe** — adding a new operator requires a new permitted subclass, and the compiler reminds you to update switch expressions.
