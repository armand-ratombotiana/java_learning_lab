# JPA and Hibernate Module - PROJECTS.md

---

# Mini-Project: Employee Management System with JPA

## Project Overview

**Duration**: 4-5 hours  
**Difficulty**: Intermediate  
**Concepts Used**: JPA Entities, EntityManager, CRUD Operations, JPQL Queries, Entity Relationships, Transactions

This mini-project demonstrates fundamental JPA and Hibernate concepts through a complete Employee Management System with department relationships.

---

## Project Structure

```
17-jpa-hibernate/
├── pom.xml
├── src/main/java/com/learning/
│   ├── Main.java
│   ├── entity/
│   │   ├── Employee.java
│   │   ├── Department.java
│   │   └── Project.java
│   ├── repository/
│   │   ├── EmployeeRepository.java
│   │   └── DepartmentRepository.java
│   ├── service/
│   │   └── EmployeeService.java
│   └── util/
│       └── JpaUtil.java
└── src/main/resources/
    └── META-INF/
        └── persistence.xml
```

---

## Step 1: POM.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>jpa-employee-system</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>
    
    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <hibernate.version>6.2.7.Final</hibernate.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.hibernate.orm</groupId>
            <artifactId>hibernate-core</artifactId>
            <version>${hibernate.version}</version>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <version>2.2.224</version>
        </dependency>
        <dependency>
            <groupId>jakarta.persistence</groupId>
            <artifactId>jakarta.persistence-api</artifactId>
            <version>3.1.0</version>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.30</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>
    
    <build>
        <sourceDirectory>src/main/java</sourceDirectory>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>17</source>
                    <target>17</target>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

---

## Step 2: JPA Entities

```java
// entity/Department.java
package com.learning.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "departments")
public class Department {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @Column(name = "location")
    private String location;
    
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, 
                fetch = FetchType.LAZY)
    private List<Employee> employees = new ArrayList<>();
    
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
    private List<Project> projects = new ArrayList<>();
    
    public Department() {}
    
    public Department(String name, String location) {
        this.name = name;
        this.location = location;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public List<Employee> getEmployees() { return employees; }
    public void setEmployees(List<Employee> employees) { this.employees = employees; }
    
    public List<Project> getProjects() { return projects; }
    public void setProjects(List<Project> projects) { this.projects = projects; }
    
    public void addEmployee(Employee employee) {
        employees.add(employee);
        employee.setDepartment(this);
    }
    
    public void addProject(Project project) {
        projects.add(project);
        project.setDepartment(this);
    }
    
    @Override
    public String toString() {
        return "Department{id=" + id + ", name='" + name + "', location='" + location + "'}";
    }
}
```

```java
// entity/Employee.java
package com.learning.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "employees")
public class Employee {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String firstName;
    
    @Column(nullable = false)
    private String lastName;
    
    @Column(unique = true)
    private String email;
    
    @Column(name = "salary")
    private Double salary;
    
    @Column(name = "hire_date")
    private LocalDate hireDate;
    
    @Enumerated(EnumType.STRING)
    private EmployeeType type;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;
    
    @ManyToMany
    @JoinTable(
        name = "employee_projects",
        joinColumns = @JoinColumn(name = "employee_id"),
        inverseJoinColumns = @JoinColumn(name = "project_id")
    )
    private List<Project> projects = new java.util.ArrayList<>();
    
    public enum EmployeeType {
        FULL_TIME, PART_TIME, CONTRACTOR
    }
    
    public Employee() {}
    
    public Employee(String firstName, String lastName, String email, 
                    Double salary, EmployeeType type) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.salary = salary;
        this.type = type;
        this.hireDate = LocalDate.now();
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public Double getSalary() { return salary; }
    public void setSalary(Double salary) { this.salary = salary; }
    
    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }
    
    public EmployeeType getType() { return type; }
    public void setType(EmployeeType type) { this.type = type; }
    
    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }
    
    public List<Project> getProjects() { return projects; }
    public void setProjects(List<Project> projects) { this.projects = projects; }
    
    public void addProject(Project project) {
        projects.add(project);
        project.getEmployees().add(this);
    }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    @Override
    public String toString() {
        return "Employee{id=" + id + ", name='" + getFullName() + "', email='" + email + "'}";
    }
}
```

```java
// entity/Project.java
package com.learning.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projects")
public class Project {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    @Column(name = "start_date")
    private LocalDate startDate;
    
    @Column(name = "end_date")
    private LocalDate endDate;
    
    @Enumerated(EnumType.STRING)
    private ProjectStatus status;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;
    
    @ManyToMany(mappedBy = "projects")
    private List<Employee> employees = new ArrayList<>();
    
    public enum ProjectStatus {
        PLANNING, IN_PROGRESS, COMPLETED, ON_HOLD
    }
    
    public Project() {}
    
    public Project(String name, String description, ProjectStatus status) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.startDate = LocalDate.now();
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    
    public ProjectStatus getStatus() { return status; }
    public void setStatus(ProjectStatus status) { this.status = status; }
    
    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }
    
    public List<Employee> getEmployees() { return employees; }
    public void setEmployees(List<Employee> employees) { this.employees = employees; }
    
    @Override
    public String toString() {
        return "Project{id=" + id + ", name='" + name + "', status=" + status + "}";
    }
}
```

---

## Step 3: JPA Utility and Persistence Configuration

```java
// util/JpaUtil.java
package com.learning.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JpaUtil {
    
    private static final EntityManagerFactory emf;
    
    static {
        emf = Persistence.createEntityManagerFactory("employee-pu");
    }
    
    public static EntityManager createEntityManager() {
        return emf.createEntityManager();
    }
    
    public static void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
```

