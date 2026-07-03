# Module 43: Service Mesh - Quizzes

---

## Q1: Architecture
In a Service Mesh architecture, what acts as the "Data Plane"?

A) The central controller that issues configurations.
B) The database that stores the service logs.
C) The lightweight sidecar proxies deployed alongside each microservice.
D) The API Gateway handling external internet traffic.

**Answer**: C
**Explanation**: The Data Plane consists of sidecar proxies (like Envoy) attached to every service instance. They intercept, route, and encrypt all inbound and outbound network traffic for that specific service.

---

## Q2: Service Mesh vs API Gateway
What is the primary difference in the intended use cases of an API Gateway and a Service Mesh?

A) API Gateways manage internal East-West traffic, while Service Meshes manage external North-South traffic.
B) API Gateways manage external North-South traffic coming into the cluster, while Service Meshes manage internal East-West traffic between microservices.
C) API Gateways are only used for monoliths.
D) Service Meshes are only used for databases.

**Answer**: B
**Explanation**: An API Gateway is the front door to the system (North-South). A Service Mesh secures and routes the internal communications (East-West) that happen *after* the request passes the Gateway.

---

## Q3: Security (mTLS)
What benefit does Mutual TLS (mTLS) provide when enabled via a Service Mesh?

A) It encrypts the user's password in the database.
B) It automatically encrypts and authenticates all network traffic between internal microservices without any code changes to the applications.
C) It prevents SQL injection attacks.
D) It speeds up database queries.

**Answer**: B
**Explanation**: mTLS ensures that internal network traffic is encrypted and that services can mathematically prove their identity to one another, achieving a "Zero Trust" network model purely through infrastructure configuration.