# Internals: Database Testing

## Internal Architecture

### Data Fixture Generator Internals
Generates realistic test data using configurable field generators and random value strategies.

**Generator Pipeline:**
1. Register named field generators with Supplier lambdas
2. Each generate() call produces one Map row
3. Supports type inference for SQL generation

### Migration Tester Internals
Validates database migrations by executing ordered steps and running validation rules.

**Step Execution:**
1. Sort steps by order number
2. Execute each step sequentially
3. Track timing and success/failure per step
4. On failure, stop execution and report

**Validation Framework:**
- Required validations: Must pass for migration to succeed
- Optional validations: Warning only on failure
- Runnable checks with custom assertion logic

### Testcontainers Integration
Integration tests use Testcontainers for real database instances:
```java
@Testcontainers
class DatabaseTest {
    @Container
    PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");
}
```

### Data Seeding Strategy
- Deterministic random (fixed seed) for reproducible tests
- Batch generation for performance
- Fixture lifecycle: setup → verify → teardown

### Migration Testing Patterns
1. **Forward test:** Apply migration, verify schema
2. **Rollback test:** Apply migration, rollback, verify original state
3. **Data preservation test:** Ensure existing data survives migration
4. **Performance test:** Measure migration execution time

### Monitoring
- Test execution time per fixture batch
- Migration step duration
- Validation pass/fail rate
- Database connection pool usage during tests
