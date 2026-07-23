# Two Sigma Interview Guide — Algorithms Academy

## Interview Process for Algorithm-Heavy Roles
- **Rounds**: HR screen → Online assessment (HackerRank, 90 min, 2 problems) → Phone screen (1-2 rounds, 60 min each) → Onsite (4-5 rounds, 60 min each): algorithmic coding, system design, probability/stats, and behavioral.
- **Timeline**: 4-8 weeks total. Online assessment within 1 week. Phone screens within 2-3 weeks. Onsite within 3-4 weeks of phone.
- **Algorithm Difficulty**: LeetCode Hard. Two Sigma asks harder-than-average algorithm problems because they test mathematical and statistical reasoning alongside coding.
- **How algorithm-heavy?**: Heavy but with a twist. 50% of the interview is algorithms that involve probability, math, or statistics. Two Sigma values correctness and efficiency equally.

## Top Problems by Algorithm Category

### Category: Probability and Randomized Algorithms
#### Problem: Shuffle an Array (LC 384)
- **Difficulty/Frequency**: Medium / Very High at Two Sigma
- **Problem statement**: Shuffle a set of numbers without duplicates. Implement reset and shuffle with uniform distribution.
- **Interview walkthrough**: Fisher-Yates shuffle. Iterate from end to start, pick a random index from 0 to i, swap with current position. Each permutation is equally likely.
- **Solution approaches**: Fisher-Yates O(n). Naive (pick random remaining element) O(n^2). Two Sigma expects Fisher-Yates with proof of uniformity.
- **Java code**:
```java
import java.util.Random;

/**
 * Solution for Shuffle an Array using Fisher-Yates.
 */
public class ShuffleArray {

    private int[] original;
    private int[] array;
    private Random rand = new Random();

    /**
     * Initializes shuffle object with the array.
     *
     * @param nums initial array
     */
    public ShuffleArray(int[] nums) {
        this.original = nums.clone();
        this.array = nums.clone();
    }

    /**
     * Resets the array to its original configuration.
     *
     * @return original array
     */
    public int[] reset() {
        array = original.clone();
        return array;
    }

    /**
     * Returns a uniformly random shuffling of the array.
     *
     * @return shuffled array
     */
    public int[] shuffle() {
        for (int i = array.length - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            int temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
        return array;
    }
}
```
- **What Two Sigma evaluates**: Uniform distribution proof, in-place shuffling, random number generator usage, clone vs reference.
- **Follow-ups**: Weighted random selection. Reservoir sampling for streaming data. Random pick with weight.

#### Problem: Random Pick with Weight (LC 528)
- **Difficulty/Frequency**: Medium / High
- **Problem statement**: Given an array of positive weights, implement random pick with probability proportional to weight.
- **Interview walkthrough**: Compute prefix sums. Generate random number in [1, totalSum]. Binary search on prefix sums to find the bucket.
- **Solution approaches**: Prefix sum + binary search O(log n) per pick, O(n) initialization. Two Sigma expects the binary search approach.
- **Java code**:
```java
import java.util.Random;

/**
 * Solution for Random Pick with Weight.
 */
public class RandomPickWeight {

    private int[] prefixSums;
    private int totalSum;
    private Random rand = new Random();

    /**
     * Initializes with weights.
     *
     * @param w array of positive weights
     */
    public RandomPickWeight(int[] w) {
        prefixSums = new int[w.length];
        int sum = 0;
        for (int i = 0; i < w.length; i++) {
            sum += w[i];
            prefixSums[i] = sum;
        }
        totalSum = sum;
    }

    /**
     * Picks an index randomly with probability proportional to weight.
     *
     * @return picked index
     */
    public int pickIndex() {
        int target = rand.nextInt(totalSum) + 1;
        int left = 0, right = prefixSums.length - 1;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (prefixSums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }
        return left;
    }
}
```
- **What Two Sigma evaluates**: Probability distribution understanding, binary search on cumulative distribution, numerical edge cases.
- **Follow-ups**: What if weights change frequently (binary indexed tree). Random sampling without replacement. Random pick with non-integral weights.

### Category: Statistics and Math
#### Problem: Pow(x, n) with Statistics Context (LC 50 variant)
- **Difficulty/Frequency**: Medium / High
- **Problem statement**: Compute x^n for large n where x is a double. Two Sigma may frame this as computing compound interest or probability multiplication.
- **Interview walkthrough**: Exponentiation by squaring. Iterative approach using bit manipulation for efficiency.
- **Solution approaches**: Already covered in Google section. Two Sigma will expect the O(log n) solution and discuss numerical precision.
- **Java code**:
```java
/**
 * Solution for computing power for Two Sigma context.
 */
public class CompoundInterest {

    /**
     * Computes compound growth factor: (1 + rate)^periods.
     *
     * @param rate    interest rate per period
     * @param periods number of periods
     * @return growth factor
     */
    public double compoundFactor(double rate, int periods) {
        double x = 1.0 + rate;
        long n = periods;
        double result = 1.0;
        while (n > 0) {
            if ((n & 1) == 1) result *= x;
            x *= x;
            n >>= 1;
        }
        return result;
    }
}
```
- **What Two Sigma evaluates**: Numerical precision (double vs BigDecimal), approximation for small rates, practical financial computation.
- **Follow-ups**: Compute continuous compounding (e^rt). Newton's method for root finding.

