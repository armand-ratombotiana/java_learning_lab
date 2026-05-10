# Helidon Projects

This directory contains hands-on projects using Helidon, a lightweight Java framework for building microservices. Helidon provides both Helidon SE (functional style) and Helidon MP (MicroProfile compatible) to support different programming paradigms.

## Project Overview

Helidon is designed for cloud-native microservices with fast startup times, low memory footprint, and excellent support for reactive programming. This module covers two projects of increasing complexity.

---

# Mini-Project: REST API Service (2-4 Hours)

## Project Description

Build a RESTful employee management service using Helidon SE with reactive routing, health checks, and metrics. This project demonstrates Helidon's lightweight approach to building modern Java microservices.

## Technologies Used

- Helidon 4.0.0
- Helidon SE (using routing API)
- Java 21
- Maven
- In-memory employee store

## Implementation Steps

### Step 1: Create Maven Project Structure

```bash
# Create project directory
mkdir helidon-employee-service
cd helidon-employee-service

# Create directory structure
mkdir -p src/main/java/com/learning/employee/{config,model,service,resource}
mkdir -p src/main/resources
mkdir -p src/test/java/com/learning/employee
```

### Step 2: Create pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>helidon-employee-service</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>
    
    <name>Helidon Employee Service</name>
    <description>Employee management REST API with Helidon SE</description>
    
    <properties>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <helidon.version>4.0.0</helidon.version>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>io.helidon.webserver</groupId>
            <artifactId>helidon-webserver</artifactId>
            <version>${helidon.version}</version>
        </dependency>
        <dependency>
            <groupId>io.helidon.webserver</groupId>
            <artifactId>helidon-webserver-cors</artifactId>
            <version>${helidon.version}</version>
        </dependency>
        <dependency>
            <groupId>io.helidon.config</groupId>
            <artifactId>helidon-config</artifactId>
            <version>${helidon.version}</version>
        </dependency>
        <dependency>
            <groupId>io.helidon.media</groupId>
            <artifactId>helidon-media-jsonb</artifactId>
            <version>${helidon.version}</version>
        </dependency>
        <dependency>
            <groupId>io.helidon.health</groupId>
            <artifactId>helidon-health</artifactId>
            <version>${helidon.version}</version>
        </dependency>
        <dependency>
            <groupId>io.helidon.health</groupId>
            <artifactId>helidon-health-checks</artifactId>
            <version>${helidon.version}</version>
        </dependency>
        <dependency>
            <groupId>io.helidon.metrics</groupId>
            <artifactId>helidon-metrics</artifactId>
            <version>${helidon.version}</version>
        </dependency>
        <dependency>
            <groupId>jakarta.json</groupId>
            <artifactId>jakarta.json-api</artifactId>
            <version>2.1.3</version>
        </dependency>
        <dependency>
            <groupId>org.eclipse.parsson</groupId>
            <artifactId>parsson</artifactId>
            <version>1.1.5</version>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>21</source>
                    <target>21</target>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <version>3.3.0</version>
                <configuration>
                    <manifest>
                        <mainClass>com.learning.employee.Main</mainClass>
                    </manifest>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-dependency-plugin</artifactId>
                <version>3.6.0</version>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>3.5.1</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                        <configuration>
                            <transformers>
                                <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                                    <mainClass>com.learning.employee.Main</mainClass>
                                </transformer>
                            </transformers>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

### Step 3: Create Employee Model

```java
package com.learning.employee.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class Employee {
    
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String department;
    private String jobTitle;
    private LocalDate hireDate;
    private Double salary;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public Employee() {
        this.active = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Employee(Long id, String firstName, String lastName, String email, 
                    String department, String jobTitle, LocalDate hireDate, Double salary) {
        this();
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.department = department;
        this.jobTitle = jobTitle;
        this.hireDate = hireDate;
        this.salary = salary;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { 
        this.firstName = firstName;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { 
        this.lastName = lastName;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { 
        this.email = email;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { 
        this.department = department;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { 
        this.jobTitle = jobTitle;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }
    
    public Double getSalary() { return salary; }
    public void setSalary(Double salary) { 
        this.salary = salary;
        this.updatedAt = LocalDate.now();
    }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { 
        this.active = active;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(id, employee.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
```

