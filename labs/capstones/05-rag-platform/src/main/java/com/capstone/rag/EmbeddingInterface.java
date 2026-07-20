package com.capstone.rag;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public interface EmbeddingInterface {
    float[] embed(String text);
    List<float[]> embedBatch(List<String> texts);
    int getDimension();

    class MockEmbedding implements EmbeddingInterface {
        private final int dimension;
        private final AtomicLong counter = new AtomicLong(0);
        private final Map<String, float[]> cache = new ConcurrentHashMap<>();

        public MockEmbedding() { this(384); }
        public MockEmbedding(int dimension) { this.dimension = dimension; }

        @Override
        public float[] embed(String text) {
            return cache.computeIfAbsent(text, k -> generateEmbedding(k));
        }

        @Override
        public List<float[]> embedBatch(List<String> texts) {
            return texts.stream().map(this::embed).toList();
        }

        @Override
        public int getDimension() { return dimension; }

        private float[] generateEmbedding(String text) {
            float[] vec = new float[dimension];
            long seed = text.hashCode();
            Random rng = new Random(seed);
            for (int i = 0; i < dimension; i++) vec[i] = (float) (rng.nextGaussian() * 0.1);
            float norm = 0;
            for (float v : vec) norm += v * v;
            norm = (float) Math.sqrt(norm);
            for (int i = 0; i < dimension; i++) vec[i] /= norm;
            return vec;
        }
    }

    class OpenAIEmbedding implements EmbeddingInterface {
        private final int dimension;
        public OpenAIEmbedding() { this(1536); }
        public OpenAIEmbedding(int dimension) { this.dimension = dimension; }

        @Override
        public float[] embed(String text) {
            throw new UnsupportedOperationException("API key not configured");
        }

        @Override
        public List<float[]> embedBatch(List<String> texts) {
            throw new UnsupportedOperationException("API key not configured");
        }

        @Override
        public int getDimension() { return dimension; }
    }
}
