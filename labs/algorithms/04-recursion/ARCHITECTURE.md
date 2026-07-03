# Architecture — Recursion

## Java Ecosystem
- Stream API: Some operations recursive internally
- CompletableFuture: Recursive async composition
- ForkJoinPool: RecursiveTask/RecursiveAction for parallel divide-and-conquer
- Tree structures: File systems, DOM, ASTs

## Design Patterns Using Recursion
- Composite Pattern: Tree structures with same-type children
- Visitor Pattern: Recursive traversal
- Interpreter Pattern: Recursive grammar evaluation
- Iterator Pattern: Tree iterators use recursion internally
