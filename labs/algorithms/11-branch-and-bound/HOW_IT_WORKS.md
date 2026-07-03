# How Branch and Bound Works

## TSP — Branching on Edges
`
Cities: A, B, C, D
Start at A

Level 1: Choose first edge
  Branch: A→B (cost 10) → bound = 10 + lowerBound = 25
  Branch: A→C (cost 15) → bound = 15 + lowerBound = 30
  Branch: A→D (cost 12) → bound = 12 + lowerBound = 27

Process A→B first (lowest bound):
  Level 2: From B, choose next edge
    Branch: B→C (cost 20) → bound = 30
    Branch: B→D (cost 8) → bound = 18 + lowerBound = 28

Continue until complete tour found, prune branches with bound ≥ best
`

## Knapsack — Branching on Items
`
Items sorted by value/weight ratio
Node: bound = current value + (remaining capacity × best ratio)
If bound ≤ best value → prune
`
"@

wf "INTERNALS.md" @"
# Branch and Bound — Internal Mechanics

## Knapsack B&B
`java
class Node implements Comparable<Node> {
    int level, value, weight;
    double bound;
    public int compareTo(Node o) { return Double.compare(o.bound, this.bound); }
}

public int knapsack(int[] values, int[] weights, int capacity) {
    int n = values.length;
    Integer[] indices = IntStream.range(0, n).boxed().toArray(Integer[]::new);
    Arrays.sort(indices, (a, b) -> 
        Double.compare((double)values[b]/weights[b], (double)values[a]/weights[a]));

    PriorityQueue<Node> pq = new PriorityQueue<>();
    pq.offer(new Node(-1, 0, 0, bound(indices, values, weights, capacity, -1, 0, 0)));
    int best = 0;

    while (!pq.isEmpty()) {
        Node node = pq.poll();
        if (node.bound <= best) continue;

        int next = node.level + 1;
        if (next == n) continue;

        // take item
        int w = node.weight + weights[indices[next]];
        int v = node.value + values[indices[next]];
        if (w <= capacity) {
            best = Math.max(best, v);
            pq.offer(new Node(next, w, v, bound(indices, values, weights, capacity, next, w, v)));
        }

        // don't take item
        pq.offer(new Node(next, node.weight, node.value,
            bound(indices, values, weights, capacity, next, node.weight, node.value)));
    }
    return best;
}

private double bound(Integer[] indices, int[] values, int[] weights, int cap, int level, int weight, int value) {
    double bound = value;
    int remain = cap - weight;
    for (int i = level + 1; i < indices.length && remain > 0; i++) {
        int idx = indices[i];
        if (remain >= weights[idx]) {
            bound += values[idx];
            remain -= weights[idx];
        } else {
            bound += (double)values[idx] / weights[idx] * remain;
            break;
        }
    }
    return bound;
}
`
"@

wf "MATH_FOUNDATION.md" @"
# Math Foundation for Branch and Bound

## Bounding Functions

### Knapsack Upper Bound
Sort by value/weight ratio. Take whole items greedily, then fraction of next item:
bound = currentValue + Σ(full items) + remainingCapacity × (next value/weight)

### TSP Lower Bound
Using reduction of cost matrix:
1. Subtract minimum from each row
2. Subtract minimum from each column
3. Sum of all subtracted values = lower bound

## Pruning Condition
If upper bound (maximization) ≤ current best → prune
If lower bound (minimization) ≥ current best → prune

## Search Strategies
- Best-first: Choose node with best bound (memory intensive)
- Depth-first: Low memory, may explore poor branches early
- Breadth-first: High memory, uniform exploration
