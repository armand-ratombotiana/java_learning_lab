package com.algo.lab26;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class BitManipulationTest {

    @Test
    void testIsPowerOfTwo() {
        assertTrue(BitTricks.isPowerOfTwo(1));
        assertTrue(BitTricks.isPowerOfTwo(2));
        assertTrue(BitTricks.isPowerOfTwo(1024));
        assertFalse(BitTricks.isPowerOfTwo(0));
        assertFalse(BitTricks.isPowerOfTwo(3));
        assertFalse(BitTricks.isPowerOfTwo(-8));
    }

    @Test
    void testCountBits() {
        assertEquals(0, BitTricks.countBits(0));
        assertEquals(1, BitTricks.countBits(1));
        assertEquals(3, BitTricks.countBits(42));
        assertEquals(8, BitTricks.countBits(255));
    }

    @Test
    void testReverseBits() {
        assertEquals(0, BitTricks.reverseBits(0));
        assertEquals(-1, BitTricks.reverseBits(-1));
        assertEquals(0x80000000, BitTricks.reverseBits(1));
    }

    @Test
    void testNextPowerOfTwo() {
        assertEquals(1, BitTricks.nextPowerOfTwo(1));
        assertEquals(4, BitTricks.nextPowerOfTwo(3));
        assertEquals(8, BitTricks.nextPowerOfTwo(5));
        assertEquals(16, BitTricks.nextPowerOfTwo(16));
    }

    @Test
    void testGrayCode() {
        assertEquals(1, GrayCode.binaryToGray(1));
        assertEquals(3, GrayCode.binaryToGray(2));
        assertEquals(1, GrayCode.grayToBinary(1));
        assertEquals(3, GrayCode.grayToBinary(2));
    }

    @Test
    void testGraySequence() {
        List<Integer> seq = GrayCode.generateSequence(3);
        assertEquals(8, seq.size());
        assertEquals(0, (int) seq.get(0));
        assertEquals(7, (int) seq.get(seq.size() - 1));
    }

    @Test
    void testXorBasis() {
        XorBasis basis = new XorBasis();
        basis.insert(5);
        basis.insert(3);
        basis.insert(7);
        assertTrue(basis.queryMax() > 0);
    }

    @Test
    void testTsp() {
        int[][] dist = {
            {0, 10, 15, 20},
            {10, 0, 35, 25},
            {15, 35, 0, 30},
            {20, 25, 30, 0}
        };
        assertEquals(80, BitDpTsp.solve(dist));
    }

    @Test
    void testSubsetEnumeration() {
        List<Integer> subsets = SubsetEnumeration.enumerateAll(3);
        assertEquals(8, subsets.size());
        List<Integer> submasks = SubsetEnumeration.enumerateSubmasks(5);
        assertTrue(submasks.contains(5));
        assertTrue(submasks.contains(4));
        assertTrue(submasks.contains(1));
        assertTrue(submasks.contains(0));
    }
}
