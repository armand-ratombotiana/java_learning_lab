# Database Access - Pedagogic Guide

---

## Learning Objectives

By the end of this module, learners will be able to:

1. **Understand JDBC Architecture**
   - Explain the JDBC driver manager model
   - Describe connection lifecycle management
   - Identify different ResultSet types

2. **Implement Connection Pooling**
   - Configure HikariCP for production workloads
   - Tune pool parameters based on metrics
   - Detect and prevent connection leaks

3. **Write Efficient Database Code**
   - Use PreparedStatement correctly
   - Implement batch operations
   - Handle transactions properly

4. **Apply Production Patterns**
   - Implement circuit breaker for database access
   - Use read replicas for scaling
   - Handle distributed transactions

---

## Teaching Sequence

### Phase 1: Foundation (1-2 hours)

**Topic 1: JDBC Basics**
- Lecture: JDBC Architecture and Driver Loading
- Demo: Simple connection and query
- Exercise: Basic CRUD operations

**Topic 2: Connection Management**
- Lecture: Connection lifecycle and resource management
- Demo: try-with-resources pattern
- Exercise: Implement proper connection handling

### Phase 2: Performance (1-2 hours)

**Topic 3: Connection Pooling**
- Lecture: Why connection pooling matters
- Demo: HikariCP configuration
- Exercise: Configure pool for specific workload

**Topic 4: Batch Operations**
- Lecture: When to use batch operations
- Demo: Insert 10,000 records efficiently
- Exercise: Migrate legacy code to batch

### Phase 3: Advanced Patterns (2-3 hours)

**Topic 5: Transaction Management**
- Lecture: ACID properties in practice
- Demo: Nested transactions, savepoints
- Exercise: Implement order processing with transactions

**Topic 6: Production Patterns**
- Lecture: Circuit breaker, retry policies
- Demo: Implementing database circuit breaker
- Exercise: Add resilience to existing code

---

## Hands-On Projects

### Mini-Project: JDBC Employee Management
**Duration**: 4-5 hours
**Focus**: Core JDBC concepts

Learning outcomes:
- Connection management
- PreparedStatement usage
- Transaction handling
- DAO pattern

### Real-World Project: E-Commerce Order Processing
**Duration**: 12+ hours
**Focus**: Production patterns

Learning outcomes:
- Spring JdbcTemplate
- Batch processing
- Transaction management
- Stored procedures

---

## Assessment Criteria

### Must Have (Core)
- [ ] Create connections using try-with-resources
- [ ] Use PreparedStatement with parameters
- [ ] Implement basic transaction handling
- [ ] Configure connection pool with HikariCP

### Should Have (Intermediate)
- [ ] Implement batch operations
- [ ] Use JdbcTemplate for queries
- [ ] Create custom RowMapper
- [ ] Handle transaction rollback

### Nice to Have (Advanced)
- [ ] Implement circuit breaker pattern
- [ ] Use multiple data sources
- [ ] Execute stored procedures
- [ ] Configure read replicas

---

## Common Pitfalls

1. **Connection Leaks**
   - Always use try-with-resources
   - Enable leak detection in development

2. **SQL Injection**
   - Never concatenate user input into SQL
   - Always use PreparedStatement with parameters

3. **Transaction Mistakes**
   - Don't catch exceptions that abort transactions
   - Be aware of isolation levels

4. **Pool Exhaustion**
   - Monitor pool usage
   - Set appropriate timeouts

---

## Discussion Questions

1. When would you prefer raw JDBC over JPA/Hibernate?
2. How do you decide on connection pool size?
3. What are the trade-offs of different transaction isolation levels?
4. How would you handle a database failover in your application?

---

## Extension Activities

1. **Performance Challenge**: Optimize batch insert from 10 seconds to under 1 second
2. **Resilience Challenge**: Implement retry with exponential backoff
3. **Scaling Challenge**: Add read replica routing

---

## Additional Resources

- HikariCP Wiki: Configuration Best Practices
- Spring JDBC Template Reference
- PostgreSQL JDBC Documentation
- Database Performance Tuning Guide

