# Code Deep Dive: Apache Flink

See Java source files in src/main/java/com/dataeng/ten/ for complete implementations:

- SensorReadingProcessor.java: Full streaming pipeline with watermarks, keyed state, windowing
- SensorReading.java: POJO with event timestamp
- SensorReadingSource.java: Parallel source function generating sensor data
- AlarmResult.java / AggregateResult.java: Output types
- SensorAggregator.java: AggregateFunction for window computations

Key patterns:
```java
DataStream<SensorReading> stream = env.addSource(new KafkaSource<>())
    .assignTimestampsAndWatermarks(
        WatermarkStrategy.<SensorReading>forBoundedOutOfOrderness(Duration.ofSeconds(5))
            .withTimestampAssigner((r, ts) -> r.getTimestamp())
    );

stream.keyBy(SensorReading::getSensorId)
    .window(TumblingEventTimeWindows.of(Time.minutes(1)))
    .allowedLateness(Time.seconds(30))
    .aggregate(new SensorAggregator());
```
