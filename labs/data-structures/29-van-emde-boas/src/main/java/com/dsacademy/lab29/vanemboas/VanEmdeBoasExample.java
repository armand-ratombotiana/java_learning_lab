package com.dsacademy.lab29.vanemboas;

import java.util.TreeSet;

public class VanEmdeBoasExample {

    public static void main(String[] args) {
        int universe = 1 << 16;
        VanEmdeBoasTree veb = new VanEmdeBoasTree(universe);
        TreeSet<Integer> ts = new TreeSet<>();

        int[] values = {100, 50, 200, 75, 150, 25, 300, 1, 65535};
        for (int v : values) {
            veb.insert(v);
            ts.add(v);
        }

        System.out.println("vEB min: " + veb.min() + ", TreeSet first: " + ts.first());
        System.out.println("vEB max: " + veb.max() + ", TreeSet last: " + ts.last());
        System.out.println("Contains 75: " + veb.contains(75));
        System.out.println("Contains 999: " + veb.contains(999));

        int pred = veb.predecessor(80);
        System.out.println("Predecessor of 80: " + pred + " (TreeSet lower: " + ts.lower(80) + ")");

        int succ = veb.successor(80);
        System.out.println("Successor of 80: " + succ + " (TreeSet higher: " + ts.higher(80) + ")");

        long start = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            veb.contains(i);
        }
        long vebTime = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            ts.contains(i);
        }
        long tsTime = System.nanoTime() - start;

        System.out.println("\nvEB contains (10K): " + vebTime / 1_000_000 + "ms");
        System.out.println("TreeSet contains (10K): " + tsTime / 1_000_000 + "ms");
    }
}
