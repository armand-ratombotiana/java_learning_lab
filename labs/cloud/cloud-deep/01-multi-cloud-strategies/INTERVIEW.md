# Interview Questions — Multi-Cloud Strategies

## Beginner

Q: What is multi-cloud and how does it differ from hybrid cloud?
A: Multi-cloud uses multiple public cloud providers; hybrid cloud combines public and private cloud.

Q: Why would an organization adopt multi-cloud?
A: Avoid vendor lock-in, leverage best-of-breed services, regulatory compliance, fault tolerance.

## Intermediate

Q: How do you design a cloud-agnostic abstraction layer?
A: Define interfaces for compute, storage, networking; implement provider-specific adapters; use factory or strategy pattern.

Q: What are the challenges of multi-cloud networking?
A: Cross-cloud latency, NAT traversal, IP overlap, consistent security policies, data transfer costs.

## Advanced

Q: How would you implement active-active failover across three clouds?
A: Global load balancer (DNS/L7), stateless application tier, cross-cloud DB replication with CRDT or multi-master, health-probe-driven traffic steering.

Q: What is the trade-off between abstraction and provider-native features?
A: Abstraction limits access to provider-specific capabilities. Use abstraction for core primitives, expose provider-specific extensions via capability interfaces or feature flags.
