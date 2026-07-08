# 13 - Distributed ID Generation

## Overview
Distributed ID generation creates unique identifiers across multiple nodes without coordination. This lab covers Snowflake-style IDs, UUID v7, ULID, database sequences, and custom distributed ID generators.

## Prerequisites
- Java 21+
- Maven 3.8+
- Understanding of distributed systems fundamentals

## Learning Objectives
- Compare ID generation strategies for distributed systems
- Implement Snowflake-style unique ID generation
- Work with UUID v7 and ULID formats
- Understand performance and collision trade-offs
- Design ID generators for different workload patterns

## Topics Covered
- Snowflake algorithm (Twitter-style IDs)
- UUID v4, v6, v7 formats
- ULID (Universally Unique Lexicographically Sortable Identifier)
- Database sequences and auto-increment
- Coordinated vs uncoordinated generation
- Time-based vs random ID components
- ID collision probability analysis
- Monotonicity and ordering properties

## Package Structure
- com.distributed.idgeneration â€” Core implementations
  - SnowflakeIdGenerator.java â€” Twitter-style Snowflake IDs
  - UuidV7Generator.java â€” Time-ordered UUID v7
  - UlidGenerator.java â€” ULID implementation
  - IdGenerator.java â€” Common interface
  - SequenceIdGenerator.java â€” Coordinated sequence IDs
