# How Apache Beam Works

1. Construct pipeline by applying PTransforms to PCollections
2. Pipeline is a DAG of transforms with explicit inputs and outputs
3. Runner translates pipeline to its native execution plan
4. Runner executes transforms in topological order
5. Elements flow through transforms in bundles (micro-batches)
6. Windows group elements by time for unbounded data
7. Triggers decide when to emit pane results
8. Stateful DoFn maintains per-key state across elements
9. Checkpointing enables fault tolerance for streaming pipelines
10. Metrics emitted for monitoring and debugging
