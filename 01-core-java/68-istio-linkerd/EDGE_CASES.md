# Module 68: Istio & Linkerd - Edge Cases & Pitfalls

---

## Pitfall 1: Istio's Complexity Overhead

### ❌ Wrong
A startup with 5 microservices deciding to deploy Istio on day one "for future-proofing." They spend 3 weeks struggling to configure Istio Gateways, VirtualServices, DestinationRules, and PeerAuthentication, delaying their product launch.

### ✅ Correct
Choose Linkerd for small to medium deployments that just need mTLS and basic observability with zero configuration. Only migrate to Istio when you have a massive enterprise architecture requiring multi-cluster routing, complex header manipulation, or fine-grained RBAC that Linkerd doesn't support.

---

## Pitfall 2: The Sidecar Injection Race Condition

### ❌ Wrong
Deploying a Spring Boot app that connects to a database immediately on startup. Sometimes the pod crashes with a `ConnectionRefused` error, and sometimes it works fine.

### ✅ Correct
This is a famous Service Mesh race condition. Kubernetes starts the Envoy/Linkerd sidecar container and the Java container simultaneously. If the Java app boots faster than the proxy and tries to make an outbound network call, the proxy isn't ready to intercept it, and the network call fails.
*Fix*: Implement retry logic in your database connection pool, or configure Kubernetes annotations (`holdApplicationUntilProxyStarts: true` in Istio) to pause the Java container until the proxy is fully ready.

---

## Pitfall 3: Tracing Context Drops

### ❌ Wrong
Assuming that because Istio/Linkerd generates distributed traces, you don't have to change your Java code. You look at Jaeger and see disjointed traces that don't connect.

### ✅ Correct
As covered in Module 43, the proxy intercepts traffic, but it relies on HTTP headers (like `x-b3-traceid`) to link incoming requests to outgoing requests. Your Java application *must* extract these headers from incoming requests and inject them into any outgoing `RestTemplate` or `WebClient` requests.