# 🎯 Mini-Project: Employee Management System

A complete command-line employee management system demonstrating Java basics.

## Project Overview

**Duration**: 2-3 hours  
**Difficulty**: Beginner  
**Concepts Used**: Variables, Data Types, Control Flow, Arrays, Methods, OOP

---

## Project Structure

```
01-java-basics/src/main/java/com/learning/project/
├── Main.java                 # Entry point
├── model/
│   └── Employee.java        # Employee data class
├── service/
│   └── EmployeeService.java # Business logic
├── util/
│   └── Validator.java      # Input validation
└── ui/
    └── Menu.java            # User interface
```

---

## Step 1: Create Employee Model

```java
// model/Employee.java
package com.learning.project.model;

public class Employee {
    private int id;
    private String name;
    private String department;
    private double salary;
    private String email;

    public Employee(int id, String name, String department, 
                     double salary, String email) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.salary = salary;
        this.email = email;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { 
        this.department = department; 
    }
    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return String.format("ID: %d | Name: %s | Dept: %s | Salary: $%.2f | Email: %s",
            id, name, department, salary, email);
    }
}
```

---

## Step 2: Create Validator Utility

```java
// util/Validator.java
package com.learning.project.util;

import java.util.regex.Pattern;

public class Validator {
    
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    public static boolean isValidId(int id) {
        return id > 0;
    }
    
    public static boolean isValidName(String name) {
        return name != null && name.length() >= 2 && name.length() <= 50;
    }
    
    public static boolean isValidSalary(double salary) {
        return salary >= 0 && salary <= 1000000;
    }
    
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    public static boolean isValidDepartment(String dept) {
        String[] validDepts = {"IT", "HR", "Finance", "Marketing", "Sales"};
        for (String d : validDepts) {
            if (d.equalsIgnoreCase(dept)) return true;
        }
        return false;
    }
}
```

---

## Step 3: Create Employee Service (Business Logic)

```java
// service/EmployeeService.java
package com.learning.project.service;

import com.learning.project.model.Employee;
import com.learning.project.util.Validator;
import java.util.ArrayList;
import java.util.List;

public class EmployeeService {
    private List<Employee> employees;
    private int nextId;

    public EmployeeService() {
        this.employees = new ArrayList<>();
        this.nextId = 1;
        // Add some sample data
        addSampleData();
    }

    private void addSampleData() {
        employees.add(new Employee(nextId++, "John Doe", "IT", 75000, "john@company.com"));
        employees.add(new Employee(nextId++, "Jane Smith", "HR", 65000, "jane@company.com"));
        employees.add(new Employee(nextId++, "Bob Wilson", "Finance", 80000, "bob@company.com"));
    }

    public boolean addEmployee(String name, String dept, double salary, String email) {
        // Validation
        if (!Validator.isValidName(name)) {
            System.out.println("Error: Invalid name!");
            return false;
        }
        if (!Validator.isValidDepartment(dept)) {
            System.out.println("Error: Invalid department!");
            return false;
        }
        if (!Validator.isValidSalary(salary)) {
            System.out.println("Error: Invalid salary!");
            return false;
        }
        if (!Validator.isValidEmail(email)) {
            System.out.println("Error: Invalid email!");
            return false;
        }

        employees.add(new Employee(nextId++, name, dept, salary, email));
        System.out.println("Employee added successfully!");
        return true;
    }

    public void displayAllEmployees() {
        if (employees.isEmpty()) {
            System.out.println("No employees found.");
            return;
        }
        System.out.println("\n=== All Employees ===");
        System.out.println("-".repeat(80));
        for (Employee emp : employees) {
            System.out.println(emp);
        }
    }

    public Employee findEmployeeById(int id) {
        for (Employee emp : employees) {
            if (emp.getId() == id) {
                return emp;
            }
        }
        return null;
    }

    public boolean updateEmployee(int id, String name, String dept, 
                                  double salary, String email) {
        Employee emp = findEmployeeById(id);
        if (emp == null) {
            System.out.println("Employee not found!");
            return false;
        }

        if (name != null) emp.setName(name);
        if (dept != null) emp.setDepartment(dept);
        if (salary > 0) emp.setSalary(salary);
        if (email != null) emp.setEmail(email);

        System.out.println("Employee updated successfully!");
        return true;
    }

    public boolean deleteEmployee(int id) {
        Employee emp = findEmployeeById(id);
        if (emp == null) {
            System.out.println("Employee not found!");
            return false;
        }
        employees.remove(emp);
        System.out.println("Employee deleted successfully!");
        return true;
    }

    public void displayByDepartment(String dept) {
        System.out.println("\n=== Employees in " + dept + " ===");
        for (Employee emp : employees) {
            if (emp.getDepartment().equalsIgnoreCase(dept)) {
                System.out.println(emp);
            }
        }
    }

    public void displayStats() {
        if (employees.isEmpty()) {
            System.out.println("No data to analyze.");
            return;
        }

        double totalSalary = 0;
        double minSalary = Double.MAX_VALUE;
        double maxSalary = 0;
        int count = employees.size();

        for (Employee emp : employees) {
            totalSalary += emp.getSalary();
            minSalary = Math.min(minSalary, emp.getSalary());
            maxSalary = Math.max(maxSalary, emp.getSalary());
        }

        System.out.println("\n=== Salary Statistics ===");
        System.out.println("Total Employees: " + count);
        System.out.printf("Average Salary: $%.2f%n", totalSalary / count);
        System.out.printf("Min Salary: $%.2f%n", minSalary);
        System.out.printf("Max Salary: $%.2f%n", maxSalary);
        System.out.printf("Total Salary Budget: $%.2f%n", totalSalary);
    }

    public int getCount() {
        return employees.size();
    }
}
```

