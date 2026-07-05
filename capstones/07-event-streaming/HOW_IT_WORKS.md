# How It Works: Event Streaming

1. Producer connects to any broker, requests metadata (topic -> partition -> leader mappings)
2. Producer sends record to partition leader (hash(key) % num_partitions, or round-robin if no key)
3. Leader appends record to its log segment (sequential write to disk)
4. Follower replicas fetch records from leader (pull replication)
5. Follower acknowledges when record is written (min.insync.replicas controls ACK level)
6. Leader responds to producer with offset (acks=all -> wait for all in-sync replicas)
7. Consumer sends fetch request to partition leader with offset
8. Leader reads from log segments starting at offset, returns records up to maxBytes
9. Consumer commits offset (stored in __consumer_offsets topic)
10. On rebalance, partition assignment is redistributed within consumer group
