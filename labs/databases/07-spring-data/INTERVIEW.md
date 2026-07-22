# Interview Questions: Spring Data JPA (Oracle Focus)

## Oracle-Specific Questions
- How does Hibernate's Oracle dialect differ from other dialects? What Oracle-specific features does it enable?
- Explain Hibernate sequence generation with Oracle — `SEQUENCE` vs `IDENTITY` vs `TABLE` generators.
- How do you tune Hibernate batch operations for Oracle to minimize round trips?
- What is the N+1 problem in Spring Data JPA and how does it manifest with Oracle queries?
- How does Hibernate handle Oracle's REF CURSOR for stored procedure calls?
- Explain Hibernate second-level cache with Oracle Coherence or Redis.
- How do you use Oracle-specific SQL functions (CONNECT BY, MERGE) with Spring Data JPA?
- What are the best practices for connection pooling with Oracle (HikariCP, UCP)?

## Google Cloud / Technical
- Spring Data JPA with Cloud SQL (PostgreSQL/MySQL) vs Oracle
- Cloud SQL IAM database authentication for Spring Boot
- Spring Data JPA on Google Cloud Run with Oracle backend

## Microsoft / Azure
- Spring Data JPA with Azure SQL vs Oracle
- Azure Spring Apps with Oracle backend via JDBC
- Azure Cosmos DB vs Oracle for Spring Data JPA

## Amazon / AWS
- Spring Data JPA on Elastic Beanstalk with RDS Oracle
- AWS Secrets Manager for Spring Boot Oracle credentials
- RDS Proxy for Spring Data JPA connection pooling

## Apple
- Secure coding practices for JPA entities handling Apple user data
- Apple privacy requirements for ORM-mapped data

## LeetCode-Style SQL Problems
| Problem | Topic | Difficulty | Pattern |
|---------|-------|-----------|---------|
| LC 175 | Combine Two Tables | Easy | JOIN |
| LC 176 | Second Highest Salary | Easy | Subquery |
| LC 177 | Nth Highest Salary | Medium | Window Function |
| LC 184 | Department Highest Salary | Medium | @Query JPQL |
| LC 185 | Department Top Three Salaries | Hard | Window + Pagination |
| LC 196 | Delete Duplicate Emails | Easy | @Modifying @Query |
| LC 197 | Rising Temperature | Easy | Self JOIN JPQL |
| LC 262 | Trips and Users | Hard | JPQL + CASE |

## Production Scenarios
- Scenario 1: "Hibernate sequence generation causing contention on Oracle SEQUENCE"
- Scenario 2: "N+1 query problem in Spring Data REST API — response time 30 seconds"
- Scenario 3: "LazyInitializationException in serialization — session-per-request pattern"
- Scenario 4: "Oracle ORA-01795: maximum number of expressions in IN list is 1000"

## Interview Patterns & Tips
- Oracle interviews expect JPA/Hibernate knowledge for Java-based Oracle apps
- Know Oracle dialect configuration and sequence generation strategies
- Expect questions on batch operations, pagination, and locking in Oracle
- Spring Data + Oracle roles: $120K-$175K
- OCP Java + Oracle combination is highly valued