---

## Step 4: Create Menu System

```java
// ui/Menu.java
package com.learning.project.ui;

import com.learning.project.service.EmployeeService;
import java.util.Scanner;

public class Menu {
    private Scanner scanner;
    private EmployeeService service;
    private boolean running;

    public Menu() {
        this.scanner = new Scanner(System.in);
        this.service = new EmployeeService();
        this.running = true;
    }

    public void start() {
        System.out.println("\n======================================");
        System.out.println("  EMPLOYEE MANAGEMENT SYSTEM");
        System.out.println("======================================");
        
        while (running) {
            displayMenu();
            int choice = getChoice();
            handleChoice(choice);
        }
    }

    private void displayMenu() {
        System.out.println("\n--- MAIN MENU ---");
        System.out.println("1. Add Employee");
        System.out.println("2. View All Employees");
        System.out.println("3. Search Employee by ID");
        System.out.println("4. Update Employee");
        System.out.println("5. Delete Employee");
        System.out.println("6. View by Department");
        System.out.println("7. View Salary Statistics");
        System.out.println("8. Exit");
        System.out.print("\nEnter your choice: ");
    }

    private int getChoice() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void handleChoice(int choice) {
        switch (choice) {
            case 1 -> addEmployee();
            case 2 -> service.displayAllEmployees();
            case 3 -> searchEmployee();
            case 4 -> updateEmployee();
            case 5 -> deleteEmployee();
            case 6 -> viewByDepartment();
            case 7 -> service.displayStats();
            case 8 -> {
                System.out.println("Goodbye!");
                running = false;
            }
            default -> System.out.println("Invalid choice. Please try again.");
        }
    }

    private void addEmployee() {
        System.out.println("\n--- Add New Employee ---");
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        
        System.out.print("Department (IT/HR/Finance/Marketing/Sales): ");
        String dept = scanner.nextLine().trim();
        
        System.out.print("Salary: ");
        double salary = 0;
        try {
            salary = Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid salary format!");
            return;
        }
        
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        
        service.addEmployee(name, dept, salary, email);
    }

    private void searchEmployee() {
        System.out.print("\nEnter Employee ID: ");
        int id = getIntInput();
        Employee emp = service.findEmployeeById(id);
        if (emp != null) {
            System.out.println("\n" + emp);
        } else {
            System.out.println("Employee not found!");
        }
    }

    private void updateEmployee() {
        System.out.print("\nEnter Employee ID to update: ");
        int id = getIntInput();
        
        System.out.print("New Name (press Enter to skip): ");
        String name = scanner.nextLine().trim();
        name = name.isEmpty() ? null : name;
        
        System.out.print("New Department (press Enter to skip): ");
        String dept = scanner.nextLine().trim();
        dept = dept.isEmpty() ? null : dept;
        
        System.out.print("New Salary (enter 0 to skip): ");
        double salary = getDoubleInput();
        
        System.out.print("New Email (press Enter to skip): ");
        String email = scanner.nextLine().trim();
        email = email.isEmpty() ? null : email;
        
        service.updateEmployee(id, name, dept, salary, email);
    }

    private void deleteEmployee() {
        System.out.print("\nEnter Employee ID to delete: ");
        int id = getIntInput();
        service.deleteEmployee(id);
    }

    private void viewByDepartment() {
        System.out.print("\nEnter Department: ");
        String dept = scanner.nextLine().trim();
        service.displayByDepartment(dept);
    }

    private int getIntInput() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private double getDoubleInput() {
        try {
            return Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
```

