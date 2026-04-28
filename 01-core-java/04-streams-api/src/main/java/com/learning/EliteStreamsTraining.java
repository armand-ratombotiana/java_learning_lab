package com.learning;

import java.util.*;
import java.util.function.Function;
import java.util.stream.*;

/**
 * ELITE STREAMS API TRAINING - FAANG Interview Preparation
 *
 * This class contains 12 advanced stream processing problems commonly asked
 * in interviews at top tech companies (Google, Amazon, Meta, Microsoft, Netflix, Apple).
 *
 * PEDAGOGIC APPROACH:
 * - Foundation Level (Problems 1-4): Stream pipeline basics
 * - Intermediate Level (Problems 5-8): Complex collectors and grouping
 * - Advanced Level (Problems 9-12): Custom collectors and optimization
 *
 * Each problem includes:
 * - Clear problem statement
 * - Multiple solution approaches
 * - Time and space complexity analysis
 * - Interview tips and follow-up questions
 *
 * @author Elite Interview Preparation Team
 * @version 1.0
 */
public class EliteStreamsTraining {

    // ============================================================================
    // FOUNDATION LEVEL - Stream Pipeline Basics (Problems 1-4)
    // ============================================================================

    /**
     * Problem 1: Find Top K Frequent Words
     *
     * Company: Amazon, Google
     * Difficulty: Medium
     *
     * Given a list of words, return the k most frequent words.
     * If two words have the same frequency, sort them lexicographically.
     *
     * Example:
     *   Input: words = ["java", "stream", "java", "api", "stream", "java"], k = 2
     *   Output: ["java", "stream"]
     *
     * Time Complexity: O(n log k) where n is the number of words
     * Space Complexity: O(n) for the frequency map
     *
     * Interview Tips:
     * - Discuss trade-offs between heap-based and sorting approaches
     * - Consider memory constraints for very large datasets
     * - Mention streaming vs batch processing considerations
     *
     * @param words list of words
     * @param k number of top frequent words to return
     * @return list of k most frequent words
     */
    public static List<String> topKFrequentWords(List<String> words, int k) {
        if (words == null || words.isEmpty() || k <= 0) {
            return new ArrayList<>();
        }

        // Solution using streams with grouping and sorting
        return words.stream()
            // Count frequency of each word
            .collect(Collectors.groupingBy(
                Function.identity(),
                Collectors.counting()
            ))
            .entrySet()
            .stream()
            // Sort by frequency (descending), then lexicographically
            .sorted((e1, e2) -> {
                int freqCompare = e2.getValue().compareTo(e1.getValue());
                return freqCompare != 0 ? freqCompare : e1.getKey().compareTo(e2.getKey());
            })
            .limit(k)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }

    /**
     * Problem 2: Partition Students by Grade
     *
     * Company: Meta, Microsoft
     * Difficulty: Easy-Medium
     *
     * Given a list of students with scores, partition them into passing (>=60) and failing (<60).
     * Return a map with two keys: true (passing) and false (failing).
     *
     * Example:
     *   Input: students with scores [45, 78, 92, 55, 88, 34]
     *   Output: {true=[78,92,88], false=[45,55,34]}
     *
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     *
     * Interview Tips:
     * - Discuss partitioningBy vs groupingBy
     * - Consider downstream collectors for advanced analytics
     * - Mention immutability of the result
     *
     * @param students list of students with scores
     * @return map of passing (true) and failing (false) students
     */
    public static Map<Boolean, List<Student>> partitionStudentsByGrade(List<Student> students) {
        if (students == null || students.isEmpty()) {
            return Map.of(true, new ArrayList<>(), false, new ArrayList<>());
        }

        return students.stream()
            .collect(Collectors.partitioningBy(s -> s.getScore() >= 60));
    }

    /**
     * Problem 3: Calculate Average Salary by Department
     *
     * Company: Google, Amazon
     * Difficulty: Medium
     *
     * Given a list of employees, calculate the average salary for each department.
     *
     * Example:
     *   Input: employees in departments [Engineering(100k,120k), Sales(80k,90k)]
     *   Output: {Engineering=110k, Sales=85k}
     *
     * Time Complexity: O(n)
     * Space Complexity: O(d) where d is the number of departments
     *
     * Interview Tips:
     * - Discuss precision issues with floating-point arithmetic
     * - Consider parallel stream for large datasets
     * - Mention potential for custom collectors for more complex aggregations
     *
     * @param employees list of employees with department and salary
     * @return map of department to average salary
     */
    public static Map<String, Double> averageSalaryByDepartment(List<Employee> employees) {
        if (employees == null || employees.isEmpty()) {
            return new HashMap<>();
        }

        return employees.stream()
            .collect(Collectors.groupingBy(
                Employee::getDepartment,
                Collectors.averagingDouble(Employee::getSalary)
            ));
    }

