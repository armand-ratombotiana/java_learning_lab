# Module 35: System Design - Deep Dive

**Difficulty Level**: Advanced  
**Prerequisites**: Modules 01-34 (especially Cloud, Microservices, and Databases)  
**Estimated Reading Time**: 60 minutes  

---

## 📚 Table of Contents

1. [Introduction to System Design](#intro)
2. [Scalability (Vertical vs Horizontal)](#scalability)
3. [Load Balancing and Caching](#load-balancing)
4. [Database Scaling (Sharding & Replication)](#database)
5. [CAP Theorem](#cap)

---

## 1. Introduction to System Design <a name="intro"></a>
System Design is the process of defining the architecture, modules, interfaces, and data for a system to satisfy specified requirements. It requires analyzing trade-offs between latency, throughput, consistency, availability, and cost.

---

## 2. Scalability (Vertical vs Horizontal) <a name="scalability"></a>
- **Vertical Scaling (Scale Up)**: Adding more power (CPU, RAM) to an existing machine. It is easy but has a hard upper limit and a single point of failure.
- **Horizontal Scaling (Scale Out)**: Adding more machines into your pool of resources. This provides redundancy and limitless scaling but requires distributed systems architecture.

---

## 3. Load Balancing and Caching <a name="load-balancing"></a>
- **Load Balancers**: Distribute incoming network traffic across multiple servers (e.g., Nginx, HAProxy, AWS ALB) to ensure no single server bears too much demand.
- **Caching**: Storing frequently accessed data in fast memory (e.g., Redis, Memcached) to reduce latency and database load.

---

## 4. Database Scaling (Sharding & Replication) <a name="database"></a>
- **Replication**: Copying data from a primary (master) node to replica (slave) nodes. Used for read-heavy workloads (writes to primary, reads from replicas) and fault tolerance.
- **Sharding**: Distributing data across multiple databases (horizontal partitioning). Useful when data volume exceeds the capacity of a single machine.

---

## 5. CAP Theorem <a name="cap"></a>
The CAP Theorem states that a distributed data store can only simultaneously provide two out of the following three guarantees:
1. **Consistency**: Every read receives the most recent write or an error.
2. **Availability**: Every request receives a non-error response, without guaranteeing it contains the most recent write.
3. **Partition Tolerance**: The system continues to operate despite an arbitrary number of messages being dropped or delayed by the network.

*Note: Since network partitions (P) are unavoidable in distributed systems, architects must choose between Consistency (CP) and Availability (AP).*