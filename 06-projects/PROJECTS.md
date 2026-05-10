# General Java Projects

This directory contains general Java projects demonstrating various programming concepts, utilities, and applications. These projects help strengthen Java fundamentals and build practical development skills.

## Mini-Project: File Processing Utility (2-4 hours)

### Overview

Build a comprehensive file processing utility that demonstrates Java I/O, concurrency, and design patterns. This project processes CSV files, generates reports, and performs batch operations with proper error handling.

### Project Structure

```
file-processing-utility/
├── pom.xml
└── src/
    └── main/
        └── java/
            └── com/
                └── projects/
                    ├── FileProcessorApplication.java
                    ├── processor/
                    │   ├── CsvProcessor.java
                    │   ├── FileProcessor.java
                    │   └── ReportGenerator.java
                    ├── model/
                    │   ├── Transaction.java
                    │   └── ProcessingResult.java
                    ├── util/
                    │   ├── FileWatcher.java
                    │   └── CsvParser.java
                    └── service/
                        └── ProcessingService.java
```

### Implementation

```java
package com.projects;

import com.projects.processor.CsvProcessor;
import com.projects.service.ProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class FileProcessorApplication {
    
    private static final Logger logger = LoggerFactory.getLogger(FileProcessorApplication.class);

    public static void main(String[] args) {
        logger.info("Starting File Processing Utility");
        
        if (args.length < 1) {
            printUsage();
            System.exit(1);
        }
        
        String command = args[0];
        
        try {
            switch (command) {
                case "process" -> processFile(args);
                case "batch" -> batchProcess(args);
                case "watch" -> watchDirectory(args);
                case "report" -> generateReport(args);
                default -> {
                    printUsage();
                    System.exit(1);
                }
            }
        } catch (Exception e) {
            logger.error("Error executing command: {}", e.getMessage(), e);
            System.exit(1);
        }
    }

    private static void processFile(String[] args) {
        if (args.length < 2) {
            logger.error("Missing file path");
            return;
        }
        
        String filePath = args[1];
        CsvProcessor processor = new CsvProcessor();
        
        try {
            var result = processor.processFile(Paths.get(filePath));
            logger.info("Processed {} records successfully", result.getSuccessCount());
            logger.info("Failed: {}, Warnings: {}", result.getErrorCount(), result.getWarningCount());
            
            if (result.getErrors().isEmpty() == false) {
                result.getErrors().forEach(e -> logger.error("Error: {}", e));
            }
        } catch (Exception e) {
            logger.error("Failed to process file: {}", e.getMessage(), e);
        }
    }

    private static void batchProcess(String[] args) {
        if (args.length < 2) {
            logger.error("Missing directory path");
            return;
        }
        
        String directory = args[1];
        ProcessingService service = new ProcessingService();
        
        File dir = new File(directory);
        if (!dir.isDirectory()) {
            logger.error("Not a valid directory: {}", directory);
            return;
        }
        
        File[] files = dir.listFiles((d, name) -> name.endsWith(".csv"));
        
        if (files != null) {
            logger.info("Found {} CSV files to process", files.length);
            Arrays.stream(files).forEach(f -> {
                try {
                    service.processFile(f.toPath());
                } catch (Exception e) {
                    logger.error("Failed to process {}: {}", f.getName(), e.getMessage());
                }
            });
        }
    }

    private static void watchDirectory(String[] args) {
        if (args.length < 2) {
            logger.error("Missing directory path");
            return;
        }
        
        String directory = args[1];
        ProcessingService service = new ProcessingService();
        
        logger.info("Watching directory: {}", directory);
        
        try {
            service.watchDirectory(Paths.get(directory));
        } catch (InterruptedException e) {
            logger.info("Watch interrupted");
        }
    }

    private static void generateReport(String[] args) {
        if (args.length < 2) {
            logger.error("Missing file path");
            return;
        }
        
        String filePath = args[1];
        CsvProcessor processor = new CsvProcessor();
        
        try {
            var result = processor.processFile(Paths.get(filePath));
            processor.generateReport(result, filePath + ".report.txt");
            logger.info("Report generated: {}.report.txt", filePath);
        } catch (Exception e) {
            logger.error("Failed to generate report: {}", e.getMessage(), e);
        }
    }

    private static void printUsage() {
        System.out.println("""
            File Processing Utility
            ========================
            
            Usage:
              java -jar file-processor.jar <command> [arguments]
            
            Commands:
              process <file>    Process a single CSV file
              batch <dir>      Process all CSV files in directory
              watch <dir>      Watch directory for new files
              report <file>    Generate report for processed file
            
            Example:
              java -jar file-processor.jar process data/transactions.csv
            """);
    }
}
```