    /**
     * Problem 4: Find Longest String in Each Group
     *
     * Company: Microsoft, LinkedIn
     * Difficulty: Medium
     *
     * Given a list of strings, group them by their first character and
     * find the longest string in each group.
     *
     * Example:
     *   Input: ["apple", "ant", "banana", "bear", "cat"]
     *   Output: {a="apple", b="banana", c="cat"}
     *
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     *
     * Interview Tips:
     * - Discuss handling of empty strings and null values
     * - Consider case sensitivity implications
     * - Mention custom comparators for tie-breaking
     *
     * @param strings list of strings
     * @return map of first character to longest string
     */
    public static Map<Character, String> longestStringByFirstChar(List<String> strings) {
        if (strings == null || strings.isEmpty()) {
            return new HashMap<>();
        }

        return strings.stream()
            .filter(s -> s != null && !s.isEmpty())
            .collect(Collectors.groupingBy(
                s -> s.charAt(0),
                Collectors.collectingAndThen(
                    Collectors.maxBy(Comparator.comparingInt(String::length)),
                    opt -> opt.orElse("")
                )
            ));
    }

    // ============================================================================
    // INTERMEDIATE LEVEL - Complex Collectors (Problems 5-8)
    // ============================================================================

    /**
     * Problem 5: Multi-Level Grouping - Department and Seniority
     *
     * Company: Amazon, Meta
     * Difficulty: Medium-Hard
     *
     * Given a list of employees, group them first by department,
     * then by seniority level (Junior < 3 years, Mid 3-7 years, Senior > 7 years).
     *
     * Example:
     *   Input: employees with department and years of experience
     *   Output: {Engineering={Junior=[...], Mid=[...], Senior=[...]}, ...}
     *
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     *
     * Interview Tips:
     * - Discuss nested grouping patterns
     * - Consider memory implications for deep hierarchies
     * - Mention streaming vs materialization trade-offs
     *
     * @param employees list of employees
     * @return nested map of department -> seniority level -> employees
     */
    public static Map<String, Map<String, List<Employee>>> groupByDepartmentAndSeniority(
            List<Employee> employees) {
        if (employees == null || employees.isEmpty()) {
            return new HashMap<>();
        }

        return employees.stream()
            .collect(Collectors.groupingBy(
                Employee::getDepartment,
                Collectors.groupingBy(emp -> {
                    int years = emp.getYearsOfExperience();
                    if (years < 3) return "Junior";
                    else if (years <= 7) return "Mid";
                    else return "Senior";
                })
            ));
    }

    /**
     * Problem 6: Custom Statistics Collector
     *
     * Company: Google, Netflix
     * Difficulty: Hard
     *
     * Given a list of numbers, collect custom statistics including:
     * count, sum, min, max, average, and standard deviation.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     *
     * Interview Tips:
     * - Discuss single-pass vs multi-pass algorithms
     * - Consider numerical stability for standard deviation
     * - Mention parallel stream compatibility
     *
     * @param numbers list of numbers
     * @return statistics object with all computed values
     */
    public static CustomStatistics collectStatistics(List<Double> numbers) {
        if (numbers == null || numbers.isEmpty()) {
            return new CustomStatistics(0, 0.0, 0.0, 0.0, 0.0, 0.0);
        }

        DoubleSummaryStatistics stats = numbers.stream()
            .mapToDouble(Double::doubleValue)
            .summaryStatistics();

        // Calculate standard deviation
        double mean = stats.getAverage();
        double variance = numbers.stream()
            .mapToDouble(n -> Math.pow(n - mean, 2))
            .average()
            .orElse(0.0);
        double stdDev = Math.sqrt(variance);

        return new CustomStatistics(
            stats.getCount(),
            stats.getSum(),
            stats.getMin(),
            stats.getMax(),
            stats.getAverage(),
            stdDev
        );
    }

