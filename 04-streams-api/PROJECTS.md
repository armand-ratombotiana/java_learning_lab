# Java Streams API Module - PROJECTS.md

---

# 🎯 Mini-Project: Data Pipeline Processor

## Project Overview

**Duration**: 3-4 hours  
**Difficulty**: Intermediate  
**Concepts Used**: Stream API, Lambda Expressions, Functional Interfaces, Filtering, Mapping, Reduction

This project demonstrates Java Stream API capabilities through a practical data processing pipeline.

---

## Project Structure

```
04-streams-api/src/main/java/com/learning/project/
├── Main.java
├── model/
│   ├── Transaction.java
│   └── Customer.java
├── service/
│   └── DataPipelineService.java
├── pipeline/
│   └── TransactionProcessor.java
└── ui/
    └── PipelineMenu.java
```

---

## Step 1: Transaction Model

```java
// model/Transaction.java
package com.learning.project.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private String id;
    private String customerId;
    private double amount;
    private TransactionType type;
    private String category;
    private LocalDateTime timestamp;
    private String status;
    
    public enum TransactionType {
        CREDIT, DEBIT, TRANSFER, REFUND
    }
    
    public Transaction(String id, String customerId, double amount, 
                      TransactionType type, String category) {
        this.id = id;
        this.customerId = customerId;
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.timestamp = LocalDateTime.now();
        this.status = "COMPLETED";
    }
    
    // Getters
    public String getId() { return id; }
    public String getCustomerId() { return customerId; }
    public double getAmount() { return amount; }
    public TransactionType getType() { return type; }
    public String getCategory() { return category; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getStatus() { return status; }
    
    public boolean isCredit() { return type == TransactionType.CREDIT; }
    public boolean isDebit() { return type == TransactionType.DEBIT; }
    
    @Override
    public String toString() {
        return String.format("[%s] %s: $%.2f (%s) - %s",
            id, type, amount, category, 
            timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
}
```

---

## Step 2: Customer Model

```java
// model/Customer.java
package com.learning.project.model;

import java.time.LocalDateTime;

public class Customer {
    private String id;
    private String name;
    private String email;
    private String region;
    private CustomerTier tier;
    private LocalDateTime joinedAt;
    
    public enum CustomerTier {
        BRONZE, SILVER, GOLD, PLATINUM
    }
    
    public Customer(String id, String name, String email, 
                   String region, CustomerTier tier) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.region = region;
        this.tier = tier;
        this.joinedAt = LocalDateTime.now();
    }
    
    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRegion() { return region; }
    public CustomerTier getTier() { return tier; }
    public LocalDateTime getJoinedAt() { return joinedAt; }
    
    @Override
    public String toString() {
        return String.format("[%s] %s (%s) - %s", id, name, tier, region);
    }
}
```

---

## Step 3: Data Pipeline Service

