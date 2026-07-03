# Module 62: Edge Computing & IoT - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: What is the fundamental difference between Edge Computing and Fog Computing?
**Answer**:
Both concepts aim to move computation closer to the data source to reduce latency and bandwidth usage. 
- **Edge Computing** typically pushes the computation all the way to the extreme edge—directly onto the device itself (e.g., an autonomous vehicle computing its radar data on its own internal GPU).
- **Fog Computing** (a term coined by Cisco) refers to a decentralized architecture where the compute nodes are placed on the local network (LAN) but not on the devices themselves. For example, 1,000 "dumb" sensors in a factory floor stream raw data to a powerful "Fog Node" (a local server rack sitting in the factory's back room) which performs the heavy analytics and then selectively talks to the Cloud.

### Q2: Why is JSON often avoided in high-frequency Edge/IoT environments, and what are the alternatives?
**Answer**:
JSON is a text-based format. It is highly verbose, meaning it wastes a significant amount of network bandwidth transmitting repeating keys (e.g., `{"temperature": 25.5}`). It also requires CPU overhead to parse string characters into binary machine data. On constrained, battery-operated devices using expensive cellular networks, this overhead is unacceptable.
**Alternatives**: Binary serialization formats like **Protocol Buffers (Protobuf)**, **FlatBuffers**, or **CBOR**. These encode data compactly without keys and can be deserialized by the CPU almost instantly, drastically saving bandwidth and battery life.

### Q3: How does the "Store and Forward" architecture provide resilience in Edge environments?
**Answer**:
Edge devices are often deployed in environments with spotty internet connectivity (e.g., a cargo ship crossing the ocean, or a smart tractor on a rural farm). If the device attempts to stream data to AWS synchronously and the connection drops, data is lost.
The Store and Forward architecture requires the Edge device to maintain a persistent local buffer (like an embedded database or local message queue). It constantly *stores* telemetry locally. A background thread continually attempts to connect to the cloud. When a stable connection is verified, it *forwards* the buffered data and marks it as synced. This guarantees zero data loss during network outages.

---

## 💻 Whiteboarding Scenarios

### Scenario 1: Handling Data Floods at the Edge
**Problem**: An interviewer presents this scenario: "We are monitoring vibrations on an industrial pipeline. 10 sensors are capturing vibration data at 1,000 Hertz (1,000 readings per second per sensor). We need to detect anomalies, but streaming 10,000 messages per second to the cloud over a cellular connection is crashing our routers. How do you re-architect this using Edge Computing?"

**Solution**:
You cannot send raw data. You must implement **Edge Analytics** using an aggregation pipeline.
1. Deploy a small Java Edge Gateway (using GraalVM) on the pipeline site.
2. The sensors send data locally (via Ethernet or short-range MQTT) to the Gateway.
3. The Gateway implements a streaming engine (like Apache Flink or a custom Ring Buffer).
4. **The Aggregation Rule**: Group the readings into a 1-second Tumbling Window. Calculate the Max, Min, and Average vibration for that second.
5. **The Anomaly Rule**: If the Max vibration exceeds the safety threshold, immediately trigger a local physical alarm (zero latency) AND send a high-priority "CRITICAL ALERT" message to the cloud.
6. **The Telemetry Rule**: If the vibrations are normal, discard the 10,000 raw readings. Instead, package the 1-second Average calculations into a single, compressed JSON/Protobuf payload and send 1 message to the cloud every 5 minutes.