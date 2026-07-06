# Step-by-Step — Network Flow Implementation

## Dinic Implementation Steps

1. Build graph with adjacency lists of edges (to, rev, cap)
2. Initialize flow = 0
3. BFS from s to compute level[] (distance from s):
   a. Set level[s] = 0, queue all adjacent with cap > 0
   b. If t is unreachable, break
4. While BFS reaches t:
   a. Initialize it[] (current edge pointer for each node) to 0
   b. While DFS finds a path from s to t:
      - Track minimum residual capacity along path
      - Subtract from forward edges, add to reverse edges
      - Add to total flow
   c. After DFS returns 0, run BFS again for new level graph
5. Return total flow

## Bipartite Matching Steps

1. Build flow network: source->left size 1, left->right size 1, right->sink size 1
2. Run Dinic on the constructed network
3. Extract matching: check which left->right edges have 0 residual capacity