```xml
<!-- src/main/resources/META-INF/persistence.xml -->
<?xml version="1.0" encoding="UTF-8"?>
<persistence xmlns="http://xmlns.jcp.org/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence
             http://xmlns.jcp.org/xml/ns/persistence/persistence_3_1.xsd"
             version="3.1">
    
    <persistence-unit name="employee-pu" transaction-type="RESOURCE_LOCAL">
        <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>
        
        <class>com.learning.entity.Employee</class>
        <class>com.learning.entity.Department</class>
        <class>com.learning.entity.Project</class>
        
        <properties>
            <property name="jakarta.persistence.jdbc.driver" value="org.h2.Driver"/>
            <property name="jakarta.persistence.jdbc.url" value="jdbc:h2:mem:employeedb;DB_CLOSE_DELAY=-1"/>
            <property name="jakarta.persistence.jdbc.user" value="sa"/>
            <property name="jakarta.persistence.jdbc.password" value=""/>
            
            <property name="hibernate.dialect" value="org.hibernate.dialect.H2Dialect"/>
            <property name="hibernate.hbm2ddl.auto" value="create-drop"/>
            <property name="hibernate.show_sql" value="true"/>
            <property name="hibernate.format_sql" value="true"/>
        </properties>
    </persistence-unit>
</persistence>
```

---

## Step 4: Repository Layer

```java
// repository/EmployeeRepository.java
package com.learning.repository;

import com.learning.entity.Employee;
import com.learning.entity.Employee.EmployeeType;
import com.learning.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class EmployeeRepository {
    
    public void save(Employee employee) {
        EntityManager em = JpaUtil.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(employee);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
    
    public Employee findById(Long id) {
        EntityManager em = JpaUtil.createEntityManager();
        try {
            return em.find(Employee.class, id);
        } finally {
            em.close();
        }
    }
    
    public List<Employee> findAll() {
        EntityManager em = JpaUtil.createEntityManager();
        try {
            TypedQuery<Employee> query = em.createQuery(
                "SELECT e FROM Employee e", Employee.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public Employee findByEmail(String email) {
        EntityManager em = JpaUtil.createEntityManager();
        try {
            TypedQuery<Employee> query = em.createQuery(
                "SELECT e FROM Employee e WHERE e.email = :email", Employee.class);
            query.setParameter("email", email);
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        } finally {
            em.close();
        }
    }
    
    public List<Employee> findByDepartment(Long departmentId) {
        EntityManager em = JpaUtil.createEntityManager();
        try {
            TypedQuery<Employee> query = em.createQuery(
                "SELECT e FROM Employee e WHERE e.department.id = :deptId", Employee.class);
            query.setParameter("deptId", departmentId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<Employee> findByType(EmployeeType type) {
        EntityManager em = JpaUtil.createEntityManager();
        try {
            TypedQuery<Employee> query = em.createQuery(
                "SELECT e FROM Employee e WHERE e.type = :type", Employee.class);
            query.setParameter("type", type);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<Employee> findHighSalaried(Double minSalary) {
        EntityManager em = JpaUtil.createEntityManager();
        try {
            TypedQuery<Employee> query = em.createQuery(
                "SELECT e FROM Employee e WHERE e.salary >= :minSalary ORDER BY e.salary DESC", 
                Employee.class);
            query.setParameter("minSalary", minSalary);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public void update(Employee employee) {
        EntityManager em = JpaUtil.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(employee);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
    
    public void delete(Long id) {
        EntityManager em = JpaUtil.createEntityManager();
        try {
            em.getTransaction().begin();
            Employee employee = em.find(Employee.class, id);
            if (employee != null) {
                em.remove(employee);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
    
    public long countByDepartment(Long departmentId) {
        EntityManager em = JpaUtil.createEntityManager();
        try {
            Query query = em.createQuery(
                "SELECT COUNT(e) FROM Employee e WHERE e.department.id = :deptId");
            query.setParameter("deptId", departmentId);
            return (Long) query.getSingleResult();
        } finally {
            em.close();
        }
    }
    
    public double calculateAverageSalary() {
        EntityManager em = JpaUtil.createEntityManager();
        try {
            Query query = em.createQuery("SELECT AVG(e.salary) FROM Employee e");
            Double result = (Double) query.getSingleResult();
            return result != null ? result : 0.0;
        } finally {
            em.close();
        }
    }
}
```

```java
// repository/DepartmentRepository.java
package com.learning.repository;

import com.learning.entity.Department;
import com.learning.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class DepartmentRepository {
    
    public void save(Department department) {
        EntityManager em = JpaUtil.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(department);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
    
    public Department findById(Long id) {
        EntityManager em = JpaUtil.createEntityManager();
        try {
            return em.find(Department.class, id);
        } finally {
            em.close();
        }
    }
    
    public Department findByName(String name) {
        EntityManager em = JpaUtil.createEntityManager();
        try {
            TypedQuery<Department> query = em.createQuery(
                "SELECT d FROM Department d WHERE d.name = :name", Department.class);
            query.setParameter("name", name);
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        } finally {
            em.close();
        }
    }
    
    public List<Department> findAll() {
        EntityManager em = JpaUtil.createEntityManager();
        try {
            TypedQuery<Department> query = em.createQuery(
                "SELECT d FROM Department d", Department.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public void update(Department department) {
        EntityManager em = JpaUtil.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(department);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
    
    public void delete(Long id) {
        EntityManager em = JpaUtil.createEntityManager();
        try {
            em.getTransaction().begin();
            Department department = em.find(Department.class, id);
            if (department != null) {
                em.remove(department);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
```

---

## Step 5: Service Layer

