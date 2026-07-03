# 24 - GraalVM Native Image

GraalVM native image compilation concepts. Covers AOT (ahead-of-time) compilation, reflection configuration (reflect-config.json), resource configuration, serialization support, dynamic proxy configuration, runtime vs build-time initialization, and native image build process.

## Prerequisites

- Java 11+
- Maven 3.x
- GraalVM SDK (optional for native compilation)

## Key Concepts

- AOT compilation: native binary from bytecode, no JIT warmup needed
- Reflection configuration: reachability metadata, reflect-config.json
- Resource configuration: resource-config.json for classpath resources
- Serialization configuration: serialization-config.json
- Runtime vs build-time initialization: `--initialize-at-build-time`, `--initialize-at-run-time`
- Dynamic proxy configuration
- JVM vs native image comparison (startup time, memory, peak performance)

## Module Structure

- `graalvm-native-image/` - GraalVM native image configuration concepts

## Learning Objectives

- Understand the GraalVM native image compilation pipeline
- Configure reflection, resources, and serialization for native images
- Compare JVM vs native image trade-offs

## Estimated Time

- 1-2 hours

## How to Build

```bash
cd 24-graalvm-native
mvn clean package
```

Run the lab:

```bash
cd graalvm-native-image
mvn compile exec:java -Dexec.mainClass="com.learning.graalvm.Lab"
```