```java
package com.projects.processor;

import com.projects.model.ProcessingResult;
import com.projects.model.Transaction;
import com.projects.util.CsvParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CsvProcessor {
    
    private static final Logger logger = LoggerFactory.getLogger(CsvProcessor.class);
    
    private final CsvParser csvParser;
    private final ReportGenerator reportGenerator;

    public CsvProcessor() {
        this.csvParser = new CsvParser();
        this.reportGenerator = new ReportGenerator();
    }

    public ProcessingResult processFile(Path filePath) {
        ProcessingResult result = new ProcessingResult();
        
        try {
            logger.info("Processing file: {}", filePath);
            
            List<String> lines = Files.readAllLines(filePath);
            
            if (lines.isEmpty()) {
                result.addError("File is empty");
                return result;
            }
            
            String header = lines.get(0);
            if (!validateHeader(header)) {
                result.addError("Invalid header format");
                return result;
            }
            
            for (int i = 1; i < lines.size(); i++) {
                try {
                    String line = lines.get(i);
                    Transaction transaction = csvParser.parseLine(line, header);
                    
                    if (validateTransaction(transaction)) {
                        result.addSuccess(transaction);
                    } else {
                        result.addWarning("Invalid transaction at line " + (i + 1));
                    }
                } catch (Exception e) {
                    result.addError("Error at line " + (i + 1) + ": " + e.getMessage());
                }
            }
            
            logger.info("Processing complete. Success: {}, Errors: {}, Warnings: {}",
                result.getSuccessCount(), result.getErrorCount(), result.getWarningCount());
            
        } catch (IOException e) {
            result.addError("Failed to read file: " + e.getMessage());
            logger.error("Failed to process file: {}", e.getMessage(), e);
        }
        
        return result;
    }

    private boolean validateHeader(String header) {
        String[] columns = header.split(",");
        return columns.length >= 5 && 
               columns[0].toLowerCase().contains("id") &&
               columns[1].toLowerCase().contains("amount");
    }

    private boolean validateTransaction(Transaction transaction) {
        return transaction != null &&
               transaction.getId() != null &&
               transaction.getAmount() != null &&
               transaction.getAmount().doubleValue() > 0;
    }

    public void generateReport(ProcessingResult result, String outputPath) {
        reportGenerator.generateReport(result, outputPath);
    }
}
```

