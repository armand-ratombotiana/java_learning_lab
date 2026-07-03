# Module 62: Edge Computing & IoT - Edge Cases & Pitfalls

---

## Pitfall 1: Assuming Constant Cloud Connectivity

### ❌ Wrong
Writing a Java Edge application that receives a local sensor event, attempts an HTTP POST to AWS, and drops the data or crashes if the HTTP request times out.

### ✅ Correct
Edge networks (like cellular 4G/5G or satellite) drop frequently. Edge Gateways must operate under the "Store and Forward" paradigm. If the cloud connection drops, the Java application must queue the aggregated data to a local disk buffer (e.g., using a local SQLite DB or embedded ActiveMQ). When the connection is restored, the buffered messages are synchronized to the cloud.

---

## Pitfall 2: Over-provisioning Cloud Resources for Raw Data

### ❌ Wrong
Configuring a fleet of 10,000 IoT devices to stream their raw, uncompressed, millisecond-resolution telemetry data directly into a central AWS Kafka cluster, resulting in a $50,000/month bandwidth and storage bill.

### ✅ Correct
Apply **Edge Analytics**. The Edge Gateway should process the raw stream locally using a sliding window. It should only forward data to the cloud if a threshold is breached (e.g., Temperature > 100°C), or it should forward a compressed 5-minute average summary.

---

## Pitfall 3: Failing to Manage Edge Deployments (Over-the-Air Updates)

### ❌ Wrong
Deploying a compiled `.jar` file to an Edge device manually via SSH, and providing no mechanism to update it automatically. If a critical security flaw is found in the Java code, a technician must physically drive to 1,000 factories to update the software.

### ✅ Correct
Treat Edge devices like distributed Kubernetes nodes. Use an OTA (Over-The-Air) update manager (like AWS IoT Greengrass or K3s edge deployments) that allows you to push a new Docker container or GraalVM binary to the entire fleet securely and automatically from a central dashboard.