---

## Step 5: Main Entry Point

```java
// Main.java
package com.learning.project;

import com.learning.project.ui.Menu;

public class Main {
    public static void main(String[] args) {
        Menu menu = new Menu();
        menu.start();
    }
}
```

---

## Running the Project

```bash
# Compile
javac -d out -cp src src/com/learning/project/**/*.java src/com/learning/project/*.java

# Run
java -cp out com.learning.project.Main
```

---

## Sample Output

```
======================================
  EMPLOYEE MANAGEMENT SYSTEM
======================================

--- MAIN MENU ---
1. Add Employee
2. View All Employees
3. Search Employee by ID
4. Update Employee
5. Delete Employee
6. View by Department
7. View Salary Statistics
8. Exit

Enter your choice: 2

=== All Employees ==
--------------------------------------------------------------------------------
ID: 1 | Name: John Doe | Dept: IT | Salary: $75000.00 | Email: john@company.com
ID: 2 | Name: Jane Smith | Dept: HR | Salary: $65000.00 | Email: jane@company.com
ID: 3 | Name: Bob Wilson | Dept: Finance | Salary: $80000.00 | Email: bob@company.com
```

---

## Extensions (Try These!)

1. **Add sorting**: Sort employees by name, salary, or department
2. **File I/O**: Save/load employee data to file
3. **Search**: Add search by name or email
4. **Validation**: Add more validation rules
5. **Statistics**: Add department-wise statistics

---

## Concepts Demonstrated

| Concept | Where Used |
|---------|------------|
| **Variables** | All fields in Employee class |
| **Data Types** | int, String, double |
| **Control Flow** | switch in Menu.handleChoice() |
| **Arrays/Collections** | List<Employee> in EmployeeService |
| **Methods** | All service methods |
| **OOP** | Encapsulation in Employee class |
| **Exception Handling** | Input parsing in Menu |
| **Packages** | model, service, util, ui |

---

---

# 🚀 Real-World Project: Employee Management System with File I/O

## Project Overview

**Duration**: 10-15 hours  
**Difficulty**: Intermediate  
**Concepts Used**: File I/O, Object Serialization, Exception Handling, Collections, Menu System

This project extends the mini-project into a production-ready application with persistent storage, data export/import, and backup functionality.

---

## Project Structure

```
01-java-basics/src/main/java/com/learning/project/
├── Main.java
├── model/
│   ├── Employee.java
│   └── Department.java
├── service/
│   ├── EmployeeService.java
│   └── DepartmentService.java
├── repository/
│   └── EmployeeRepository.java
├── util/
│   ├── Validator.java
│   ├── FileManager.java
│   └── ExportManager.java
├── ui/
│   └── Menu.java
└── exception/
    └── DataOperationException.java
```

