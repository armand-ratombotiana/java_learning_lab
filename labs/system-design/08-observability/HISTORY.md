# Observability - HISTORY

## Timeline

### 1960s-1980s: Debugging Era
- printf debugging — add print statements, run, read output
- Core dumps for post-mortem analysis
- IBM mainframe system logs

### 1990s: Logging Standardization
- syslog (RFC 3164, 1980s) — Unix log standard
- 1998: log4j 1.0 (Java logging framework)
- Log analysis tools (Splunk founded 2003)

### 2000s: Monitoring Revolution
- 2001: Nagios — host/service monitoring
- 2006: Cacti — network graphing
- 2007: Ganglia — cluster monitoring
- 2009: Graphite — time-series database

### 2010s: Three Pillars Emerge
- 2011: Netflix open-sources Hystrix + Servo
- 2012: Prometheus starts (SoundCloud)
- 2013: ELK stack (Elasticsearch, Logstash, Kibana)
- 2015: OpenTracing standard (Jaeger, Zipkin)
- 2016: Micrometer starts (metrics facade)
- 2018: OpenTelemetry merges OpenTracing + OpenCensus

### 2020s: eBPF, Continuous Profiling
- eBPF enables kernel-level observability
- Continuous profiling (Parca, Pyroscope)
- Log analysis with LLMs
- Observability as a service (Datadog, New Relic, Grafana Cloud)

## Java Observability Evolution
- log4j (1998) → SLF4J (2005) → Logback (2006) — logging
- JMX (2004) — JVM metrics
- Dropwizard Metrics (2012) → Micrometer (2017) — metrics
- Spring Boot Actuator (2014) — production-ready features
- Spring Cloud Sleuth (2015) → OpenTelemetry (2022) — tracing
- Project Loom (2022+) — structured concurrency with observability
