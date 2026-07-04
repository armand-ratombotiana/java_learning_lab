# Service Mesh Flashcards

**Q: What is a service mesh?**
A: Infrastructure layer for service-to-service communication, security, and observability.

**Q: What is Envoy?**
A: High-performance L7 proxy used as the Istio data plane (sidecar).

**Q: What is Istiod?**
A: Unified Istio control plane (Pilot + Citadel + Galley).

**Q: What is a VirtualService?**
A: Defines routing rules (conditions, weights) for traffic to a service.

**Q: What is a DestinationRule?**
A: Defines traffic policies (load balancing, mTLS, circuit breaking) for subsets.

**Q: What is mTLS?**
A: Mutual TLS — both sides present certificates for authentication.

**Q: What is a Gateway in Istio?**
A: Edge proxy for ingress/egress traffic management.

**Q: What is Kiali?**
A: Istio's web UI for visualizing service mesh topology.

**Q: What is the xDS API?**
A: Envoy's dynamic configuration API (Listener, Cluster, Route, Endpoint discovery).

**Q: What is SPIFFE?**
A: Standard for service identity (e.g., spiffe://cluster.local/ns/default/sa/myapp).
