# API Design - WHY IT EXISTS

## Problem Statement
Without intentional API design, systems develop inconsistent endpoints: some return XML, some JSON; some use snake_case, some camelCase; error formats vary. This creates confusion for client developers and increases integration time.

## Origin
API design evolved from SOAP (1998) to REST (2000, Roy Fielding's dissertation) to modern GraphQL (2015) and gRPC (2016). Each generation addressed limitations of the previous.

## Core Drivers
- **Consistency**: Standardized patterns reduce cognitive load
- **Developer Experience**: Well-designed APIs are intuitive to use
- **Maintainability**: Good design makes evolution easier
- **Performance**: API design affects payload size, round trips
- **Interoperability**: Systems from different teams must integrate

## Why Not Just Expose Internal Methods?
Internal APIs are optimized for the server, not the client. They expose internal data structures, require multiple calls for a single view, and are tightly coupled to implementation.

## Java Ecosystem
- **Spring Web**: REST controllers with annotations
- **Spring HATEOAS**: Hypermedia-driven REST APIs
- **Spring GraphQL**: GraphQL server implementation
- **gRPC-Java**: High-performance RPC framework
- **OpenAPI/Swagger**: API documentation standard
