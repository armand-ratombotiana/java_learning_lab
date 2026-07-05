# Debugging: ML Platform

## Common Issues

### Model performs well offline but poorly online
- Training-serving skew: feature computation differs
- Missing features at serving time (Redis lookup failure)
- Time drift: training data distribution differs from current data

### Prediction latency spikes
- Model too large for real-time serving (prune or quantize)
- Feature store lookup too slow (use local cache)
- GC pauses from large model in heap

### Training job fails
- Insufficient memory/data skew (increase Spark partitions)
- Feature pipeline didn't run (missing features)
- Training data corrupted (check schema validation)

### Model registry corruption
- Concurrent writes to same model version
- S3 consistency issues (read-after-write)
- Metadata database schema migration
