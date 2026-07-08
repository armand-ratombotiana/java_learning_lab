# Backend for Frontend (BFF) — Theory

## 1. Introduction
Backend for Frontend (BFF) is a pattern where a dedicated backend service is created for each frontend client type. Rather than having a single general-purpose API, each client type (mobile app, web app, desktop app, IoT device) gets its own tailored backend.

## 2. Problem Context
Traditional monolithic APIs serve all client types through a single interface, leading to over-fetching for mobile clients, under-fetching for complex UIs, tight coupling between API and all clients, suboptimal protocols, and shared bloat where features for one client impact others.

## 3. Core Principles

### 3.1 Client-Specific Optimization
Each BFF is optimized for the specific needs of its client type, including data shape, payload size, and communication protocol.

### 3.2 Separation of Concerns
Each BFF has independent development, deployment, and scaling characteristics. Changes to one BFF do not affect other clients.

### 3.3 Single Responsibility
Each BFF is responsible only for serving its specific client type. It aggregates data from downstream services as needed.

### 3.4 Proximity to Client
The BFF team should be aligned with the client team, enabling faster iteration and better understanding of client needs.

## 4. BFF Architecture Components

### 4.1 API Gateway Layer
Routes requests to appropriate BFF instances based on client type. Provides cross-cutting concerns like authentication, rate limiting, and logging.

### 4.2 BFF Services
Each BFF service handles request transformation, data aggregation from downstream services, response formatting, client-specific business logic, and session management.

### 4.3 Downstream Services
Shared business services that multiple BFFs consume. These contain core domain logic independent of client type.

## 5. Protocol Considerations

### 5.1 REST for Web BFFs
Traditional RESTful APIs work well for web clients with full network capabilities.

### 5.2 GraphQL for Complex UIs
GraphQL enables declarative data fetching where the client specifies exactly what data it needs.

### 5.3 gRPC for Mobile BFFs
gRPC provides efficient binary serialization, streaming capabilities, and strong typing.

### 5.4 WebSocket for Real-Time BFFs
Persistent connections for real-time updates in notification-heavy or collaborative applications.

## 6. Benefits
- Optimized client performance through tailored payloads
- Independent evolution of client-specific backends
- Reduced coupling between teams
- Better developer experience with focused codebases
- Improved security through reduced attack surface per BFF
- Simplified client-side code

## 7. Challenges
- Service proliferation and management overhead
- Duplication of logic across BFFs
- Consistent API governance across BFFs
- Increased infrastructure costs
- Cross-cutting concerns must be solved for each BFF

## 8. When to Use BFF

### Use When:
- Multiple distinct client types with different needs
- Mobile clients with bandwidth constraints
- Complex UIs requiring data aggregation
- Independent frontend teams with different release cycles

### Avoid When:
- Simple CRUD applications with one client type
- Insufficient team size to maintain multiple BFFs
- Strong consistency requirements across all clients
- Early-stage products still exploring market fit