### Step 4: Create Employee Service

```java
package com.learning.employee.service;

import com.learning.employee.model.Employee;
import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class EmployeeService {
    
    private final Map<Long, Employee> employeeStore = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    @PostConstruct
    public void init() {
        // Add sample employees
        addEmployee(new Employee(
            idGenerator.getAndIncrement(),
            "John",
            "Doe",
            "john.doe@company.com",
            "Engineering",
            "Senior Developer",
            LocalDate.of(2020, 3, 15),
            95000.0
        ));
        
        addEmployee(new Employee(
            idGenerator.getAndIncrement(),
            "Jane",
            "Smith",
            "jane.smith@company.com",
            "Marketing",
            "Marketing Manager",
            LocalDate.of(2019, 8, 1),
            85000.0
        ));
        
        addEmployee(new Employee(
            idGenerator.getAndIncrement(),
            "Bob",
            "Johnson",
            "bob.johnson@company.com",
            "Sales",
            "Sales Representative",
            LocalDate.of(2021, 1, 10),
            65000.0
        ));
    }
    
    public List<Employee> getAllEmployees() {
        return new ArrayList<>(employeeStore.values());
    }
    
    public List<Employee> getActiveEmployees() {
        return employeeStore.values().stream()
            .filter(Employee::isActive)
            .collect(Collectors.toList());
    }
    
    public Optional<Employee> getEmployeeById(Long id) {
        return Optional.ofNullable(employeeStore.get(id));
    }
    
    public List<Employee> getEmployeesByDepartment(String department) {
        return employeeStore.values().stream()
            .filter(e -> e.getDepartment().equalsIgnoreCase(department))
            .collect(Collectors.toList());
    }
    
    public List<Employee> searchEmployees(String query) {
        String lowerQuery = query.toLowerCase();
        return employeeStore.values().stream()
            .filter(e -> 
                e.getFirstName().toLowerCase().contains(lowerQuery) ||
                e.getLastName().toLowerCase().contains(lowerQuery) ||
                e.getEmail().toLowerCase().contains(lowerQuery) ||
                e.getDepartment().toLowerCase().contains(lowerQuery) ||
                e.getJobTitle().toLowerCase().contains(lowerQuery))
            .collect(Collectors.toList());
    }
    
    public Employee addEmployee(Employee employee) {
        Long id = idGenerator.getAndIncrement();
        employee.setId(id);
        employeeStore.put(id, employee);
        return employee;
    }
    
    public Optional<Employee> updateEmployee(Long id, Employee employeeUpdate) {
        Employee existing = employeeStore.get(id);
        if (existing == null) {
            return Optional.empty();
        }
        
        if (employeeUpdate.getFirstName() != null) {
            existing.setFirstName(employeeUpdate.getFirstName());
        }
        if (employeeUpdate.getLastName() != null) {
            existing.setLastName(employeeUpdate.getLastName());
        }
        if (employeeUpdate.getEmail() != null) {
            existing.setEmail(employeeUpdate.getEmail());
        }
        if (employeeUpdate.getDepartment() != null) {
            existing.setDepartment(employeeUpdate.getDepartment());
        }
        if (employeeUpdate.getJobTitle() != null) {
            existing.setJobTitle(employeeUpdate.getJobTitle());
        }
        if (employeeUpdate.getSalary() != null) {
            existing.setSalary(employeeUpdate.getSalary());
        }
        
        employeeStore.put(id, existing);
        return Optional.of(existing);
    }
    
    public Optional<Employee> deleteEmployee(Long id) {
        return Optional.ofNullable(employeeStore.remove(id));
    }
    
    public Optional<Employee> deactivateEmployee(Long id) {
        Employee employee = employeeStore.get(id);
        if (employee == null) {
            return Optional.empty();
        }
        employee.setActive(false);
        return Optional.of(employee);
    }
    
    public boolean employeeExists(Long id) {
        return employeeStore.containsKey(id);
    }
    
    public long getEmployeeCount() {
        return employeeStore.size();
    }
    
    public long getActiveEmployeeCount() {
        return employeeStore.values().stream()
            .filter(Employee::isActive)
            .count();
    }
}
```

### Step 5: Create Employee REST Resource

