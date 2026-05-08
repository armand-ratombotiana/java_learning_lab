# Pedagogic Guide - Gatling

## Learning Path

### Phase 1: DSL Fundamentals
1. Simulation structure
2. Scenario definition
3. HTTP requests
4. Response checks

### Phase 2: Load Profiles
1. Injection profiles (constant, ramp, etc.)
2. Closed vs open models
3. Pause configuration
4. Peak load simulation

### Phase 3: Data Handling
1. Feeders (CSV, JSON, Redis)
2. Session attributes
3. Dynamic value extraction
4. Random data selection

### Phase 4: Advanced Features
1. WebSocket testing
2. Conditional logic
3. Loop and repeat
4. Debugging techniques

### Phase 5: Integration
1. Maven plugin setup
2. CI/CD pipeline
3. Performance regression
4. Trend analysis

## Injection Strategies

| Strategy | Use Case |
|----------|----------|
| atOnceUsers(n) | Immediate load |
| rampUsers(n).during(t) | Gradual ramp |
| constantUsersPerSec(r).during(t) | Sustained load |
| stressPeakUsers(n).during(t) | Spike testing |

## Gatling vs JMeter

| Aspect | Gatling | JMeter |
|--------|---------|--------|
| Language | Scala DSL | GUI/XML |
| Performance | Higher | Moderate |
| Learning Curve | Steeper | Easier |
| Reports | HTML (built-in) | Plugin needed |
| Scalability | Excellent | Requires distributed mode |

## Interview Topics
- How to create representative scenarios?
- When to use closed vs. open models?
- How to avoid local cache skewing results?
- Gatling best practices for reliable tests?
- How to interpret results?

## Next Steps
- Explore Gatling FrontLine for enterprise
- Learn about Gatling Enterprise
- Study real-browser testing with Gatling