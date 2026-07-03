# Module 43: Service Mesh - Edge Cases & Pitfalls

---

## Pitfall 1: Application-Level Resilience vs Mesh Resilience

### ❌ Wrong
Writing custom Java code inside your microservices using resilience libraries (like Resilience4j or Hystrix) for timeouts and circuit breaking, while simultaneously configuring the Service Mesh to do the exact same thing. This leads to conflicting configurations where the application times out before the mesh finishes its retries, creating confusing bugs.

### ✅ Correct
Choose one layer for resilience. If you adopt a Service Mesh, remove the circuit-breaking and retry logic from the application code and allow the sidecar proxies to handle it transparently.

---

## Pitfall 2: Memory and CPU Overhead

### ❌ Wrong
Deploying a Service Mesh on a small cluster with dozens of tiny microservices without anticipating resource constraints. Every sidecar proxy (e.g., Envoy) consumes CPU and memory. In extreme cases, the sidecars can consume more resources than the microservices themselves.

### ✅ Correct
Carefully tune the CPU and memory limits for the sidecar proxies. Consider the total cluster capacity. For very small or simple applications, a Service Mesh might be over-engineering.

---

## Pitfall 3: Assuming Complete Transparency

### ❌ Wrong
Assuming that because the Service Mesh handles Distributed Tracing, you don't need to do anything in the application code.

### ✅ Correct
While the mesh generates the trace IDs automatically at the proxy level, the application *must* propagate the trace headers (e.g., `x-b3-traceid`) from incoming HTTP requests to outgoing HTTP requests. If the Java app doesn't forward the headers, the trace will break, and you won't be able to correlate the logs.