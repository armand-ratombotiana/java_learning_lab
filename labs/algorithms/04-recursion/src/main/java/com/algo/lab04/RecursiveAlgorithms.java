package com.algo.lab04;

import java.util.ArrayList;
import java.util.List;

/**
 * Classic recursive algorithms.
 *
 * Factorial: O(n) time, O(n) stack space
 * Fibonacci (naive): O(2^n) time, O(n) stack space
 * Tower of Hanoi: O(2^n) time, O(n) stack space
 * Subset Generation: O(n * 2^n) time, O(n) space
 * Permutations: O(n * n!) time, O(n) space
 */
public class RecursiveAlgorithms {

    private RecursiveAlgorithms() {}

    public static long factorial(int n) {
        if (n < 0) throw new IllegalArgumentException("n must be non-negative");
        if (n <= 1) return 1;
        return n * factorial(n - 1);
    }

    public static long fibonacci(int n) {
        if (n < 0) throw new IllegalArgumentException("n must be non-negative");
        if (n <= 1) return n;
        return fibonacci(n - 1) + fibonacci(n - 2);
    }

    public static List<String> towerOfHanoi(int n, char from, char to, char aux) {
        List<String> steps = new ArrayList<>();
        towerOfHanoi(n, from, to, aux, steps);
        return steps;
    }

    private static void towerOfHanoi(int n, char from, char to, char aux, List<String> steps) {
        if (n == 1) {
            steps.add("Move disk 1 from " + from + " to " + to);
            return;
        }
        towerOfHanoi(n - 1, from, aux, to, steps);
        steps.add("Move disk " + n + " from " + from + " to " + to);
        towerOfHanoi(n - 1, aux, to, from, steps);
    }

    public static <T> List<List<T>> subsets(List<T> elements) {
        List<List<T>> result = new ArrayList<>();
        subsets(elements, 0, new ArrayList<>(), result);
        return result;
    }

    private static <T> void subsets(List<T> elements, int idx, List<T> current, List<List<T>> result) {
        if (idx == elements.size()) {
            result.add(new ArrayList<>(current));
            return;
        }
        subsets(elements, idx + 1, current, result);
        current.add(elements.get(idx));
        subsets(elements, idx + 1, current, result);
        current.remove(current.size() - 1);
    }

    public static <T> List<List<T>> permutations(List<T> elements) {
        List<List<T>> result = new ArrayList<>();
        permute(elements, 0, result);
        return result;
    }

    private static <T> void permute(List<T> elements, int start, List<List<T>> result) {
        if (start == elements.size() - 1) {
            result.add(new ArrayList<>(elements));
            return;
        }
        for (int i = start; i < elements.size(); i++) {
            swap(elements, start, i);
            permute(elements, start + 1, result);
            swap(elements, start, i);
        }
    }

    private static <T> void swap(List<T> list, int i, int j) {
        T temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }
}