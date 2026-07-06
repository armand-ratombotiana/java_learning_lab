# Mathematical Foundation of Locking

## Lock Contention
Contention probability for N threads competing for a single lock follows from queueing theory. With arrival rate λ and service rate μ, the probability of lock contention is ρ = λ/μ. When ρ > 1, the queue grows unbounded (threads pile up waiting for the lock).

## Amdahl's Law with Locking
When a fraction S of execution is serialized by a lock, maximum speedup is 1/(S + (1-S)/N). Even a 5% serialized section limits speedup to 20× regardless of core count. This is why reducing lock hold time is critical.

## CAS Retry Probability
For K threads performing CAS on the same variable, the expected number of retries before success approximates (K-1)/2 for the last thread to succeed. Total CAS operations across all threads is approximately K(K+1)/2. This quadratic scaling shows why CAS contention degrades with thread count.

## Fair Lock Wait Time
In a fair M/M/1 queue (Poisson arrivals, exponential service), the expected wait time for thread J is J/μ (where μ is service rate). The last of N threads waits N/μ on average. In an unfair lock, the variance is higher but the mean wait time is lower because barging threads can bypass the queue.

## StampedLock Validation Probability
If writes occur at rate λ_w and the optimistic read takes time t_r, the probability that validation fails is 1 - e^(-λ_w × t_r). For low write rates or fast reads, validation succeeds nearly always. For high write rates, optimistic reads degenerate to read lock overhead.

## ABA Problem Probability
In a CAS operation, the ABA problem occurs when the value changes A→B→A between a thread's read and CAS. For a 64-bit counter, the probability of ABA is negligible (hash collision probability). For pointer-based structures (e.g., Treiber stack), ABA is more likely and requires tagged references (AtomicStampedReference).
