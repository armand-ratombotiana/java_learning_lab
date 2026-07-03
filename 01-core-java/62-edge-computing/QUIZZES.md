# Module 62: Edge Computing & IoT - Quizzes

---

## Q1: Edge vs Cloud
What is the primary architectural driver for moving computing workloads from the centralized Cloud to the Edge?

A) To increase the amount of raw data stored in cloud databases.
B) To reduce network latency, save bandwidth costs, and allow localized systems to operate autonomously during internet outages.
C) Because cloud computing is obsolete.
D) To make the applications easier to hack.

**Answer**: B
**Explanation**: In time-critical applications (like autonomous driving or factory safety shutdown systems), waiting 150ms for a round-trip HTTP request to AWS is unacceptable. Processing data physically on the device (Edge) drops latency to microseconds and guarantees execution even without an internet connection.

---

## Q2: IoT Protocols
Why is MQTT preferred over standard HTTP for IoT sensor communication?

A) MQTT natively encrypts all traffic.
B) MQTT is a synchronous protocol that guarantees order.
C) MQTT operates over a publish/subscribe model, features an incredibly small packet header overhead (saving battery and bandwidth on constrained cellular networks), and handles intermittent connection drops gracefully.
D) MQTT relies on the HTTP/2 specification.

**Answer**: C
**Explanation**: HTTP is a heavy, text-based, synchronous protocol. MQTT is binary, lightweight, asynchronous, and designed specifically for unreliable networks with high latency and low bandwidth.

---

## Q3: Java at the Edge
How does GraalVM make Java viable for constrained Edge devices (like a Raspberry Pi)?

A) By compressing the source code.
B) By compiling the Java application Ahead-Of-Time (AOT) into a standalone native binary, completely eliminating the massive memory overhead of the traditional JVM and providing instant startup times.
C) By rewriting the Java code in Python.
D) By allowing Java to run inside an HTML browser.

**Answer**: B
**Explanation**: Historically, the JVM required hundreds of megabytes of RAM just to start, making it unsuitable for small edge devices. GraalVM native images require no JVM, use single-digit megabytes of RAM, and execute instantly.