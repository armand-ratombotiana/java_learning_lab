# Database Access Module - PROJECTS.md

---

# Mini-Project: JDBC Employee Management System

## Project Overview

**Duration**: 4-5 hours  
**Difficulty**: Intermediate  
**Concepts Used**: JDBC Connection, PreparedStatement, ResultSet, Connection Pooling, Transaction Management, DAO Pattern

This mini-project demonstrates core JDBC concepts through an Employee Management System with manual connection handling, prepared statements, and transaction management.

---

## Project Structure

```
18-database-access/
├── pom.xml
├── src/main/java/com/learning/
│   ├── Main.java
│   ├── dao/
│   │   ├── EmployeeDAO.java
│   │   └── DepartmentDAO.java
│   ├── model/
│   │   ├── Employee.java
│   │   └── Department.java
│   ├── util/
│   │   ├── DatabaseUtil.java
│   │   └── ConnectionPool.java
│   └── service/
│       └── EmployeeService.java
└── src/main/resources/
    └── schema.sql
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
    <artifactId>jdbc-employee-system</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>
    
    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <version>2.2.224</version>
        </dependency>
        <dependency>
            <groupId>com.zaxxer</groupId>
            <artifactId>HikariCP</artifactId>
            <version>5.1.0</version>
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

## Step 2: Model Classes

```java
// model/Employee.java
package com.learning.model;

import java.time.LocalDate;

public class Employee {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Double salary;
    private LocalDate hireDate;
    private String department;
    private String position;
    
    public Employee() {}
    
    public Employee(String firstName, String lastName, String email, 
                    Double salary, String department, String position) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.salary = salary;
        this.department = department;
        this.position = position;
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
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    @Override
    public String toString() {
        return "Employee{id=" + id + ", name='" + getFullName() + "', department='" + department + "'}";
    }
}
```

```java
// model/Department.java
package com.learning.model;

public class Department {
    private Long id;
    private String name;
    private String location;
    private Integer employeeCount;
    
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
    
    public Integer getEmployeeCount() { return employeeCount; }
    public void setEmployeeCount(Integer employeeCount) { this.employeeCount = employeeCount; }
    
    @Override
    public String toString() {
        return "Department{id=" + id + ", name='" + name + "', location='" + location + "'}";
    }
}
```

---

## Step 3: Database Utility and Connection Pool

```java
// util/DatabaseUtil.java
package com.learning.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseUtil {
    
    private static final String JDBC_URL = "jdbc:h2:mem:employeedb;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "";
    
    static {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load H2 driver", e);
        }
    }
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    }
    
    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS departments (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(100) NOT NULL UNIQUE,
                    location VARCHAR(100)
                )
            """);
            
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS employees (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    first_name VARCHAR(50) NOT NULL,
                    last_name VARCHAR(50) NOT NULL,
                    email VARCHAR(100) UNIQUE,
                    salary DECIMAL(10,2),
                    hire_date DATE,
                    department_id BIGINT,
                    position VARCHAR(50),
                    FOREIGN KEY (department_id) REFERENCES departments(id)
                )
            """);
            
            stmt.execute("INSERT INTO departments (name, location) VALUES ('IT', 'Building A')");
            stmt.execute("INSERT INTO departments (name, location) VALUES ('HR', 'Building B')");
            stmt.execute("INSERT INTO departments (name, location) VALUES ('Finance', 'Building C')");
            
            System.out.println("Database initialized");
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }
    
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Failed to close connection: " + e.getMessage());
            }
        }
    }
}
```

```java
// util/ConnectionPool.java
package com.learning.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionPool {
    
    private static final HikariDataSource dataSource;
    
    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:employeedb;DB_CLOSE_DELAY=-1");
        config.setUsername("sa");
        config.setPassword("");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setPoolName("EmployeePool");
        
        dataSource = new HikariDataSource(config);
    }
    
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
    
    public static DataSource getDataSource() {
        return dataSource;
    }
    
    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
```

---

## Step 4: DAO Classes

```java
// dao/EmployeeDAO.java
package com.learning.dao;

import com.learning.model.Employee;
import com.learning.util.ConnectionPool;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {
    
    public void save(Employee employee) throws SQLException {
        String sql = "INSERT INTO employees (first_name, last_name, email, salary, hire_date, department_id, position) " +
                     "VALUES (?, ?, ?, ?, ?, (SELECT id FROM departments WHERE name = ?), ?)";
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, employee.getFirstName());
            pstmt.setString(2, employee.getLastName());
            pstmt.setString(3, employee.getEmail());
            pstmt.setDouble(4, employee.getSalary());
            pstmt.setDate(5, Date.valueOf(employee.getHireDate()));
            pstmt.setString(6, employee.getDepartment());
            pstmt.setString(7, employee.getPosition());
            
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    employee.setId(rs.getLong(1));
                }
            }
        }
    }
    
    public Employee findById(Long id) throws SQLException {
        String sql = "SELECT e.*, d.name as dept_name FROM employees e " +
                     "LEFT JOIN departments d ON e.department_id = d.id WHERE e.id = ?";
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEmployee(rs);
                }
            }
        }
        return null;
    }
    
    public List<Employee> findAll() throws SQLException {
        String sql = "SELECT e.*, d.name as dept_name FROM employees e " +
                     "LEFT JOIN departments d ON e.department_id = d.id";
        
        List<Employee> employees = new ArrayList<>();
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                employees.add(mapResultSetToEmployee(rs));
            }
        }
        return employees;
    }
    
    public List<Employee> findByDepartment(String department) throws SQLException {
        String sql = "SELECT e.*, d.name as dept_name FROM employees e " +
                     "LEFT JOIN departments d ON e.department_id = d.id WHERE d.name = ?";
        
        List<Employee> employees = new ArrayList<>();
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, department);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    employees.add(mapResultSetToEmployee(rs));
                }
            }
        }
        return employees;
    }
    
    public List<Employee> findBySalaryRange(Double minSalary, Double maxSalary) throws SQLException {
        String sql = "SELECT e.*, d.name as dept_name FROM employees e " +
                     "LEFT JOIN departments d ON e.department_id = d.id WHERE e.salary BETWEEN ? AND ?";
        
        List<Employee> employees = new ArrayList<>();
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, minSalary);
            pstmt.setDouble(2, maxSalary);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    employees.add(mapResultSetToEmployee(rs));
                }
            }
        }
        return employees;
    }
    
    public void update(Employee employee) throws SQLException {
        String sql = "UPDATE employees SET first_name = ?, last_name = ?, email = ?, " +
                     "salary = ?, position = ?, department_id = (SELECT id FROM departments WHERE name = ?) " +
                     "WHERE id = ?";
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, employee.getFirstName());
            pstmt.setString(2, employee.getLastName());
            pstmt.setString(3, employee.getEmail());
            pstmt.setDouble(4, employee.getSalary());
            pstmt.setString(5, employee.getPosition());
            pstmt.setString(6, employee.getDepartment());
            pstmt.setLong(7, employee.getId());
            
            pstmt.executeUpdate();
        }
    }
    
    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM employees WHERE id = ?";
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        }
    }
    
    public List<Employee> searchByName(String searchTerm) throws SQLException {
        String sql = "SELECT e.*, d.name as dept_name FROM employees e " +
                     "LEFT JOIN departments d ON e.department_id = d.id " +
                     "WHERE e.first_name LIKE ? OR e.last_name LIKE ?";
        
        List<Employee> employees = new ArrayList<>();
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String likePattern = "%" + searchTerm + "%";
            pstmt.setString(1, likePattern);
            pstmt.setString(2, likePattern);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    employees.add(mapResultSetToEmployee(rs));
                }
            }
        }
        return employees;
    }
    
    public double getAverageSalary() throws SQLException {
        String sql = "SELECT AVG(salary) FROM employees";
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        return 0.0;
    }
    
    public double getAverageSalaryByDepartment(String department) throws SQLException {
        String sql = "SELECT AVG(e.salary) FROM employees e " +
                     "JOIN departments d ON e.department_id = d.id WHERE d.name = ?";
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, department);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        }
        return 0.0;
    }
    
    private Employee mapResultSetToEmployee(ResultSet rs) throws SQLException {
        Employee employee = new Employee();
        employee.setId(rs.getLong("id"));
        employee.setFirstName(rs.getString("first_name"));
        employee.setLastName(rs.getString("last_name"));
        employee.setEmail(rs.getString("email"));
        
        double salary = rs.getDouble("salary");
        if (!rs.wasNull()) {
            employee.setSalary(salary);
        }
        
        Date hireDate = rs.getDate("hire_date");
        if (hireDate != null) {
            employee.setHireDate(hireDate.toLocalDate());
        }
        
        employee.setDepartment(rs.getString("dept_name"));
        employee.setPosition(rs.getString("position"));
        
        return employee;
    }
}
```

```java
// dao/DepartmentDAO.java
package com.learning.dao;

