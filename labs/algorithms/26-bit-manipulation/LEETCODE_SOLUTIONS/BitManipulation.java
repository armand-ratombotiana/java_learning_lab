package com.algorithms.bitmanip;

/**
 * Custom: Bit Manipulation Utilities
 * Common bit tricks and operations.
 *
 * Time Complexity: O(1) per operation
 * Space Complexity: O(1)
 */
public class BitManipulation {

    public boolean getBit(int num, int i) {
        return (num & (1 << i)) != 0;
    }

    public int setBit(int num, int i) {
        return num | (1 << i);
    }

    public int clearBit(int num, int i) {
        return num & ~(1 << i);
    }

    public int toggleBit(int num, int i) {
        return num ^ (1 << i);
    }

    // LC 136: Single Number
    public int singleNumber(int[] nums) {
        int result = 0;
        for (int n : nums) result ^= n;
        return result;
    }

    // LC 191: Number of 1 Bits
    public int hammingWeight(int n) {
        int count = 0;
        while (n != 0) {
            count++;
            n &= (n - 1);
        }
        return count;
    }

    // LC 231: Power of Two
    public boolean isPowerOfTwo(int n) {
        return n > 0 && (n & (n - 1)) == 0;
    }

    public static void main(String[] args) {
        BitManipulation bm = new BitManipulation();
        System.out.println("Get bit 2 of 5 (101): " + bm.getBit(5, 2) + " (expected: true)");
        System.out.println("Set bit 0 of 4 (100): " + bm.setBit(4, 0) + " (expected: 5)");
        System.out.println("Clear bit 2 of 7 (111): " + bm.clearBit(7, 2) + " (expected: 3)");
        System.out.println("Toggle bit 0 of 4 (100): " + bm.toggleBit(4, 0) + " (expected: 5)");

        System.out.println("Single number [4,1,2,1,2]: " + bm.singleNumber(new int[]{4,1,2,1,2}) + " (expected: 4)");
        System.out.println("Hamming weight of 11 (1011): " + bm.hammingWeight(11) + " (expected: 3)");
        System.out.println("Is power of two 16: " + bm.isPowerOfTwo(16) + " (expected: true)");
        System.out.println("Is power of two 0: " + bm.isPowerOfTwo(0) + " (expected: false)");
    }
}
