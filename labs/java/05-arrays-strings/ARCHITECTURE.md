# Architecture — Arrays & Strings

## Data Transformation Pipeline

Arrays and strings flow through transformation layers: input → parse → validate → transform → output. Each stage transforms data.

## Immutable Data Flow

Immutable strings flow through the system as values. JSON parsing converts strings to domain objects. String formatting converts objects to output strings.

## Message Serialization

Objects → JSON/XML strings (serialization) → network → parse → objects (deserialization). This is the foundation of microservices communication.

## CQRS with Arrays

Command-Query Responsibility Segregation: arrays used for bulk queries (reporting, analytics). Strings used for individual queries (search, lookup).

## Event Sourcing

Events serialized as JSON strings stored in event store. Arrays of events replayed to reconstruct state.

## Layered Caching

Strings as cache keys (URL paths, query parameters). Arrays as cache results (lists of IDs, batch results).