#### Problem: Count Primes (LC 204)
- **Difficulty/Frequency**: Easy / Medium
- **Problem statement**: Count the number of prime numbers less than a non-negative number n.
- **Interview walkthrough**: Sieve of Eratosthenes. Create boolean array of size n. Mark multiples of each prime starting from 2.
- **Solution approaches**: Sieve of Eratosthenes O(n log log n). Optimized sieve (only odds, start from p^2). Two Sigma expects the optimized version.
- **Java code**:
```java
import java.util.Arrays;

/**
 * Solution for Count Primes using Sieve of Eratosthenes.
 */
public class CountPrimes {

    /**
     * Returns count of primes less than n.
     *
     * @param n upper bound (exclusive)
     * @return number of primes < n
     */
    public int countPrimes(int n) {
        if (n <= 2) return 0;
        boolean[] isPrime = new boolean[n];
        Arrays.fill(isPrime, true);
        isPrime[0] = false;
        isPrime[1] = false;
        for (int i = 2; i * i < n; i++) {
            if (isPrime[i]) {
                for (int j = i * i; j < n; j += i) {
                    isPrime[j] = false;
                }
            }
        }
        int count = 0;
        for (boolean p : isPrime) {
            if (p) count++;
        }
        return count;
    }
}
```
- **What Two Sigma evaluates**: Sieve understanding, memory vs time trade-off, optimization (starting from i*i).
- **Follow-ups**: Segmented sieve for large ranges. Prime factorization. Check if a number is prime (Miller-Rabin).

### Category: Dynamic Programming
#### Problem: Maximum Profit in Job Scheduling (LC 1235)
- **Difficulty/Frequency**: Hard / High at Two Sigma
- **Problem statement**: Given startTime, endTime, and profit arrays, find the maximum profit you can achieve by scheduling non-overlapping jobs.
- **Interview walkthrough**: Sort jobs by end time. DP: dp[i] = max profit considering first i jobs. For each job, binary search for the last job that ends before this job starts. dp[i] = max(dp[i-1], profit[i] + dp[prev]).
- **Solution approaches**: DP with binary search O(n log n). Two Sigma expects the optimized version.
- **Java code**:
```java
import java.util.*;

/**
 * Solution for Maximum Profit in Job Scheduling.
 */
public class JobScheduling {

    static class Job {
        int start, end, profit;
        Job(int s, int e, int p) {
            start = s; end = e; profit = p;
        }
    }

    /**
     * Returns maximum profit from non-overlapping jobs.
     *
     * @param startTime array of start times
     * @param endTime   array of end times
     * @param profit    array of profits
     * @return maximum profit
     */
    public int jobScheduling(int[] startTime, int[] endTime,
                             int[] profit) {
        int n = startTime.length;
        Job[] jobs = new Job[n];
        for (int i = 0; i < n; i++) {
            jobs[i] = new Job(startTime[i], endTime[i], profit[i]);
        }
        Arrays.sort(jobs, (a, b) -> a.end - b.end);
        int[] dp = new int[n];
        dp[0] = jobs[0].profit;
        for (int i = 1; i < n; i++) {
            int include = jobs[i].profit;
            int prev = binarySearch(jobs, jobs[i].start, i);
            if (prev != -1) include += dp[prev];
            dp[i] = Math.max(dp[i - 1], include);
        }
        return dp[n - 1];
    }

    private int binarySearch(Job[] jobs, int time, int limit) {
        int left = 0, right = limit - 1, result = -1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (jobs[mid].end <= time) {
                result = mid;
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return result;
    }
}
```
- **What Two Sigma evaluates**: Weighted interval scheduling DP, binary search optimization, job sorting strategy.
- **Follow-ups**: Meeting rooms II. Minimum number of arrows to burst balloons. Maximum number of events that can be attended.