---

## Step 1: Custom Exception

```java
// exception/DataOperationException.java
package com.learning.project.exception;

public class DataOperationException extends Exception {
    private String operation;
    
    public DataOperationException(String message, String operation) {
        super(message);
        this.operation = operation;
    }
    
    public DataOperationException(String message, Throwable cause, String operation) {
        super(message, cause);
        this.operation = operation;
    }
    
    public String getOperation() {
        return operation;
    }
}
```

---

## Step 2: Department Model

```java
// model/Department.java
package com.learning.project.model;

public class Department {
    private String code;
    private String name;
    private String description;
    private int employeeCount;
    
    public Department(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.employeeCount = 0;
    }
    
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getEmployeeCount() { return employeeCount; }
    public void setEmployeeCount(int count) { this.employeeCount = count; }
    
    @Override
    public String toString() {
        return String.format("[%s] %s - %d employees", code, name, employeeCount);
    }
}
```

---

## Step 3: File Manager (Persistence)

```java
// util/FileManager.java
package com.learning.project.util;

import com.learning.project.model.Employee;
import com.learning.project.exception.DataOperationException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private static final String DATA_DIR = "data";
    private static final String EMPLOYEES_FILE = DATA_DIR + "/employees.dat";
    private static final String BACKUP_DIR = "backup";
    
    public FileManager() {
        createDirectories();
    }
    
    private void createDirectories() {
        new File(DATA_DIR).mkdirs();
        new File(BACKUP_DIR).mkdirs();
    }
    
    public void saveEmployees(List<Employee> employees) throws DataOperationException {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(EMPLOYEES_FILE))) {
            oos.writeObject(employees);
        } catch (IOException e) {
            throw new DataOperationException("Failed to save employees", "SAVE", e);
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<Employee> loadEmployees() throws DataOperationException {
        File file = new File(EMPLOYEES_FILE);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(file))) {
            return (List<Employee>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new DataOperationException("Failed to load employees", "LOAD", e);
        }
    }
    
    public void createBackup() throws DataOperationException {
        File source = new File(EMPLOYEES_FILE);
        if (!source.exists()) {
            throw new DataOperationException("No data to backup", "BACKUP");
        }
        
        String timestamp = java.time.LocalDateTime.now()
            .toString().replace(":", "-");
        String backupFile = BACKUP_DIR + "/employees_backup_" + timestamp + ".dat";
        
        try {
            java.nio.file.Files.copy(source.toPath(), 
                new File(backupFile).toPath());
        } catch (IOException e) {
            throw new DataOperationException("Failed to create backup", "BACKUP", e);
        }
    }
    
    public void restoreFromBackup(String backupFile) throws DataOperationException {
        try {
            java.nio.file.Files.copy(
                new File(backupFile).toPath(),
                new File(EMPLOYEES_FILE).toPath(),
                java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new DataOperationException("Failed to restore backup", "RESTORE", e);
        }
    }
    
    public List<String> listBackups() {
        List<String> backups = new ArrayList<>();
        File backupDir = new File(BACKUP_DIR);
        if (backupDir.exists()) {
            for (File f : backupDir.listFiles((d, name) -> name.endsWith(".dat"))) {
                backups.add(f.getName());
            }
        }
        return backups;
    }
}
```

---

## Step 4: Export Manager (CSV/JSON)

