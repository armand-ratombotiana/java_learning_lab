# Spring Security - Authentication & Authorization

## Overview
Spring Security provides comprehensive security services for Spring applications. This module covers authentication (JWT, OAuth2), authorization, method-level security, and advanced security patterns for enterprise applications.

## Key Concepts
- **Authentication**: JWT, OAuth2, LDAP, Database
- **Authorization**: Roles, permissions, method security
- **Security Filters**: Filter chain, custom filters
- **Password Encoding**: BCrypt, Argon2
- **Session Management**: Stateless vs stateful
- **CSRF Protection**: Token-based protection
- **Method Security**: @PreAuthorize, @Secured
- **OAuth2/OIDC**: Resource server, authorization server

## Project Structure
```
20-spring-security/
├── pom.xml
├── src/main/java/com/learning/
│   ├── Main.java
│   ├── config/
│   ├── controller/
│   ├── service/
│   ├── filter/
│   └── model/
```

## Running
```bash
cd 20-spring-security
mvn clean compile
mvn spring-boot:run
```

## Concepts Covered
- JWT token generation and validation
- Custom authentication filter
- BCrypt password encoding
- Role-based access control (RBAC)
- Method-level security
- OAuth2 Resource Server
- Session management
- CSRF protection
- Audit logging
- Account lockout
- Custom authentication provider

## Dependencies
- Spring Security
- Spring Boot Starter Security
- Spring Boot Starter OAuth2 Resource Server
- jjwt (JWT library)
- H2 Database