# 05 - Micronaut Learning

Modular learning modules for the Micronaut framework. Covers Micronaut basics (DI, AOP, configuration), data access with Micronaut Data, HTTP server and client, security (OAuth2/JWT), Kafka integration, gRPC services, and AWS deployment.

## Prerequisites

- Java 11+
- Maven 3.x

## Key Concepts

- Micronaut dependency injection and AOP
- HTTP controllers with `@Controller`, `@Get`, `@Post`, `@PathVariable`, `@Body`
- Micronaut Data with compile-time repository generation
- Security with OAuth 2.0 and JWT
- Kafka producer/consumer integration
- gRPC service definition and implementation
- AWS Lambda deployment

## Module Structure

- `01-micronaut-basics/` - DI, HTTP controllers, configuration
- `02-micronaut-data/` - Data repositories and JDBC
- `03-micronaut-http/` - HTTP client, declarative clients
- `03-micronaut-security/` - OAuth2, JWT, security rules
- `04-micronaut-kafka/` - Kafka messaging
- `05-micronaut-grpc/` - gRPC services
- `06-micronaut-aws/` - AWS Lambda deployment

## Learning Objectives

- Build REST APIs with Micronaut controllers
- Implement data access with Micronaut Data
- Secure applications with Micronaut Security
- Integrate messaging and gRPC

## Estimated Time

- 6-10 hours across all submodules

## How to Build

```bash
cd 05-micronaut-learning
mvn clean package
```

Run individual submodules:

```bash
cd 01-micronaut-basics
mvn mn:run
```
