package collectionsdeep;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;

/**
 * Bloom Filter — probabilistic data structure for set membership.
 * 
 * Can say "definitely not in set" or "probably in set" (false positives, no false negatives).
 * 
 * Uses: cache filtering, spell checkers, database query optimization.
 * 
 * Time: O(k) per operation where k = number of hash functions
 * Space: O(m) where m = bit array size
 */
public class BloomFilter<T> {
    private final BitSet bits;
    private final int m; // bits
    private final int k; // hash functions

    public BloomFilter(int expectedInsertions, double falsePositiveRate) {
        this.m = optimalBits(expectedInsertions, falsePositiveRate);
        this.k = optimalHashFunctions(expectedInsertions, m);
        this.bits = new BitSet(m);
    }

    private static int optimalBits(int n, double p) {
        return (int) Math.ceil(-n * Math.log(p) / (Math.log(2) * Math.log(2)));
    }

    private static int optimalHashFunctions(int n, int m) {
        return Math.max(1, (int) Math.round((double) m / n * Math.log(2)));
    }

    private int[] hash(T element) {
        byte[] data = element.toString().getBytes(StandardCharsets.UTF_8);
        int[] hashes = new int[k];
        for (int i = 0; i < k; i++) {
            // Simulate multiple hash functions by salting
            ByteBuffer buf = ByteBuffer.allocate(data.length + 4);
            buf.put(data);
            buf.putInt(i);
            hashes[i] = Math.abs(hashBytes(buf.array()) % m);
        }
        return hashes;
    }

    private int hashBytes(byte[] bytes) {
        int h = 0;
        for (byte b : bytes) h = 31 * h + (b & 0xFF);
        return h;
    }

    public void add(T element) {
        for (int h : hash(element)) bits.set(h);
    }

    public boolean mightContain(T element) {
        for (int h : hash(element)) {
            if (!bits.get(h)) return false;
        }
        return true;
    }

    public static void main(String[] args) {
        BloomFilter<String> bf = new BloomFilter<>(1000, 0.01);
        for (int i = 0; i < 500; i++) bf.add("item" + i);

        // All added items should be found
        for (int i = 0; i < 500; i++) {
            assert bf.mightContain("item" + i) : "Should contain item" + i;
        }

        // Items not added — expect few false positives
        int falsePositives = 0;
        for (int i = 500; i < 1500; i++) {
            if (bf.mightContain("item" + i)) falsePositives++;
        }
        System.out.println("False positives: " + falsePositives + "/1000 (expected ~1%)");
        assert falsePositives < 100 : "Too many false positives: " + falsePositives;

        System.out.println("All BloomFilter tests passed.");
    }
}