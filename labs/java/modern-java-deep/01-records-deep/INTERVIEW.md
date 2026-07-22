# Interview Questions: Records Deep Dive

## Company-Specific Focus

### Google
- Records: transparent data carriers with automatic equals/hashCode/toString
- Compact constructor: validation in records without redundant field assignments
- Local records: declaring records inline for intermediate data

### Microsoft
- Java records vs C# records: positional construction vs nominal records
- Serialization: records are serializable by default

### Amazon
- DTO immutability: records for request/response DTOs
- Pattern matching: record patterns for type-safe decomposition
- Sealed records: combining sealed types with records for modeling

### Meta
- Equality: value-based equality of records simplifies data comparison
- Accessor methods: record.field() vs traditional getField()

### Apple
- Records and JPA: why records can't be JPA entities (no-arg constructor, mutability)
- Records and immutability: all fields are private final

### Oracle
- JEP 395: Records (Final)
- JLS 8.10: Record Classes
- Record components: header defines the state
- Canonical vs compact constructor

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| 138 Copy List with Random Pointer | Medium | Google, Amazon, Apple | Record for deep copy representation |
| 148 Sort List | Medium | Amazon, Google | Record for node wrappers |
| 706 Design HashMap | Easy | Google, Apple, Amazon | Record for key-value entry |
| 380 Insert Delete GetRandom O(1) | Medium | Amazon, Google | Record for value-wrapper pattern |
| 211 Design Add and Search Words Data Structure | Medium | Google, Amazon | Record for trie nodes |

## Real Production Scenarios
- **Plaid**: Migrating 200+ POJOs to records reduced lines of code by 60% in DTO layer
- **Uber**: Record-based DTOs reduced data size in serialization for protobuf mapping
- **LinkedIn**: Records in Kafka event schema reduced boilerplate in event handlers

## Interview Patterns & Tips
- **Immutability**: records are shallowly immutable
- **Compact constructor**: for validation and normalization
- **Not for JPA**: no no-arg constructor, can't be proxied
- **Pattern matching**: record patterns destructure in switch and instanceof

## Deep Dive Questions
- **Bytecode**: How are records compiled in the class file? (Synthetic methods, record attribute)
- **Equals/HashCode**: How are equals and hashCode implemented for records?
- **Serialization**: How do records interact with serialization?
- **Reflection**: How to inspect record components via reflection?
- **Pattern matching**: How does record deconstruction work under the hood?