# Module 68: Istio & Linkerd - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: What is the difference between a Blue/Green Deployment and a Canary Deployment? How does Istio facilitate them?
**Answer**:
- **Blue/Green**: You spin up an exact replica of your production environment (Green) alongside the current live one (Blue). You test Green internally. Once ready, you flip a router switch to send 100% of traffic to Green instantly. If it breaks, you flip the switch back to Blue.
- **Canary**: You deploy the new version (V2) but only route a tiny fraction of live user traffic (e.g., 1%) to it. You monitor errors and latency. If healthy, you gradually increase traffic to 10%, 50%, and finally 100%. 
Istio facilitates Canary deployments flawlessly via `VirtualService` weights. Instead of manually scaling Kubernetes Pods to control ratios, you simply update the percentage in a YAML file, and the Envoy proxies handle the exact mathematical distribution of requests.

### Q2: Why did Linkerd choose to write their micro-proxy in Rust instead of Go or C++?
**Answer**:
Service mesh proxies sit in the critical path of every single network request. They must be incredibly fast, safe, and light.
- **Go** (used for Linkerd's control plane) has a Garbage Collector. GC pauses would introduce unpredictable latency spikes to network traffic.
- **C++** (used for Envoy) is fast but notoriously difficult to secure against memory-safety bugs (buffer overflows, use-after-free).
- **Rust** provides the best of both worlds: It has no Garbage Collector, ensuring perfectly predictable, sub-millisecond tail latency, and its compiler enforces strict memory safety guarantees, eliminating entire classes of security vulnerabilities.

---

## 💻 Whiteboarding Scenarios

### Scenario 1: Retries vs Timeouts in a Microservices Chain
**Problem**: Service A calls Service B, which calls Service C. An interviewer asks you to configure Istio retries. You configure Service A to retry calling Service B three times with a timeout of 5 seconds each. Service B is configured to retry calling Service C three times. Is this a good design? What happens if Service C goes down?

**Solution**:
This is a terrible design called a **Retry Storm**.
If Service C goes down, Service B tries to call it 3 times. But Service A is *also* retrying Service B 3 times. This multiplies the traffic: $3 \times 3 = 9$ requests hitting Service C for a single failed user interaction. In deep microservice architectures, this exponential multiplication acts as an internal DDoS attack, completely crushing recovering services.
**Fix**: 
1. Only retry at the very edge of the network (e.g., Service A retries Service B once). Downstream services should fail fast.
2. Implement **Circuit Breaking** via Istio `DestinationRules` to detect that Service C is failing and immediately open the circuit, returning a 503 instantly rather than timing out and compounding retries.