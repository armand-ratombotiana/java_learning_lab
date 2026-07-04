# Flink Theory

## Time Semantics
- **Event Time**: When event occurred (source timestamp)
- **Processing Time**: When event is processed
- **Ingestion Time**: When event enters Flink

## State Management
- **Keyed State**: Per-key in keyed streams
- **Operator State**: Per-operator parallelism
- **Managed State**: Auto checkpoint/recovery

## Windows
- **Tumbling**: Fixed non-overlapping
- **Sliding**: Fixed overlapping
- **Session**: Inactivity gaps
