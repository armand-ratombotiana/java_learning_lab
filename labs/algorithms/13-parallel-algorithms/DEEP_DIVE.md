# Deep Dive: Parallel Algorithms

## 1. Work-Span Model

The work-span (work-depth) model is the standard theoretical framework for analyzing parallel algorithms.

### Definitions

```java
public class WorkSpanModel {
    // For a parallel algorithm:
    //   WORK(W): total number of operations
    //   SPAN(S): length of the longest chain of dependencies (critical path)
    //
    // Parallelism = W / S
    // Lower bound on time with P processors: T(P) ≥ max(W/P, S)
    //
    // Work-optimal: W = O(W_seq) where W_seq is best sequential work
    // Span-optimal: S ≤ O(W_seq / P) for some P
    //
    // Example: prefix sum
    //   Sequential: W = O(n), S = O(n)
    //   Parallel (tree): W = O(n), S = O(log n)
    //   Parallelism = O(n / log n)
    
    // The W-S model assumes:
    // - Unlimited processors available
    // - No overhead for synchronization
    // - All operations take unit time
    // - Perfect load balancing
    // Real hardware adds: overhead, contention, communication costs
}
```

### Brent's Lemma

```java
// Brent's Lemma (1974):
// Given a parallel algorithm with work W and span S:
// T(P) ≤ W/P + S
//
// Proof:
// - In the worst case, the critical path takes S time units
// - During these S time units, at most P operations can execute per unit time
// - Total work = W, so at most W/P additional time units needed (in parallel)
// - Therefore T(P) ≤ W/P + S
//
// Corollary: T(P) = O(W/P + S)
// The algorithm achieves linear speedup if W/P ≥ S (i.e., P ≤ W/S)
//
// The "speedup" is defined as: S(P) = T(1) / T(P)
// Linear speedup: S(P) = P
// Super-linear speedup: S(P) > P (cache effects, not theoretical)

public class BrentLemma {
    // Example: parallel merge sort
    // W = O(n log n), S = O(log² n)
    // P_max = W/S = O(n log n / log² n) = O(n / log n)
    // With P = n processors: T ≤ O(n log n / n) + O(log² n) = O(log² n)
    
    // Amortized analysis:
    // With P = O(n / log n) processors, time = O(log² n + log n) = O(log² n)
    // This is the optimal parallel time for comparison-based sorting
    
    public static void main(String[] args) {
        int n = 1_000_000;
        double work = n * Math.log(n) / Math.log(2);
        double span = Math.log(n) / Math.log(2) * Math.log(n) / Math.log(2);
        double parallelism = work / span;
        
        System.out.printf("n=%d%n", n);
        System.out.printf("Work: %.0f%n", work);
        System.out.printf("Span: %.0f%n", span);
        System.out.printf("Parallelism: %.1f%n", parallelism);
        System.out.printf("Ideal processors: %.0f%n", parallelism);
    }
}
```

## 2. Amdahl's Law vs Gustafson's Law

### Amdahl's Law (fixed-size speedup)

```java
// Amdahl's Law:
// Speedup S(P) = 1 / (s + (1-s)/P)
// where s = fraction of algorithm that is sequential
//
// As P → ∞: S(P) → 1/s
// Implication: even 5% sequential code limits speedup to 20x
//
// Example:
// s = 0.05 (5% sequential), P = 100:
// S(100) = 1 / (0.05 + 0.95/100) = 1 / (0.05 + 0.0095) = 16.8x
//
// This seems pessimistic, but assumes fixed problem size.

public class AmdahlsLaw {
    public static double speedup(double sequentialFraction, int processors) {
        double parallelFraction = 1.0 - sequentialFraction;
        return 1.0 / (sequentialFraction + parallelFraction / processors);
    }
    
    // Analysis: adding more processors gives diminishing returns
    public static void main(String[] args) {
        System.out.println("Amdahl's Law (5% sequential):");
        for (int p : new int[]{2, 4, 8, 16, 32, 64, 128}) {
            double s = speedup(0.05, p);
            double e = s / p * 100; // efficiency
            System.out.printf("P=%3d: speedup=%5.2f, efficiency=%5.1f%%%n", p, s, e);
        }
    }
}
```

### Gustafson's Law (scaled speedup)

