package com.learning.lab.module04.solution;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class Solution {
    public static void main(String[] args) {
        System.out.println("=== Module 04: Streams API - Complete Solution ===\n");

        demonstrateStreamCreation();
        demonstrateIntermediateOperations();
        demonstrateTerminalOperations();
        demonstrateReductionOperations();
        demonstrateCollectors();
        demonstrateOptional();
        demonstratePrimitiveStreams();
        demonstrateAdvancedPatterns();
        demonstrateParallelStreams();
    }

    private static void demonstrateStreamCreation() {
        System.out.println("=== Stream Creation ===\n");

        fromCollection();
        fromArray();
        fromValues();
        fromIterate();
        fromGenerate();
        fromString();
        fromRandom();
    }

    private static void fromCollection() {
        System.out.println("--- From Collection ---");

        List<Integer> list = List.of(1, 2, 3, 4, 5);
        Stream<Integer> stream = list.stream();
        System.out.println("From List: " + stream.collect(Collectors.toList()));

        Set<String> set = Set.of("A", "B", "C");
        System.out.println("From Set: " + set.stream().collect(Collectors.toList()));

        Map<Integer, String> map = Map.of(1, "One", 2, "Two");
        System.out.println("From Map keys: " + map.keySet().stream().collect(Collectors.toList()));
        System.out.println("From Map values: " + map.values().stream().collect(Collectors.toList()));
        System.out.println("From Map entries: " + map.entrySet().stream()
            .map(e -> e.getKey() + ":" + e.getValue()).collect(Collectors.toList()));
        System.out.println();
    }

    private static void fromArray() {
        System.out.println("--- From Array ---");

        String[] names = {"Alice", "Bob", "Charlie"};
        System.out.println("From String array: " + Arrays.stream(names).collect(Collectors.toList()));

        int[] numbers = {1, 2, 3, 4, 5};
        System.out.println("From int array (boxed): " + Arrays.stream(numbers).boxed().collect(Collectors.toList()));

        double[] decimals = {1.1, 2.2, 3.3};
        System.out.println("From double array (boxed): " + Arrays.stream(decimals).boxed().collect(Collectors.toList()));
        System.out.println();
    }

    private static void fromValues() {
        System.out.println("--- From Values ---");

        Stream<Integer> intStream = Stream.of(1, 2, 3, 4, 5);
        System.out.println("Stream.of(int): " + intStream.collect(Collectors.toList()));

        Stream<String> strStream = Stream.of("A", "B", "C");
        System.out.println("Stream.of(String): " + strStream.collect(Collectors.toList()));

        Stream<String> mixed = Stream.ofNullable("Hello");
        System.out.println("Stream.ofNullable: " + mixed.collect(Collectors.toList()));

        Stream<Object> empty = Stream.empty();
        System.out.println("Stream.empty: " + empty.collect(Collectors.toList()));
        System.out.println();
    }

    private static void fromIterate() {
        System.out.println("--- Stream.iterate ---");

        Stream<Integer> firstFive = Stream.iterate(1, n -> n + 1).limit(5);
        System.out.println("First 5 integers: " + firstFive.collect(Collectors.toList()));

        Stream<Integer> powers = Stream.iterate(1, n -> n * 2).limit(5);
        System.out.println("Powers of 2: " + powers.collect(Collectors.toList()));

        Stream<Integer> range = Stream.iterate(0, n -> n < 10, n -> n + 1);
        System.out.println("0 to 9: " + range.collect(Collectors.toList()));

        Stream<String> alphabet = Stream.iterate("A", s -> s.length() < 26, s -> String.valueOf((char)(s.charAt(0) + 1)));
        System.out.println("Alphabet (limited): " + alphabet.limit(5).collect(Collectors.toList()));
        System.out.println();
    }

    private static void fromGenerate() {
        System.out.println("--- Stream.generate ---");

        Stream<String> repeated = Stream.generate(() -> "x").limit(5);
        System.out.println("Repeated 'x': " + repeated.collect(Collectors.toList()));

        Stream<Double> random = Stream.generate(Math::random).limit(3);
        System.out.println("Random values: " + random.map(d -> String.format("%.4f", d)).collect(Collectors.toList()));

        Stream<String> uuid = Stream.generate(UUID::randomUUID).limit(2);
        System.out.println("UUIDs: " + uuid.collect(Collectors.toList()));
        System.out.println();
    }

    private static void fromString() {
        System.out.println("--- From String ---");

        String text = "Hello";
        System.out.println("Chars from string: " + text.chars()
            .mapToObj(c -> (char)c)
            .collect(Collectors.toList()));

        System.out.println("Chars as ints: " + text.chars().boxed().collect(Collectors.toList()));
        System.out.println();
    }

    private static void fromRandom() {
        System.out.println("--- From Random ---");

        Random random = new Random();
        IntStream randomInts = random.ints(5, 1, 100);
        System.out.println("Random ints (1-100): " + randomInts.boxed().collect(Collectors.toList()));

        DoubleStream randomDoubles = random.doubles(3);
        System.out.println("Random doubles: " + randomDoubles.limit(3).boxed().collect(Collectors.toList()));
        System.out.println();
    }

    private static void demonstrateIntermediateOperations() {
        System.out.println("=== Intermediate Operations ===\n");

        demonstrateFilter();
        demonstrateMap();
        demonstrateFlatMap();
        demonstrateDistinct();
        demonstrateSorted();
        demonstrateLimitSkip();
        demonstratePeek();
    }

    private static void demonstrateFilter() {
        System.out.println("--- Filter ---");

        List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        System.out.println("Original: " + numbers);

        List<Integer> evens = numbers.stream().filter(n -> n % 2 == 0).collect(Collectors.toList());
        System.out.println("Filter (even): " + evens);

        List<Integer> oddGreaterThan5 = numbers.stream()
            .filter(n -> n % 2 != 0 && n > 5)
            .collect(Collectors.toList());
        System.out.println("Filter (odd > 5): " + oddGreaterThan5);

        List<String> names = List.of("Alice", "Bob", "Charlie", "David");
        List<String> startWithA = names.stream()
            .filter(name -> name.startsWith("A"))
            .collect(Collectors.toList());
        System.out.println("Names starting with A: " + startWithA);
        System.out.println();
    }

    private static void demonstrateMap() {
        System.out.println("--- Map ---");

        List<Integer> numbers = List.of(1, 2, 3, 4, 5);

        List<Integer> doubled = numbers.stream().map(n -> n * 2).collect(Collectors.toList());
        System.out.println("Doubled: " + doubled);

        List<String> strings = List.of("1", "2", "3");
        List<Integer> parsed = strings.stream().map(Integer::parseInt).collect(Collectors.toList());
        System.out.println("Parsed: " + parsed);

        List<String> names = List.of("alice", "bob", "charlie");
        List<String> upper = names.stream().map(String::toUpperCase).collect(Collectors.toList());
        System.out.println("Uppercase: " + upper);

        List<String> mapped = names.stream().map(name -> name.toUpperCase() + "_").collect(Collectors.toList());
        System.out.println("With suffix: " + mapped);
        System.out.println();
    }

    private static void demonstrateFlatMap() {
        System.out.println("--- FlatMap ---");

        List<List<Integer>> nested = List.of(List.of(1, 2), List.of(3, 4), List.of(5, 6));
        List<Integer> flattened = nested.stream()
            .flatMap(List::stream)
            .collect(Collectors.toList());
        System.out.println("Flattened: " + flattened);

        String[][] arrays = {{"a", "b"}, {"c", "d"}, {"e", "f"}};
        List<String> merged = Arrays.stream(arrays)
            .flatMap(Arrays::stream)
            .collect(Collectors.toList());
        System.out.println("Merged arrays: " + merged);

        List<String> words = List.of("hello world", "java streams");
        List<String> wordChars = words.stream()
            .flatMap(word -> Arrays.stream(word.split("")))
            .collect(Collectors.toList());
        System.out.println("Characters: " + wordChars);
        System.out.println();
    }

    private static void demonstrateDistinct() {
        System.out.println("--- Distinct ---");

        List<Integer> withDuplicates = List.of(1, 2, 2, 3, 3, 3, 4, 4, 4, 4);
        List<Integer> distinct = withDuplicates.stream().distinct().collect(Collectors.toList());
        System.out.println("Original: " + withDuplicates);
        System.out.println("Distinct: " + distinct);

        List<String> names = List.of("Alice", "Bob", "alice", "Charlie", "bob");
        List<String> uniqueLower = names.stream()
            .map(String::toLowerCase)
            .distinct()
            .collect(Collectors.toList());
        System.out.println("Unique lowercase: " + uniqueLower);
        System.out.println();
    }

    private static void demonstrateSorted() {
        System.out.println("--- Sorted ---");

        List<Integer> numbers = List.of(5, 2, 8, 1, 9, 3);
        System.out.println("Original: " + numbers);

        List<Integer> natural = numbers.stream().sorted().collect(Collectors.toList());
        System.out.println("Natural order: " + natural);

        List<Integer> reverse = numbers.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        System.out.println("Reverse order: " + reverse);

        List<String> names = List.of("Charlie", "Alice", "Bob");
        List<String> byLength = names.stream().sorted(Comparator.comparingInt(String::length)).collect(Collectors.toList());
        System.out.println("By length: " + byLength);

        List<String> byLengthDesc = names.stream().sorted(Comparator.comparingInt(String::length).reversed()).collect(Collectors.toList());
        System.out.println("By length desc: " + byLengthDesc);
        System.out.println();
    }

    private static void demonstrateLimitSkip() {
        System.out.println("--- Limit & Skip ---");

        List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        List<Integer> first5 = numbers.stream().limit(5).collect(Collectors.toList());
        System.out.println("First 5: " + first5);

        List<Integer> last5 = numbers.stream().skip(5).collect(Collectors.toList());
        System.out.println("Skip 5: " + last5);

        List<Integer> middle = numbers.stream().skip(3).limit(4).collect(Collectors.toList());
        System.out.println("Skip 3, limit 4: " + middle);

        List<Integer> range = Stream.iterate(0, n -> n + 1).limit(10).collect(Collectors.toList());
        System.out.println("Range 0-9: " + range);
        System.out.println();
    }

    private static void demonstratePeek() {
        System.out.println("--- Peek ---");

        List<Integer> result = new ArrayList<>();
        List<Integer> numbers = List.of(1, 2, 3, 4, 5);

        numbers.stream()
            .filter(n -> n > 2)
            .peek(n -> System.out.println("After filter: " + n))
            .map(n -> n * 2)
            .peek(n -> System.out.println("After map: " + n))
            .forEach(result::add);

        System.out.println("Result: " + result);
        System.out.println();
    }

    private static void demonstrateTerminalOperations() {
        System.out.println("=== Terminal Operations ===\n");

        demonstrateForEach();
        demonstrateCollect();
        demonstrateReduce();
        demonstrateMatch();
        demonstrateFind();
        demonstrateCount();
    }

    private static void demonstrateForEach() {
        System.out.println("--- ForEach ---");

        List<String> names = List.of("Alice", "Bob", "Charlie");
        System.out.print("forEach: ");
        names.stream().forEach(name -> System.out.print(name + " "));
        System.out.println();

        System.out.print("forEachOrdered (parallel): ");
        names.parallelStream().forEachOrdered(System.out::print);
        System.out.println();

        List<String> collected = new ArrayList<>();
        names.stream().forEach(collected::add);
        System.out.println("Collected to list: " + collected);
        System.out.println();
    }

    private static void demonstrateCollect() {
        System.out.println("--- Collect ---");

        List<String> names = List.of("Alice", "Bob", "Charlie");

        List<String> toList = names.stream().collect(Collectors.toList());
        System.out.println("toList: " + toList);

        Set<String> toSet = names.stream().map(String::toUpperCase).collect(Collectors.toSet());
        System.out.println("toSet: " + toSet);

        String joined = names.stream().collect(Collectors.joining(", "));
        System.out.println("joining: " + joined);

        Map<String, Integer> toMap = names.stream()
            .collect(Collectors.toMap(s -> s, String::length));
        System.out.println("toMap: " + toMap);

        Map<Integer, List<String>> grouping = names.stream()
            .collect(Collectors.groupingBy(String::length));
        System.out.println("groupingBy: " + grouping);
        System.out.println();
    }

    private static void demonstrateReduce() {
        System.out.println("--- Reduce ---");

        List<Integer> numbers = List.of(1, 2, 3, 4, 5);

        Optional<Integer> sum = numbers.stream().reduce(Integer::sum);
        System.out.println("Sum: " + sum.orElse(0));

        Integer product = numbers.stream().reduce(1, (a, b) -> a * b);
        System.out.println("Product: " + product);

        Optional<Integer> max = numbers.stream().reduce(Integer::max);
        System.out.println("Max: " + max.orElse(0));

        Optional<Integer> min = numbers.stream().reduce(Integer::min);
        System.out.println("Min: " + min.orElse(0));

        String concatenated = names().stream().reduce("", (a, b) -> a + b + ", ");
        System.out.println("Concat: " + concatenated.trim());
        System.out.println();
    }

    private static List<String> names() {
        return List.of("Alice", "Bob", "Charlie");
    }

    private static void demonstrateMatch() {
        System.out.println("--- Match ---");

        List<String> names = List.of("Alice", "Bob", "Charlie");

        boolean anyStartsWithA = names.stream().anyMatch(s -> s.startsWith("A"));
        System.out.println("anyMatch (starts with A): " + anyStartsWithA);

        boolean allLengthGreaterThan2 = names.stream().allMatch(s -> s.length() > 2);
        System.out.println("allMatch (length > 2): " + allLengthGreaterThan2);

        boolean noneEmpty = names.stream().noneMatch(String::isEmpty);
        System.out.println("noneMatch (empty): " + noneEmpty);

        boolean anyLongerThan10 = names.stream().anyMatch(s -> s.length() > 10);
        System.out.println("anyMatch (length > 10): " + anyLongerThan10);
        System.out.println();
    }

    private static void demonstrateFind() {
        System.out.println("--- Find ---");

        List<String> names = List.of("Alice", "Bob", "Charlie");

        Optional<String> first = names.stream().findFirst();
        System.out.println("findFirst: " + first.orElse("none"));

        Optional<String> any = names.stream().findAny();
        System.out.println("findAny: " + any.orElse("none"));

        Optional<String> firstLong = names.stream().filter(s -> s.length() > 3).findFirst();
        System.out.println("findFirst (filtered): " + firstLong.orElse("none"));

        List<String> parallelNames = List.of("Alice", "Bob", "Charlie", "David");
        Optional<String> anyParallel = parallelNames.parallelStream().findAny();
        System.out.println("findAny (parallel): " + anyParallel.orElse("none"));
        System.out.println();
    }

    private static void demonstrateCount() {
        System.out.println("--- Count ---");

        List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        long totalCount = numbers.stream().count();
        System.out.println("Total count: " + totalCount);

        long evenCount = numbers.stream().filter(n -> n % 2 == 0).count();
        System.out.println("Even count: " + evenCount);

        long distinctCount = Stream.of(1, 2, 2, 3, 3, 3).distinct().count();
        System.out.println("Distinct count: " + distinctCount);
        System.out.println();
    }

    private static void demonstrateReductionOperations() {
        System.out.println("=== Reduction Operations ===\n");

        summing();
        averaging();
        counting();
        summarizing();
        grouping();
        partitioning();
    }

    private static void summing() {
        System.out.println("--- Summing ---");

        List<Integer> numbers = List.of(1, 2, 3, 4, 5);

        int sum = numbers.stream().mapToInt(Integer::intValue).sum();
        System.out.println("Sum (mapToInt): " + sum);

        Long sumLong = numbers.stream().collect(Collectors.summingLong(Integer::longValue));
        System.out.println("Sum (summingLong): " + sumLong);

        Double sumDouble = numbers.stream().collect(Collectors.summingDouble(Double::valueOf));
        System.out.println("Sum (summingDouble): " + sumDouble);
    }

    private static void averaging() {
        System.out.println("--- Averaging ---");

        List<Integer> numbers = List.of(1, 2, 3, 4, 5);

        Double average = numbers.stream().collect(Collectors.averagingDouble(Integer::doubleValue));
        System.out.println("Average: " + average);

        Double averageInt = numbers.stream().collect(Collectors.averagingInt(Integer::intValue));
        System.out.println("Average (averagingInt): " + averageInt);
    }

    private static void counting() {
        System.out.println("--- Counting ---");

        List<String> names = List.of("Alice", "Bob", "Charlie");

        Long count = names.stream().collect(Collectors.counting());
        System.out.println("Count: " + count);
    }

    private static void summarizing() {
        System.out.println("--- Summarizing ---");

        List<Integer> numbers = List.of(1, 2, 3, 4, 5);

        IntSummaryStatistics stats = numbers.stream().collect(Collectors.summarizingInt(Integer::intValue));
        System.out.println("Statistics: " + stats);
        System.out.println("  Count: " + stats.getCount());
        System.out.println("  Sum: " + stats.getSum());
        System.out.println("  Min: " + stats.getMin());
        System.out.println("  Max: " + stats.getMax());
        System.out.println("  Average: " + stats.getAverage());
    }

    private static void grouping() {
        System.out.println("--- Grouping ---");

        List<String> names = List.of("Alice", "Bob", "Charlie", "David", "Eve");

        Map<Integer, List<String>> byLength = names.stream()
            .collect(Collectors.groupingBy(String::length));
        System.out.println("Group by length: " + byLength);

        Map<String, Long> countingByFirst = names.stream()
            .collect(Collectors.groupingBy(s -> s.substring(0, 1), Collectors.counting()));
        System.out.println("Counting by first letter: " + countingByFirst);
    }

    private static void partitioning() {
        System.out.println("--- Partitioning ---");

        List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        Map<Boolean, List<Integer>> partitioned = numbers.stream()
            .collect(Collectors.partitioningBy(n -> n % 2 == 0));
        System.out.println("Partition by even/odd: " + partitioned);

        Map<Boolean, Long> countPartitioned = numbers.stream()
            .collect(Collectors.partitioningBy(n -> n > 5, Collectors.counting()));
        System.out.println("Count (>5): " + countPartitioned);
    }

    private static void demonstrateCollectors() {
        System.out.println("=== Collectors ===\n");

        toCollection();
        mapping();
        reducing();
    }

    private static void toCollection() {
        System.out.println("--- To Specific Collections ---");

        List<Integer> numbers = List.of(1, 2, 3, 4, 5);

        LinkedList<Integer> linkedList = numbers.stream().collect(Collectors.toCollection(LinkedList::new));
        System.out.println("LinkedList: " + linkedList);

        TreeSet<Integer> treeSet = numbers.stream().collect(Collectors.toCollection(TreeSet::new));
        System.out.println("TreeSet: " + treeSet);

        HashSet<Integer> hashSet = numbers.stream().collect(Collectors.toCollection(HashSet::new));
        System.out.println("HashSet: " + hashSet);
    }

    private static void mapping() {
        System.out.println("--- Mapping Collector ---");

        List<String> names = List.of("Alice", "Bob", "Charlie");

        List<Integer> lengths = names.stream()
            .collect(Collectors.mapping(String::length, Collectors.toList()));
        System.out.println("Lengths: " + lengths);

        Set<Integer> uniqueLengths = names.stream()
            .collect(Collectors.mapping(String::length, Collectors.toSet()));
        System.out.println("Unique lengths: " + uniqueLengths);
    }

    private static void reducing() {
        System.out.println("--- Reducing Collector ---");

        List<String> names = List.of("A", "B", "C");

        String joined = names.stream().collect(Collectors.reducing((a, b) -> a + b)).orElse("");
        System.out.println("Joined: " + joined);

        Integer sum = names.stream().collect(Collectors.reducing(0, String::length, Integer::sum));
        System.out.println("Sum of lengths: " + sum);
    }

    private static void demonstrateOptional() {
        System.out.println("=== Optional ===\n");

        optionalCreation();
        optionalTransformation();
        optionalActions();
    }

    private static void optionalCreation() {
        System.out.println("--- Optional Creation ---");

        Optional<String> empty = Optional.empty();
        System.out.println("Empty: " + empty);

        Optional<String> of = Optional.of("Hello");
        System.out.println("Of: " + of.get());

        Optional<String> ofNullable = Optional.ofNullable(null);
        System.out.println("OfNullable (null): " + ofNullable);

        Optional<String> ofNullable2 = Optional.ofNullable("World");
        System.out.println("OfNullable (value): " + ofNullable2.get());
    }

    private static void optionalTransformation() {
        System.out.println("--- Optional Transformation ---");

        Optional<String> optional = Optional.of("Hello");

        Optional<Integer> mapped = optional.map(String::length);
        System.out.println("map: " + mapped.orElse(0));

        Optional<String> flatMapped = optional.flatMap(s -> Optional.of(s.toUpperCase()));
        System.out.println("flatMap: " + flatMapped.get());

        Optional<String> filtered = optional.filter(s -> s.length() > 5);
        System.out.println("filter (len>5): " + filtered.orElse("filtered out"));
    }

    private static void optionalActions() {
        System.out.println("--- Optional Actions ---");

        Optional<String> present = Optional.of("Hello");
        Optional<String> empty = Optional.empty();

        present.ifPresent(s -> System.out.println("ifPresent: " + s));

        empty.ifPresentOrElse(
            s -> System.out.println("Value: " + s),
            () -> System.out.println("ifPresentOrElse: no value")
        );

        String orElse = empty.orElse("default");
        System.out.println("orElse: " + orElse);

        String orElseGet = empty.orElseGet(() -> "generated");
        System.out.println("orElseGet: " + orElseGet);

        String orElseThrow = present.orElseThrow(IllegalStateException::new);
        System.out.println("orElseThrow: " + orElseThrow);
    }

    private static void demonstratePrimitiveStreams() {
        System.out.println("=== Primitive Streams ===\n");

        intStream();
        doubleStream();
        longStream();
        boxing();
    }

    private static void intStream() {
        System.out.println("--- IntStream ---");

        IntStream range = IntStream.range(1, 6);
        System.out.println("Range 1-5: " + range.boxed().collect(Collectors.toList()));

        IntStream rangeClosed = IntStream.rangeClosed(1, 5);
        System.out.println("RangeClosed 1-5: " + rangeClosed.boxed().collect(Collectors.toList()));

        IntStream of = IntStream.of(1, 2, 3, 4, 5);
        System.out.println("IntStream.of: " + of.boxed().collect(Collectors.toList()));
    }

    private static void doubleStream() {
        System.out.println("--- DoubleStream ---");

        DoubleStream doubles = DoubleStream.of(1.1, 2.2, 3.3);
        System.out.println("DoubleStream.of: " + doubles.boxed().collect(Collectors.toList()));

        double sum = DoubleStream.of(1.1, 2.2, 3.3).sum();
        System.out.println("Sum: " + sum);

        double average = DoubleStream.of(1.0, 2.0, 3.0).average().orElse(0);
        System.out.println("Average: " + average);
    }

    private static void longStream() {
        System.out.println("--- LongStream ---");

        LongStream longs = LongStream.of(100, 200, 300);
        System.out.println("LongStream.of: " + longs.boxed().collect(Collectors.toList()));

        long sum = LongStream.rangeClosed(1, 5).sum();
        System.out.println("Sum 1-5: " + sum);
    }

    private static void boxing() {
        System.out.println("--- Boxing ---");

        IntStream ints = IntStream.of(1, 2, 3);
        Stream<Integer> boxed = ints.boxed();
        System.out.println("Boxed: " + boxed.collect(Collectors.toList()));

        List<Integer> list = List.of(1, 2, 3);
        IntStream streamFromList = list.stream().mapToInt(Integer::intValue);
        System.out.println("mapToInt: " + streamFromList.boxed().collect(Collectors.toList()));
    }

    private static void demonstrateAdvancedPatterns() {
        System.out.println("=== Advanced Patterns ===\n");

        demonstrateLazyEvaluation();
        demonstrateShortCircuiting();
        demonstrateStatefulOperations();
    }

    private static void demonstrateLazyEvaluation() {
        System.out.println("--- Lazy Evaluation ---");

        List<Integer> numbers = List.of(1, 2, 3, 4, 5);

        long count = numbers.stream()
            .filter(n -> {
                System.out.println("Filter: " + n);
                return n > 2;
            })
            .map(n -> {
                System.out.println("Map: " + n);
                return n * 2;
            })
            .count();
        System.out.println("Final count: " + count);
    }

    private static void demonstrateShortCircuiting() {
        System.out.println("--- Short-Circuiting ---");

        List<String> names = List.of("Alice", "Bob", "Charlie", "David", "Eve");

        Optional<String> first = names.stream()
            .filter(s -> {
                System.out.println("Checking: " + s);
                return s.length() > 4;
            })
            .findFirst();
        System.out.println("First match: " + first.get());

        boolean any = names.stream()
            .anyMatch(s -> s.startsWith("A"));
        System.out.println("Any starts with A: " + any);
    }

    private static void demonstrateStatefulOperations() {
        System.out.println("--- Stateful Operations ---");

        System.out.println("distinct (stateful):");
        Stream.of(1, 2, 2, 3, 3, 3)
            .distinct()
            .forEach(System.out::print);
        System.out.println();

        System.out.println("sorted (stateful):");
        Stream.of(3, 1, 4, 1, 5)
            .sorted()
            .forEach(System.out::print);
        System.out.println();
    }

    private static void demonstrateParallelStreams() {
        System.out.println("=== Parallel Streams ===\n");

        parallelCreation();
        parallelOperations();
        parallelCaveats();
    }

    private static void parallelCreation() {
        System.out.println("--- Parallel Creation ---");

        List<Integer> numbers = List.of(1, 2, 3, 4, 5);
        Stream<Integer> parallel = numbers.parallelStream();
        System.out.println("Created parallel stream");

        IntStream parallelInt = IntStream.range(1, 100).parallel();
        System.out.println("Created parallel IntStream");
    }

    private static void parallelOperations() {
        System.out.println("--- Parallel Operations ---");

        List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        long start = System.nanoTime();
        long count = numbers.parallelStream()
            .filter(n -> {
                System.out.println("Thread: " + Thread.currentThread().getName() + " processing " + n);
                return n % 2 == 0;
            })
            .count();
        long time = System.nanoTime() - start;

        System.out.println("Even count: " + count);
        System.out.println("Time: " + time + " ns");

        long sum = numbers.parallelStream()
            .mapToLong(Integer::longValue)
            .sum();
        System.out.println("Parallel sum: " + sum);
    }

    private static void parallelCaveats() {
        System.out.println("--- Parallel Caveats ---");

        List<String> list = new ArrayList<>();
        Stream.of("a", "b", "c").parallel().forEach(list::add);
        System.out.println("Non-thread-safe collect: " + list);

        List<String> safeList = Stream.of("a", "b", "c")
            .parallel()
            .collect(Collectors.toList());
        System.out.println("Thread-safe collect: " + safeList);

        long sequentialSum = IntStream.range(1, 1000).sequential().sum();
        long parallelSum = IntStream.range(1, 1000).parallel().sum();
        System.out.println("Sequential: " + sequentialSum + ", Parallel: " + parallelSum);
    }
}