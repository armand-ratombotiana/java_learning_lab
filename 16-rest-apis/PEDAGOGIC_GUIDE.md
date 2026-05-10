# REST APIs - Pedagogic Guide

## Teaching Strategy

### Module Overview
This module teaches REST API design, implementation patterns, and management features. Students learn to build robust APIs with versioning, caching, rate limiting, and documentation.

### Suggested Learning Path
1. **Day 1**: REST Fundamentals & Design
   - REST principles and constraints
   - Resource identification
   - HTTP methods usage
   - Status codes

2. **Day 2**: HTTP Client Implementation
   - Java HTTP client usage
   - Request/response handling
   - Error handling
   - Connection management

3. **Day 3**: API Versioning & Evolution
   - Versioning strategies comparison
   - URI versioning implementation
   - Header and query param versioning
   - Deprecation strategies

4. **Day 4**: API Management
   - Rate limiting algorithms
   - Caching strategies
   - Request logging
   - Correlation tracking

5. **Day 5**: Advanced Patterns
   - API Gateway pattern
   - OpenAPI documentation
   - Async API clients
   - Error standardization

## Teaching Methods

### Lecture Style
- Start with real-world API examples (GitHub, Stripe APIs)
- Show good and bad API designs
- Live coding of client implementation
- Explain each pattern with use case

### Design Patterns
- Use industry-standard APIs as examples
- Compare approach differences (e.g., versioning strategies)
- Discuss trade-offs

### Common Pitfalls to Address
1. Using wrong HTTP methods
2. Not following REST conventions
3. Improper error handling
4. Missing versioning strategy
5. Security misconfiguration

## Hands-on Exercises

| Exercise | Difficulty | Est. Time | Key Concept |
|----------|------------|-----------|--------------|
| 1 | Basic | 45 min | REST Client |
| 2 | Basic | 45 min | Versioning |
| 3 | Intermediate | 45 min | Rate Limiting |
| 4 | Intermediate | 45 min | Caching |
| 5 | Intermediate | 45 min | Logging |
| 6 | Advanced | 60 min | Pagination |
| 7 | Advanced | 45 min | Error Handling |
| 8 | Advanced | 60 min | OpenAPI |
| 9 | Expert | 60 min | Async Client |
| 10 | Expert | 75 min | API Gateway |

## Assessment Criteria
- Students can design RESTful APIs
- Students implement HTTP clients
- Students use versioning correctly
- Students implement rate limiting and caching
- Students create API documentation

## Recommended Projects

### Mini-Project (3-4 hours)
REST Client Implementation - Build a complete REST client with retry, timeout, and logging.

### Real-World Project (8+ hours)
API Versioning System - Implement all versioning strategies with rate limiting, caching, and documentation.

## Resources
- REST API Design: https://restfulapi.net/
- HTTP Status Codes: https://developer.mozilla.org/en-US/docs/Web/HTTP/Status
- OpenAPI Specification: https://swagger.io/specification/

## Time Allocation
- Theory: 30%
- Coding: 40%
- Exercises: 30%

## Prerequisites
- HTTP protocol understanding
- JSON data format
- Java networking basics
- Design patterns knowledge