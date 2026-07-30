# Interview Questions — Service Mesh

## Q1: What is the difference between control plane and data plane in Istio?
**A:** Control plane (Istiod) manages configuration, certificate distribution, and service discovery. Data plane (Envoy sidecars) intercepts and manages all service-to-service traffic.

## Q2: How does mTLS work in Istio?
**A:** Istiod issues X.509 certificates to each Envoy sidecar. Sidecars authenticate each other using these certs, encrypting all traffic with TLS. This is transparent to the application code.

## Q3: What is fault injection used for?
**A:** To test system resilience by intentionally introducing errors (delays, HTTP 500s) into the mesh. Validates that retries, timeouts, and circuit breakers work as expected.

## Q4: How does Envoy implement circuit breaking?
**A:** Envoy tracks upstream circuit breaker thresholds (max connections, pending requests, retries). When exceeded, it fails fast without sending traffic to the overloaded service.
