# Protocol Buffers -- Code Deep Dive

## Main Implementation

### Class Structure
The main class demonstrates Protocol Buffers serialization principles.

**Package**: com.javalab.02

### Core Components
1. **Proto Schema** - .proto file defining message structure
2. **Generated Builder** - Builder pattern for constructing messages
3. **Serialization Methods** - toByteArray(), writeTo(), parseFrom()
4. **JSON Conversion** - Using protobuf JSON utility

### Proto Schema Example
syntax = "proto3";
package com.javalab;
message Person {
    string name = 1;
    int32 age = 2;
    repeated string emails = 3;
    oneof contact { string phone = 4; string address = 5; }
}

### Compilation
protoc --java_out=src/main/java person.proto
Generated: Person (immutable), Person.Builder, PersonOrBuilder

### Message Construction
Person person = Person.newBuilder().setName("Alice").setAge(30).addEmails("alice@example.com").build();

### Serialization/Deserialization
byte[] bytes = person.toByteArray();
Person parsed = Person.parseFrom(bytes);

### JSON Conversion
String json = JsonFormat.printer().print(person);
JsonFormat.parser().merge(json, builder);

### Field Types
- Scalar: int32, int64, uint32, uint64, sint32, sint64, fixed32, fixed64, sfixed32, sfixed64, float, double, bool, string, bytes
- Enum: User-defined enum types
- Message: Nested message types
- oneof: Exclusive field selection (only one field can be set at a time)
- map: map<KeyType, ValueType> for key-value pairs

### Performance
- 2-5x faster than Java serialization
- 3-10x smaller output
- Schema compilation enables pre-generated serializers

### Best Practices
1. Use proto3 syntax for new projects
2. Reserve field numbers for forward compatibility
3. Use packed=true for repeated scalar fields
4. Prefer sint32/sint64 for negative numbers (ZigZag encoding)
5. Use field numbers 1-15 for frequently used fields (1 byte key)
6. Never reuse or repurpose a field number
