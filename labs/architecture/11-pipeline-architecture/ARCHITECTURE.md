# Pipeline Architecture Reference

## Architecture Diagram
```
[Data Source] -> [Pipeline Orchestrator] -> [Data Sink]
                      |
        +-------------+-------------+
        |             |             |
   [Stage 1]    [Stage 2]     [Stage N]
   Validation   Transform     Persist
        |             |             |
   [Error Handler] [Error H]   [Error H]
        |             |             |
        +------[Dead Letter Queue]--+
```

## Stage Catalog
| Stage Type | Purpose | Example |
|-----------|---------|---------|
| Validation | Check input validity | Schema validation |
| Filter | Remove unwanted data | Null filter |
| Transformer | Convert data format | JSON to XML |
| Enricher | Add external data | Customer lookup |
| Aggregator | Combine multiple items | Sum totals |
| Splitter | Divide item | Line item split |
| Router | Route based on content | Priority routing |
| Persister | Save to storage | Database write |

## Pipeline Configuration
```yaml
pipeline:
  name: order-processing
  stages:
    - name: validation
      type: validation
      config:
        schema: order-schema.json
    - name: enrichment
      type: enricher
      config:
        service: customer-service
    - name: processing
      type: transformer
    - name: persistence
      type: persister
      config:
        collection: orders
  error-handling:
    max-retries: 3
    dead-letter-queue: true
```

## Error Handling Strategy
- Per-stage error handlers
- Retry with backoff for transient failures
- Dead letter queue for persistent failures
- Pipeline circuit breaker for cascading failures
- Monitoring alerts on error rates
