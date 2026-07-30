# MOCK_INTERVIEW — Stream Collectors

## Scenario
Given a list of log entries (timestamp, level, message), build a summary with count per level and the most recent message for each level.

## Interviewer Notes
- Candidate should use `groupingBy` with downstream collectors
- `maxBy(Comparator)` for the most recent message
- `teeing` can combine count + max in one pass

## Expected Solution Sketch
```java
Map<Level, Summary> summary = logs.stream()
    .collect(groupingBy(Log::level,
        teeing(counting(),
               maxBy(Comparator.comparing(Log::timestamp)),
               (cnt, max) -> new Summary(cnt, max.map(Log::message).orElse("")))));
```

## Follow‑Up
- How would you handle a very large stream (millions of entries)?
- What if `Log` is a record — any collector benefits?