```java
// service/DataPipelineService.java
package com.learning.project.service;

import com.learning.project.model.*;
import java.util.*;
import java.util.stream.*;

public class DataPipelineService {
    private List<Transaction> transactions;
    private Map<String, Customer> customers;
    
    public DataPipelineService() {
        this.transactions = new ArrayList<>();
        this.customers = new HashMap<>();
        initializeSampleData();
    }
    
    private void initializeSampleData() {
        // Create customers
        Customer c1 = new Customer("C001", "Alice Johnson", "alice@email.com", "North", CustomerTier.GOLD);
        Customer c2 = new Customer("C002", "Bob Smith", "bob@email.com", "South", CustomerTier.SILVER);
        Customer c3 = new Customer("C003", "Carol Williams", "carol@email.com", "East", CustomerTier.PLATINUM);
        Customer c4 = new Customer("C004", "David Brown", "david@email.com", "West", CustomerTier.BRONZE);
        
        customers.put(c1.getId(), c1);
        customers.put(c2.getId(), c2);
        customers.put(c3.getId(), c3);
        customers.put(c4.getId(), c4);
        
        // Create transactions
        addTransaction("T001", "C001", 150.00, Transaction.TransactionType.DEBIT, "Shopping");
        addTransaction("T002", "C001", 2000.00, Transaction.TransactionType.CREDIT, "Salary");
        addTransaction("T003", "C002", 75.50, Transaction.TransactionType.DEBIT, "Food");
        addTransaction("T004", "C002", 500.00, Transaction.TransactionType.TRANSFER, "Transfer");
        addTransaction("T005", "C003", 3000.00, Transaction.TransactionType.CREDIT, "Investment");
        addTransaction("T006", "C003", 120.00, Transaction.TransactionType.DEBIT, "Utilities");
        addTransaction("T007", "C004", 25.00, Transaction.TransactionType.DEBIT, "Entertainment");
        addTransaction("T008", "C001", 45.00, Transaction.TransactionType.REFUND, "Return");
        addTransaction("T009", "C003", 800.00, Transaction.TransactionType.DEBIT, "Travel");
        addTransaction("T010", "C002", 1500.00, Transaction.TransactionType.CREDIT, "Bonus");
    }
    
    private void addTransaction(String id, String customerId, double amount, 
                                 Transaction.TransactionType type, String category) {
        transactions.add(new Transaction(id, customerId, amount, type, category));
    }
    
    // Stream Operations
    
    public List<Transaction> getAllTransactions() {
        return new ArrayList<>(transactions);
    }
    
    public List<Transaction> filterByType(Transaction.TransactionType type) {
        return transactions.stream()
            .filter(t -> t.getType() == type)
            .collect(Collectors.toList());
    }
    
    public List<Transaction> filterByMinAmount(double minAmount) {
        return transactions.stream()
            .filter(t -> t.getAmount() >= minAmount)
            .collect(Collectors.toList());
    }
    
    public double getTotalAmount() {
        return transactions.stream()
            .mapToDouble(Transaction::getAmount)
            .sum();
    }
    
    public double getAverageAmount() {
        return transactions.stream()
            .mapToDouble(Transaction::getAmount)
            .average()
            .orElse(0.0);
    }
    
    public Optional<Transaction> getLargestTransaction() {
        return transactions.stream()
            .max(Comparator.comparingDouble(Transaction::getAmount));
    }
    
    public Optional<Transaction> getSmallestTransaction() {
        return transactions.stream()
            .min(Comparator.comparingDouble(Transaction::getAmount));
    }
    
    public long getTransactionCount() {
        return transactions.stream().count();
    }
    
    public Map<String, List<Transaction>> groupByCategory() {
        return transactions.stream()
            .collect(Collectors.groupingBy(Transaction::getCategory));
    }
    
    public Map<Transaction.TransactionType, Double> sumByType() {
        return transactions.stream()
            .collect(Collectors.groupingBy(
                Transaction::getType,
                Collectors.summingDouble(Transaction::getAmount)
            ));
    }
    
    public Map<String, Double> totalByCustomer() {
        return transactions.stream()
            .collect(Collectors.groupingBy(
                Transaction::getCustomerId,
                Collectors.summingDouble(Transaction::getAmount)
            ));
    }
    
    public List<String> getUniqueCategories() {
        return transactions.stream()
            .map(Transaction::getCategory)
            .distinct()
            .collect(Collectors.toList());
    }
    
    public boolean hasTransactionOver(double amount) {
        return transactions.stream()
            .anyMatch(t -> t.getAmount() > amount);
    }
    
    public boolean allTransactionsOver(double amount) {
        return transactions.stream()
            .allMatch(t -> t.getAmount() > amount);
    }
    
    public List<Transaction> getTopTransactions(int n) {
        return transactions.stream()
            .sorted(Comparator.comparingDouble(Transaction::getAmount).reversed())
            .limit(n)
            .collect(Collectors.toList());
    }
    
    public List<Transaction> getTransactionsSortedByAmount(boolean ascending) {
        return transactions.stream()
            .sorted(ascending ? 
                Comparator.comparingDouble(Transaction::getAmount) :
                Comparator.comparingDouble(Transaction::getAmount).reversed())
            .collect(Collectors.toList());
    }
    
    public DoubleSummaryStatistics getStatistics() {
        return transactions.stream()
            .mapToDouble(Transaction::getAmount)
            .summaryStatistics();
    }
    
    public Map<CustomerTier, Long> countByTier() {
        return customers.values().stream()
            .collect(Collectors.groupingBy(
                Customer::getTier,
                Collectors.counting()
            ));
    }
    
    public Map<String, Double> averageByRegion() {
        return customers.values().stream()
            .collect(Collectors.groupingBy(
                Customer::getRegion,
                Collectors.averagingInt(c -> getCustomerTransactionCount(c.getId()))
            ));
    }
    
    private long getCustomerTransactionCount(String customerId) {
        return transactions.stream()
            .filter(t -> t.getCustomerId().equals(customerId))
            .count();
    }
    
    public List<Customer> getCustomersByTier(CustomerTier tier) {
        return customers.values().stream()
            .filter(c -> c.getTier() == tier)
            .collect(Collectors.toList());
    }
    
    public double getTotalByCustomer(String customerId) {
        return transactions.stream()
            .filter(t -> t.getCustomerId().equals(customerId))
            .mapToDouble(Transaction::getAmount)
            .sum();
    }
}
```