```java
// service/EmployeeService.java
package com.learning.service;

import com.learning.entity.Department;
import com.learning.entity.Employee;
import com.learning.entity.Employee.EmployeeType;
import com.learning.entity.Project;
import com.learning.entity.Project.ProjectStatus;
import com.learning.repository.DepartmentRepository;
import com.learning.repository.EmployeeRepository;
import java.util.List;

public class EmployeeService {
    
    private final EmployeeRepository employeeRepo;
    private final DepartmentRepository departmentRepo;
    
    public EmployeeService() {
        this.employeeRepo = new EmployeeRepository();
        this.departmentRepo = new DepartmentRepository();
    }
    
    public void createEmployee(Employee employee) {
        employeeRepo.save(employee);
        System.out.println("Employee created: " + employee.getFullName());
    }
    
    public void assignEmployeeToDepartment(Long employeeId, Long departmentId) {
        Employee employee = employeeRepo.findById(employeeId);
        Department department = departmentRepo.findById(departmentId);
        
        if (employee != null && department != null) {
            employee.setDepartment(department);
            employeeRepo.update(employee);
            System.out.println("Assigned " + employee.getFullName() + " to " + department.getName());
        }
    }
    
    public void assignProjectToEmployee(Long employeeId, Long projectId) {
        EntityManager em = JpaUtil.createEntityManager();
        try {
            Employee employee = em.find(Employee.class, employeeId);
            Project project = em.find(Project.class, projectId);
            
            if (employee != null && project != null) {
                employee.addProject(project);
                em.getTransaction().begin();
                em.merge(employee);
                em.getTransaction().commit();
                System.out.println("Assigned project " + project.getName() + " to " + employee.getFullName());
            }
        } finally {
            em.close();
        }
    }
    
    public void listAllEmployees() {
        List<Employee> employees = employeeRepo.findAll();
        System.out.println("\n=== All Employees ===");
        employees.forEach(e -> System.out.println(e));
    }
    
    public void listEmployeesByDepartment(Long departmentId) {
        List<Employee> employees = employeeRepo.findByDepartment(departmentId);
        System.out.println("\n=== Employees in Department " + departmentId + " ===");
        employees.forEach(e -> System.out.println(e));
    }
    
    public void listHighSalariedEmployees(double minSalary) {
        List<Employee> employees = employeeRepo.findHighSalaried(minSalary);
        System.out.println("\n=== High Salaried Employees (>" + minSalary + ") ===");
        employees.forEach(e -> System.out.println(e.getFullName() + ": $" + e.getSalary()));
    }
    
    public void printDepartmentStats() {
        List<Department> departments = departmentRepo.findAll();
        System.out.println("\n=== Department Statistics ===");
        
        for (Department dept : departments) {
            long count = employeeRepo.countByDepartment(dept.getId());
            System.out.println(dept.getName() + ": " + count + " employees");
        }
    }
    
    public void printAverageSalary() {
        double avg = employeeRepo.calculateAverageSalary();
        System.out.println("\nAverage Salary: $" + String.format("%.2f", avg));
    }
    
    public void createDepartmentWithEmployees() {
        Department dept = new Department("Engineering", "Building A");
        departmentRepo.save(dept);
        
        Employee emp1 = new Employee("John", "Doe", "john@company.com", 75000.0, EmployeeType.FULL_TIME);
        Employee emp2 = new Employee("Jane", "Smith", "jane@company.com", 80000.0, EmployeeType.FULL_TIME);
        
        emp1.setDepartment(dept);
        emp2.setDepartment(dept);
        
        employeeRepo.save(emp1);
        employeeRepo.save(emp2);
        
        System.out.println("Created department with employees");
    }
}
```

---

## Step 6: Main Application

```java
// Main.java
package com.learning;

import com.learning.entity.Department;
import com.learning.entity.Employee;
import com.learning.entity.Employee.EmployeeType;
import com.learning.entity.Project;
import com.learning.entity.Project.ProjectStatus;
import com.learning.repository.DepartmentRepository;
import com.learning.repository.EmployeeRepository;
import com.learning.service.EmployeeService;

public class Main {
    
    public static void main(String[] args) {
        System.out.println("=== JPA Employee Management System ===\n");
        
        DepartmentRepository deptRepo = new DepartmentRepository();
        EmployeeRepository empRepo = new EmployeeRepository();
        
        Department itDept = new Department("IT", "Building 1");
        Department hrDept = new Department("HR", "Building 2");
        Department salesDept = new Department("Sales", "Building 3");
        
        deptRepo.save(itDept);
        deptRepo.save(hrDept);
        deptRepo.save(salesDept);
        
        System.out.println("Created departments");
        
        Employee emp1 = new Employee("Alice", "Johnson", "alice@company.com", 75000.0, EmployeeType.FULL_TIME);
        Employee emp2 = new Employee("Bob", "Williams", "bob@company.com", 65000.0, EmployeeType.FULL_TIME);
        Employee emp3 = new Employee("Charlie", "Brown", "charlie@company.com", 55000.0, EmployeeType.PART_TIME);
        Employee emp4 = new Employee("Diana", "Miller", "diana@company.com", 85000.0, EmployeeType.FULL_TIME);
        
        emp1.setDepartment(itDept);
        emp2.setDepartment(itDept);
        emp3.setDepartment(hrDept);
        emp4.setDepartment(salesDept);
        
        empRepo.save(emp1);
        empRepo.save(emp2);
        empRepo.save(emp3);
        empRepo.save(emp4);
        
        System.out.println("Created employees");
        
        Project project1 = new Project("Website Redesign", "Complete redesign of company website", ProjectStatus.IN_PROGRESS);
        project1.setDepartment(itDept);
        
        EntityManager em = JpaUtil.createEntityManager();
        em.getTransaction().begin();
        em.persist(project1);
        em.getTransaction().commit();
        em.close();
        
        System.out.println("Created projects");
        
        System.out.println("\n=== Initial Employee List ===");
        empRepo.findAll().forEach(e -> System.out.println(e.getFullName() + " - " + e.getDepartment().getName()));
        
        System.out.println("\n=== IT Department Employees ===");
        empRepo.findByDepartment(itDept.getId()).forEach(e -> System.out.println(e.getFullName()));
        
        System.out.println("\n=== Full-Time Employees ===");
        empRepo.findByType(EmployeeType.FULL_TIME).forEach(e -> System.out.println(e.getFullName()));
        
        System.out.println("\n=== High Salaried (>$70,000) ===");
        empRepo.findHighSalaried(70000.0).forEach(e -> System.out.println(e.getFullName() + ": $" + e.getSalary()));
        
        System.out.println("\n=== Department Employee Counts ===");
        deptRepo.findAll().forEach(d -> {
            long count = empRepo.countByDepartment(d.getId());
            System.out.println(d.getName() + ": " + count + " employees");
        });
        
        double avgSalary = empRepo.calculateAverageSalary();
        System.out.println("\nAverage Salary: $" + String.format("%.2f", avgSalary));
        
        emp1.setSalary(80000.0);
        empRepo.update(emp1);
        System.out.println("\nUpdated " + emp1.getFullName() + "'s salary to $80,000");
        
        System.out.println("\n=== Final Employee List ===");
        empRepo.findAll().forEach(e -> System.out.println(e.getFullName() + " - $" + e.getSalary()));
        
        JpaUtil.close();
        System.out.println("\n=== System Shutdown ===");
    }
}
```