```java
// Gustafson's Law:
// Scaled speedup S'(P) = P - s·(P - 1) = (1-s)·P + s
// where s = sequential fraction (measured on the P-processor system)
//
// Interpretation: as we add processors, we can solve larger problems
// The sequential part doesn't grow with problem size
//
// Example:
// s = 0.05, P = 100:
// S'(100) = 100 - 0.05·99 = 95.05x
//
// Amdahl: fixed problem, diminishing returns
// Gustafson: scaled problem (same time, larger solution), near-linear speedup

public class GustafsonsLaw {
    public static double scaledSpeedup(double sequentialFraction, int processors) {
        return processors - sequentialFraction * (processors - 1);
    }
    
    public static double weakScalingEfficiency(double sequentialFraction, int processors) {
        // In weak scaling, work per processor stays constant
        // Efficiency = scaled_speedup / processors
        double scaledSpeedup = scaledSpeedup(sequentialFraction, processors);
        return scaledSpeedup / processors;
    }
    
    public static void main(String[] args) {
        System.out.println("Gustafson's Law (5% sequential, weak scaling):");
        for (int p : new int[]{2, 4, 8, 16, 32, 64, 128}) {
            double s = scaledSpeedup(0.05, p);
            double e = weakScalingEfficiency(0.05, p);
            System.out.printf("P=%3d: speedup=%5.2f, efficiency=%5.2f%n", p, s, e);
        }
    }
}
```

### Practical Implications

```java
public class LawsComparison {
    // Amdahl: useful for strong scaling (fixed problem, minimize time)
    //   - Simulation: need results faster → add processors
    //   - Diminishing returns after P > 1/s
    //
    // Gustafson: useful for weak scaling (fixed time per processor, maximize size)
    //   - Data processing: more data with more processors
    //   - Near-linear speedup achievable
    //
    // Total speedup = Amdahl component + Gustafson component
    // In practice: problems have both fixed-cost and scalable-cost parts
    
    // The "serial fraction" isn't really constant:
    // - Initialization, I/O, communication overhead
    // - These scale with P, not with problem size
    // - As n grows, s decreases → Gustafson's model is more relevant
    
    // Real-world parallel efficiency:
    // s → s(n) + c(P) where c(P) = communication overhead
    // At large P, communication dominates
    // T(P) = W/P + S + c(P)·P (simplified model)
}
```

## 3. Parallel Prefix Sum (Scan)

The parallel prefix sum is a fundamental building block for many parallel algorithms.

### Sequential vs Parallel

```java
public class ParallelPrefixSum {
    
    // Sequential O(n):
    public static int[] prefixSum(int[] arr) {
        int n = arr.length;
        int[] result = new int[n];
        result[0] = arr[0];
        for (int i = 1; i < n; i++) {
            result[i] = result[i-1] + arr[i];
        }
        return result;
    }
    // Work: O(n), Span: O(n)
    
    // Parallel O(log n): Blelloch's algorithm
    // Two phases: up-sweep (reduce) and down-sweep (distribute)
    public static int[] parallelPrefixSum(int[] arr) {
        int n = arr.length;
        int[] data = arr.clone();
        
        // Phase 1: Up-sweep (build tree from leaves to root)
        for (int stride = 1; stride < n; stride <<= 1) {
            // Parallel for:
            for (int i = 0; i < n; i += 2 * stride) {
                if (i + stride - 1 < n) {
                    data[i + 2 * stride - 1] += data[i + stride - 1];
                }
            }
            // In real parallelism: use fork-join or parallel streams
        }
        
        // Set root to identity (0 for sum)
        data[n - 1] = 0;
        
        // Phase 2: Down-sweep (distribute from root to leaves)
        for (int stride = n >> 1; stride > 0; stride >>= 1) {
            // Parallel for:
            for (int i = 0; i < n; i += 2 * stride) {
                if (i + stride - 1 < n) {
                    int temp = data[i + stride - 1];
                    data[i + stride - 1] = data[i + 2 * stride - 1];
                    data[i + 2 * stride - 1] += temp;
                }
            }
        }
        
        // Result is prefix sum where result[i] = sum of arr[0..i-1]
        // Standard form: add arr[i] to get exclusive prefix
        int[] result = new int[n];
        for (int i = 0; i < n - 1; i++) {
            result[i + 1] = data[i] + arr[i];
        }
        result[0] = arr[0];
        // Actually, let's fix for inclusive prefix:
        for (int i = 1; i < n; i++) {
            result[i] = result[i-1] + arr[i];
        }
        // In production: combine the two phases properly
        
        return result;
    }
    // Work: O(n), Span: O(log n)
    // Parallelism: O(n / log n)
}
```

### Application: Stream Compaction