```java
package com.learning.employee.resource;

import com.learning.employee.model.Employee;
import com.learning.employee.service.EmployeeService;
import io.helidon.http.Http.MediaType;
import io.helidon.webserver.http.HttpException;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import java.util.List;

import static io.helidon.http.Http.Status.*;
import static io.helidon.webserver.http.HttpFilters.*;

public class EmployeeResource {
    
    private final EmployeeService employeeService;
    
    public EmployeeResource(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }
    
    void getAllEmployees(ServerRequest request, ServerResponse response) {
        String activeOnly = request.queryParam("active").orElse("false");
        
        List<Employee> employees;
        if ("true".equalsIgnoreCase(activeOnly)) {
            employees = employeeService.getActiveEmployees();
        } else {
            employees = employeeService.getAllEmployees();
        }
        
        response.status(OK_200).send(employees);
    }
    
    void getEmployeeById(ServerRequest request, ServerResponse response) {
        Long id = Long.parseLong(request.path("id"));
        
        employeeService.getEmployeeById(id)
            .ifPresentOrElse(
                emp -> response.status(OK_200).send(emp),
                () -> response.status(NOT_FOUND_404)
                    .send(new ErrorMessage("Employee not found with id: " + id))
            );
    }
    
    void getEmployeesByDepartment(ServerRequest request, ServerResponse response) {
        String department = request.path("department");
        
        List<Employee> employees = employeeService.getEmployeesByDepartment(department);
        response.status(OK_200).send(employees);
    }
    
    void searchEmployees(ServerRequest request, ServerResponse response) {
        String query = request.queryParam("q")
            .orElseThrow(() -> new HttpException(
                BAD_REQUEST_400, 
                "Query parameter 'q' is required"
            ));
        
        List<Employee> employees = employeeService.searchEmployees(query);
        response.status(OK_200).send(employees);
    }
    
    void createEmployee(ServerRequest request, ServerResponse response) {
        Employee employee = request.content().as(Employee.class);
        
        if (employee.getFirstName() == null || employee.getLastName() == null) {
            response.status(BAD_REQUEST_400)
                .send(new ErrorMessage("First name and last name are required"));
            return;
        }
        
        Employee created = employeeService.addEmployee(employee);
        response.status(CREATED_201).send(created);
    }
    
    void updateEmployee(ServerRequest request, ServerResponse response) {
        Long id = Long.parseLong(request.path("id"));
        
        if (!employeeService.employeeExists(id)) {
            response.status(NOT_FOUND_404)
                .send(new ErrorMessage("Employee not found with id: " + id));
            return;
        }
        
        Employee employeeUpdate = request.content().as(Employee.class);
        Employee updated = employeeService.updateEmployee(id, employeeUpdate)
            .orElseThrow(() -> new HttpException(
                INTERNAL_SERVER_ERROR_500,
                "Failed to update employee"
            ));
        
        response.status(OK_200).send(updated);
    }
    
    void deleteEmployee(ServerRequest request, ServerResponse response) {
        Long id = Long.parseLong(request.path("id"));
        
        employeeService.deleteEmployee(id)
            .ifPresentOrElse(
                emp -> response.status(NO_CONTENT_204).send(),
                () -> response.status(NOT_FOUND_404)
                    .send(new ErrorMessage("Employee not found with id: " + id))
            );
    }
    
    void deactivateEmployee(ServerRequest request, ServerResponse response) {
        Long id = Long.parseLong(request.path("id"));
        
        employeeService.deactivateEmployee(id)
            .ifPresentOrElse(
                emp -> response.status(OK_200).send(emp),
                () -> response.status(NOT_FOUND_404)
                    .send(new ErrorMessage("Employee not found with id: " + id))
            );
    }
    
    void getEmployeeCount(ServerRequest request, ServerResponse response) {
        long count = employeeService.getEmployeeCount();
        response.status(OK_200).send(new CountResponse(count));
    }
    
    void getActiveEmployeeCount(ServerRequest request, ServerResponse response) {
        long count = employeeService.getActiveEmployeeCount();
        response.status(OK_200).send(new CountResponse(count));
    }
    
    static class ErrorMessage {
        public String message;
        
        public ErrorMessage() {}
        
        public ErrorMessage(String message) {
            this.message = message;
        }
    }
    
    static class CountResponse {
        public long count;
        
        public CountResponse() {}
        
        public CountResponse(long count) {
            this.count = count;
        }
    }
}
```

