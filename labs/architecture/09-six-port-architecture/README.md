# Six-Port Architecture

## Overview
Six-Port Architecture extends the Ports and Adapters concept by explicitly defining six distinct port types: driving ports (primary) and driven ports (secondary), each with specific responsibilities for interaction patterns.

## Topics Covered
- Driving vs driven ports
- Primary and secondary adapters
- Port categories (inbound, outbound)
- Adapter types (REST, messaging, persistence)
- Testing strategies
- Comparison with hexagonal architecture

## Java/Spring Stack
- Spring Boot for adapter framework
- Spring Data JPA for persistence adapters
- Spring Web for inbound REST adapters
- Spring Cloud Stream for messaging adapters
- ArchUnit for architectural testing
- MapStruct for adapter mapping
