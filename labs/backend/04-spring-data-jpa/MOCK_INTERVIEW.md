# Mock Interview: Spring Data JPA (Lab 04)

**Role:** Backend Engineer (Senior)  
**Duration:** 45 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** What is JPA and how does it work with Hibernate?

**Candidate:** JPA (Jakarta Persistence API) is a specification for object-relational mapping (ORM) in Java. Hibernate is the most popular implementation of JPA. JPA defines:
- Entity lifecycle management (new, managed, detached, removed)
- Object-relational mapping via annotations
- JPQL (Java Persistence Query Language)
- Criteria API for type-safe queries
- Caching (first and second-level)
- Transaction management

Hibernate implements these specifications and adds extras like Hibernate-specific annotations, batch processing, and multi-tenancy support. Spring Data JPA provides a repository abstraction layer over JPA.

**Interviewer:** Explain the N+1 query problem and how to solve it.

**Candidate:** The N+1 problem occurs when you fetch a collection of parent entities (1 query) and then access each parent's lazy-loaded collection, triggering N additional queries. For example:

```java
List<Author> authors = authorRepository.findAll(); // 1 query
for (Author a : authors) {
    a.getBooks().size(); // N queries, one per author
}
```

Solutions:
1. **`JOIN FETCH`** in JPQL: `SELECT a FROM Author a JOIN FETCH a.books`
2. **`@EntityGraph`**: `@EntityGraph(attributePaths = "books")`
3. **`@BatchSize`**: `@BatchSize(size = 10)` on the collection — reduces to ceil(N/10) queries
4. **DTO projections**: Query only needed fields, avoids entity loading entirely
5. **Spring Data JPA's `EntityGraph` on repository methods**: `@EntityGraph(value = "Author.books", type = EntityGraphType.FETCH)`

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** How do you map entity inheritance in JPA? When would you use each strategy?

**Candidate:** Three inheritance mapping strategies:

1. **`SINGLE_TABLE`** (default) — single table for all subclasses with a discriminator column
   - Pros: Fast queries (no joins), simple schema
   - Cons: Nullable columns for subclass-specific fields, table grows wide
   - Best when: Subclasses have few additional fields, polymorphism is important

2. **`JOINED`** — separate tables per subclass, joined via foreign key
   - Pros: Normalized schema, no nullable columns
   - Cons: Requires joins for queries, slower for deep hierarchies
   - Best when: Subclasses have many unique fields, normalization matters

3. **`TABLE_PER_CLASS`** — separate table for each concrete class
   - Pros: No joins needed, each table is self-contained
   - Cons: Duplicate columns, polymorphic queries use UNIONs
   - Best when: Hierarchies are shallow, polymorphic queries are rare

**Interviewer:** How does the JPA entity lifecycle work? What happens during `save()` on a Spring Data JPA repository?

**Candidate:** The entity lifecycle has four states:
1. **`new` (transient):** Entity created with `new`, not associated with a persistence context
2. **`managed` (persistent):** Entity associated with a `PersistenceContext` (EntityManager). Changes are tracked.
3. **`detached`:** Entity was managed but the persistence context was closed. Changes not tracked.
4. **`removed`:** Entity scheduled for deletion.

When `repository.save(entity)` is called:
- **If entity ID is null or 0:** `EntityManager.persist()` is called → entity becomes managed
- **If entity has a non-null ID:** `EntityManager.merge()` is called → new managed instance is returned (original entity remains detached)
- During `flush()` or transaction commit, Hibernate generates SQL INSERT/UPDATE statements
- Dirty checking: Hibernate compares managed entity state at flush time to detect changes

**Important caveat:** `save()` from `CrudRepository` returns the managed entity. Always use the returned entity, not the input one, because `merge()` returns a new instance.

---

## Round 3: Hard (15-20 minutes)

**Interviewer:** You need to design a performant data access layer for a social media feed that handles 10M posts/day. The feed shows posts with author info and like counts. How do you design the JPA entities and queries?

**Candidate:** This requires multiple optimization strategies:

**Entity design:**
```java
@Entity
@Table(name = "posts")
public class Post {
    @Id private Long id;
    private String content;
    private LocalDateTime createdAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Author author;
    
    // Don't map likes as collection — too expensive
    @Transient
    private long likeCount; // Fetched separately or via count query
}
```

**Optimization strategies:**

1. **DTO projection instead of entity:** Create `PostSummary` interface projection:
```java
public interface PostSummary {
    Long getId();
    String getContent();
    LocalDateTime getCreatedAt();
    AuthorSummary getAuthor();  // Nested projection
    
    interface AuthorSummary {
        Long getId();
        String getName();
    }
}
```

