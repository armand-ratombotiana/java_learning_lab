package com.learning.backend04;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a university department.
 *
 * @Entity marks this POJO as a JPA entity managed by Hibernate.
 * @OneToMany defines the one-to-many relationship: one department has many employees.
 * - mappedBy indicates the owning side is the "department" field in Employee.
 * - cascade = ALL means operations on Department propagate to its employees.
 * - orphanRemoval removes employees when they are removed from the list.
 */
@Entity
@Table(name = "departments")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Employee> employees = new ArrayList<>();

    public Department() {}

    public Department(String name) {
        this.name = name;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Employee> getEmployees() { return employees; }
    public void setEmployees(List<Employee> employees) { this.employees = employees; }

    /**
     * Helper method to maintain bidirectional consistency.
     */
    public void addEmployee(Employee employee) {
        employees.add(employee);
        employee.setDepartment(this);
    }

    @Override
    public String toString() {
        return "Department{id=" + id + ", name='" + name + "', employeeCount=" + employees.size() + "}";
    }
}
