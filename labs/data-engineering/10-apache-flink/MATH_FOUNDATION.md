# Math Foundation for Apache Flink

## Latency
```
ProcessingLatency = ProcessingTime - EventTime
WatermarkLatency = CurrentWatermark - CurrentEventTime
EndToEndLatency = IngestionTime - ConsumptionTime
```

## Throughput
```
Throughput = RecordsProcessed / Time
Parallelism = Tasks / Slots
MaxThroughput = Parallelism * ThroughputPerSlot
```

## Watermark Computation
```
Watermark(t) = max(event_time_seen) - max_out_of_orderness
Idle sources need watermark idle timeout to avoid stalling
```

## Checkpoint Size
```
CheckpointSize = SUM(state_backend_overhead + operator_state)
Duration = SyncDuration + AsyncDuration
RecoveryTime = LastCheckpointRestoreTime + ReprocessingTime
```
