# Step by Step: Graph Operations

## Graph Construction

```
Vertices: 0, 1, 2, 3
Edges:    0-1, 0-2, 1-3, 2-3 (undirected)

Adjacency List (initial):
0: []
1: []
2: []
3: []

addEdge(0, 1):
0: [1]
1: [0]

addEdge(0, 2):
0: [1, 2]
2: [0]

addEdge(1, 3):
1: [0, 3]
3: [1]

addEdge(2, 3):
2: [0, 3]
3: [1, 2]
```

## BFS Finding Shortest Path

```
Graph:
0 ─ 1
│   │
2 ─ 3

BFS from 0 to 3:
Queue: [(0, dist=0)]
Dequeue (0, 0): visit 0, neighbors = [1, 2]
  dist[1] = 1, parent[1] = 0
  dist[2] = 1, parent[2] = 0
Queue: [(1, 1), (2, 1)]
Dequeue (1, 1): neighbors = [0, 3]
  0 visited, dist[3] = 2, parent[3] = 1
Queue: [(2, 1), (3, 2)]
Dequeue (2, 1): visited
Dequeue (3, 2): target found!
Path: 3 → parent[3]=1 → parent[1]=0 → 0
Shortest path length: 2
```

## Topological Sort (Kahn's)

```
Graph: A → B → C, A → D → C

In-degree: A=0, B=1, C=2, D=1

Queue: [A]
Dequeue A: decrement B(0), D(0)
Queue: [B, D]
Dequeue B: decrement C(1)
Queue: [D]
Dequeue D: decrement C(0)
Queue: [C]
Dequeue C
Result: [A, B, D, C]  (valid topological order)
```

## Dijkstra Step by Step

```
Graph: 0--10→1, 0--5→2, 2--1→3, 3--2→1, 1--4→4

Dijkstra from 0:

Initialize: dist = [0, ∞, ∞, ∞, ∞]
PQ: [(0,0)]

PQ poll → (0,0):
  Relax 0→1: dist[1]=10 (prev=0)
  Relax 0→2: dist[2]=5  (prev=0)
PQ: [(2,5), (1,10)]

PQ poll → (2,5):
  Relax 2→3: dist[3]=6  (prev=2)
PQ: [(3,6), (1,10)]

PQ poll → (3,6):
  Relax 3→1: dist[1]=8 < 10, update (prev=3)
PQ: [(1,8), (1,10)]

PQ poll → (1,8):
  Relax 1→4: dist[4]=12 (prev=1)
PQ: [(4,12)]

PQ poll → (4,12): no outgoing edges

Result distances: [0, 8, 5, 6, 12]
```