---

## Build Instructions

```bash
cd 17-jpa-hibernate
mvn clean compile
mvn exec:java -Dexec.mainClass="com.learning.Main"
```

To run with H2 console:
```bash
mvn exec:java -Dexec.mainClass="com.learning.Main" -Dhibernate.show_sql=true
```

---

# Real-World Project: E-Commerce Order Management System

## Project Overview

**Duration**: 12+ hours  
**Difficulty**: Advanced  
**Concepts Used**: Complex Entity Relationships, Named Queries, Entity Graphs, Batch Operations, Caching, Transactions, JPA Criteria API

This comprehensive e-commerce system demonstrates advanced JPA/Hibernate patterns for managing orders, products, customers, and inventory with complex relationships and optimized queries.

---

## Project Structure

```
17-jpa-hibernate/
├── pom.xml
├── src/main/java/com/learning/
│   ├── Main.java
│   ├── entity/
│   │   ├── Customer.java
│   │   ├── Order.java
│   │   ├── OrderItem.java
│   │   ├── Product.java
│   │   ├── Category.java
│   │   ├── Inventory.java
│   │   └── Payment.java
│   ├── repository/
│   │   ├── CustomerRepository.java
│   │   ├── OrderRepository.java
│   │   ├── ProductRepository.java
│   │   └── InventoryRepository.java
│   ├── service/
│   │   ├── OrderService.java
│   │   ├── InventoryService.java
│   │   └── AnalyticsService.java
│   ├── dto/
│   │   ├── OrderSummary.java
│   │   └── ProductDTO.java
│   └── util/
│       ├── JpaUtil.java
│       └── QueryUtil.java
└── src/main/resources/
    └── META-INF/
        └── persistence.xml
```

---

## POM.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>jpa-ecommerce</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>
    
    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <hibernate.version>6.2.7.Final</hibernate.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.hibernate.orm</groupId>
            <artifactId>hibernate-core</artifactId>
            <version>${hibernate.version}</version>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <version>2.2.224</version>
        </dependency>
        <dependency>
            <groupId>jakarta.persistence</groupId>
            <artifactId>jakarta.persistence-api</artifactId>
            <version>3.1.0</version>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.30</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>
    
    <build>
        <sourceDirectory>src/main/java</sourceDirectory>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>17</source>
                    <target>17</target>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

---

## Entity Classes

```java
// entity/Category.java
package com.learning.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
public class Category {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;
    
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Category> subcategories = new ArrayList<>();
    
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Product> products = new ArrayList<>();
    
    public Category() {}
    
    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Category getParent() { return parent; }
    public void setParent(Category parent) { this.parent = parent; }
    
    public List<Category> getSubcategories() { return subcategories; }
    public void setSubcategories(List<Category> subcategories) { this.subcategories = subcategories; }
    
    public List<Product> getProducts() { return products; }
    public void setProducts(List<Product> products) { this.products = products; }
}
```

```java
// entity/Product.java
package com.learning.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@NamedQueries({
    @NamedQuery(name = "Product.findAllActive", query = "SELECT p FROM Product p WHERE p.active = true"),
    @NamedQuery(name = "Product.findByCategory", query = "SELECT p FROM Product p WHERE p.category.id = :categoryId AND p.active = true"),
    @NamedQuery(name = "Product.findByPriceRange", query = "SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice AND p.active = true"),
    @NamedQuery(name = "Product.findTopRated", query = "SELECT p FROM Product p WHERE p.active = true ORDER BY p.rating DESC"),
    @NamedQuery(name = "Product.countByCategory", query = "SELECT COUNT(p) FROM Product p WHERE p.category.id = :categoryId")
})
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(length = 2000)
    private String description;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    private String sku;
    
    @Column(name = "active")
    private boolean active = true;
    
    private Double rating;
    
    @Column(name = "review_count")
    private Integer reviewCount = 0;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    
    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL)
    private Inventory inventory;
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public Product() {}
    
    public Product(String name, String description, BigDecimal price, String sku) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.sku = sku;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    
    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
    
    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }
    
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    
    public Inventory getInventory() { return inventory; }
    public void setInventory(Inventory inventory) { this.inventory = inventory; }
    
    public List<OrderItem> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItem> orderItems) { this.orderItems = orderItems; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
```

```java
// entity/Inventory.java
package com.learning.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "inventory")
public class Inventory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;
    
    @Column(name = "quantity")
    private Integer quantity = 0;
    
    @Column(name = "reserved_quantity")
    private Integer reservedQuantity = 0;
    
    @Column(name = "reorder_level")
    private Integer reorderLevel = 10;
    
    @Column(name = "reorder_quantity")
    private Integer reorderQuantity = 50;
    
    public Inventory() {}
    
    public Inventory(Product product, Integer quantity) {
        this.product = product;
        this.quantity = quantity;
    }
    
    public Integer getAvailableQuantity() {
        return quantity - reservedQuantity;
    }
    
    public boolean needsReorder() {
        return getAvailableQuantity() <= reorderLevel;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    
    public Integer getReservedQuantity() { return reservedQuantity; }
    public void setReservedQuantity(Integer reservedQuantity) { this.reservedQuantity = reservedQuantity; }
    
    public Integer getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(Integer reorderLevel) { this.reorderLevel = reorderLevel; }
    
    public Integer getReorderQuantity() { return reorderQuantity; }
    public void setReorderQuantity(Integer reorderQuantity) { this.reorderQuantity = reorderQuantity; }
}
```