```java
// util/ExportManager.java
package com.learning.project.util;

import com.learning.project.model.Employee;
import com.learning.project.exception.DataOperationException;
import java.io.*;
import java.util.List;

public class ExportManager {
    
    public void exportToCSV(List<Employee> employees, String filename) 
            throws DataOperationException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("ID,Name,Department,Salary,Email");
            for (Employee emp : employees) {
                writer.printf("%d,%s,%s,%.2f,%s%n",
                    emp.getId(),
                    escapeCSV(emp.getName()),
                    emp.getDepartment(),
                    emp.getSalary(),
                    emp.getEmail());
            }
        } catch (IOException e) {
            throw new DataOperationException("Failed to export to CSV", "EXPORT_CSV", e);
        }
    }
    
    public void exportToHTML(List<Employee> employees, String filename) 
            throws DataOperationException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("<!DOCTYPE html>");
            writer.println("<html><head><title>Employee Report</title>");
            writer.println("<style>table{border-collapse:collapse;width:100%}");
            writer.println("th,td{border:1px solid #ddd;padding:8px;text-align:left}");
            writer.println("th{background-color:#4CAF50;color:white}</style>");
            writer.println("</head><body>");
            writer.println("<h1>Employee Report</h1>");
            writer.println("<table><tr><th>ID</th><th>Name</th><th>Department</th>");
            writer.println("<th>Salary</th><th>Email</th></tr>");
            for (Employee emp : employees) {
                writer.printf("<tr><td>%d</td><td>%s</td><td>%s</td>",
                    emp.getId(), emp.getName(), emp.getDepartment());
                writer.printf("<td>$%.2f</td><td>%s</td></tr>%n",
                    emp.getSalary(), emp.getEmail());
            }
            writer.println("</table></body></html>");
        } catch (IOException e) {
            throw new DataOperationException("Failed to export to HTML", "EXPORT_HTML", e);
        }
    }
    
    private String escapeCSV(String value) {
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
```

---

## Step 5: Repository Layer

```java
// repository/EmployeeRepository.java
package com.learning.project.repository;

import com.learning.project.model.Employee;
import com.learning.project.util.FileManager;
import com.learning.project.exception.DataOperationException;
import java.util.List;

public class EmployeeRepository {
    private final FileManager fileManager;
    private List<Employee> employees;
    private int nextId;
    
    public EmployeeRepository() {
        this.fileManager = new FileManager();
        this.employees = loadData();
        this.nextId = calculateNextId();
    }
    
    private List<Employee> loadData() {
        try {
            return fileManager.loadEmployees();
        } catch (DataOperationException e) {
            System.out.println("Warning: Could not load data, starting fresh");
            return new java.util.ArrayList<>();
        }
    }
    
    private int calculateNextId() {
        int maxId = 0;
        for (Employee emp : employees) {
            if (emp.getId() > maxId) maxId = emp.getId();
        }
        return maxId + 1;
    }
    
    public List<Employee> findAll() {
        return new java.util.ArrayList<>(employees);
    }
    
    public Employee findById(int id) {
        return employees.stream()
            .filter(e -> e.getId() == id)
            .findFirst()
            .orElse(null);
    }
    
    public int add(Employee employee) {
        employee.setId(nextId++);
        employees.add(employee);
        save();
        return employee.getId();
    }
    
    public boolean update(Employee employee) {
        for (int i = 0; i < employees.size(); i++) {
            if (employees.get(i).getId() == employee.getId()) {
                employees.set(i, employee);
                save();
                return true;
            }
        }
        return false;
    }
    
    public boolean delete(int id) {
        boolean removed = employees.removeIf(e -> e.getId() == id);
        if (removed) save();
        return removed;
    }
    
    public void save() {
        try {
            fileManager.saveEmployees(employees);
        } catch (DataOperationException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }
    
    public void createBackup() throws DataOperationException {
        fileManager.createBackup();
    }
    
    public List<String> getBackups() {
        return fileManager.listBackups();
    }
    
    public void restore(String backupFile) throws DataOperationException {
        fileManager.restoreFromBackup(backupFile);
        this.employees = loadData();
        this.nextId = calculateNextId();
    }
}
```

---

## Step 6: Enhanced Menu with All Features

