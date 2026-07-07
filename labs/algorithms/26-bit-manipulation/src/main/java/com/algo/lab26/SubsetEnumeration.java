package com.algo.lab26;

import java.util.ArrayList;
import java.util.List;

public class SubsetEnumeration {

    private SubsetEnumeration() {}

    public static List<Integer> enumerateAll(int n) {
        List<Integer> result = new ArrayList<>(1 << n);
        for (int mask = 0; mask < (1 << n); mask++) {
            result.add(mask);
        }
        return result;
    }

    public static List<Integer> enumerateSubmasks(int mask) {
        List<Integer> result = new ArrayList<>();
        int sub = mask;
        while (true) {
            result.add(sub);
            if (sub == 0) break;
            sub = (sub - 1) & mask;
        }
        return result;
    }
}
