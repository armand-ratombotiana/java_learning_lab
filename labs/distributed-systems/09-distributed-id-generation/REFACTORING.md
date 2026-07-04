# Refactoring ID Generation

## Migrating from UUID to Snowflake

### Before (UUID v4):
```java
public class OrderService {
    public String createOrder() {
        String orderId = UUID.randomUUID().toString();
        // Store order with UUID
        return orderId;
    }
}
```

### After (Snowflake):
```java
public class OrderService {
    private final SnowflakeGenerator idGen;
    
    public long createOrder() {
        long orderId = idGen.nextId();
        // Store order with Snowflake ID
        return orderId;
    }
}
```

## Migrating from DB Sequence
Replace database sequence calls with local Snowflake generation for better performance.
