# 26 - GraphQL (Schema & Resolvers)

GraphQL schema definition and resolver implementation. Covers schema type definitions (Query, Mutation, types), resolver functions (DataFetcher), field definitions with arguments, execution of queries against a schema, and object type modeling.

## Prerequisites

- Java 11+
- Maven 3.x

## Key Concepts

- Schema definition: `type Query`, `type Mutation`, `type User`
- Type system: ID, String, Int, Float, Boolean, List, NonNull
- Resolvers: DataFetcher functions for each field
- Arguments: field-level input parameters
- Execution: query parsing, validation, and result building
- Object types with fields and relationships

## Module Structure

- `src/main/java/com/learning/lab/module26/Lab.java` - GraphQL schema and resolvers lab
- `src/test/` - Test implementations
- `SOLUTION/` - Solution code
- `RESOURCES/` - GraphQL reference materials

## Learning Objectives

- Define GraphQL schemas with types, queries, and mutations
- Implement resolver functions with DataFetcher
- Execute GraphQL queries and handle results

## Estimated Time

- 1-2 hours

## How to Run

```bash
cd 26-graphql
mvn compile exec:java -Dexec.mainClass="com.learning.lab.module26.Lab"
```
