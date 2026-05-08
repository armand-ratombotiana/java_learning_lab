# 39 - Message Queues

Message queues enable asynchronous communication between services, decouple producers and consumers, and provide reliability, ordering, and fault tolerance.

## Overview

- **Topic**: Message Queue Implementation
- **Prerequisites**: Java concurrency, distributed systems basics
- **Duration**: 2-3 hours

## Key Concepts

- Producer/Consumer patterns
- Queue types (FIFO, priority, dead-letter)
- Message acknowledgment and persistence
- Pub/Sub and point-to-point models

## Getting Started

Run the training code:
```bash
cd 39-message-queues
mvn compile exec:java -Dexec.mainClass=com.learning.messagequeues.Lab
```

## Module Contents

- Message queue fundamentals
- Java Message Service (JMS)
- Implementation patterns
- Queue management and monitoring