2. **Batch fetching with `@BatchSize`** on all lazy collections

3. **Second-level cache with Redis** (Lab 13) for read-heavy entities

4. **Query for stats separately:** `SELECT p.id, COUNT(l.id) FROM Post p LEFT JOIN Like l ... GROUP BY p.id`

5. **Paginate with keyset pagination** (not offset):
```java
@Query("SELECT p FROM Post p WHERE p.id < :lastSeenId ORDER BY p.id DESC")
List<Post> findNextPage(@Param("lastSeenId") Long lastSeenId, Pageable pageable);
```
Keyset pagination avoids the offset performance penalty on large tables.

6. **Read-write separation:** Use `@Transactional(readOnly = true)` for feed reads (routes to read replica), separate write model for post creation.

**Interviewer:** Great. Now, how would you handle a bulk insert of 100,000 posts? What JPA considerations apply?

**Candidate:** Batch inserts require special handling in Hibernate:

1. **Batching configuration:**
```properties
spring.jpa.properties.hibernate.jdbc.batch_size=50
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true
spring.jpa.properties.hibernate.jdbc.batch_versioned_data=true
```

2. **JDBC batch mode:** Hibernate groups INSERT statements of the same type into batches of `batch_size`

3. **Avoid identity ID generation** for batch inserts: `IDENTITY` strategy disables JDBC batching because Hibernate needs the generated ID immediately. Use `SEQUENCE` instead:
```java
@Id
@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "post_seq")
@SequenceGenerator(name = "post_seq", allocationSize = 50)
private Long id;
```

4. **Manual flush and clear:** Periodically flush and clear the persistence context to prevent memory growth:
```java
@Transactional
public void bulkInsert(List<Post> posts) {
    int batchSize = 50;
    for (int i = 0; i < posts.size(); i++) {
        entityManager.persist(posts.get(i));
        if (i % batchSize == 0 && i > 0) {
            entityManager.flush();
            entityManager.clear();
        }
    }
}
```

5. **StatelessSession** for truly large batch operations (no dirty checking):
```java
StatelessSession session = sessionFactory.openStatelessSession();
session.beginTransaction();
for (Post post : posts) {
    session.insert(post);
}
session.getTransaction().commit();
session.close();
```

**Interviewer:** How does Hibernate's first-level cache differ from the second-level cache? How would you configure a distributed second-level cache?

**Candidate:** 

**First-level cache:**
- Bound to the `EntityManager`/`Session`
- Exists for the duration of a session (typically one transaction)
- Cannot be disabled
- Automatically caches all entities loaded or persisted
- Not visible to other sessions

**Second-level cache:**
- Shared across sessions/EntityManagers within the same SessionFactory
- Optional, must be explicitly configured
- Stores entities, collections, and query results
- Can be distributed (Redis, Hazelcast, Infinispan)

**Distributed second-level cache with Redis:**
```properties
spring.jpa.properties.hibernate.cache.use_second_level_cache=true
spring.jpa.properties.hibernate.cache.region.factory_class=org.hibernate.cache.jcache.JCacheRegionFactory
spring.cache.type=redis
```
Using JCache (JSR-107) + Redis:
```xml
<dependency>
    <groupId>org.redisson</groupId>
    <artifactId>redisson-hibernate-6</artifactId>
</dependency>
```

Entities annotated with `@Cacheable` and `@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)`.

**Cache concurrency strategies:**
- `READ_ONLY` — immutable entities
- `READ_WRITE` — mutable entities, soft locks
- `NONSTRICT_READ_WRITE` — mutable entities, eventual consistency
- `TRANSACTIONAL` — full ACID, requires JTA

---

## Interviewer Feedback

**Strengths:**
- Deep understanding of entity lifecycle
- Practical optimization strategies for large-scale data
- Good knowledge of batch processing patterns

**Areas to Improve:**
- Could mention Hibernate 6.x SQM (Simple Query Model) improvements
- Should discuss `@DynamicUpdate` for selective column updates

**Verdict:** Strong Hire

---

## Follow-Up Questions

1. What is the Open Session in View pattern? What are its drawbacks?
2. How does Hibernate's `PersistenceContext` implement the identity map pattern?
3. Explain the difference between `@OneToMany` with `mappedBy` and `@ManyToOne`.
4. How do you handle optimistic locking with `@Version` in distributed systems?
5. What are Hibernate types and how do you create a custom `UserType`?

---

*Lab 04 MOCK_INTERVIEW.md — Part of Backend Academy Interview Preparation*
