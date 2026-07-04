# Time and Ordering: Interview Questions

## Q1: Explain the difference between Lamport clocks and vector clocks.
**A**: Lamport clocks are single counters that give a total order but can't detect concurrency. Vector clocks are arrays per process that can exactly determine causality and detect concurrent events.

## Q2: How does Google Spanner achieve external consistency?
**A**: TrueTime returns a time interval [earliest, latest] with bounded uncertainty ε. Spanner waits for ε (the uncertainty interval) to pass before making a transaction visible, ensuring all transactions see a consistent snapshot.

## Q3: What is a Hybrid Logical Clock?
**A**: HLC combines physical wall clock time with a logical component. It's bounded by physical time drift, stays close to wall clock, and can track causality. 16 bytes total, O(1) operations.

## Q4: How do vector clocks handle process churn?
**A**: Processes enter and leave, bloating vector clocks. Solutions: use version vectors (known process set), interval tree clocks (O(log N)), or periodically prune stale entries.

## Q5: When would you use Lamport vs Vector clocks?
**A**: Lamport: ordering only, minimal overhead (8 bytes), simple. Vector: causality tracking needed, conflict detection, O(N) overhead acceptable. HLC: best of both for most cases.
