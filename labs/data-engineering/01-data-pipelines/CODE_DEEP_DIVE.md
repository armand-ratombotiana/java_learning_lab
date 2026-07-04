# Code Deep Dive: Spring Batch Pipeline

## Domain Model
```java
public class Transaction {
    private String id; private String userId; private BigDecimal amount;
    private LocalDateTime timestamp; private String status;
    // Getters and Setters
}
```

## Item Processor
```java
public class EnrichmentProcessor implements ItemProcessor<Transaction, Enriched> {
    public Enriched process(Transaction t) {
        Customer c = customerService.findById(t.getUserId());
        Enriched e = new Enriched();
        e.setId(t.getId()); e.setCustomerName(c.getFullName());
        e.setRiskScore(fraudClient.evaluate(t));
        return e;
    }
}
```

## Job Configuration
```java
@Bean
public Job pipelineJob(JobRepository repo, Step step) {
    return new JobBuilder("pipeline", repo).start(step)
        .listener(new MetricsListener()).build();
}
```
