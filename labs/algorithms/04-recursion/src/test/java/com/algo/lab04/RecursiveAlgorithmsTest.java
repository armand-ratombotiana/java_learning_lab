package com.algo.lab04;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.List;

class RecursiveAlgorithmsTest {

    @Test
    void testFactorial() {
        assertEquals(1, RecursiveAlgorithms.factorial(0));
        assertEquals(1, RecursiveAlgorithms.factorial(1));
        assertEquals(2, RecursiveAlgorithms.factorial(2));
        assertEquals(6, RecursiveAlgorithms.factorial(3));
        assertEquals(24, RecursiveAlgorithms.factorial(4));
        assertEquals(120, RecursiveAlgorithms.factorial(5));
        assertEquals(3628800, RecursiveAlgorithms.factorial(10));
    }

    @Test
    void testFactorialNegative() {
        assertThrows(IllegalArgumentException.class, () -> RecursiveAlgorithms.factorial(-1));
    }

    @Test
    void testFibonacci() {
        assertEquals(0, RecursiveAlgorithms.fibonacci(0));
        assertEquals(1, RecursiveAlgorithms.fibonacci(1));
        assertEquals(1, RecursiveAlgorithms.fibonacci(2));
        assertEquals(2, RecursiveAlgorithms.fibonacci(3));
        assertEquals(3, RecursiveAlgorithms.fibonacci(4));
        assertEquals(5, RecursiveAlgorithms.fibonacci(5));
        assertEquals(55, RecursiveAlgorithms.fibonacci(10));
    }

    @Test
    void testFibonacciNegative() {
        assertThrows(IllegalArgumentException.class, () -> RecursiveAlgorithms.fibonacci(-1));
    }

    @Test
    void testTowerOfHanoiOneDisk() {
        List<String> steps = RecursiveAlgorithms.towerOfHanoi(1, 'A', 'C', 'B');
        assertEquals(1, steps.size());
        assertTrue(steps.get(0).contains("Move disk 1 from A to C"));
    }

    @Test
    void testTowerOfHanoiThreeDisks() {
        List<String> steps = RecursiveAlgorithms.towerOfHanoi(3, 'A', 'C', 'B');
        assertEquals(7, steps.size());
    }

    @Test
    void testTowerOfHanoiCount() {
        int disks = 5;
        List<String> steps = RecursiveAlgorithms.towerOfHanoi(disks, 'A', 'C', 'B');
        assertEquals((1 << disks) - 1, steps.size());
    }

    @Test
    void testSubsets() {
        List<List<Integer>> subsets = RecursiveAlgorithms.subsets(Arrays.asList(1, 2));
        assertEquals(4, subsets.size());
    }

    @Test
    void testSubsetsCount() {
        List<List<Integer>> subsets = RecursiveAlgorithms.subsets(Arrays.asList(1, 2, 3, 4));
        assertEquals(16, subsets.size());
    }

    @Test
    void testSubsetsEmpty() {
        List<List<Integer>> subsets = RecursiveAlgorithms.subsets(Arrays.asList());
        assertEquals(1, subsets.size());
        assertTrue(subsets.get(0).isEmpty());
    }

    @Test
    void testSubsetsContainsEmptySet() {
        List<List<Integer>> subsets = RecursiveAlgorithms.subsets(Arrays.asList(1, 2, 3));
        assertTrue(subsets.contains(Arrays.asList()));
    }

    @Test
    void testPermutations() {
        List<List<Integer>> perms = RecursiveAlgorithms.permutations(Arrays.asList(1, 2, 3));
        assertEquals(6, perms.size());
    }

    @Test
    void testPermutationsCount() {
        List<List<Character>> perms = RecursiveAlgorithms.permutations(Arrays.asList('A', 'B', 'C', 'D'));
        assertEquals(24, perms.size());
    }

    @Test
    void testPermutationsSingle() {
        List<List<String>> perms = RecursiveAlgorithms.permutations(Arrays.asList("X"));
        assertEquals(1, perms.size());
        assertEquals("X", perms.get(0).get(0));
    }

    @Test
    void testPermutationsEachHasAllElements() {
        List<List<Integer>> perms = RecursiveAlgorithms.permutations(Arrays.asList(1, 2, 3, 4));
        for (List<Integer> p : perms) {
            assertEquals(4, p.size());
            assertTrue(p.containsAll(Arrays.asList(1, 2, 3, 4)));
        }
    }
}