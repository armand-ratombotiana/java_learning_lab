# Refactoring for CAP Tradeoffs

## Migrating from CP to AP

### Before (CP):
```java
public class CPInventoryService {
    public void updateInventory(String productId, int quantity) {
        // Requires quorum - fails during partition
        database.writeWithQuorum(productId, quantity);
    }
}
```

### After (AP):
```java
public class APInventoryService {
    public void updateInventory(String productId, int quantity) {
        // Accept write locally, replicate later
        localCache.put(productId, quantity);
        replicationQueue.enqueue(new InventoryUpdate(productId, quantity));
    }
}
```

## Migrating from AP to CP

### Before (AP):
```java
public class APBankingService {
    public BigDecimal getBalance(String accountId) {
        return cache.get(accountId); // May be stale
    }
}
```

### After (CP):
```java
public class CPBankingService {
    public BigDecimal getBalance(String accountId) {
        return database.readWithQuorum(accountId); // Always consistent
    }
}
```