    /**
     * Problem 7: Flatten Nested Structures
     *
     * Company: Amazon, Microsoft
     * Difficulty: Medium
     *
     * Given a list of departments, where each department has a list of teams,
     * and each team has a list of employees, flatten to get all employees.
     *
     * Example:
     *   Input: departments with nested teams and employees
     *   Output: flat list of all employees
     *
     * Time Complexity: O(n) where n is total number of employees
     * Space Complexity: O(n)
     *
     * Interview Tips:
     * - Discuss flatMap vs nested loops
     * - Consider memory efficiency for large hierarchies
     * - Mention stream laziness benefits
     *
     * @param departments list of departments with nested teams
     * @return flat list of all employees
     */
    public static List<Employee> flattenDepartmentStructure(List<Department> departments) {
        if (departments == null || departments.isEmpty()) {
            return new ArrayList<>();
        }

        return departments.stream()
            .flatMap(dept -> dept.getTeams().stream())
            .flatMap(team -> team.getEmployees().stream())
            .collect(Collectors.toList());
    }

    /**
     * Problem 8: Top N Employees by Salary in Each Department
     *
     * Company: Meta, Google
     * Difficulty: Hard
     *
     * Given a list of employees, find the top N highest-paid employees
     * in each department.
     *
     * Example:
     *   Input: employees, N=3
     *   Output: {Engineering=[emp1,emp2,emp3], Sales=[emp4,emp5,emp6]}
     *
     * Time Complexity: O(n log N) per department
     * Space Complexity: O(n)
     *
     * Interview Tips:
     * - Discuss heap-based approach vs sorting
     * - Consider tie-breaking strategies
     * - Mention handling departments with fewer than N employees
     *
     * @param employees list of employees
     * @param n number of top employees per department
     * @return map of department to top N employees
     */
    public static Map<String, List<Employee>> topNEmployeesByDepartment(
            List<Employee> employees, int n) {
        if (employees == null || employees.isEmpty() || n <= 0) {
            return new HashMap<>();
        }

        return employees.stream()
            .collect(Collectors.groupingBy(
                Employee::getDepartment,
                Collectors.collectingAndThen(
                    Collectors.toList(),
                    list -> list.stream()
                        .sorted(Comparator.comparingDouble(Employee::getSalary).reversed())
                        .limit(n)
                        .collect(Collectors.toList())
                )
            ));
    }

    // ============================================================================
    // ADVANCED LEVEL - Custom Collectors and Optimization (Problems 9-12)
    // ============================================================================

    /**
     * Problem 9: Parallel Stream Processing with Aggregation
     *
     * Company: Google, Netflix
     * Difficulty: Hard
     *
     * Process a large list of transactions in parallel, calculating:
     * - Total transaction amount
     * - Number of transactions per category
     * - Average transaction value per category
     *
     * Time Complexity: O(n/p) where p is the number of processors
     * Space Complexity: O(c) where c is the number of categories
     *
     * Interview Tips:
     * - Discuss parallel stream overhead vs benefits
     * - Consider thread safety of collectors
     * - Mention combiner function importance
     *
     * @param transactions list of transactions
     * @return aggregated transaction summary
     */
    public static TransactionSummary processTransactionsInParallel(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return new TransactionSummary(0.0, new HashMap<>(), new HashMap<>());
        }

        // Calculate total amount in parallel
        double totalAmount = transactions.parallelStream()
            .mapToDouble(Transaction::getAmount)
            .sum();

        // Count transactions per category
        Map<String, Long> countByCategory = transactions.parallelStream()
            .collect(Collectors.groupingByConcurrent(
                Transaction::getCategory,
                Collectors.counting()
            ));

        // Average transaction value per category
        Map<String, Double> avgByCategory = transactions.parallelStream()
            .collect(Collectors.groupingByConcurrent(
                Transaction::getCategory,
                Collectors.averagingDouble(Transaction::getAmount)
            ));

