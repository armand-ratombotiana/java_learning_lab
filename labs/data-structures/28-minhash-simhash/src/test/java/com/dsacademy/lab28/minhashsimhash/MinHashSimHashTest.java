package com.dsacademy.lab28.minhashsimhash;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.*;

public class MinHashSimHashTest {

    @Test
    void testMinHashSimilarSets() {
        MinHash mh = new MinHash(100, 1000);
        Set<Integer> setA = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
        Set<Integer> setB = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
        mh.addDocument("A", setA);
        mh.addDocument("B", setB);
        double sim = mh.estimateJaccard("A", "B");
        assertTrue(sim > 0.8, "Similarity too low: " + sim);
    }

    @Test
    void testMinHashDisjointSets() {
        MinHash mh = new MinHash(100, 1000);
        Set<Integer> setA = new HashSet<>(Arrays.asList(1, 2, 3));
        Set<Integer> setB = new HashSet<>(Arrays.asList(4, 5, 6));
        mh.addDocument("A", setA);
        mh.addDocument("B", setB);
        double sim = mh.estimateJaccard("A", "B");
        assertTrue(sim < 0.1, "Similarity too high: " + sim);
    }

    @Test
    void testMinHashPartialOverlap() {
        MinHash mh = new MinHash(200, 1000);
        Set<Integer> setA = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6));
        Set<Integer> setB = new HashSet<>(Arrays.asList(4, 5, 6, 7, 8, 9));
        mh.addDocument("A", setA);
        mh.addDocument("B", setB);
        double sim = mh.estimateJaccard("A", "B");
        double expected = 3.0 / 9.0;
        assertTrue(Math.abs(sim - expected) < 0.2, "Similarity off: " + sim + " expected ~" + expected);
    }

    @Test
    void testSimHashIdenticalTexts() {
        SimHash sh = new SimHash(64);
        long fp1 = sh.computeFingerprint("the quick brown fox");
        long fp2 = sh.computeFingerprint("the quick brown fox");
        assertEquals(fp1, fp2);
        assertEquals(1.0, sh.cosineSimilarity(fp1, fp2));
    }

    @Test
    void testSimHashDifferentTexts() {
        SimHash sh = new SimHash(64);
        long fp1 = sh.computeFingerprint("hello world");
        long fp2 = sh.computeFingerprint("goodbye universe");
        double sim = sh.cosineSimilarity(fp1, fp2);
        assertTrue(sim < 0.8);
    }

    @Test
    void testSimHashNearDuplicates() {
        SimHash sh = new SimHash(64);
        long fp1 = sh.computeFingerprint("the quick brown fox jumps over the lazy dog");
        long fp2 = sh.computeFingerprint("the quick brown fox jumps over the lazy cat");
        double sim = sh.cosineSimilarity(fp1, fp2);
        assertTrue(sim > 0.5, "Similarity too low for near duplicates: " + sim);
    }

    @Test
    void testLshIndex() {
        LshIndex lsh = new LshIndex(8, 8);
        SimHash sh = new SimHash(64);
        long fp1 = sh.computeFingerprint("document one content here");
        long fp2 = sh.computeFingerprint("document one content here");
        lsh.insert("doc1", fp1, 64);
        lsh.insert("doc2", fp2, 64);
        Set<String> results = lsh.query(fp1, 64);
        assertTrue(results.contains("doc1"));
    }

    @Test
    void testEmptyString() {
        SimHash sh = new SimHash(64);
        assertEquals(0, sh.computeFingerprint(""));
    }
}