```java
// entity/Customer.java
package com.learning.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
public class Customer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String firstName;
    
    @Column(nullable = false)
    private String lastName;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    private String phone;
    
    @Column(name = "loyalty_points")
    private Integer loyaltyPoints = 0;
    
    @Enumerated(EnumType.STRING)
    private CustomerTier tier = CustomerTier.BRONZE;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address address;
    
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    public enum CustomerTier {
        BRONZE, SILVER, GOLD, PLATINUM
    }
    
    public Customer() {}
    
    public Customer(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.createdAt = LocalDateTime.now();
    }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public Integer getLoyaltyPoints() { return loyaltyPoints; }
    public void setLoyaltyPoints(Integer loyaltyPoints) { this.loyaltyPoints = loyaltyPoints; }
    
    public CustomerTier getTier() { return tier; }
    public void setTier(CustomerTier tier) { this.tier = tier; }
    
    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }
    
    public List<Order> getOrders() { return orders; }
    public void setOrders(List<Order> orders) { this.orders = orders; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
}

@Entity
@Table(name = "addresses")
class Address {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    
    public Address() {}
    
    public Address(String street, String city, String state, String zipCode, String country) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.country = country;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    
    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }
    
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
}
```

```java
// entity/Order.java
package com.learning.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@NamedQueries({
    @NamedQuery(name = "Order.findByCustomer", query = "SELECT o FROM Order o WHERE o.customer.id = :customerId ORDER BY o.orderDate DESC"),
    @NamedQuery(name = "Order.findByStatus", query = "SELECT o FROM Order o WHERE o.status = :status"),
    @NamedQuery(name = "Order.findByDateRange", query = "SELECT o FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate"),
    @NamedQuery(name = "Order.countByStatus", query = "SELECT COUNT(o) FROM Order o WHERE o.status = :status")
})
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "order_number", unique = true, nullable = false)
    private String orderNumber;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();
    
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    
    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;
    
    @Column(name = "shipping_address")
    private String shippingAddress;
    
    @Column(name = "billing_address")
    private String billingAddress;
    
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Payment payment;
    
    @Column(name = "order_date")
    private LocalDateTime orderDate;
    
    @Column(name = "shipping_date")
    private LocalDateTime shippingDate;
    
    @Column(name = "delivery_date")
    private LocalDateTime deliveryDate;
    
    public enum OrderStatus {
        PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED, REFUNDED
    }
    
    @PrePersist
    protected void onCreate() {
        orderDate = LocalDateTime.now();
        if (orderNumber == null) {
            orderNumber = "ORD-" + System.currentTimeMillis();
        }
    }
    
    public Order() {}
    
    public Order(Customer customer, String shippingAddress) {
        this.customer = customer;
        this.shippingAddress = shippingAddress;
        this.billingAddress = shippingAddress;
        this.status = OrderStatus.PENDING;
        this.totalAmount = BigDecimal.ZERO;
    }
    
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
        recalculateTotal();
    }
    
    public void removeItem(OrderItem item) {
        items.remove(item);
        item.setOrder(null);
        recalculateTotal();
    }
    
    public void recalculateTotal() {
        this.totalAmount = items.stream()
            .map(item -> item.getUnitPrice().multiply(new BigDecimal(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
    
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    
    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    
    public String getBillingAddress() { return billingAddress; }
    public void setBillingAddress(String billingAddress) { this.billingAddress = billingAddress; }
    
    public Payment getPayment() { return payment; }
    public void setPayment(Payment payment) { this.payment = payment; }
    
    public LocalDateTime getOrderDate() { return orderDate; }
    public LocalDateTime getShippingDate() { return shippingDate; }
    public LocalDateTime getDeliveryDate() { return deliveryDate; }
}
```

```java
// entity/OrderItem.java
package com.learning.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class OrderItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;
    
    public OrderItem() {}
    
    public OrderItem(Product product, Integer quantity) {
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = product.getPrice();
    }
    
    public BigDecimal getSubtotal() {
        return unitPrice.multiply(new BigDecimal(quantity));
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
}
```

```java
// entity/Payment.java
package com.learning.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;
    
    @Enumerated(EnumType.STRING)
    private PaymentMethod method;
    
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Column(name = "transaction_id")
    private String transactionId;
    
    @Column(name = "payment_date")
    private LocalDateTime paymentDate;
    
    public enum PaymentMethod {
        CREDIT_CARD, DEBIT_CARD, PAYPAL, BANK_TRANSFER
    }
    
    public enum PaymentStatus {
        PENDING, COMPLETED, FAILED, REFUNDED
    }
    
    @PrePersist
    protected void onCreate() {
        paymentDate = LocalDateTime.now();
    }
    
    public Payment() {}
    
    public Payment(Order order, PaymentMethod method, BigDecimal amount) {
        this.order = order;
        this.method = method;
        this.amount = amount;
        this.status = PaymentStatus.PENDING;
    }
    
    public void markCompleted(String transactionId) {
        this.status = PaymentStatus.COMPLETED;
        this.transactionId = transactionId;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    
    public PaymentMethod getMethod() { return method; }
    public void setMethod(PaymentMethod method) { this.method = method; }
    
    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    
    public LocalDateTime getPaymentDate() { return paymentDate; }
}
```

---

## Repository Classes

