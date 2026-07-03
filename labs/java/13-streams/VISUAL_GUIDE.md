# Streams — Visual Guide

## Pipeline Flow

```
Source ──► filter() ──► map() ──► sorted() ──► collect()
  │          │           │           │            │
  │     [Predicate]  [Function]  [Comparator]  [Collector]
  │          │           │           │            │
  ▼          ▼           ▼           ▼            ▼
[1,2,3]   [2,3]      ["2","3"]   ["2","3"]    ["2","3"]
[4,5,6]   [4,5,6]     ["4","5",   ["4","5",    ["4","5",
                        "6"]        "6"]          "6"]
```

## Lazy Evaluation Timing

```
LINE SETUP (no data flows):
  pipeline = source
    .filter(x > 0)           ← Just records the filter
    .map(toString)            ← Just records the map
    .sorted()                 ← Just records the sort

EXECUTION (triggered by terminal):
  pipeline.collect(toList())
    → Data flows through all stages at once
```

## Parallel Processing

```
Source: [1,2,3,4,5,6,7,8,9,10]
         ╱      │       ╲
    [1,2,3]  [4,5,6]  [7,8,9,10]
      │         │         │
   filter    filter    filter
      │         │         │
     map       map       map
      │         │         │
   collect   collect   collect
      ╲         │        ╱
       ╲        │       ╱
       ┌───────────────┐
       │     Merge     │
       └───────────────┘
```

## Stateful vs Stateless

```
Stateless operations (each element independent):
  filter  →  accept/reject
  map     →  transform
  flatMap →  expand

  [1,2,3] → filter → [1,3]
  (process starts immediately per element)

Stateful operations (must buffer):
  sorted  →  wait for all, sort, then emit
  limit   →  track count, stop when reached
  distinct→  track seen set

  [3,1,2] → sorted → [1,2,3]
  (cannot emit until all seen)
```

## Collectors Taxonomy

```
Collect to Collection:
  toList()        → List
  toSet()         → Set
  toCollection()  → Your custom collection

Group/Aggregate:
  groupingBy()    → Map<K, List<V>>
  partitioningBy() → Map<Boolean, List<V>>
  counting()      → Long
  summingInt()    → Integer
  averagingDouble() → Double

String:
  joining(", ")   → String

Custom:
  collectingAndThen() → Transform result
  teeing() (12+)    → Merge two collectors
```
