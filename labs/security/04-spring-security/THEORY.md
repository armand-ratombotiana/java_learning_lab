# Theory: 04-spring-security

## Core Concepts

### Spring Security architecture, security filters, method security

Security in Java applications is built on foundational concepts that work together to protect resources and data.

### Key Principles

1. **Defense in Depth** - Multiple layers of security controls ensure that if one layer fails, others still provide protection.

2. **Least Privilege** - Every component should operate with the minimum set of permissions necessary to function.

3. **Fail Secure** - When a security control fails, it should default to a denied state rather than an allowed state.

4. **Separation of Concerns** - Security logic should be separated from business logic.

### Spring Security Integration

Spring Security provides a comprehensive framework that implements these principles through its filter chain architecture, authentication providers, and authorization managers.

### Java Platform Support

The Java platform provides:

- Java Cryptography Architecture (JCA) for cryptographic operations
- Java Authentication and Authorization Service (JAAS)
- Java Secure Socket Extension (JSSE) for TLS/SSL

## Detailed Analysis

### Authentication

Authentication verifies the identity of a user or system. Spring Security supports multiple authentication mechanisms including form login, HTTP Basic, OAuth2, and SAML.

### Authorization

Authorization determines what an authenticated user is allowed to do. This can be role-based, scope-based, or attribute-based.

### Cryptography

Cryptographic operations ensure data confidentiality, integrity, and authenticity. Java provides strong cryptographic support through the JCA framework.
