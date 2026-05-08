# GraalVM Native Image

Learn GraalVM Native Image for fast startup times.

## Overview
GraalVM Native Image compiles Java applications into standalone native executables. This provides near-instant startup and reduced memory footprint compared to the JVM.

## Key Features
- Ahead-of-time compilation
- Fast startup time
- Reduced memory footprint
- Spring Boot native support
- Native executable output

## Build & Run
```bash
mvn -Pnative spring-boot:run-no-fork
# or
mvn clean package -Pnative
```

## Version
- Spring Boot: Latest
- Java: 21