```java
// repository/ProductRepository.java
package com.learning.repository;

import com.learning.entity.Product;
import com.learning.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.math.BigDecimal;
import java.util.List;

public class ProductRepository {
    
    public void save(Product product) {
        EntityManager em = JpaUtil.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(product);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
    
    public Product findById(Long id) {
        EntityManager em = JpaUtil.createEntityManager();
        try {
            return em.find(Product.class, id);
        } finally {
            em.close();
        }
    }
    
    public Product findBySku(String sku) {
        EntityManager em = JpaUtil.createEntityManager();
        try {
            TypedQuery<Product> query = em.createQuery(
                "SELECT p FROM Product p WHERE p.sku = :sku", Product.class);
            query.setParameter("sku", sku);
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        } finally {
            em.close();
        }
    }
    
    public List<Product> findAllActive() {
        EntityManager em = JpaUtil.createEntityManager();
        try {
            TypedQuery<Product> query = em.createNamedQuery("Product.findAllActive", Product.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<Product> findByCategory(Long categoryId) {
        EntityManager em = JpaUtil.createEntityManager();
        try {
            TypedQuery<Product> query = em.createNamedQuery("Product.findByCategory", Product.class);
            query.setParameter("categoryId", categoryId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<Product> findByPriceRange(BigDecimal min, BigDecimal max) {
        EntityManager em = JpaUtil.createEntityManager();
        try {
            TypedQuery<Product> query = em.createNamedQuery("Product.findByPriceRange", Product.class);
            query.setParameter("minPrice", min);
            query.setParameter("maxPrice", max);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<Product> findTopRated(int limit) {
        EntityManager em = JpaUtil.createEntityManager();
        try {
            TypedQuery<Product> query = em.createNamedQuery("Product.findTopRated", Product.class);
            query.setMaxResults(limit);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public void update(Product product) {
        EntityManager em = JpaUtil.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(product);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
    
    public void delete(Long id) {
        EntityManager em = JpaUtil.createEntityManager();
        try {
            em.getTransaction().begin();
            Product product = em.find(Product.class, id);
            if (product != null) {
                em.remove(product);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
```

```java
// repository/OrderRepository.java
package com.learning.repository;

import com.learning.entity.Order;
import com.learning.entity.Order.OrderStatus;
import com.learning.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

public class OrderRepository {
    
    public void save(Order order) {
        EntityManager em = JpaUtil.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(order);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
    
    public Order findById(Long id) {
        EntityManager em = JpaUtil.createEntityManager();
        try {
            return em.find(Order.class, id);
        } finally {
            em.close();
        }
    }
    
    public Order findByOrderNumber(String orderNumber) {
        EntityManager em = JpaUtil.createEntityManager();
        try {
            TypedQuery<Order> query = em.createQuery(
                "SELECT o FROM Order o WHERE o.orderNumber = :orderNumber", Order.class);
            query.setParameter("orderNumber", orderNumber);
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        } finally {
            em.close();
        }
    }
    
    public List<Order> findByCustomer(Long customerId) {
        EntityManager em = JpaUtil.createEntityManager();
        try {
            TypedQuery<Order> query = em.createNamedQuery("Order.findByCustomer", Order.class);
            query.setParameter("customerId", customerId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<Order> findByStatus(OrderStatus status) {
        EntityManager em = JpaUtil.createEntityManager();
        try {
            TypedQuery<Order> query = em.createNamedQuery("Order.findByStatus", Order.class);
            query.setParameter("status", status);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<Order> findByDateRange(LocalDateTime start, LocalDateTime end) {
        EntityManager em = JpaUtil.createEntityManager();
        try {
            TypedQuery<Order> query = em.createNamedQuery("Order.findByDateRange", Order.class);
            query.setParameter("startDate", start);
            query.setParameter("endDate", end);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public long countByStatus(OrderStatus status) {
        EntityManager em = JpaUtil.createEntityManager();
        try {
            TypedQuery<Long> query = em.createNamedQuery("Order.countByStatus", Long.class);
            query.setParameter("status", status);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }
    
    public void update(Order order) {
        EntityManager em = JpaUtil.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(order);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
```

---

## Service Classes