```java
// ui/Menu.java
package com.learning.project.ui;

import com.learning.project.model.Employee;
import com.learning.project.repository.EmployeeRepository;
import com.learning.project.util.ExportManager;
import com.learning.project.exception.DataOperationException;
import java.util.Scanner;

public class Menu {
    private Scanner scanner;
    private EmployeeRepository repository;
    private ExportManager exportManager;
    private boolean running;
    
    public Menu() {
        this.scanner = new Scanner(System.in);
        this.repository = new EmployeeRepository();
        this.exportManager = new ExportManager();
        this.running = true;
    }
    
    public void start() {
        System.out.println("\n======================================");
        System.out.println("  EMPLOYEE MANAGEMENT SYSTEM v2.0");
        System.out.println("  (with File I/O & Data Persistence)");
        System.out.println("======================================");
        
        while (running) {
            displayMenu();
            int choice = getChoice();
            handleChoice(choice);
        }
    }
    
    private void displayMenu() {
        System.out.println("\n--- MAIN MENU ---");
        System.out.println("1. Add Employee");
        System.out.println("2. View All Employees");
        System.out.println("3. Search Employee by ID");
        System.out.println("4. Update Employee");
        System.out.println("5. Delete Employee");
        System.out.println("6. View by Department");
        System.out.println("7. View Salary Statistics");
        System.out.println("8. Export to CSV");
        System.out.println("9. Export to HTML");
        System.out.println("10. Create Backup");
        System.out.println("11. Restore from Backup");
        System.out.println("12. Exit");
        System.out.print("\nEnter your choice: ");
    }
    
    private int getChoice() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    private void handleChoice(int choice) {
        try {
            switch (choice) {
                case 1 -> addEmployee();
                case 2 -> viewAllEmployees();
                case 3 -> searchEmployee();
                case 4 -> updateEmployee();
                case 5 -> deleteEmployee();
                case 6 -> viewByDepartment();
                case 7 -> viewStatistics();
                case 8 -> exportCSV();
                case 9 -> exportHTML();
                case 10 -> createBackup();
                case 11 -> restoreBackup();
                case 12 -> { System.out.println("Goodbye!"); running = false; }
                default -> System.out.println("Invalid choice!");
            }
        } catch (DataOperationException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private void addEmployee() {
        System.out.println("\n--- Add New Employee ---");
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Department: ");
        String dept = scanner.nextLine().trim();
        System.out.print("Salary: ");
        double salary = getDouble();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        
        Employee emp = new Employee(0, name, dept, salary, email);
        int id = repository.add(emp);
        System.out.println("Employee added with ID: " + id);
    }
    
    private void viewAllEmployees() {
        var employees = repository.findAll();
        if (employees.isEmpty()) {
            System.out.println("No employees found.");
            return;
        }
        System.out.println("\n=== All Employees ===");
        for (Employee emp : employees) {
            System.out.println(emp);
        }
    }
    
    private void searchEmployee() {
        System.out.print("\nEnter Employee ID: ");
        int id = getIntInput();
        Employee emp = repository.findById(id);
        if (emp != null) {
            System.out.println(emp);
        } else {
            System.out.println("Employee not found!");
        }
    }
    
    private void updateEmployee() {
        System.out.print("\nEnter Employee ID: ");
        int id = getIntInput();
        Employee emp = repository.findById(id);
        if (emp == null) {
            System.out.println("Employee not found!");
            return;
        }
        
        System.out.println("Current: " + emp);
        System.out.print("New Name (Enter to skip): ");
        String name = scanner.nextLine().trim();
        if (!name.isEmpty()) emp.setName(name);
        
        System.out.print("New Department (Enter to skip): ");
        String dept = scanner.nextLine().trim();
        if (!dept.isEmpty()) emp.setDepartment(dept);
        
        System.out.print("New Salary (0 to skip): ");
        double salary = getDouble();
        if (salary > 0) emp.setSalary(salary);
        
        System.out.print("New Email (Enter to skip): ");
        String email = scanner.nextLine().trim();
        if (!email.isEmpty()) emp.setEmail(email);
        
        repository.update(emp);
        System.out.println("Employee updated!");
    }
    
    private void deleteEmployee() {
        System.out.print("\nEnter Employee ID: ");
        int id = getIntInput();
        if (repository.delete(id)) {
            System.out.println("Employee deleted!");
        } else {
            System.out.println("Employee not found!");
        }
    }
    
    private void viewByDepartment() {
        System.out.print("\nEnter Department: ");
        String dept = scanner.nextLine().trim();
        System.out.println("\n=== Employees in " + dept + " ===");
        for (Employee emp : repository.findAll()) {
            if (emp.getDepartment().equalsIgnoreCase(dept)) {
                System.out.println(emp);
            }
        }
    }
    
    private void viewStatistics() {
        var employees = repository.findAll();
        if (employees.isEmpty()) {
            System.out.println("No data.");
            return;
        }
        
        double total = 0, min = Double.MAX_VALUE, max = 0;
        for (Employee emp : employees) {
            total += emp.getSalary();
            min = Math.min(min, emp.getSalary());
            max = Math.max(max, emp.getSalary());
        }
        
        System.out.println("\n=== Salary Statistics ===");
        System.out.println("Total Employees: " + employees.size());
        System.out.printf("Average Salary: $%.2f%n", total / employees.size());
        System.out.printf("Min Salary: $%.2f%n", min);
        System.out.printf("Max Salary: $%.2f%n", max);
        System.out.printf("Total Budget: $%.2f%n", total);
    }
    
    private void exportCSV() throws DataOperationException {
        String filename = "employees_export.csv";
        exportManager.exportToCSV(repository.findAll(), filename);
        System.out.println("Exported to " + filename);
    }
    
    private void exportHTML() throws DataOperationException {
        String filename = "employees_report.html";
        exportManager.exportToHTML(repository.findAll(), filename);
        System.out.println("Exported to " + filename);
    }
    
    private void createBackup() throws DataOperationException {
        repository.createBackup();
        System.out.println("Backup created successfully!");
    }
    
    private void restoreBackup() {
        var backups = repository.getBackups();
        if (backups.isEmpty()) {
            System.out.println("No backups found.");
            return;
        }
        
        System.out.println("\nAvailable backups:");
        for (int i = 0; i < backups.size(); i++) {
            System.out.println((i + 1) + ". " + backups.get(i));
        }
        System.out.print("Select backup number: ");
        int choice = getIntInput();
        
        if (choice > 0 && choice <= backups.size()) {
            try {
                repository.restore(backups.get(choice - 1));
                System.out.println("Restored from backup!");
            } catch (DataOperationException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
    
    private int getIntInput() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    private double getDouble() {
        try {
            return Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    public static void main(String[] args) {
        new Menu().start();
    }
}
```

