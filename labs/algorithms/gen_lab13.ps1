$labDir = "C:\Users\jratombo-adm\Desktop\java_learning_lab\labs\algorithms\13-parallel-algorithms"

function wf($name, $content) {
    Set-Content -Path (Join-Path $labDir $name) -Value $content
}

wf "README.md" @"
# Parallel Algorithms — Overview

Covers Fork/Join framework, parallel sorting, and concurrent algorithms in Java.

## Learning Objectives
- Understand parallel algorithm design principles
- Implement Fork/Join tasks using RecursiveAction and RecursiveTask
- Apply parallel sorting algorithms
- Analyze speedup, Amdahl's Law, and scalability

## Prerequisites
- Divide-and-conquer fundamentals
- Basic concurrency concepts (threads, synchronization)
- Java 8+ Stream API basics

## Estimated Time
- **Total**: 5–6 hours
"@

wf "THEORY.md" @"
# Parallel Algorithms — Theoretical Foundation

## Parallelism Models

### Data Parallelism
Same operation on different data elements (SIMD)

### Task Parallelism
Different operations on same or different data (MIMD)

## Fork/Join Model
- Fork: Split task into subtasks
- Join: Wait for subtasks and combine results
- Work stealing: Idle threads steal work from busy threads

## Key Metrics
- **Speedup**: S(p) = T(1) / T(p)
- **Efficiency**: E(p) = S(p) / p
- **Amdahl's Law**: Maximum speedup = 1 / (1 - P + P/p)
  where P = parallelizable fraction

## Granularity
- Too coarse: Poor load balancing
- Too fine: Overhead dominates computation
- Sweet spot: Task size 10μs-100μs
"@

wf "WHY_IT_EXISTS.md" @"
# Why Parallel Algorithms Exist