        return new TransactionSummary(totalAmount, countByCategory, avgByCategory);
    }

    /**
     * Problem 10: Custom Immutable Collector
     *
     * Company: Meta, Microsoft
     * Difficulty: Hard
     *
     * Create a custom collector that builds an immutable list with
     * duplicate detection and validation.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     *
     * Interview Tips:
     * - Discuss Collector.of() factory method
     * - Consider supplier, accumulator, combiner, finisher functions
     * - Mention characteristics (CONCURRENT, UNORDERED, IDENTITY_FINISH)
     *
     * @param stream stream of elements
     * @return immutable list without duplicates
     */
    public static <T> List<T> collectToImmutableNoDuplicates(Stream<T> stream) {
        return stream.collect(Collector.<T, LinkedHashSet<T>, List<T>>of(
            // Supplier: create new accumulator
            LinkedHashSet::new,
            // Accumulator: add element to accumulator
            LinkedHashSet::add,
            // Combiner: merge two accumulators (for parallel streams)
            (set1, set2) -> {
                set1.addAll(set2);
                return set1;
            },
            // Finisher: convert to immutable list
            set -> Collections.unmodifiableList(new ArrayList<>(set))
        ));
    }

    /**
     * Problem 11: Stream Pipeline Optimization
     *
     * Company: Amazon, Google
     * Difficulty: Medium-Hard
     *
     * Given a complex filtering and transformation pipeline,
     * optimize for performance by reordering operations.
     *
     * Original: filter expensive -> map -> filter cheap
     * Optimized: filter cheap -> filter expensive -> map
     *
     * Time Complexity: Depends on filter selectivity
     * Space Complexity: O(n)
     *
     * Interview Tips:
     * - Discuss short-circuit operations (limit, findFirst, anyMatch)
     * - Consider filter selectivity and cost
     * - Mention lazy evaluation benefits
     *
     * @param data list of data items
     * @return optimized processed results
     */
    public static List<String> optimizedPipeline(List<DataItem> data) {
        if (data == null || data.isEmpty()) {
            return new ArrayList<>();
        }

        // Optimized: cheap filters first, expensive operations last
        return data.stream()
            // 1. Cheap filter - null/empty check
            .filter(item -> item.getValue() != null && !item.getValue().isEmpty())
            // 2. Cheap filter - simple comparison
            .filter(item -> item.getValue().length() > 5)
            // 3. Expensive filter - complex computation
            .filter(DataItem::isValid)
            // 4. Transformation (only on filtered data)
            .map(item -> item.getValue().toUpperCase())
            // 5. Distinct (after reducing dataset size)
            .distinct()
            .collect(Collectors.toList());
    }

    /**
     * Problem 12: Windowing and Batching
     *
     * Company: Netflix, Google
     * Difficulty: Hard
     *
     * Given a stream of events, partition them into fixed-size batches
     * for batch processing (e.g., for database inserts).
     *
     * Example:
     *   Input: [1,2,3,4,5,6,7,8,9], batchSize=3
     *   Output: [[1,2,3], [4,5,6], [7,8,9]]
     *
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     *
     * Interview Tips:
     * - Discuss custom collector implementation
     * - Consider memory implications for large batches
     * - Mention use cases (database batch inserts, network requests)
     *
     * @param items list of items to batch
     * @param batchSize size of each batch
     * @return list of batches
     */
    public static <T> List<List<T>> partitionIntoBatches(List<T> items, int batchSize) {
        if (items == null || items.isEmpty() || batchSize <= 0) {
            return new ArrayList<>();
        }

        // Custom collector for batching
        return IntStream.range(0, items.size())
            .boxed()
            .collect(Collectors.groupingBy(
                index -> index / batchSize,
                Collectors.mapping(items::get, Collectors.toList())
            ))
            .values()
            .stream()
            .collect(Collectors.toList());
    }

    // ============================================================================
    // HELPER CLASSES
    // ============================================================================

    /**
     * Student class for partitioning exercise.
     */
    public static class Student {
        private String name;
        private int score;

        public Student(String name, int score) {
            this.name = name;
            this.score = score;
        }

        public String getName() {
            return name;
        }

        public int getScore() {
            return score;
        }

        @Override
        public String toString() {
            return name + "(" + score + ")";
        }
    }

    /**
     * Employee class for various exercises.
     */
    public static class Employee {
        private String name;
        private String department;
        private double salary;
        private int yearsOfExperience;

        public Employee(String name, String department, double salary, int yearsOfExperience) {
            this.name = name;
            this.department = department;
            this.salary = salary;
            this.yearsOfExperience = yearsOfExperience;
        }

        public String getName() {
            return name;
        }

        public String getDepartment() {
            return department;
        }

        public double getSalary() {
            return salary;
        }

        public int getYearsOfExperience() {
            return yearsOfExperience;
        }

        @Override
        public String toString() {
            return name + "(" + department + ",$" + salary + "," + yearsOfExperience + "y)";
        }
    }

    /**
     * Department class for nested structure exercises.
     */
    public static class Department {
        private String name;
        private List<Team> teams;

        public Department(String name, List<Team> teams) {
            this.name = name;
            this.teams = teams;
        }

        public String getName() {
            return name;
        }

        public List<Team> getTeams() {
            return teams;
        }
    }

    /**
     * Team class for nested structure exercises.
     */
    public static class Team {
        private String name;
        private List<Employee> employees;

        public Team(String name, List<Employee> employees) {
            this.name = name;
            this.employees = employees;
        }

        public String getName() {
            return name;
        }

        public List<Employee> getEmployees() {
            return employees;
        }
    }

    /**
     * Transaction class for parallel processing exercises.
     */
    public static class Transaction {
        private String id;
        private String category;
        private double amount;

        public Transaction(String id, String category, double amount) {
            this.id = id;
            this.category = category;
            this.amount = amount;
        }

        public String getId() {
            return id;
        }

        public String getCategory() {
            return category;
        }

        public double getAmount() {
            return amount;
        }
    }

    /**
     * DataItem class for optimization exercises.
     */
    public static class DataItem {
        private String value;

        public DataItem(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public boolean isValid() {
            // Simulate expensive validation
            return value != null && value.matches("[A-Za-z]+");
        }
    }

    /**
     * Custom statistics result.
     */
    public static class CustomStatistics {
        private final long count;
        private final double sum;
        private final double min;
        private final double max;
        private final double average;
        private final double standardDeviation;

        public CustomStatistics(long count, double sum, double min, double max,
                              double average, double standardDeviation) {
            this.count = count;
            this.sum = sum;
            this.min = min;
            this.max = max;
            this.average = average;
            this.standardDeviation = standardDeviation;
        }

        public long getCount() { return count; }
        public double getSum() { return sum; }
        public double getMin() { return min; }
        public double getMax() { return max; }
        public double getAverage() { return average; }
        public double getStandardDeviation() { return standardDeviation; }

        @Override
        public String toString() {
            return String.format("Stats{count=%d, sum=%.2f, min=%.2f, max=%.2f, avg=%.2f, stdDev=%.2f}",
                count, sum, min, max, average, standardDeviation);
        }
    }

    /**
     * Transaction summary result.
     */
    public static class TransactionSummary {
        private final double totalAmount;
        private final Map<String, Long> countByCategory;
        private final Map<String, Double> avgByCategory;

        public TransactionSummary(double totalAmount, Map<String, Long> countByCategory,
                                Map<String, Double> avgByCategory) {
            this.totalAmount = totalAmount;
            this.countByCategory = countByCategory;
            this.avgByCategory = avgByCategory;
        }

        public double getTotalAmount() { return totalAmount; }
        public Map<String, Long> getCountByCategory() { return countByCategory; }
        public Map<String, Double> getAvgByCategory() { return avgByCategory; }

        @Override
        public String toString() {
            return String.format("TransactionSummary{total=%.2f, categories=%d}",
                totalAmount, countByCategory.size());
        }
    }

    /**
     * Main method to demonstrate all elite training exercises.
     */
    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("ELITE STREAMS API TRAINING - FAANG Interview Preparation");
        System.out.println("=".repeat(80));

        demonstrateAll();

        System.out.println("\n" + "=".repeat(80));
        System.out.println("TRAINING COMPLETE - Ready for Elite Interviews!");
        System.out.println("=".repeat(80));
    }

    /**
     * Demonstrates all training problems.
     */
    public static void demonstrateAll() {
        System.out.println("\n--- FOUNDATION LEVEL ---\n");
        demonstrateTopKFrequentWords();
        demonstratePartitionStudents();
        demonstrateAverageSalary();
        demonstrateLongestString();

        System.out.println("\n--- INTERMEDIATE LEVEL ---\n");
        demonstrateMultiLevelGrouping();
        demonstrateCustomStatistics();
        demonstrateFlattenStructure();
        demonstrateTopNEmployees();

        System.out.println("\n--- ADVANCED LEVEL ---\n");
        demonstrateParallelProcessing();
        demonstrateCustomCollector();
        demonstrateOptimizedPipeline();
        demonstrateBatching();
    }

    private static void demonstrateTopKFrequentWords() {
        System.out.println("Problem 1: Top K Frequent Words");
        List<String> words = Arrays.asList("java", "stream", "java", "api", "stream", "java", "code");
        List<String> result = topKFrequentWords(words, 2);
        System.out.println("Input: " + words);
        System.out.println("Top 2 frequent: " + result);
        System.out.println();
    }

    private static void demonstratePartitionStudents() {
        System.out.println("Problem 2: Partition Students by Grade");
        List<Student> students = Arrays.asList(
            new Student("Alice", 78),
            new Student("Bob", 45),
            new Student("Charlie", 92),
            new Student("David", 55)
        );
        Map<Boolean, List<Student>> result = partitionStudentsByGrade(students);
        System.out.println("Passing: " + result.get(true));
        System.out.println("Failing: " + result.get(false));
        System.out.println();
    }

    private static void demonstrateAverageSalary() {
        System.out.println("Problem 3: Average Salary by Department");
        List<Employee> employees = Arrays.asList(
            new Employee("Alice", "Engineering", 100000, 5),
            new Employee("Bob", "Engineering", 120000, 8),
            new Employee("Charlie", "Sales", 80000, 3)
        );
        Map<String, Double> result = averageSalaryByDepartment(employees);
        System.out.println("Average salaries: " + result);
        System.out.println();
    }

    private static void demonstrateLongestString() {
        System.out.println("Problem 4: Longest String by First Character");
        List<String> strings = Arrays.asList("apple", "ant", "banana", "bear", "cat");
        Map<Character, String> result = longestStringByFirstChar(strings);
        System.out.println("Longest by first char: " + result);
        System.out.println();
    }

    private static void demonstrateMultiLevelGrouping() {
        System.out.println("Problem 5: Multi-Level Grouping");
        List<Employee> employees = Arrays.asList(
            new Employee("Alice", "Engineering", 100000, 2),
            new Employee("Bob", "Engineering", 120000, 8),
            new Employee("Charlie", "Sales", 80000, 5)
        );
        Map<String, Map<String, List<Employee>>> result = groupByDepartmentAndSeniority(employees);
        System.out.println("Grouped by department and seniority:");
        result.forEach((dept, seniority) -> {
            System.out.println("  " + dept + ": " + seniority.keySet());
        });
        System.out.println();
    }

    private static void demonstrateCustomStatistics() {
        System.out.println("Problem 6: Custom Statistics");
        List<Double> numbers = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0);
        CustomStatistics stats = collectStatistics(numbers);
        System.out.println(stats);
        System.out.println();
    }

    private static void demonstrateFlattenStructure() {
        System.out.println("Problem 7: Flatten Nested Structure");
        List<Department> departments = Arrays.asList(
            new Department("Engineering", Arrays.asList(
                new Team("Backend", Arrays.asList(
                    new Employee("Alice", "Engineering", 100000, 5)
                ))
            ))
        );
        List<Employee> employees = flattenDepartmentStructure(departments);
        System.out.println("Flattened employees: " + employees);
        System.out.println();
    }

    private static void demonstrateTopNEmployees() {
        System.out.println("Problem 8: Top N Employees by Department");
        List<Employee> employees = Arrays.asList(
            new Employee("Alice", "Engineering", 100000, 5),
            new Employee("Bob", "Engineering", 120000, 8),
            new Employee("Charlie", "Engineering", 90000, 3),
            new Employee("David", "Sales", 85000, 4)
        );
        Map<String, List<Employee>> result = topNEmployeesByDepartment(employees, 2);
        result.forEach((dept, emps) -> {
            System.out.println(dept + " top 2: " + emps);
        });
        System.out.println();
    }

    private static void demonstrateParallelProcessing() {
        System.out.println("Problem 9: Parallel Stream Processing");
        List<Transaction> transactions = Arrays.asList(
            new Transaction("T1", "Food", 50.0),
            new Transaction("T2", "Electronics", 200.0),
            new Transaction("T3", "Food", 30.0)
        );
        TransactionSummary summary = processTransactionsInParallel(transactions);
        System.out.println(summary);
        System.out.println();
    }

    private static void demonstrateCustomCollector() {
        System.out.println("Problem 10: Custom Immutable Collector");
        List<Integer> result = collectToImmutableNoDuplicates(
            Stream.of(1, 2, 2, 3, 3, 4)
        );
        System.out.println("Immutable unique list: " + result);
        System.out.println();
    }

    private static void demonstrateOptimizedPipeline() {
        System.out.println("Problem 11: Optimized Pipeline");
        List<DataItem> data = Arrays.asList(
            new DataItem("hello"),
            new DataItem("world"),
            new DataItem("stream"),
            new DataItem("api")
        );
        List<String> result = optimizedPipeline(data);
        System.out.println("Optimized result: " + result);
        System.out.println();
    }

    private static void demonstrateBatching() {
        System.out.println("Problem 12: Windowing and Batching");
        List<Integer> items = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        List<List<Integer>> batches = partitionIntoBatches(items, 3);
        System.out.println("Batches of 3: " + batches);
        System.out.println();
    }
}
