# Code Deep Dive: Kafka Streams

## Clickstream Pipeline
```java
@Configuration
@EnableKafkaStreams
public class ClickstreamPipeline {
    @Bean
    public KStream<String, ClickEvent> clickStream(StreamsBuilder builder) {
        return builder.stream("clicks");
    }

    @Bean
    public KTable<String, Long> pageViews(KStream<String, ClickEvent> clicks) {
        return clicks.groupBy((k, v) -> v.getUrl())
            .count(Materialized.as("page-view-counts"));
    }
}
```

## Interactive Queries
```java
@RestController
public class StateQueryController {
    @GetMapping("/count/{url}")
    public Long getCount(@PathVariable String url) {
        ReadOnlyKeyValueStore<String, Long> store =
            streams.store(StoreQueryParameters.fromNameAndType(
                "page-view-counts", QueryableStoreTypes.keyValueStore()));
        return store.get(url);
    }
}
```
