# Debugging: Event Streaming

## Common Issues

### Producer request timeout
- Check if partition leader is available (leader election may be stuck)
- Network connectivity between producer and broker
- acks=all may wait for replicas that are offline
- min.insync.replicas not met

### Consumer rebalancing too frequently
- session.timeout.ms too low (consumer heartbeats timing out)
- max.poll.interval.ms too low (consumer not processing fast enough)
- Network partitions causing coordinator disconnection

### High consumer lag
- Insufficient consumer parallelism (not enough partitions)
- Processing bottleneck (per-record processing too slow)
- Fetch request configuration too conservative (fetch.max.bytes too low)

### Disk space filling up
- Retention policy not configured (log.retention.hours)
- Compaction not enabled on compacted topics
- Produce rate exceeds retention-based deletion rate
