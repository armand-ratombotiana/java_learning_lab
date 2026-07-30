package com.math.deep.lab08;

import java.util.*;

public class InformationTheory {

    public static double entropy(double[] probs) {
        double h = 0;
        for (double p : probs) {
            if (p > 0) h -= p * log2(p);
        }
        return h;
    }

    public static double jointEntropy(double[][] jointProbs) {
        double h = 0;
        for (double[] row : jointProbs)
            for (double p : row)
                if (p > 0) h -= p * log2(p);
        return h;
    }

    public static double mutualInformation(double[][] jointProbs) {
        int n = jointProbs.length;
        int m = jointProbs[0].length;
        double[] px = new double[n];
        double[] py = new double[m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                px[i] += jointProbs[i][j];
                py[j] += jointProbs[i][j];
            }
        }
        double mi = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                double p = jointProbs[i][j];
                if (p > 0) mi += p * log2(p / (px[i] * py[j]));
            }
        }
        return mi;
    }

    public static double klDivergence(double[] p, double[] q) {
        double kl = 0;
        for (int i = 0; i < p.length; i++) {
            if (p[i] > 0) kl += p[i] * log2(p[i] / q[i]);
        }
        return kl;
    }

    public static double channelCapacityBSC(double crossoverProb) {
        if (crossoverProb < 0 || crossoverProb > 1)
            throw new IllegalArgumentException("Crossover probability must be in [0,1]");
        return 1.0 - entropy(new double[]{crossoverProb, 1 - crossoverProb});
    }

    public static double channelCapacityAWGN(double snr) {
        return 0.5 * log2(1 + snr);
    }

    public static Map<Character, String> huffmanCoding(Map<Character, Integer> frequencies) {
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(n -> n.freq));
        for (var entry : frequencies.entrySet()) {
            pq.add(new Node(entry.getKey(), entry.getValue()));
        }
        while (pq.size() > 1) {
            Node left = pq.poll();
            Node right = pq.poll();
            Node parent = new Node('\0', left.freq + right.freq);
            parent.left = left;
            parent.right = right;
            pq.add(parent);
        }
        Map<Character, String> codes = new HashMap<>();
        buildCodes(pq.peek(), "", codes);
        return codes;
    }

    private static void buildCodes(Node node, String code, Map<Character, String> codes) {
        if (node == null) return;
        if (node.ch != '\0') { codes.put(node.ch, code); return; }
        buildCodes(node.left, code + "0", codes);
        buildCodes(node.right, code + "1", codes);
    }

    private static class Node {
        char ch;
        int freq;
        Node left, right;
        Node(char ch, int freq) { this.ch = ch; this.freq = freq; }
    }

    public static double sourceCodingLimit(double[] probs) {
        return entropy(probs);
    }

    private static double log2(double x) {
        return Math.log(x) / Math.log(2);
    }
}
