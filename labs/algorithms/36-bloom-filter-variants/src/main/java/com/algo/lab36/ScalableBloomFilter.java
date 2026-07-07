package com.algo.lab36;

import java.util.ArrayList;
import java.util.List;

/**
 * Scalable Bloom Filter that automatically grows by adding new filter layers
 * when the false positive rate approaches the threshold. Maintains a tight
 * total false positive bound using a geometric series of tightening ratios.
 */
public class ScalableBloomFilter {
    private final List<InnerFilter> filters;
    private final double baseFp;
    private final double tighteningRatio;
    private int count;

    public ScalableBloomFilter(double baseFalsePositive) {
        this.baseFp = baseFalsePositive;
        this.tighteningRatio = 0.8;
        this.filters = new ArrayList<>();
        this.filters.add(new InnerFilter(1000, baseFp));
        this.count = 0;
    }

    public void add(String item) {
        InnerFilter last = filters.get(filters.size() - 1);
        last.add(item);
        count++;
        if (count > last.maxElements()) {
            double newFp = filters.get(filters.size() - 1).fp * tighteningRatio;
            filters.add(new InnerFilter(last.maxElements() * 2, newFp));
        }
    }

    public boolean contains(String item) {
        for (InnerFilter f : filters) {
            if (f.contains(item)) return true;
        }
        return false;
    }

    public int filterCount() { return filters.size(); }

    private static class InnerFilter {
        final CountingBloomFilter filter;
        final int maxElements;
        final double fp;

        InnerFilter(int maxElements, double fp) {
            this.maxElements = maxElements;
            this.fp = fp;
            this.filter = new CountingBloomFilter(maxElements, fp);
        }

        void add(String item) { filter.add(item); }
        boolean contains(String item) { return filter.contains(item); }
        int maxElements() { return maxElements; }
    }
}