```java
package com.projects.processor;

import com.projects.model.ProcessingResult;
import com.projects.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReportGenerator {
    
    private static final Logger logger = LoggerFactory.getLogger(ReportGenerator.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void generateReport(ProcessingResult result, String outputPath) {
        try (BufferedWriter writer = Files.newBufferedWriter(Path.of(outputPath))) {
            writer.write("=".repeat(60));
            writer.newLine();
            writer.write("Processing Report");
            writer.newLine();
            writer.write("=".repeat(60));
            writer.newLine();
            writer.newLine();
            
            writer.write("Generated: " + LocalDateTime.now().format(formatter));
            writer.newLine();
            writer.newLine();
            
            writer.write("Summary:");
            writer.newLine();
            writer.write("-".repeat(40));
            writer.newLine();
            writer.write(String.format("  Total Processed: %d%n", result.getTotalProcessed()));
            writer.write(String.format("  Successful: %d%n", result.getSuccessCount()));
            writer.write(String.format("  Errors: %d%n", result.getErrorCount()));
            writer.write(String.format("  Warnings: %d%n", result.getWarningCount()));
            writer.newLine();
            
            if (!result.getSuccessRecords().isEmpty()) {
                writer.write("Top Transactions by Amount:");
                writer.newLine();
                writer.write("-".repeat(40));
                writer.newLine();
                
                result.getSuccessRecords().stream()
                    .sorted((a, b) -> b.getAmount().compareTo(a.getAmount()))
                    .limit(10)
                    .forEach(t -> writer.write(String.format("  %s: $%.2f%n", 
                        t.getId(), t.getAmount())));
                writer.newLine();
            }
            
            if (!result.getErrors().isEmpty()) {
                writer.write("Errors:");
                writer.newLine();
                writer.write("-".repeat(40));
                writer.newLine();
                result.getErrors().forEach(e -> {
                    try {
                        writer.write("  - " + e);
                        writer.newLine();
                    } catch (IOException ex) {
                        logger.error("Failed to write error: {}", ex.getMessage());
                    }
                });
            }
            
            logger.info("Report generated: {}", outputPath);
            
        } catch (IOException e) {
            logger.error("Failed to generate report: {}", e.getMessage(), e);
        }
    }
}
```

```java
package com.projects.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Transaction {
    
    private String id;
    private LocalDate date;
    private BigDecimal amount;
    private String category;
    private String description;
    private String status;
    private String accountId;

    public Transaction() {
    }

    public Transaction(String id, BigDecimal amount, String category) {
        this.id = id;
        this.amount = amount;
        this.category = category;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
}
```

```java
package com.projects.model;

import java.util.ArrayList;
import java.util.List;

public class ProcessingResult {
    
    private final List<Transaction> successRecords = new ArrayList<>();
    private final List<String> errors = new ArrayList<>();
    private final List<String> warnings = new ArrayList<>();
    private final List<Transaction> failedRecords = new ArrayList<>();

    public void addSuccess(Transaction transaction) {
        successRecords.add(transaction);
    }

    public void addError(String error) {
        errors.add(error);
    }

    public void addWarning(String warning) {
        warnings.add(warning);
    }

    public void addFailed(Transaction transaction) {
        failedRecords.add(transaction);
    }

    public int getSuccessCount() { return successRecords.size(); }
    public int getErrorCount() { return errors.size(); }
    public int getWarningCount() { return warnings.size(); }
    public int getTotalProcessed() { return successCount() + errorCount() + warnings.size(); }

    public List<Transaction> getSuccessRecords() { return successRecords; }
    public List<String> getErrors() { return errors; }
    public List<String> getWarnings() { return warnings; }
    public List<Transaction> getFailedRecords() { return failedRecords; }
}
```

```java
package com.projects.util;

import com.projects.model.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CsvParser {
    
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Transaction parseLine(String line, String header) {
        String[] values = splitCsvLine(line);
        String[] headers = header.split(",");
        
        if (values.length != headers.length) {
            throw new IllegalArgumentException("Column count mismatch");
        }
        
        Transaction transaction = new Transaction();
        
        for (int i = 0; i < headers.length; i++) {
            String headerName = headers[i].trim().toLowerCase();
            String value = values[i].trim();
            
            switch (headerName) {
                case "id", "transaction_id" -> transaction.setId(value);
                case "date", "transaction_date" -> transaction.setDate(parseDate(value));
                case "amount" -> transaction.setAmount(new BigDecimal(value));
                case "category", "type" -> transaction.setCategory(value);
                case "description", "memo" -> transaction.setDescription(value);
                case "status" -> transaction.setStatus(value);
                case "account", "account_id" -> transaction.setAccountId(value);
            }
        }
        
        return transaction;
    }

    private LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr, DATE_FORMAT);
        } catch (Exception e) {
            return LocalDate.now();
        }
    }

    private String[] splitCsvLine(String line) {
        // Handle quoted values and commas within quotes
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        
        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        result.add(current.toString());
        
        return result.toArray(new String[0]);
    }
}
```

