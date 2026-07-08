# Debugging Apache Flink

## Backpressure
Check web UI backpressure tab (localhost:8081); increase parallelism or optimize operator chains

## OOM Errors
Switch to RocksDB state backend; reduce state size with TTL; check managed memory fraction

## Checkpoint Failure
CheckpointTimeout — increase timeout; reduce state size; use unaligned checkpoints

## Watermark Not Advancing
Source not producing data; configure idle source timeout as fallback
