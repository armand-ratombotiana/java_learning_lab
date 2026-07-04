# Time and Ordering: Reflection

## Key Insights
- Logical clocks are essential for causality in distributed systems
- Vector clocks provide complete causality information at O(N) cost
- HLC is often the best practical choice (bounded, cheap, causal)
- Physical clock synchronization is harder than most engineers realize

## Questions
1. Are you relying on physical time for ordering guarantees?
2. Could causality tracking improve your system's correctness?
3. What's your tolerance for clock drift?
4. Would HLC solve your ordering needs?

## Personal Notes
- Lamport's 1978 paper is still one of the most relevant distributed systems papers
- TrueTime is elegant but requires specialized hardware
- In practice, most systems can work with monotonic IDs and HLC
