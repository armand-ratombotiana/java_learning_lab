# Streaming Projects - Module 63

This module covers Apache Flink, Storm, and real-time processing for streaming data.

## Mini-Project: Flink Word Count Stream (2-4 hours)

### Overview
Build a real-time word counting application using Apache Flink that processes streaming text data.

### Project Structure
```
flink-streaming-demo/
├── src/main/java/com/learning/streaming/
│   ├── WordCountJob.java
│   ├── source/SocketTextSource.java
│   └── sink/PrintSink.java
├── pom.xml
└── run.sh
```

### Implementation
```java
// pom.xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.learning</groupId>
    <artifactId>flink-streaming-demo</artifactId>
    <version>1.0.0</version>
    
    <properties>
        <java.version>17</java.version>
        <flink.version>1.18.1</flink.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.apache.flink</groupId>
            <artifactId>flink-streaming-java</artifactId>
            <version>${flink.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.flink</groupId>
            <artifactId>flink-clients</artifactId>
            <version>${flink.version}</version>
        </dependency>
    </dependencies>
</project>

// WordCountJob.java
package com.learning.streaming;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;

public class WordCountJob {
    
    public static void main(String[] args) throws Exception {
        ParameterTool params = ParameterTool.fromArgs(args);
        
        String hostname = params.get("host", "localhost");
        int port = params.getInt("port", 9999);
        
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        
        DataStream<String> text = env.socketTextStream(hostname, port);
        
        DataStream<WordCount> counts = text
            .flatMap(new FlatMapFunction<String, WordCount>() {
                @Override
                public void flatMap(String value, Collector<WordCount> out) {
                    String[] words = value.split("\\s+");
                    for (String word : words) {
                        if (!word.isEmpty()) {
                            out.collect(new WordCount(word, 1));
                        }
                    }
                }
            })
            .keyBy(WordCount::getWord)
            .sum("count")
            .window(SlidingProcessingTimeWindows.of(Time.seconds(10), Time.seconds(5)))
            .reduce((a, b) -> new WordCount(a.getWord(), a.getCount() + b.getCount()));
        
        counts.print();
        
        env.execute("Word Count Streaming Job");
    }
}

class WordCount {
    private String word;
    private int count;
    
    public WordCount() {}
    
    public WordCount(String word, int count) {
        this.word = word;
        this.count = count;
    }
    
    public String getWord() { return word; }
    public void setWord(String word) { this.word = word; }
    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
    
    @Override
    public String toString() {
        return word + " : " + count;
    }
}
```

---

## Real-World Project: Real-Time Analytics Platform (8+ hours)

### Overview
Build a comprehensive real-time analytics platform using Apache Flink for processing clickstream data, generating metrics, and detecting anomalies.

### Project Structure
```
realtime-analytics/
├── flink-jobs/
│   ├── src/main/java/com/learning/analytics/
│   │   ├── ClickStreamJob.java
│   │   ├── processor/ClickProcessor.java
│   │   ├── aggregator/MetricsAggregator.java
│   │   ├── detector/AnomalyDetector.java
│   │   └── sink/ElasticsearchSink.java
│   └── pom.xml
├── kafka/
│   ├── click-events.json
│   └── docker-compose.yml
├── elasticsearch/
└── grafana/
```

