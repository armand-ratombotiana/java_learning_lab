package com.algo.lab27;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SuffixArrayTest {

    @Test
    void testSuffixArrayBanana() {
        int[] sa = SuffixArray.build("banana");
        assertArrayEquals(new int[]{5, 3, 1, 0, 4, 2}, sa);
    }

    @Test
    void testSuffixArrayAAAA() {
        int[] sa = SuffixArray.build("aaaa");
        assertArrayEquals(new int[]{3, 2, 1, 0}, sa);
    }

    @Test
    void testLCPArray() {
        int[] sa = SuffixArray.build("banana");
        int[] lcp = LCPArray.build("banana", sa);
        assertArrayEquals(new int[]{0, 1, 3, 0, 0, 2}, lcp);
    }

    @Test
    void testPatternMatches() {
        int[] sa = SuffixArray.build("banana");
        int[] matches = SuffixArrayExample.patternMatches("banana", "ana", sa);
        assertArrayEquals(new int[]{1, 3}, matches);
    }

    @Test
    void testPatternNoMatch() {
        int[] sa = SuffixArray.build("banana");
        int[] matches = SuffixArrayExample.patternMatches("banana", "xyz", sa);
        assertEquals(0, matches.length);
    }

    @Test
    void testLongestRepeatedSubstring() {
        int[] sa = SuffixArray.build("banana");
        int[] lcp = LCPArray.build("banana", sa);
        assertEquals("ana", SuffixArrayExample.longestRepeatedSubstring("banana", sa, lcp));
    }

    @Test
    void testCountDistinctSubstrings() {
        int[] sa = SuffixArray.build("banana");
        int[] lcp = LCPArray.build("banana", sa);
        assertEquals(15, SuffixArrayExample.countDistinctSubstrings(6, lcp));
    }
}
