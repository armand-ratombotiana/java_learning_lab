# Module 68: Istio & Linkerd - Quizzes

---

## Q1: Core Proxies
What is the underlying data plane proxy technology used by Istio?

A) Nginx
B) HAProxy
C) Envoy
D) Linkerd2-proxy

**Answer**: C
**Explanation**: Istio uses Envoy (written in C++) as its sidecar proxy, which handles all inbound and outbound traffic interception and routing.

---

## Q2: Linkerd's Advantage
What makes Linkerd exceptionally lightweight compared to Istio?

A) It runs in Python.
B) Its data plane proxy is custom-built entirely in Rust, avoiding garbage collection pauses and providing memory safety with a drastically smaller footprint than Envoy.
C) It doesn't encrypt traffic.
D) It only runs on Windows.

**Answer**: B
**Explanation**: Rust's zero-cost abstractions and memory safety allow Linkerd's micro-proxy to consume barely any memory (often under 10MB per pod) while delivering sub-millisecond latency.

---

## Q3: Sidecar Race Conditions
In a Kubernetes Service Mesh environment, why might a Spring Boot application crash with a network error immediately upon startup?

A) The Java code is corrupted.
B) The database deleted itself.
C) A race condition occurred where the Java container booted up and attempted an outbound network call before the sidecar proxy container was fully initialized and ready to route traffic.
D) Spring Boot is incompatible with Kubernetes.

**Answer**: C
**Explanation**: Because K8s starts all containers in a Pod simultaneously, the fast-booting app might try to use the network before the proxy has established its internal iptables routing rules.