import com.learning.model.Department;
import com.learning.util.ConnectionPool;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDAO {
    
    public void save(Department department) throws SQLException {
        String sql = "INSERT INTO departments (name, location) VALUES (?, ?)";
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, department.getName());
            pstmt.setString(2, department.getLocation());
            
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    department.setId(rs.getLong(1));
                }
            }
        }
    }
    
    public Department findById(Long id) throws SQLException {
        String sql = "SELECT * FROM departments WHERE id = ?";
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDepartment(rs);
                }
            }
        }
        return null;
    }
    
    public Department findByName(String name) throws SQLException {
        String sql = "SELECT * FROM departments WHERE name = ?";
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, name);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDepartment(rs);
                }
            }
        }
        return null;
    }
    
    public List<Department> findAll() throws SQLException {
        String sql = "SELECT d.*, COUNT(e.id) as emp_count FROM departments d " +
                     "LEFT JOIN employees e ON d.id = e.department_id GROUP BY d.id";
        
        List<Department> departments = new ArrayList<>();
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Department dept = mapResultSetToDepartment(rs);
                dept.setEmployeeCount(rs.getInt("emp_count"));
                departments.add(dept);
            }
        }
        return departments;
    }
    
    public void update(Department department) throws SQLException {
        String sql = "UPDATE departments SET name = ?, location = ? WHERE id = ?";
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, department.getName());
            pstmt.setString(2, department.getLocation());
            pstmt.setLong(3, department.getId());
            
            pstmt.executeUpdate();
        }
    }
    
    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM departments WHERE id = ?";
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        }
    }
    
    private Department mapResultSetToDepartment(ResultSet rs) throws SQLException {
        Department department = new Department();
        department.setId(rs.getLong("id"));
        department.setName(rs.getString("name"));
        department.setLocation(rs.getString("location"));
        return department;
    }
}
```

---

## Step 5: Service Layer

```java
// service/EmployeeService.java
package com.learning.service;

import com.learning.dao.EmployeeDAO;
import com.learning.model.Employee;
import java.sql.SQLException;
import java.util.List;

public class EmployeeService {
    
    private final EmployeeDAO employeeDAO;
    
    public EmployeeService() {
        this.employeeDAO = new EmployeeDAO();
    }
    
    public void createEmployee(Employee employee) {
        try {
            employeeDAO.save(employee);
            System.out.println("Employee created: " + employee.getFullName());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create employee", e);
        }
    }
    
    public Employee getEmployee(Long id) {
        try {
            return employeeDAO.findById(id);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find employee", e);
        }
    }
    
    public List<Employee> getAllEmployees() {
        try {
            return employeeDAO.findAll();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve employees", e);
        }
    }
    
    public List<Employee> getEmployeesByDepartment(String department) {
        try {
            return employeeDAO.findByDepartment(department);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve employees", e);
        }
    }
    
    public List<Employee> searchEmployees(String searchTerm) {
        try {
            return employeeDAO.searchByName(searchTerm);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to search employees", e);
        }
    }
    
    public void updateEmployee(Employee employee) {
        try {
            employeeDAO.update(employee);
            System.out.println("Employee updated: " + employee.getFullName());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update employee", e);
        }
    }
    
    public void deleteEmployee(Long id) {
        try {
            employeeDAO.delete(id);
            System.out.println("Employee deleted with ID: " + id);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete employee", e);
        }
    }
    
    public void printDepartmentStatistics() {
        try {
            List<Employee> employees = employeeDAO.findAll();
            
            System.out.println("\n=== Department Statistics ===");
            
            employees.stream()
                .collect(java.util.stream.Collectors.groupingBy(Employee::getDepartment))
                .forEach((dept, emps) -> {
                    double avgSalary = emps.stream()
                        .mapToDouble(e -> e.getSalary() != null ? e.getSalary() : 0.0)
                        .average()
                        .orElse(0.0);
                    
                    System.out.println(dept + ": " + emps.size() + " employees, avg salary: $" + 
                        String.format("%.2f", avgSalary));
                });
            
            double overallAvg = employeeDAO.getAverageSalary();
            System.out.println("Overall Average Salary: $" + String.format("%.2f", overallAvg));
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to calculate statistics", e);
        }
    }
    
    public void transferEmployee(Long employeeId, String newDepartment) {
        try {
            Employee employee = employeeDAO.findById(employeeId);
            if (employee != null) {
                employee.setDepartment(newDepartment);
                employeeDAO.update(employee);
                System.out.println("Employee " + employee.getFullName() + " transferred to " + newDepartment);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to transfer employee", e);
        }
    }
}
```

---

## Step 6: Main Application

```java
// Main.java
package com.learning;

import com.learning.dao.DepartmentDAO;
import com.learning.model.Department;
import com.learning.model.Employee;
import com.learning.service.EmployeeService;
import com.learning.util.DatabaseUtil;
import java.util.List;

public class Main {
    
