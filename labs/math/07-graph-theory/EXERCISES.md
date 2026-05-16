# Exercises: Graph Theory

**1.** Draw complete graph K₄. How many edges?
- Solution: K₄ has 4 vertices, each connected to every other. Edges = n(n-1)/2 = 4×3/2 = 6

**2.** Is K₅ bipartite? Explain.
- Solution: No, K₅ is not bipartite because it contains odd cycles (K₅ is complete with all vertices connected, cannot be 2-colored without conflicts)

**3.** Find all paths from A to D in given graph.
- Solution: Without a specific graph, paths would include: A→D directly (if edge exists), A→B→D, A→C→D, A→B→C→D, etc. Count depends on graph structure.

**4.** BFS traversal starting from vertex 1.
- Solution: BFS explores level by level. From 1: visit 1, then all neighbors, then their unvisited neighbors. Order: 1, then neighbors, then next level.

**5.** DFS traversal starting from vertex 1.
- Solution: DFS explores depth-first: from 1 go to first neighbor, then its first unvisited neighbor, backtrack when stuck. Order depends on adjacency list order.

**6.** Find shortest path using Dijkstra.
- Solution: Start with source with distance 0. Repeatedly pick minimum distance unvisited vertex, relax its edges. Final distances from source give shortest paths.

**7.** Find MST using Kruskal.
- Solution: Sort all edges by weight. Add edges in order, skipping those that create cycles. Continue until V-1 edges added. Total weight minimized.

**8.** Prove: Tree with n vertices has n-1 edges.
- Solution: By induction. Base: n=1, tree has 0 edges. Assume true for n, add vertex connected to tree by one edge gives n+1 vertices and n edges. QED.

**9.** Check if graph is Eulerian.
- Solution: A graph has Eulerian circuit iff connected (except isolated vertices) and all vertices have even degree. Has Eulerian path if exactly 0 or 2 vertices have odd degree.

**10.** Find chromatic number of a star graph.
- Solution: Star graph (one central vertex connected to all leaves) has chromatic number 2. Center gets one color, all leaves can share the other color.