```java
package com.projects.service;

import com.projects.processor.CsvProcessor;
import com.projects.model.ProcessingResult;

import java.nio.file.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProcessingService {
    
    private final CsvProcessor processor;
    private final ExecutorService executor;

    public ProcessingService() {
        this.processor = new CsvProcessor();
        this.executor = Executors.newFixedThreadPool(4);
    }

    public ProcessingResult processFile(Path filePath) {
        return processor.processFile(filePath);
    }

    public void processFilesAsync(java.util.List<Path> files, java.util.function.Consumer<ProcessingResult> callback) {
        files.forEach(file -> executor.submit(() -> {
            try {
                var result = processor.processFile(file);
                callback.accept(result);
            } catch (Exception e) {
                System.err.println("Failed to process: " + file + " - " + e.getMessage());
            }
        }));
    }

    public void watchDirectory(Path directory) throws InterruptedException {
        WatchService watchService = FileSystems.getDefault().newWatchService();
        directory.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
        
        System.out.println("Watching " + directory + " for new files...");
        
        while (true) {
            WatchKey key = watchService.take();
            
            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();
                
                if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                    Path filePath = (Path) event.context();
                    if (filePath.toString().endsWith(".csv")) {
                        System.out.println("New file detected: " + filePath);
                        processFile(directory.resolve(filePath));
                    }
                }
            }
            
            key.reset();
        }
    }

    public void shutdown() {
        executor.shutdown();
    }
}
```

### pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.projects</groupId>
    <artifactId>file-processing-utility</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>2.0.9</version>
        </dependency>
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
            <version>1.4.11</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <version>3.3.0</version>
                <configuration>
                    <archive>
                        <manifest>
                            <mainClass>com.projects.FileProcessorApplication</mainClass>
                        </manifest>
                    </archive>
                </configuration>
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
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

### Build and Run

```bash
cd file-processing-utility
mvn clean package

# Process single file
java -jar target/file-processing-utility-1.0.0.jar process data/sample.csv

# Batch process
java -jar target/file-processing-utility-1.0.0.jar batch data/

# Watch directory
java -jar target/file-processing-utility-1.0.0.jar watch data/

# Generate report
java -jar target/file-processing-utility-1.0.0.jar report data/sample.csv
```

---

## Real-World Project: Inventory Management System (8+ hours)

### Overview

Build a comprehensive inventory management system demonstrating enterprise Java patterns including layered architecture, transaction management, concurrent processing, REST API design, and database integration. This system handles warehouse operations, stock tracking, and reporting.

### Architecture

```
inventory-management/
├── src/main/java/
│   ├── com/inventory/
│   │   ├── InventoryManagementApplication.java
│   │   ├── api/
│   │   │   ├── ProductController.java
│   │   │   ├── WarehouseController.java
│   │   │   └── ReportController.java
│   │   ├── service/
│   │   │   ├── ProductService.java
│   │   │   ├── WarehouseService.java
│   │   │   ├── StockService.java
│   │   │   └── ReportService.java
│   │   ├── repository/
│   │   │   ├── ProductRepository.java
│   │   │   ├── WarehouseRepository.java
│   │   │   └── StockRepository.java
│   │   ├── entity/
│   │   │   ├── Product.java
│   │   │   ├── Warehouse.java
│   │   │   ├── Stock.java
│   │   │   └── StockMovement.java
│   │   └── dto/
│   │       ├── ProductRequest.java
│   │       └── StockMovementRequest.java
```

### Implementation

```java
package com.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
@EnableScheduling
public class InventoryManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(InventoryManagementApplication.class, args);
    }
}
```

