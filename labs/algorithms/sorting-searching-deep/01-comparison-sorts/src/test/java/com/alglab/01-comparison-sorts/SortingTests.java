package com.alglab.comparisonsorts;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SortingTests {

    private static final Random RNG = new Random(42);

    private int[] empty;
    private int[] single;
    private int[] sorted;
    private int[] reverse;
    private int[] random;
    private int[] duplicates;
    private int[] expectedSorted;
    private int[] expectedReverse;

    @BeforeEach
    void setUp() {
        empty = new int[]{};
        single = new int[]{1};
        sorted = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        reverse = new int[]{10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
        random = RNG.ints(100, 0, 1000).toArray();
        duplicates = new int[]{5, 3, 5, 1, 3, 5, 2, 4, 1, 2};
        expectedSorted = sorted.clone();
        expectedReverse = reverse.clone();
        Arrays.sort(expectedReverse);
    }

    static Stream<Arguments> sortProvider() {
        return Stream.of(
                Arguments.of("InsertionSort"),
                Arguments.of("SelectionSort"),
                Arguments.of("BubbleSort"),
                Arguments.of("CocktailShaker"),
                Arguments.of("CombSort"),
                Arguments.of("ShellSort")
        );
    }

    private void assertSorted(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            assertTrue(arr[i - 1] <= arr[i],
                    "Array not sorted at index " + i + ": " + arr[i - 1] + " > " + arr[i]);
        }
    }

    private void assertStable(int[] original, int[] sorted, int[] values) {
        int n = original.length;
        for (int v : values) {
            int lastPos = -1;
            for (int i = 0; i < n; i++) {
                if (original[i] == v) {
                    int posInSorted = -1;
                    for (int j = 0; j < n; j++) {
                        if (sorted[j] == v) {
                            posInSorted = j;
                            break;
                        }
                    }
                    if (lastPos >= posInSorted) {
                        fail("Sort is not stable for value " + v);
                    }
                    lastPos = posInSorted;
                }
            }
        }
    }

    @Nested
    @DisplayName("Insertion Sort Tests")
    class InsertionSortTests {
        @Test
        void sortEmpty() {
            InsertionSort.sort(empty);
            assertArrayEquals(new int[]{}, empty);
        }

        @Test
        void sortSingle() {
            InsertionSort.sort(single);
            assertArrayEquals(new int[]{1}, single);
        }

        @Test
        void sortSorted() {
            int[] arr = sorted.clone();
            InsertionSort.sort(arr);
            assertArrayEquals(expectedSorted, arr);
        }

        @Test
        void sortReverse() {
            int[] arr = reverse.clone();
            InsertionSort.sort(arr);
            assertArrayEquals(expectedReverse, arr);
        }

        @Test
        void sortRandom() {
            int[] arr = random.clone();
            InsertionSort.sort(arr);
            assertSorted(arr);
        }

        @Test
        void sortDuplicates() {
            int[] arr = duplicates.clone();
            InsertionSort.sort(arr);
            assertSorted(arr);
        }

        @Test
        void sortGeneric() {
            Integer[] arr = {3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5};
            InsertionSort.sort(arr);
            for (int i = 1; i < arr.length; i++) {
                assertTrue(arr[i - 1].compareTo(arr[i]) <= 0);
            }
        }

        @Test
        void binaryInsertionSort() {
            int[] arr = random.clone();
            InsertionSort.binaryInsertionSort(arr);
            assertSorted(arr);
        }

        @Test
        void insertionSortStable() {
            int[] values = {1, 2, 3, 4, 5};
            assertStable(duplicates, Arrays.copyOf(duplicates, duplicates.length), values);
        }
    }

    @Nested
    @DisplayName("Selection Sort Tests")
    class SelectionSortTests {
        @Test
        void sortEmpty() {
            SelectionSort.sort(empty);
            assertArrayEquals(new int[]{}, empty);
        }

        @Test
        void sortSingle() {
            SelectionSort.sort(single);
            assertArrayEquals(new int[]{1}, single);
        }

        @Test
        void sortSorted() {
            int[] arr = sorted.clone();
            SelectionSort.sort(arr);
            assertArrayEquals(expectedSorted, arr);
        }

        @Test
        void sortReverse() {
            int[] arr = reverse.clone();
            SelectionSort.sort(arr);
            assertArrayEquals(expectedReverse, arr);
        }

        @Test
        void sortRandom() {
            int[] arr = random.clone();
            SelectionSort.sort(arr);
            assertSorted(arr);
        }

        @Test
        void sortDuplicates() {
            int[] arr = duplicates.clone();
            SelectionSort.sort(arr);
            assertSorted(arr);
        }

        @Test
        void stableSort() {
            int[] arr = duplicates.clone();
            SelectionSort.stableSort(arr);
            assertSorted(arr);
        }
    }

    @Nested
    @DisplayName("Bubble Sort Tests")
    class BubbleSortTests {
        @Test
        void sortEmpty() {
            BubbleSort.sort(empty);
            assertArrayEquals(new int[]{}, empty);
        }

        @Test
        void sortSingle() {
            BubbleSort.sort(single);
            assertArrayEquals(new int[]{1}, single);
        }

        @Test
        void sortSorted() {
            int[] arr = sorted.clone();
            BubbleSort.sort(arr);
            assertArrayEquals(expectedSorted, arr);
        }

        @Test
        void sortReverse() {
            int[] arr = reverse.clone();
            BubbleSort.sort(arr);
            assertArrayEquals(expectedReverse, arr);
        }

        @Test
        void sortRandom() {
            int[] arr = random.clone();
            BubbleSort.sort(arr);
            assertSorted(arr);
        }

        @Test
        void cocktailShakerSortRandom() {
            int[] arr = random.clone();
            BubbleSort.cocktailShakerSort(arr);
            assertSorted(arr);
        }

        @Test
        void combSortRandom() {
            int[] arr = random.clone();
            BubbleSort.combSort(arr);
            assertSorted(arr);
        }

        @Test
        void gnomeSortRandom() {
            int[] arr = random.clone();
            BubbleSort.gnomeSort(arr);
            assertSorted(arr);
        }
    }

    @Nested
    @DisplayName("Shell Sort Tests")
    class ShellSortTests {
        @Test
        void shellSortEmpty() {
            ShellSort.shellSort(empty);
            assertArrayEquals(new int[]{}, empty);
        }

        @Test
        void shellSortSingle() {
            ShellSort.shellSort(single);
            assertArrayEquals(new int[]{1}, single);
        }

        @Test
        void shellSortSorted() {
            int[] arr = sorted.clone();
            ShellSort.shellSort(arr);
            assertArrayEquals(expectedSorted, arr);
        }

        @Test
        void shellSortReverse() {
            int[] arr = reverse.clone();
            ShellSort.shellSort(arr);
            assertArrayEquals(expectedReverse, arr);
        }

        @Test
        void shellSortRandom() {
            int[] arr = random.clone();
            ShellSort.shellSort(arr);
            assertSorted(arr);
        }

        @Test
        void hibbardSortRandom() {
            int[] arr = random.clone();
            ShellSort.hibbardSort(arr);
            assertSorted(arr);
        }

        @Test
        void sedgewickSortRandom() {
            int[] arr = random.clone();
            ShellSort.sedgewickSort(arr);
            assertSorted(arr);
        }

        @Test
        void prattSortRandom() {
            int[] arr = random.clone();
            ShellSort.prattSort(arr);
            assertSorted(arr);
        }

        @Test
        void shellSortLargeRandom() {
            int[] arr = RNG.ints(10000, 0, 100000).toArray();
            ShellSort.shellSort(arr);
            assertSorted(arr);
        }
    }

    @Test
    @DisplayName("All algorithms produce same result as Arrays.sort")
    void allAlgorithmsMatchArraysSort() {
        int[][] arrays = {
                RNG.ints(50, 0, 100).toArray(),
                RNG.ints(50, 0, 100).toArray(),
                RNG.ints(50, 0, 100).toArray(),
                RNG.ints(50, 0, 100).toArray()
        };
        int[] reference = arrays[0].clone();
        Arrays.sort(reference);

        InsertionSort.sort(arrays[0]);
        SelectionSort.sort(arrays[1]);
        BubbleSort.sort(arrays[2]);
        ShellSort.shellSort(arrays[3]);

        assertArrayEquals(reference, arrays[0]);
        assertArrayEquals(reference, arrays[1]);
        assertArrayEquals(reference, arrays[2]);
        assertArrayEquals(reference, arrays[3]);
    }
}
