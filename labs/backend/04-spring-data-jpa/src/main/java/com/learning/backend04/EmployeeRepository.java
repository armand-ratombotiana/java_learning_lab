package com.learning.backend04;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for Employee entities.
 *
 * Spring Data JPA automatically implements methods derived from method names:
 * - findByEmail() → SELECT ... WHERE email = ?
 * - findBySalaryBetween() → SELECT ... WHERE salary BETWEEN ? AND ?
 * - countByDepartmentId() → SELECT COUNT(*) ... WHERE department_id = ?
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findByDepartmentId(Long departmentId);

    List<Employee> findBySalaryBetween(double min, double max);

    long countByDepartmentId(Long departmentId);

    /**
     * Custom JPQL query with named parameter.
     */
    @Query("SELECT e FROM Employee e WHERE e.hireDate BETWEEN :start AND :end ORDER BY e.hireDate")
    List<Employee> findEmployeesHiredBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);

    /**
     * Aggregation query returning average salary per department.
     */
    @Query("SELECT d.name AS department, AVG(e.salary) AS avgSalary " +
           "FROM Employee e JOIN e.department d GROUP BY d.name")
    List<Object[]> averageSalaryByDepartment();
}
