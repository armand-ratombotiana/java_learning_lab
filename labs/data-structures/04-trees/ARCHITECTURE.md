# Architecture: Trees in System Design

## Usage Patterns

### Expression Trees

```
        ┌── * ──┐
        │        │
     ┌─ + ─┐  ┌─ 3
     │     │  │
     2     5
```

Compilers parse source code into an **Abstract Syntax Tree (AST)**. Each node is an operator or operand. Tree traversal generates bytecode or machine code.

### Decision Trees (ML)

```java
class DecisionNode {
    int featureIndex;
    double threshold;
    DecisionNode left;   // feature <= threshold
    DecisionNode right;  // feature > threshold
    Double prediction;   // leaf: predicted value
}
```

Random forests are an ensemble of decision trees.

### Game Trees (Minimax)

```
        ┌─── Max ───┐
        │            │
    ┌─ Min ─┐    ┌─ Min ─┐
    │       │    │       │
    3       5    2       9
```

Minimax evaluates game states by alternating between maximizing and minimizing players.

## Database Index Architecture

### B-Tree Index

```
Root (internal):
┌─────┬─────┬─────┐
│ 100 │ 200 │     │
├──┬──┼──┬──┼──┬──┤
│  │  │  │  │  │  │
└──┴──┴──┴──┴──┴──┘
  ↓    ↓    ↓    ↓
Leaf (page 1):  Leaf (page 2):  Leaf (page 3):
[1, 10, 20...]  [101, 110...]  [201, 210...]
```

B-trees minimize disk I/O by having high branching factors (typically hundreds of keys per node).

## File System Hierarchy

Unix file systems use a tree structure:
- `/` is the root directory
- Directories are internal nodes containing entries
- Regular files are leaf nodes
- Hard links create additional parent references (DAG, not pure tree)

## HTML DOM Tree

```html
<html>
  └── <body>
        ├── <div>
        │   ├── <h1>
        │   └── <p>
        └── <footer>
```

JavaScript DOM APIs traverse this tree. React's virtual DOM is also a tree, enabling efficient diffing.

## Java Collections Architecture

- `TreeMap` / `TreeSet`: Red-Black tree implementation
- `PriorityQueue`: array-backed binary heap (complete binary tree)
- `HashMap`: bucket array; each bucket may be a tree (Java 8+)
- `TreeMap` is used for sorted maps and navigable maps
