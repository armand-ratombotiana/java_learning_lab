package com.java.module.foreign;

import java.util.Arrays;
import java.util.random.RandomGenerator;

/**
 * Lab 03: Vector API — SIMD operations, species, reductions.
 *
 * NOTE: This file is a conceptual demo for the Vector API.
 * Real vector operations require jdk.incubator.vector module.
 * The implementations below use scalar loops as fallback
 * when the vector module is unavailable.
 */
public class VectorApiLab {

    // --- Simulated vector operation (scalar fallback) ---
    public float[] addArrays(float[] a, float[] b) {
        int n = Math.min(a.length, b.length);
        float[] result = new float[n];
        for (int i = 0; i < n; i++) {
            result[i] = a[i] + b[i];
        }
        return result;
    }

    // --- Dot product (scalar fallback) ---
    public float dotProduct(float[] a, float[] b) {
        int n = Math.min(a.length, b.length);
        float sum = 0;
        for (int i = 0; i < n; i++) {
            sum += a[i] * b[i];
        }
        return sum;
    }

    // --- Simulated fused multiply-add ---
    public float[] fma(float[] a, float[] b, float[] c) {
        int n = Math.min(Math.min(a.length, b.length), c.length);
        float[] result = new float[n];
        for (int i = 0; i < n; i++) {
            result[i] = Math.fma(a[i], b[i], c[i]);
        }
        return result;
    }

    // --- Simulated masked operation ---
    public float[] addWithMask(float[] a, float[] b, boolean[] mask) {
        int n = Math.min(Math.min(a.length, b.length), mask.length);
        float[] result = new float[n];
        for (int i = 0; i < n; i++) {
            result[i] = mask[i] ? a[i] + b[i] : a[i];
        }
        return result;
    }

    // --- Simulated vector reduction (max) ---
    public float maxElement(float[] arr) {
        float max = Float.NEGATIVE_INFINITY;
        for (float v : arr) {
            if (v > max) max = v;
        }
        return max;
    }

    // --- In a real Vector API implementation ---
    // import jdk.incubator.vector.*;
    //
    // static final VectorSpecies<Float> SPECIES = FloatVector.SPECIES_PREFERRED;
    //
    // public float[] addArraysVector(float[] a, float[] b) {
    //     int n = Math.min(a.length, b.length);
    //     float[] result = new float[n];
    //     int i = 0;
    //     for (; i <= n - SPECIES.length(); i += SPECIES.length()) {
    //         var va = FloatVector.fromArray(SPECIES, a, i);
    //         var vb = FloatVector.fromArray(SPECIES, b, i);
    //         va.add(vb).intoArray(result, i);
    //     }
    //     for (; i < n; i++) result[i] = a[i] + b[i];
    //     return result;
    // }

    // --- Demo ---
    public static void main(String[] args) {
        var lab = new VectorApiLab();
        var rng = RandomGenerator.getDefault();

        float[] a = rng.ints(16, 0, 100).mapToObj(i -> (float) i)
                .mapToDouble(Float::doubleValue).collect(
                        () -> new float[16],
                        (arr, d) -> arr[(int) d] = (float) d,
                        (arr1, arr2) -> {});

        // Rebuild arrays differently for demo
        float[] arr1 = {1, 2, 3, 4, 5, 6, 7, 8};
        float[] arr2 = {8, 7, 6, 5, 4, 3, 2, 1};

        var sum = lab.addArrays(arr1, arr2);
        System.out.println("Add: " + Arrays.toString(sum));

        float dot = lab.dotProduct(arr1, arr2);
        System.out.println("Dot: " + dot);

        float max = lab.maxElement(arr1);
        System.out.println("Max: " + max);
    }
}