---

## Running the Project

```bash
cd 01-java-basics

# Compile
javac -d out -sourcepath src src/com/learning/project/**/*.java

# Run
java -cp out com.learning.project.ui.Menu
```

---

## Sample Output

```
======================================
  EMPLOYEE MANAGEMENT SYSTEM v2.0
  (with File I/O & Data Persistence)
======================================

--- MAIN MENU ---
1. Add Employee
...
Enter your choice: 8
Exported to employees_export.csv
```

---

## Concepts Demonstrated

| Concept | Implementation |
|---------|----------------|
| **File I/O** | Object serialization, text file handling |
| **Exception Handling** | Custom exceptions, try-with-resources |
| **Collections** | List, ArrayList, streams |
| **Data Persistence** | Save/load to disk |
| **Backup/Restore** | Timestamp-based backups |
| **Export** | CSV and HTML generation |

---

## Extensions

1. Add JSON export using manual string building
2. Implement data encryption for sensitive files
3. Add search by name using string matching
4. Add pagination for large datasets
5. Add user authentication

---

## Next Steps

After completing this project, proceed to:
- **Module 2 (OOP)**: Add inheritance - create Manager subclass
- **Module 3 (Collections)**: Use HashMap for faster lookup by ID
- **Module 4 (Streams)**: Refactor using stream operations