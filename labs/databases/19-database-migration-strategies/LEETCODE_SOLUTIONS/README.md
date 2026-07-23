# LEETCODE_SOLUTIONS — Database Migration Strategies

## Schema Evolution Solutions

| Migration Type | LeetCode Schema Change | Flyway Migration |
|----------------|----------------------|------------------|
| Add column | Add new fields to Employee | V2__add_employee_email.sql |
| Create index | Index on salary column | V3__index_employee_salary.sql |
| Add constraint | FK from Employee to Department | V4__add_dept_fk.sql |
| Rename column | Change column name | V5__rename_employee_field.sql |
| Add new table | Create Person, Address tables | V6__create_person_address.sql |
| Seed data | Insert sample data | V7__seed_employee_data.sql |

### Migration Example for LeetCode Schema
```sql
-- V1__create_employee_table.sql
CREATE TABLE Employee (
    id INT PRIMARY KEY,
    name VARCHAR(100),
    salary DECIMAL(10,2),
    managerId INT
);

-- V2__add_manager_constraint.sql
ALTER TABLE Employee
ADD CONSTRAINT fk_manager FOREIGN KEY (managerId) REFERENCES Employee(id);

-- V3__seed_data.sql
INSERT INTO Employee VALUES (1, 'Joe', 70000, 3);
INSERT INTO Employee VALUES (2, 'Henry', 80000, 4);
INSERT INTO Employee VALUES (3, 'Sam', 60000, NULL);
INSERT INTO Employee VALUES (4, 'Max', 90000, NULL);
```
