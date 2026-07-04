# Refactoring Flink Applications

## Before: Monolithic ProcessFunction
```java
stream.process(new MyComplexProcessFunction() {
    // 500 lines
});
```

## After: Modular Operators
```java
DataStream<Raw> raw = env.addSource(source);
DataStream<Parsed> parsed = raw.map(new Parser());
DataStream<Enriched> enriched = parsed.keyBy(...).process(new Enricher());
DataStream<Output> result = enriched.window(...).aggregate(new Aggregator());
```

## Before: Hardcoded Config
```java
stream.window(TumblingProcessingTimeWindows.of(Time.minutes(5)));
```

## After: Parameterized
```java
@Value("${window.size.minutes:5}")
private int windowSize;
stream.window(TumblingProcessingTimeWindows.of(Time.of(windowSize, TimeUnit.MINUTES)));
```
