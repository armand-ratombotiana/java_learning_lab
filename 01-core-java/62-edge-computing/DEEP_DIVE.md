# Module 62: Edge Computing & IoT with Java - Deep Dive

**Difficulty Level**: Advanced  
**Prerequisites**: Modules 01-61 (especially Networking, Cloud, and Message Queues)  
**Estimated Reading Time**: 60 minutes  

---

## 📚 Table of Contents

1. [What is Edge Computing?](#intro)
2. [IoT Protocols (MQTT, CoAP)](#protocols)
3. [Java at the Edge (GraalVM & MicroProfile)](#java-edge)
4. [Data Aggregation and Filtering](#aggregation)
5. [Security at the Edge](#security)

---

## 1. What is Edge Computing? <a name="intro"></a>
Cloud computing centralizes data processing in massive data centers. Edge computing reverses this by moving compute power, storage, and analytics physically closer to the source of data generation (e.g., smart thermostats, self-driving cars, factory sensors). 
This drastic reduction in distance eliminates network latency, saves bandwidth (by not sending raw data to the cloud), and allows critical systems to function even if the internet connection to the central cloud drops.

---

## 2. IoT Protocols (MQTT, CoAP) <a name="protocols"></a>
Edge devices often operate on battery power and unreliable, low-bandwidth cellular networks. Standard HTTP is too heavy.
- **MQTT (Message Queuing Telemetry Transport)**: An extremely lightweight Pub/Sub messaging protocol. It runs over TCP and uses a central broker. It has minimal packet overhead, making it the dominant IoT protocol.
- **CoAP (Constrained Application Protocol)**: A specialized web transfer protocol for constrained nodes and networks. It runs over UDP and uses a RESTful architectural style similar to HTTP, but heavily compressed.

---

## 3. Java at the Edge (GraalVM & MicroProfile) <a name="java-edge"></a>
Historically, Java was too heavy (requiring a 200MB JRE) to run on a Raspberry Pi or an embedded factory sensor. 
Today, Java is highly viable at the Edge:
- **GraalVM**: Compiles Java into a standalone native binary (`< 50MB`), completely eliminating the JRE requirement and allowing instant startup.
- **MicroProfile / Quarkus**: Frameworks specifically designed to compile natively and run with ultra-low memory footprints, tailored for Edge and Kubernetes environments.

---

## 4. Data Aggregation and Filtering <a name="aggregation"></a>
A factory might have 1,000 temperature sensors emitting readings every millisecond. Sending 1,000,000 messages per second to AWS over a 4G connection is impossibly expensive.
The **Edge Gateway** (a small server running on the factory floor, perhaps written in Java) subscribes to all local sensors via MQTT. It aggregates the data, calculates the average temperature over a 1-minute window, and sends only *one* JSON payload per minute to the central Cloud.

---

## 5. Security at the Edge <a name="security"></a>
Edge devices are physically accessible to attackers (someone can literally unscrew a smart doorbell). 
- Zero Trust architectures are required.
- Hardcoded passwords or AWS keys inside Edge Java code will be instantly compromised if the device is stolen and reverse-engineered.
- Hardware-backed security (like TPM chips) or short-lived, dynamically rotated TLS certificates are standard.