```java
package com.inventory.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String sku;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    @NotNull
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Stock> stocks = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    private List<StockMovement> movements = new ArrayList<>();

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "reorder_level")
    private Integer reorderLevel = 10;

    @Column(name = "reorder_quantity")
    private Integer reorderQuantity = 50;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    public Product() {
    }

    public Product(String sku, String name, BigDecimal price) {
        this.sku = sku;
        this.name = name;
        this.price = price;
    }

    public Integer getTotalStock() {
        return stocks.stream()
            .mapToInt(Stock::getQuantity)
            .sum();
    }

    public boolean isLowStock() {
        return getTotalStock() <= reorderLevel;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public List<Stock> getStocks() { return stocks; }
    public void setStocks(List<Stock> stocks) { this.stocks = stocks; }

    public List<StockMovement> getMovements() { return movements; }
    public void setMovements(List<StockMovement> movements) { this.movements = movements; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Integer getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(Integer reorderLevel) { this.reorderLevel = reorderLevel; }

    public Integer getReorderQuantity() { return reorderQuantity; }
    public void setReorderQuantity(Integer reorderQuantity) { this.reorderQuantity = reorderQuantity; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
```

```java
package com.inventory.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "warehouses")
public class Warehouse {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String location;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "is_active")
    private boolean active = true;

    @OneToMany(mappedBy = "warehouse")
    private java.util.List<Stock> stocks;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Warehouse() {
    }

    public Warehouse(String name, String location, String address) {
        this.name = name;
        this.location = location;
        this.address = address;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public java.util.List<Stock> getStocks() { return stocks; }
    public void setStocks(java.util.List<Stock> stocks) { this.stocks = stocks; }
}
```

```java
package com.inventory.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stocks")
public class Stock {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "reserved_quantity")
    private Integer reservedQuantity = 0;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    public Stock() {
    }

    public Stock(Product product, Warehouse warehouse, Integer quantity) {
        this.product = product;
        this.warehouse = warehouse;
        this.quantity = quantity;
        this.lastUpdated = LocalDateTime.now();
    }

    public Integer getAvailableQuantity() {
        return quantity - reservedQuantity;
    }

    public void reserve(Integer qty) {
        if (getAvailableQuantity() >= qty) {
            reservedQuantity += qty;
            lastUpdated = LocalDateTime.now();
        }
    }

    public void release(Integer qty) {
        reservedQuantity = Math.max(0, reservedQuantity - qty);
        lastUpdated = LocalDateTime.now();
    }

    public void add(Integer qty) {
        quantity += qty;
        lastUpdated = LocalDateTime.now();
    }

    public void remove(Integer qty) {
        quantity = Math.max(0, quantity - qty);
        lastUpdated = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Warehouse getWarehouse() { return warehouse; }
    public void setWarehouse(Warehouse warehouse) { this.warehouse = warehouse; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { 
        this.quantity = quantity;
        this.lastUpdated = LocalDateTime.now();
    }

    public Integer getReservedQuantity() { return reservedQuantity; }
    public void setReservedQuantity(Integer reservedQuantity) { this.reservedQuantity = reservedQuantity; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}
```

```java
package com.inventory.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_movements")
public class StockMovement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_warehouse_id")
    private Warehouse fromWarehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_warehouse_id")
    private Warehouse toWarehouse;

    @Column(nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovementType type;

    @Column(length = 500)
    private String reason;

    @Column(name = "reference_number")
    private String referenceNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public StockMovement() {
    }

    public StockMovement(Product product, Integer quantity, MovementType type) {
        this.product = product;
        this.quantity = quantity;
        this.type = type;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Warehouse getFromWarehouse() { return fromWarehouse; }
    public void setFromWarehouse(Warehouse fromWarehouse) { this.fromWarehouse = fromWarehouse; }

    public Warehouse getToWarehouse() { return toWarehouse; }
    public void setToWarehouse(Warehouse toWarehouse) { this.toWarehouse = toWarehouse; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public MovementType getType() { return type; }
    public void setType(MovementType type) { this.type = type; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }

    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}

enum MovementType {
    PURCHASE, SALE, RETURN, ADJUSTMENT, TRANSFER, DAMAGED, EXPIRED
}

@Entity
class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String username;
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}

@Entity
class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
```

