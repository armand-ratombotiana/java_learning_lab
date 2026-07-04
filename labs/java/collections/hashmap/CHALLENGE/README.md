# HashMap Challenge: The DoS Attack Simulator

## 🎯 Objective
Understand the security implications of hash functions by simulating a Hash Collision Denial of Service (DoS) attack.

## 📝 Background
Before Java 8 introduced treeification, Web Servers (like Tomcat) were vulnerable to a specific DoS attack. If an attacker sent an HTTP POST request with thousands of parameters that all hashed to the exact same bucket index, the server's `HashMap` (used to store request parameters) would degrade into a massive linked list. Inserting the next parameter would take O(n) time, maxing out CPU utilization and crashing the server.

## ⚔️ The Challenge
1. **The Generator**: Write an algorithm that generates 50,000 distinct string keys that all produce the exact same `hashCode()` in Java (e.g., `"AaAaAa"`, `"BBAaBB"`).
2. **The Benchmark**: 
    - Run your application on **Java 7** (or simulate the pre-Java 8 behavior by disabling treeification in a custom map). Measure the time to insert these 50,000 colliding keys.
    - Run the same application on **Java 21**. Measure the time to insert the same 50,000 keys.
3. **The Analysis**: Write a brief report on the CPU time difference and explain exactly how Red-Black trees mitigated this attack vector.