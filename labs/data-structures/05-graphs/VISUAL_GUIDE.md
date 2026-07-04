# Visual Guide to Graphs

## Directed vs Undirected

```
Undirected:           Directed:
  1 ─── 2               1 ──→ 2
  │     │               │     ↑
  │     │               │     │
  3 ─── 4               ↓     │
                        3 ──→ 4
```

## Adjacency Matrix (Directed, 4 vertices)

```
Edges: 0→1, 0→2, 1→3, 2→3, 3→0

    0  1  2  3
0  [0, 1, 1, 0]
1  [0, 0, 0, 1]
2  [0, 0, 0, 1]
3  [1, 0, 0, 0]
```

## BFS Traversal

```
Graph:
0 ─── 1 ─── 3
│           │
└─── 2 ────┘

BFS from 0:
Queue: [0]          visit: ∅
Dequeue 0 → visit 0, add 1, 2
Queue: [1, 2]       visit: 0
Dequeue 1 → visit 1, add 3
Queue: [2, 3]       visit: 0, 1
Dequeue 2 → visit 2, add nothing new
Queue: [3]          visit: 0, 1, 2
Dequeue 3 → visit 3
Queue: []           visit: 0, 1, 2, 3
```

## DFS Traversal

```
Same graph, DFS from 0:
Stack: [0]          visit: ∅
Pop 0 → visit 0, push 1, 2
Stack: [1, 2]       visit: 0
Pop 2 → visit 2, push nothing new
Stack: [1]          visit: 0, 2
Pop 1 → visit 1, push 3
Stack: [3]          visit: 0, 2, 1
Pop 3 → visit 3
Stack: []           visit: 0, 2, 1, 3
```

## Dijkstra's Algorithm

```
Graph (weighted):
    (4)     (1)
  0 ──── 1 ──── 3
  │               │
 (2)             (5)
  │               │
  2 ────(1)──── 4

Dijkstra from 0:
dist = [0, ∞, ∞, ∞, ∞]
Process 0 → relax: dist[1]=4, dist[2]=2
Pick 2 (dist=2) → relax: dist[4]=3
Pick 4 (dist=3) → relax: dist[3]=8
Pick 1 (dist=4) → relax: dist[3]=5 (update!)
Pick 3 (dist=5)
Result: [0, 4, 2, 5, 3]
```