### Category: Graphs
#### Problem: Reconstruct Itinerary (LC 332)
- **Difficulty/Frequency**: Hard / High at Two Sigma
- **Problem statement**: Given a list of airline tickets (from, to), reconstruct the itinerary in order. You start at JFK. If multiple valid orders exist, return the lexical smallest one.
- **Interview walkthrough**: Build graph as adjacency list with min-heap or sorted list for lexical order. Use Hierholzer's algorithm for Eulerian path. DFS post-order traversal, removing edges as visited.
- **Solution approaches**: DFS with post-order (Eulerian path) O(E log E). Two Sigma expects the Hierholzer's algorithm.
- **Java code**:
```java
import java.util.*;

/**
 * Solution for Reconstruct Itinerary.
 */
public class ReconstructItinerary {

    /**
     * Reconstructs itinerary from tickets starting at JFK.
     *
     * @param tickets list of ticket pairs [from, to]
     * @return list of airports in itinerary order
     */
    public List<String> findItinerary(List<List<String>> tickets) {
        Map<String, PriorityQueue<String>> graph = new HashMap<>();
        for (List<String> t : tickets) {
            graph.computeIfAbsent(t.get(0),
                    k -> new PriorityQueue<>()).add(t.get(1));
        }
        List<String> result = new LinkedList<>();
        dfs(graph, "JFK", result);
        return result;
    }

    private void dfs(Map<String, PriorityQueue<String>> graph,
                     String airport, List<String> result) {
        PriorityQueue<String> neighbors = graph.get(airport);
        while (neighbors != null && !neighbors.isEmpty()) {
            dfs(graph, neighbors.poll(), result);
        }
        result.add(0, airport);
    }
}
```
- **What Two Sigma evaluates**: Eulerian path concept, graph construction, priority queue for lexical ordering, recursion understanding.
- **Follow-ups**: Valid arrangement of pairs (similar Eulerian path problem). Count of Eulerian circuits. Minimum cost to connect cities (MST).

#### Problem: Minimum Cost to Connect Cities (Prim's / Kruskal's)
- **Difficulty/Frequency**: Medium / High
- **Problem statement**: Given N cities and connection costs, find minimum cost to connect all cities.
- **Interview walkthrough**: This is a Minimum Spanning Tree problem. Use Kruskal's with Union-Find or Prim's with PriorityQueue.
- **Solution approaches**: Kruskal's O(E log E). Prim's O(E log V). Two Sigma expects either with Union-Find implementation.
- **Java code**:
```java
import java.util.*;

/**
 * Solution for minimum cost to connect cities using Kruskal's.
 */
public class ConnectCities {

    static class Edge {
        int u, v, cost;
        Edge(int u, int v, int cost) {
            this.u = u; this.v = v; this.cost = cost;
        }
    }

    private int[] parent;

    /**
     * Returns minimum cost to connect all cities.
     *
     * @param n     number of cities
     * @param edges array of connections [city1, city2, cost]
     * @return minimum cost
     */
    public int minimumCost(int n, int[][] edges) {
        List<Edge> edgeList = new ArrayList<>();
        for (int[] e : edges) {
            edgeList.add(new Edge(e[0], e[1], e[2]));
        }
        edgeList.sort((a, b) -> a.cost - b.cost);
        parent = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;
        int totalCost = 0, edgesUsed = 0;
        for (Edge e : edgeList) {
            if (union(e.u, e.v)) {
                totalCost += e.cost;
                edgesUsed++;
                if (edgesUsed == n - 1) return totalCost;
            }
        }
        return -1;
    }

    private int find(int x) {
        if (parent[x] != x) parent[x] = find(parent[x]);
        return parent[x];
    }

    private boolean union(int x, int y) {
        int rx = find(x), ry = find(y);
        if (rx == ry) return false;
        parent[ry] = rx;
        return true;
    }
}
```
- **What Two Sigma evaluates**: MST algorithm, Union-Find implementation (path compression + union by rank), edge sorting.
- **Follow-ups**: Kruskal's with union by rank. Minimum cost to connect all points (Manhattan distance MST). Critical connections in a network.

### Category: Design with Algorithms
#### Problem: Implement Trie (Prefix Tree) (LC 208)
- **Difficulty/Frequency**: Medium / Very High
- **Problem statement**: Implement a trie with insert, search, and startsWith methods.
- **Interview walkthrough**: Trie node has boolean isEnd and array of 26 child nodes. Insert by iterating characters and creating nodes. Search similar but check isEnd at end.
- **Solution approaches**: Array-based trie O(L). HashMap-based trie for Unicode. Two Sigma expects the array-based version.
- **Java code**:
```java
/**
 * Implementation of Trie (Prefix Tree).
 */
public class Trie {

    private TrieNode root;

    static class TrieNode {
        TrieNode[] children = new TrieNode[26];
        boolean isEnd;
    }

    /** Initialize trie. */
    public Trie() {
        root = new TrieNode();
    }

    /**
     * Inserts a word into the trie.
     *
     * @param word word to insert
     */
    public void insert(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            int idx = c - 'a';
            if (node.children[idx] == null) {
                node.children[idx] = new TrieNode();
            }
            node = node.children[idx];
        }
        node.isEnd = true;
    }

    /**
     * Checks if the word is in the trie.
     *
     * @param word word to search
     * @return true if word exists
     */
    public boolean search(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            int idx = c - 'a';
            if (node.children[idx] == null) return false;
            node = node.children[idx];
        }
        return node.isEnd;
    }

    /**
     * Checks if any word starts with the given prefix.
     *
     * @param prefix prefix to search
     * @return true if prefix exists
     */
    public boolean startsWith(String prefix) {
        TrieNode node = root;
        for (char c : prefix.toCharArray()) {
            int idx = c - 'a';
            if (node.children[idx] == null) return false;
            node = node.children[idx];
        }
        return true;
    }
}
```
- **What Two Sigma evaluates**: Trie data structure knowledge, space efficiency (references vs arrays), prefix search application.
- **Follow-ups**: Add and search word with wildcards (LC 211). Design search autocomplete system. Longest word in dictionary.

