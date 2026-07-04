# Common Mistakes

1. **Wrong Time Characteristic**: Using ProcessingTime for event-time logic
2. **No Watermark Strategy**: Missing assignTimestampsAndWatermarks
3. **Wrong State Backend**: JVM heap OOM for large state
4. **No Checkpointing**: Missing fault tolerance config
