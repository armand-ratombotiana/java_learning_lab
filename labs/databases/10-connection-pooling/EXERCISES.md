# Exercises: Connection Pooling

## Exercise 1 – Basic HikariCP Setup
- Configure HikariCP programmatically with PostgreSQL or H2
- Set pool size to 5, connection timeout to 10s
- Verify connections are being pooled (check JMX metrics)

## Exercise 2 – Pool Tuning
- Create a load test that simulates 50 concurrent requests
- Measure response times with pool sizes: 1, 5, 10, 20, 50
- Plot throughput vs pool size. Where does it peak?

## Exercise 3 – Leak Detection
- Write code that acquires a connection but never returns it
- Enable `leak-detection-threshold=5000`
- Observe the stack trace in the logs

## Exercise 4 – Multi-DataSource
- Configure two HikariCP data sources: one for reads, one for writes
- Use `@Primary` and `@Qualifier` annotations
- Route read queries to read pool, write queries to write pool

## Exercise 5 – Monitoring
- Expose HikariCP metrics via Spring Boot Actuator
- Create a Grafana dashboard showing:
  - Active connections over time
  - Connection timeout rate
  - Average connection acquire time
  - Pool utilization percentage
