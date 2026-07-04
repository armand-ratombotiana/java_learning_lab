# API Gateways - Why It Exists

## The Problem

Microservices create challenges:
1. Clients need to know all service locations
2. Cross-cutting concerns must be implemented per-service
3. Protocol translation (HTTP, WebSocket, gRPC)
4. Security needs centralized enforcement

## Without Gateway vs With Gateway
| Aspect | Without Gateway | With Gateway |
|--------|----------------|--------------|
| Client | Calls multiple services | Calls single endpoint |
| Auth | Per-service | Centralized |
| Rate limiting | Per-service | Centralized |
| Monitoring | Distributed | Centralized |
| Protocol | Clients handle | Gateway translates |

## Famous Gateways
- Spring Cloud Gateway (Java/Reactive)
- Kong (Lua/OpenResty)
- NGINX Plus
- AWS API Gateway
- Zuul (Netflix, legacy)
- Envoy (Proxy, CNCF)
