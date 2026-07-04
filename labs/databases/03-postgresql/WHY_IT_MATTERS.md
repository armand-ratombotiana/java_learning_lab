# Why PostgreSQL Matters

PostgreSQL is a **critical skill** for modern Java back-end development.

## Market Position
- #1 Most Loved database (Stack Overflow survey)
- #4 Most Popular database (DB-Engines)
- Default choice for new projects (alongside PostgreSQL)
- Standard for cloud RDS (AWS, GCP, Azure, DigitalOcean)

## Java Ecosystem
```yaml
# application.yml
spring.datasource:
  url: jdbc:postgresql://localhost:5432/mydb
  driver-class-name: org.postgresql.Driver
  username: app_user
  password: secret

spring.jpa:
  database: POSTGRESQL
  hibernate.ddl-auto: validate
  properties.hibernate.dialect: org.hibernate.dialect.PostgreSQLDialect
```

## When PostgreSQL Excels
- Complex queries with CTEs and window functions
- JSONB for flexible schemas with relational integrity
- Full-text search without additional infrastructure
- Geospatial applications (PostGIS)
- Time-series data (TimescaleDB extension)
- High-concurrency OLTP
