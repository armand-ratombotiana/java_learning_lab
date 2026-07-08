# Why Apache Beam Exists

Each stream processing engine (Spark Streaming, Flink, Kafka Streams, Samza) has its own API. Porting pipelines between engines requires complete rewrites. Beam provides a unified API that runs on any engine, enabling vendor independence, migration flexibility, and reduced lock-in.
