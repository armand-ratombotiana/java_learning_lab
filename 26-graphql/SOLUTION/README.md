# GraphQL Solution

## Concepts Covered

### Schema Definition
- Object types: User, Post
- Field definitions with arguments
- Non-null types and lists

### Query Operations
- Simple queries with arguments
- Nested queries (posts -> author)
- Query variables
- Aliases and fragments

### Resolvers
- DataFetcher implementations
- Batch loading with DataLoader

### Advanced Features
- Mutations support
- Subscriptions support
- Schema introspection

## Dependencies

```xml
<dependency>
    <groupId>com.graphql-java</groupId>
    <artifactId>graphql-java</artifactId>
    <version>21.0</version>
</dependency>
```

## Running Tests

```bash
mvn test -Dtest=GraphQLSolutionTest
```