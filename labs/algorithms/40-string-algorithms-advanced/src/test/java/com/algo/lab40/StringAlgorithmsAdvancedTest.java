package com.algo.lab40;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StringAlgorithmsAdvancedTest {

    @Test
    void testSuffixAutomatonContains() {
        SuffixAutomaton sam = new SuffixAutomaton("banana");
        assertTrue(sam.contains("ana"));
        assertTrue(sam.contains("ban"));
        assertTrue(sam.contains("na"));
        assertFalse(sam.contains("xyz"));
        assertFalse(sam.contains("bananaa"));
    }

    @Test
    void testSuffixAutomatonDistinctSubstrings() {
        SuffixAutomaton sam = new SuffixAutomaton("abc");
        assertEquals(6, sam.countDistinctSubstrings()); // a,b,c,ab,bc,abc
    }

    @Test
    void testManacherLongestPalindrome() {
        assertEquals("aba", Manacher.longestPalindrome("babad"));
        assertEquals("bb", Manacher.longestPalindrome("cbbd"));
        assertEquals("a", Manacher.longestPalindrome("a"));
        assertEquals("", Manacher.longestPalindrome(""));
    }

    @Test
    void testManacherCountPalindromes() {
        assertEquals(2, Manacher.countPalindromes("aa"));  // a, aa, a
        assertEquals(3, Manacher.countPalindromes("aba")); // a,b,a,aba
        assertEquals(1, Manacher.countPalindromes("a"));
    }

    @Test
    void testBWTTransformInverse() {
        String original = "banana";
        String transformed = BurrowsWheeler.transform(original);
        String recovered = BurrowsWheeler.inverse(transformed);
        assertEquals(original, recovered);
    }

    @Test
    void testBWTRunLength() {
        String s = "aaabbc";
        String rle = BurrowsWheeler.runLengthEncode(s);
        assertEquals("a3b2c", rle);
        assertEquals(s, BurrowsWheeler.runLengthDecode(rle));
    }

    @Test
    void testHuffmanEncoding() {
        HuffmanCoding hc = new HuffmanCoding();
        hc.buildTree("hello");
        String encoded = hc.encode();
        String decoded = hc.decode(encoded);
        assertEquals("hello", decoded);
        assertNotNull(hc.getEncodingMap().get('e'));
    }

    @Test
    void testHuffmanCompressionRatio() {
        HuffmanCoding hc = new HuffmanCoding();
        hc.buildTree("aaaaabbbbbccccdd");
        double ratio = hc.compressionRatio();
        assertTrue(ratio < 1.0);
    }

    @Test
    void testBWTEmpty() {
        String t = BurrowsWheeler.transform("");
        assertEquals("|0", t);
    }
}
