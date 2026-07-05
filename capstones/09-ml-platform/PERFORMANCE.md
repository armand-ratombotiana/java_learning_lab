# Performance: ML Platform

## Throughput Targets
- Online prediction latency: < 50ms P99 (simple models), < 500ms (deep learning)
- Feature retrieval: < 5ms P99 from Redis
- Batch prediction throughput: 10k predictions/s per node
- Training job setup overhead: < 30s

## Bottlenecks
- **Model inference**: Large deep learning models need GPU. Use ONNX Runtime with CUDA.
- **Feature computation**: Complex feature engineering (joins, aggregations) dominates pipeline time.
- **Data loading**: I/O from S3/Database is often the bottleneck. Use columnar formats (Parquet) + predicate pushdown.
- **Model registry I/O**: Reading model artifacts from S3 adds latency. Cache model in local memory.

## Optimization Strategies
- Use ONNX Runtime with CUDA for GPU-accelerated inference
- Pre-fetch features asynchronously before prediction request
- Batching: accumulate requests, predict in batch on GPU
- Model quantization: FP16/INT8 for 2-4x speedup with minimal accuracy loss
- Feature store: use local cache (Caffeine) with TTL for hot features
- Model warm-up: run dummy prediction on load to initialize JIT/JNI