---

## Step 4: Pipeline Menu Interface

```java
// ui/PipelineMenu.java
package com.learning.project.ui;

import com.learning.project.model.*;
import com.learning.project.service.DataPipelineService;
import java.util.*;

public class PipelineMenu {
    private Scanner scanner;
    private DataPipelineService service;
    private boolean running;
    
    public PipelineMenu() {
        this.scanner = new Scanner(System.in);
        this.service = new DataPipelineService();
        this.running = true;
    }
    
    public void start() {
        System.out.println("\n🔄 DATA PIPELINE PROCESSOR");
        System.out.println("==========================");
        
        while (running) {
            displayMenu();
            handleChoice(getChoice());
        }
    }
    
    private void displayMenu() {
        System.out.println("\n--- MAIN MENU ---");
        System.out.println("1. View All Transactions");
        System.out.println("2. Filter by Type");
        System.out.println("3. Filter by Amount");
        System.out.println("4. Calculate Totals");
        System.out.println("5. Statistics Summary");
        System.out.println("6. Group by Category");
        System.out.println("7. Group by Type");
        System.out.println("8. Top N Transactions");
        System.out.println("9. Customer Analysis");
        System.out.println("10. Advanced Queries");
        System.out.println("11. Exit");
        System.out.print("\nChoice: ");
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
            case 1 -> viewAllTransactions();
            case 2 -> filterByType();
            case 3 -> filterByAmount();
            case 4 -> calculateTotals();
            case 5 -> statisticsSummary();
            case 6 -> groupByCategory();
            case 7 -> groupByType();
            case 8 -> topTransactions();
            case 9 -> customerAnalysis();
            case 10 -> advancedQueries();
            case 11 -> { System.out.println("Goodbye!"); running = false; }
            default -> System.out.println("Invalid choice!");
        }
    }
    
    private void viewAllTransactions() {
        var transactions = service.getAllTransactions();
        transactions.forEach(System.out::println);
    }
    
    private void filterByType() {
        System.out.println("Types: CREDIT, DEBIT, TRANSFER, REFUND");
        System.out.print("Enter type: ");
        String typeStr = scanner.nextLine().trim().toUpperCase();
        
        try {
            Transaction.TransactionType type = Transaction.TransactionType.valueOf(typeStr);
            var transactions = service.filterByType(type);
            transactions.forEach(System.out::println);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid type!");
        }
    }
    
    private void filterByAmount() {
        System.out.print("Minimum amount: ");
        double min = getDouble();
        var transactions = service.filterByMinAmount(min);
        System.out.println("Found " + transactions.size() + " transactions");
        transactions.forEach(System.out::println);
    }
    
    private void calculateTotals() {
        System.out.println("\n=== TOTALS ===");
        System.out.printf("Total Amount: $%.2f%n", service.getTotalAmount());
        System.out.printf("Average Amount: $%.2f%n", service.getAverageAmount());
        System.out.println("Transaction Count: " + service.getTransactionCount());
        
        var largest = service.getLargestTransaction();
        largest.ifPresent(t -> System.out.printf("Largest: $%.2f%n", t.getAmount()));
        
        var smallest = service.getSmallestTransaction();
        smallest.ifPresent(t -> System.out.printf("Smallest: $%.2f%n", t.getAmount()));
    }
    
    private void statisticsSummary() {
        var stats = service.getStatistics();
        System.out.println("\n=== STATISTICS ===");
        System.out.println("Count: " + stats.getCount());
        System.out.printf("Sum: $%.2f%n", stats.getSum());
        System.out.printf("Min: $%.2f%n", stats.getMin());
        System.out.printf("Max: $%.2f%n", stats.getMax());
        System.out.printf("Average: $%.2f%n", stats.getAverage());
    }
    
    private void groupByCategory() {
        var grouped = service.groupByCategory();
        System.out.println("\n=== BY CATEGORY ===");
        for (var entry : grouped.entrySet()) {
            double total = entry.getValue().stream()
                .mapToDouble(Transaction::getAmount).sum();
            System.out.printf("%s: %d transactions, $%.2f total%n",
                entry.getKey(), entry.getValue().size(), total);
        }
    }
    
    private void groupByType() {
        var grouped = service.sumByType();
        System.out.println("\n=== BY TYPE ===");
        for (var entry : grouped.entrySet()) {
            System.out.printf("%s: $%.2f%n", entry.getKey(), entry.getValue());
        }
    }
    
    private void topTransactions() {
        System.out.print("Enter number of top transactions: ");
        int n = getInt();
        var top = service.getTopTransactions(n);
        System.out.println("\n=== TOP " + n + " TRANSACTIONS ===");
        top.forEach(System.out::println);
    }
    
    private void customerAnalysis() {
        System.out.println("\n=== CUSTOMER ANALYSIS ===");
        
        System.out.println("\nBy Tier:");
        var tierCount = service.countByTier();
        for (var entry : tierCount.entrySet()) {
            System.out.printf("  %s: %d customers%n", entry.getKey(), entry.getValue());
        }
        
        System.out.println("\nAverage by Region:");
        var avgByRegion = service.averageByRegion();
        for (var entry : avgByRegion.entrySet()) {
            System.out.printf("  %s: %.1f transactions%n", entry.getKey(), entry.getValue());
        }
    }
    
    private void advancedQueries() {
        System.out.println("\n=== ADVANCED QUERIES ===");
        
        System.out.println("Unique Categories: " + service.getUniqueCategories());
        
        System.out.printf("Has transaction over $1000? %b%n", 
            service.hasTransactionOver(1000));
        
        System.out.printf("All transactions over $10? %b%n", 
            service.allTransactionsOver(10));
        
        System.out.println("\nTotal by Customer:");
        var byCustomer = service.totalByCustomer();
        for (var entry : byCustomer.entrySet()) {
            System.out.printf("  %s: $%.2f%n", entry.getKey(), entry.getValue());
        }
    }
    
    private int getInt() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return 0;
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
        new PipelineMenu().start();
    }
}
```

