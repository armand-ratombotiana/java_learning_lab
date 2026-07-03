# Visual Guide — Parallel Algorithms

## Fork/Join Tree
`
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
`
T = task, C = combine/join

## Work Stealing
`
Thread 1: [T1][T2][T3] → finishes T1, steals T6 from Thread 2
Thread 2: [T4][T5][T6] → finishes T4, T5
`
"@

wf "CODE_DEEP_DIVE.md" @"
# Code Deep Dive — Parallel Algorithms

## Parallel Quicksort
`java
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
`

## Parallel Matrix Multiplication
`java
public class MatrixMulTask extends RecursiveTask<int[][]> {
    protected int[][] compute() {
        if (n <= THRESHOLD) return sequentialMultiply();
        // split into submatrices, fork/join
    }
}
`
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
