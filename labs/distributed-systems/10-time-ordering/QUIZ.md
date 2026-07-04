# Time and Ordering: Quiz

## Questions
1. What is the happens-before relationship?
2. How does a Lamport clock work?
3. What can vector clocks do that Lamport clocks cannot?
4. What is a Hybrid Logical Clock?
5. How does TrueTime achieve external consistency?
6. What is clock drift?
7. Why can't physical clocks determine causality?
8. What is a concurrent event?
9. How much metadata does a vector clock need?
10. What is the HLC physical component?

## Answers
1. A partial order defining which events causally influenced others
2. Single counter incremented on each event, takes max on receive
3. Detect concurrency between events
4. Combines physical time with logical counter for bounded drift
5. Returns clock interval [earliest, latest], waits for uncertainty to pass
6. Physical clock inaccuracies causing time differences
7. NTP can't perfectly synchronize clocks; drift causes errors
8. Two events where neither happens-before the other
9. O(N) where N is number of processes
10. Wall clock time (System.currentTimeMillis)
