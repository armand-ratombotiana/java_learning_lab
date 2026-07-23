# LEETCODE_SOLUTIONS — Flyway / Liquibase

## Database Migration Solutions

| Migration Problem | Flyway Solution | Liquibase Solution |
|------------------|-----------------|-------------------|
| Create table | V1__create_employees.sql | changelog-01.yaml |
| Add constraint | V2__add_fk.sql | changeset with addForeignKeyConstraint |
| Modify column | V3__alter_salary_type.sql | changeset with modifyDataType |
| Seed data | V4__seed_employees.sql | changeset with insert |
| Index creation | V5__add_indexes.sql | changeset with createIndex |
| LeetCode schema | V6__create_leetcode_tables.sql | changeset with SQL |

### Example: Create LeetCode Schema via Migration
```sql
-- V1__create_leetcode_tables.sql
CREATE TABLE employee (
    id INT PRIMARY KEY,
    name VARCHAR(100),
    salary DECIMAL(10,2),
    managerId INT REFERENCES employee(id)
);
```
