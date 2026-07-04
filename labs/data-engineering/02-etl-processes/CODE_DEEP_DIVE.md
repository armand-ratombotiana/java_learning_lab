# Code Deep Dive: Spring Batch ETL

## Job Configuration
```java
@Configuration
public class EtlJobConfig {
    @Bean
    public Job etlJob(JobRepository repo, Step extract, Step transform, Step load) {
        return new JobBuilder("customerETL", repo)
            .start(extract).next(transform).next(load)
            .listener(new EtlJobListener()).build();
    }
}
```

## Item Processor
```java
public class CustomerProcessor implements ItemProcessor<Customer, EnrichedCustomer> {
    public EnrichedCustomer process(Customer c) {
        EnrichedCustomer e = new EnrichedCustomer();
        e.setCustomerId(c.getId());
        e.setFullName(c.getFirstName() + " " + c.getLastName());
        e.setSegment(c.getTotalOrders() > 50 ? "LOYAL" : "REGULAR");
        return e;
    }
}
```
