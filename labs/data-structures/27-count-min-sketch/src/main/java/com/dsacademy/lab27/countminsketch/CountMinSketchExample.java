package com.dsacademy.lab27.countminsketch;

import java.util.Random;

public class CountMinSketchExample {

    public static void main(String[] args) {
        CountMinSketch cms = new CountMinSketch(5, 1000);
        Random rand = new Random(42);

        int[] frequencies = new int[100];
        for (int i = 0; i < 100000; i++) {
            int item = rand.nextInt(100);
            frequencies[item]++;
            cms.add(item);
        }

        System.out.println("=== Frequency Estimation ===");
        int totalError = 0;
        for (int i = 0; i < 10; i++) {
            long actual = frequencies[i];
            long estimated = cms.estimateCount(i);
            totalError += Math.abs(estimated - actual);
            System.out.println("Item " + i + ": actual=" + actual + ", estimated=" + estimated);
        }

        System.out.println("\nTotal count: " + cms.getTotalCount());
        System.out.println("Total estimation error (first 10): " + totalError);
    }
}
