# Refactoring Apache Beam Pipelines

## Composite Transforms
Before: Multiple inline transforms
After: Extract into composite PTransform for reuse

## Fusion Prevention
Before: Adjacent ParDos fused, no parallelism
After: Insert Reshuffle.via() between heavy transforms

## Schema-Based Processing
Before: Raw KV<String, String> with manual parsing
After: Use Beam Schema with Row type for type-safe processing
