package com.math14;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

class InformationTheoryTest {

    @Test
    void testEntropy() {
        double[] probs = {0.5, 0.5};
        assertEquals(1.0, InformationTheory.entropy(probs), 1e-10);
    }

    @Test
    void testEntropyDeterministic() {
        double[] probs = {1.0, 0.0};
        assertEquals(0.0, InformationTheory.entropy(probs), 1e-10);
    }

    @Test
    void testEntropyUniform() {
        double[] probs = {0.25, 0.25, 0.25, 0.25};
        assertEquals(2.0, InformationTheory.entropy(probs), 1e-10);
    }

    @Test
    void testEntropyFromCounts() {
        int[] counts = {5, 5};
        assertEquals(1.0, InformationTheory.entropy(counts), 1e-10);
    }

    @Test
    void testEntropyFromText() {
        // "ab" has two equally likely chars
        double h = InformationTheory.entropy("ab");
        assertEquals(1.0, h, 1e-10);
    }

    @Test
    void testJointEntropy() {
        double[][] joint = {{0.25, 0.25}, {0.25, 0.25}};
        assertEquals(2.0, InformationTheory.jointEntropy(joint), 1e-10);
    }

    @Test
    void testConditionalEntropy() {
        double[][] joint = {{0.5, 0}, {0, 0.5}};
        assertEquals(0.0, InformationTheory.conditionalEntropy(joint), 1e-10);
    }

    @Test
    void testMutualInformation() {
        double[][] joint = {{0.5, 0}, {0, 0.5}};
        assertEquals(1.0, InformationTheory.mutualInformation(joint), 1e-10);
    }

    @Test
    void testMutualInformationIndependent() {
        double[][] joint = {{0.25, 0.25}, {0.25, 0.25}};
        assertEquals(0.0, InformationTheory.mutualInformation(joint), 1e-10);
    }

    @Test
    void testKLDivergence() {
        double[] p = {0.5, 0.5};
        double[] q = {0.5, 0.5};
        assertEquals(0.0, InformationTheory.klDivergence(p, q), 1e-10);
    }

    @Test
    void testKLDivergenceNonZero() {
        double[] p = {0.9, 0.1};
        double[] q = {0.5, 0.5};
        double kl = InformationTheory.klDivergence(p, q);
        assertTrue(kl > 0);
    }

    @Test
    void testKLDivergenceUndefined() {
        double[] p = {0.5, 0.5};
        double[] q = {1.0, 0.0};
        assertThrows(IllegalArgumentException.class, () -> InformationTheory.klDivergence(p, q));
    }

    @Test
    void testCrossEntropy() {
        double[] p = {0.5, 0.5};
        double[] q = {0.5, 0.5};
        assertEquals(1.0, InformationTheory.crossEntropy(p, q), 1e-10);
    }

    @Test
    void testChannelCapacity() {
        // Binary symmetric channel with no noise = capacity 1
        double[][] channel = {{1, 0}, {0, 1}};
        double capacity = InformationTheory.channelCapacity(channel, 100);
        assertEquals(1.0, capacity, 0.01);
    }

    @Test
    void testHuffmanEncoding() {
        Map<String, Double> symbols = Map.of("A", 0.5, "B", 0.25, "C", 0.25);
        Map<String, String> codes = InformationTheory.huffmanEncoding(symbols);
        assertEquals(3, codes.size());
        // Most probable symbol should have shortest code
        assertTrue(codes.get("A").length() <= codes.get("B").length());
        assertTrue(codes.get("A").length() <= codes.get("C").length());
    }

    @Test
    void testAverageCodeLength() {
        Map<String, Double> symbols = Map.of("A", 0.5, "B", 0.25, "C", 0.25);
        Map<String, String> codes = InformationTheory.huffmanEncoding(symbols);
        double avg = InformationTheory.averageCodeLength(symbols, codes);
        double entropy = InformationTheory.entropy(new double[]{0.5, 0.25, 0.25});
        assertTrue(avg >= entropy);
    }

    @Test
    void testShannonFanoEncoding() {
        Map<String, Double> symbols = Map.of("A", 0.5, "B", 0.25, "C", 0.25);
        var codes = InformationTheory.shannonFanoEncoding(symbols);
        assertEquals(3, codes.size());
    }

    @Test
    void testEntropyUneven() {
        double[] probs = {0.1, 0.2, 0.3, 0.4};
        double h = InformationTheory.entropy(probs);
        assertTrue(h > 0);
        assertTrue(h < 2.0); // less than uniform 4-class
    }
}
