package com.dsacademy.lab29.vanemboas;

public class VebNode {

    final int universeSize;
    int min;
    int max;
    VebNode summary;
    VebNode[] cluster;

    public VebNode(int universeSize) {
        this.universeSize = universeSize;
        this.min = -1;
        this.max = -1;
        if (universeSize > 2) {
            int subSize = higherSquareRoot(universeSize);
            summary = new VebNode(subSize);
            cluster = new VebNode[subSize];
        }
    }

    static int higherSquareRoot(int n) {
        double sqrt = Math.sqrt(n);
        int ceil = (int) Math.ceil(sqrt);
        return Math.max(ceil, 1);
    }

    static int lowerSquareRoot(int n) {
        return (int) Math.floor(Math.sqrt(n));
    }

    static int high(int x, int subSize) {
        return x / subSize;
    }

    static int low(int x, int subSize) {
        return x % subSize;
    }

    static int index(int clusterIdx, int offset, int subSize) {
        return clusterIdx * subSize + offset;
    }
}
