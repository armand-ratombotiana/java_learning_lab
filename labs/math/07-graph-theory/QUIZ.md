# Quiz: Graph Theory

## Section A: Multiple Choice (Questions 1-10)

**1.** K₃ has how many edges?
- A) 2
- B) 3
- C) 6
- D) 9

**2.** Tree with 5 vertices has how many edges?
- A) 4
- B) 5
- C) 6
- D) 10

**3.** BFS uses:
- A) Stack
- B) Queue
- C) Priority queue
- D) None

**4.** Complete bipartite graph K₂,₃ has:
- A) 5 edges
- B) 6 edges
- C) 10 edges
- D) 12 edges

**5.** Graph coloring assigns:
- A) Weights
- B) Colors to vertices
- C) Directions
- D) None

**6.** Dijkstra's algorithm finds:
- A) MST
- B) Shortest path
- C) Topological sort
- D) Cycle detection

**7.** A graph with no cycles is called:
- A) Complete
- B) Tree
- C) Bipartite
- D) Eulerian

**8.** Kruskal's algorithm uses:
- A) DFS
- B) BFS
- C) Union-Find
- D) Priority queue

**9.** In directed graph, in-degree of vertex v is:
- A) Number of edges leaving v
- B) Number of edges entering v
- C) Total edges
- D) None

**10.** Graph is bipartite if it contains:
- A) Odd cycle
- B) Even cycle
- C) No odd cycle
- D) Self-loop

## Section B: True/False (Questions 11-20)

**11.** A complete graph Kₙ has n(n-1)/2 edges.
- TRUE / FALSE

**12.** DFS can detect cycles.
- TRUE / FALSE

**13.** Tree is always bipartite.
- TRUE / FALSE

**14.** Euler circuit exists iff all vertices have even degree.
- TRUE / FALSE

**15.** BFS finds shortest path in unweighted graph.
- TRUE / FALSE

**16.** Prim's algorithm works on disconnected graphs.
- TRUE / FALSE

**17.** A graph with n vertices and n-1 edges is always a tree.
- TRUE / FALSE

**18.** Bellman-Ford can handle negative weights.
- TRUE / FALSE

**19.** Directed acyclic graph has topological order.
- TRUE / FALSE

**20.** Chromatic number of complete graph Kₙ is n.
- TRUE / FALSE

## Section C: Fill in the Blank (Questions 21-30)

**21.** Graph with 6 vertices, each degree 3 is called _____

**22.** Number of edges in K₅ = _____

**23.** Minimum spanning tree for graph with V vertices has ____ edges

**24.** BFS time complexity: O(____)

**25.** In DFS, vertices are marked as ____ when fully explored

**26.** Graph has Euler path if ____ vertices have odd degree

**27.** Floyd-Warshall algorithm finds ____ shortest paths

**28.** A graph where every vertex connects to every other is _____

**29.** In adjacency matrix, checking edge (u,v) takes O(____)

**30.** Graph with 4 vertices forming a square has ____ edges

## Answer Key

### Section A
1. B, 2. A, 3. B, 4. B, 5. B, 6. B, 7. B, 8. C, 9. B, 10. C

### Section B
11. TRUE, 12. TRUE, 13. TRUE, 14. TRUE, 15. TRUE, 16. FALSE (works on connected), 17. FALSE (must also be connected), 18. TRUE, 19. TRUE, 20. TRUE

### Section C
21. 3-regular, 22. 10, 23. V-1, 24. V+E, 25. visited, 26. 0 or 2, 27. all-pairs, 28. complete, 29. 1, 30. 4