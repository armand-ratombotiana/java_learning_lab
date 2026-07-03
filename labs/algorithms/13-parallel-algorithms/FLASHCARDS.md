# Flashcards

- Q: Fork/Join base classes? → A: RecursiveAction (void) and RecursiveTask<V>
- Q: Amdahl's Law? → A: Speedup limited by serial portion: 1/(1-P+P/p)
- Q: Work stealing? → A: Idle threads steal from busy threads' deques
- Q: Parallel stream vs sequential? → A: Change stream() to parallelStream()
- Q: ForkJoinPool default? → A: Common pool = #CPUs - 1
- Q: When NOT to parallelize? → A: Small datasets, I/O-bound, shared mutable state
