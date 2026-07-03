# Visual Guide — Approximation

## Vertex Cover Approximation
`
Graph:           Optimal VC (size 3):
A - B - D        {B, D, E}
|   |   |
C - E - F

2-Approx VC (size 4):
Pick edge (A,B) → add {A,B}
Remove incident edges
Pick edge (D,F) → add {D,F}
Result: {A, B, D, F} (size 4 ≤ 2 × 3)
`

## TSP Christofides Tour
`
Step 1: MST               Step 2: Odd vertices
A---B---C                  A(odd)  B(even)  C(odd)
|       |                          
D---E---F                  D(even) E(odd)   F(even)

Step 3: Match odds        Step 4: Combine
A---C (match)             MST + Matching → Eulerian
E---F (match)             Shortcut → Hamiltonian
`
"@

wf "CODE_DEEP_DIVE.md" @"
# Code Deep Dive — Approximation

## Max-Cut — 0.5-Approximation (Randomized)
`java
public int maxCutRandomized(int[][] graph) {
    Random rand = new Random();
    boolean[] partition = new boolean[graph.length];
    for (int i = 0; i < graph.length; i++)
        partition[i] = rand.nextBoolean();
    
    int cutSize = 0;
    for (int i = 0; i < graph.length; i++)
        for (int j : graph[i])
            if (partition[i] != partition[j]) cutSize++;
    return cutSize / 2; // each edge counted twice
}
`
Expected ratio: 0.5 (can be derandomized by method of conditional expectations)

## Load Balancing (Makespan Minimization)
`java
public int greedyLoadBalance(int[] jobs, int machines) {
    int[] loads = new int[machines];
    Arrays.sort(jobs); // or use priority queue for better ratio
    for (int i = jobs.length - 1; i >= 0; i--) {
        int minMachine = 0;
        for (int m = 1; m < machines; m++)
            if (loads[m] < loads[minMachine]) minMachine = m;
        loads[minMachine] += jobs[i];
    }
    return Arrays.stream(loads).max().orElse(0);
}
`
Ratio: LPT (Longest Processing Time) = 4/3 - 1/(3m)
