package com.ai17;

import java.util.*;

public class ContextConstruction {
    private int maxTokens;
    private String separator;

    public ContextConstruction(int maxTokens, String separator) {
        this.maxTokens = maxTokens;
        this.separator = separator;
    }

    public String buildContext(List<String> retrievedDocs, String query) {
        StringBuilder context = new StringBuilder();
        int tokenCount = 0;
        for (String doc : retrievedDocs) {
            String[] tokens = (doc + separator).split("\\s+");
            if (tokenCount + tokens.length > maxTokens) {
                int remaining = maxTokens - tokenCount;
                for (int i = 0; i < Math.min(remaining, tokens.length); i++) {
                    if (context.length() > 0) context.append(" ");
                    context.append(tokens[i]);
                }
                break;
            }
            if (context.length() > 0) context.append(" ");
            context.append(doc);
            tokenCount += tokens.length;
        }
        return context.toString();
    }

    public String buildPromptWithContext(String context, String query, String instruction) {
        return instruction + "\n\nContext:\n" + context + "\n\nQuery: " + query + "\n\nAnswer:";
    }

    public List<String> slidingWindowChunks(String text, int windowSize, int stride) {
        List<String> chunks = new ArrayList<>();
        String[] words = text.split("\\s+");
        for (int start = 0; start < words.length; start += stride) {
            int end = Math.min(start + windowSize, words.length);
            StringBuilder chunk = new StringBuilder();
            for (int i = start; i < end; i++) {
                if (i > start) chunk.append(" ");
                chunk.append(words[i]);
            }
            chunks.add(chunk.toString());
            if (end == words.length) break;
        }
        return chunks;
    }

    public static void main(String[] args) {
        System.out.println("=== Context Construction Demo ===");
        ContextConstruction cc = new ContextConstruction(20, "\n---\n");
        List<String> docs = List.of(
            "Machine learning is a field of AI that uses data to train models.",
            "Neural networks are composed of layers of interconnected neurons.",
            "Attention mechanisms allow models to focus on relevant parts of input."
        );
        String context = cc.buildContext(docs, "neural networks");
        System.out.println("Built context (" + context.split("\\s+").length + " tokens):");
        System.out.println(context);
        String prompt = cc.buildPromptWithContext(context, "What are neural networks?", "Answer based on the context provided.");
        System.out.println("\nFull prompt:\n" + prompt);
        String sample = "This is a long document that we want to process using sliding window chunks for better context handling";
        System.out.println("\nSliding window chunks:");
        for (String c : cc.slidingWindowChunks(sample, 5, 3))
            System.out.println("  [" + c + "]");
    }
}
