# Flashcards: Event Streaming

Front: What is a partition in Kafka? | Back: An ordered, immutable sequence of records within a topic. Partitions enable parallelism and scalability.

Front: What is a consumer group? | Back: A set of consumers that coordinate to read partitions. Each partition is assigned to exactly one consumer in the group.

Front: What is ISR? | Back: In-Sync Replicas — replicas that are fully caught up with the partition leader. Only ISR replicas can become leader.

Front: What is log compaction? | Back: Retention policy that keeps only the latest value for each key, discarding older versions. Useful for state snapshots.

Front: What is a log segment? | Back: A file on disk containing a portion of a partition's data + index files. Segments are rolled based on size or time.

Front: What is consumer lag? | Back: The difference between the latest producer offset and the consumer's committed offset for a partition.
