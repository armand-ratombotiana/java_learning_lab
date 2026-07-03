# Module 67: Multi-Cloud Architectures - Quizzes

---

## Q1: Data Gravity
Which concept describes the phenomenon where large datasets attract applications, services, and computing power to be located closer to them?

A) Data sovereignty
B) Vendor Lock-in
C) Data Gravity
D) Eventual Consistency

**Answer**: C
**Explanation**: Data Gravity implies that as data accumulates, it becomes hard and expensive to move. Therefore, it is almost always cheaper and faster to move the compute (the application) to the cloud where the data resides, rather than pulling the data across clouds to the application.

---

## Q2: The Multi-Cloud Abstraction
Which technology is widely considered the foundational abstraction layer that makes multi-cloud deployments feasible for application developers?

A) Apache Kafka
B) Kubernetes
C) AWS Lambda
D) Spring Boot

**Answer**: B
**Explanation**: Kubernetes provides a standardized API for container orchestration. A Kubernetes `Deployment` YAML looks exactly the same whether applied to AWS EKS, Google GKE, or Azure AKS, shielding the developer from the underlying cloud provider's proprietary APIs.

---

## Q3: Cloud Egress
In cloud computing billing models, what is typically true regarding data transfer (Ingress vs. Egress)?

A) Data Ingress (into the cloud) is free, but Data Egress (out of the cloud to the internet or another cloud) is heavily billed.
B) Data Egress is free, but Data Ingress is heavily billed.
C) Both are free.
D) Both are billed at exactly the same rate.

**Answer**: A
**Explanation**: Cloud providers want your data to enter their ecosystem (Ingress is free). However, they impose high Egress fees to discourage you from migrating your data to a competitor or splitting workloads across multiple clouds.