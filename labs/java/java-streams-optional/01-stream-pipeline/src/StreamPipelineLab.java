package com.java.streams.optional.lab01;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamPipelineLab {

    public static void main(String[] args) {
        List<String> words = Arrays.asList("java", "stream", "lazy", "evaluation", "pipeline");

        // Pipeline: filter words longer than 4, uppercase, sort, collect
        List<String> result = words.stream()
                .filter(w -> w.length() > 4)
                .peek(w -> System.out.println("After filter: " + w))
                .map(String::toUpperCase)
                .peek(w -> System.out.println("After map: " + w))
                .sorted()
                .collect(Collectors.toList());

        System.out.println("Result: " + result);

        // Lazy evaluation demo — no terminal op, nothing prints
        System.out.println("\nNo terminal op — pipeline not executed:");
        words.stream()
                .filter(w -> w.length() > 4)
                .peek(System.out::println);
        System.out.println("(nothing printed above)");

        // Infinite stream with limit
        System.out.println("\nFirst 5 powers of 2:");
        Stream.iterate(1, n -> n * 2)
                .limit(5)
                .forEach(n -> System.out.print(n + " "));
        System.out.println();
    }
}
