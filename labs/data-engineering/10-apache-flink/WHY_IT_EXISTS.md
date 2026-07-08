# Why Apache Flink Exists

Batch processing (MapReduce, Spark Streaming micro-batches) trades latency for throughput. Real-time applications require true stream processing with sub-second latency and exactly-once semantics. Flink was designed as a streaming-first engine, treating batch as a special case of streaming — enabling real-time fraud detection, live dashboards, streaming ETL, and event-driven applications.
