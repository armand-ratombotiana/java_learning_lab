# Interview Questions: Records

## Company-Specific Focus

### Google
- Records as transparent data carriers: implicit equals, hashCode, toString
- Compact constructor vs canonical constructor: validation before field assignment
- Local records: declaring records inside methods for intermediate data

### Microsoft
- Java records vs C# record types: positional records and nominal records
- Records and serialization: serially replaceable in JSON
- Cannot extend an existing record: design limitations

### Amazon
- Records for immutable DTOs in microservice communication
- Record patterns in stream pipelines for deconstruction and mapping
- Zero-boilerplate streams with record mapping: map(Person::name) no lambda needed

### Meta
- Records replacing hand-written POJO boilerplate: maintenance savings
- Records as local tuples inside a single method
- The default accessor method naming convention

### Apple
- Immutability inherent with records: all fields are private final
- Canonical constructor for object identity
- Defensive copy in record: ensure immutability for mutable fields

### Oracle
- JEP 395 (Java 16): the record type and its full specification
- JLS 8.10: record class declaration
- Record: extends java.lang.Record — you can not extend it
- The header's components become record-holder behavior in the binary representation

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| 138 Copy List with Random Pointer | Medium | Search, Google, Apple | Record for node representation |
| 148 Sort List | Medium | Amazon, Google, Facebook | Record for linked list nodes |
| 706 Design HashMap | Easy | Google, Apple, Amazon | Record for key-value pairs |
| 208 Implement Trie | Medium | Amazon, Google | Record for edge nodes |
| 707 Design Linked List | Medium | Google, Amazon, Microsoft | Record for node storage |

## Real Production Scenarios
- **Plaid**: Migrating 200+ POJOs to records in the transaction processing API — reduced lines of code by 60% for the DTO layer
- **Square**: record modals for an immutable event sourcing model—fully replaced mutable objects in the entire publication chain
- **Uber**: A record based DTO was used for grpc codegen — a compact messaging provided a 10% reduction in serialized data

## Interview Patterns & Tips
- **Not replaceable by builders**: records are positionally defined; for many fields, prefer regular classes
- **Immutability**: Not guaranteed: mutable fields (like List) allow mutation of the object state
- **Reflection**: Using reflection, you can still mutate record fields
- **Compact constructor**: Run validation before field initialization

## Deep Dive Questions
- **JVM**: What does the bytecode generated for record look like?
- **Memory**: How does the JVM store the record's component metadata in the class file?
- **Equality behavior**: How is record hashCode and equals implemented manually according to the JVM
- **Record with JPA**: Why cannot JPA entities be records? (no-arg contructor, proxy, laziness)
- **Deserialization**: How do records maintain data integrity across a deserialize cycle?