    public static void main(String[] args) {
        System.out.println("=== JDBC Employee Management System ===\n");
        
        DatabaseUtil.initializeDatabase();
        
        EmployeeService employeeService = new EmployeeService();
        
        Employee emp1 = new Employee("John", "Doe", "john.doe@company.com", 75000.0, "IT", "Developer");
        Employee emp2 = new Employee("Jane", "Smith", "jane.smith@company.com", 80000.0, "IT", "Senior Developer");
        Employee emp3 = new Employee("Bob", "Johnson", "bob.johnson@company.com", 65000.0, "HR", "HR Specialist");
        Employee emp4 = new Employee("Alice", "Williams", "alice.williams@company.com", 85000.0, "Finance", "Accountant");
        
        employeeService.createEmployee(emp1);
        employeeService.createEmployee(emp2);
        employeeService.createEmployee(emp3);
        employeeService.createEmployee(emp4);
        
        System.out.println("\n=== All Employees ===");
        List<Employee> allEmployees = employeeService.getAllEmployees();
        allEmployees.forEach(e -> System.out.println(e.getFullName() + " - " + e.getDepartment()));
        
        System.out.println("\n=== IT Department Employees ===");
        List<Employee> itEmployees = employeeService.getEmployeesByDepartment("IT");
        itEmployees.forEach(System.out::println);
        
        System.out.println("\n=== Search for 'John' ===");
        List<Employee> searchResults = employeeService.searchEmployees("John");
        searchResults.forEach(System.out::println);
        
        emp1.setSalary(80000.0);
        employeeService.updateEmployee(emp1);
        
        employeeService.printDepartmentStatistics();
        
        DepartmentDAO deptDAO = new DepartmentDAO();
        System.out.println("\n=== All Departments ===");
        try {
            List<Department> departments = deptDAO.findAll();
            departments.forEach(d -> System.out.println(d.getName() + " - " + d.getEmployeeCount() + " employees"));
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        
        com.learning.util.ConnectionPool.shutdown();
        System.out.println("\n=== System Shutdown ===");
    }
}
```

---

## Build Instructions

```bash
cd 18-database-access
mvn clean compile
mvn exec:java -Dexec.mainClass="com.learning.Main"
```

---

# Real-World Project: E-Commerce Order Processing System

## Project Overview

**Duration**: 12+ hours  
**Difficulty**: Advanced  
**Concepts Used**: JDBC Template, Connection Pooling, Transaction Management, Batch Operations, Stored Procedures, ResultSetExtractor, RowMapper

This comprehensive project implements a complete e-commerce order processing system with JDBC Template, transaction management, and optimized batch operations.

---

## Project Structure

```
18-database-access/
├── pom.xml
├── src/main/java/com/learning/
│   ├── Main.java
│   ├── config/
│   │   └── AppConfig.java
│   ├── dao/
│   │   ├── CustomerDAO.java
│   │   ├── ProductDAO.java
│   │   ├── OrderDAO.java
│   │   └── InventoryDAO.java
│   ├── model/
│   │   ├── Customer.java
│   │   ├── Product.java
│   │   ├── Order.java
│   │   ├── OrderItem.java
│   │   └── Inventory.java
│   ├── service/
│   │   ├── OrderProcessingService.java
│   │   └── InventoryService.java
│   └── util/
│       ├── DatabaseUtil.java
│       └── ConnectionPool.java
└── src/main/resources/
    └── schema.sql
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
    <artifactId>jdbc-ecommerce</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>
    
    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <version>2.2.224</version>
        </dependency>
        <dependency>
            <groupId>com.zaxxer</groupId>
            <artifactId>HikariCP</artifactId>
            <version>5.1.0</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-jdbc</artifactId>
            <version>6.1.0</version>
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
            </plugin>
        </plugins>
    </build>
</project>
```

---

## Model Classes

```java
// model/Customer.java
package com.learning.model;

import java.time.LocalDateTime;

public class Customer {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private Integer loyaltyPoints;
    private String tier;
    private LocalDateTime createdAt;
    
    public Customer() {}
    
    public Customer(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.loyaltyPoints = 0;
        this.tier = "BRONZE";
    }
    
    public String getFullName() { return firstName + " " + lastName; }
    
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
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    
    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }
    
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    
    public Integer getLoyaltyPoints() { return loyaltyPoints; }
    public void setLoyaltyPoints(Integer loyaltyPoints) { this.loyaltyPoints = loyaltyPoints; }
    