```java
public class StreamCompaction {
    // Use parallel prefix sum to filter elements
    
    public static int[] compact(int[] arr, Predicate<Integer> pred) {
        int n = arr.length;
        
        // Step 1: Create flags (1 if predicate matches)
        int[] flags = new int[n];
        // Parallel:
        IntStream.range(0, n).parallel().forEach(i -> 
            flags[i] = pred.test(arr[i]) ? 1 : 0);
        
        // Step 2: Parallel prefix sum on flags
        int[] prefix = parallelPrefixSum(flags);
        int total = prefix[n - 1] + flags[n - 1]; // or flags[n-1]==1 ? prefix[n-1]+1 : ...
        // Actually use last element of prefix + last flag
        int totalCount = flags[n-1] == 1 ? prefix[n-1] + 1 : prefix[n-1];
        // Fix: prefix[n-1] = sum of flags[0..n-2], so total = prefix[n-1] + (flags[n-1]==1 ? 1 : 0)
        
        // Step 3: Scatter
        int[] result = new int[total];
        // Parallel:
        IntStream.range(0, n).parallel().forEach(i -> {
            if (flags[i] == 1) {
                int dest = i == 0 ? 0 : prefix[i-1];
                result[dest] = arr[i];
            }
        });
        
        return result;
    }
}
```

## 4. Parallel Merge Sort

```java
public class ParallelMergeSort {
    
    // Sequential merge sort: W = O(n log n), S = O(n log n)
    // Parallel merge: W = O(n), S = O(log n) (using binary search-based merge)
    // Parallel merge sort: W = O(n log n), S = O(log² n)
    
    public static <T extends Comparable<T>> void parallelMergeSort(
            T[] arr, int left, int right, int cutoff) {
        if (right - left <= cutoff) {
            Arrays.sort(arr, left, right); // Sequential base case
            return;
        }
        
        int mid = (left + right) / 2;
        
        // Fork
        CompletableFuture<Void> leftFuture = CompletableFuture.runAsync(() ->
            parallelMergeSort(arr, left, mid, cutoff));
        CompletableFuture<Void> rightFuture = CompletableFuture.runAsync(() ->
            parallelMergeSort(arr, mid, right, cutoff));
        
        // Join
        CompletableFuture.allOf(leftFuture, rightFuture).join();
        
        // Merge
        parallelMerge(arr, left, mid, right);
    }
    
    // Parallel merge using binary search (O(log n) span)
    public static <T extends Comparable<T>> void parallelMerge(
            T[] arr, int left, int mid, int right) {
        // If small enough, sequential merge
        if (right - left <= 1024) {
            T[] temp = Arrays.copyOfRange(arr, left, right);
            int i = 0, j = mid - left, k = left;
            while (i < mid - left && j < right - left) {
                if (temp[i].compareTo(temp[j]) <= 0) {
                    arr[k++] = temp[i++];
                } else {
                    arr[k++] = temp[j++];
                }
            }
            while (i < mid - left) arr[k++] = temp[i++];
            while (j < right - left) arr[k++] = temp[j++];
            return;
        }
        
        // Find median element of the longer half
        int n1 = mid - left, n2 = right - mid;
        if (n1 >= n2) {
            int i = (left + mid) / 2;
            // Binary search in right half
            int j = binarySearch(arr[i], arr, mid, right);
            
            // Recursively merge two halves in parallel
            int finalI = i;
            CompletableFuture<Void> leftMerge = CompletableFuture.runAsync(() ->
                parallelMerge(arr, left, finalI, j));
            CompletableFuture<Void> rightMerge = CompletableFuture.runAsync(() ->
                parallelMerge(arr, finalI, mid, right));
            CompletableFuture.allOf(leftMerge, rightMerge).join();
        } else {
            int j = (mid + right) / 2;
            int i = binarySearch(arr[j], arr, left, mid);
            
            int finalJ = j;
            CompletableFuture<Void> leftMerge = CompletableFuture.runAsync(() ->
                parallelMerge(arr, left, i, finalJ));
            CompletableFuture<Void> rightMerge = CompletableFuture.runAsync(() ->
                parallelMerge(arr, i, mid, right));
            CompletableFuture.allOf(leftMerge, rightMerge).join();
        }
    }
    
    private static <T extends Comparable<T>> int binarySearch(
            T key, T[] arr, int left, int right) {
        while (left < right) {
            int mid = (left + right) / 2;
            if (key.compareTo(arr[mid]) <= 0) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }
        return left;
    }
    
    // Analysis:
    // Sequential merge sort: T(n) = 2T(n/2) + O(n) → O(n log n)
    // Parallel merge sort:  T(n) = T(n/2) + O(log n) → O(log² n)
    //   (Both halves run in parallel, merge takes O(log n) span)
    // Work: O(n log n), Span: O(log² n)
    // Parallelism: O(n / log n)
    
    // Cutoff selection:
    // - Too small: excessive overhead from task creation
    // - Too large: not enough parallelism
    // - Empirical: 1000-10000 elements for sequential base
    // - Or: use number of processors × work-per-task budget
}
```

