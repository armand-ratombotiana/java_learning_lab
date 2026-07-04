# Visual Guide to Pipeline Architecture

## Basic Pipeline
```
Input -> [Stage 1] -> [Stage 2] -> [Stage 3] -> Output
         Filter      Transform    Aggregate
```

## Branching Pipeline
```
              +--> [Branch A] --+
              |                 |
Input --------+                 +--> [Aggregate] -> Output
              |                 |
              +--> [Branch B] --+
```

## Error Handling Pipeline
```
Input -> [Stage 1] -> [Stage 2] -> [Stage 3] -> Output
              |            |            |
              v            v            v
         [Error Handler 1] [Err 2]   [Err 3]
              |            |            |
              +----> [Dead Letter Queue] <----+
```

## Java Stream Pipeline
```
orders.stream()                    // Source
    .filter(Order::isActive)       // Stage 1: Filter
    .map(orderMapper::toDto)       // Stage 2: Transform
    .sorted(byDate())              // Stage 3: Sort
    .limit(100)                    // Stage 4: Limit
    .collect(toList());            // Terminal: Collect
```

## Complex Pipeline with Feedback
```
         +-----------------------------+
         |                             |
         v    success                  |
[Validate] -> [Process] -> [Persist] -> [Success]
   |              |              |
   v              v              v
[Reject]     [Retry Queue]  [Error Handler]
   |              |              |
   v              v              v
  Exit          [Retry]      [DLQ]
```