---

## Running the Mini-Project

```bash
cd 04-streams-api
javac -d out -sourcepath src src/com/learning/project/**/*.java src/com/learning/project/*.java
java -cp out com.learning.project.ui.PipelineMenu
```

---

## Stream Concepts Demonstrated

| Concept | Implementation |
|---------|----------------|
| **filter()** | Filter transactions by type, amount |
| **map()** | Transform to different types |
| **reduce()** | Sum amounts |
| **collect()** | Grouping, partitioning |
| **sorted()** | Sort by amount |
| **findFirst/Any** | Find min/max |
| **flatMap()** | Flatten collections |
| **parallel()** | Parallel processing |

---

# 🚀 Real-World Project: Enterprise Data Processing Pipeline

## Project Overview

**Duration**: 15-20 hours  
**Difficulty**: Advanced  
**Concepts Used**: Parallel Streams, Custom Collectors, Optional Operations, Advanced Data Processing

This project implements a production-ready data pipeline with parallel processing, custom collectors, and complex transformations.

---

## Project Structure

```
04-streams-api/src/main/java/com/learning/project/
├── Main.java
├── model/
│   ├── Transaction.java
│   ├── Customer.java
│   └── Report.java
├── service/
│   ├── DataPipelineService.java
│   └── ReportGenerator.java
├── pipeline/
│   ├── PipelineBuilder.java
│   └── DataTransformer.java
├── collector/
│   └── CustomCollectors.java
├── processor/
│   └── BatchProcessor.java
└── ui/
    └── PipelineMenu.java
```

