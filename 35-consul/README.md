# 35 - Consul Learning Module

## Overview
Consul is a service mesh solution providing service discovery, health checking, and key-value store. This module covers Consul integration with Spring Boot.

## Module Structure
- `consul-service-discovery/` - Spring Cloud Consul implementation

## Technology Stack
- Spring Boot 3.x
- Spring Cloud Consul
- Consul Agent
- Maven

## Prerequisites
- Consul server running on `localhost:8500`

## Key Features
- Service discovery via DNS and HTTP API
- Health checking (HTTP, TCP, TTL, Script)
- Key-value store for configuration
- Multi-datacenter support
- Service mesh capabilities (Consul Connect)
- ACLs for security

## Build & Run
```bash
cd consul-service-discovery
mvn clean install
mvn spring-boot:run
```

## Default Configuration
- Host: `localhost`
- Port: `8500`
- Data center: `dc1`

## Service Discovery Flow
1. Service registers with Consul agent
2. Client queries Consul for service location
3. Consul returns healthy instances
4. Load balancing happens client-side

## Related Modules
- 40-jaeger (distributed tracing)
- 38-prometheus (metrics collection)