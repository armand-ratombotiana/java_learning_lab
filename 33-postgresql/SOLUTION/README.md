# PostgreSQL Solution

## Concepts Covered

### JSONB Operations
- Insert/select JSONB data
- JSON path operators (->>, ->)
- Update JSONB fields
- Search with containment (@>) and existence (?)

### Array Operations
- Array column types
- Unnest for array expansion
- Array operators

### Full Text Search
- TsVector and TsQuery
- GIN indexes for performance
- plainto_tsquery for user input

### Window Functions
- Running totals
- RANK, DENSE_RANK, ROW_NUMBER
- PARTITION BY for grouping

### Advanced Features
- CTEs (WITH RECURSIVE)
- Upsert (INSERT ON CONFLICT)
- Batch operations
- COPY command for bulk loading

## Dependencies

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.7.0</version>
</dependency>
```

## Configuration

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/mydb
    username: postgres
    password: secret
```

## Running Tests

```bash
mvn test -Dtest=PostgreSQLSolutionTest
```