    public String getTier() { return tier; }
    public void setTier(String tier) { this.tier = tier; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
}
```

```java
// model/Product.java
package com.learning.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Product {
    private Long id;
    private String name;
    private String description;
    private String sku;
    private BigDecimal price;
    private Long categoryId;
    private String categoryName;
    private Boolean active;
    private Integer stockQuantity;
    private Double rating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public Product() {}
    
    public Product(String name, String description, String sku, BigDecimal price) {
        this.name = name;
        this.description = description;
        this.sku = sku;
        this.price = price;
        this.active = true;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    
    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
    
    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
```

```java
// model/Order.java
package com.learning.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Order {
    private Long id;
    private String orderNumber;
    private Long customerId;
    private String customerName;
    private List<OrderItem> items;
    private BigDecimal totalAmount;
    private String status;
    private String shippingAddress;
    private String billingAddress;
    private LocalDateTime orderDate;
    private LocalDateTime shippingDate;
    private LocalDateTime deliveryDate;
    
    public Order() {}
    
    public Order(Long customerId, String shippingAddress) {
        this.customerId = customerId;
        this.shippingAddress = shippingAddress;
        this.billingAddress = shippingAddress;
        this.status = "PENDING";
        this.totalAmount = BigDecimal.ZERO;
        this.orderDate = LocalDateTime.now();
    }
    
    public void addItem(OrderItem item) {
        items.add(item);
        totalAmount = totalAmount.add(item.getUnitPrice().multiply(new BigDecimal(item.getQuantity())));
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
    
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    
    public String getBillingAddress() { return billingAddress; }
    public void setBillingAddress(String billingAddress) { this.billingAddress = billingAddress; }
    
    public LocalDateTime getOrderDate() { return orderDate; }
    public LocalDateTime getShippingDate() { return shippingDate; }
    public LocalDateTime getDeliveryDate() { return deliveryDate; }
}
```

```java
// model/OrderItem.java
package com.learning.model;

import java.math.BigDecimal;

public class OrderItem {
    private Long id;
    private Long orderId;
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
    
    public OrderItem() {}
    
    public OrderItem(Long productId, Integer quantity, BigDecimal unitPrice) {
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = unitPrice.multiply(new BigDecimal(quantity));
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
}
```

---

## Config Class

```java
// config/AppConfig.java
package com.learning.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import javax.sql.DataSource;

public class AppConfig {
    
    private static final HikariDataSource dataSource;
    private static final JdbcTemplate jdbcTemplate;
    private static final PlatformTransactionManager transactionManager;
    
    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:ecommerce;DB_CLOSE_DELAY=-1");
        config.setUsername("sa");
        config.setPassword("");
        config.setMaximumPoolSize(20);
        config.setMinimumIdle(5);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setPoolName("EcommercePool");
        
        dataSource = new HikariDataSource(config);
        
        jdbcTemplate = new JdbcTemplate(dataSource);
        
        transactionManager = new DataSourceTransactionManager(dataSource);
        
        initializeSchema();
    }
    
    private static void initializeSchema() {
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS categories (" +
            "id BIGINT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(100) NOT NULL UNIQUE, " +
            "description VARCHAR(500))");
        
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS products (" +
            "id BIGINT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(200) NOT NULL, " +
            "description VARCHAR(1000), sku VARCHAR(50) UNIQUE, price DECIMAL(10,2) NOT NULL, " +
            "category_id BIGINT, active BOOLEAN DEFAULT TRUE, stock_quantity INT DEFAULT 0, " +
            "rating DOUBLE, created_at TIMESTAMP, updated_at TIMESTAMP, " +
            "FOREIGN KEY (category_id) REFERENCES categories(id))");
        
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS customers (" +
            "id BIGINT AUTO_INCREMENT PRIMARY KEY, first_name VARCHAR(50) NOT NULL, " +
            "last_name VARCHAR(50) NOT NULL, email VARCHAR(100) UNIQUE NOT NULL, " +
            "phone VARCHAR(20), address VARCHAR(200), city VARCHAR(50), state VARCHAR(50), " +
            "zip_code VARCHAR(20), country VARCHAR(50), loyalty_points INT DEFAULT 0, " +
            "tier VARCHAR(20) DEFAULT 'BRONZE', created_at TIMESTAMP)");
        
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS orders (" +
            "id BIGINT AUTO_INCREMENT PRIMARY KEY, order_number VARCHAR(50) UNIQUE NOT NULL, " +
            "customer_id BIGINT NOT NULL, total_amount DECIMAL(10,2), status VARCHAR(20), " +
            "shipping_address VARCHAR(500), billing_address VARCHAR(500), order_date TIMESTAMP, " +
            "shipping_date TIMESTAMP, delivery_date TIMESTAMP, " +
            "FOREIGN KEY (customer_id) REFERENCES customers(id))");
        
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS order_items (" +
            "id BIGINT AUTO_INCREMENT PRIMARY KEY, order_id BIGINT NOT NULL, " +
            "product_id BIGINT NOT NULL, quantity INT NOT NULL, unit_price DECIMAL(10,2) NOT NULL, " +
            "subtotal DECIMAL(10,2), FOREIGN KEY (order_id) REFERENCES orders(id), " +
            "FOREIGN KEY (product_id) REFERENCES products(id))");
        
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS inventory (" +
            "id BIGINT AUTO_INCREMENT PRIMARY KEY, product_id BIGINT UNIQUE NOT NULL, " +
            "quantity INT DEFAULT 0, reserved_quantity INT DEFAULT 0, " +
            "reorder_level INT DEFAULT 10, reorder_quantity INT DEFAULT 50, " +
            "FOREIGN KEY (product_id) REFERENCES products(id))");
        
        initializeData();
    }
    
    private static void initializeData() {
        try {
            jdbcTemplate.execute("INSERT INTO categories (name, description) VALUES ('Electronics', 'Electronic devices')");
            jdbcTemplate.execute("INSERT INTO categories (name, description) VALUES ('Clothing', 'Apparel and fashion')");
            jdbcTemplate.execute("INSERT INTO categories (name, description) VALUES ('Books', 'Books and publications')");
            
            jdbcTemplate.execute("INSERT INTO products (name, description, sku, price, category_id, active, stock_quantity, rating, created_at) " +
                "VALUES ('Laptop Pro', 'High-performance laptop', 'LAP-001', 1299.99, 1, TRUE, 50, 4.5, CURRENT_TIMESTAMP)");
            jdbcTemplate.execute("INSERT INTO products (name, description, sku, price, category_id, active, stock_quantity, rating, created_at) " +
                "VALUES ('Smartphone X', 'Latest smartphone', 'PHN-001', 899.99, 1, TRUE, 100, 4.7, CURRENT_TIMESTAMP)");
            jdbcTemplate.execute("INSERT INTO products (name, description, sku, price, category_id, active, stock_quantity, rating, created_at) " +
                "VALUES ('Wireless Headphones', 'Premium headphones', 'HPH-001', 299.99, 1, TRUE, 75, 4.3, CURRENT_TIMESTAMP)");
            
            jdbcTemplate.execute("INSERT INTO customers (first_name, last_name, email, city, loyalty_points, tier, created_at) " +
                "VALUES ('John', 'Doe', 'john@email.com', 'New York', 500, 'GOLD', CURRENT_TIMESTAMP)");
            jdbcTemplate.execute("INSERT INTO customers (first_name, last_name, email, city, loyalty_points, tier, created_at) " +
                "VALUES ('Jane', 'Smith', 'jane@email.com', 'Los Angeles', 300, 'SILVER', CURRENT_TIMESTAMP)");
            
            System.out.println("Database initialized");
        } catch (Exception e) {
            System.out.println("Data may already exist: " + e.getMessage());
        }
    }
    
    public static DataSource getDataSource() { return dataSource; }
    public static JdbcTemplate getJdbcTemplate() { return jdbcTemplate; }
    public static PlatformTransactionManager getTransactionManager() { return transactionManager; }
}
```

---

## DAO Classes

```java
// dao/CustomerDAO.java
package com.learning.dao;

import com.learning.config.AppConfig;
import com.learning.model.Customer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CustomerDAO {
    
    private final JdbcTemplate jdbcTemplate;
    
    public CustomerDAO() {
        this.jdbcTemplate = AppConfig.getJdbcTemplate();
    }
    
    private final RowMapper<Customer> customerMapper = (rs, rowNum) -> {
        Customer customer = new Customer();
        customer.setId(rs.getLong("id"));
        customer.setFirstName(rs.getString("first_name"));
        customer.setLastName(rs.getString("last_name"));
        customer.setEmail(rs.getString("email"));
        customer.setPhone(rs.getString("phone"));
        customer.setAddress(rs.getString("address"));
        customer.setCity(rs.getString("city"));
        customer.setState(rs.getString("state"));
        customer.setZipCode(rs.getString("zip_code"));
        customer.setCountry(rs.getString("country"));
        customer.setLoyaltyPoints(rs.getInt("loyalty_points"));
        customer.setTier(rs.getString("tier"));
        customer.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return customer;
    };
    
    public Long save(Customer customer) {
        String sql = "INSERT INTO customers (first_name, last_name, email, phone, address, " +
                     "city, state, zip_code, country, loyalty_points, tier, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, customer.getFirstName());
            ps.setString(2, customer.getLastName());
            ps.setString(3, customer.getEmail());
            ps.setString(4, customer.getPhone());
            ps.setString(5, customer.getAddress());
            ps.setString(6, customer.getCity());
            ps.setString(7, customer.getState());
            ps.setString(8, customer.getZipCode());
            ps.setString(9, customer.getCountry());
            ps.setInt(10, customer.getLoyaltyPoints() != null ? customer.getLoyaltyPoints() : 0);
            ps.setString(11, customer.getTier() != null ? customer.getTier() : "BRONZE");
            return ps;
        }, keyHolder);
        
        return keyHolder.getKey().longValue();
    }
    
    public Customer findById(Long id) {
        String sql = "SELECT * FROM customers WHERE id = ?";
        List<Customer> results = jdbcTemplate.query(sql, customerMapper, id);
        return results.isEmpty() ? null : results.get(0);
    }
    
    public Customer findByEmail(String email) {
        String sql = "SELECT * FROM customers WHERE email = ?";
        List<Customer> results = jdbcTemplate.query(sql, customerMapper, email);
        return results.isEmpty() ? null : results.get(0);
    }
    
    public List<Customer> findAll() {
        String sql = "SELECT * FROM customers ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, customerMapper);
    }
    
    public List<Customer> findByTier(String tier) {
        String sql = "SELECT * FROM customers WHERE tier = ?";
        return jdbcTemplate.query(sql, customerMapper, tier);
    }
    
    public void update(Customer customer) {
        String sql = "UPDATE customers SET first_name = ?, last_name = ?, email = ?, " +
                     "phone = ?, address = ?, city = ?, state = ?, zip_code = ?, " +
                     "country = ?, loyalty_points = ?, tier = ? WHERE id = ?";
        
        jdbcTemplate.update(sql, 
            customer.getFirstName(), customer.getLastName(), customer.getEmail(),
            customer.getPhone(), customer.getAddress(), customer.getCity(),
            customer.getState(), customer.getZipCode(), customer.getCountry(),
            customer.getLoyaltyPoints(), customer.getTier(), customer.getId());
    }
    
    public void addLoyaltyPoints(Long customerId, int points) {
        String sql = "UPDATE customers SET loyalty_points = loyalty_points + ? WHERE id = ?";
        jdbcTemplate.update(sql, points, customerId);
        
        updateTier(customerId);
    }
    
    private void updateTier(Long customerId) {
        String sql = "SELECT loyalty_points FROM customers WHERE id = ?";
        Integer points = jdbcTemplate.queryForObject(sql, Integer.class, customerId);
        
        String tier = "BRONZE";
        if (points != null) {
            if (points >= 1000) tier = "PLATINUM";
            else if (points >= 500) tier = "GOLD";
            else if (points >= 200) tier = "SILVER";
        }
        
        String updateSql = "UPDATE customers SET tier = ? WHERE id = ?";
        jdbcTemplate.update(updateSql, tier, customerId);
    }
    
    public void delete(Long id) {
        String sql = "DELETE FROM customers WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
```

```java
// dao/ProductDAO.java
package com.learning.dao;

import com.learning.config.AppConfig;
import com.learning.model.Product;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.math.BigDecimal;
import java.util.List;

public class ProductDAO {
    
    private final JdbcTemplate jdbcTemplate;
    
    public ProductDAO() {
        this.jdbcTemplate = AppConfig.getJdbcTemplate();
    }
    
    private final RowMapper<Product> productMapper = (rs, rowNum) -> {
        Product product = new Product();
        product.setId(rs.getLong("id"));
        product.setName(rs.getString("name"));
        product.setDescription(rs.getString("description"));
        product.setSku(rs.getString("sku"));
        product.setPrice(rs.getBigDecimal("price"));
        product.setCategoryId(rs.getLong("category_id"));
        product.setActive(rs.getBoolean("active"));
        product.setStockQuantity(rs.getInt("stock_quantity"));
        product.setRating(rs.getDouble("rating"));
        
        if (rs.getTimestamp("created_at") != null) {
            product.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        if (rs.getTimestamp("updated_at") != null) {
            product.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        }
        
        return product;
    };
    
    public Long save(Product product) {
        String sql = "INSERT INTO products (name, description, sku, price, category_id, " +
                     "active, stock_quantity, rating, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, product.getName());
            ps.setString(2, product.getDescription());
            ps.setString(3, product.getSku());
            ps.setBigDecimal(4, product.getPrice());
            ps.setLong(5, product.getCategoryId());
            ps.setBoolean(6, product.getActive() != null ? product.getActive() : true);
            ps.setInt(7, product.getStockQuantity() != null ? product.getStockQuantity() : 0);
            ps.setDouble(8, product.getRating() != null ? product.getRating() : 0.0);
            return ps;
        }, keyHolder);
        
        return keyHolder.getKey().longValue();
    }
    
    public Product findById(Long id) {
        String sql = "SELECT p.*, c.name as category_name FROM products p " +
                     "LEFT JOIN categories c ON p.category_id = c.id WHERE p.id = ?";
        
        List<Product> results = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Product product = productMapper.mapRow(rs, rowNum);
            product.setCategoryName(rs.getString("category_name"));
            return product;
        }, id);
        
        return results.isEmpty() ? null : results.get(0);
    }
    
    public Product findBySku(String sku) {
        String sql = "SELECT * FROM products WHERE sku = ?";
        List<Product> results = jdbcTemplate.query(sql, productMapper, sku);
        return results.isEmpty() ? null : results.get(0);
    }
    
    public List<Product> findAll() {
        String sql = "SELECT p.*, c.name as category_name FROM products p " +
                     "LEFT JOIN categories c ON p.category_id = c.id WHERE p.active = TRUE";
        
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Product product = productMapper.mapRow(rs, rowNum);
            product.setCategoryName(rs.getString("category_name"));
            return product;
        });
    }
    
