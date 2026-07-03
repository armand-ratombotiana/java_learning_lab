# Architecture — Java Streams

## Stream Pipeline Architecture
```
Source → Intermediate Op(s) → Terminal Op
         (lazy, build stages)   (eager, produce result)
```

## Class Hierarchy
```
Stream<T> (interface)
├── IntStream
├── LongStream
└── DoubleStream
```

## Key Internal Components
| Component | Role |
|-----------|------|
| `Spliterator` | Splits source for traversal/partitioning |
| `Sink` | Per-element consumer in pipeline stages |
| `PipelineHelper` | Links stages and builds evaluation graph |
| `TerminalOp` | Executes the pipeline and produces output |

## Reference Pipeline Classes
- `ReferencePipeline.Head` — source stage
- `ReferencePipeline.StatelessOp` — stateless intermediate op (filter, map)
- `ReferencePipeline.StatefulOp` — stateful intermediate op (sorted, distinct)

## Fork/Join in Parallel Streams
Parallel streams use the common `ForkJoinPool`. Each stage's `Sink` is evaluated in parallel across split partitions.

## Collector Architecture
```
Supplier → (accumulator → combiner) → finisher
```

## Design Patterns
- **Builder pattern** for pipeline construction (chaining)
- **Visitor pattern** in terminal operation evaluation
- **Strategy pattern** in parallel vs sequential execution
