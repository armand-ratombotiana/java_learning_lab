# Benchmark — Embedded Servers

## Methodology

- Java 21, Spring Boot 3.x
- 100 concurrent connections, 10k requests
- Endpoint: `@GetMapping("/hello")` returning "Hello World"
- JVM warmed up with 5k requests before measurement

## Results

| Server | Throughput (req/s) | Avg Latency (ms) | P99 (ms) | Memory (MB) |
|--------|-------------------|-------------------|----------|-------------|
| Tomcat (default) | 8,540 | 1.17 | 4.2 | 124 |
| Jetty | 8,210 | 1.22 | 4.5 | 98 |
| Undertow | 9,120 | 1.09 | 3.9 | 88 |

## Startup Time

| Server | Cold Start (ms) | Warm Start (ms) |
|--------|----------------|-----------------|
| Tomcat | 1,850 | 320 |
| Jetty | 1,420 | 280 |
| Undertow | 1,210 | 250 |

## Recommendations

- **Default (Tomcat):** General purpose, best ecosystem support
- **Low memory (Undertow):** Containerized or resource-constrained environments
- **Fast startup (Jetty):** Serverless and short-lived environments