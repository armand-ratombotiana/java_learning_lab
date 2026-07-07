package com.algo.lab26;

import java.util.ArrayList;
import java.util.List;

public class GrayCode {

    private GrayCode() {}

    public static int binaryToGray(int binary) {
        return binary ^ (binary >> 1);
    }

    public static int grayToBinary(int gray) {
        int mask;
        for (mask = gray >> 1; mask != 0; mask >>= 1) {
            gray ^= mask;
        }
        return gray;
    }

    public static List<Integer> generateSequence(int bits) {
        List<Integer> result = new ArrayList<>(1 << bits);
        for (int i = 0; i < (1 << bits); i++) {
            result.add(i ^ (i >> 1));
        }
        return result;
    }
}