### Step 6: Create Application Main Class

```java
package com.learning.employee;

import com.learning.employee.model.Employee;
import com.learning.employee.resource.EmployeeResource;
import com.learning.employee.service.EmployeeService;
import io.helidon.config.Config;
import io.helidon.health.HealthCheck;
import io.helidon.health.HealthCheckResponse;
import io.helidon.metrics.api.MeterRegistry;
import io.helidon.metrics.api.SimpleMeterRegistry;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.http.HttpRouting;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static io.helidon.http.Http.MediaType.*;
import static io.helidon.webserver.http.HttpFilters.*;

public class Main {
    
    public static void main(String[] args) throws IOException {
        Config config = Config.create();
        
        EmployeeService employeeService = new EmployeeService();
        EmployeeResource employeeResource = new EmployeeResource(employeeService);
        
        MeterRegistry meterRegistry = new SimpleMeterRegistry();
        
        WebServer server = WebServer.builder()
            .port(config.get("server.port").asInt().orElse(8080))
            .routing(routing -> routing
                .register("/health", HealthCheck.of(
                    HealthCheck.builder()
                        .name("employee-service")
                        .provider(() -> HealthCheckResponse.named("employee-service")
                            .up()
                            .withData("employees", employeeService.getEmployeeCount())
                            .build())
                ))
                .register("/metrics", meterRegistry)
                .register("/api/employees", employeeResource, routingBuilder -> routingBuilder
                    .get("/", employeeResource::getAllEmployees)
                    .get("/count", employeeResource::getActiveEmployeeCount)
                    .get("/{id}", employeeResource::getEmployeeById)
                    .get("/department/{department}", employeeResource::getEmployeesByDepartment)
                    .get("/search", employeeResource::searchEmployees)
                    .post("/", employeeResource::createEmployee)
                    .put("/{id}", employeeResource::updateEmployee)
                    .delete("/{id}", employeeResource::deleteEmployee)
                    .patch("/{id}/deactivate", employeeResource::deactivateEmployee)
                )
            )
            .build()
            .start();
        
        System.out.println("Employee Service started on port: " + server.port());
        System.out.println("Health check available at: http://localhost:" + server.port() + "/health");
        System.out.println("Metrics available at: http://localhost:" + server.port() + "/metrics");
        System.out.println("API available at: http://localhost:" + server.port() + "/api/employees");
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down Employee Service...");
            server.shutdown();
        }));
        
        server.awaitShutdown();
    }
}
```

### Step 7: Configure Application Properties

Create `src/main/resources/application.yaml`:

```yaml
server:
  port: 8080
  host: "0.0.0.0"

app:
  name: "Helidon Employee Service"
  version: "1.0.0"

database:
  url: "jdbc:h2:mem:employeeDb"
  username: "sa"
  password: ""
```

### Step 8: Run and Test

```bash
# Build the project
cd helidon-employee-service
mvn clean package

# Run the application
java -jar target/helidon-employee-service-1.0.0.jar

# Test endpoints
# Get all employees
curl http://localhost:8080/api/employees

# Get employee by ID
curl http://localhost:8080/api/employees/1

# Search employees
curl "http://localhost:8080/api/employees/search?q=John"

# Create new employee
curl -X POST http://localhost:8080/api/employees \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Alice",
    "lastName": "Williams",
    "email": "alice.williams@company.com",
    "department": "Engineering",
    "jobTitle": "Software Engineer",
    "hireDate": "2024-01-15",
    "salary": 75000.0
  }'

# Update employee
curl -X PUT http://localhost:8080/api/employees/1 \
  -H "Content-Type: application/json" \
  -d '{"jobTitle": "Lead Developer", "salary": 105000.0}'

# Delete employee
curl -X DELETE http://localhost:8080/api/employees/1

# Check health
curl http://localhost:8080/health
```

## Expected Output

The mini-project produces a fully functional employee management API with:
- CRUD operations for employees
- Department-based filtering
- Search functionality
- Health check endpoints
- Basic metrics

---

