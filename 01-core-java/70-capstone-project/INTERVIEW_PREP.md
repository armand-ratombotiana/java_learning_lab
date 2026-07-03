# Module 70: Enterprise Capstone Project - Interview Preparation

---

## 📝 Architectural Defense

In a Senior or Staff-level interview, you are rarely asked to write code on a whiteboard. Instead, you are asked to design a system (like this Capstone) and then rigorously defend your architectural choices against hostile scenarios.

### Q1: The Network Partition Defense
**Interviewer**: "In your architecture, the Trading Engine validates the trade and publishes an event to Kafka. The Ledger service reads it. What happens if the network cable connecting the Trading Engine to the Kafka cluster is cut halfway through the transaction?"

**Your Defense**:
"Because I implemented the Transactional Outbox pattern, the system survives this gracefully. The Trading Engine writes the Order to the PostgreSQL database and writes the Event to an `outbox` table in the *same local database transaction*. 
If the network to Kafka is cut, the Outbox Poller simply fails to publish and leaves the event marked as `processed = false`. When the network heals 10 minutes later, the Poller picks up the backlog and publishes the events. No data is lost, and the databases eventually achieve consistency."

### Q2: The Traffic Spike Defense
**Interviewer**: "Elon Musk just tweeted about a specific stock. Your platform experiences a 10,000% spike in traffic in exactly 5 seconds. Walk me through the bottlenecks in your architecture and how it survives."

**Your Defense**:
"The first bottleneck is the API Gateway thread pool. Because I used Spring WebFlux (or Java 21 Virtual Threads), the gateway uses non-blocking I/O and will not exhaust its OS threads.
The second bottleneck is the Database. A 10,000% spike in writes will cause lock contention. To mitigate this, I implemented an asynchronous command model. The API does not write to the DB synchronously; it pushes the command to a Kafka topic and returns an HTTP 202 Accepted. Kafka acts as a shock absorber. The database insertion consumers will process the massive backlog at their maximum safe capacity without crashing the DB."

### Q3: The Data Corruption Defense
**Interviewer**: "A junior developer accidentally deployed a bug to the Trading Engine that calculates math incorrectly, resulting in 5,000 trades being executed at the wrong price before we caught it and rolled back. How do we fix the corrupt user balances?"

**Your Defense**:
"Because I used Event Sourcing for the Ledger, no state was irreversibly overwritten. We don't have to guess what the old balances were. 
To fix this, we deploy a Compensating Transaction script. We query the immutable Event Store for the 5,000 incorrect `TradeExecuted` events. For each one, we issue a `TradeReverted` event to reverse the math, and then append the correct `TradeExecuted` event. The CQRS read models will instantly process these new events and recalculate the user balances perfectly, preserving absolute cryptographic auditability."