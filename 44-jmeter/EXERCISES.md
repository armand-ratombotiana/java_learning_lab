# Exercises - JMeter

## Exercise 1: Basic HTTP Test
Create a simple load test:

1. Add Thread Group with 100 users
2. Configure HTTP Request sampler
3. Add Response Assertion for status code
4. Run test and analyze results

## Exercise 2: Think Time Simulation
Model realistic user behavior:

1. Add Constant Timer (2 second delay)
2. Configure Gaussian Random Timer
3. Test with varying think times
4. Compare response time distributions

## Exercise 3: Database Performance
Test database under load:

1. Configure JDBC Connection Configuration
2. Add JDBC Request sampler with query
3. Add Response Assertion for row count
4. Monitor connection pool metrics

## Exercise 4: Distributed Load
Scale testing across machines:

1. Configure remote JMeter server
2. Set up client-server communication
3. Run distributed test
4. Aggregate results centrally

## Exercise 5: Custom Assertions
Validate complex responses:

1. Create JSON Path assertion
2. Add Duration assertion (< 500ms)
3. Implement BeanShell assertion
4. Create custom assertion for business rules

## Bonus Challenge
Build a test plan that: ramps up to 1000 users over 5 minutes, maintains load for 15 minutes, then ramps down over 2 minutes. Include assertions for response time p95 < 1s and error rate < 1%.