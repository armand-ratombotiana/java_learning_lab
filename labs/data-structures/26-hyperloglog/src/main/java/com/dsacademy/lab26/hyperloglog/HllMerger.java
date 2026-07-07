package com.dsacademy.lab26.hyperloglog;

import java.util.ArrayList;
import java.util.List;

public class HllMerger {

    public static HyperLogLog merge(List<HyperLogLog> sketches) {
        if (sketches == null || sketches.isEmpty()) {
            throw new IllegalArgumentException("Empty sketch list");
        }
        int precision = sketches.get(0).getPrecision();
        HyperLogLog result = new HyperLogLog(precision);
        for (HyperLogLog sketch : sketches) {
            if (sketch.getPrecision() != precision) {
                throw new IllegalArgumentException("Precision mismatch");
            }
            result.merge(sketch);
        }
        return result;
    }
}
