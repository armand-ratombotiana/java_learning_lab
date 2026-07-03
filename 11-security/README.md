# 11 - Security (OAuth 2.0)

Authentication and authorization using OAuth 2.0 and Spring Security. Covers OAuth 2.0 login flow, JWT resource server configuration, security filter chains, role-based access control, and protected API endpoint configuration.

## Prerequisites

- Java 11+
- Maven 3.x
- Spring Boot

## Key Concepts

- OAuth 2.0 authorization code flow
- JWT (JSON Web Token) validation
- Spring Security filter chain
- `oauth2Login()` configuration for social login
- `oauth2ResourceServer()` with JWT decoder
- Role-based access to endpoints (public vs authenticated)
- `SecurityFilterChain` bean configuration

## Module Structure

- `01-oauth2/` - Spring Security OAuth 2.0 integration

## Learning Objectives

- Configure OAuth 2.0 login and JWT resource server
- Secure REST endpoints with Spring Security
- Understand OAuth 2.0 grant types and token flows

## Estimated Time

- 2-3 hours

## How to Build

```bash
cd 11-security
mvn clean package
```

Run the Spring Boot application:

```bash
cd 01-oauth2
mvn spring-boot:run
```
