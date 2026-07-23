package com.leetcode.spacefilling;

/**
 * Custom: Z-order Curve (Morton Code)
 * Maps multi-dimensional data to one dimension while preserving locality.
 * Used in spatial indexing.
 *
 * Time Complexity: O(1) per code
 * Space Complexity: O(1)
 */
public class ZCurveOrder {

    public int mortonEncode2D(int x, int y) {
        return (spread(x) << 1) | spread(y);
    }

    public int[] mortonDecode2D(int code) {
        return new int[] { compact(code >> 1), compact(code) };
    }

    private int spread(int w) {
        int x = w & 0x0000ffff;
        x = (x | (x << 8)) & 0x00ff00ff;
        x = (x | (x << 4)) & 0x0f0f0f0f;
        x = (x | (x << 2)) & 0x33333333;
        x = (x | (x << 1)) & 0x55555555;
        return x;
    }

    private int compact(int x) {
        x &= 0x55555555;
        x = (x | (x >>> 1)) & 0x33333333;
        x = (x | (x >>> 2)) & 0x0f0f0f0f;
        x = (x | (x >>> 4)) & 0x00ff00ff;
        x = (x | (x >>> 8)) & 0x0000ffff;
        return x;
    }

    public static void main(String[] args) {
        ZCurveOrder z = new ZCurveOrder();
        int code = z.mortonEncode2D(3, 5);
        System.out.println("Morton code for (3,5): " + code);
        int[] decoded = z.mortonDecode2D(code);
        System.out.println("Decoded: (" + decoded[0] + "," + decoded[1] + ") (expected: (3,5))");

        // Verify neighboring points have close codes
        int c1 = z.mortonEncode2D(2, 5);
        int c2 = z.mortonEncode2D(3, 4);
        System.out.println("Code for (2,5): " + c1 + ", (3,4): " + c2);
    }
}