# Real-World Project: E-Commerce Order Processing System (8+ Hours)

## Project Description

Build a complete e-commerce order processing system using Helidon MP (MicroProfile) with CDI, JPA, and Bean Validation. This project implements order management, inventory checking, payment processing, and notification services with transaction support.

## Architecture

```
┌─────────────────────────────────────────────┐
│         API Gateway (Helidon)                 │
└─────────────────────────────────────────────┘
                    │
    ┌───────────────┼───────────────┐
    │               │               │
┌───▼───┐     ┌────▼────┐    ┌──▼───┐
│Order  │     │Inventory│    │Payment│
│Service│     │ Service │    │Service│
└───────┘     └─────────┘    └──────┘
```

## Implementation Steps

### Step 1: Create Helidon MP Project

```bash
mkdir helidon-order-system
cd helidon-order-system

# Create Maven structure
mkdir -p src/main/java/com/learning/order/{entity,repository,service,resource,config}
mkdir -p src/main/resources/META-INF
mkdir -p src/test/java/com/learning/order
```

### Step 2: Create Order Service pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>helidon-order-system</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>
    
    <name>Helidon Order System</name>
    <description>E-Commerce Order Processing with Helidon MP</description>
    
    <properties>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <helidon.version>4.0.0</helidon.version>
        <hikari.version>5.1.0</hikari.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>io.helidon</groupId>
            <artifactId>helidon-microprofile-bundle</artifactId>
            <version>${helidon.version}</version>
        </dependency>
        <dependency>
            <groupId>io.helidon.webserver</groupId>
            <artifactId>helidon-webserver</artifactId>
            <version>${helidon.version}</version>
        </dependency>
        <dependency>
            <groupId>io.helidon.microprofile</groupId>
            <artifactId>helidon-microprofile-jwt</artifactId>
            <version>${helidon.version}</version>
        </dependency>
        <dependency>
            <groupId>jakarta.persistence</groupId>
            <artifactId>jakarta.persistence-api</artifactId>
            <version>3.1.0</version>
        </dependency>
        <dependency>
            <groupId>jakarta.transaction</groupId>
            <artifactId>jakarta.transaction-api</artifactId>
            <version>2.0.1</version>
        </dependency>
        <dependency>
            <groupId>jakarta.validation</groupId>
            <artifactId>jakarta.validation-api</artifactId>
            <version>3.0.2</version>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <version>2.2.224</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>com.zaxxer</groupId>
            <artifactId>HikariCP</artifactId>
            <version>${hikari.version}</version>
        </dependency>
        <dependency>
            <groupId>org.eclipse.parsson</groupId>
            <artifactId>parsson</artifactId>
            <version>1.1.5</version>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>3.5.1</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals><goal>shade</goal></goals>
                        <configuration>
                            <transformers>
                                <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                                    <mainClass>com.learning.order.Main</mainClass>
                                </transformer>
                                <transformer implementation="org.apache.maven.plugins.shade.resource.ServicesResourceTransformer"/>
                            </transformers>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

### Step 3: Create Order Entity

```java
package com.learning.order.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "orders")
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @Column(name = "order_number", nullable = false, unique = true)
    private String orderNumber;
    
    @NotNull
    @Column(name = "customer_id", nullable = false)
    private Long customerId;
    
    @NotNull
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status = OrderStatus.PENDING;
    
    @Column(name = "shipping_address", columnDefinition = "TEXT")
    private String shippingAddress;
    
    @Column(name = "billing_address", columnDefinition = "TEXT")
    private String billingAddress;
    
    @Column(name = "payment_method")
    private String paymentMethod;
    
    @Column(name = "payment_id")
    private String paymentId;
    
    @OneToMany(mappedBy = "orderId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;
    
    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrderId(this.id);
    }
    
    public void removeItem(OrderItem item) {
        items.remove(item);
        item.setOrderId(null);
    }
    
    public void calculateTotal() {
        this.totalAmount = items.stream()
            .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    
    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    
    public String getBillingAddress() { return billingAddress; }
    public void setBillingAddress(String billingAddress) { this.billingAddress = billingAddress; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getShippedAt() { return shippedAt; }
    public void setShippedAt(LocalDateTime shippedAt) { this.shippedAt = shippedAt; }
    
    public LocalDateTime getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(LocalDateTime deliveredAt) { this.deliveredAt = deliveredAt; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
```

