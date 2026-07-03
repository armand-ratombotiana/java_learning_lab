package com.learning.functional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementations for Advanced Streams and Collectors exercises.
 */
public class AdvancedStreamsAndCollectors {

    public record User(int id, String name, int age, String department, double salary) {}

    // =========================================================================
    // 1. Grouping and Downstream Collectors
    // =========================================================================
    public static Map<String, List<User>> groupUsersByDepartment(List<User> users) {
        return users.stream()
                .collect(Collectors.groupingBy(User::department));
    }

    public static Map<String, Double> getAverageSalaryByDepartment(List<User> users) {
        return users.stream()
                .collect(Collectors.groupingBy(
                        User::department,
                        Collectors.averagingDouble(User::salary)
                ));
    }

    public static Map<String, Optional<User>> getHighestPaidUserByDepartment(List<User> users) {
        return users.stream()
                .collect(Collectors.groupingBy(
                        User::department,
                        Collectors.maxBy(Comparator.comparingDouble(User::salary))
                ));
    }

    // =========================================================================
    // 2. Partitioning
    // =========================================================================
    public static Map<Boolean, List<User>> partitionByAge(List<User> users, int ageThreshold) {
        return users.stream()
                .collect(Collectors.partitioningBy(u -> u.age() >= ageThreshold));
    }

    // =========================================================================
    // 3. FlatMap operations
    // =========================================================================
    public record Order(int orderId, List<String> items) {}

    public static List<String> getAllUniqueItems(List<Order> orders) {
        return orders.stream()
                .flatMap(order -> order.items().stream())
                .distinct()
                .collect(Collectors.toList());
    }
    
    // =========================================================================
    // 4. Custom String Joining
    // =========================================================================
    public static String getUserNamesCommaSeparated(List<User> users) {
        return users.stream()
                .map(User::name)
                .collect(Collectors.joining(", ", "Users: [", "]"));
    }
}