```java
package com.inventory.service;

import com.inventory.entity.*;
import com.inventory.repository.*;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StockService {
    
    private static final Logger logger = LoggerFactory.getLogger(StockService.class);
    
    private final StockRepository stockRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final StockMovementRepository movementRepository;
    private final NotificationService notificationService;

    public StockService(StockRepository stockRepository,
                       ProductRepository productRepository,
                       WarehouseRepository warehouseRepository,
                       StockMovementRepository movementRepository,
                       NotificationService notificationService) {
        this.stockRepository = stockRepository;
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
        this.movementRepository = movementRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public StockMovement adjustStock(Long productId, Long warehouseId, 
            Integer quantity, String reason, MovementType type) {
        
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
            .orElseThrow(() -> new RuntimeException("Warehouse not found"));
        
        Stock stock = stockRepository.findByProductIdAndWarehouseId(productId, warehouseId)
            .orElseGet(() -> {
                Stock newStock = new Stock(product, warehouse, 0);
                return stockRepository.save(newStock);
            });
        
        if (type == MovementType.PURCHASE || type == MovementType.RETURN) {
            stock.add(quantity);
        } else if (type == MovementType.SALE || type == MovementType.DAMAGED) {
            if (stock.getAvailableQuantity() < quantity) {
                throw new RuntimeException("Insufficient stock");
            }
            stock.remove(quantity);
        }
        
        stockRepository.save(stock);
        
        StockMovement movement = new StockMovement(product, quantity, type);
        movement.setFromWarehouse(type == MovementType.SALE ? warehouse : null);
        movement.setToWarehouse(type == MovementType.PURCHASE ? warehouse : null);
        movement.setReason(reason);
        movement.setWarehouse(warehouse);
        movementRepository.save(movement);
        
        logger.info("Stock adjusted: {} {} for product {} in warehouse {}", 
            type, quantity, product.getSku(), warehouse.getName());
        
        if (stock.getAvailableQuantity() <= product.getReorderLevel()) {
            notificationService.sendLowStockAlert(product, warehouse, stock.getAvailableQuantity());
        }
        
        return movement;
    }

    @Transactional
    public StockMovement transferStock(Long productId, Long fromWarehouseId, 
            Long toWarehouseId, Integer quantity) {
        
        Stock fromStock = stockRepository.findByProductIdAndWarehouseId(productId, fromWarehouseId)
            .orElseThrow(() -> new RuntimeException("Source stock not found"));
        
        if (fromStock.getAvailableQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock for transfer");
        }
        
        fromStock.remove(quantity);
        stockRepository.save(fromStock);
        
        Stock toStock = stockRepository.findByProductIdAndWarehouseId(productId, toWarehouseId)
            .orElseGet(() -> {
                Product product = fromStock.getProduct();
                Warehouse toWarehouse = warehouseRepository.findById(toWarehouseId)
                    .orElseThrow(() -> new RuntimeException("Destination warehouse not found"));
                Stock newStock = new Stock(product, toWarehouse, 0);
                return stockRepository.save(newStock);
            });
        
        toStock.add(quantity);
        stockRepository.save(toStock);
        
        StockMovement movement = new StockMovement(
            fromStock.getProduct(), quantity, MovementType.TRANSFER);
        movement.setFromWarehouse(fromStock.getWarehouse());
        movement.setToWarehouse(toStock.getWarehouse());
        movementRepository.save(movement);
        
        logger.info("Transferred {} units of product {} from warehouse {} to warehouse {}",
            quantity, fromStock.getProduct().getSku(), 
            fromStock.getWarehouse().getName(), toStock.getWarehouse().getName());
        
        return movement;
    }

    public Optional<Stock> getStock(Long productId, Long warehouseId) {
        return stockRepository.findByProductIdAndWarehouseId(productId, warehouseId);
    }

    public List<Stock> getProductStocks(Long productId) {
        return stockRepository.findByProductId(productId);
    }

    public List<Stock> getWarehouseStocks(Long warehouseId) {
        return stockRepository.findByWarehouseId(warehouseId);
    }

    public List<Stock> getLowStockProducts(Integer threshold) {
        return stockRepository.findLowStockProducts(threshold);
    }

    public boolean reserveStock(Long productId, Long warehouseId, Integer quantity) {
        return stockRepository.findByProductIdAndWarehouseId(productId, warehouseId)
            .map(stock -> {
                stock.reserve(quantity);
                stockRepository.save(stock);
                return true;
            })
            .orElse(false);
    }

    public void releaseStock(Long productId, Long warehouseId, Integer quantity) {
        stockRepository.findByProductIdAndWarehouseId(productId, warehouseId)
            .ifPresent(stock -> {
                stock.release(quantity);
                stockRepository.save(stock);
            });
    }
}
```

