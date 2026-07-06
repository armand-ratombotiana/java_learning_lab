package com.algo.lab20;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class RandomizedTest {

    @Test
    void testQuickSelectBasic() {
        int[] arr = {3, 1, 4, 1, 5, 9, 2, 6};
        assertEquals(1, RandomizedQuickSelect.select(arr, 0));
        assertEquals(2, RandomizedQuickSelect.select(arr, 1));
        assertEquals(9, RandomizedQuickSelect.select(arr, 7));
    }

    @Test
    void testQuickSelectSingle() {
        int[] arr = {42};
        assertEquals(42, RandomizedQuickSelect.select(arr, 0));
    }

    @Test
    void testQuickSelectThrows() {
        assertThrows(IllegalArgumentException.class, () -> RandomizedQuickSelect.select(null, 0));
        assertThrows(IllegalArgumentException.class, () -> RandomizedQuickSelect.select(new int[]{1}, 1));
    }

    @Test
    void testReservoirSamplingBasic() {
        int[] stream = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int[] sample = ReservoirSampling.sample(stream, 3);
        assertEquals(3, sample.length);
    }

    @Test
    void testReservoirSamplingAll() {
        int[] stream = {1, 2, 3};
        int[] sample = ReservoirSampling.sample(stream, 5);
        assertEquals(3, sample.length);
    }

    @Test
    void testReservoirSamplingEmpty() {
        assertEquals(0, ReservoirSampling.sample(new int[0], 3).length);
        assertEquals(0, ReservoirSampling.sample(null, 3).length);
        assertEquals(0, ReservoirSampling.sample(new int[]{1}, 0).length);
    }

    @Test
    void testFisherYatesShuffle() {
        int[] arr = {1, 2, 3, 4, 5};
        int[] copy = arr.clone();
        FisherYatesShuffle.shuffle(arr);
        Arrays.sort(arr);
        assertArrayEquals(copy, arr);
    }

    @Test
    void testFisherYatesEmpty() {
        int[] arr = {};
        FisherYatesShuffle.shuffle(arr);
        assertEquals(0, arr.length);
    }

    @Test
    void testFisherYatesSingle() {
        int[] arr = {1};
        FisherYatesShuffle.shuffle(arr);
        assertEquals(1, arr[0]);
    }

    @Test
    void testFisherYatesShuffledCopy() {
        int[] arr = {1, 2, 3, 4, 5};
        int[] copy = FisherYatesShuffle.shuffledCopy(arr);
        assertNotSame(arr, copy);
        Arrays.sort(copy);
        assertArrayEquals(arr, copy);
    }

    @Test
    void testFreivaldsCheckerCorrect() {
        int[][] A = {{1, 2}, {3, 4}};
        int[][] B = {{5, 6}, {7, 8}};
        int[][] C = {{19, 22}, {43, 50}};
        assertTrue(FreivaldsChecker.check(A, B, C, 10));
    }

    @Test
    void testFreivaldsCheckerIncorrect() {
        int[][] A = {{1, 2}, {3, 4}};
        int[][] B = {{5, 6}, {7, 8}};
        int[][] C = {{0, 0}, {0, 0}};
        assertFalse(FreivaldsChecker.check(A, B, C, 10));
    }

    @Test
    void testFreivaldsCheckerEmpty() {
        int[][] A = {};
        assertTrue(FreivaldsChecker.check(A, A, A, 5));
    }

    @Test
    void testKargerMinCut() {
        int[][] graph = {
            {0, 1, 1, 0},
            {1, 0, 1, 1},
            {1, 1, 0, 1},
            {0, 1, 1, 0}
        };
        int cut = KargerMinCut.minCut(graph);
        assertTrue(cut > 0);
    }

    @Test
    void testKargerMinCutTriangular() {
        int[][] graph = {
            {0, 1, 1},
            {1, 0, 1},
            {1, 1, 0}
        };
        int cut = KargerMinCut.minCut(graph);
        assertEquals(2, cut);
    }
}
