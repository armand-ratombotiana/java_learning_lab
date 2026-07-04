# Observability - DEBUGGING

## Common Observability Issues

| Symptom | Cause | Diagnostic |
|---------|-------|------------|
| Missing metrics | Wrong endpoint, disabled | Check /actuator/prometheus endpoint |
| High cardinality | User IDs as labels | Check metric label values |
| No traces | Missing propagation | Check traceparent header presence |
| Logs not aggregating | Log format mismatch | Check Logstash encoder config |
| Alerts not firing | Rule syntax error | Test rule in Prometheus UI |
| Grafana no data | Data source config error | Check Prometheus data source |

## Prometheus Debugging

```bash
# Check targets are up
http://localhost:9090/targets

# Test metric existence
curl http://localhost:8080/actuator/prometheus | findstr requests_total

# Check rule evaluation
http://localhost:9090/rules
```

## Tracing Debugging

```bash
# Check Jaeger UI
http://localhost:16686

# Search by service
# Look for spans without parent (missing propagation)
# Check trace context propagation headers
```

## Log Analysis

```bash
# Using Loki LogCLI
logcli query '{service="order-service"} |= "ERROR"'

# Using Elasticsearch
curl -X GET "localhost:9200/logs-*/_search" \
  -H 'Content-Type: application/json' \
  -d '{"query": {"match": {"level": "ERROR"}}}'
```

## Health Check Debugging

```bash
# Detailed health
curl http://localhost:8080/actuator/health
# Shows component-level health (db, disk, cache)

# Manually set unhealthy (for testing)
curl -X POST http://localhost:8080/actuator/health/fail
```

## Metric Debugging

```java
// Expose metrics for debugging
@RestController
@RequestMapping("/debug")
public class DebugController {
    @GetMapping("/metrics")
    public String dumpMetrics(MeterRegistry registry) {
        StringBuilder sb = new StringBuilder();
        registry.forEachMeter(meter -> {
            sb.append(meter.getId().getName()).append(": ")
              .append(meter.measure()).append("\n");
        });
        return sb.toString();
    }
}
```
