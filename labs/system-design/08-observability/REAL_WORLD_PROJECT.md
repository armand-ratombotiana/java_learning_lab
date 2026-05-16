# Observability - REAL WORLD PROJECT

Production observability with:
- Prometheus metrics
- Grafana dashboards
- Jaeger tracing
- ELK stack for logs

```java
@Configuration
public class ObservabilityConfig {
    @Bean
    public MeterRegistry prometheusMeterRegistry() {
        return new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
    }
}
```