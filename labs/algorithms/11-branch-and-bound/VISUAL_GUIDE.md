# Visual Guide — Branch and Bound

## B&B Search Tree for Knapsack
`
Capacity=5, Items: (w=2,v=3), (w=3,v=4), (w=4,v=5)
Sorted by ratio: item2(1.33), item3(1.25), item1(1.5)

Root: v=0, w=0, bound=7.33
├── Take item2: v=4, w=3, bound=7
│   ├── Take item3: v=9, w=7 ✗ (overweight)
│   └── Skip item3: v=4, w=3, bound=5.5
│       ├── Take item1: v=7, w=5, bound=7 ✓ best=7
│       └── Skip item1: v=4, w=3, bound=4 (prune ≤ 7)
└── Skip item2: v=0, w=0, bound=6.5 (prune ≤ 7)
Best solution: take item2 + item1 = value 7
`
"@

wf "CODE_DEEP_DIVE.md" @"
# Code Deep Dive — Branch and Bound

## TSP with B&B
`java
public int tsp(int[][] dist) {
    int n = dist.length;
    int[] bestTour = new int[n];
    int[] currTour = new int[n];
    boolean[] visited = new boolean[n];
    int[] bestCost = {Integer.MAX_VALUE};

    currTour[0] = 0;
    visited[0] = true;
    tspBranch(dist, currTour, visited, 1, 0, bestTour, bestCost);
    return bestCost[0];
}

private void tspBranch(int[][] dist, int[] currTour, boolean[] visited,
        int level, int currCost, int[] bestTour, int[] bestCost) {
    if (level == currTour.length) {
        int totalCost = currCost + dist[currTour[level-1]][currTour[0]];
        if (totalCost < bestCost[0]) bestCost[0] = totalCost;
        return;
    }

    for (int i = 1; i < currTour.length; i++) {
        if (!visited[i]) {
            int newCost = currCost + dist[currTour[level-1]][i];
            if (newCost < bestCost[0]) {
                visited[i] = true;
                currTour[level] = i;
                tspBranch(dist, currTour, visited, level + 1, newCost, bestTour, bestCost);
                visited[i] = false;
            }
        }
    }
}
`
"@

wf "STEP_BY_STEP.md" @"
# Step-by-Step — Branch and Bound

## General Approach
1. Define node structure (level, value, weight, bound)
2. Choose search strategy (best-first typically)
3. Compute initial best (heuristic or greedy)
4. For each node:
   a. If bound cannot improve best → prune
   b. Branch: generate child nodes
   c. Compute bound for each child
   d. Add children to priority queue
5. Continue until queue empty

## Designing a Good Bound
- Tight bound = more pruning = faster
- But tight bound is costly to compute
- Trade-off between bound quality and computation time
- Fractional knapsack bound is a common starting point
