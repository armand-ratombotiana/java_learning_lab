# Pedagogic Guide - JMeter

## Learning Path

### Phase 1: Test Plan Basics
1. Elements and hierarchy
2. Thread groups and ramp-up
3. Samplers and requests
4. Listeners and results

### Phase 2: Advanced Controllers
1. Simple Controller
2. Loop Controller
3. Transaction Controller
4. Interleave Controller

### Phase 3: Scripting
1. JSR223 PreProcessor
2. BeanShell scripts
3. Groovy integration
4. Custom functions

### Phase 4: Reporting
1. View Results Tree
2. Summary Report
3. HTML Dashboard generation
4. Response time graphs

### Phase 5: CI/CD Integration
1. Maven plugin configuration
2. Jenkins integration
3. Performance regression
4. Trend analysis

## Thread Group Config

| Setting | Description |
|---------|-------------|
| Number of Threads | Virtual users |
| Ramp-up Period | Time to reach all users |
| Loop Count | How many iterations |
| Duration | Test run duration |

## Key Metrics

| Metric | Description |
|--------|-------------|
| Throughput | Requests per second |
| Avg Response Time | Mean response time |
| 90th Percentile | 90% of requests under |
| Error Rate | Failed requests % |

## Interview Topics
- How to design representative load tests?
- What metrics matter for performance?
- How to identify bottlenecks?
- Difference between JMeter and Gatling?
- How to avoid testing issues (cookie, cache)?

## Next Steps
- Explore JMeter plugins (JSON, Graphite)
- Learn distributed testing setup
- Study correlation of dynamic values