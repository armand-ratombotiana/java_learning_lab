package com.algo.lab16;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class StringMatchingTest {

    @Test
    void testKMPBasic() {
        assertEquals(List.of(2), KMP.search("hello", "ll"));
    }

    @Test
    void testKMPMultipleMatches() {
        assertEquals(List.of(0, 5), KMP.search("aaaaa", "aa"));
    }

    @Test
    void testKMPNoMatch() {
        assertTrue(KMP.search("abc", "z").isEmpty());
    }

    @Test
    void testKMPEmptyPattern() {
        assertTrue(KMP.search("abc", "").isEmpty());
    }

    @Test
    void testKMPNullInputs() {
        assertTrue(KMP.search(null, "a").isEmpty());
        assertTrue(KMP.search("a", null).isEmpty());
    }

    @Test
    void testKMPExactMatch() {
        assertEquals(List.of(0), KMP.search("abc", "abc"));
    }

    @Test
    void testKMPPatternLargerThanText() {
        assertTrue(KMP.search("a", "abc").isEmpty());
    }

    @Test
    void testKMPLPSArray() {
        assertArrayEquals(new int[]{0, 0, 0, 0}, KMP.computeLPS("abcd"));
        assertArrayEquals(new int[]{0, 1, 0, 1, 2, 0}, KMP.computeLPS("aababa"));
    }

    @Test
    void testBoyerMooreBasic() {
        assertEquals(List.of(2), BoyerMoore.search("hello", "ll"));
    }

    @Test
    void testBoyerMooreNoMatch() {
        assertTrue(BoyerMoore.search("abcdef", "xyz").isEmpty());
    }

    @Test
    void testBoyerMooreEmpty() {
        assertTrue(BoyerMoore.search("", "a").isEmpty());
        assertTrue(BoyerMoore.search("a", "").isEmpty());
    }

    @Test
    void testBoyerMooreSingleChar() {
        assertEquals(List.of(0, 2), BoyerMoore.search("aba", "a"));
    }

    @Test
    void testRabinKarpBasic() {
        assertEquals(List.of(4), RabinKarp.search("abcdef", "ef"));
    }

    @Test
    void testRabinKarpMultiple() {
        assertEquals(List.of(0, 3, 6), RabinKarp.search("aaabaaab", "aaa"));
    }

    @Test
    void testRabinKarpNoMatch() {
        assertTrue(RabinKarp.search("abc", "def").isEmpty());
    }

    @Test
    void testRabinKarpEdgeCases() {
        assertTrue(RabinKarp.search(null, "a").isEmpty());
        assertTrue(RabinKarp.search("a", null).isEmpty());
        assertTrue(RabinKarp.search("a", "").isEmpty());
    }

    @Test
    void testZAlgorithmBasic() {
        assertEquals(List.of(2), ZAlgorithm.search("hello", "ll"));
    }

    @Test
    void testZAlgorithmMultiple() {
        assertEquals(List.of(0, 3), ZAlgorithm.search("ababa", "aba"));
    }

    @Test
    void testZAlgorithmNoMatch() {
        assertTrue(ZAlgorithm.search("abc", "def").isEmpty());
    }

    @Test
    void testZAlgorithmEdgeCases() {
        assertTrue(ZAlgorithm.search(null, "a").isEmpty());
        assertTrue(ZAlgorithm.search("a", null).isEmpty());
    }

    @Test
    void testZArray() {
        int[] z = ZAlgorithm.computeZ("aaaa");
        assertArrayEquals(new int[]{0, 3, 2, 1}, z);
    }

    @Test
    void testAhoCorasickBasic() {
        AhoCorasick ac = new AhoCorasick();
        ac.addPattern("he");
        ac.addPattern("she");
        ac.addPattern("his");
        ac.addPattern("hers");
        ac.buildFailureLinks();
        Map<String, List<Integer>> result = ac.search("ushers");
        assertEquals(List.of(1), result.get("she"));
        assertEquals(List.of(2), result.get("he"));
        assertEquals(List.of(2), result.get("hers"));
    }

    @Test
    void testAhoCorasickEmptyPatterns() {
        AhoCorasick ac = new AhoCorasick();
        ac.addPattern("");
        ac.buildFailureLinks();
        assertTrue(ac.search("text").isEmpty());
    }

    @Test
    void testAhoCorasickNoMatch() {
        AhoCorasick ac = new AhoCorasick();
        ac.addPattern("xyz");
        ac.buildFailureLinks();
        assertTrue(ac.search("abcdef").isEmpty());
    }

    @Test
    void testAhoCorasickOverlapping() {
        AhoCorasick ac = new AhoCorasick();
        ac.addPattern("aa");
        ac.addPattern("aaa");
        ac.buildFailureLinks();
        Map<String, List<Integer>> result = ac.search("aaaa");
        assertEquals(2, result.size());
        assertFalse(result.get("aa").isEmpty());
    }
}