```java
// service/OrderService.java
package com.learning.service;

import com.learning.entity.*;
import com.learning.entity.Order.OrderStatus;
import com.learning.entity.Payment.PaymentMethod;
import com.learning.entity.Payment.PaymentStatus;
import com.learning.repository.OrderRepository;
import com.learning.repository.ProductRepository;
import com.learning.util.JpaUtil;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;

public class OrderService {
    
    private final OrderRepository orderRepo;
    private final ProductRepository productRepo;
    
    public OrderService() {
        this.orderRepo = new OrderRepository();
        this.productRepo = new ProductRepository();
    }
    
    public Order createOrder(Customer customer, List<OrderItemData> items, String shippingAddress) {
        EntityManager em = JpaUtil.createEntityManager();
        
        try {
            em.getTransaction().begin();
            
            Order order = new Order(customer, shippingAddress);
            em.persist(order);
            
            for (OrderItemData itemData : items) {
                Product product = em.find(Product.class, itemData.productId);
                if (product == null) {
                    throw new IllegalArgumentException("Product not found: " + itemData.productId);
                }
                
                Inventory inventory = product.getInventory();
                if (inventory.getAvailableQuantity() < itemData.quantity) {
                    throw new IllegalStateException("Insufficient inventory for product: " + product.getName());
                }
                
                inventory.setReservedQuantity(inventory.getReservedQuantity() + itemData.quantity);
                
                OrderItem orderItem = new OrderItem(product, itemData.quantity);
                em.persist(orderItem);
                order.addItem(orderItem);
            }
            
            order.setStatus(OrderStatus.CONFIRMED);
            
            Payment payment = new Payment(order, PaymentMethod.CREDIT_CARD, order.getTotalAmount());
            payment.markCompleted("TXN-" + System.currentTimeMillis());
            em.persist(payment);
            order.setPayment(payment);
            
            customer.setLoyaltyPoints(customer.getLoyaltyPoints() + order.getTotalAmount().intValue());
            em.merge(customer);
            
            em.getTransaction().commit();
            
            System.out.println("Order created: " + order.getOrderNumber() + " for $" + order.getTotalAmount());
            return order;
            
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Failed to create order", e);
        } finally {
            em.close();
        }
    }
    
    public void shipOrder(Long orderId) {
        EntityManager em = JpaUtil.createEntityManager();
        
        try {
            em.getTransaction().begin();
            
            Order order = em.find(Order.class, orderId);
            if (order == null) {
                throw new IllegalArgumentException("Order not found");
            }
            
            if (order.getStatus() != OrderStatus.CONFIRMED && order.getStatus() != OrderStatus.PROCESSING) {
                throw new IllegalStateException("Order cannot be shipped in current status");
            }
            
            for (OrderItem item : order.getItems()) {
                Inventory inventory = item.getProduct().getInventory();
                inventory.setReservedQuantity(inventory.getReservedQuantity() - item.getQuantity());
                inventory.setQuantity(inventory.getQuantity() - item.getQuantity());
            }
            
            order.setStatus(OrderStatus.SHIPPED);
            order.setShippingDate(java.time.LocalDateTime.now());
            
            em.getTransaction().commit();
            System.out.println("Order shipped: " + order.getOrderNumber());
            
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Failed to ship order", e);
        } finally {
            em.close();
        }
    }
    
    public void cancelOrder(Long orderId) {
        EntityManager em = JpaUtil.createEntityManager();
        
        try {
            em.getTransaction().begin();
            
            Order order = em.find(Order.class, orderId);
            if (order == null) {
                throw new IllegalArgumentException("Order not found");
            }
            
            if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED) {
                throw new IllegalStateException("Order cannot be cancelled");
            }
            
            for (OrderItem item : order.getItems()) {
                Inventory inventory = item.getProduct().getInventory();
                inventory.setReservedQuantity(inventory.getReservedQuantity() - item.getQuantity());
            }
            
            order.setStatus(OrderStatus.CANCELLED);
            
            em.getTransaction().commit();
            System.out.println("Order cancelled: " + order.getOrderNumber());
            
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Failed to cancel order", e);
        } finally {
            em.close();
        }
    }
    
    public static class OrderItemData {
        public Long productId;
        public Integer quantity;
        
        public OrderItemData(Long productId, Integer quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }
    }
}
```

```java
// service/InventoryService.java
package com.learning.service;

import com.learning.entity.Inventory;
import com.learning.entity.Product;
import com.learning.util.JpaUtil;
import jakarta.persistence.EntityManager;
import java.util.List;

public class InventoryService {
    
    public void updateInventory(Long productId, Integer quantity) {
        EntityManager em = JpaUtil.createEntityManager();
        
        try {
            em.getTransaction().begin();
            
            Product product = em.find(Product.class, productId);
            if (product == null) {
                throw new IllegalArgumentException("Product not found");
            }
            
            Inventory inventory = product.getInventory();
            if (inventory == null) {
                inventory = new Inventory(product, quantity);
                em.persist(inventory);
            } else {
                inventory.setQuantity(quantity);
            }
            
            em.getTransaction().commit();
            System.out.println("Inventory updated for product: " + product.getName());
            
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Failed to update inventory", e);
        } finally {
            em.close();
        }
    }
    
    public void reserveInventory(Long productId, Integer quantity) {
        EntityManager em = JpaUtil.createEntityManager();
        
        try {
            em.getTransaction().begin();
            
            Product product = em.find(Product.class, productId);
            Inventory inventory = product.getInventory();
            
            if (inventory.getAvailableQuantity() < quantity) {
                throw new IllegalStateException("Insufficient inventory");
            }
            
            inventory.setReservedQuantity(inventory.getReservedQuantity() + quantity);
            
            em.getTransaction().commit();
            System.out.println("Reserved " + quantity + " units of " + product.getName());
            
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
    
    public void releaseInventory(Long productId, Integer quantity) {
        EntityManager em = JpaUtil.createEntityManager();
        
        try {
            em.getTransaction().begin();
            
            Product product = em.find(Product.class, productId);
            Inventory inventory = product.getInventory();
            
            inventory.setReservedQuantity(
                Math.max(0, inventory.getReservedQuantity() - quantity));
            
            em.getTransaction().commit();
            
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
    
    public void checkLowStock() {
        EntityManager em = JpaUtil.createEntityManager();
        
        try {
            List<Product> products = em.createQuery(
                "SELECT p FROM Product p WHERE p.inventory.quantity <= p.inventory.reorderLevel", 
                Product.class).getResultList();
            
            System.out.println("\n=== Low Stock Products ===");
            products.forEach(p -> System.out.println(p.getName() + 
                ": " + p.getInventory().getQuantity() + " units"));
            
        } finally {
            em.close();
        }
    }
}
```

