package com.algo.lab32;

public class InclusionExclusion {

    public static long countNotInAny(int[]... propertySets) {
        int k = propertySets.length;
        long total = 0;
        for (int mask = 0; mask < (1 << k); mask++) {
            long intersection = Long.MAX_VALUE;
            for (int i = 0; i < k; i++) {
                if ((mask & (1 << i)) != 0) {
                    intersection = intersection == Long.MAX_VALUE
                        ? propertySets[i].length
                        : Math.min(intersection, propertySets[i].length);
                }
            }
            if (intersection == Long.MAX_VALUE) intersection = 100;
            if (Integer.bitCount(mask) % 2 == 0) {
                total += intersection;
            } else {
                total -= intersection;
            }
        }
        return total;
    }
}
