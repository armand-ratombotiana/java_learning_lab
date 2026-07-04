# Architecture Patterns - DEBUGGING

## Layered Architecture Debugging

### Common Issues & Solutions

| Symptom | Root Cause | Fix |
|---------|-----------|-----|
| 500 on save | Repository exception | Check DB connection, SQL syntax |
| Wrong data returned | Service returning stale cache | Invalidate cache in update path |
| Slow response | Layer skipping bypassing cache | Ensure all calls go through proper layers |

### Debug Commands
```bash
# Enable Spring Boot debug logging
logging.level.com.example=DEBUG

# Check bean wiring
curl http://localhost:8080/actuator/beans
```

## Microservices Debugging

### Distributed Tracing
Use Spring Cloud Sleuth + Zipkin to trace requests across services.

### Common Failure Patterns
- **Service not found**: Check Eureka registry: `curl http://localhost:8761/eureka/apps`
- **Circuit breaker open**: Check Resilience4j metrics endpoint
- **Timeout**: Increase `spring.cloud.circuitbreaker.resilience4j.timeout`

### Log Aggregation
```bash
# Search across all services (ELK stack example)
GET /product-service-*/_search
{"query": {"match": {"message": "ERROR"}}}
```

## Event-Driven Debugging

### Tools
- **Kafka**: `kafka-console-consumer --bootstrap-server localhost:9092 --topic order-events --from-beginning`
- **Offset Explorer**: Check consumer lag

### Common Issues
- **Duplicate events**: Check consumer idempotency
- **Event ordering**: Ensure same partition key for related events
- **Dead letters**: Check DLQ topic for unprocessable events
- **Backpressure**: Monitor consumer lag; add more partitions or consumers

## CQRS Debugging

### Data Consistency Checks
```java
// Verify write model matches read model
public boolean verifyConsistency(String orderId) {
    List<Event> events = eventStore.getEvents(orderId);
    OrderView actual = reconstructFromEvents(events);
    OrderView expected = queryService.getOrderById(orderId);
    return actual.equals(expected);
}
```

### Projection Lag
```java
// Check projection lag
long writeTime = event.getTimestamp().toEpochMilli();
long readTime = System.currentTimeMillis();
long lag = readTime - writeTime;
```