## 5. Fork-Join Deep Dive

The fork-join model (Java ForkJoinPool) is the foundation of Java parallel programming.

### Thread Pool Internals

```java
public class ForkJoinInternals {
    // ForkJoinPool architecture:
    // - Array of work queues (one per worker thread)
    // - Each queue is a Deque (double-ended queue)
    // - Worker pushes/pops from own queue (LIFO)
    // - Idle workers steal from other queues (FIFO)
    // - Uses CAS for lock-free operations
    
    // Work queue structure:
    // class WorkQueue {
    //     ForkJoinTask<?>[] array;   // circular array of tasks
    //     int base;                   // index for stealing (FIFO)
    //     int top;                    // index for push/pop (LIFO)
    //     volatile int scanState;     // for synchronization
    // }
    
    // Task structure:
    // class ForkJoinTask<V> {
    //     volatile int status;       // 0 = done, negative = running, positive = waiting
    //     // Subtypes:
    //     // RecursiveAction: fork-join without result
    //     // RecursiveTask<V>: fork-join with result
    //     // CountedCompleter: callback-based
    // }
}

// Fork-join best practices:
public class ForkJoinBestPractices {
    // 1. Use RecursiveTask for results, RecursiveAction for side effects
    // 2. Always join() AFTER fork(), never join() first
    // 3. Use threshold-based base cases to limit granularity
    // 4. Avoid blocking operations inside fork-join tasks
    // 5. Use invoke() for top-level, fork()/join() for subtasks
    // 6. Don't use synchronized inside tasks (prefer fork-join coordination)
    
    static class SumTask extends RecursiveTask<Long> {
        private static final int THRESHOLD = 10000;
        private final int[] array;
        private final int start, end;
        
        SumTask(int[] arr, int s, int e) { array = arr; start = s; end = e; }
        
        @Override
        protected Long compute() {
            if (end - start <= THRESHOLD) {
                long sum = 0;
                for (int i = start; i < end; i++) sum += array[i];
                return sum;
            }
            int mid = (start + end) / 2;
            SumTask left = new SumTask(array, start, mid);
            SumTask right = new SumTask(array, mid, end);
            left.fork();       // enqueue left
            long rightResult = right.compute(); // compute right directly
            long leftResult = left.join();      // wait for left
            return leftResult + rightResult;
        }
    }
    
    public static long sumArray(int[] arr) {
        return ForkJoinPool.commonPool().invoke(new SumTask(arr, 0, arr.length));
    }
}
```

### Work-Stealing Algorithm

```java
public class WorkStealing {
    // Work-stealing scheduling:
    // 
    // Each worker maintains its own deque of ready tasks
    // 
    // Push new task: add to top of own deque (O(1) amortized)
    // Pop task: remove from top of own deque (O(1))
    // 
    // When deque is empty:
    //   - Choose a random victim worker
    //   - Steal from BOTTOM of victim's deque
    //   - If victim's deque is also empty, try another victim
    //
    // Why steal from bottom?
    //   - Worker pushes/pops from top (LIFO) → good cache locality
    //   - Thief steals from bottom (FIFO) → steals largest/first tasks
    //   - LIFO for worker: deep tasks are hot in cache
    //   - FIFO for thief: shallow tasks have more work to do
    // 
    // Termination detection:
    //   - All workers are idle → check for tasks in other workers
    //   - If all workers idle and no tasks exist → termination
    //   - Uses a "steal counter" and "active worker" count
    
    // Work-stealing efficiency:
    // An O(W/S + log P) bound exists for work-stealing on P processors
    // Expected time: T(1)/P + O(S) — optimal for well-parallelized algorithms
    
    // Java ForkJoinPool implementation:
    // - Common pool: ForkJoinPool.commonPool()
    // - Default parallelism: Runtime.availableProcessors() - 1
    // - Custom pools created for specific use cases
    // - Pool size can be configured with -Djava.util.concurrent.ForkJoinPool.common.parallelism=N
    
    public static void main(String[] args) {
        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println("Available cores: " + cores);
        System.out.println("Common pool parallelism: " + 
            ForkJoinPool.getCommonPoolParallelism());
    }
}
```

## 6. Cilk+ Concepts

