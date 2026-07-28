# Lab 07: Mock Interview — Data Mesh Architect

**Interviewer**: "Your company has a centralized data lake with 100 engineers. How do you migrate to a data mesh without breaking existing pipelines?"

**Candidate**: "I'd take an incremental approach: first, identify bounded contexts — natural domain boundaries like payments, marketing, inventory. Each domain gets ownership of their raw data and defined output products. The first migration target is a domain that's already relatively independent. We'd run old and new pipelines in parallel for one month."

**Interviewer**: "How do you prevent the data mesh from becoming just 50 separate silos?"

**Candidate**: "The key is the federated governance layer. We define global standards: common schema for data product metadata, standardized discovery APIs, and cross-domain data contracts. The data platform team provides the self-serve infrastructure — a shared catalog, a data product template, and monitoring tools. Each domain can innovate locally but must adhere to the interoperability standards to publish data products."

**Interviewer**: "What are the compute implications of a data mesh?"

**Candidate**: "Each domain may run their own compute. That means no more centralized Spark clusters. We'd adopt a multi-cluster model with Kubernetes — each domain gets a namespace with their own Spark operator. The data platform team manages the shared storage layer (object store + Hive Metastore). Cost allocation becomes per-namespace with chargeback."

**Interviewer**: "How do you handle a data product that fails its SLA (latency, completeness)?"

**Candidate**: "Each data product emits health metrics to a shared mesh monitoring system. When a product fails its SLA, the platform sends alerts to the owning domain. Downstream consumers can check the product's health before consuming. We also support graceful degradation: if a product is stale, the consumer can use the last known good snapshot or skip that product."
