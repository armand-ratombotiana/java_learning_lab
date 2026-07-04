# Observability - REFLECTION

## Key Takeaways

1. **Observability is not just tools**: Dashboards are useless if you don't know what to look for. Start with clear questions, then build the tools to answer them.

2. **Correlation is key**: Logs, metrics, and traces must be connected. A trace ID in logs and spans enables the full picture of any request.

3. **Zero instrumentation is the goal**: OpenTelemetry auto-instrumentation means adding observability without changing application code. Invest in auto-instrumentation.

4. **Less is more**: Too many metrics, too verbose logs, too much tracing overload storage and cognitive capacity. Be selective.

## Self-Assessment Questions

- Can I explain the three pillars of observability?
- Do I understand how to instrument a Java service with OpenTelemetry?
- Can I design meaningful alerts that reduce on-call fatigue?
- Do I know how to correlate logs, metrics, and traces for debugging?

## Common Misconceptions

- "Observability means using Datadog" — Observability is an approach, not a product. You can be observable with open-source tools.
- "All metrics should be collected" — High cardinality destroys Prometheus performance. Be selective.
- "Log everything, you never know what you'll need" — Logging costs money, storage, and CPU. Log what you act on.
- "Tracing works by magic" — It requires context propagation, which requires proper library integration or manual instrumentation.

## Next Steps

- Deploy the ELK stack (or Grafana Loki) and ship application logs
- Set up Prometheus + Grafana with Spring Boot Actuator
- Instrument a microservice with OpenTelemetry auto-instrumentation
- Read "Observability Engineering" by Charity Majors, Liz Fong-Jones, George Miranda
