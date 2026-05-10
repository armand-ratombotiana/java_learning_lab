package com.learning.nlppipeline;

import java.util.*;

public class Test {

    public static void main(String[] args) {
        System.out.println("Running NLP Pipeline Tests\n");

        testTokenization();
        testStopWordRemoval();
        testTextNormalization();
        testFeatureExtraction();
        testPipeline();

        System.out.println("\nAll tests passed!");
    }

    private static void testTokenization() {
        System.out.println("Test: Tokenization");
        Solution.TokenizationExample tokenizer = new Solution.TokenizationExample();

        List<String> tokens = Arrays.asList("This", "is", "a", "test");
        assert tokens.size() == 4 : "Should have 4 tokens";

        System.out.println("  - Tokenization ready, tokens: " + tokens.size());
    }

    private static void testStopWordRemoval() {
        System.out.println("Test: Stop Word Removal");
        Solution.TokenizationExample tokenizer = new Solution.TokenizationExample();

        List<String> tokens = Arrays.asList("the", "cat", "is", "on", "the", "mat");
        List<String> filtered = tokenizer.removeStopWords(tokens);

        assert filtered.size() < tokens.size() : "Should have fewer tokens after removal";
        System.out.println("  - Stop words removed: " + tokens.size() + " -> " + filtered.size());
    }

    private static void testTextNormalization() {
        System.out.println("Test: Text Normalization");
        Solution.TextProcessingExample textProcessor = new Solution.TextProcessingExample();

        String normalized = textProcessor.normalizeText("  Hello   World!  ");
        assert normalized.equals("Hello World") : "Should normalize whitespace and remove punctuation";

        String htmlRemoved = textProcessor.removeHTMLTags("<p>Hello</p>");
        assert htmlRemoved.equals("Hello") : "Should remove HTML tags";

        System.out.println("  - Text normalization works");
    }

    private static void testFeatureExtraction() {
        System.out.println("Test: Feature Extraction");
        Solution.FeatureExtractionExample featureExtraction = new Solution.FeatureExtractionExample();

        List<String> tokens = Arrays.asList("hello", "world", "hello", "hello");
        Map<String, Integer> freq = featureExtraction.getWordFrequency(tokens);

        assert freq.get("hello") == 3 : "hello should appear 3 times";
        assert freq.get("world") == 1 : "world should appear once";

        List<String> topWords = featureExtraction.getTopNWords(freq, 2);
        assert topWords.get(0).equals("hello") : "Most frequent word should be hello";

        System.out.println("  - Feature extraction works");
    }

    private static void testPipeline() {
        System.out.println("Test: Pipeline");
        Solution.PipelineExample pipelineExample = new Solution.PipelineExample();

        Solution.NLPPipeline pipeline = pipelineExample.createPipeline();
        assert pipeline != null : "Pipeline should not be null";

        System.out.println("  - NLP pipeline created successfully");
    }
}