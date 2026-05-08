# 48 - Reactive Streams

Reactive streams provide a standard for asynchronous stream processing with backpressure, enabling non-blocking handling of data streams with proper flow control.

## Overview

- **Topic**: Reactive Stream Processing
- **Prerequisites**: Java 9+, concurrency
- **Duration**: 2-3 hours

## Key Concepts

- Java 9 Flow API
- Backpressure handling
- Publisher/Subscriber model
- Hot vs cold streams

## Getting Started

Run the training code:
```bash
cd 48-reactive-streams
mvn compile exec:java -Dexec.mainClass=com.learning.reactive.Lab
```

## Module Contents

- Flow API implementation
- Backpressure patterns
- Reactive operators
- Integration with Spring/Reactor