### Implementation
```java
// pom.xml for Flink jobs
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <properties>
        <java.version>17</java.version>
        <flink.version>1.18.1</flink.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.apache.flink</groupId>
            <artifactId>flink-streaming-java</artifactId>
            <version>${flink.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.flink</groupId>
            <artifactId>flink-connector-kafka</artifactId>
            <version>${flink.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.flink</groupId>
            <artifactId>flink-connector-elasticsearch7</artifactId>
            <version>${flink.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.kafka</groupId>
            <artifactId>kafka-clients</artifactId>
            <version>3.6.0</version>
        </dependency>
    </dependencies>
</project>

// ClickStreamJob.java
package com.learning.analytics;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

public class ClickStreamJob {
    
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(4);
        
        KafkaSource<String> kafkaSource = KafkaSource.<String>builder()
            .setBootstrapServers("localhost:9092")
            .setTopics("click-events")
            .setGroupId("flink-analytics-group")
            .setStartingOffsets(OffsetsInitializer.latest())
            .setValueOnlyDeserializer(new SimpleStringSchema())
            .build();
        
        DataStream<String> clickEvents = env.fromSource(
            kafkaSource,
            WatermarkStrategy.noWatermarks(),
            "Kafka Click Events"
        );
        
        DataStream<ClickEvent> parsedEvents = clickEvents
            .map(clickJson -> parseClickEvent(clickJson));
        
        DataStream<PageMetrics> pageMetrics = parsedEvents
            .keyBy(ClickEvent::getPageId)
            .window(SlidingEventTimeWindows.of(Time.minutes(5), Time.minutes(1)))
            .aggregate(new MetricsAggregator());
        
        DataStream<AnomalyAlert> anomalies = parsedEvents
            .keyBy(ClickEvent::getUserId)
            .window(SlidingEventTimeWindows.of(Time.minutes(10), Time.minutes(1)))
            .process(new AnomalyDetector());
        
        ElasticsearchSink<PageMetrics> esSink = createElasticsearchSink();
        pageMetrics.addSink(esSink);
        
        anomalies.print();
        
        env.execute("Real-Time Analytics Job");
    }
    
    private static ClickEvent parseClickEvent(String json) {
        return new ClickEvent("user1", "page1", "home", 
            System.currentTimeMillis(), "click");
    }
    
    private static ElasticsearchSink<PageMetrics> createElasticsearchSink() {
        ElasticsearchSink.Builder<PageMetrics> builder = new ElasticsearchSink.Builder<>(
            "http://localhost:9200",
            (PageMetrics element, RuntimeException e) -> {
                System.err.println("Failed to write to Elasticsearch: " + e.getMessage());
            }
        );
        
        builder.setBulkFlushMaxActions(1);
        return builder.build();
    }
}

// ClickEvent.java
package com.learning.analytics;

public class ClickEvent {
    private String userId;
    private String pageId;
    private String action;
    private long timestamp;
    private String sessionId;
    
    public ClickEvent() {}
    
    public ClickEvent(String userId, String pageId, String action, 
                      long timestamp, String sessionId) {
        this.userId = userId;
        this.pageId = pageId;
        this.action = action;
        this.timestamp = timestamp;
        this.sessionId = sessionId;
    }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getPageId() { return pageId; }
    public void setPageId(String pageId) { this.pageId = pageId; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
}

// MetricsAggregator.java
package com.learning.analytics;

import org.apache.flink.api.common.functions.AggregateFunction;

public class MetricsAggregator implements AggregateFunction<ClickEvent, PageMetrics, PageMetrics> {
    
    @Override
    public PageMetrics createAccumulator() {
        return new PageMetrics();
    }
    
    @Override
    public PageMetrics add(ClickEvent value, PageMetrics accumulator) {
        accumulator.incrementViews();
        
        if ("click".equals(value.getAction())) {
            accumulator.incrementClicks();
        }
        
        accumulator.addUser(value.getUserId());
        
        return accumulator;
    }
    
    @Override
    public PageMetrics getResult(PageMetrics accumulator) {
        return accumulator;
    }
    
    @Override
    public PageMetrics merge(PageMetrics a, PageMetrics b) {
        a.merge(b);
        return a;
    }
}

// PageMetrics.java
package com.learning.analytics;

import java.util.HashSet;
import java.util.Set;

public class PageMetrics {
    private String pageId;
    private long views;
    private long clicks;
    private Set<String> uniqueUsers;
    private long windowStart;
    private long windowEnd;
    
    public PageMetrics() {
        this.uniqueUsers = new HashSet<>();
    }
    
    public void incrementViews() { this.views++; }
    public void incrementClicks() { this.clicks++; }
    public void addUser(String userId) { this.uniqueUsers.add(userId); }
    public void merge(PageMetrics other) {
        this.views += other.views;
        this.clicks += other.clicks;
        this.uniqueUsers.addAll(other.uniqueUsers);
    }
    
    public String getPageId() { return pageId; }
    public void setPageId(String pageId) { this.pageId = pageId; }
    public long getViews() { return views; }
    public long getClicks() { return clicks; }
    public long getUniqueUsers() { return uniqueUsers.size(); }
    public long getWindowStart() { return windowStart; }
    public void setWindowStart(long windowStart) { this.windowStart = windowStart; }
    public long getWindowEnd() { return windowEnd; }
    public void setWindowEnd(long windowEnd) { this.windowEnd = windowEnd; }
}

// AnomalyDetector.java
package com.learning.analytics;

import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.util.Collector;

public class AnomalyDetector 
    extends ProcessWindowFunction<ClickEvent, AnomalyAlert, String, GlobalWindow> {
    
    @Override
    public void process(String key, Context context, 
                        Iterable<ClickEvent> elements, Collector<AnomalyAlert> out) {
        
        int count = 0;
        long firstTimestamp = 0;
        long lastTimestamp = 0;
        
        for (ClickEvent event : elements) {
            count++;
            if (firstTimestamp == 0) firstTimestamp = event.getTimestamp();
            lastTimestamp = event.getTimestamp();
        }
        
        if (count > 50) {
            long durationSeconds = (lastTimestamp - firstTimestamp) / 1000;
            double ratePerMinute = durationSeconds > 0 
                ? (count * 60.0 / durationSeconds) 
                : count;
            
            if (ratePerMinute > 100) {
                out.collect(new AnomalyAlert(
                    key, "HIGH_CLICK_RATE", ratePerMinute, 
                    System.currentTimeMillis()
                ));
            }
        }
    }
}

// AnomalyAlert.java
package com.learning.analytics;

public class AnomalyAlert {
    private String userId;
    private String alertType;
    private double value;
    private long timestamp;
    
    public AnomalyAlert(String userId, String alertType, double value, long timestamp) {
        this.userId = userId;
        this.alertType = alertType;
        this.value = value;
        this.timestamp = timestamp;
    }
    
    public String getUserId() { return userId; }
    public String getAlertType() { return alertType; }
    public double getValue() { return value; }
    public long getTimestamp() { return timestamp; }
}
```

### Build and Deploy
```bash
# Build Flink job
mvn clean package -DskipTests

# Start Kafka and other services
docker-compose up -d

# Submit Flink job
./bin/flink run target/realtime-analytics-1.0.0.jar

# Monitor job
./bin/flink list

# View metrics
curl http://localhost:8081/jobs/<job-id>/metrics
```

### Learning Outcomes
- Build streaming applications with Apache Flink
- Process real-time data from Kafka
- Implement windowing and aggregations
- Detect anomalies in real-time
- Write to Elasticsearch for visualization
- Monitor and tune streaming jobs