package com.ai16;

import java.util.*;

public class Word2Vec {
    private Map<String, double[]> embeddings;
    private Map<String, Integer> wordToIdx;
    private List<String> idxToWord;
    private int vectorSize;
    private double learningRate;

    public Word2Vec(int vectorSize, double learningRate) {
        this.vectorSize = vectorSize;
        this.learningRate = learningRate;
        this.embeddings = new HashMap<>();
        this.wordToIdx = new HashMap<>();
        this.idxToWord = new ArrayList<>();
    }

    public void trainSkipGram(List<String> corpus, int windowSize, int epochs) {
        List<String> tokens = new ArrayList<>();
        for (String sentence : corpus) {
            for (String word : sentence.toLowerCase().split("\\s+"))
                if (!word.isEmpty()) tokens.add(word);
        }
        Set<String> vocab = new LinkedHashSet<>(tokens);
        idxToWord.addAll(vocab);
        for (int i = 0; i < idxToWord.size(); i++)
            wordToIdx.put(idxToWord.get(i), i);
        int vocabSize = vocab.size();
        double[][] inputVectors = new double[vocabSize][vectorSize];
        double[][] outputVectors = new double[vocabSize][vectorSize];
        Random rng = new Random(42);
        for (int i = 0; i < vocabSize; i++)
            for (int j = 0; j < vectorSize; j++)
                inputVectors[i][j] = (rng.nextDouble() - 0.5) / vectorSize;

        for (int epoch = 0; epoch < epochs; epoch++) {
            double loss = 0;
            int pairs = 0;
            for (int t = 0; t < tokens.size(); t++) {
                int centerIdx = wordToIdx.get(tokens.get(t));
                int start = Math.max(0, t - windowSize);
                int end = Math.min(tokens.size(), t + windowSize + 1);
                for (int c = start; c < end; c++) {
                    if (c == t) continue;
                    int contextIdx = wordToIdx.get(tokens.get(c));
                    double[] hidden = inputVectors[centerIdx];
                    double score = 0;
                    for (int j = 0; j < vectorSize; j++)
                        score += hidden[j] * outputVectors[contextIdx][j];
                    double prob = 1 / (1 + Math.exp(-score));
                    double error = prob - 1;
                    loss += error * error;
                    pairs++;
                    for (int j = 0; j < vectorSize; j++) {
                        double grad = error * outputVectors[contextIdx][j];
                        inputVectors[centerIdx][j] -= learningRate * grad;
                        outputVectors[contextIdx][j] -= learningRate * error * hidden[j];
                    }
                }
            }
            if (epoch % 5 == 0)
                System.out.println("Epoch " + epoch + " avg loss: " + (loss / pairs));
        }
        for (int i = 0; i < vocabSize; i++)
            embeddings.put(idxToWord.get(i), inputVectors[i]);
    }

    public double[] getEmbedding(String word) {
        return embeddings.get(word);
    }

    public Map<String, double[]> getEmbeddings() { return embeddings; }

    public static void main(String[] args) {
        System.out.println("=== Word2Vec (Skip-Gram) Demo ===");
        List<String> corpus = List.of(
            "the cat sat on the mat",
            "the dog sat on the log",
            "cats and dogs are pets"
        );
        Word2Vec w2v = new Word2Vec(5, 0.01);
        w2v.trainSkipGram(corpus, 2, 20);
        for (String word : List.of("the", "cat", "dog", "sat"))
            System.out.println(word + ": " + Arrays.toString(w2v.getEmbedding(word)));
    }
}