```java
package com.learning.order.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "order_item")
public class OrderItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "order_id")
    private Long orderId;
    
    @Column(name = "product_id", nullable = false)
    private Long productId;
    
    @Column(name = "product_name", nullable = false)
    private String productName;
    
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;
    
    @Column(name = "line_total", precision = 10, scale = 2)
    private BigDecimal lineTotal;
    
    public void calculateLineTotal() {
        this.lineTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
    
    // Getters and Setters
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
    
    public BigDecimal getLineTotal() { return lineTotal; }
    public void setLineTotal(BigDecimal lineTotal) { this.lineTotal = lineTotal; }
}
```

```java
package com.learning.order.entity;

public enum OrderStatus {
    PENDING,
    CONFIRMED,
    PAID,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    REFUNDED
}
```

### Step 4: Create Repository

```java
package com.learning.order.repository;

import com.learning.order.entity.Order;
import com.learning.order.entity.OrderStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class OrderRepository {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    public Order save(Order order) {
        if (order.getId() == null) {
            entityManager.persist(order);
            return order;
        } else {
            return entityManager.merge(order);
        }
    }
    
    public Optional<Order> findById(Long id) {
        Order order = entityManager.find(Order.class, id);
        return Optional.ofNullable(order);
    }
    
    public Optional<Order> findByOrderNumber(String orderNumber) {
        TypedQuery<Order> query = entityManager.createQuery(
            "SELECT o FROM Order o WHERE o.orderNumber = :orderNumber",
            Order.class
        );
        query.setParameter("orderNumber", orderNumber);
        
        try {
            return Optional.of(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    public List<Order> findByCustomerId(Long customerId) {
        TypedQuery<Order> query = entityManager.createQuery(
            "SELECT o FROM Order o WHERE o.customerId = :customerId ORDER BY o.createdAt DESC",
            Order.class
        );
        query.setParameter("customerId", customerId);
        return query.getResultList();
    }
    
    public List<Order> findByStatus(OrderStatus status) {
        TypedQuery<Order> query = entityManager.createQuery(
            "SELECT o FROM Order o WHERE o.status = :status ORDER BY o.createdAt DESC",
            Order.class
        );
        query.setParameter("status", status);
        return query.getResultList();
    }
    
    public void delete(Order order) {
        entityManager.remove(order);
    }
    
    public void deleteById(Long id) {
        findById(id).ifPresent(this::delete);
    }
    
    public List<Order> findAll() {
        TypedQuery<Order> query = entityManager.createQuery(
            "SELECT o FROM Order o ORDER BY o.createdAt DESC",
            Order.class
        );
        return query.getResultList();
    }
}
```

### Step 5: Create Order Service

