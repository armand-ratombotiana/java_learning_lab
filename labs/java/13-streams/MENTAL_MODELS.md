# Streams — Mental Models

## Model 1: The Factory Assembly Line

A stream is a factory assembly line. Items enter at one end (source), pass through workstations (intermediate operations), and arrive at a finished product area (terminal operation).

```
[Source] → [Filter] → [Map] → [Sort] → [Collect]
  Items    Keep good  Transform   Order    Package
```

The line runs only when the terminal operation is started (lazy evaluation). You can add workstations before starting the line, but nothing moves until the packaging station is activated.

## Model 2: The Iterator vs the Stream

- **Iterator**: You walk through a list and pick items manually (external iteration)
- **Stream**: You tell a conveyor belt system what to do, and it moves items automatically (internal iteration)

Iterator is like shopping with a basket — you walk aisles, pick items. Stream is like ordering online — you specify a filter ("only organic") and a mapper ("deliver to home") and let the system handle it.

## Model 3: The Water Pipe

A stream is a pipe with water flowing through it:
- `filter()` is a sieve that only lets certain particles through
- `map()` changes the water temperature or color
- `flatMap()` merges multiple pipes into one
- `limit()` caps the flow rate
- `distinct()` removes duplicates (like a carbon filter)
- `sorted()` arranges particles in order (requires seeing all, thus stateful)
- `forEach()` releases water into a drain (side effect)

The pipe only starts flowing when you open the faucet at the end (terminal operation).

## Model 4: SQL Queries

Stream pipelines are analogous to SQL:

```sql
SELECT name, COUNT(*)        -- .collect(groupingBy(name, counting()))
FROM customers               -- customers.stream()
WHERE status = 'active'      -- .filter(c -> c.status() == ACTIVE)
GROUP BY name                -- .collect(groupingBy(...))
HAVING COUNT(*) > 1          -- (filter after collect)
ORDER BY name;               -- .sorted()
```

## Model 5: The Parallel Factory

A parallel stream is a factory with multiple identical assembly lines:
- The source is split into batches (spliterator)
- Each batch goes to a separate line (ForkJoinPool thread)
- Workstations process batches independently
- Results from all lines are merged at the end

This parallelism requires stateless operations — if workstations share information (mutable state), the factory produces incorrect results.
