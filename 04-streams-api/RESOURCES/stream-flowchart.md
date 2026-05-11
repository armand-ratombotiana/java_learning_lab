# Stream Operations Flowchart

## Pipeline Overview

```
[Source] --> [Intermediate Ops] --> [Terminal Op] --> [Result]
```

## Operation Types

### Intermediate Operations (Lazy)
Return a new Stream, enabling chaining.

| Operation | Description |
|-----------|-------------|
| `filter()` | Keep elements matching predicate |
| `map()` | Transform each element |
| `flatMap()` | Transform and flatten |
| `distinct()` | Remove duplicates |
| `sorted()` | Sort elements |
| `limit()` | Truncate to first n |
| `skip()` | Skip first n elements |

### Terminal Operations (Eager)
Produce a result or side-effect.

| Operation | Description |
|-----------|-------------|
| `collect()` | Aggregate to collection |
| `forEach()` | Apply action to each |
| `reduce()` | Combine into single value |
| `count()` | Count elements |
| `anyMatch()` | Any match predicate? |
| `allMatch()` | All match predicate? |
| `noneMatch()` | None match predicate? |
| `findFirst()` | Get first element |
| `min()/max()` | Find min/max |

## Execution Flow

```
List<String> → stream() → filter() → map() → collect()
                ↓           ↓         ↓
              Source    Intermediate Intermediate Terminal
```

## Parallel Streams
Use `parallelStream()` for multi-threaded processing on multi-core systems.