```java
package com.learning.order.service;

import com.learning.order.entity.Order;
import com.learning.order.entity.OrderItem;
import com.learning.order.entity.OrderStatus;
import com.learning.order.repository.OrderRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class OrderService {
    
    @Inject
    private OrderRepository orderRepository;
    
    @Inject
    private InventoryService inventoryService;
    
    @Inject
    private PaymentService paymentService;
    
    @Inject
    private NotificationService notificationService;
    
    @Transactional
    public Order createOrder(Order order) {
        // Generate order number
        order.setOrderNumber(generateOrderNumber());
        
        // Validate and calculate totals
        for (OrderItem item : order.getItems()) {
            item.calculateLineTotal();
        }
        order.calculateTotal();
        
        // Check inventory availability
        for (OrderItem item : order.getItems()) {
            if (!inventoryService.reserveStock(item.getProductId(), item.getQuantity())) {
                throw new RuntimeException(
                    "Insufficient stock for product: " + item.getProductName()
                );
            }
        }
        
        // Save order
        Order savedOrder = orderRepository.save(order);
        
        // Process payment
        try {
            paymentService.processPayment(savedOrder);
            savedOrder.setStatus(OrderStatus.PAID);
        } catch (Exception e) {
            savedOrder.setStatus(OrderStatus.CANCELLED);
            // Release inventory
            for (OrderItem item : order.getItems()) {
                inventoryService.releaseStock(item.getProductId(), item.getQuantity());
            }
            throw new RuntimeException("Payment failed: " + e.getMessage());
        }
        
        // Send confirmation
        notificationService.sendOrderConfirmation(savedOrder);
        
        return savedOrder;
    }
    
    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        
        OrderStatus currentStatus = order.getStatus();
        
        // Validate status transition
        if (!isValidStatusTransition(currentStatus, newStatus)) {
            throw new RuntimeException(
                "Invalid status transition from " + currentStatus + " to " + newStatus
            );
        }
        
        order.setStatus(newStatus);
        
        if (newStatus == OrderStatus.SHIPPED) {
            order.setShippedAt(LocalDateTime.now());
        } else if (newStatus == OrderStatus.DELIVERED) {
            order.setDeliveredAt(LocalDateTime.now());
        } else if (newStatus == OrderStatus.CANCELLED) {
            // Release inventory
            for (OrderItem item : order.getItems()) {
                inventoryService.releaseStock(item.getProductId(), item.getQuantity());
            }
            notificationService.sendOrderCancellation(order);
        }
        
        return orderRepository.save(order);
    }
    
    @Transactional(readOnly = true)
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public Optional<Order> getOrderByNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber);
    }
    
    @Transactional(readOnly = true)
    public List<Order> getOrdersByCustomer(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }
    
    @Transactional(readOnly = true)
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }
    
    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    
    private String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private boolean isValidStatusTransition(OrderStatus from, OrderStatus to) {
        return switch (from) {
            case PENDING -> to == OrderStatus.CONFIRMED || to == OrderStatus.CANCELLED;
            case CONFIRMED -> to == OrderStatus.PAID || to == OrderStatus.CANCELLED;
            case PAID -> to == OrderStatus.PROCESSING || to == OrderStatus.CANCELLED;
            case PROCESSING -> to == OrderStatus.SHIPPED;
            case SHIPPED -> to == OrderStatus.DELIVERED;
            case DELIVERED, CANCELLED, REFUNDED -> false;
        };
    }
}
```

### Step 6: Create Resource DTOs

```java
package com.learning.order.resource;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public class CreateOrderRequest {
    
    @NotNull(message = "Customer ID is required")
    private Long customerId;
    
    @NotBlank(message = "Shipping address is required")
    private String shippingAddress;
    
    private String billingAddress;
    
    @NotBlank(message = "Payment method is required")
    private String paymentMethod;
    
    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<OrderItemRequest> items;
    
    // Getters and Setters
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    
    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    
    public String getBillingAddress() { return billingAddress; }
    public void setBillingAddress(String billingAddress) { this.billingAddress = billingAddress; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public List<OrderItemRequest> getItems() { return items; }
    public void setItems(List<OrderItemRequest> items) { this.items = items; }
}
```

```java
package com.learning.order.resource;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public class OrderItemRequest {
    
    @NotNull(message = "Product ID is required")
    private Long productId;
    
    @NotBlank(message = "Product name is required")
    private String productName;
    
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;
    
    @NotNull(message = "Unit price is required")
    @Positive(message = "Unit price must be positive")
    private BigDecimal unitPrice;
    
    // Getters and Setters
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
}
```

### Step 7: Create Order REST Resource