```java
// service/AnalyticsService.java
package com.learning.service;

import com.learning.entity.Order;
import com.learning.entity.Order.OrderStatus;
import com.learning.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

public class AnalyticsService {
    
    public BigDecimal getTotalRevenue() {
        EntityManager em = JpaUtil.createEntityManager();
        
        try {
            Query query = em.createQuery(
                "SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = :status");
            query.setParameter("status", OrderStatus.DELIVERED);
            BigDecimal result = (BigDecimal) query.getSingleResult();
            return result != null ? result : BigDecimal.ZERO;
        } finally {
            em.close();
        }
    }
    
    public long getOrderCountByStatus(OrderStatus status) {
        EntityManager em = JpaUtil.createEntityManager();
        
        try {
            Query query = em.createQuery(
                "SELECT COUNT(o) FROM Order o WHERE o.status = :status");
            query.setParameter("status", status);
            return (Long) query.getSingleResult();
        } finally {
            em.close();
        }
    }
    
    public List<Object[]> getTopSellingProducts(int limit) {
        EntityManager em = JpaUtil.createEntityManager();
        
        try {
            Query query = em.createQuery(
                "SELECT oi.product.name, SUM(oi.quantity) as totalSold " +
                "FROM OrderItem oi JOIN oi.order o " +
                "WHERE o.status = :status " +
                "GROUP BY oi.product.name " +
                "ORDER BY totalSold DESC");
            query.setParameter("status", OrderStatus.DELIVERED);
            query.setMaxResults(limit);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public BigDecimal getAverageOrderValue() {
        EntityManager em = JpaUtil.createEntityManager();
        
        try {
            Query query = em.createQuery(
                "SELECT AVG(o.totalAmount) FROM Order o WHERE o.status = :status");
            query.setParameter("status", OrderStatus.DELIVERED);
            BigDecimal result = (BigDecimal) query.getSingleResult();
            return result != null ? result : BigDecimal.ZERO;
        } finally {
            em.close();
        }
    }
    
    public void printSalesReport() {
        System.out.println("\n=== Sales Report ===");
        
        System.out.println("Total Revenue: $" + getTotalRevenue());
        System.out.println("Average Order Value: $" + getAverageOrderValue());
        
        System.out.println("\nOrders by Status:");
        for (OrderStatus status : OrderStatus.values()) {
            long count = getOrderCountByStatus(status);
            if (count > 0) {
                System.out.println("  " + status + ": " + count);
            }
        }
        
        System.out.println("\nTop 5 Selling Products:");
        List<Object[]> topProducts = getTopSellingProducts(5);
        for (Object[] row : topProducts) {
            System.out.println("  " + row[0] + ": " + row[1] + " units");
        }
    }
}
```

---

## Main Application

```java
// Main.java
package com.learning;

import com.learning.entity.*;
import com.learning.entity.Order.OrderStatus;
import com.learning.entity.Customer.CustomerTier;
import com.learning.repository.ProductRepository;
import com.learning.service.*;
import com.learning.service.OrderService.OrderItemData;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class Main {
    
    public static void main(String[] args) {
        System.out.println("=== E-Commerce Order Management System ===\n");
        
        setupInitialData();
        
        runOrderOperations();
        
        runAnalytics();
        
        JpaUtil.close();
        System.out.println("\n=== System Shutdown ===");
    }
    
    private static void setupInitialData() {
        EntityManager em = JpaUtil.createEntityManager();
        
        try {
            em.getTransaction().begin();
            
            Category electronics = new Category("Electronics", "Electronic devices and accessories");
            Category clothing = new Category("Clothing", "Apparel and fashion");
            Category books = new Category("Books", "Books and publications");
            
            em.persist(electronics);
            em.persist(clothing);
            em.persist(books);
            
            Product laptop = new Product("Laptop Pro 15", "High-performance laptop", 
                new BigDecimal("1299.99"), "LAP-001");
            laptop.setCategory(electronics);
            laptop.setRating(4.5);
            em.persist(laptop);
            
            Product phone = new Product("Smartphone X", "Latest generation smartphone",
                new BigDecimal("899.99"), "PHN-001");
            phone.setCategory(electronics);
            phone.setRating(4.7);
            em.persist(phone);
            
            Product headphones = new Product("Wireless Headphones", "Premium noise-cancelling headphones",
                new BigDecimal("299.99"), "HPH-001");
            headphones.setCategory(electronics);
            headphones.setRating(4.3);
            em.persist(headphones);
            
            Inventory inv1 = new Inventory(laptop, 50);
            Inventory inv2 = new Inventory(phone, 100);
            Inventory inv3 = new Inventory(headphones, 75);
            em.persist(inv1);
            em.persist(inv2);
            em.persist(inv3);
            
            Customer customer1 = new Customer("John", "Doe", "john@email.com");
            customer1.setTier(CustomerTier.GOLD);
            Address addr1 = new Address("123 Main St", "New York", "NY", "10001", "USA");
            customer1.setAddress(addr1);
            em.persist(customer1);
            
            Customer customer2 = new Customer("Jane", "Smith", "jane@email.com");
            customer2.setTier(CustomerTier.SILVER);
            Address addr2 = new Address("456 Oak Ave", "Los Angeles", "CA", "90001", "USA");
            customer2.setAddress(addr2);
            em.persist(customer2);
            
            em.getTransaction().commit();
            System.out.println("Initial data loaded");
            
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
    
    private static void runOrderOperations() {
        ProductRepository productRepo = new ProductRepository();
        OrderService orderService = new OrderService();
        InventoryService inventoryService = new InventoryService();
        
        List<Product> products = productRepo.findAllActive();
        System.out.println("\n=== Available Products ===");
        products.forEach(p -> System.out.println(p.getName() + " - $" + p.getPrice()));
        
        EntityManager em = JpaUtil.createEntityManager();
        Customer customer = em.find(Customer.class, 1L);
        em.close();
        
        if (customer != null) {
            List<OrderItemData> items = Arrays.asList(
                new OrderItemData(1L, 1),
                new OrderItemData(3L, 2)
            );
            
            Order order = orderService.createOrder(customer, items, "123 Main St, New York, NY 10001");
            System.out.println("Created order: " + order.getOrderNumber());
            
            orderService.shipOrder(order.getId());
            
            InventoryService invService = new InventoryService();
            invService.checkLowStock();
        }
    }
    
    private static void runAnalytics() {
        AnalyticsService analytics = new AnalyticsService();
        analytics.printSalesReport();
    }
}
```

---

## Build Instructions

```bash
cd 17-jpa-hibernate
mvn clean compile
mvn exec:java -Dexec.mainClass="com.learning.Main"
```

To enable SQL logging:
```bash
mvn exec:java -Dexec.mainClass="com.learning.Main" -Djpa.show_sql=true
```

This comprehensive project demonstrates advanced JPA/Hibernate concepts including complex entity relationships, named queries, transactional operations, batch processing, and analytical queries for a real-world e-commerce application.