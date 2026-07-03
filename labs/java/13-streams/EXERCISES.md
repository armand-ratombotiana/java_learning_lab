# Exercises — Java Streams

## Beginner
1. Given `List<Integer>`, use streams to find the sum of all even numbers.
2. From `List<String>`, collect all strings longer than 5 characters into a new list.
3. Convert `List<String>` to `Set<String>` of unique uppercase strings.

## Intermediate
4. Group `List<Person>` by age (`Map<Integer, List<Person>>`).
5. Find the top 3 highest-paid employees from `List<Employee>`.
6. Join all strings in a list separated by `", "` using `Collectors.joining`.

## Advanced
7. Compute word frequency from `List<String>` using `groupingBy` + `counting`.
8. Flatten a `List<List<Integer>>` matrix and find distinct primes.
9. Partition numbers into prime/non-prime using `partitioningBy`.

## Parallel Streams
10. Compare sequential vs parallel performance summing 10M integers.
11. Implement a parallel `map-reduce` word count on a large text file.

## Custom Collector
12. Write a custom collector that collects elements into a `TreeSet`.

## Edge Cases
13. What happens when an empty stream is passed to `reduce`? Test it.
14. Benchmark `findFirst()` vs `findAny()` on a parallel stream.
