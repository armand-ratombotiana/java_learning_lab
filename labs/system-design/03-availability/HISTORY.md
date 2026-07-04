# Availability - HISTORY

## Timeline

### 1960s-1970s: Mainframe Reliability
- IBM mainframes with redundant components
- Fault-tolerant computing for aerospace (Apollo program)
- Tandem NonStop computers (1974) — first fault-tolerant commercial system

### 1980s: High-Availability Clustering
- VAXcluster (DEC) — shared storage failover
- HP MC/ServiceGuard — active/passive clustering
- Telephone networks achieve 99.999% uptime

### 1990s: Load Balancing & Failover
- F5 BIG-IP (1997) — first application delivery controller
- Microsoft Cluster Server (MSCS)
- Linux-HA project starts

### 2000s: Internet-Scale Availability
- 2004: Google's GFS paper — designed for component failures
- 2006: AWS launches — availability zones concept
- 2008: Netflix starts Chaos Monkey to test failure tolerance
- 2009: Erlang-based CouchDB — "let it crash" philosophy

### 2010s: Cloud-Native Resilience
- 2011: Circuit breaker pattern popularized (Netflix Hystrix)
- 2015: Kubernetes self-healing (liveness/readiness probes)
- 2017: Service mesh (Istio) for resilience features
- 2018: Chaos Engineering becomes formal practice

### 2020s: Declarative Resilience
- Resilience4j replaces Hystrix (lightweight, modular)
- eBPF enables kernel-level observability
- Cell-based architecture for blast radius reduction
- AI-driven incident response

## Java Resilience Evolution
- Hystrix (2012) → Resilience4j (2018) — lightweight circuit breakers
- Spring Retry (2014) — declarative retries
- Spring Cloud Circuit Breaker (2020) — unified API
- io.projectreactor (Reactive) — backpressure + retry