## Company-Specific Algorithm Focus
Two Sigma is unique in its emphasis on **probability, statistics, and randomized algorithms**. They are the only company among these seven that routinely tests **prime sieving, random shuffling, and probability distributions**. Two Sigma also focuses heavily on **DP with optimization** (especially binary search for weighted interval scheduling). They care about **Eulerian paths** and **minimum spanning trees**. Two Sigma expects mathematical rigor in algorithm analysis, including proofs of optimality and correctness.

## System Design with Algorithms
1. **Design a Real-Time Market Data Feed Handler** — Requires ring buffer (lock-free queue) for low-latency data ingestion, order book reconstruction using price-time priority queue (TreeMap of sorted prices, each with a Queue), and fast matching engine using modified red-black tree for O(log n) order insertion/cancellation.
2. **Design a Statistical Arbitrage Trading System** — Requires cointegration testing (Engle-Granger method), z-score calculation for pairs trading, Kalman filter for dynamic hedge ratio estimation, and backtesting framework using dynamic programming for strategy evaluation across historical data.

## Behavioral Questions (STAR)
1. **Tell me about a time you used data to make a decision**: I built a Monte Carlo simulation with 10,000 runs to evaluate the performance of a new trading strategy under different market conditions. The simulation used Latin hypercube sampling for efficient parameter space exploration. The results showed a 60% probability of outperformance, so we paper-traded for 3 months.
2. **Tell me about a time you optimized performance**: A market data processing pipeline was running at 500 microseconds per event. I replaced the standard sorting algorithm with a bucket sort (prices are discrete) and used memory-mapped files for the input buffer. This reduced latency to 50 microseconds per event.
3. **Tell me about a time you solved a challenging problem**: I needed to detect regime changes in market volatility. I implemented a hidden Markov model using the Baum-Welch algorithm (expectation-maximization) with the forward-backward algorithm for posterior state estimation. This successfully identified three volatility regimes.
4. **Tell me about a time you communicated a complex concept**: I explained the difference between O(n^2) and O(n log n) sorting to non-technical stakeholders by showing a graph of actual runtimes on our trading data volume. I then explained why we needed to replace our naive matching engine with a priority queue-based approach.
5. **Tell me about a time you handled a production issue**: A memory leak in our order book system was causing restarts every 4 hours. I used a memory profiler to identify an unbounded HashMap of stale orders. I implemented a bounded cache with LRU eviction policy using LinkedHashMap with access-order=true, fixing the memory leak permanently.

## Study Plan
Prioritize these labs in order:
1. Lab 16: Randomized Algorithms
2. Lab 4: Dynamic Programming I
3. Lab 5: Dynamic Programming II
4. Lab 13: Number Theory and Primes
5. Lab 7: Graph Algorithms I
6. Lab 8: Graph Algorithms II (MST, shortest paths)
7. Lab 3: HashMaps and Sets (for design questions)

## Tips
- Two Sigma values **mathematical proof** in your algorithm reasoning. Be prepared to prove things like uniform distribution, optimal substructure, and greedy correctness.
- Two Sigma asks **more probability questions** than any other top tech company. Review combinatorics, conditional probability, and expected value.
- **Numerical precision** is important. Discuss floating-point error, overflow, and when to use BigDecimal vs double.
- The online assessment is challenging — practice with **contest-level LeetCode problems**. Two Sigma's HackerRank problems have high time limits but also high difficulty.
- Be ready for **system design with a trading focus**. Know how order books, matching engines, and market data feeds work.
- Two Sigma interviewers appreciate **elegant mathematical solutions** over brute-force coding. If you can use a math formula (closed-form solution), that is often preferred.
- **Discuss edge cases with large numbers**: integer overflow, stack overflow from deep recursion, and memory limits for large data structures.
- Show awareness of **statistical concepts** like bias-variance trade-off, overfitting in backtesting, and survivorship bias.
