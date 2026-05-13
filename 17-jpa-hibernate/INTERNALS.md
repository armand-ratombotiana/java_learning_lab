# JPA/Hibernate Internals

## Entity Lifecycle States

### State Diagram

```
                ┌──────────────────────────────────────────┐
                │                                          │
                ▼                                          ▼
    ┌──────────────────┐                        ┌──────────────────┐
    │    TRANSIENT     │──persist()─────────────▶│     PERSISTENT   │
    │  (new object)   │                         │  (managed by EM) │
    └──────────────────┘                         └────────┬─────────┘
           ▲                                              │
           │ remove()                                     │ evict()
           │ clear()                                      │ close()
           ▼                                              ▼
    ┌──────────────────┐                        ┌──────────────────┐
    │    REMOVED      │◀────────────────────────│     DETACHED    │
    │ (to be deleted) │                         │ (not managed)   │
    └──────────────────┘                        └──────────────────┘
```

### State Details

| State | Description | Session Behavior |
|-------|-------------|------------------|
| Transient | New object, not associated with EM | No tracking |
| Persistent | Managed by EM, changes tracked | Automatic dirty checking |
| Detached | Previously managed, now disconnected | Changes NOT tracked |
| Removed | Marked for deletion | Removed on flush |

## Dirty Checking Mechanism

### Change Detection Process

```java
// Simplified DirtyCheckingView in Hibernate
public class DirtyCheckingEntityEntry {
    private final EntityKey key;
    private final Object[] snapshot;  // Original values
    
    public boolean hasDirtyAttributes(Object[] currentState) {
        for (int i = 0; i < currentState.length; i++) {
            if (!equals(currentState[i], snapshot[i])) {
                return true;
            }
        }
        return false;
    }
}
```

### Flush Process

```
1. Transaction commits or EntityManager.flush()
       ↓
2. EntityEntryArray iterated
       ↓
3. dirtyChecking() checks each entity
       ↓
4. Generate SQL for dirty entities
       ↓
5. Execute in transaction
       ↓
6. Update snapshot to current state
```

### Snapshot Storage

```java
// In EntityEntryImpl
private Object[] loadedState;  // Snapshot of entity at load time

// When entity is loaded:
loadedState = extractEntityState(entity);

// On flush:
if (hasDirtyAttributes(currentState)) {
    generateUpdateSQL(currentState, loadedState);
    loadedState = currentState.clone();  // Update snapshot
}
```

## First-Level Cache (Persistence Context)

### Session Cache Structure

```java
// In SessionImpl
private final Map<EntityKey, EntityEntry> entitiesByKey = new HashMap<>();
private final Map<Object, EntityEntry> entityByInterceptor = new HashMap<>();
private final Map<EntityKey, Object[]> entitySnapshotsByKey = new HashMap<>();
```

### EntityKey Generation

```java
// EntityKey encapsulates identifier information
public final class EntityKey {
    private final String entityName;
    private final Serializable identifier;
    private final int hashCode;
}

// Unique per (class, id) pair
// Used as cache key in first-level cache
```

### Cache Lookup Process

```java
// SessionImpl.get()
public Object get(Class clazz, Serializable id) {
    EntityKey key = new EntityKey(clazz, id);
    
    // 1. Check persistence context
    if (entitiesByKey.containsKey(key)) {
        return entitiesByKey.get(key);  // Return cached
    }
    
    // 2. Load from database
    Object entity = loadFromDatabase(key);
    
    // 3. Add to persistence context
    addEntity(key, entity);
    
    return entity;
}
```

## Flush Modes and Timing

### Flush Modes

```java
public enum FlushMode {
    AUTO,       // Default, flush before query execution
    COMMIT,     // Flush only at commit
    ALWAYS,     // Flush before every query
    MANUAL      // Never auto-flush
}
```

### Automatic Flush

```
Query.execute()
      ↓
if (flushMode == AUTO)
      ↓
flush()  ← Executes pending inserts/updates/deletes
      ↓
Execute query
```

### Flush Order (by Entity Type)

```
1. Orphans (cascade remove)
2. Entity insertions
3. Collection removals (old)
4. Collection updates
5. Collection additions (new)
6. Entity deletions
```

## Cascade Operations

### Cascade Types

