package com.learning.backend04;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Entity representing an employee belonging to a department.
 *
 * @ManyToOne defines the owning side of the relationship.
 * @JoinColumn specifies the foreign key column in the employees table.
 * The owning side (Employee) holds the foreign key.
 */
@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    private double salary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    public Employee() {}

    public Employee(String name, String email, LocalDate hireDate, double salary) {
        this.name = name;
        this.email = email;
        this.hireDate = hireDate;
        this.salary = salary;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }

    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }

    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }

    @Override
    public String toString() {
        return "Employee{id=" + id + ", name='" + name + "', email='" + email + "'}";
    }
}
