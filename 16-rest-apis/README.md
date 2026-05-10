# REST APIs - Design & Implementation Patterns

## Overview
This module covers REST API design principles, implementation patterns, client development, and advanced API management features including versioning, rate limiting, caching, and documentation.

## Key Concepts
- **REST Principles**: Resource-oriented, stateless, hypermedia
- **API Design**: URL structure, HTTP methods, status codes
- **API Versioning**: URI, header, query parameter strategies
- **Rate Limiting**: Token bucket, sliding window algorithms
- **Caching**: ETag, Cache-Control, conditional requests
- **Documentation**: OpenAPI/Swagger, API first design

## Project Structure
```
16-rest-apis/
├── pom.xml
├── src/main/java/com/learning/
│   ├── Main.java
│   ├── client/
│   ├── gateway/
│   ├── versioning/
│   └── cache/
```

## Running
```bash
cd 16-rest-apis
mvn clean compile
mvn exec:java -Dexec.mainClass="com.learning.Main"
```

## Concepts Covered
- REST API Design Principles
- HTTP Client Implementation
- API Versioning Strategies
- Rate Limiting Implementation
- Request/Response Logging
- Response Caching
- API Documentation (OpenAPI)
- Error Handling Patterns
- Pagination & Filtering
- HATEOAS Implementation

## Dependencies
- Java HTTP Client (JDK 11+)
- Gson for JSON parsing
- OpenAPI Generator (optional)