    public List<Product> findByCategory(Long categoryId) {
        String sql = "SELECT * FROM products WHERE category_id = ? AND active = TRUE";
        return jdbcTemplate.query(sql, productMapper, categoryId);
    }
    
    public List<Product> findByPriceRange(BigDecimal min, BigDecimal max) {
        String sql = "SELECT * FROM products WHERE price BETWEEN ? AND ? AND active = TRUE";
        return jdbcTemplate.query(sql, productMapper, min, max);
    }
    
    public List<Product> findLowStock(int threshold) {
        String sql = "SELECT p.*, i.quantity as stock_quantity FROM products p " +
                     "JOIN inventory i ON p.id = i.product_id WHERE i.quantity <= ?";
        return jdbcTemplate.query(sql, productMapper, threshold);
    }
    
    public void update(Product product) {
        String sql = "UPDATE products SET name = ?, description = ?, sku = ?, price = ?, " +
                     "category_id = ?, active = ?, stock_quantity = ?, rating = ?, " +
                     "updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        
        jdbcTemplate.update(sql, 
            product.getName(), product.getDescription(), product.getSku(),
            product.getPrice(), product.getCategoryId(), product.getActive(),
            product.getStockQuantity(), product.getRating(), product.getId());
    }
    
    public void updateStock(Long productId, int quantity) {
        String sql = "UPDATE products SET stock_quantity = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        jdbcTemplate.update(sql, quantity, productId);
    }
    
    public void delete(Long id) {
        String sql = "UPDATE products SET active = FALSE WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
    
    public int getTotalProductCount() {
        String sql = "SELECT COUNT(*) FROM products WHERE active = TRUE";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null ? count : 0;
    }
}
```

```java
// dao/OrderDAO.java
package com.learning.dao;

import com.learning.config.AppConfig;
import com.learning.model.Order;
import com.learning.model.OrderItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.math.BigDecimal;
import java.util.List;

public class OrderDAO {
    
    private final JdbcTemplate jdbcTemplate;
    private final ProductDAO productDAO;
    
    public OrderDAO() {
        this.jdbcTemplate = AppConfig.getJdbcTemplate();
        this.productDAO = new ProductDAO();
    }
    
    private final RowMapper<Order> orderMapper = (rs, rowNum) -> {
        Order order = new Order();
        order.setId(rs.getLong("id"));
        order.setOrderNumber(rs.getString("order_number"));
        order.setCustomerId(rs.getLong("customer_id"));
        order.setTotalAmount(rs.getBigDecimal("total_amount"));
        order.setStatus(rs.getString("status"));
        order.setShippingAddress(rs.getString("shipping_address"));
        order.setBillingAddress(rs.getString("billing_address"));
        
        if (rs.getTimestamp("order_date") != null) {
            order.setOrderDate(rs.getTimestamp("order_date").toLocalDateTime());
        }
        if (rs.getTimestamp("shipping_date") != null) {
            order.setShippingDate(rs.getTimestamp("shipping_date").toLocalDateTime());
        }
        if (rs.getTimestamp("delivery_date") != null) {
            order.setDeliveryDate(rs.getTimestamp("delivery_date").toLocalDateTime());
        }
        
        return order;
    };
    
    private final RowMapper<OrderItem> orderItemMapper = (rs, rowNum) -> {
        OrderItem item = new OrderItem();
        item.setId(rs.getLong("id"));
        item.setOrderId(rs.getLong("order_id"));
        item.setProductId(rs.getLong("product_id"));
        item.setProductName(rs.getString("product_name"));
        item.setQuantity(rs.getInt("quantity"));
        item.setUnitPrice(rs.getBigDecimal("unit_price"));
        item.setSubtotal(rs.getBigDecimal("subtotal"));
        return item;
    };
    
    public Long save(Order order) {
        String sql = "INSERT INTO orders (order_number, customer_id, total_amount, status, " +
                     "shipping_address, billing_address, order_date) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        String orderNumber = "ORD-" + System.currentTimeMillis();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, orderNumber);
            ps.setLong(2, order.getCustomerId());
            ps.setBigDecimal(3, order.getTotalAmount());
            ps.setString(4, order.getStatus());
            ps.setString(5, order.getShippingAddress());
            ps.setString(6, order.getBillingAddress());
            return ps;
        }, keyHolder);
        
        Long orderId = keyHolder.getKey().longValue();
        
        for (OrderItem item : order.getItems()) {
            saveOrderItem(orderId, item);
        }
        
