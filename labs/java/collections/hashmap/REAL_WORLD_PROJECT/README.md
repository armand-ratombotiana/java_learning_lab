# Real-World Project: Distributed Hash Table (DHT) Node

## 🎯 Objective
Simulate a node in a Distributed Hash Table (like Amazon's Dynamo or Cassandra). This moves hashing from a single-machine data structure to a distributed systems concept.

## 📝 Requirements
1. **Consistent Hashing**: Implement a consistent hashing ring.
2. **Virtual Nodes**: Implement virtual nodes to ensure even data distribution across physical servers.
3. **Data Routing**: Given a string key, hash it and determine exactly which physical server (node) is responsible for storing it.
4. **Node Addition/Removal**: Simulate adding a new server or removing a dead server, and calculate exactly which keys need to be rebalanced/moved.

## 🛠️ Technical Specs
- Use a `TreeMap` (or custom binary search tree) to represent the hash ring.
- Use MD5 or SHA-1 for the hashing algorithm (simulated) to place both servers and data on the ring.
- Build a simulation harness that distributes 1,000,000 keys across 5 nodes, then adds a 6th node and prints the metrics of data movement.