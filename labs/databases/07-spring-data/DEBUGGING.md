# Debugging: Spring Data JPA

## Enable SQL Logging
```properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

## Common Issues

| Symptom | Root Cause | Fix |
|---|---|---|
| `LazyInitializationException` | Accessing lazy collection outside tx | `@Transactional` or `JOIN FETCH` |
| `InvalidDataAccessApiUsageException` | Wrong ID type in repository | Match `@Id` field type |
| Query returning stale data | 1st-level cache in same session | `@Modifying(clearAutomatically=true)` |
| `PropertyReferenceException` | Property name typo in method | Check entity property names |
| Transaction not rolling back | Checked exception thrown | Use `@Transactional(rollbackFor = Exception.class)` |

## Hibernate Statistics
```properties
spring.jpa.properties.hibernate.generate_statistics=true
logging.level.org.hibernate.stat=DEBUG
```

## Debugging Proxy Issues
```java
// Check if it's a proxy
boolean isProxy = AopUtils.isAopProxy(repository);
// Unwrap to underlying implementation
SimpleJpaRepository<?, ?> impl = (SimpleJpaRepository<?, ?>)
    ((JpaRepositoryFactoryBean) factory).getObject();
```
