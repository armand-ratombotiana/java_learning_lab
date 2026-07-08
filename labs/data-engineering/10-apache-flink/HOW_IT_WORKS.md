# How Apache Flink Works

1. Source operators read data from external systems (Kafka, files, sockets)
2. Data flows through transformation operators (map, filter, flatMap, keyBy)
3. Operators maintain state (counters, aggregations, caches) via state backends
4. Windows group events by time or count for aggregation
5. Sink operators write results to external systems
6. Checkpoint coordinator periodically triggers distributed snapshots
7. On failure, all tasks restart from the latest checkpoint
8. Watermarks track event-time progress for trigger decisions