```java
package com.learning.order.resource;

import com.learning.order.entity.Order;
import com.learning.order.entity.OrderItem;
import com.learning.order.entity.OrderStatus;
import com.learning.order.service.OrderService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("/api/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {
    
    @Inject
    private OrderService orderService;
    
    @POST
    public Response createOrder(@Valid CreateOrderRequest request) {
        try {
            Order order = new Order();
            order.setCustomerId(request.getCustomerId());
            order.setShippingAddress(request.getShippingAddress());
            order.setBillingAddress(request.getBillingAddress());
            order.setPaymentMethod(request.getPaymentMethod());
            
            for (OrderItemRequest itemRequest : request.getItems()) {
                OrderItem item = new OrderItem();
                item.setProductId(itemRequest.getProductId());
                item.setProductName(itemRequest.getProductName());
                item.setQuantity(itemRequest.getQuantity());
                item.setUnitPrice(itemRequest.getUnitPrice());
                order.addItem(item);
            }
            
            Order created = orderService.createOrder(order);
            return Response.status(Response.Status.CREATED)
                .entity(created)
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(e.getMessage()))
                .build();
        }
    }
    
    @GET
    @Path("/{id}")
    public Response getOrder(@PathParam("id") Long id) {
        return orderService.getOrderById(id)
            .map(order -> Response.ok(order).build())
            .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }
    
    @GET
    @Path("/number/{orderNumber}")
    public Response getOrderByNumber(@PathParam("orderNumber") String orderNumber) {
        return orderService.getOrderByNumber(orderNumber)
            .map(order -> Response.ok(order).build())
            .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }
    
    @GET
    @Path("/customer/{customerId}")
    public Response getOrdersByCustomer(@PathParam("customerId") Long customerId) {
        List<Order> orders = orderService.getOrdersByCustomer(customerId);
        return Response.ok(orders).build();
    }
    
    @GET
    @Path("/status/{status}")
    public Response getOrdersByStatus(@PathParam("status") OrderStatus status) {
        List<Order> orders = orderService.getOrdersByStatus(status);
        return Response.ok(orders).build();
    }
    
    @GET
    public Response getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return Response.ok(orders).build();
    }
    
    @PATCH
    @Path("/{id}/status")
    public Response updateOrderStatus(
            @PathParam("id") Long id,
            @Valid UpdateStatusRequest request) {
        try {
            Order updated = orderService.updateOrderStatus(id, request.getStatus());
            return Response.ok(updated).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(e.getMessage()))
                .build();
        }
    }
    
    static class UpdateStatusRequest {
        private OrderStatus status;
        
        public OrderStatus getStatus() { return status; }
        public void setStatus(OrderStatus status) { this.status = status; }
    }
    
    static class ErrorResponse {
        private String error;
        
        public ErrorResponse() {}
        
        public ErrorResponse(String error) {
            this.error = error;
        }
        
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
    }
}
```

### Step 8: Configure Application

Create `src/main/resources/META-INF/microprofile-config.yaml`:

```yaml
mp:
  jwt:
    verify:
      publickey:
        location: publicKey.pem
      issuer: https://example.com/orders

server:
  port: 8080
  host: "0.0.0.0"

javax:
  datasource:
    jdbc:
      dataSourceClassName: org.h2.jdbcx.JdbcDataSource
      dataSource:
        url: "jdbc:h2:mem:orderdb"
        user: sa
        password: ""
```

Create `src/main/resources/META-INF/beans.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="https://jakarta.ee/xml/ns/jakartaee"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/beans_3_0.xsd"
       version="3.0"
       bean-discovery-mode="all">
</beans>
```

### Step 9: Run and Test

```bash
# Build project
cd helidon-order-system
mvn clean package

# Run application
java -jar target/helidon-order-system-1.0.0.jar

# Test API
# Create order
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "shippingAddress": "123 Main St, City, State 12345",
    "billingAddress": "123 Main St, City, State 12345",
    "paymentMethod": "CREDIT_CARD",
    "items": [
      {
        "productId": 101,
        "productName": "Laptop",
        "quantity": 1,
        "unitPrice": 999.99
      },
      {
        "productId": 102,
        "productName": "Mouse",
        "quantity": 2,
        "unitPrice": 29.99
      }
    ]
  }'

# Get order by ID
curl http://localhost:8080/api/orders/1

# Get orders by customer
curl http://localhost:8080/api/orders/customer/1

# Update status
curl -X PATCH http://localhost:8080/api/orders/1/status \
  -H "Content-Type: application/json" \
  -d '{"status":"CONFIRMED"}'
```

## Expected Output

The real-world project produces complete order processing system with:
- Order CRUD operations
- Inventory management
- Payment processing
- Status workflow automation
- Transaction support

## Build Instructions Summary

| Step | Command | Time |
|------|---------|------|
| Create project | Maven archetype | 15 min |
| Configure pom.xml | Edit dependencies | 10 min |
| Implement entities | JPA entities | 1 hour |
| Implement repository | Data access | 30 min |
| Implement services | Business logic | 2 hours |
| Implement REST API | JAX-RS resources | 1 hour |
| Configure application | YAML config | 15 min |
| Test integration | Manual testing | 3 hours |

Total estimated time: 8-10 hours