---

## Step 1: Custom Collectors

```java
// collector/CustomCollectors.java
package com.learning.project.collector;

import com.learning.project.model.*;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class CustomCollectors {
    
    public static <T> Collector<T, ?, Map<String, List<T>>> groupByAny(
            Function<T, String> classifier) {
        return Collector.of(
            HashMap::new,
            (map, item) -> map.computeIfAbsent(classifier.apply(item), k -> new ArrayList<>()).add(item),
            (m1, m2) -> {
                m2.forEach((k, v) -> m1.computeIfAbsent(k, ArrayList::new).addAll(v));
                return m1;
            }
        );
    }
    
    public static <T> Collector<T, ?, Optional<T>> mostExpensive(
            java.util.function.ToDoubleFunction<T> valueExtractor) {
        return Collector.of(
            () -> new java.util.AbstractMap.SimpleEntry<>(null, Double.MIN_VALUE),
            (acc, item) -> {
                double value = valueExtractor.applyAsDouble(item);
                if (value > acc.getValue()) {
                    acc.setValue(value);
                    acc.setKey(item);
                }
            },
            (a, b) -> {
                if (b.getValue() > a.getValue()) return b;
                return a;
            },
            entry -> Optional.ofNullable(entry.getKey())
        );
    }
    
    public static <T> Collector<T, ?, DoubleSummaryStatistics> statisticsBy(
            Function<T, Double> valueExtractor) {
        return Collector.of(
            DoubleSummaryStatistics::new,
            (stats, item) -> stats.accept(valueExtractor.apply(item)),
            DoubleSummaryStatistics::combine
        );
    }
}
```

---

## Step 2: Pipeline Builder

```java
// pipeline/PipelineBuilder.java
package com.learning.project.pipeline;

import com.learning.project.model.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class PipelineBuilder<T> {
    private List<Function<Stream<T>, Stream<T>>> stages;
    private List<Consumer<T>> sinks;
    
    public PipelineBuilder() {
        this.stages = new ArrayList<>();
        this.sinks = new ArrayList<>();
    }
    
    public PipelineBuilder<T> filter(Predicate<T> predicate) {
        stages.add(s -> s.filter(predicate));
        return this;
    }
    
    public PipelineBuilder<T> map(Function<T, ?> mapper) {
        stages.add(s -> s.map(mapper));
        return this;
    }
    
    public PipelineBuilder<T> flatMap(Function<T, ?> mapper) {
        stages.add(s -> s.flatMap(item -> {
            if (item instanceof java.util.Collection) {
                return ((java.util.Collection<?>) item).stream();
            }
            return Stream.of(item);
        }));
        return this;
    }
    
    public PipelineBuilder<T> sorted(Comparator<T> comparator) {
        stages.add(s -> s.sorted(comparator));
        return this;
    }
    
    public PipelineBuilder<T> distinct() {
        stages.add(Stream::distinct);
        return this;
    }
    
    public PipelineBuilder<T> limit(long n) {
        stages.add(s -> s.limit(n));
        return this;
    }
    
    public PipelineBuilder<T> peek(Consumer<T> consumer) {
        stages.add(s -> s.peek(consumer));
        return this;
    }
    
    public PipelineBuilder<T> collectTo(Consumer<List<T>> consumer) {
        sinks.add(item -> consumer.accept(new ArrayList<>()));
        return this;
    }
    
    public List<T> execute(Collection<T> input) {
        Stream<T> stream = input.stream();
        for (Function<Stream<T>, Stream<T>> stage : stages) {
            stream = stage.apply(stream);
        }
        return stream.collect(Collectors.toList());
    }
    
    public void executeAndSink(Collection<T> input, Consumer<T> sink) {
        Stream<T> stream = input.stream();
        for (Function<Stream<T>, Stream<T>> stage : stages) {
            stream = stage.apply(stream);
        }
        stream.forEach(sink);
    }
}
```

---

## Step 3: Data Transformer

