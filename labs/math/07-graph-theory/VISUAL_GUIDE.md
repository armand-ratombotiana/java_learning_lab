# Visual Guide to Graph Theory

## Simple Graph

```
   A ───── B
   | \     |
   |   \   |
   |     \ |
   C ───── D
```

## Directed Graph

```
   A ──→ B
   ↑     ↓
   D ←── C
```

## Tree

```
       A
      / \
     B   C
    / \   \
   D   E   F
```

## Bipartite Graph

```
  A ─── 1
  B ─── 2
  C ─── 3
  D ─── 4
```

Edges only between left and right side.

## BFS vs DFS Traversal

```
BFS (level-order):    DFS (depth-first):
     A                    A
   / | \                /   \
  B  C  D              B     D
 / \     \            / \     \
E   F     G          E   F     G
```
