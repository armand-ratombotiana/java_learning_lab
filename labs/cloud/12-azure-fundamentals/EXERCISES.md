# 12 - Azure Fundamentals - Exercises

## Overview
Practice implementing Azure cloud services including VMs, Blob Storage, Azure SQL, AKS, and managed identities through these hands-on exercises.

## Exercise 1: Basic Implementation
Create a Java program that demonstrates core Azure Fundamentals concepts.

**Requirements:**
- Implement at least 3 main classes with meaningful methods
- Use Java 21 features (records, sealed classes, pattern matching)
- Include proper error handling and logging
- Write JUnit 5 tests with at least 5 test cases

**Starter Code:**
`java
package com.cloud.azurefun;

public class Exercise1 {
    // TODO: Implement core functionality
}
`

## Exercise 2: Integration Pattern
Build a system that integrates multiple components related to Azure cloud services including VMs, Blob Storage, Azure SQL, AKS, and managed identities.

**Requirements:**
- Connect at least 3 different service simulations
- Implement proper data flow between components
- Include configuration management and metrics collection

## Exercise 3: Error Handling
Implement comprehensive error handling for edge cases.

**Tasks:**
- Handle null inputs gracefully
- Implement retry logic with exponential backoff
- Add circuit breaker pattern
- Create proper error response structures

## Exercise 4: Performance Optimization
Profile and optimize your implementation.

**Tasks:**
1. Identify bottlenecks using JFR or Async Profiler
2. Implement caching where appropriate
3. Optimize connection pooling
4. Measure before/after performance

## Exercise 5: Production Readiness
Prepare your implementation for production deployment.

**Deliverables:**
- Dockerfile
- Health check endpoint
- Metrics endpoint (Prometheus format)
- Configuration externalization
- Graceful shutdown support

## Exercise 6: Security Hardening
Apply security best practices.

**Checklist:**
- [ ] Input validation on all public methods
- [ ] Proper exception handling (no generic Exception catches)
- [ ] Secure configuration defaults
- [ ] Audit logging for critical operations
- [ ] Rate limiting for exposed endpoints

## Submission Requirements
- All code must compile with Java 21
- All tests must pass (run: mvn test)
- Include brief write-up of design decisions
- Document any assumptions made
- Code coverage must exceed 70%