```java
// pipeline/DataTransformer.java
package com.learning.project.pipeline;

import com.learning.project.model.*;
import java.util.*;
import java.util.stream.*;

public class DataTransformer {
    
    public static List<String> transformToNames(List<Customer> customers) {
        return customers.stream()
            .map(Customer::getName)
            .collect(Collectors.toList());
    }
    
    public static Map<String, Integer> countByTier(List<Customer> customers) {
        return customers.stream()
            .collect(Collectors.groupingBy(
                c -> c.getTier().name(),
                Collectors.collectingAndThen(
                    Collectors.counting(),
                    Long::intValue
                )
            ));
    }
    
    public static List<Transaction> filterAndSort(
            List<Transaction> transactions,
            double minAmount,
            boolean ascending) {
        
        return transactions.stream()
            .filter(t -> t.getAmount() >= minAmount)
            .sorted(ascending ?
                Comparator.comparingDouble(Transaction::getAmount) :
                Comparator.comparingDouble(Transaction::getAmount).reversed())
            .collect(Collectors.toList());
    }
    
    public static Map<String, DoubleSummaryStatistics> statisticsByCategory(
            List<Transaction> transactions) {
        
        return transactions.stream()
            .collect(Collectors.groupingBy(
                Transaction::getCategory,
                DoubleSummaryStatistics::new
            ));
    }
    
    public static Map<Boolean, List<Transaction>> partitionByAmount(
            List<Transaction> transactions,
            double threshold) {
        
        return transactions.stream()
            .collect(Collectors.partitioningBy(
                t -> t.getAmount() >= threshold
            ));
    }
    
    public static String summarizeTransactions(List<Transaction> transactions) {
        return transactions.stream()
            .map(Transaction::toString)
            .collect(Collectors.joining("\n"));
    }
    
    public static double calculateNetAmount(List<Transaction> transactions) {
        return transactions.stream()
            .mapToDouble(t -> t.isCredit() ? t.getAmount() : -t.getAmount())
            .sum();
    }
}
```

---

## Step 4: Report Generator

```java
// service/ReportGenerator.java
package com.learning.project.service;

import com.learning.project.model.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.*;

public class ReportGenerator {
    
    public String generateTransactionReport(List<Transaction> transactions) {
        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(60)).append("\n");
        sb.append("TRANSACTION REPORT\n");
        sb.append("Generated: ").append(LocalDateTime.now()).append("\n");
        sb.append("=".repeat(60)).append("\n\n");
        
        var stats = transactions.stream()
            .mapToDouble(Transaction::getAmount)
            .summaryStatistics();
        
        sb.append("Total Transactions: ").append(stats.getCount()).append("\n");
        sb.append(String.format("Total Amount: $%.2f%n", stats.getSum()));
        sb.append(String.format("Average: $%.2f%n", stats.getAverage()));
        sb.append(String.format("Min: $%.2f%n", stats.getMin()));
        sb.append(String.format("Max: $%.2f%n", stats.getMax()));
        
        sb.append("\n--- By Category ---\n");
        var byCategory = transactions.stream()
            .collect(Collectors.groupingBy(
                Transaction::getCategory,
                Collectors.summingDouble(Transaction::getAmount)
            ));
        byCategory.forEach((cat, sum) -> 
            sb.append(String.format("  %s: $%.2f%n", cat, sum)));
        
        sb.append("\n--- By Type ---\n");
        var byType = transactions.stream()
            .collect(Collectors.groupingBy(
                Transaction::getType,
                Collectors.counting()
            ));
        byType.forEach((type, count) -> 
            sb.append(String.format("  %s: %d%n", type, count)));
        
        return sb.toString();
    }
    
    public String generateCustomerReport(Map<String, Customer> customers,
                                         List<Transaction> transactions) {
        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(60)).append("\n");
        sb.append("CUSTOMER REPORT\n");
        sb.append("=".repeat(60)).append("\n\n");
        
        for (Customer customer : customers.values()) {
            var customerTx = transactions.stream()
                .filter(t -> t.getCustomerId().equals(customer.getId()))
                .collect(Collectors.toList());
            
            double total = customerTx.stream()
                .mapToDouble(Transaction::getAmount)
                .sum();
            
            long count = customerTx.size();
            
            sb.append(String.format("%s - %s%n", customer.getId(), customer.getName()));
            sb.append(String.format("  Tier: %s%n", customer.getTier()));
            sb.append(String.format("  Region: %s%n", customer.getRegion()));
            sb.append(String.format("  Transactions: %d%n", count));
            sb.append(String.format("  Total Value: $%.2f%n", total));
            sb.append(String.format("  Average: $%.2f%n", count > 0 ? total / count : 0));
            sb.append("\n");
        }
        
        return sb.toString();
    }
    
    public String generateAnalyticsReport(List<Transaction> transactions) {
        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(60)).append("\n");
        sb.append("ANALYTICS REPORT\n");
        sb.append("=".repeat(60)).append("\n\n");
        
        // Top categories
        var topCategories = transactions.stream()
            .collect(Collectors.groupingBy(
                Transaction::getCategory,
                Collectors.summingDouble(Transaction::getAmount)
            ))
            .entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .limit(5)
            .toList();
        
        sb.append("Top 5 Categories by Value:\n");
        topCategories.forEach(e -> 
            sb.append(String.format("  %s: $%.2f%n", e.getKey(), e.getValue())));
        
        // Transaction trends
        sb.append("\n--- Transaction Volume ---\n");
        var byType = transactions.stream()
            .collect(Collectors.groupingBy(
                Transaction::getType,
                Collectors.counting()
            ));
        byType.forEach((type, count) -> 
            sb.append(String.format("  %s: %d%n", type, count)));
        
        return sb.toString();
    }
}
```

