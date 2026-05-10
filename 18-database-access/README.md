# Database Access Module

<div align="center">

![Java](https://img.shields.io/badge/Java_17+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![JDBC](https://img.shields.io/badge/JDBC-Database-0066CC?style=for-the-badge)
![HikariCP](https://img.shields.io/badge/HikariCP-Connection%20Pool-00CC66?style=for-the-badge)

**Master JDBC, Connection Pooling, and Transaction Management**

</div>

---

## Overview

This module covers database access patterns in Java, from raw JDBC to advanced connection pooling and transaction management. You'll learn production patterns used in enterprise applications.

---

## Topics Covered

### 1. JDBC Fundamentals
- Connection management
- PreparedStatement vs Statement
- ResultSet handling
- Batch operations
- Transaction management

### 2. Connection Pooling
- HikariCP configuration
- Pool sizing strategies
- Connection validation
- Leak detection
- Performance tuning

### 3. Spring JDBC Template
- JdbcTemplate operations
- NamedParameterJdbcTemplate
- RowMapper and ResultSetExtractor
- Batch operations with JdbcTemplate

### 4. Advanced Patterns
- Stored procedures
- CallableStatement
- Multiple data sources
- Transaction isolation levels
- Distributed transactions

---

## Module Structure

```
18-database-access/
├── README.md                      # This file
├── PROJECTS.md                    # Hands-on projects
├── PEDAGOGIC_GUIDE.md            # Teaching guide
├── EXERCISES.md                  # Practice exercises
└── src/main/java/com/learning/  # Source code
```

---

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+
- H2 Database (included as dependency)

### Run Examples
```bash
cd 18-database-access
mvn clean compile
mvn exec:java -Dexec.mainClass="com.learning.Main"
```

### Run Tests
```bash
mvn test
```

---

## Key Concepts

### Connection Pooling Best Practices

```java
// HikariCP Configuration
HikariConfig config = new HikariConfig();
config.setMaximumPoolSize(20);      // Rule of thumb: cores * 2
config.setMinimumIdle(5);
config.setConnectionTimeout(30000);
config.setIdleTimeout(600000);
config.setMaxLifetime(1800000);
config.setPoolName("ProductionPool");
```

### Transaction Patterns

```java
// Programmatic transaction
@Transactional
public void processOrder(Order order) {
    jdbcTemplate.update("INSERT INTO orders...", order);
    jdbcTemplate.update("UPDATE inventory...", order);
    jdbcTemplate.update("INSERT INTO audit_log...", order);
}
```

---

## Production Patterns

1. **Connection Pool Sizing**: Match pool size to expected concurrency
2. **Statement Pooling**: Reuse prepared statements
3. **Batch Operations**: Process bulk data efficiently
4. **Read/Write Splitting**: Separate read and write operations
5. **Circuit Breaker**: Handle database failures gracefully

---

## Next Steps

After completing this module, proceed to:
- [19-microservices](../19-microservices) - Service communication
- [17-jpa-hibernate](../17-jpa-hibernate) - ORM with JPA/Hibernate

---

## Resources

- [HikariCP Documentation](https://github.com/brettwooldridge/HikariCP)
- [Spring JDBC Reference](https://docs.spring.io/spring-framework/docs/current/reference/html/jdbc.html)
- [JDBC Tutorial](https://docs.oracle.com/javase/tutorial/jdbc/)

