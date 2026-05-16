# Performance with Design Patterns

## Pattern-Specific Performance

### Singleton
- First call: slight overhead (checking/double-check)
- Subsequent calls: minimal (cached instance)
- Memory: single instance vs multiple

### Factory Method
- Object creation: may add overhead vs direct new
- Benefit: centralized creation, possible pooling
- Tradeoff: indirection cost vs flexibility

### Flyweight (Not discussed but related)
- Useful for many similar objects
- Reduces memory significantly
- Tradeoff: complexity in sharing

## Performance Considerations

| Pattern | Creation | Memory | Runtime |
|---------|----------|--------|---------|
| Singleton | Slow (first) | Low | Fast |
| Factory | Variable | Depends | Overhead |
| Builder | Slower | Depends | Overhead |
| Strategy | Fast | Depends | Fast |

## When Performance Matters

- Hot paths: Avoid Factory/Builder overhead
- Many objects: Consider Flyweight/Pool
- Critical sections: Keep simple

## Modern Java Considerations

- Records reduce need for Builder
- Dependency injection handles Singleton issues
- Lambdas can replace Strategy