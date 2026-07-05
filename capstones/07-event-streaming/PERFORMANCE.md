# Performance: Event Streaming

## Throughput Targets
- Single broker: 100 MB/s write, 200 MB/s read (sequential)
- P99 produce latency (acks=1): < 5ms
- P99 produce latency (acks=all): < 20ms (within same DC)
- Consumer fetch latency: < 5ms

## Bottlenecks
- **Disk IO**: Sequential writes are fast; random reads (catch-up consumers) are slower. Use more partitions.
- **Page Cache**: Kafka relies heavily on OS page cache. Allocate 25-50% of RAM to page cache.
- **Network**: Throughput limited by NIC bandwidth (10Gbps vs 25Gbps).
- **GC**: Large heaps cause long GC pauses. Use G1GC, keep heap < 32GB.
- **File handles**: Each segment uses 3 file handles. Monitor ulimit.

## Optimization Strategies
- Use multiple disks (JBOD) for data directories
- Striped produce across partitions for parallelism
- Tune `batch.size` and `linger.ms` for throughput vs latency
- Use compression (gzip, lz4, zstd) on producer side
- Isolate compacted and non-compacted topics on different brokers
- Tune OS: dirty_ratio, dirty_background_ratio, swappiness=1
