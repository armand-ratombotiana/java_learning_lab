# Refactoring for Consistency

## From Eventual to Strong

### Before:
```java
public class InconsistentCartService {
    private final DistributedCache cache; // Eventually consistent
    public void addItem(String cartId, Item item) {
        cache.put(cartId + ":items", getItems(cartId) + item);
    }
}
```

### After:
```java
public class ConsistentCartService {
    private final Database db; // Strongly consistent
    public void addItem(String cartId, Item item) {
        db.transaction(tx -> {
            List<Item> items = tx.read(cartId);
            items.add(item);
            tx.write(cartId, items);
        });
    }
}
```

## Adding Read-Your-Writes
Wrap the store with a client-side cache that tracks local writes.
