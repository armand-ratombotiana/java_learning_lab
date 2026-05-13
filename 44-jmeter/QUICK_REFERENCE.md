# 44 - JMeter Quick Reference

## Key Concepts

| Concept | Description |
|---------|-------------|
| Thread Group | Virtual users configuration |
| Sampler | Request type (HTTP, JDBC, JMS) |
| Listener | Results collection/display |
| Assertion | Response validation |
| Timer | Think time simulation |

## Commands

```bash
# Run with Maven
cd jmeter-performance
mvn verify

# Run test plan manually
jmeter -n -t src/test/jmeter/test-plan.jmx -l results.jtl

# Generate HTML report
jmeter -g results.jtl -o report-folder

# Remote testing
jmeter -n -t test.jmx -r -l results.jtl
```

## Test Plan Elements

| Element | Purpose |
|---------|---------|
| Thread Group | Number of users, ramp-up, loops |
| HTTP Request | HTTP method, path, parameters |
| Response Assertion | Status code, response content |
| Constant Timer | Delay between requests |
| View Results Tree | Debug individual requests |
| Summary Report | Aggregate statistics |

## Example Configuration

```xml
<ThreadGroup>
  <numThreads>100</numThreads>
  <rampTime>30</rampTime>
  <duration>300</duration>
  <loopCount>1</loopCount>
</ThreadGroup>

<HTTPSamplerProxy>
  <stringProp name="HTTPSampler.domain">localhost</stringProp>
  <stringProp name="HTTPSampler.port">8080</stringProp>
  <stringProp name="HTTPSampler.path">/api/users</stringProp>
  <stringProp name="HTTPSampler.method">GET</stringProp>
</HTTPSamplerProxy>
```

## Results Analysis

| Metric | Description |
|--------|-------------|
| Throughput | Requests per second |
| Median | 50% response time |
| 90th/95th/99th | Response time percentiles |
| Error % | Failed request percentage |