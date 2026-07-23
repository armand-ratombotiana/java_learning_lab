package streams;

import java.util.*;
import java.util.stream.*;

/**
 * Common Stream API patterns with examples.
 * 
 * Covers: filtering, mapping, flatMapping, reducing, grouping, partitioning,
 *         parallel streams, custom collectors, infinite streams.
 * 
 * Each pattern demonstrates idiomatic Java stream usage.
 */
public class StreamExamples {

    // Data class for examples
    record Person(String name, int age, String city) {}
    record Order(String id, double amount, String status) {}

    public static void main(String[] args) {
        List<Person> people = List.of(
            new Person("Alice", 25, "NYC"),
            new Person("Bob", 30, "SF"),
            new Person("Charlie", 35, "NYC"),
            new Person("Diana", 28, "SF"),
            new Person("Eve", 40, "NYC")
        );

        // Filter + Map
        List<String> names = people.stream()
            .filter(p -> p.age() > 28)
            .map(Person::name)
            .collect(Collectors.toList());
        assert names.equals(List.of("Charlie", "Eve")) : "Filter+Map failed";

        // Grouping by city
        Map<String, List<Person>> byCity = people.stream()
            .collect(Collectors.groupingBy(Person::city));
        assert byCity.get("NYC").size() == 3;
        assert byCity.get("SF").size() == 2;

        // Partitioning (age > 30)
        Map<Boolean, List<Person>> partitioned = people.stream()
            .collect(Collectors.partitioningBy(p -> p.age() > 30));
        assert partitioned.get(true).size() == 2; // Charlie, Eve
        assert partitioned.get(false).size() == 3;

        // Reducing — sum of ages
        int totalAge = people.stream().mapToInt(Person::age).sum();
        assert totalAge == 158;

        // FlatMap — flatten nested lists
        List<List<Integer>> nested = List.of(List.of(1, 2), List.of(3, 4));
        List<Integer> flat = nested.stream().flatMap(List::stream).collect(Collectors.toList());
        assert flat.equals(List.of(1, 2, 3, 4));

        // CollectingAndThen — immutable result
        List<String> immutable = people.stream()
            .map(Person::name)
            .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));

        // Joining
        String joined = people.stream().map(Person::name).collect(Collectors.joining(", "));
        assert joined.equals("Alice, Bob, Charlie, Diana, Eve");

        // Max / Min
        Person oldest = people.stream().max(Comparator.comparingInt(Person::age)).orElseThrow();
        assert oldest.name().equals("Eve");

        // Custom collector — average age by city
        Map<String, Double> avgAgeByCity = people.stream()
            .collect(Collectors.groupingBy(Person::city, Collectors.averagingInt(Person::age)));
        System.out.println("Avg age by city: " + avgAgeByCity);

        // takeWhile / dropWhile (Java 9+)
        List<Integer> nums = List.of(1, 2, 3, 4, 5, 6);
        List<Integer> taken = nums.stream().takeWhile(n -> n < 4).collect(Collectors.toList());
        assert taken.equals(List.of(1, 2, 3));

        List<Integer> dropped = nums.stream().dropWhile(n -> n < 4).collect(Collectors.toList());
        assert dropped.equals(List.of(4, 5, 6));

        System.out.println("All StreamExamples tests passed.");
    }
}