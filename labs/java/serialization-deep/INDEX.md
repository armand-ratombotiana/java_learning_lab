# Serialization Deep - Micro-Labs

## Overview
This module contains 5 deep-dive micro-labs covering Java serialization, Protocol Buffers, JSON, XML, and Kryo serialization frameworks.

## Lab Index
| # | Lab | Description |
|---|-----|-------------|
| 01 | [Java Serialization](./01-java-serialization/) | ObjectOutputStream/ObjectInputStream, serialVersionUID, writeObject/readObject, readResolve/writeReplace, Externalizable, serialization proxy pattern |
| 02 | [Protocol Buffers](./02-protocol-buffers/) | Proto3 schema, protoc compilation, generated builders, JSON/bytes serialization, field types, oneof, maps |
| 03 | [JSON Serialization](./03-json-serialization/) | Jackson ObjectMapper, @JsonProperty, @JsonFormat, custom serializer/deserializer, Jackson vs Gson vs JSON-B |
| 04 | [XML Serialization](./04-xml-serialization/) | JAXB marshalling/unmarshalling, @XmlRootElement, @XmlElement, @XmlJavaTypeAdapter, XStream, Jackson XML |
| 05 | [Kryo Serialization](./05-kryo-serialization/) | Kryo serialization, registration, thread-safe serializer, field vs field serializer, performance comparisons |

## How to Use
Each micro-lab is self-contained with 24 markdown files, Java source code, JUnit 5 tests, and 7 subdirectories for hands-on work. Start from lab 01 and progress sequentially.
