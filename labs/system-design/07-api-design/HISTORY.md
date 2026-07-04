# API Design - HISTORY

## Timeline

### 1990s: SOAP Revolution
- 1998: SOAP 1.0 (Simple Object Access Protocol)
- XML-based, WSDL for service description
- Heavy, complex, but standardized
- Enterprise Service Bus (ESB) pattern

### 2000s: REST Emerges
- 2000: Roy Fielding's dissertation defines REST
- 2004: Rails RESTful routing popularizes REST
- 2007: REST wins over SOAP for web APIs
- JSON replaces XML as preferred format

### 2010s: REST Maturity
- 2012: Swagger (now OpenAPI) specification
- 2014: JSON API specification
- 2015: Apple moves to REST for App Store API
- HAL, Siren, JSON-LD for hypermedia

### 2015: GraphQL
- Facebook open-sources GraphQL
- Client-specified queries, no over-fetching
- Single endpoint, strongly typed schema
- React + GraphQL becomes dominant frontend stack

### 2016: gRPC
- Google open-sources gRPC
- HTTP/2, Protocol Buffers, streaming
- High performance, polyglot
- Service mesh (Istio) uses gRPC

### 2020s: API-First Design
- OpenAPI 3.0 becomes standard
- tRPC for TypeScript end-to-end types
- AsyncAPI for event-driven APIs
- API gateways (Kong, Apigee) manage lifecycle

## Java API Evolution
- JAX-RS (2008) — REST API specification for Java
- Spring MVC REST (2012) — @RestController
- Spring HATEOAS (2013) — hypermedia
- Spring GraphQL (2022) — GraphQL support
- Protobuf + gRPC (2017) — high-performance RPC