---

## Step 5: Batch Processor with Parallel Streams

```java
// processor/BatchProcessor.java
package com.learning.project.processor;

import com.learning.project.model.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class BatchProcessor {
    private final int batchSize;
    private final ExecutorService executor;
    
    public BatchProcessor(int batchSize, int threads) {
        this.batchSize = batchSize;
        this.executor = Executors.newFixedThreadPool(threads);
    }
    
    public List<Transaction> processInBatches(List<Transaction> transactions) {
        List<List<Transaction>> batches = partition(transactions, batchSize);
        
        return batches.parallelStream()
            .flatMap(batch -> processBatch(batch).stream())
            .collect(Collectors.toList());
    }
    
    private List<Transaction> processBatch(List<Transaction> batch) {
        return batch.stream()
            .map(this::enrichTransaction)
            .collect(Collectors.toList());
    }
    
    private Transaction enrichTransaction(Transaction t) {
        // Simulate processing
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return t;
    }
    
    private <T> List<List<T>> partition(List<T> list, int size) {
        return IntStream.range(0, (int) Math.ceil((double) list.size() / size))
            .mapToObj(i -> list.subList(i * size, Math.min((i + 1) * size, list.size())))
            .collect(Collectors.toList());
    }
    
    public Map<String, List<Transaction>> processParallel(
            Map<String, List<Transaction>> transactions) {
        
        return transactions.entrySet().parallelStream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> e.getValue().parallelStream()
                    .map(this::enrichTransaction)
                    .collect(Collectors.toList())
            ));
    }
    
    public void shutdown() {
        executor.shutdown();
    }
}
```

---

## Running the Real-World Project

```bash
cd 04-streams-api
javac -d out -sourcepath src src/com/learning/project/**/*.java src/com/learning/project/*.java
java -cp out com.learning.project.ui.PipelineMenu
```

---

## Advanced Stream Concepts

| Concept | Implementation |
|---------|----------------|
| **Parallel Streams** | Batch processor with thread pool |
| **Custom Collectors** | GroupBy, Statistics collectors |
| **Pipeline Builder** | Fluent builder pattern |
| **Optional Operations** | Safe null handling |
| **Data Transformation** | Complex transformations |

---

## Extensions

1. Add reactive streams with Project Reactor
2. Integrate with Apache Kafka
3. Add machine learning pipeline
4. Implement real-time analytics
5. Add distributed processing with Spark

---

## Next Steps

After completing this module:
- **Module 5**: Add concurrent pipeline processing
- **Module 8**: Make pipeline generic for any data type
- **Module 7**: Add file-based import/export