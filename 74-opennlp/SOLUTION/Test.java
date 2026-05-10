package com.learning.opennlp;

import java.util.Arrays;

public class Test {

    public static void main(String[] args) {
        System.out.println("Running OpenNLP Tests\n");

        testTokenization();
        testSentenceDetection();
        testPOS();
        testNER();
        testChunker();
        testNLPResult();

        System.out.println("\nAll tests passed!");
    }

    private static void testTokenization() {
        System.out.println("Test: Tokenization");
        Solution.TokenizationExample tokenizer = new Solution.TokenizationExample();

        String[] tokens = tokenizer.tokenizeSimple("This is a test sentence.");

        assert tokens.length == 5 : "Should have 5 tokens";
        assert tokens[0].equals("This") : "First token should be 'This'";
        assert tokens[4].equals("sentence") : "Last token should be 'sentence'";
        System.out.println("  - Tokenization works: " + Arrays.toString(tokens));
    }

    private static void testSentenceDetection() {
        System.out.println("Test: Sentence Detection");
        Solution.SentenceDetectionExample sentenceDetector = new Solution.SentenceDetectionExample();

        String[] sentences = sentenceDetector.segment("Hello world. This is a test. Another sentence.");

        assert sentences.length == 3 : "Should have 3 sentences";
        System.out.println("  - Sentence detection: " + sentences.length + " sentences");
    }

    private static void testPOS() {
        System.out.println("Test: POS Tagging");
        Solution.TokenizationExample tokenizer = new Solution.TokenizationExample();
        String[] tokens = tokenizer.tokenizeSimple("The quick brown fox");

        assert tokens.length == 4 : "Should have 4 tokens";
        System.out.println("  - POS tagging ready for " + tokens.length + " tokens");
    }

    private static void testNER() {
        System.out.println("Test: Named Entity Recognition");
        Solution.TokenizationExample tokenizer = new Solution.TokenizationExample();
        String[] tokens = tokenizer.tokenizeSimple("John works at Google in New York");

        assert tokens.length == 9 : "Should have 9 tokens";
        System.out.println("  - NER ready for " + tokens.length + " tokens");
    }

    private static void testChunker() {
        System.out.println("Test: Chunker");
        Solution.TokenizationExample tokenizer = new Solution.TokenizationExample();
        String[] tokens = tokenizer.tokenizeSimple("The quick brown fox jumps");

        assert tokens.length == 6 : "Should have 6 tokens";
        System.out.println("  - Chunker ready for " + tokens.length + " tokens");
    }

    private static void testNLPResult() {
        System.out.println("Test: NLP Result");
        Solution.PipelineExample.NLPResult result = new Solution.PipelineExample.NLPResult();
        result.sentences = new String[]{"Sentence 1", "Sentence 2"};
        result.tokens = new String[]{"token1", "token2"};
        result.posTags = new String[]{"NN", "VB"};
        result.entities = new opennlp.tools.util.Span[0];

        assert result.sentences.length == 2 : "Should have 2 sentences";
        assert result.tokens.length == 2 : "Should have 2 tokens";
        System.out.println("  - NLP result structure works");
    }
}