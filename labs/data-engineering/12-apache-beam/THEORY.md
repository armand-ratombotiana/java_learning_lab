# Apache Beam Theory

## Pipeline Model
Pipeline = DAG of PTransforms applied to PCollections. PCollection may be bounded (batch file) or unbounded (Kafka stream). Transforms include ParDo (element-wise), GroupByKey (grouping), Combine (aggregation), Flatten (merge), Partition (split).

## Windowing
Fixed: fixed-size non-overlapping windows. Sliding: fixed-size overlapping windows. Sessions: dynamic windows based on inactivity gaps. Global: single window (default for batch). Windowing is required for unbounded PCollections.

## Triggers
AfterWatermark: fire when watermark passes window end. AfterProcessingTime: fire after wall-clock delay. AfterPane: fire after N elements. Repeatedly: repeat trigger. AfterAny/AfterAll: composition. Default: watermark with early firings.

## Runners
DirectRunner: local execution for testing. FlinkRunner: true streaming, best for production streaming. SparkRunner: micro-batch. DataflowRunner: managed service with auto-scaling. SamzaRunner: Yahoo's stream processor.
