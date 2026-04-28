package com.learning;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.util.stream.*;

import com.learning.EliteStreamsTraining.*;

/**
 * Comprehensive test suite for EliteStreamsTraining.
 *
 * Tests all 12 elite stream processing problems with:
 * - Normal case testing
 * - Edge case handling
 * - Performance validation
 * - Complexity verification
 *
 * @author Elite Interview Preparation Team
 * @version 1.0
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EliteStreamsTrainingTest {

    // ============================================================================
    // FOUNDATION LEVEL TESTS (Problems 1-4)
    // ============================================================================

    @Test
    @Order(1)
    @DisplayName("Problem 1: Top K Frequent Words - Normal Cases")
    void testTopKFrequentWords_NormalCases() {
        // Test case 1: Basic functionality
        List<String> words1 = Arrays.asList("java", "stream", "java", "api", "stream", "java");
        List<String> result1 = EliteStreamsTraining.topKFrequentWords(words1, 2);
        assertEquals(2, result1.size());
        assertEquals("java", result1.get(0)); // Most frequent
        assertEquals("stream", result1.get(1)); // Second most frequent

        // Test case 2: Lexicographic ordering for same frequency
        List<String> words2 = Arrays.asList("apple", "banana", "apple", "banana", "cherry");
        List<String> result2 = EliteStreamsTraining.topKFrequentWords(words2, 2);
        assertEquals("apple", result2.get(0)); // apple comes before banana
        assertEquals("banana", result2.get(1));

        // Test case 3: K equals total unique words
        List<String> words3 = Arrays.asList("one", "two", "three");
        List<String> result3 = EliteStreamsTraining.topKFrequentWords(words3, 3);
        assertEquals(3, result3.size());
    }

    @Test
    @Order(2)
    @DisplayName("Problem 1: Top K Frequent Words - Edge Cases")
    void testTopKFrequentWords_EdgeCases() {
        // Empty list
        List<String> empty = new ArrayList<>();
        assertTrue(EliteStreamsTraining.topKFrequentWords(empty, 2).isEmpty());

        // Null list
        assertNotNull(EliteStreamsTraining.topKFrequentWords(null, 2));

        // K = 0
        List<String> words = Arrays.asList("java", "stream");
        assertTrue(EliteStreamsTraining.topKFrequentWords(words, 0).isEmpty());

        // K > number of unique words
        List<String> words2 = Arrays.asList("java", "stream");
        List<String> result = EliteStreamsTraining.topKFrequentWords(words2, 10);
        assertEquals(2, result.size());
    }

    @Test
    @Order(3)
    @DisplayName("Problem 2: Partition Students - Normal Cases")
    void testPartitionStudents_NormalCases() {
        List<Student> students = Arrays.asList(
            new Student("Alice", 78),
            new Student("Bob", 45),
            new Student("Charlie", 92),
            new Student("David", 55),
            new Student("Eve", 88)
        );

        Map<Boolean, List<Student>> result = EliteStreamsTraining.partitionStudentsByGrade(students);

        // Passing students (>= 60)
        assertEquals(3, result.get(true).size());
        assertTrue(result.get(true).stream().allMatch(s -> s.getScore() >= 60));

        // Failing students (< 60)
        assertEquals(2, result.get(false).size());
        assertTrue(result.get(false).stream().allMatch(s -> s.getScore() < 60));
    }

    @Test
    @Order(4)
    @DisplayName("Problem 2: Partition Students - Edge Cases")
    void testPartitionStudents_EdgeCases() {
        // All passing
        List<Student> allPassing = Arrays.asList(
            new Student("Alice", 90),
            new Student("Bob", 80)
        );
        Map<Boolean, List<Student>> result1 = EliteStreamsTraining.partitionStudentsByGrade(allPassing);
        assertEquals(2, result1.get(true).size());
        assertEquals(0, result1.get(false).size());

        // All failing
        List<Student> allFailing = Arrays.asList(
            new Student("Charlie", 45),
            new Student("David", 30)
        );
        Map<Boolean, List<Student>> result2 = EliteStreamsTraining.partitionStudentsByGrade(allFailing);
        assertEquals(0, result2.get(true).size());
        assertEquals(2, result2.get(false).size());

        // Boundary case (exactly 60)
        List<Student> boundary = Arrays.asList(new Student("Eve", 60));
        Map<Boolean, List<Student>> result3 = EliteStreamsTraining.partitionStudentsByGrade(boundary);
        assertEquals(1, result3.get(true).size());
    }

    @Test
    @Order(5)
    @DisplayName("Problem 3: Average Salary by Department - Normal Cases")
    void testAverageSalary_NormalCases() {
        List<Employee> employees = Arrays.asList(
            new Employee("Alice", "Engineering", 100000, 5),
            new Employee("Bob", "Engineering", 120000, 8),
            new Employee("Charlie", "Sales", 80000, 3),
            new Employee("David", "Sales", 90000, 4)
        );

        Map<String, Double> result = EliteStreamsTraining.averageSalaryByDepartment(employees);

        assertEquals(110000.0, result.get("Engineering"), 0.01);
        assertEquals(85000.0, result.get("Sales"), 0.01);
    }

    @Test
    @Order(6)
    @DisplayName("Problem 3: Average Salary by Department - Edge Cases")
    void testAverageSalary_EdgeCases() {
        // Single employee per department
        List<Employee> single = Arrays.asList(
            new Employee("Alice", "Engineering", 100000, 5)
        );
        Map<String, Double> result = EliteStreamsTraining.averageSalaryByDepartment(single);
        assertEquals(100000.0, result.get("Engineering"), 0.01);

        // Empty list
        assertTrue(EliteStreamsTraining.averageSalaryByDepartment(new ArrayList<>()).isEmpty());
    }

    @Test
    @Order(7)
    @DisplayName("Problem 4: Longest String by First Char - Normal Cases")
    void testLongestString_NormalCases() {
        List<String> strings = Arrays.asList(
            "apple", "ant", "application",
            "banana", "bear",
            "cat", "car"
        );

        Map<Character, String> result = EliteStreamsTraining.longestStringByFirstChar(strings);

        assertEquals("application", result.get('a'));
        assertEquals("banana", result.get('b'));
        assertEquals("cat", result.get('c'));
    }

    @Test
    @Order(8)
    @DisplayName("Problem 4: Longest String by First Char - Edge Cases")
    void testLongestString_EdgeCases() {
        // Empty strings should be filtered
        List<String> withEmpty = Arrays.asList("apple", "", "ant");
        Map<Character, String> result1 = EliteStreamsTraining.longestStringByFirstChar(withEmpty);
        assertEquals("apple", result1.get('a'));

        // Null values should be handled
        List<String> withNull = new ArrayList<>();
        withNull.add("apple");
        withNull.add(null);
        withNull.add("ant");
        Map<Character, String> result2 = EliteStreamsTraining.longestStringByFirstChar(withNull);
        assertEquals("apple", result2.get('a'));
    }

    // ============================================================================
    // INTERMEDIATE LEVEL TESTS (Problems 5-8)
    // ============================================================================

    @Test
    @Order(9)
    @DisplayName("Problem 5: Multi-Level Grouping - Normal Cases")
    void testMultiLevelGrouping_NormalCases() {
        List<Employee> employees = Arrays.asList(
            new Employee("Junior1", "Engineering", 70000, 1),
            new Employee("Junior2", "Engineering", 75000, 2),
            new Employee("Mid1", "Engineering", 90000, 5),
            new Employee("Senior1", "Engineering", 130000, 10),
            new Employee("Junior3", "Sales", 60000, 1),
            new Employee("Senior2", "Sales", 100000, 8)
        );

        Map<String, Map<String, List<Employee>>> result =
            EliteStreamsTraining.groupByDepartmentAndSeniority(employees);

        // Check Engineering department
        assertEquals(3, result.get("Engineering").size());
        assertEquals(2, result.get("Engineering").get("Junior").size());
        assertEquals(1, result.get("Engineering").get("Mid").size());
        assertEquals(1, result.get("Engineering").get("Senior").size());

        // Check Sales department
        assertEquals(2, result.get("Sales").size());
        assertEquals(1, result.get("Sales").get("Junior").size());
        assertEquals(1, result.get("Sales").get("Senior").size());
    }

    @Test
    @Order(10)
    @DisplayName("Problem 5: Multi-Level Grouping - Edge Cases")
    void testMultiLevelGrouping_EdgeCases() {
        // Empty list
        assertTrue(EliteStreamsTraining.groupByDepartmentAndSeniority(new ArrayList<>()).isEmpty());

        // Null list
        assertNotNull(EliteStreamsTraining.groupByDepartmentAndSeniority(null));

        // Boundary years: 3 and 7
        List<Employee> boundary = Arrays.asList(
            new Employee("Boundary1", "Engineering", 80000, 3), // Should be Mid
            new Employee("Boundary2", "Engineering", 90000, 7)  // Should be Mid
        );
        Map<String, Map<String, List<Employee>>> result =
            EliteStreamsTraining.groupByDepartmentAndSeniority(boundary);
        assertEquals(2, result.get("Engineering").get("Mid").size());
    }

    @Test
    @Order(11)
    @DisplayName("Problem 6: Custom Statistics - Normal Cases")
    void testCustomStatistics_NormalCases() {
        List<Double> numbers = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0);

        CustomStatistics stats = EliteStreamsTraining.collectStatistics(numbers);

        assertEquals(5, stats.getCount());
        assertEquals(15.0, stats.getSum(), 0.01);
        assertEquals(1.0, stats.getMin(), 0.01);
        assertEquals(5.0, stats.getMax(), 0.01);
        assertEquals(3.0, stats.getAverage(), 0.01);

        // Standard deviation for 1,2,3,4,5 is approximately 1.414
        assertTrue(Math.abs(stats.getStandardDeviation() - 1.414) < 0.01);
    }

    @Test
    @Order(12)
    @DisplayName("Problem 6: Custom Statistics - Edge Cases")
    void testCustomStatistics_EdgeCases() {
        // Single element
        List<Double> single = Arrays.asList(5.0);
        CustomStatistics stats1 = EliteStreamsTraining.collectStatistics(single);
        assertEquals(1, stats1.getCount());
        assertEquals(5.0, stats1.getAverage(), 0.01);
        assertEquals(0.0, stats1.getStandardDeviation(), 0.01); // StdDev of single element is 0

        // Empty list
        CustomStatistics stats2 = EliteStreamsTraining.collectStatistics(new ArrayList<>());
        assertEquals(0, stats2.getCount());
    }

    @Test
    @Order(13)
    @DisplayName("Problem 7: Flatten Nested Structure - Normal Cases")
    void testFlattenStructure_NormalCases() {
        Employee emp1 = new Employee("Alice", "Engineering", 100000, 5);
        Employee emp2 = new Employee("Bob", "Engineering", 120000, 8);
        Employee emp3 = new Employee("Charlie", "Sales", 80000, 3);

        Team team1 = new Team("Backend", Arrays.asList(emp1, emp2));
        Team team2 = new Team("Sales-East", Arrays.asList(emp3));

        Department dept1 = new Department("Engineering", Arrays.asList(team1));
        Department dept2 = new Department("Sales", Arrays.asList(team2));

        List<Department> departments = Arrays.asList(dept1, dept2);

        List<Employee> result = EliteStreamsTraining.flattenDepartmentStructure(departments);

        assertEquals(3, result.size());
        assertTrue(result.contains(emp1));
        assertTrue(result.contains(emp2));
        assertTrue(result.contains(emp3));
    }

    @Test
    @Order(14)
    @DisplayName("Problem 7: Flatten Nested Structure - Edge Cases")
    void testFlattenStructure_EdgeCases() {
        // Empty departments
        assertTrue(EliteStreamsTraining.flattenDepartmentStructure(new ArrayList<>()).isEmpty());

        // Department with empty teams
        Department emptyDept = new Department("Empty", new ArrayList<>());
        List<Employee> result = EliteStreamsTraining.flattenDepartmentStructure(
            Arrays.asList(emptyDept)
        );
        assertTrue(result.isEmpty());
    }

    @Test
    @Order(15)
    @DisplayName("Problem 8: Top N Employees - Normal Cases")
    void testTopNEmployees_NormalCases() {
        List<Employee> employees = Arrays.asList(
            new Employee("Emp1", "Engineering", 150000, 10),
            new Employee("Emp2", "Engineering", 120000, 8),
            new Employee("Emp3", "Engineering", 100000, 5),
            new Employee("Emp4", "Engineering", 90000, 3),
            new Employee("Emp5", "Sales", 95000, 6),
            new Employee("Emp6", "Sales", 85000, 4)
        );

        Map<String, List<Employee>> result = EliteStreamsTraining.topNEmployeesByDepartment(employees, 2);

        // Engineering top 2
        assertEquals(2, result.get("Engineering").size());
        assertEquals("Emp1", result.get("Engineering").get(0).getName());
        assertEquals("Emp2", result.get("Engineering").get(1).getName());

        // Sales top 2
        assertEquals(2, result.get("Sales").size());
        assertEquals("Emp5", result.get("Sales").get(0).getName());
        assertEquals("Emp6", result.get("Sales").get(1).getName());
    }

    @Test
    @Order(16)
    @DisplayName("Problem 8: Top N Employees - Edge Cases")
    void testTopNEmployees_EdgeCases() {
        // N greater than department size
        List<Employee> employees = Arrays.asList(
            new Employee("Emp1", "Engineering", 100000, 5),
            new Employee("Emp2", "Engineering", 90000, 3)
        );

        Map<String, List<Employee>> result = EliteStreamsTraining.topNEmployeesByDepartment(employees, 10);
        assertEquals(2, result.get("Engineering").size());

        // N = 0
        assertTrue(EliteStreamsTraining.topNEmployeesByDepartment(employees, 0).isEmpty());

        // Empty list
        assertTrue(EliteStreamsTraining.topNEmployeesByDepartment(new ArrayList<>(), 2).isEmpty());
    }

    // ============================================================================
    // ADVANCED LEVEL TESTS (Problems 9-12)
    // ============================================================================

    @Test
    @Order(17)
    @DisplayName("Problem 9: Parallel Processing - Normal Cases")
    void testParallelProcessing_NormalCases() {
        List<Transaction> transactions = Arrays.asList(
            new Transaction("T1", "Food", 50.0),
            new Transaction("T2", "Electronics", 200.0),
            new Transaction("T3", "Food", 30.0),
            new Transaction("T4", "Electronics", 150.0),
            new Transaction("T5", "Clothing", 75.0)
        );

        TransactionSummary summary = EliteStreamsTraining.processTransactionsInParallel(transactions);

        assertEquals(505.0, summary.getTotalAmount(), 0.01);
        assertEquals(2, summary.getCountByCategory().get("Food"));
        assertEquals(2, summary.getCountByCategory().get("Electronics"));
        assertEquals(1, summary.getCountByCategory().get("Clothing"));

        assertEquals(40.0, summary.getAvgByCategory().get("Food"), 0.01);
        assertEquals(175.0, summary.getAvgByCategory().get("Electronics"), 0.01);
    }

    @Test
    @Order(18)
    @DisplayName("Problem 9: Parallel Processing - Edge Cases")
    void testParallelProcessing_EdgeCases() {
        // Empty list
        TransactionSummary summary = EliteStreamsTraining.processTransactionsInParallel(new ArrayList<>());
        assertEquals(0.0, summary.getTotalAmount());
        assertTrue(summary.getCountByCategory().isEmpty());

        // Single transaction
        List<Transaction> single = Arrays.asList(new Transaction("T1", "Food", 50.0));
        TransactionSummary summary2 = EliteStreamsTraining.processTransactionsInParallel(single);
        assertEquals(50.0, summary2.getTotalAmount(), 0.01);
    }

    @Test
    @Order(19)
    @DisplayName("Problem 10: Custom Collector - Normal Cases")
    void testCustomCollector_NormalCases() {
        // Test with duplicates
        List<Integer> result = EliteStreamsTraining.collectToImmutableNoDuplicates(
            Stream.of(1, 2, 2, 3, 3, 4, 5, 5)
        );

        assertEquals(5, result.size());
        assertTrue(result.contains(1));
        assertTrue(result.contains(2));
        assertTrue(result.contains(3));
        assertTrue(result.contains(4));
        assertTrue(result.contains(5));

        // Verify immutability
        assertThrows(UnsupportedOperationException.class, () -> result.add(6));
    }

    @Test
    @Order(20)
    @DisplayName("Problem 10: Custom Collector - Preserves Order")
    void testCustomCollector_PreservesOrder() {
        List<Integer> result = EliteStreamsTraining.collectToImmutableNoDuplicates(
            Stream.of(5, 3, 1, 4, 2, 3, 1)
        );

        // Should preserve first occurrence order
        assertEquals(Arrays.asList(5, 3, 1, 4, 2), result);
    }

    @Test
    @Order(21)
    @DisplayName("Problem 11: Optimized Pipeline - Normal Cases")
    void testOptimizedPipeline_NormalCases() {
        List<DataItem> data = Arrays.asList(
            new DataItem("hello"),
            new DataItem("world"),
            new DataItem("stream"),
            new DataItem("api"),
            new DataItem("java"),
            new DataItem("code")
        );

        List<String> result = EliteStreamsTraining.optimizedPipeline(data);

        // All results should be uppercase and > 5 characters
        assertTrue(result.stream().allMatch(s -> s.length() > 5));
        assertTrue(result.stream().allMatch(s -> s.equals(s.toUpperCase())));
    }

    @Test
    @Order(22)
    @DisplayName("Problem 11: Optimized Pipeline - Filtering Works")
    void testOptimizedPipeline_Filtering() {
        List<DataItem> data = Arrays.asList(
            new DataItem("hello"),      // Filtered (length = 5, needs > 5)
            new DataItem("hi"),         // Filtered (length <= 5)
            new DataItem(""),           // Filtered (empty)
            new DataItem(null),         // Filtered (null)
            new DataItem("stream"),     // Valid (length = 6 > 5, alphabetic)
            new DataItem("123456"),     // Filtered (not alpha, despite length > 5)
            new DataItem("world")       // Filtered (length = 5)
        );

        List<String> result = EliteStreamsTraining.optimizedPipeline(data);

        // Should only contain "STREAM" (length > 5 and alphabetic)
        assertEquals(1, result.size());
        assertTrue(result.contains("STREAM"));
    }

    @Test
    @Order(23)
    @DisplayName("Problem 12: Batching - Normal Cases")
    void testBatching_NormalCases() {
        List<Integer> items = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        List<List<Integer>> batches = EliteStreamsTraining.partitionIntoBatches(items, 3);

        assertEquals(3, batches.size());
        assertEquals(Arrays.asList(1, 2, 3), batches.get(0));
        assertEquals(Arrays.asList(4, 5, 6), batches.get(1));
        assertEquals(Arrays.asList(7, 8, 9), batches.get(2));
    }

    @Test
    @Order(24)
    @DisplayName("Problem 12: Batching - Uneven Batches")
    void testBatching_UnevenBatches() {
        List<Integer> items = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);
        List<List<Integer>> batches = EliteStreamsTraining.partitionIntoBatches(items, 3);

        assertEquals(3, batches.size());
        assertEquals(Arrays.asList(1, 2, 3), batches.get(0));
        assertEquals(Arrays.asList(4, 5, 6), batches.get(1));
        assertEquals(Arrays.asList(7, 8), batches.get(2)); // Partial batch
    }

    @Test
    @Order(25)
    @DisplayName("Problem 12: Batching - Edge Cases")
    void testBatching_EdgeCases() {
        // Empty list
        assertTrue(EliteStreamsTraining.partitionIntoBatches(new ArrayList<>(), 3).isEmpty());

        // Batch size = 0
        List<Integer> items = Arrays.asList(1, 2, 3);
        assertTrue(EliteStreamsTraining.partitionIntoBatches(items, 0).isEmpty());

        // Batch size larger than list
        List<List<Integer>> result = EliteStreamsTraining.partitionIntoBatches(items, 10);
        assertEquals(1, result.size());
        assertEquals(Arrays.asList(1, 2, 3), result.get(0));

        // Null list
        assertNotNull(EliteStreamsTraining.partitionIntoBatches(null, 3));
    }

    // ============================================================================
    // PERFORMANCE TESTS
    // ============================================================================

    @Test
    @Order(26)
    @DisplayName("Performance: Top K Frequent - Large Dataset")
    void testPerformance_TopKFrequent() {
        // Create large dataset
        List<String> largeDataset = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            largeDataset.add("word" + (i % 100)); // 100 unique words
        }

        long startTime = System.currentTimeMillis();
        List<String> result = EliteStreamsTraining.topKFrequentWords(largeDataset, 10);
        long endTime = System.currentTimeMillis();

        assertEquals(10, result.size());
        assertTrue(endTime - startTime < 1000, "Should complete in < 1 second");
    }

    @Test
    @Order(27)
    @DisplayName("Performance: Parallel Processing - Large Dataset")
    void testPerformance_ParallelProcessing() {
        // Create large transaction list
        List<Transaction> largeDataset = new ArrayList<>();
        for (int i = 0; i < 100000; i++) {
            largeDataset.add(new Transaction(
                "T" + i,
                "Category" + (i % 10),
                Math.random() * 1000
            ));
        }

        long startTime = System.currentTimeMillis();
        TransactionSummary summary = EliteStreamsTraining.processTransactionsInParallel(largeDataset);
        long endTime = System.currentTimeMillis();

        assertEquals(100000, summary.getCountByCategory().values().stream().mapToLong(Long::longValue).sum());
        assertTrue(endTime - startTime < 5000, "Should complete in < 5 seconds");
    }

    @Test
    @Order(28)
    @DisplayName("Performance: Batching - Large Dataset")
    void testPerformance_Batching() {
        List<Integer> largeDataset = new ArrayList<>();
        for (int i = 0; i < 100000; i++) {
            largeDataset.add(i);
        }

        long startTime = System.currentTimeMillis();
        List<List<Integer>> batches = EliteStreamsTraining.partitionIntoBatches(largeDataset, 100);
        long endTime = System.currentTimeMillis();

        assertEquals(1000, batches.size());
        assertTrue(endTime - startTime < 2000, "Should complete in < 2 seconds");
    }

    // ============================================================================
    // INTEGRATION TESTS
    // ============================================================================

    @Test
    @Order(29)
    @DisplayName("Integration: Complex Pipeline Combination")
    void testIntegration_ComplexPipeline() {
        // Create complex scenario combining multiple problems
        List<Employee> employees = Arrays.asList(
            new Employee("Alice", "Engineering", 100000, 5),
            new Employee("Bob", "Engineering", 120000, 8),
            new Employee("Charlie", "Sales", 80000, 3),
            new Employee("David", "Sales", 90000, 4),
            new Employee("Eve", "Marketing", 75000, 2)
        );

        // 1. Group by department
        Map<String, Map<String, List<Employee>>> grouped =
            EliteStreamsTraining.groupByDepartmentAndSeniority(employees);

        // 2. Get average salary
        Map<String, Double> avgSalaries =
            EliteStreamsTraining.averageSalaryByDepartment(employees);

        // 3. Get top 1 employee per department
        Map<String, List<Employee>> topEmployees =
            EliteStreamsTraining.topNEmployeesByDepartment(employees, 1);

        assertNotNull(grouped);
        assertNotNull(avgSalaries);
        assertNotNull(topEmployees);
        assertEquals(3, avgSalaries.size()); // 3 departments
    }

    @Test
    @Order(30)
    @DisplayName("Integration: End-to-End Stream Processing")
    void testIntegration_EndToEnd() {
        // Simulate real-world scenario: process student data
        List<Student> students = Arrays.asList(
            new Student("Alice", 92),
            new Student("Bob", 78),
            new Student("Charlie", 45),
            new Student("David", 88),
            new Student("Eve", 55),
            new Student("Frank", 95)
        );

        // 1. Partition into passing/failing
        Map<Boolean, List<Student>> partitioned =
            EliteStreamsTraining.partitionStudentsByGrade(students);

        // 2. Get names of passing students
        List<String> passingNames = partitioned.get(true).stream()
            .map(Student::getName)
            .sorted()
            .toList();

        assertEquals(4, passingNames.size());
        assertEquals("Alice", passingNames.get(0));
        assertEquals("Bob", passingNames.get(1));
        assertEquals("David", passingNames.get(2));
        assertEquals("Frank", passingNames.get(3));
    }
}
