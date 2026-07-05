package com.algo.lab04;

import java.util.Arrays;
import java.util.List;

public class RecursionExample {
    public static void main(String[] args) {
        System.out.println("=== Recursion Demo ===\n");

        System.out.println("--- Factorial ---");
        for (int n = 0; n <= 10; n++) {
            System.out.printf("%d! = %d%n", n, RecursiveAlgorithms.factorial(n));
        }

        System.out.println("\n--- Fibonacci ---");
        for (int n = 0; n <= 15; n++) {
            System.out.printf("F(%d) = %d%n", n, RecursiveAlgorithms.fibonacci(n));
        }

        System.out.println("\n--- Tower of Hanoi (3 disks) ---");
        RecursiveAlgorithms.towerOfHanoi(3, 'A', 'C', 'B')
            .forEach(System.out::println);

        System.out.println("\n--- Subsets of {1, 2, 3} ---");
        List<List<Integer>> subsets = RecursiveAlgorithms.subsets(Arrays.asList(1, 2, 3));
        subsets.forEach(System.out::println);
        System.out.println("Total subsets: " + subsets.size());

        System.out.println("\n--- Permutations of {A, B, C} ---");
        List<List<String>> perms = RecursiveAlgorithms.permutations(Arrays.asList("A", "B", "C"));
        perms.forEach(System.out::println);
        System.out.println("Total permutations: " + perms.size());
    }
}