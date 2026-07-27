# Service Mesh & DevOps MOCK_INTERVIEW.md

## Scenario 1: Observability with Service Mesh
Your team lacks observability for inter-service communication.

**Questions**:
1. How does a service mesh improve observability?
2. What metrics does Istio expose?
3. How do you set up distributed tracing?
4. How do you build service topology maps?

**Expected approach**: Istio exports Envoy metrics (HTTP, TCP, gRPC). Prometheus scrapes, Grafana dashboards. Distributed tracing via Jaeger/Zipkin (trace propagation needs header forwarding). Kiali for service topology, health, and traffic visualization. Access logs via Envoy logging to stdout/stderr.

## Scenario 2: Traffic Management with Mesh
You need to implement advanced traffic management for blue-green and canary deployments.

**Questions**:
1. How does Istio VirtualService configure traffic routing?
2. How do you implement A/B testing?
3. How do you mirror traffic for testing?
4. How do you implement circuit breaking?

**Expected approach**: VirtualService with HTTP match + destination weights for canary. Mirroring via `mirror:` field (dark launch). Circuit breaking via DestinationRule `trafficPolicy.connectionPool` + `outlierDetection`. A/B testing via header-based routing (user-agent, cookie).

## Scenario 3: mTLS Migration
Your services communicate over plain HTTP. You need to enable mTLS.

**Questions**:
1. How do you enable mTLS in the mesh?
2. How do you migrate incrementally?
3. How do you handle services outside the mesh?
4. How do you monitor mTLS adoption?

**Expected approach**: Start with PERMISSIVE mode (both HTTP and HTTPS accepted). Gradually switch to STRICT. External services: ServiceEntry with TLS origination. Monitor via `istioctl authn tls-check`, Kiali badges, Prometheus mTLS metrics.

## Scenario 4: Multi-Cluster Service Mesh
Services run in two different Kubernetes clusters. They need to communicate securely.

**Questions**:
1. How do you set up multi-cluster mesh?
2. How does DNS resolution work across clusters?
3. How do you handle failover?
4. How do you manage mTLS across clusters?

**Expected approach**: Istio multi-cluster: shared root CA, installation per cluster, enable cross-cluster service discovery. DNS via `*.global` domain or ServiceEntry. Failover via DestinationRule `localityLbSetting`. mTLS: shared SPIFFE trust domain.

## Scenario 5: Mesh Performance Optimization
Your service mesh is adding 5ms latency overhead. This is unacceptable.

**Questions**:
1. How do you measure mesh overhead?
2. How do you optimize proxy performance?
3. How do you tune Istio?
4. How do you decide what NOT to put through the mesh?

**Expected approach**: Measure baseline vs mesh latency. Optimizations: increase proxy resources (CPU/memory), tune Envoy settings (connection pool, buffer sizes), use Zipkin sampling rate (1% is enough). Exclude: health check traffic, high-throughput batch jobs via `traffic.sidecar.istio.io/includeInboundPorts`.

## Key Service Mesh & DevOps Interview Questions
1. How does a service mesh improve DevOps practices?
2. What's the operational overhead of running a mesh?
3. How do you monitor the mesh itself?
4. How do you debug a "mesh is slow" complaint?
5. How does Istio integrate with CI/CD?
6. What's the impact of mesh on deployment strategies?
7. How do you handle mesh upgrades?
8. What's the relationship between service mesh and GitOps?
9. How do you secure the mesh control plane?
10. How does Linkerd reduce operational complexity?

## Whiteboard Challenge
Design a service mesh strategy for a platform with 200+ microservices across 5 Kubernetes clusters in 3 regions. Include migration from no-mesh, mTLS rollout, traffic management, observability, and multi-cluster failover.

## Follow-up
1. How would you handle mesh cost (resource overhead)?
2. How would you implement mesh security policies?
3. How would you handle mesh version upgrades across clusters?