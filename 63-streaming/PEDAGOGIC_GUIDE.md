# Pedagogic Guide - Apache Flink

## Learning Path

### Phase 1: Fundamentals
1. Streaming vs batch processing
2. Flink architecture
3. DataStream API basics

### Phase 2: Intermediate
1. Window functions
2. Time concepts and watermarks
3. State management

### Phase 3: Advanced
1. Exactly-once guarantees
2. SQL for streaming
3. Process functions

## Window Types

| Type | Description |
|------|-------------|
| Tumbling | Fixed size, non-overlapping |
| Sliding | Fixed size, overlapping |
| Session | Gap-based |
| Global | All elements |

## Time Concepts
- Processing Time: System clock
- Event Time: Actual occurrence time
- Watermark: Progress indicator

## Comparison with Spark
- Flink: Native streaming, lower latency
- Spark Streaming: Mini-batch, higher latency