        return orderId;
    }
    
    private void saveOrderItem(Long orderId, OrderItem item) {
        String sql = "INSERT INTO order_items (order_id, product_id, quantity, unit_price, subtotal) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        jdbcTemplate.update(sql, 
            orderId, item.getProductId(), item.getQuantity(),
            item.getUnitPrice(), item.getSubtotal());
    }
    
    public Order findById(Long id) {
        String sql = "SELECT o.*, c.first_name || ' ' || c.last_name as customer_name " +
                     "FROM orders o JOIN customers c ON o.customer_id = c.id WHERE o.id = ?";
        
        List<Order> results = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Order order = orderMapper.mapRow(rs, rowNum);
            order.setCustomerName(rs.getString("customer_name"));
            return order;
        }, id);
        
        if (results.isEmpty()) return null;
        
        Order order = results.get(0);
        order.setItems(findOrderItems(order.getId()));
        
        return order;
    }
    
    public List<OrderItem> findOrderItems(Long orderId) {
        String sql = "SELECT oi.*, p.name as product_name FROM order_items oi " +
                     "JOIN products p ON oi.product_id = p.id WHERE oi.order_id = ?";
        return jdbcTemplate.query(sql, orderItemMapper, orderId);
    }
    
    public List<Order> findByCustomer(Long customerId) {
        String sql = "SELECT * FROM orders WHERE customer_id = ? ORDER BY order_date DESC";
        return jdbcTemplate.query(sql, orderMapper, customerId);
    }
    
    public List<Order> findByStatus(String status) {
        String sql = "SELECT * FROM orders WHERE status = ? ORDER BY order_date DESC";
        return jdbcTemplate.query(sql, orderMapper, status);
    }
    
    public void updateStatus(Long orderId, String status) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        jdbcTemplate.update(sql, status, orderId);
    }
    
    public void shipOrder(Long orderId) {
        String sql = "UPDATE orders SET status = 'SHIPPED', shipping_date = CURRENT_TIMESTAMP WHERE id = ?";
        jdbcTemplate.update(sql, orderId);
    }
    
    public void deliverOrder(Long orderId) {
        String sql = "UPDATE orders SET status = 'DELIVERED', delivery_date = CURRENT_TIMESTAMP WHERE id = ?";
        jdbcTemplate.update(sql, orderId);
    }
    
    public BigDecimal getTotalRevenue() {
        String sql = "SELECT SUM(total_amount) FROM orders WHERE status = 'DELIVERED'";
        BigDecimal result = jdbcTemplate.queryForObject(sql, BigDecimal.class);
        return result != null ? result : BigDecimal.ZERO;
    }
    
    public long countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM orders WHERE status = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, status);
        return count != null ? count : 0;
    }
}
```

```java
// dao/InventoryDAO.java
package com.learning.dao;

import com.learning.config.AppConfig;
import org.springframework.jdbc.core.JdbcTemplate;

public class InventoryDAO {
    
    private final JdbcTemplate jdbcTemplate;
    
    public InventoryDAO() {
        this.jdbcTemplate = AppConfig.getJdbcTemplate();
    }
    
    public void initializeInventory(Long productId, int quantity) {
        String sql = "INSERT INTO inventory (product_id, quantity) VALUES (?, ?) " +
                     "ON DUPLICATE KEY UPDATE quantity = ?";
        jdbcTemplate.update(sql, productId, quantity, quantity);
    }
    
    public int getAvailableQuantity(Long productId) {
        String sql = "SELECT quantity - COALESCE(reserved_quantity, 0) FROM inventory WHERE product_id = ?";
        Integer qty = jdbcTemplate.queryForObject(sql, Integer.class, productId);
        return qty != null ? qty : 0;
    }
    
    public boolean reserveInventory(Long productId, int quantity) {
        String sql = "UPDATE inventory SET reserved_quantity = reserved_quantity + ? " +
                     "WHERE product_id = ? AND (quantity - reserved_quantity) >= ?";
        int updated = jdbcTemplate.update(sql, quantity, productId, quantity);
        return updated > 0;
    }
    
    public void releaseInventory(Long productId, int quantity) {
        String sql = "UPDATE inventory SET reserved_quantity = GREATEST(reserved_quantity - ?, 0) " +
                     "WHERE product_id = ?";
        jdbcTemplate.update(sql, quantity, productId);
    }
    
    public void commitInventory(Long productId, int quantity) {
        String sql = "UPDATE inventory SET quantity = quantity - ?, " +
                     "reserved_quantity = reserved_quantity - ? WHERE product_id = ?";
        jdbcTemplate.update(sql, quantity, quantity, productId);
    }
    
    public boolean checkLowStock(Long productId) {
        String sql = "SELECT quantity <= reorder_level FROM inventory WHERE product_id = ?";
        Boolean isLow = jdbcTemplate.queryForObject(sql, Boolean.class, productId);
        return isLow != null && isLow;
    }
}
```

---

## Service Classes

```java
// service/OrderProcessingService.java
package com.learning.service;

import com.learning.dao.CustomerDAO;
import com.learning.dao.OrderDAO;
import com.learning.dao.InventoryDAO;
import com.learning.model.Customer;
import com.learning.model.Order;
import com.learning.model.OrderItem;
import com.learning.model.Product;
import com.learning.dao.ProductDAO;
import java.math.BigDecimal;
import java.util.List;

public class OrderProcessingService {
    
    private final OrderDAO orderDAO;
    private final CustomerDAO customerDAO;
    private final ProductDAO productDAO;
    private final InventoryDAO inventoryDAO;
    
    public OrderProcessingService() {
        this.orderDAO = new OrderDAO();
        this.customerDAO = new CustomerDAO();
        this.productDAO = new ProductDAO();
        this.inventoryDAO = new InventoryDAO();
    }
    
    public Order createOrder(Long customerId, List<OrderItemData> items, String shippingAddress) {
        Customer customer = customerDAO.findById(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found");
        }
        
        for (OrderItemData itemData : items) {
            int available = inventoryDAO.getAvailableQuantity(itemData.productId);
            if (available < itemData.quantity) {
                throw new IllegalStateException("Insufficient inventory for product: " + itemData.productId);
            }
        }
        
        for (OrderItemData itemData : items) {
            if (!inventoryDAO.reserveInventory(itemData.productId, itemData.quantity)) {
                for (OrderItemData undo : items) {
                    inventoryDAO.releaseInventory(undo.productId, undo.quantity);
                }
                throw new IllegalStateException("Failed to reserve inventory");
            }
        }
        
        Order order = new Order(customerId, shippingAddress);
        
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItemData itemData : items) {
            Product product = productDAO.findById(itemData.productId);
            OrderItem item = new OrderItem(
                itemData.productId,
                itemData.quantity,
                product.getPrice()
            );
            item.setProductName(product.getName());
            order.addItem(item);
            
            total = total.add(product.getPrice().multiply(new BigDecimal(itemData.quantity)));
        }
        
        order.setTotalAmount(total);
        order.setStatus("CONFIRMED");
        
        Long orderId = orderDAO.save(order);
        order.setId(orderId);
        
        customerDAO.addLoyaltyPoints(customerId, total.intValue());
        
        System.out.println("Order created: " + order.getOrderNumber() + " for $" + total);
        
