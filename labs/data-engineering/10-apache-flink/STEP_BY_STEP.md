# Step-by-Step: Working with Apache Flink

1. Download Flink; start cluster with ./bin/start-cluster.sh
2. Configure Kafka source connector with deserialization schema
3. Assign timestamps and watermark strategy for event-time processing
4. Apply transformations: map, filter, flatMap, keyBy, window
5. Use KeyedState (ValueState, ListState, MapState) for stateful operations
6. Configure checkpoint interval and state backend (RocksDB for large state)
7. Write sink to Kafka, JDBC, Elasticsearch, or file system
8. Execute: env.execute('job-name'); monitor via web UI (localhost:8081)