| Cascade Type | Effect |
|--------------|--------|
| PERSIST | persist() propagates to children |
| REMOVE | remove() propagates to children |
| REFRESH | refresh() propagates to children |
| MERGE | merge() propagates to children |
| DETACH | detach() propagates to children |
| ALL | All of above |

### Implementation

```java
// CascadingActionEntityInsertAction
public void execute() {
    for (Object child : entities) {
        ActionQueue actionQueue = getSession().getActionQueue();
        
        // Add child action to queue
        actionQueue.addAction(new EntityInsertAction(child));
    }
}
```

## Entity Identity

### Uniqueness Guarantees

```java
// Same Session = same Java object
Session session = sessionFactory.openSession();
Person p1 = session.get(Person.class, 1);  // Returns same instance
Person p2 = session.get(Person.class, 1);
assert p1 == p2;  // Same object reference!
```

### Different Sessions = Different Instances

```java
Session s1 = sessionFactory.openSession();
Session s2 = sessionFactory.openSession();

Person p1 = s1.get(Person.class, 1);
Person p2 = s2.get(Person.class, 1);

assert p1 != p2;  // Different objects in different sessions
```

## Batch Processing Internals

### JDBC Batching

```java
// Hibernate JDBC batch configuration
<property name="hibernate.jdbc.batch_size">25</property>

// Generated SQL:
INSERT INTO Person (name) VALUES (?);  // Batch 1
INSERT INTO Person (name) VALUES (?);  // Batch 2
...
// Execute at batch_size
```

### Batch Insert Flow

```
Entity.persist()
      ↓
ActionQueue.addAction(EntityInsertAction)
      ↓
After transaction commit or batch size reached:
      ↓
executeActions()
      ↓
PreparedStatement.addBatch() x N
      ↓
PreparedStatement.executeBatch()
```

### StatelessSession for Bulk Operations

```java
// StatelessSession bypasses persistence context
StatelessSession session = sessionFactory.openStatelessSession();
Transaction tx = session.beginTransaction();

session.insert(new Person("John"));
session.insert(new Person("Jane"));
// No first-level cache!

tx.commit();
session.close();
```

## Transaction Isolation and Locking

### Optimistic Locking

```java
// @Version field for optimistic locking
@Entity
public class Product {
    @Id
    private Long id;
    
    @Version
    private Long version;  // Incremented on each update
    
    private String name;
}
```

### Optimistic Lock Failure

```
Thread A: read product (version = 1)
Thread B: read product (version = 1)
Thread A: update product → version = 2
Thread B: update product → 
   StaleObjectStateException!
   (version mismatch detected)
```

### Pessimistic Locking

```java
// SELECT ... FOR UPDATE
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT p FROM Product p WHERE p.id = :id")
Product findByIdWithLock(@Param("id") Long id);

// Generated SQL:
// SELECT * FROM product WHERE id = ? FOR UPDATE
```

## Collection Internals

### Lazy vs Eager Loading

```java
@Entity
public class Order {
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<Item> items;  // Loaded only when accessed
    
    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER)
    private List<Note> notes;  // Loaded with Order
}
```

### Lazy Loading Proxy

```java
// PersistentBag implementation (lazy collection)
public class PersistentBag extends AbstractPersistentCollection {
    private List delegate;  // The actual collection
    
    public Iterator iterator() {
        initialize(true);  // Load from DB if not initialized
        return delegate.iterator();
    }
}
```

### Collection Initialization

```java
// In CollectionInitializeAction
public void initialize() throws HibernateException {
    // Load collection from database
    CollectionPersister persister = getPersister();
    CollectionLoader loader = persister.createLoader();
    
    // Populate collection
    loader.loadCollection(getSession(), collection);
}
```

## Entity Graphs

### EntityGraph Processing

```java
// Specification:
@EntityGraph(attributePaths = {"items", "customer"})
@Query("SELECT o FROM Order o")
List<Order> findOrdersWithDetails();

// Translated to SQL:
SELECT * FROM order 
LEFT JOIN item ON order_id = order.id
LEFT JOIN customer ON customer_id = customer.id
```

### Fetch vs Load

```
@EntityGraph with attributePaths:
- fetch = LAZY (default) → Separate JOIN
- fetch = EAGER → Inline in main query
```