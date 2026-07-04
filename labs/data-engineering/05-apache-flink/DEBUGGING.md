# Debugging

## Backpressure
- Flink Web UI shows backpressure in red for tasks
- Fix: Increase parallelism, optimize operators

## Watermark Not Advancing
- Check if timestamps are monotonic
- Verify valid timestamps in events

## Checkpoint Failures
- Increase timeout for large state
- Check logs for serialization issues