        return order;
    }
    
    public void shipOrder(Long orderId) {
        Order order = orderDAO.findById(orderId);
        
        if (order == null) {
            throw new IllegalArgumentException("Order not found");
        }
        
        if (!"CONFIRMED".equals(order.getStatus()) && !"PROCESSING".equals(order.getStatus())) {
            throw new IllegalStateException("Order cannot be shipped in current status");
        }
        
        for (OrderItem item : order.getItems()) {
            inventoryDAO.commitInventory(item.getProductId(), item.getQuantity());
        }
        
        orderDAO.shipOrder(orderId);
        System.out.println("Order shipped: " + order.getOrderNumber());
    }
    
    public void deliverOrder(Long orderId) {
        Order order = orderDAO.findById(orderId);
        
        if (order == null) {
            throw new IllegalArgumentException("Order not found");
        }
        
        if (!"SHIPPED".equals(order.getStatus())) {
            throw new IllegalStateException("Order must be shipped before delivery");
        }
        
        orderDAO.deliverOrder(orderId);
        System.out.println("Order delivered: " + order.getOrderNumber());
    }
    
    public void cancelOrder(Long orderId) {
        Order order = orderDAO.findById(orderId);
        
        if (order == null) {
            throw new IllegalArgumentException("Order not found");
        }
        
        if ("DELIVERED".equals(order.getStatus()) || "CANCELLED".equals(order.getStatus())) {
            throw new IllegalStateException("Order cannot be cancelled");
        }
        
        for (OrderItem item : order.getItems()) {
            inventoryDAO.releaseInventory(item.getProductId(), item.getQuantity());
        }
        
        orderDAO.updateStatus(orderId, "CANCELLED");
        System.out.println("Order cancelled: " + order.getOrderNumber());
    }
    
    public void printOrderDetails(Long orderId) {
        Order order = orderDAO.findById(orderId);
        
        if (order == null) {
            System.out.println("Order not found");
            return;
        }
        
        System.out.println("\n=== Order Details ===");
        System.out.println("Order Number: " + order.getOrderNumber());
        System.out.println("Customer: " + order.getCustomerName());
        System.out.println("Status: " + order.getStatus());
        System.out.println("Total: $" + order.getTotalAmount());
        System.out.println("\nItems:");
        
        for (OrderItem item : order.getItems()) {
            System.out.println("  - " + item.getProductName() + " x " + item.getQuantity() + 
                " @ $" + item.getUnitPrice() + " = $" + item.getSubtotal());
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

import com.learning.dao.InventoryDAO;
import com.learning.dao.ProductDAO;
import com.learning.model.Product;
import java.util.List;

public class InventoryService {
    
    private final InventoryDAO inventoryDAO;
    private final ProductDAO productDAO;
    
    public InventoryService() {
        this.inventoryDAO = new InventoryDAO();
        this.productDAO = new ProductDAO();
    }
    
    public void initializeInventory(Long productId, int quantity) {
        inventoryDAO.initializeInventory(productId, quantity);
        System.out.println("Initialized inventory for product " + productId + " with " + quantity + " units");
    }
    
    public void checkAvailability(Long productId, int requestedQuantity) {
        int available = inventoryDAO.getAvailableQuantity(productId);
        
        Product product = productDAO.findById(productId);
        
        System.out.println("\n=== Inventory Check ===");
        System.out.println("Product: " + product.getName());
        System.out.println("Requested: " + requestedQuantity);
        System.out.println("Available: " + available);
        
        if (available >= requestedQuantity) {
            System.out.println("Status: Available");
        } else {
            System.out.println("Status: Insufficient Stock");
            System.out.println("Short by: " + (requestedQuantity - available));
        }
    }
    
    public void checkLowStock() {
        List<Product> lowStockProducts = productDAO.findLowStock(10);
        
        System.out.println("\n=== Low Stock Alert ===");
        if (lowStockProducts.isEmpty()) {
            System.out.println("No low stock products");
        } else {
            lowStockProducts.forEach(p -> {
                System.out.println("Product: " + p.getName() + " (SKU: " + p.getSku() + 
                    ") - Stock: " + p.getStockQuantity());
            });
        }
    }
    
    public void reorderStock(Long productId) {
        Product product = productDAO.findById(productId);
        
        if (product == null) {
            throw new IllegalArgumentException("Product not found");
        }
        
        int currentStock = inventoryDAO.getAvailableQuantity(productId);
        
        System.out.println("Current stock for " + product.getName() + ": " + currentStock);
        
        if (currentStock < 10) {
            inventoryDAO.initializeInventory(productId, currentStock + 50);
            System.out.println("Reorder completed. New stock: " + (currentStock + 50));
        }
    }
}
```

---

## Main Application

```java
// Main.java
package com.learning;

import com.learning.dao.ProductDAO;
import com.learning.model.Customer;
import com.learning.model.Order;
import com.learning.model.Product;
import com.learning.service.InventoryService;
import com.learning.service.OrderProcessingService;
import com.learning.service.OrderProcessingService.OrderItemData;
import com.learning.dao.CustomerDAO;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class Main {
    
    public static void main(String[] args) {
        System.out.println("=== E-Commerce Order Processing System ===\n");
        
        ProductDAO productDAO = new ProductDAO();
        CustomerDAO customerDAO = new CustomerDAO();
        
        System.out.println("=== Available Products ===");
        List<Product> products = productDAO.findAll();
        products.forEach(p -> System.out.println(p.getName() + " - $" + p.getPrice() + " (Stock: " + p.getStockQuantity() + ")"));
        
        InventoryService inventoryService = new InventoryService();
        
        products.forEach(p -> inventoryService.initializeInventory(p.getId(), p.getStockQuantity()));
        
        Customer customer1 = customerDAO.findById(1L);
        Customer customer2 = customerDAO.findById(2L);
        
        OrderProcessingService orderService = new OrderProcessingService();
        
        System.out.println("\n=== Creating Order 1 ===");
        List<OrderItemData> order1Items = Arrays.asList(
            new OrderItemData(1L, 1),
            new OrderItemData(3L, 2)
        );
        
        Order order1 = orderService.createOrder(1L, order1Items, "123 Main St, New York, NY 10001");
        
        System.out.println("\n=== Creating Order 2 ===");
        List<OrderItemData> order2Items = Arrays.asList(
            new OrderItemData(2L, 1)
        );
        
        Order order2 = orderService.createOrder(2L, order2Items, "456 Oak Ave, Los Angeles, CA 90001");
        
        orderService.printOrderDetails(order1.getId());
        orderService.printOrderDetails(order2.getId());
        
        orderService.shipOrder(order1.getId());
        orderService.shipOrder(order2.getId());
        
        orderService.deliverOrder(order1.getId());
        
        System.out.println("\n=== Updated Customer Loyalty Points ===");
        Customer updatedCustomer1 = customerDAO.findById(1L);
        Customer updatedCustomer2 = customerDAO.findById(2L);
        System.out.println(updatedCustomer1.getFullName() + ": " + updatedCustomer1.getLoyaltyPoints() + " points (" + updatedCustomer1.getTier() + ")");
        System.out.println(updatedCustomer2.getFullName() + ": " + updatedCustomer2.getLoyaltyPoints() + " points (" + updatedCustomer2.getTier() + ")");
        
        inventoryService.checkLowStock();
        
        com.learning.config.AppConfig.getJdbcTemplate();
        
        System.out.println("\n=== System Shutdown ===");
    }
}
```

---

## Build Instructions

```bash
cd 18-database-access
mvn clean compile
mvn exec:java -Dexec.mainClass="com.learning.Main"
```

This comprehensive project demonstrates advanced JDBC concepts including JDBC Template, connection pooling with HikariCP, transaction management, batch operations, and complex DAO patterns for a production-grade e-commerce system.

---

# Production Patterns: Connection Pooling

## HikariCP Production Configuration

```java
// util/ProductionPoolConfig.java
package com.learning.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import java.util.concurrent.TimeUnit;

public class ProductionPoolConfig {
    
    private static HikariDataSource createDataSource(
            String jdbcUrl, String username, String password) {
        
        HikariConfig config = new HikariConfig();
        
        // Connection settings
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName("org.postgresql.Driver");
        
        // Pool sizing (adjust based on expected load)
        config.setMaximumPoolSize(20);
        config.setMinimumIdle(5);
        
        // Timeouts (in milliseconds)
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);        // 10 minutes
        config.setMaxLifetime(1800000);       // 30 minutes
        
        // Validation
        config.setConnectionTestQuery("SELECT 1");
        config.setValidationTimeout(5000);
        
        // Leak detection (2 seconds in dev, disable in prod)
        config.setLeakDetectionThreshold(TimeUnit.SECONDS.toMillis(2));
        
        // Performance optimizations
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        
        // Pool name for monitoring
        config.setPoolName("ProductionPool");
        config.setRegisterMbeans(true);
        
        return new HikariDataSource(config);
    }
}
```

## Connection Pool Monitoring

```java
// util/PoolMonitor.java
package com.learning.util;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;

public class PoolMonitor {
    
    public static void printPoolStats(HikariDataSource dataSource) {
        System.out.println("=== HikariCP Pool Statistics ===");
        System.out.println("Active Connections: " + dataSource.getHikariPoolMXBean()
            .getActiveConnections());
        System.out.println("Idle Connections: " + dataSource.getHikariPoolMXBean()
            .getIdleConnections());
        System.out.println("Total Connections: " + dataSource.getHikariPoolMXBean()
            .getTotalConnections());
        System.out.println("Threads Awaiting: " + dataSource.getHikariPoolMXBean()
            .getThreadsAwaitingConnection());
        System.out.println("Connection Timeout Count: " + dataSource.getHikariPoolMXBean()
            .getConnectionTimeoutCount());
        System.out.println("================================");
    }
    
    public static boolean isPoolHealthy(HikariDataSource dataSource) {
        try (Connection conn = dataSource.getConnection()) {
            return conn.isValid(5);
        } catch (Exception e) {
            return false;
        }
    }
}
```

## Circuit Breaker for Database Access

```java
// util/DatabaseCircuitBreaker.java
package com.learning.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

public class DatabaseCircuitBreaker {
    
    private static final int FAILURE_THRESHOLD = 5;
    private static final long RESET_TIMEOUT_MS = 60000;
    
    private final AtomicInteger failureCount = new AtomicInteger(0);
    private final AtomicLong lastFailureTime = new AtomicLong(0);
    private volatile State state = State.CLOSED;
    
    public enum State { CLOSED, OPEN, HALF_OPEN }
    
    public <T> T execute(Supplier<T> operation, Supplier<T> fallback) 
            throws SQLException {
        
        if (state == State.OPEN) {
            if (shouldAttemptReset()) {
                state = State.HALF_OPEN;
            } else {
                return fallback.get();
            }
        }
        
        try {
            T result = operation.get();
            onSuccess();
            return result;
        } catch (Exception e) {
            onFailure();
            return fallback.get();
        }
    }
    
    private void onSuccess() {
        failureCount.set(0);
        state = State.CLOSED;
    }
    
    private void onFailure() {
        int failures = failureCount.incrementAndGet();
        lastFailureTime.set(System.currentTimeMillis());
        
        if (failures >= FAILURE_THRESHOLD) {
            state = State.OPEN;
            System.out.println("Circuit breaker OPENED after " + failures + " failures");
        }
    }
    
    private boolean shouldAttemptReset() {
        return System.currentTimeMillis() - lastFailureTime.get() > RESET_TIMEOUT_MS;
    }
    
    public State getState() {
        return state;
    }
}
```

## Read Replica Routing

```java
// util/ReadReplicaRouter.java
package com.learning.util;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ReadReplicaRouter {
    
    private final DataSource primaryDS;
    private final List<DataSource> replicaDSList;
    private volatile boolean readFromReplica = true;
    
    public ReadReplicaRouter(DataSource primary, List<DataSource> replicas) {
        this.primaryDS = primary;
        this.replicaDSList = replicas;
    }
    
    public Connection getReadConnection() throws SQLException {
        if (!readFromReplica || replicaDSList.isEmpty()) {
            return primaryDS.getConnection();
        }
        return replicaDSList.get(
            ThreadLocalRandom.current().nextInt(replicaDSList.size())
        ).getConnection();
    }
    
    public Connection getWriteConnection() throws SQLException {
        return primaryDS.getConnection();
    }
    
    public Connection getConnection(boolean isReadOnly) throws SQLException {
        return isReadOnly ? getReadConnection() : getWriteConnection();
    }
    
    public void disableReplicaReads() {
        readFromReplica = false;
    }
    
    public void enableReplicaReads() {
        readFromReplica = true;
    }
}
```

## Batch Processing with Progress Tracking

```java
// util/BatchProcessor.java
package com.learning.util;

import org.springframework.jdbc.core.JdbcTemplate;
import java.util.List;
import java.util.function.Consumer;

public class BatchProcessor {
    
    private final JdbcTemplate jdbcTemplate;
    private static final int BATCH_SIZE = 1000;
    
    public BatchProcessor(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    public int batchInsert(String sql, List<Object[]> batchValues) {
        return batchInsert(sql, batchValues, null);
    }
    
    public int batchInsert(String sql, List<Object[]> batchValues, 
                          Consumer<Integer> progressCallback) {
        int totalUpdated = 0;
        int batchCount = 0;
        
        for (int i = 0; i < batchValues.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, batchValues.size());
            List<Object[]> batch = batchValues.subList(i, end);
            
            int[] results = jdbcTemplate.batchUpdate(sql, batch);
            int batchTotal = 0;
            for (int result : results) {
                batchTotal += Math.abs(result);
            }
            totalUpdated += batchTotal;
            batchCount++;
            
            if (progressCallback != null) {
                progressCallback.accept(totalUpdated);
            }
            
            System.out.printf("Batch %d: %d records, Progress: %d/%d%n",
                batchCount, batchTotal, totalUpdated, batchValues.size());
        }
        
        return totalUpdated;
    }
}
```

## Transaction with Savepoint

```java
// service/TransactionWithSavepoint.java
package com.learning.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import java.sql.SQLException;
import java.sql.Savepoint;

public class TransactionWithSavepoint {
    
    private final TransactionTemplate transactionTemplate;
    
    public TransactionWithSavepoint(PlatformTransactionManager txManager) {
        this.transactionTemplate = new TransactionTemplate(txManager);
    }
    
    public void processOrderWithPartialRollback(Order order) {
        transactionTemplate.executeWithoutResult(status -> {
            try {
                executeStep1(order);
                Savepoint step1Savepoint = status.createSavepoint();
                
                try {
                    executeStep2(order);
                    Savepoint step2Savepoint = status.createSavepoint();
                    
                    try {
                        executeStep3(order);
                    } catch (Exception e) {
                        status.rollbackToSavepoint(step2Savepoint);
                        executeStep3Fallback(order);
                    }
                } catch (Exception e) {
                    status.rollbackToSavepoint(step1Savepoint);
                    executeStep2Fallback(order);
                }
            } catch (Exception e) {
                throw new RuntimeException("Transaction failed", e);
            }
        });
    }
    
    private void executeStep1(Order order) {
        System.out.println("Executing step 1: Reserve inventory");
    }
    
    private void executeStep2(Order order) {
        System.out.println("Executing step 2: Process payment");
    }
    
    private void executeStep3(Order order) {
        System.out.println("Executing step 3: Create shipping label");
    }
    
    private void executeStep2Fallback(Order order) {
        System.out.println("Fallback: Releasing inventory reservation");
    }
    
    private void executeStep3Fallback(Order order) {
        System.out.println("Fallback: Cancelling payment");
    }
}
```