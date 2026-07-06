package com.algo.lab22;

/**
 * Nim game solver using perfect play via XOR (nim-sum).
 * A position is losing if the XOR of all heap sizes is 0.
 * Time: O(n), Space: O(1)
 */
public class NimSolver {

    private NimSolver() {}

    public static boolean isWinningPosition(int[] heaps) {
        int xor = 0;
        for (int h : heaps) xor ^= h;
        return xor != 0;
    }

    public static int[] findWinningMove(int[] heaps) {
        if (!isWinningPosition(heaps)) return null;
        int xor = 0;
        for (int h : heaps) xor ^= h;
        for (int i = 0; i < heaps.length; i++) {
            int target = heaps[i] ^ xor;
            if (target < heaps[i]) {
                return new int[]{i, heaps[i] - target};
            }
        }
        return null;
    }

    public static int[] makeMove(int[] heaps, int heapIdx, int remove) {
        int[] result = heaps.clone();
        result[heapIdx] -= remove;
        return result;
    }

    public static int[] optimalMove(int[] heaps) {
        int[] move = findWinningMove(heaps);
        if (move != null) return move;
        for (int i = 0; i < heaps.length; i++) {
            if (heaps[i] > 0) return new int[]{i, 1};
        }
        return null;
    }
}
