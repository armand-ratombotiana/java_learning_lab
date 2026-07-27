# Service Mesh MOCK_INTERVIEW.md

## Scenario 1: Service Mesh Adoption
Your team has 30 microservices with no service mesh. mTLS, traffic splitting, and observability are manual.

**Questions**:
1. Why would you adopt a service mesh?
2. Compare Istio, Linkerd, and Consul Connect.
3. How would you incrementally adopt a mesh?
4. What's the migration strategy?

**Expected approach**: Benefits: mTLS, traffic management, observability, security. Istio for full features, Linkerd for simplicity, Consul for multi-platform. Incremental: install control plane, enable sidecar injection per namespace, use PERMISSIVE mTLS, gradually switch to STRICT. Start with non-critical services.

## Scenario 2: Debugging Service Mesh
Service A can't connect to Service B. Both are in the mesh.

**Questions**:
1. How would you debug the connectivity issue?
2. What tools (Kiali, Grafana, Jaeger) help?
3. How do you check mTLS status?
4. How do you view Envoy proxy config?

**Expected approach**: Istio: `istioctl proxy-status`, `istioctl proxy-config` (routes, listeners, clusters, endpoints). Kiali for topology. Check PeerAuthentication, DestinationRule, VirtualService. mTLS: `istioctl authn tls-check`. Envoy admin API via `istioctl dashboard envoy`.

## Scenario 3: Canary Deployment with Mesh
You need to deploy v2 of a service to 5% of traffic.

**Questions**:
1. How do you configure traffic splitting in Istio?
2. How do you analyze canary success?
3. How do you gradually increase traffic?
4. How do you rollback a canary?

**Expected approach**: VirtualService with HTTPRoute weights (v1: 95, v2: 5). DestinationRule with subsets. Analyze via Prometheus (request rate, error rate, latency). Gradually increase weight. Rollback by setting v2 weight to 0. Can also use Flagger for automated canary promotion/rollback.

## Scenario 4: Zero-Trust Security
You need to implement zero-trust networking between all services.

**Questions**:
1. How does mTLS work in a service mesh?
2. How do you configure authorization policies?
3. How do you handle external services?
4. How do you audit mesh security?

**Expected approach**: PeerAuthentication: STRICT mTLS. AuthorizationPolicy: allow specific methods/paths, deny all others by default. ServiceEntry for external services with TLS origination. Audit via Kiali, `istioctl experimental dashboard`, Envoy access logs.

## Scenario 5: Multi-Cluster Mesh
Your services run in two Kubernetes clusters in different regions.

**Questions**:
1. How do you connect clusters in a mesh?
2. Compare Istio multi-cluster models.
3. How does service discovery work across clusters?
4. How do you handle failover?

**Expected approach**: Istio multi-cluster: shared control plane (VIP), replicated control plane (HA), primary-remote. Service discovery via `ServiceEntry` for remote services. Failover via `DestinationRule` with outlier detection and locality load balancing.

## Key Service Mesh Interview Questions
1. What problems does a service mesh solve?
2. Explain the sidecar proxy pattern.
3. How does Istio integrate with Kubernetes?
4. What's the difference between a data plane and control plane?
5. Explain how mTLS certificates are issued and rotated.
6. How does Envoy handle hot reload of configuration?
7. Explain the xDS protocol.
8. What's the overhead of adding a service mesh?
9. How does Linkerd differ from Istio in architecture?
10. Explain mutual TLS in detail.

## Whiteboard Challenge
Design a multi-cluster service mesh for a global SaaS platform. Consider mTLS, traffic splitting, multi-region failover, observability, and security policies. Include the migration strategy from no mesh to full mesh.

## Follow-up
1. How would you handle mesh ingress and egress traffic?
2. How do you debug mesh performance issues?
3. How does ambient mesh (sidecar-less) change the architecture?