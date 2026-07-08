# Apache Beam Internals

## Pipeline Execution Stages
1. Pipeline construction (applying transforms). 2. Pipeline translation (Runner API protos). 3. Optimization (fusion, combining). 4. Execution on runner. 5. Element processing through stages in bundles.

## Fusion Optimization
Adjacent ParDo transforms are fused into a single stage to avoid serialization/deserialization between them. Fusion can be prevented with Reshuffle for load balancing.

## Bundle Processing
Elements processed in bundles for efficiency. Checkpointing at bundle boundaries enables fault tolerance. Bundle size affects throughput and latency.

## Portability Architecture
SDK Harness (user code execution) <-> Fn API (protocol buffers) <-> Runner. This allows any SDK (Java, Python, Go) to work with any runner.
