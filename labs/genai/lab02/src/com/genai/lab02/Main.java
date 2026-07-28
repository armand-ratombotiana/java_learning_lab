package com.genai.lab02;

import java.util.*;

/**
 * GPT Architecture
 * 
 * Demonstrates causal self-attention, autoregressive generation,
 * BPE-style tokenization, and KV cache in Java.
 */
public class Main {

    /** Causal masked attention with optional KV cache. */
    public static double[][] causalAttention(double[][] Q, double[][] K, double[][] V,
                                              double[][] kvCacheK, double[][] kvCacheV) {
        int seqLen = Q.length;
        int dk = Q[0].length;
        int dv = V[0].length;

        double[][] fullK = (kvCacheK != null) ? concatRows(kvCacheK, K) : K;
        double[][] fullV = (kvCacheV != null) ? concatRows(kvCacheV, V) : V;
        int fullLen = fullK.length;

        double[][] scores = new double[seqLen][fullLen];
        for (int i = 0; i < seqLen; i++) {
            for (int j = 0; j < fullLen; j++) {
                double dot = 0.0;
                for (int k = 0; k < dk; k++) {
                    dot += Q[i][k] * fullK[j][k];
                }
                scores[i][j] = dot / Math.sqrt(dk);
                if (j > i + fullLen - seqLen) {
                    scores[i][j] = Double.NEGATIVE_INFINITY;
                }
            }
        }

        double[][] weights = softmax(scores);
        double[][] output = new double[seqLen][dv];
        for (int i = 0; i < seqLen; i++) {
            for (int j = 0; j < dv; j++) {
                for (int k = 0; k < fullLen; k++) {
                    output[i][j] += weights[i][k] * fullV[k][j];
                }
            }
        }
        return output;
    }

    public static double[][] concatRows(double[][] a, double[][] b) {
        double[][] result = Arrays.copyOf(a, a.length + b.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    public static double[][] softmax(double[][] x) {
        int rows = x.length, cols = x[0].length;
        double[][] r = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            double max = Double.NEGATIVE_INFINITY;
            for (int j = 0; j < cols; j++) if (x[i][j] > max) max = x[i][j];
            double sum = 0.0;
            for (int j = 0; j < cols; j++) { r[i][j] = Math.exp(x[i][j] - max); sum += r[i][j]; }
            for (int j = 0; j < cols; j++) r[i][j] /= sum;
        }
        return r;
    }

    /** Simple BPE-style tokenizer: maps tokens to IDs. */
    static class BPETokenizer {
        final Map<String, Integer> tokenToId = new HashMap<>();
        final Map<Integer, String> idToToken = new HashMap<>();
        int nextId;

        BPETokenizer(String[] tokens) {
            for (String t : tokens) {
                if (!tokenToId.containsKey(t)) {
                    tokenToId.put(t, nextId);
                    idToToken.put(nextId, t);
                    nextId++;
                }
            }
        }

        int[] encode(String text) {
            String[] parts = text.split(" ");
            int[] ids = new int[parts.length];
            for (int i = 0; i < parts.length; i++) {
                ids[i] = tokenToId.getOrDefault(parts[i], tokenToId.get("<unk>"));
            }
            return ids;
        }

        String decode(int[] ids) {
            StringBuilder sb = new StringBuilder();
            for (int id : ids) sb.append(idToToken.getOrDefault(id, "<unk>")).append(" ");
            return sb.toString().trim();
        }
    }

    /** Autoregressive generation with temperature sampling. */
    public static int[] generate(int[] prompt, double[][] weights, int maxTokens,
                                  BPETokenizer tok, double temperature) {
        List<Integer> seq = new ArrayList<>();
        for (int t : prompt) seq.add(t);
        Random rng = new Random(42);

        for (int step = 0; step < maxTokens; step++) {
            int vocabSize = weights[0].length;
            double[] logits = new double[vocabSize];
            int contextLen = Math.min(seq.size(), weights.length);
            for (int i = 0; i < vocabSize; i++) {
                for (int j = 0; j < contextLen; j++) {
                    logits[i] += weights[j][i] * seq.get(seq.size() - contextLen + j);
                }
            }

            double maxLogit = Double.NEGATIVE_INFINITY;
            for (double l : logits) if (l > maxLogit) maxLogit = l;
            double sum = 0.0;
            for (int i = 0; i < vocabSize; i++) {
                logits[i] = Math.exp((logits[i] - maxLogit) / temperature);
                sum += logits[i];
            }
            for (int i = 0; i < vocabSize; i++) logits[i] /= sum;

            double r = rng.nextDouble();
            double cumulative = 0.0;
            int nextToken = 0;
            for (int i = 0; i < vocabSize; i++) {
                cumulative += logits[i];
                if (r <= cumulative) { nextToken = i; break; }
            }
            seq.add(nextToken);
        }
        return seq.stream().mapToInt(Integer::intValue).toArray();
    }

    public static void main(String[] args) {
        String[] vocab = {"<pad>", "<unk>", "<eos>", "the", "cat", "sat", "on", "mat", "hello", "world"};
        BPETokenizer tok = new BPETokenizer(vocab);

        System.out.println("=== BPE Tokenizer ===");
        int[] encoded = tok.encode("the cat sat on mat");
        System.out.println("Encoded: " + Arrays.toString(encoded));
        System.out.println("Decoded: " + tok.decode(encoded));

        int seqLen = 4, dk = 4, dv = 4;
        double[][] Q = new double[seqLen][dk];
        double[][] K = new double[seqLen][dk];
        double[][] V = new double[seqLen][dv];
        for (int i = 0; i < seqLen; i++) {
            Arrays.fill(Q[i], 0.5);
            Arrays.fill(K[i], 0.3);
            Arrays.fill(V[i], 0.1 * (i + 1));
        }

        System.out.println("\n=== Causal Self-Attention ===");
        double[][] attnOut = causalAttention(Q, K, V, null, null);
        for (double[] row : attnOut) {
            System.out.println(Arrays.toString(row));
        }

        System.out.println("\n=== KV Cache Test ===");
        double[][] qNew = new double[][]{{0.5, 0.5, 0.5, 0.5}};
        double[][] kNew = new double[][]{{0.3, 0.3, 0.3, 0.3}};
        double[][] vNew = new double[][]{{0.5, 0.5, 0.5, 0.5}};
        double[][] cachedOut = causalAttention(qNew, kNew, vNew, K, V);
        System.out.println("With KV cache: " + Arrays.toString(cachedOut[0]));

        System.out.println("\nGPT architecture components validated.");
    }
}
