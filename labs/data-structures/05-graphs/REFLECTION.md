# Reflection: Graphs

## What I Learned

- Graphs are the most general data structure — they model arbitrary relationships
- Adjacency lists and matrices represent the same information with different trade-offs
- BFS explores level by level (queue); DFS explores depth first (stack)
- Dijkstra finds shortest paths in weighted graphs using a priority queue
- Topological sort orders vertices in a DAG by their dependencies
- Cycle detection, bipartite checking, and connectivity analysis are fundamental graph problems

## Questions to Consider

1. When would you choose an adjacency matrix over an adjacency list?
2. How does a social network use graph traversal for friend recommendations?
3. What is the relationship between DFS and topological sort?
4. How does Dijkstra's algorithm handle negative edge weights?
5. What are the trade-offs between recursive and iterative graph traversal?

## Connections

Graphs connect to:
- **Trees** (trees are acyclic connected graphs)
- **Linked lists** (special case: singly linked list is a path graph)
- **Arrays** (adjacency matrix is a 2D array)
- **Hash tables** (graph algorithms use HashSets for visited tracking)
- **Priority queues** (Dijkstra, Prim's)
- **Stacks/queues** (DFS uses stack, BFS uses queue)

## Self-Assessment

- [ ] Can implement graph with adjacency list and matrix
- [ ] Can implement BFS and DFS (recursive and iterative)
- [ ] Can detect cycles in undirected and directed graphs
- [ ] Can implement Dijkstra's shortest path
- [ ] Can implement topological sort (Kahn's and DFS-based)
- [ ] Can check bipartite property
- [ ] Understand graph density and representation selection
