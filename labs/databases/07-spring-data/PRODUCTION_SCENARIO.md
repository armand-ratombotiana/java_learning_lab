# Production Scenarios: Spring Data JPA (Oracle Focus)

## Scenario 1: Hibernate Sequence Contention
**Context**: A Spring Boot application using Oracle with Hibernate for entity ID generation.
**Problem**: Under load (1000 TPS), inserts started failing with "ORA-00001: unique constraint violated". Sequence numbers were returning duplicate values.
**Root Cause**: Hibernate was configured with `GenerationType.SEQUENCE` using the default `INCREMENT BY 1`. Oracle sequences cache 20 values by default. Hibernate did not request the next 20 values; instead, it called `SEQ.NEXTVAL` for each insert. The sequence was exhausted and wrapped around (cycle enabled).
**Solution**: 1) Changed sequence `INCREMENT BY` to 50 and set Hibernate allocationSize to 50. 2) Used `GenerationType.IDENTITY` for high-volume tables instead of sequences. 3) Or used `GenerationType.TABLE` with HikariCP and proper locking. 4) Optimized bulk inserts: `hibernate.jdbc.batch_size=50` and `hibernate.order_inserts=true`. 5) Set sequence `CACHE 1000` to reduce dictionary contention.
**Lessons Learned**: Always match Hibernate allocationSize with sequence INCREMENT BY. Use batch inserts for high-volume tables. Monitor sequence contention via `V$SEQUENCES`. Use `GenerationType.SEQUENCE` over `IDENTITY` for batch inserts.

## Scenario 2: N+1 Queries in REST API
**Context**: A REST API endpoint `/api/products` returned a list of products with their categories.
**Problem**: The endpoint returned 100 products but executed 101 SQL queries (1 + 100). Response time was 30 seconds.
**Root Cause**: The `Product` entity had `@ManyToOne(fetch = FetchType.LAZY)` to `Category`. When Jackson serialized the response, it accessed `product.getCategory().getName()` for each product, triggering a lazy load query per product.
**Solution**: 1) Added `@EntityGraph(attributePaths = "category")` on the repository method. 2) Used `JOIN FETCH` in the `@Query`: `SELECT p FROM Product p JOIN FETCH p.category`. 3) Enabled Hibernate batch fetching: `hibernate.default_batch_fetch_size=20`. 4) Used DTO projection: `SELECT new ProductDTO(p.id, p.name, c.name) FROM Product p JOIN p.category c`. 5) Verified query count with `spring.jpa.show-sql=true`.
**Lessons Learned**: Use EntityGraph or JOIN FETCH to solve N+1. Prefer DTO projections for read-only endpoints. Enable batch fetching as a safety net. Monitor executed queries in production with datasource-proxy.

## Scenario 3: LazyInitializationException in Serialization
**Context**: A REST API returned `LazyInitializationException: could not initialize proxy — no Session` for a GET endpoint.
**Problem**: The endpoint was supposed to return product details with category name, but serialization failed because the Hibernate session was already closed.
**Root Cause**: Spring's `OpenEntityManagerInViewFilter` was disabled. The JPA repository method returned from the service, which closed the EntityManager. Jackson then tried to access the lazy `category` proxy outside the session.
**Solution**: 1) Enabled `spring.jpa.open-in-view=true` (default). 2) Or used `@Transactional` on the service method to extend the session. 3) Or eagerly fetched the category with `JOIN FETCH`. 4) Used DTO projection to avoid proxying altogether. 5) Configured Jackson to serialize lazy-loaded entities with `com.fasterxml.jackson.datatype.hibernate6`.
**Lessons Learned**: Enable open-in-view for simple REST APIs. Prefer DTOs over entity serialization. Understand Hibernate session boundaries. Test serialization with lazy-loaded associations.

## Scenario 4: IN List Limit Exceeded — ORA-01795
**Context**: A Spring Data JPA query with `findByDepartmentIdIn(List<Long> deptIds)` was called with 5000 department IDs.
**Problem**: The query failed with `ORA-01795: maximum number of expressions in a list is 1000`. Oracle has a hard limit of 1000 elements in an IN clause.
**Root Cause**: The application allowed users to select unlimited departments. The `IN` list grew to 5000 IDs, exceeding Oracle's limit. The query used `WHERE department_id IN (:ids)`.
**Solution**: 1) Split the query into batches of 1000: `Lists.partition(deptIds, 1000)` and executed multiple queries. 2) Changed query to use a temporary table: `CREATE GLOBAL TEMPORARY TABLE dept_temp(id NUMBER) ON COMMIT DELETE ROWS` and joined to it. 3) Added application validation to limit selections to 1000. 4) Used `OR` conditions with groups of 1000: `WHERE (id IN (:batch1) OR id IN (:batch2))`. 5) Implemented server-side pagination to avoid large IN lists.
**Lessons Learned**: Know Oracle's 1000-item IN clause limit. Use temporary tables for large IN lists. Validate user input to prevent oversized queries. Split large IN lists into batches of 1000.
