package com.learning.lab13;

import java.util.*;
import java.util.stream.*;

/**
 * Demonstrates Collectors: toList, toSet, toMap, joining, groupingBy, partitioningBy.
 */
public class CollectorsExample {

    public static void showCollectors() {
        System.out.println("=== Collectors ===");

        List<Person> people = List.of(
            new Person("Alice", "IT", 50000),
            new Person("Bob", "HR", 45000),
            new Person("Charlie", "IT", 60000),
            new Person("Diana", "HR", 48000),
            new Person("Eve", "Sales", 55000)
        );

        Map<String, List<Person>> byDept = people.stream()
            .collect(Collectors.groupingBy(Person::dept));
        System.out.println("Grouped by department:");
        byDept.forEach((dept, empList) -> 
            System.out.println("  " + dept + ": " + empList));

        Map<Boolean, List<Person>> highEarners = people.stream()
            .collect(Collectors.partitioningBy(p -> p.salary() > 50000));
        System.out.println("Partitioned by salary > 50000: " + highEarners);

        String joined = people.stream()
            .map(Person::name)
            .collect(Collectors.joining(", "));
        System.out.println("Names joined: " + joined);

        Map<String, Integer> nameToSalary = people.stream()
            .collect(Collectors.toMap(Person::name, Person::salary));
        System.out.println("toMap: " + nameToSalary);

        DoubleSummaryStatistics stats = people.stream()
            .collect(Collectors.summarizingDouble(Person::salary));
        System.out.println("Salary stats: " + stats);
    }
}

record Person(String name, String dept, int salary) {}
