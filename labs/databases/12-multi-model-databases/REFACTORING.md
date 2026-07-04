# Refactoring: Single Database → Polyglot Persistence

## Monolithic Data → Specialized Stores

### Before (Single PostgreSQL)
```sql
-- All data in one database
orders (relational)
sessions (JSON column in users)
reviews (table with JSON metadata)
product_search (expensive LIKE queries)
```

### After (Polyglot)
```sql
-- Relational: orders, users, payments (PostgreSQL)
-- Document: reviews, flexible metadata (MongoDB)
-- Key-Value: sessions (Redis)
-- Search: product search (Elasticsearch)
```

## Migration Strategy

```java
public class PolyglotMigration {
    // Step 1: Dual-write (write to both old + new)
    // Step 2: Backfill (existing data to new store)
    // Step 3: Read from new store
    // Step 4: Stop writing to old store
    // Step 5: Drop old data

    @Scheduled(fixedDelay = 60000)
    public void backfillProducts() {
        Pageable pageable = PageRequest.of(0, 100);
        Page<Product> products;
        do {
            products = oldProductRepo.findAll(pageable);
            newProductDocRepo.saveAll(products.stream()
                .map(p -> new ProductDoc(p.getId(), p.getName(), p.getMetadata()))
                .toList());
            pageable = pageable.next();
        } while (products.hasNext());
    }
}
```

## Why Migrate?
- Product search: 200ms (LIKE) → 5ms (Elasticsearch)
- Session access: 50ms (JSON in RDBMS) → 1ms (Redis)
- Reviews: Complex relational schema → Flexible document model
