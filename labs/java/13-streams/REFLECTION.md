# Reflection — Java Streams

## Why This Lab Matters
Streams changed how Java developers write data-processing logic, shifting from imperative loops to declarative, potentially parallel pipelines.

## What I Learned
- Lazy evaluation enables efficient pipelines
- Parallel streams can speed up computation but require careful design
- Collectors abstract complex reduction patterns

## Questions I Still Have
- How do custom Spliterators improve parallel performance?
- Will structured concurrency in Java 21+ change how parallel streams work?

## Personal Application
- Replace legacy for-loops in data processing with streams
- Use `groupingBy` for reporting/analytics queries
- Adopt `toUnmodifiableList()` for defensive returns

## Key Takeaways
1. Streams are about *what* not *how*
2. Prefer primitive streams for numeric operations
3. Debug with `peek()` during development, remove in production
4. Parallelism is not free — benchmark before adopting
