# Approximation Algorithms — Internal Mechanics

## TSP — Christofides Algorithm (1.5-Approximation)
`java
public int christofidesTSP(int[][] dist) {
    int n = dist.length;
    // 1. Compute MST
    int[][] mst = primMST(dist, n);
    
    // 2. Find vertices with odd degree in MST
    List<Integer> oddVertices = findOddDegreeVertices(mst, n);
    
    // 3. Compute minimum-weight perfect matching on odd vertices
    int[][] matching = minWeightPerfectMatching(dist, oddVertices);
    
    // 4. Union MST and matching → Eulerian multigraph
    List<Edge> eulerianGraph = union(mst, matching, n);
    
    // 5. Find Eulerian tour
    List<Integer> tour = eulerianTour(eulerianGraph);
    
    // 6. Shortcut to Hamiltonian cycle
    return shortcut(tour, dist);
}
`

## Knapsack — FPTAS
`java
public int fptasKnapsack(int[] values, int[] weights, int capacity, double epsilon) {
    int n = values.length;
    int maxValue = Arrays.stream(values).max().orElse(0);
    double scale = (epsilon * maxValue) / n;
    
    int[] scaledValues = Arrays.stream(values)
        .map(v -> (int)(v / scale))
        .toArray();
    
    int maxScaled = Arrays.stream(scaledValues).sum();
    long[] dp = new long[maxScaled + 1];
    Arrays.fill(dp, Long.MAX_VALUE);
    dp[0] = 0;
    
    for (int i = 0; i < n; i++)
        for (int s = maxScaled; s >= scaledValues[i]; s--)
            if (dp[s - scaledValues[i]] != Long.MAX_VALUE)
                dp[s] = Math.min(dp[s], dp[s - scaledValues[i]] + weights[i]);
    
    int best = 0;
    for (int s = 0; s <= maxScaled; s++)
        if (dp[s] <= capacity) best = s;
    
    return (int)(best * scale);
}
`
Runtime: O(n²/ε), Ratio: (1+ε)
