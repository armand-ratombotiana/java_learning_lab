# Databases Flashcards

## Card 1: JDBC
- **Q:** What is JDBC?
- **A:** Java Database Connectivity - API for database operations

---

## Card 2: PreparedStatement
- **Q:** What is PreparedStatement?
- **A:** Pre-compiled SQL with parameter placeholders (?) for security and performance

---

## Card 3: ResultSet
- **Q:** What is ResultSet?
- **A:** Holds query results, allows iterating through rows

---

## Card 4: Transaction
- **Q:** What are ACID properties?
- **A:** Atomicity, Consistency, Isolation, Durability

---

## Card 5: JPA
- **Q:** What is JPA?
- **A:** Java Persistence API - ORM standard for mapping Java to databases

---

## Card 6: Entity
- **Q:** What is a JPA entity?
- **A:** Java class mapped to a database table

---

## Card 7: @Id
- **Q:** What does @Id annotation do?
- **A:** Marks field as primary key

---

## Card 8: @GeneratedValue
- **Q:** What does @GeneratedValue do?
- **A:** Specifies how to generate primary key (IDENTITY, SEQUENCE, etc.)

---

## Card 9: EntityManager
- **Q:** What is EntityManager?
- **A:** Interface for managing entities and transactions

---

## Card 10: @OneToMany
- **Q:** What is @OneToMany relationship?
- **A:** One entity relates to many of another (e.g., Author has many Books)

---

## Card 11: FetchType.LAZY
- **Q:** What is LAZY fetch type?
- **A:** Related entities loaded on demand (not immediately)

---

## Card 12: FetchType.EAGER
- **Q:** What is EAGER fetch type?
- **A:** Related entities loaded immediately with parent

---

## Card 13: @Transactional
- **Q:** What does @Transactional do?
- **A:** Marks method for transaction management

---

## Card 14: Propagation
- **Q:** What is transaction propagation?
- **A:** How transactions behave when called from another transaction

---

## Card 15: Isolation Level
- **Q:** What is isolation level?
- **A:** Controls how concurrent transactions see each other's changes

---

## Card 16: Optimistic Locking
- **Q:** What is optimistic locking?
- **A:** Uses @Version field to detect concurrent modifications

---

## Card 17: Pessimistic Locking
- **Q:** What is pessimistic locking?
- **A:** Locks rows in database using SELECT FOR UPDATE

---

## Card 18: Connection Pool
- **Q:** What is connection pooling?
- **A:** Reusing database connections instead of creating new ones

---

## Card 19: HikariCP
- **Q:** What is HikariCP?
- **A:** Fast, lightweight JDBC connection pool (default in Spring Boot)

---

## Card 20: @Query
- **Q:** What does @Query do?
- **A:** Defines custom JPQL or native SQL query

---

## Quick Reference

| Class/Annotation | Purpose |
|-----------------|---------|
| DriverManager | Creates database connections |
| PreparedStatement | Pre-compiled SQL |
| ResultSet | Query results |
| @Entity | Maps class to table |
| @Id | Primary key |
| @GeneratedValue | Auto-generate ID |
| @OneToMany | One-to-many relationship |
| @ManyToOne | Many-to-one relationship |
| @ManyToMany | Many-to-many relationship |
| @Query | Custom JPQL/SQL |
| @Transactional | Transaction boundary |
| @Modifying | Update/delete query |
| @Version | Optimistic locking |
| EntityManager | Entity operations |
| JpaRepository | Spring Data JPA |
| HikariCP | Connection pool |