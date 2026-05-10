# Spring Security - Pedagogic Guide

## Teaching Strategy

### Module Overview
This module teaches authentication, authorization, and security patterns in Spring applications. Students progress from basic form authentication to advanced OAuth2 and enterprise security patterns.

### Suggested Learning Path
1. **Day 1**: Security Fundamentals
   - Security filter chain
   - Authentication basics
   - Password encoding
   - Basic configuration

2. **Day 2**: JWT Authentication
   - JWT token structure
   - Token generation and validation
   - Stateless authentication
   - Refresh tokens

3. **Day 3**: Authorization
   - Role-based access control
   - Method-level security
   - Custom permission evaluators
   - Access control lists

4. **Day 4**: Advanced Security
   - OAuth2/OIDC
   - Custom authentication providers
   - Account lockout
   - Audit logging

5. **Day 5**: Enterprise Patterns
   - CORS configuration
   - CSRF protection
   - Session management
   - Security testing

## Teaching Methods

### Real-World Scenarios
- E-commerce platform security
- Banking application protection
- API security for mobile apps

### Code-First Approach
- Start with working configuration
- Explain each component
- Show security flow
- Demonstrate attack prevention

### Common Pitfalls to Address
1. Weak password storage
2. Improper JWT handling
3. Missing CSRF protection
4. Over-permissive authorization
5. Security misconfiguration

## Hands-on Exercises

| Exercise | Difficulty | Est. Time | Key Concept |
|----------|------------|-----------|--------------|
| 1 | Basic | 45 min | Basic Config |
| 2 | Basic | 60 min | JWT Auth |
| 3 | Basic | 45 min | Password Encoding |
| 4 | Intermediate | 45 min | Method Security |
| 5 | Intermediate | 60 min | OAuth2 |
| 6 | Advanced | 60 min | Custom Provider |
| 7 | Advanced | 45 min | Account Lockout |
| 8 | Advanced | 45 min | Audit Logging |
| 9 | Advanced | 30 min | CORS |
| 10 | Expert | 45 min | CSRF |

## Assessment Criteria
- Students can configure Spring Security
- Students implement JWT authentication
- Students apply authorization patterns
- Students secure APIs properly
- Students implement audit logging

## Recommended Projects

### Mini-Project (5-6 hours)
JWT Authentication System - Complete authentication with JWT, role-based access, and account protection.

### Real-World Project (15+ hours)
E-Commerce Security System - OAuth2, audit logging, account management, method security.

## Resources
- Spring Security Docs: https://docs.spring.io/spring-security/reference/
- JWT.io: https://jwt.io/
- OWASP Top 10: https://owasp.org/www-project-top-ten/

## Time Allocation
- Theory: 25%
- Live Coding: 40%
- Exercises: 35%

## Prerequisites
- Spring fundamentals
- HTTP/REST concepts
- Basic cryptography (helpful)
- Spring Boot knowledge