```java
package com.inventory.service;

import com.inventory.entity.Stock;
import com.inventory.entity.StockMovement;
import com.inventory.entity.Product;
import com.inventory.repository.StockRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportService {
    
    private final StockRepository stockRepository;

    public ReportService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    public long getTotalStockValue() {
        return stockRepository.findAll().stream()
            .mapToLong(stock -> 
                stock.getQuantity() * stock.getProduct().getPrice().longValue())
            .sum();
    }

    public List<Stock> getLowStockItems(Integer threshold) {
        return stockRepository.findLowStockProducts(threshold);
    }

    public StockSummary getStockSummary(Long warehouseId) {
        List<Stock> stocks = warehouseId != null 
            ? stockRepository.findByWarehouseId(warehouseId)
            : stockRepository.findAll();
        
        int totalProducts = (int) stocks.stream()
            .map(s -> s.getProduct().getId())
            .distinct()
            .count();
        
        int totalQuantity = stocks.stream()
            .mapToInt(Stock::getQuantity)
            .sum();
        
        int lowStockCount = (int) stocks.stream()
            .filter(s -> s.getQuantity() <= s.getProduct().getReorderLevel())
            .count();
        
        return new StockSummary(totalProducts, totalQuantity, lowStockCount);
    }

    @Scheduled(cron = "0 0 2 * * ?")
    public void generateDailyReport() {
        System.out.println("Generating daily inventory report...");
        var summary = getStockSummary(null);
        System.out.println("Total Products: " + summary.totalProducts());
        System.out.println("Total Quantity: " + summary.totalQuantity());
        System.out.println("Low Stock Items: " + summary.lowStockCount());
    }
}

record StockSummary(int totalProducts, int totalQuantity, int lowStockCount) {}

interface StockRepository extends org.springframework.data.jpa.repository.JpaRepository<Stock, Long> {
    java.util.Optional<Stock> findByProductIdAndWarehouseId(Long productId, Long warehouseId);
    java.util.List<Stock> findByProductId(Long productId);
    java.util.List<Stock> findByWarehouseId(Long warehouseId);
    @Query("SELECT s FROM Stock s WHERE s.quantity <= s.product.reorderLevel")
    java.util.List<Stock> findLowStockProducts(Integer threshold);
}

interface StockMovementRepository extends org.springframework.data.jpa.repository.JpaRepository<StockMovement, Long> {}

interface ProductRepository extends org.springframework.data.jpa.repository.JpaRepository<Product, Long> {}

interface WarehouseRepository extends org.springframework.data.jpa.repository.JpaRepository<Warehouse, Long> {}

interface NotificationService {
    void sendLowStockAlert(Product product, Warehouse warehouse, Integer currentStock);
}

@Service
class NotificationServiceImpl implements NotificationService {
    @Override
    public void sendLowStockAlert(Product product, Warehouse warehouse, Integer currentStock) {
        System.out.println("ALERT: Low stock for product " + product.getSku() + 
            " in warehouse " + warehouse.getName() + ". Current stock: " + currentStock);
    }
}
```

### application.yml

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:h2:mem:inventory
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
  h2:
    console:
      enabled: true

logging:
  level:
    com.inventory: DEBUG
```

### pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>

    <groupId>com.projects</groupId>
    <artifactId>inventory-management</artifactId>
    <version>1.0.0</version>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
        </dependency>
    </dependencies>
</project>
```

### Build and Run

```bash
cd inventory-management
mvn clean compile
mvn spring-boot:run
```

---

## Additional Learning Resources

- Java I/O: https://docs.oracle.com/javase/tutorial/essential/io/
- JPA/Hibernate: https://hibernate.org/orm/
- Spring Data: https://spring.io/projects/spring-data