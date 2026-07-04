# Spring Data JPA

## Overview
Spring Data JPA is a Spring Framework module that reduces boilerplate code for JPA-based data access layers. It provides repository abstractions, query derivation from method names, specifications, auditing, and pagination support.

## Key Concepts
- **Repository Hierarchy**: `Repository<T, ID>` → `CrudRepository` → `JpaRepository`
- **Query Methods**: Derive queries from method names (`findByLastName`, `findByAgeBetween`)
- **@Query**: Custom JPQL or native SQL queries
- **Specifications**: Type-safe, composable predicates (Spring Data JPA criteria)
- **Auditing**: Automatic tracking of created/modified dates and users
- **Paging & Sorting**: `Pageable`, `Sort`, `Page<T>`

## Java Example
```java
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByLastName(String lastName);
    Page<User> findByAgeBetween(int min, int max, Pageable pageable);
    @Query("SELECT u FROM User u WHERE u.email LIKE %:domain")
    List<User> findByEmailDomain(@Param("domain") String domain);
}
```
