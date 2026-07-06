# Mathematical Foundation of Threading

## Amdahl's Law
Speedup = 1 / (1 - P) + P/N, where P is the parallel portion of the workload and N is the number of processors. For a workload that is 90% parallel, max speedup on 8 cores is 1 / (0.1 + 0.9/8) = 5.7x. This explains why adding more threads eventually provides diminishing returns — the serial portion becomes the bottleneck.

## Little's Law in Thread Pools
L = λW: The average number of tasks in the system (L) equals the arrival rate (λ) times the average wait time (W). For a thread pool with capacity C and arrival rate λ, the queue length grows as λ × W. If λ exceeds the service rate μ, the queue grows without bound. ThreadPoolExecutor's bounded queue prevents unbounded growth.

## Work-Stealing Efficiency
In ForkJoinPool, the probability that a steal is successful depends on load imbalance. For a task that splits into subtasks of size S and S' where S ≠ S', the imbalance ratio is max(S,S')/min(S,S'). With random victim selection, the expected number of steals before finding work is n/(n - busy), where n is the number of workers.

## ForkJoin Threshold Math
For an array of size N with parallelism P, the optimal threshold T satisfies: T = N / P × k, where k accounts for task creation overhead. Too small a threshold (T < 1000) causes overhead to dominate. Too large (T > N/P) leaves workers idle.

## CompletableFuture Completion Order
The probability that a given future in an allOf set is the last to complete is uniform (1/N). The expected time for allOf to complete for N independent futures with exponential completion times is H_N × μ (harmonic mean). For anyOf, the expected time is μ/N.

## StructuredTaskScope Cancellation Overhead
Cancelling k of N subtasks requires O(k) time. The scope's shutdown mechanism issues `cancel(true)` on each remaining future, which interrupts the thread. The overhead is proportional to the number of cancellations, not the number of total subtasks.
