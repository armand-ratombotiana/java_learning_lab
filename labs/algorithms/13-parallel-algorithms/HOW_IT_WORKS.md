# How Parallel Algorithms Work

## Fork/Join Merge Sort
`java
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
`

## Parallel Stream
`java
int sum = numbers.parallelStream()
    .filter(n -> n % 2 == 0)
    .mapToInt(Integer::intValue)
    .sum();
`
"@

wf "INTERNALS.md" @"
# Parallel Algorithms — Internal Mechanics

## ForkJoinPool Structure
- Thread pool with work-stealing deque
- Each thread has its own deque of tasks
- When a thread's deque is empty, it steals from others
- Reduces contention compared to single work queue

## Custom RecursiveTask
`java
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
`
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
