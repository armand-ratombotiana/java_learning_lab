package com.algo.lab08;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class StringAlgorithmsTest {

    @Test
    void testKMPFound() {
        List<Integer> matches = StringAlgorithms.kmpSearch("ABABDABACDABABCABAB", "ABABCABAB");
        assertEquals(List.of(10), matches);
    }

    @Test
    void testKMPMultiple() {
        List<Integer> matches = StringAlgorithms.kmpSearch("AAAAA", "AA");
        assertEquals(List.of(0, 1, 2, 3), matches);
    }

    @Test
    void testKMPNotFound() {
        List<Integer> matches = StringAlgorithms.kmpSearch("ABCDEF", "XYZ");
        assertTrue(matches.isEmpty());
    }

    @Test
    void testKMPEmptyPattern() {
        List<Integer> matches = StringAlgorithms.kmpSearch("ABC", "");
        assertTrue(matches.isEmpty());
    }

    @Test
    void testRabinKarpFound() {
        List<Integer> matches = StringAlgorithms.rabinKarp("ABABDABACDABABCABAB", "ABABCABAB");
        assertEquals(List.of(10), matches);
    }

    @Test
    void testRabinKarpMultiple() {
        List<Integer> matches = StringAlgorithms.rabinKarp("AAAAA", "AA");
        assertEquals(List.of(0, 1, 2, 3), matches);
    }

    @Test
    void testRabinKarpNotFound() {
        List<Integer> matches = StringAlgorithms.rabinKarp("ABCDEF", "XYZ");
        assertTrue(matches.isEmpty());
    }

    @Test
    void testZAlgorithm() {
        int[] z = StringAlgorithms.zAlgorithm("aaabaaab");
        assertArrayEquals(new int[]{0, 2, 1, 0, 2, 3, 1, 0}, z);
    }

    @Test
    void testZSearchFound() {
        List<Integer> matches = StringAlgorithms.zSearch("ABABDABACDABABCABAB", "ABABCABAB");
        assertEquals(List.of(10), matches);
    }

    @Test
    void testLongestPalindromicSubstring() {
        String result = StringAlgorithms.longestPalindromicSubstring("babad");
        assertTrue(result.equals("bab") || result.equals("aba"));
    }

    @Test
    void testLongestPalindromicSubstringEven() {
        assertEquals("bb", StringAlgorithms.longestPalindromicSubstring("cbbd"));
    }

    @Test
    void testLongestPalindromicSubstringSingle() {
        assertEquals("a", StringAlgorithms.longestPalindromicSubstring("a"));
    }

    @Test
    void testLongestPalindromicSubstringEmpty() {
        assertEquals("", StringAlgorithms.longestPalindromicSubstring(""));
    }

    @Test
    void testLongestPalindromicSubstringAllSame() {
        assertEquals("aaaa", StringAlgorithms.longestPalindromicSubstring("aaaa"));
    }

    @Test
    void testLongestPalindromicSubstringComplex() {
        String result = StringAlgorithms.longestPalindromicSubstring("forgeeksskeegfor");
        assertEquals("geeksskeeg", result);
    }

    @Test
    void testAllSearchMethodsAgree() {
        String text = "ABABDABACDABABCABAB";
        String pattern = "ABABCABAB";
        var kmp = StringAlgorithms.kmpSearch(text, pattern);
        var rk = StringAlgorithms.rabinKarp(text, pattern);
        var z = StringAlgorithms.zSearch(text, pattern);
        assertEquals(kmp, rk);
        assertEquals(kmp, z);
    }
}