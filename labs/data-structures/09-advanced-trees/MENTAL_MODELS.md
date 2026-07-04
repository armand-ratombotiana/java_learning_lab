# Mental Models for Advanced Trees

## AVL Tree: The Tightrope Walker

An AVL tree walks a tightrope (balance). If the walker leans too far in either direction (balance factor ±2), they make a corrective move (rotation) to regain balance. The walker is always nearly upright, ensuring stable, fast movement.

## Red-Black Tree: The Traffic Light

Nodes are either red or black like traffic lights. The rules ensure no red-red collision (like a traffic accident). The black height is like the number of green lights — every path must have the same count. This maintains approximate balance with less strictness than AVL.

## B-Tree: The Office Building

A B-tree is like an office building. Each floor (node) has multiple offices (keys). To find a person, you go to the floor, check which office is closest, and either find them there or take the stairs/elevator (child pointer) to the next floor. Floors are wide (many offices) to minimize the number of floors you need to visit.

## Fenwick Tree: The Binary Staircase

A Fenwick tree is like a staircase where each step covers a range of stairs. Walking down the stairs (decreasing index) accumulates the result. The binary representation of the index determines which steps to take. Each step updates its range.

## Segment Tree: The Conference Room

A segment tree is like a conference hierarchy. A CEO handles the entire company (range). VPs handle divisions (sub-ranges). Managers handle teams. Each query asks "what's happening in this range?" and the hierarchy splits the question until it reaches the relevant managers.

## BST vs AVL: Balance Comparison

```
BST (sorted input):          AVL (same input):
1                            4
 \                          / \
  2                        2   6
   \                      / \ / \
    3                    1  3 5  7
     \
      4
       \
        5
         \
          6
           \
            7
```
