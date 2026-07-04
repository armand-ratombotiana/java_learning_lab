# Availability - COMMON MISTAKES

## 1. Single Point of Failure
One component that, when it fails, takes down the whole system.
```java
// WRONG: Single database, no replica
@Bean
public DataSource dataSource() { return singleDb(); }

// RIGHT: Replica or cluster
@Bean
@Primary
public DataSource writeDataSource() { return master(); }
@Bean
public DataSource readDataSource() { return replica(); }
```

## 2. Not Testing Failover
The backup system works only if tested regularly. Many failover scripts fail in production.

## 3. Cascading Failures
Service A failing causes Service B to fail, which causes Service C to fail. Circuit breakers prevent this.

## 4. Overloaded Retries
Retrying without backoff makes things worse.
```java
// WRONG: Immediate retry (makes flood worse)
@Retryable(maxAttempts = 5)

// RIGHT: Exponential backoff
@Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
```

## 5. Ignoring Network Partitions
Two data centers operating independently (split-brain). Use quorum-based consensus.

## 6. Missing Timeouts
```java
// WRONG: No timeout (hangs forever)
ResponseEntity<Product> response = restTemplate.getForEntity(url, Product.class);

// RIGHT: With timeout
@Bean
public RestTemplate restTemplate() {
    return new RestTemplate(new SimpleClientHttpRequestFactory() {{
        setConnectTimeout(2000);
        setReadTimeout(5000);
    }});
}
```

## 7. Not Testing Recovery
Backups that can't be restored are worthless. Test disaster recovery drills quarterly.

## 8. Shared Infrastructure
Multiple services sharing the same database instance — one failing service impacts all.

## 9. Insufficient Monitoring
You can't fix what you don't know is broken. Monitor circuit breaker states, replica lag, error rates.

## 10. Hot Standby Configuration Drift
Standby server falls out of sync (different packages, configs). Automate standby provisioning.
