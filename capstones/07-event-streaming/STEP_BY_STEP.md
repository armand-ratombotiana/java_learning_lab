# Step by Step: Event Streaming

## Producing a Record

1. Producer calls `producer.send(new ProducerRecord<>("orders", key, value))`
2. Producer computes partition: `Utils.toPositive(Utils.murmur2(key)) % numPartitions`
3. Producer requests metadata (if not cached): which broker is leader for partition 0 of "orders"
4. Producer serializes record, batches with other pending records to same partition
5. Producer accumulates batch up to `batch.size` or `linger.ms` timeout
6. Producer sends `ProduceRequest(acks=all, timeout=5000, data=[topic=orders, partition=0, records=batch])`
7. Leader appends batch to active segment at offset 42
8. Leader updates ISR high water mark (HWM = 43)
9. Follower 1 fetches records after offset 41, appends, responds with current offset 43
10. Follower 2 fetches records after offset 41, appends, responds with current offset 43
11. Leader waits for all ISR to acknowledge (if acks=all)
12. Leader responds to producer with `RecordMetadata(offset=42, partition=0)`
13. Producer callback invoked (success)
