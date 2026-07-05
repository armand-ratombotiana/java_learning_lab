package com.learning.backend04;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Department entities.
 *
 * @Repository is a Spring stereotype that enables exception translation
 * (JPA exceptions → DataAccessException hierarchy).
 * Extending JpaRepository gives us CRUD, pagination, sorting, etc.
 *
 * Custom queries can be defined with @Query (JPQL or native SQL).
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    Optional<Department> findByName(String name);

    /**
     * Custom JPQL query — joins Department with Employee.
     * Spring Data JPA evaluates this at runtime and injects the :name parameter.
     */
    @Query("SELECT d FROM Department d LEFT JOIN FETCH d.employees WHERE d.name = :name")
    Optional<Department> findByNameWithEmployees(@Param("name") String name);

    /**
     * Native SQL query example.
     */
    @Query(value = "SELECT * FROM departments d WHERE LENGTH(d.name) > :minLength", nativeQuery = true)
    List<Department> findDepartmentsWithNameLongerThan(@Param("minLength") int minLength);
}
