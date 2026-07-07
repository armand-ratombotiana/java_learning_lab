package com.dsacademy.lab25.spacefilling;

import java.util.*;

public class SpaceFillingExample {

    public static void main(String[] args) {
        List<int[]> points = new ArrayList<>();
        Random rand = new Random(42);
        for (int i = 0; i < 20; i++) {
            points.add(new int[]{rand.nextInt(256), rand.nextInt(256)});
        }

        List<Long> zOrderValues = new ArrayList<>();
        List<Long> hilbertValues = new ArrayList<>();
        for (int[] p : points) {
            zOrderValues.add(ZOrderCurve.encode2D(p[0], p[1]));
            hilbertValues.add(HilbertCurve.encode2D(p[0], p[1], 8));
        }

        Collections.sort(zOrderValues);
        Collections.sort(hilbertValues);

        System.out.println("=== Z-Order Curve Sort ===");
        for (long z : zOrderValues) {
            int[] decoded = ZOrderCurve.decode2D(z);
            System.out.println("  (" + decoded[0] + ", " + decoded[1] + ") -> " + z);
        }

        System.out.println("\n=== Hilbert Curve Sort ===");
        for (long h : hilbertValues) {
            int[] decoded = HilbertCurve.decode2D(h, 8);
            System.out.println("  (" + decoded[0] + ", " + decoded[1] + ") -> " + h);
        }

        System.out.println("\n=== Locality Preservation ===");
        int[] p1 = {10, 20};
        int[] p2 = {10, 21};
        int[] p3 = {200, 200};
        long z1 = ZOrderCurve.encode2D(p1[0], p1[1]);
        long z2 = ZOrderCurve.encode2D(p2[0], p2[1]);
        long z3 = ZOrderCurve.encode2D(p3[0], p3[1]);
        System.out.println("Z-order distance (close points): " + Math.abs(z1 - z2));
        System.out.println("Z-order distance (far points): " + Math.abs(z1 - z3));
    }
}