Hardware has shifted to multi-core processors (Moore's Law now about core count, not clock speed). Parallel algorithms are necessary to utilize modern hardware and achieve performance scaling.
"@

wf "WHY_IT_MATTERS.md" @"
# Why Parallel Algorithms Matter

- Modern Hardware: CPUs have 4-64+ cores; parallelism is essential
- Big Data: Processing terabytes of data requires parallel algorithms
- Real-Time Systems: Parallelism enables meeting performance guarantees
- Cloud Computing: Distributed parallel algorithms at scale
- Energy Efficiency: Parallel algorithms can be more power-efficient than faster sequential ones
"@

wf "HISTORY.md" @"
# History of Parallel Algorithms

- 1960s: Parallel computing concepts (ILLIAC IV)
- 1980s: PRAM model for parallel algorithms
- 1990s: MPI and OpenMP standards
- 2004: Multi-core era begins (desktop CPUs went multi-core)
- 2008: Java Fork/Join framework introduced (Java 7)
- 2014: Java 8 Stream API with parallel streams
- 2020s+: GPU computing (CUDA, OpenCL) for massively parallel algorithms
"@

wf "MENTAL_MODELS.md" @"
# Mental Models

## Fork/Join — "Divide the Work"
Like a manager who breaks a project into tasks, assigns them to workers, and when workers finish, combines the results.

## Work Stealing — "Help Busy Coworkers"
You finish your work and look for busy coworkers to help. This balances the load naturally.

## Amdahl's Law — "The Unavoidable Serial Part"
No matter how many workers you add, the serial part of the work limits speedup. Like a parade: adding more cars doesn't make the lead car go faster.
"@

wf "HOW_IT_WORKS.md" @"
# How Parallel Algorithms Work

## Fork/Join Merge Sort
```java
class MergeSortTask extends RecursiveAction {
    private int[] arr, aux;
    private int lo, hi;
    private static final int THRESHOLD = 1000;

    MergeSortTask(int[] arr, int[] aux, int lo, int hi) {
        this.arr = arr; this.aux = aux; this.lo = lo; this.hi = hi;
    }

    protected void compute() {
        if (hi - lo <= THRESHOLD) {
            Arrays.sort(arr, lo, hi);
            return;
        }
        int mid = lo + (hi - lo) / 2;
        MergeSortTask left = new MergeSortTask(arr, aux, lo, mid);
        MergeSortTask right = new MergeSortTask(arr, aux, mid, hi);
        invokeAll(left, right);
        merge(arr, aux, lo, mid, hi);
    }
}
```

## Parallel Stream
```java
int sum = numbers.parallelStream()
    .filter(n -> n % 2 == 0)
    .mapToInt(Integer::intValue)
    .sum();
```
"@

wf "INTERNALS.md" @"
# Parallel Algorithms — Internal Mechanics

## ForkJoinPool Structure
- Thread pool with work-stealing deque
- Each thread has its own deque of tasks
- When a thread's deque is empty, it steals from others
- Reduces contention compared to single work queue

## Custom RecursiveTask
```java
public class SumTask extends RecursiveTask<Long> {
    private final int[] arr;
    private final int lo, hi;
    private static final int THRESHOLD = 10_000;

    public SumTask(int[] arr, int lo, int hi) {
        this.arr = arr; this.lo = lo; this.hi = hi;
    }

    @Override
    protected Long compute() {
        if (hi - lo <= THRESHOLD) {
            long sum = 0;
            for (int i = lo; i < hi; i++) sum += arr[i];
            return sum;
        }
        int mid = lo + (hi - lo) / 2;
        SumTask left = new SumTask(arr, lo, mid);
        SumTask right = new SumTask(arr, mid, hi);
        left.fork();
        long rightResult = right.compute();
        long leftResult = left.join();
        return leftResult + rightResult;
    }
}

// Usage:
ForkJoinPool pool = ForkJoinPool.commonPool();
long result = pool.invoke(new SumTask(arr, 0, arr.length));
```
"@

wf "MATH_FOUNDATION.md" @"
# Math Foundation for Parallel Algorithms

## Amdahl's Law
Maximum speedup S(p) = 1 / (1 - P + P/p)
- P = fraction that can be parallelized
- p = number of processors
- As p → ∞, S(p) → 1 / (1 - P)

### Example: 90% parallelizable
- 2 cores: S = 1 / (0.1 + 0.9/2) = 1.82x
- 4 cores: S = 1 / (0.1 + 0.9/4) = 3.08x
- 8 cores: S = 1 / (0.1 + 0.9/8) = 4.71x
- ∞ cores: S = 10x (limited by 10% serial code)

## Gustafson's Law
Scaled speedup: S(p) = p + (1-p) × s (where s = serial fraction)
More optimistic than Amdahl — assumes problem size scales with processors.

## Work and Span
- Work = total operations (T₁)
- Span = longest sequential path (T∞)
- Parallelism = Work / Span
"@

wf "VISUAL_GUIDE.md" @"
# Visual Guide — Parallel Algorithms

## Fork/Join Tree
```
        Problem (size=8)
       /              \
  Task (4)          Task (4)
   /    \            /    \
T(2)  T(2)        T(2)  T(2)
 / \   / \         / \   / \
1  1  1  1        1  1  1  1
 \ /   \ /         \ /   \ /
  C    C            C    C
   \    /            \    /
    C               C
        \        /
           C
```
T = task, C = combine/join

## Work Stealing
```
Thread 1: [T1][T2][T3] → finishes T1, steals T6 from Thread 2
Thread 2: [T4][T5][T6] → finishes T4, T5
```
"@

wf "CODE_DEEP_DIVE.md" @"
# Code Deep Dive — Parallel Algorithms

## Parallel Quicksort
```java
public class ParallelQuickSort extends RecursiveAction {
    private final int[] arr;
    private final int lo, hi;

    public ParallelQuickSort(int[] arr, int lo, int hi) {
        this.arr = arr; this.lo = lo; this.hi = hi;
    }

    protected void compute() {
        if (hi - lo < 1000) {
            Arrays.sort(arr, lo, hi + 1);
            return;
        }
        int pivot = partition(arr, lo, hi);
        ParallelQuickSort left = new ParallelQuickSort(arr, lo, pivot - 1);
        ParallelQuickSort right = new ParallelQuickSort(arr, pivot + 1, hi);
        invokeAll(left, right);
    }

    private int partition(int[] arr, int lo, int hi) {
        int pivot = arr[hi];
        int i = lo;
        for (int j = lo; j < hi; j++)
            if (arr[j] <= pivot) swap(arr, i++, j);
        swap(arr, i, hi);
        return i;
    }
}
```

## Parallel Matrix Multiplication
```java
public class MatrixMulTask extends RecursiveTask<int[][]> {
    protected int[][] compute() {
        if (n <= THRESHOLD) return sequentialMultiply();
        // split into submatrices, fork/join
    }
}
```
"@

wf "STEP_BY_STEP.md" @"
# Step-by-Step — Parallel Algorithms

## Implementing Fork/Join
1. Extend RecursiveAction (void) or RecursiveTask<V> (return value)
2. define compute():
   a. If problem is small enough, solve sequentially
   b. Otherwise, split into subproblems
   c. Create subtasks, fork() them
   d. compute() remaining work directly
   e. join() to get results
3. Create ForkJoinPool
4. invoke() the root task

## Best Practices
- Set threshold by benchmarking (target: 100-10,000 elements)
- Avoid blocking operations inside fork/join tasks
- Use common pool for CPU-bound tasks
- Don't fork tasks that are too small (overhead dominates)
- Consider using parallel streams for simpler cases
"@

wf "COMMON_MISTAKES.md" @"
# Common Mistakes

- Forking all subtasks instead of computing one directly — doubles thread usage
- Threshold too small → overhead from task creation dominates
- Threshold too large → insufficient parallelism
- Shared mutable state without synchronization
- Using blocking I/O inside ForkJoinTask
- Not using ForkJoinPool.invoke() — tasks not submitted correctly
- deadlock from improper join ordering
"@

wf "DEBUGGING.md" @"
# Debugging — Parallel Algorithms

## Log Thread Information
```java
System.out.println(Thread.currentThread() + " processing [" + lo + "," + hi + ")");
```

## Verify Correctness
```java
int[] sequential = arr.clone();
Arrays.sort(sequential);

int[] parallel = arr.clone();
ForkJoinPool.commonPool().invoke(new ParallelSortTask(parallel, 0, parallel.length));

assert Arrays.equals(sequential, parallel) : "Sort results differ!";
```

## Monitor Pool Activity
```java
ForkJoinPool pool = ForkJoinPool.commonPool();
System.out.printf("Active: %d, Steals: %d%n", 
    pool.getActiveThreadCount(), pool.getStealCount());
```
"@

wf "REFACTORING.md" @"
# Refactoring — Parallel Algorithms

## Sequential → Parallel
```java
// Sequential
int sum = arr.stream().mapToInt(Integer::intValue).sum();

// Parallel (one line change)
int sum = arr.parallelStream().mapToInt(Integer::intValue).sum();
```

## Custom threshold tuning
```java
// Test different thresholds
for (int t : new int[]{100, 500, 1000, 5000, 10000}) {
    threshold = t;
    // benchmark
}
```
"@

wf "PERFORMANCE.md" @"
# Performance — Parallel Algorithms

## Merge Sort Benchmarks (n=10⁷)

| Cores | Sequential | Fork/Join | Speedup |
|-------|-----------|-----------|---------|
| 1 | 1.2s | 1.3s | 0.92x |
| 2 | 1.2s | 0.7s | 1.7x |
| 4 | 1.2s | 0.38s | 3.2x |
| 8 | 1.2s | 0.22s | 5.5x |

## When to Parallelize
- Large datasets (n > 100,000)
- CPU-bound computations
- Independent subproblems
- Low synchronization overhead
- Avoid for I/O-bound tasks (use async I/O instead)
"@

wf "SECURITY.md" @"
# Security — Parallel Algorithms

- Thread exhaustion: Creating too many threads can DoS the system
- Shared state: Race conditions can lead to incorrect results
- Data races: Unsynchronized mutable state is undefined behavior
- Deadlock: Improper fork/join ordering can cause deadlocks
- Resource leak: Not shutting down ForkJoinPool can prevent GC
- Mitigation: Use common pool, immutable data, proper synchronization
"@

wf "ARCHITECTURE.md" @"
# Architecture — Parallel Algorithms

## Java Parallelism Options
| Approach | When to Use |
|----------|-------------|
| Parallel streams | Simple data-parallel operations |
| Fork/Join | Divide-and-conquer parallelism |
| CompletableFuture | Async composition with parallelism |
| ExecutorService | Custom thread pool management |
| Phaser/CyclicBarrier | Multi-phase parallel computation |

## Real-World Systems
- Big Data: Apache Spark (distributed DAG parallelism)
- Scientific Computing: MPI for HPC
- Image Processing: Parallel pixel operations
- Machine Learning: Parallel gradient computation
- Databases: Parallel query execution
"@

wf "EXERCISES.md" @"
# Exercises — Parallel Algorithms

## Beginner
1. Compute sum of array using ForkJoinPool
2. Implement parallel max element finder
3. Use parallelStream to filter and map
4. Implement parallel Fibonacci (not efficient, but educational)

## Intermediate
5. Implement parallel Merge Sort
6. Implement parallel Quick Sort
7. Parallel matrix multiplication
8. Implement parallel word count on a large text file

## Advanced
9. Implement parallel breadth-first search
10. Implement parallel prefix sum (scan)
11. Compare performance of different thresholds
12. Implement parallel closest pair of points
"@

wf "QUIZ.md" @"
# Quiz — Parallel Algorithms

1. What is Amdahl's Law and what does it teach us?
2. What is work stealing in Fork/Join?
3. Why should you compute() one subtask directly instead of forking both?
4. What is the difference between RecursiveAction and RecursiveTask?
5. When should you NOT use parallel algorithms?
6. What does Gustafson's Law address that Amdahl's does not?
"@

wf "FLASHCARDS.md" @"
# Flashcards

- Q: Fork/Join base classes? → A: RecursiveAction (void) and RecursiveTask<V>
- Q: Amdahl's Law? → A: Speedup limited by serial portion: 1/(1-P+P/p)
- Q: Work stealing? → A: Idle threads steal from busy threads' deques
- Q: Parallel stream vs sequential? → A: Change stream() to parallelStream()
- Q: ForkJoinPool default? → A: Common pool = #CPUs - 1
- Q: When NOT to parallelize? → A: Small datasets, I/O-bound, shared mutable state
"@

wf "INTERVIEW.md" @"
# Interview Questions

1. "How does Fork/Join work internally?" — Work stealing algorithm
2. "What is Amdahl's Law? Give an example." — Understanding limits of parallelism
3. "Design a parallel algorithm for X." — Divide and conquer approach
4. "Compare parallel streams vs Fork/Join." — Ease of use vs control
5. "How would you sort 1TB of data in parallel?" — External + parallel sorting
6. "Race conditions in parallel code — how to avoid?" — Immutability, synchronization
"@

wf "REFLECTION.md" @"
# Reflection

- How does Amdahl's Law affect your algorithm design decisions?
- When is parallelism not worth the complexity?
- How does workload granularity affect parallel performance?
- What types of problems are naturally parallelizable?
- How do modern multi-core architectures affect algorithm design?
"@

wf "REFERENCES.md" @"
# References

- Javadoc: java.util.concurrent.RecursiveAction, RecursiveTask
- Goetz, B. "Java Concurrency in Practice"
- Herlihy & Shavit "The Art of Multiprocessor Programming"
- CLRS, Chapter 27 (Multithreaded Algorithms)
- Oracle Tutorial: Fork/Join
"@

Write-Host "13-parallel-algorithms: All 24 files created"
