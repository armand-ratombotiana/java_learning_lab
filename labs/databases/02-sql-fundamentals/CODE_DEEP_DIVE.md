# Code Deep Dive: SQL with Java/JDBC/JPA

## Dynamic Query Builder with JDBC

```java
public class QueryBuilder {
    public List<Employee> searchEmployees(String name, Long deptId,
                                           BigDecimal minSalary, String sortBy) {
        StringBuilder sql = new StringBuilder("SELECT * FROM employees WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (name != null) {
            sql.append(" AND name LIKE ?");
            params.add("%" + name + "%");
        }
        if (deptId != null) {
            sql.append(" AND dept_id = ?");
            params.add(deptId);
        }
        if (minSalary != null) {
            sql.append(" AND salary >= ?");
            params.add(minSalary);
        }
        if (sortBy != null) {
            sql.append(" ORDER BY ").append(sanitizeColumnName(sortBy));
        }
        sql.append(" LIMIT 100");

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                return mapEmployees(rs);
            }
        }
    }

    private String sanitizeColumnName(String name) {
        if (!name.matches("[a-zA-Z_]+")) {
            throw new IllegalArgumentException("Invalid column name");
        }
        return name;
    }
}
```

## RowMapper Pattern (Spring JDBC Template)

```java
public class EmployeeRowMapper implements RowMapper<Employee> {
    @Override
    public Employee mapRow(ResultSet rs, int rowNum) throws SQLException {
        Employee emp = new Employee();
        emp.setId(rs.getLong("id"));
        emp.setName(rs.getString("name"));
        emp.setSalary(rs.getBigDecimal("salary"));
        emp.setHireDate(rs.getTimestamp("hire_date").toLocalDateTime());
        emp.setActive(rs.getBoolean("active"));
        return emp;
    }
}

// Usage
List<Employee> employees = jdbcTemplate.query(
    "SELECT * FROM employees WHERE dept_id = ?",
    new EmployeeRowMapper(),
    departmentId
);
```

## JPA @Query with Complex Queries

```java
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("""
        SELECT new com.example.dto.EmployeeDeptDTO(
            e.id, e.name, d.name, e.salary
        )
        FROM Employee e
        JOIN e.department d
        WHERE d.name IN :deptNames
        AND e.salary BETWEEN :minSal AND :maxSal
        ORDER BY e.salary DESC
    """)
    List<EmployeeDeptDTO> findEmployeesByDeptAndSalaryRange(
            @Param("deptNames") List<String> deptNames,
            @Param("minSal") BigDecimal minSal,
            @Param("maxSal") BigDecimal maxSal
    );
}
```

## Native SQL with JPA

```java
@Query(value = """
    SELECT d.name AS department,
           AVG(e.salary) AS avg_salary,
           PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY e.salary) AS median_salary
    FROM employees e
    JOIN departments d ON e.dept_id = d.id
    GROUP BY d.name
    HAVING AVG(e.salary) > :minAvg
""", nativeQuery = true)
List<Object[]> findDepartmentStats(@Param("minAvg") BigDecimal minAvg);
```

## PostgreSQL Window Functions via JPA

```java
@Query(value = """
    SELECT name, salary, dept_id,
           ROW_NUMBER() OVER (PARTITION BY dept_id ORDER BY salary DESC) AS rank
    FROM employees
""", nativeQuery = true)
List<Object[]> findEmployeesWithRank();

// Usage
for (Object[] row : repo.findEmployeesWithRank()) {
    String name = (String) row[0];
    BigDecimal salary = (BigDecimal) row[1];
    Long deptId = ((Number) row[2]).longValue();
    int rank = ((Number) row[3]).intValue();
    System.out.printf("%s (dept %d) earns %s, rank %d%n",
            name, deptId, salary, rank);
}
```

## Pagination with Keyset (not OFFSET)

```java
public Slice<Employee> findNextPage(Long lastSeenId, Pageable pageable) {
    List<Employee> employees = em.createQuery("""
        SELECT e FROM Employee e
        WHERE e.id > :lastId
        ORDER BY e.id ASC
    """, Employee.class)
        .setParameter("lastId", lastSeenId)
        .setMaxResults(pageable.getPageSize() + 1)
        .getResultList();

    boolean hasNext = employees.size() > pageable.getPageSize();
    return new SliceImpl<>(
        hasNext ? employees.subList(0, pageable.getPageSize()) : employees,
        pageable,
        hasNext
    );
}
```