Cilk+ is an extension of C/C++ for parallel programming, and many concepts map to Java.

```java
public class CilkConcepts {
    // Cilk+ keywords mapped to Java:
    //
    // cilk_spawn f(args)   →  CompletableFuture.runAsync(() -> f(args))
    // cilk_sync             →  CompletableFuture.allOf(...).join()
    // cilk_for (int i...)   →  IntStream.range(...).parallel().forEach(...)
    // 
    // The Cilk+ runtime uses the same work-stealing algorithm as ForkJoinPool
    
    // Cilk+ The Work-First Principle:
    // "Don't pay for parallelism when not using it"
    // → ForkJoinPool is lazy: tasks don't create threads until needed
    // → When enough parallelism exists, work stealing kicks in
    
    // Cilk+ Reducer Hyperobject:
    // Thread-local reduction that's combined at the end
    // In Java: use ThreadLocal or Collector
    
    // Cilk+ Inlets and Aborts:
    // Advanced constructs for speculative parallelism
    // In Java: use CompletableFuture.anyOf() for racing tasks
    
    // Cilk+ Loop Parallelism:
    // #pragma cilk grainsize = 1000
    // cilk_for (int i = 0; i < n; i++)
    // Can we partition loops effectively?
    // → In Java: parallel streams handle this automatically
    
    // Example: Cilk-like parallel for loop
    public static void parallelFor(int start, int end, 
                                     IntConsumer body, int granularity) {
        if (end - start <= granularity) {
            for (int i = start; i < end; i++) body.accept(i);
            return;
        }
        int mid = (start + end) / 2;
        CompletableFuture<Void> left = CompletableFuture.runAsync(
            () -> parallelFor(start, mid, body, granularity));
        CompletableFuture<Void> right = CompletableFuture.runAsync(
            () -> parallelFor(mid, end, body, granularity));
        CompletableFuture.allOf(left, right).join();
    }
}
```

## 7. Practical Parallel Algorithm Design

```java
public class ParallelDesignPatterns {
    // 1. Divide and Conquer (fork-join)
    //   - Best for: sorting, searching, matrix multiply
    //   - Key: balanced decomposition, sequential cutoff
    
    // 2. Data Parallelism (map-reduce)
    //   - Best for: independent element processing
    //   - Key: stateless operations, associative reducer
    //   - Java: parallelStream.map().reduce()
    
    // 3. Pipeline Parallelism
    //   - Best for: streaming data, producer-consumer
    //   - Key: bounded queues between stages
    //   - Java: BlockingQueue, PipedStream
    
    // 4. Speculative Parallelism
    //   - Best for: search, branch-and-bound
    //   - Key: cancel failed speculations
    //   - Java: CompletableFuture.anyOf(), CancellationToken
    
    // 5. Lock-Free Data Structures
    //   - Best for: high-contention concurrent access
    //   - Key: CAS operations, hazard pointers
    //   - Java: AtomicReference, ConcurrentLinkedQueue
    
    // Choosing parallel granularity:
    // Optimal task size = overhead × (P - 1) / (P + 1) × critical_work
    // Empirical: task should take ~100x overhead of creating it
    // ForkJoinTask overhead: ~1μs → task should process ~100μs of work
}

// Benchmarking parallel performance:
public class ParallelBenchmark {
    private static final int SIZE = 100_000_000;
    private static final int[] data = new int[SIZE];
    static { Arrays.parallelSetAll(data, i -> i); }
    
    public static void main(String[] args) {
        // Sequential sum
        long t0 = System.nanoTime();
        long sum1 = 0;
        for (int v : data) sum1 += v;
        long tSeq = System.nanoTime() - t0;
        
        // Parallel sum (no warmup — for demonstration)
        t0 = System.nanoTime();
        long sum2 = Arrays.stream(data).parallel().sum();
        long tPar = System.nanoTime() - t0;
        
        double speedup = (double) tSeq / tPar;
        int cores = Runtime.getRuntime().availableProcessors();
        double efficiency = speedup / cores;
        
        System.out.printf("Sequential: %dms%n", tSeq / 1_000_000);
        System.out.printf("Parallel:   %dms%n", tPar / 1_000_000);
        System.out.printf("Speedup:    %.2fx (%.0f%% of linear)%n", speedup, efficiency * 100);
        System.out.printf("Cores: %d, Efficiency: %.0f%%%n", cores, efficiency * 100);
    }
}
```

The fundamental insight is that parallel algorithms expose parallelism through the work-span trade-off. The optimal number of processors is determined by the parallelism ratio W/S and overhead constants.
