# Performance Optimization for Apache Beam

## Fusion
Beam fuses adjacent ParDo transforms automatically. Use Reshuffle.via() to prevent fusion and enable parallelism between heavy stages.

## Parallelism
Use .withHintMatchesManyFiles() for TextIO reads. Reshuffle after skewed transforms. Set --maxNumWorkers for auto-scaling runners.

## Bundle Size
Control bundle size via pipeline options. Smaller bundles = lower latency, more overhead. Larger bundles = higher throughput, more memory.

## Serialization
Use efficient coders (Avro, Proto, custom). Avoid Java serialization. Register custom coders for better performance.
