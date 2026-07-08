package com.math14;

import java.util.*;
import java.util.stream.Collectors;

public class InformationTheory {

    public static double entropy(double[] probabilities) {
        double h = 0;
        for (double p : probabilities) {
            if (p > 0) {
                h -= p * log2(p);
            }
        }
        return h;
    }

    public static double entropy(int[] counts) {
        double total = 0;
        for (int c : counts) total += c;
        double h = 0;
        for (int c : counts) {
            if (c > 0) {
                double p = c / total;
                h -= p * log2(p);
            }
        }
        return h;
    }

    public static double entropy(String text) {
        Map<Character, Long> freq = text.chars()
            .mapToObj(c -> (char) c)
            .collect(Collectors.groupingBy(c -> c, Collectors.counting()));
        double total = text.length();
        double h = 0;
        for (long count : freq.values()) {
            double p = count / total;
            h -= p * log2(p);
        }
        return h;
    }

    public static double jointEntropy(double[][] jointProbs) {
        int rows = jointProbs.length, cols = jointProbs[0].length;
        double h = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double p = jointProbs[i][j];
                if (p > 0) h -= p * log2(p);
            }
        }
        return h;
    }

    public static double conditionalEntropy(double[][] jointProbs) {
        double h = 0;
        int rows = jointProbs.length, cols = jointProbs[0].length;
        double[] marginalsX = new double[rows];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                marginalsX[i] += jointProbs[i][j];
            }
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double p = jointProbs[i][j];
                if (p > 0 && marginalsX[i] > 0) {
                    h -= p * log2(p / marginalsX[i]);
                }
            }
        }
        return h;
    }

    public static double mutualInformation(double[][] jointProbs) {
        double mi = 0;
        int rows = jointProbs.length, cols = jointProbs[0].length;
        double[] marginalsX = new double[rows];
        double[] marginalsY = new double[cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                marginalsX[i] += jointProbs[i][j];
                marginalsY[j] += jointProbs[i][j];
            }
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double p = jointProbs[i][j];
                if (p > 0 && marginalsX[i] > 0 && marginalsY[j] > 0) {
                    mi += p * log2(p / (marginalsX[i] * marginalsY[j]));
                }
            }
        }
        return mi;
    }

    public static double klDivergence(double[] p, double[] q) {
        if (p.length != q.length) throw new IllegalArgumentException("Distributions must have same length");
        double kl = 0;
        for (int i = 0; i < p.length; i++) {
            if (p[i] > 0) {
                if (q[i] == 0) throw new IllegalArgumentException("KL divergence undefined: q[i] = 0 with p[i] > 0");
                kl += p[i] * log2(p[i] / q[i]);
            }
        }
        return kl;
    }

    public static double crossEntropy(double[] p, double[] q) {
        if (p.length != q.length) throw new IllegalArgumentException("Distributions must have same length");
        double ce = 0;
        for (int i = 0; i < p.length; i++) {
            if (p[i] > 0) {
                ce -= p[i] * log2(q[i]);
            }
        }
        return ce;
    }

    public static double channelCapacity(double[][] channelMatrix, int iterations) {
        int inputSize = channelMatrix.length;
        int outputSize = channelMatrix[0].length;
        double[] inputDist = new double[inputSize];
        Arrays.fill(inputDist, 1.0 / inputSize);

        for (int iter = 0; iter < iterations; iter++) {
            double[] outputDist = new double[outputSize];
            for (int j = 0; j < outputSize; j++) {
                for (int i = 0; i < inputSize; i++) {
                    outputDist[j] += inputDist[i] * channelMatrix[i][j];
                }
            }
            double[] phi = new double[inputSize];
            for (int i = 0; i < inputSize; i++) {
                for (int j = 0; j < outputSize; j++) {
                    if (channelMatrix[i][j] > 0 && outputDist[j] > 0) {
                        phi[i] += channelMatrix[i][j] * log2(channelMatrix[i][j] / outputDist[j]);
                    }
                }
            }
            double sum = 0;
            for (int i = 0; i < inputSize; i++) {
                inputDist[i] = Math.exp(phi[i]);
                sum += inputDist[i];
            }
            for (int i = 0; i < inputSize; i++) inputDist[i] /= sum;
        }

        double capacity = 0;
        double[] outputDist = new double[outputSize];
        for (int j = 0; j < outputSize; j++) {
            for (int i = 0; i < inputSize; i++) {
                outputDist[j] += inputDist[i] * channelMatrix[i][j];
            }
        }
        for (int i = 0; i < inputSize; i++) {
            for (int j = 0; j < outputSize; j++) {
                if (channelMatrix[i][j] > 0 && outputDist[j] > 0) {
                    capacity += inputDist[i] * channelMatrix[i][j] * log2(channelMatrix[i][j] / outputDist[j]);
                }
            }
        }
        return capacity;
    }

    public static List<String> shannonFanoEncoding(Map<String, Double> symbols) {
        List<Map.Entry<String, Double>> sorted = symbols.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .collect(Collectors.toList());
        List<String> codes = new ArrayList<>();
        Map<String, String> codeMap = new HashMap<>();
        shannonFanoRecursive(sorted, "", codeMap);
        for (String sym : symbols.keySet()) {
            codes.add(sym + ":" + codeMap.get(sym));
        }
        return codes;
    }

    private static void shannonFanoRecursive(List<Map.Entry<String, Double>> symbols, String prefix,
                                              Map<String, String> codeMap) {
        if (symbols.size() == 1) {
            codeMap.put(symbols.get(0).getKey(), prefix);
            return;
        }
        double total = symbols.stream().mapToDouble(Map.Entry::getValue).sum();
        double acc = 0;
        int split = 0;
        for (int i = 0; i < symbols.size(); i++) {
            if (acc + symbols.get(i).getValue() > total / 2) {
                split = i;
                break;
            }
            acc += symbols.get(i).getValue();
        }
        if (split == 0) split = 1;
        shannonFanoRecursive(symbols.subList(0, split), prefix + "0", codeMap);
        shannonFanoRecursive(symbols.subList(split, symbols.size()), prefix + "1", codeMap);
    }

    public static Map<String, String> huffmanEncoding(Map<String, Double> symbols) {
        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>(
            Comparator.comparingDouble(n -> n.probability));
        for (var entry : symbols.entrySet()) {
            pq.add(new HuffmanNode(entry.getValue(), entry.getKey(), null, null));
        }
        while (pq.size() > 1) {
            HuffmanNode left = pq.poll();
            HuffmanNode right = pq.poll();
            pq.add(new HuffmanNode(left.probability + right.probability(), null, left, right));
        }
        HuffmanNode root = pq.poll();
        Map<String, String> codes = new HashMap<>();
        buildHuffmanCodes(root, "", codes);
        return codes;
    }

    private static void buildHuffmanCodes(HuffmanNode node, String code, Map<String, String> codes) {
        if (node.symbol != null) {
            codes.put(node.symbol, code);
            return;
        }
        if (node.left != null) buildHuffmanCodes(node.left, code + "0", codes);
        if (node.right != null) buildHuffmanCodes(node.right, code + "1", codes);
    }

    private record HuffmanNode(double probability, String symbol, HuffmanNode left, HuffmanNode right)
        implements Comparable<HuffmanNode> {
        public int compareTo(HuffmanNode o) {
            return Double.compare(this.probability, o.probability);
        }
    }

    public static double averageCodeLength(Map<String, Double> symbols, Map<String, String> codes) {
        double avg = 0;
        for (var entry : symbols.entrySet()) {
            avg += entry.getValue() * codes.get(entry.getKey()).length();
        }
        return avg;
    }

    public static double log2(double x) {
        return Math.log(x) / Math.log(2);
    }
}
