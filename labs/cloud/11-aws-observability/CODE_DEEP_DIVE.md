# 11 — AWS Observability — Code Deep Dive

## 1. CloudWatch Custom Metrics

### Publishing Custom Metrics
```java
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.*;

CloudWatchClient cloudWatch = CloudWatchClient.create();

cloudWatch.putMetricData(PutMetricDataRequest.builder()
    .namespace("MyApplication")
    .metricData(MetricDatum.builder()
        .metricName("OrderProcessingTime")
        .value(245.0) // milliseconds
        .unit(StandardUnit.MILLISECONDS)
        .dimensions(
            Dimension.builder().name("Environment").value("Production").build(),
            Dimension.builder().name("Service").value("OrderService").build()
        )
        .timestamp(Instant.now())
        .storageResolution(1) // 1-second high resolution
        .build())
    .build());
```

### CloudWatch Logs with Java
```java
CloudWatchLogsClient logs = CloudWatchLogsClient.create();

// Create log group
logs.createLogGroup(CreateLogGroupRequest.builder()
    .logGroupName("/my-app/order-service")
    .build());

// Create log stream
logs.createLogStream(CreateLogStreamRequest.builder()
    .logGroupName("/my-app/order-service")
    .logStreamName("ip-10-0-1-45")
    .build());

// Put log events
logs.putLogEvents(PutLogEventsRequest.builder()
    .logGroupName("/my-app/order-service")
    .logStreamName("ip-10-0-1-45")
    .logEvents(
        InputLogEvent.builder()
            .timestamp(Instant.now().toEpochMilli())
            .message("{\"level\":\"INFO\",\"msg\":\"Order processed\",\"orderId\":\"ORD-123\"}")
            .build())
    .build());
```

## 2. X-Ray Distributed Tracing

### Instrumenting with X-Ray SDK
```java
import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.entities.Subsegment;
import com.amazonaws.xray.entities.TraceID;

public class OrderService {
    public Order processOrder(OrderRequest request) {
        Subsegment subsegment = AWSXRay.beginSubsegment("ProcessOrder");
        try {
            subsegment.putAnnotation("orderId", request.orderId());
            subsegment.putAnnotation("userId", request.userId());

            long start = System.nanoTime();
            Order result = doProcess(request);
            long duration = System.nanoTime() - start;

            subsegment.putMetadata("processing", Map.of(
                "durationMs", duration / 1_000_000,
                "itemCount", request.items().size()
            ));

            return result;
        } catch (Exception e) {
            subsegment.addException(e);
            throw e;
        } finally {
            AWSXRay.endSubsegment();
        }
    }

    public void traceDynamoDbCall(String tableName) {
        Subsegment sub = AWSXRay.beginSubsegment("DynamoDB Query");
        sub.putSql("SELECT * FROM " + tableName + " WHERE orderId = :id");
        sub.setNamespace("aws");
        AWSXRay.endSubsegment();
    }
}
```

## 3. Prometheus Metrics Export

### Java Metrics with Micrometer + Prometheus
```java
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import java.util.concurrent.TimeUnit;

public class MetricsExporter {
    private final PrometheusMeterRegistry registry;
    private final Counter orderCounter;
    private final Timer orderTimer;

    public MetricsExporter() {
        this.registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        this.orderCounter = registry.counter("orders_total", "service", "order-service");
        this.orderTimer = registry.timer("order_duration_seconds", "service", "order-service");
    }

    public void recordOrder(Supplier<Order> orderSupplier) {
        orderCounter.increment();
        Order result = orderTimer.record(() -> orderSupplier.get());
    }

    public String scrape() {
        return registry.scrape();
    }
}
```

## 4. AMP Remote Write

### Sending Metrics to AMP
```java
// Using Prometheus remote write protocol with SigV4 signing
public class AmpRemoteWriter {
    private final String ampEndpoint;

    public AmpRemoteWriter(String ampEndpoint) {
        this.ampEndpoint = ampEndpoint;
    }

    public void writeMetrics(String prometheusData) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(ampEndpoint + "/api/v1/remote_write"))
            .header("Content-Type", "application/x-protobuf")
            .header("Content-Encoding", "snappy")
            .POST(HttpRequest.BodyPublishers.ofString(prometheusData))
            .build();
        // AWS SigV4 signing required for authentication
    }
}
```
