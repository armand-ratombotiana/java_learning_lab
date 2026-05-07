package com.learning.opennlp;

import java.util.*;

public class OpenNLPLab {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Apache OpenNLP Lab ===\n");

        System.out.println("1. Sentence Detection:");
        System.out.println("   SentenceModel model = new SentenceModel(IN);");
        System.out.println("   SentenceDetectorME detector = new SentenceDetectorME(model);");
        System.out.println("   String[] sentences = detector.sentDetect(");
        System.out.println("       \"Java is a programming language. It runs on the JVM.\");");

        System.out.println("\n2. Tokenization:");
        System.out.println("   TokenizerModel model = new TokenizerModel(IN);");
        System.out.println("   TokenizerME tokenizer = new TokenizerME(model);");
        System.out.println("   String[] tokens = tokenizer.tokenize(\"Java is a programming language.\");");
        System.out.println("   // Output: [Java, is, a, programming, language]");

        System.out.println("\n3. Part-of-Speech Tagging:");
        System.out.println("   POSModel model = new POSModel(IN);");
        System.out.println("   POSTaggerME tagger = new POSTaggerME(model);");
        System.out.println("   String[] tags = tagger.tag(tokens);");
        System.out.println("   // Output: [NNP, VBZ, DT, NN, NN]");

        System.out.println("\n4. Named Entity Recognition:");
        System.out.println("   TokenNameFinderModel model = new TokenNameFinderModel(IN);");
        System.out.println("   NameFinderME finder = new NameFinderME(model);");
        System.out.println("   Span[] spans = finder.find(tokens);");
        System.out.println("   // Finds: person, organization, location, date, time, money");

        System.out.println("\n5. Language Detection:");
        System.out.println("   LanguageDetectorModel model = new LanguageDetectorModel(IN);");
        System.out.println("   LanguageDetectorME detector = new LanguageDetectorME(model);");
        System.out.println("   Language[] langs = detector.predictLanguages(\"Hello world\");");
        System.out.println("   // Returns: English (0.99), French (0.01)");

        System.out.println("\n6. Document Classification:");
        System.out.println("   DoccatModel model = new DoccatModel(IN);");
        System.out.println("   DocumentCategorizerME categorizer = new DocumentCategorizerME(model);");
        System.out.println("   double[] outcomes = categorizer.categorize(tokens);");
        System.out.println("   String category = categorizer.getBestCategory(outcomes);");

        System.out.println("\n7. Chunking:");
        System.out.println("   ChunkerModel model = new ChunkerModel(IN);");
        System.out.println("   ChunkerME chunker = new ChunkerME(model);");
        System.out.println("   String[] chunks = chunker.chunk(tokens, tags);");
        System.out.println("   // Output: [B-NP, I-NP, B-VP, B-NP]");

        System.out.println("\n8. Pre-trained Models Available:");
        System.out.println("   - en-sent.bin: English sentence detector");
        System.out.println("   - en-token.bin: English tokenizer");
        System.out.println("   - en-pos-maxent.bin: English POS tagger");
        System.out.println("   - en-ner-person.bin: Person name finder");
        System.out.println("   - en-ner-location.bin: Location finder");
        System.out.println("   - en-ner-date.bin: Date finder");

        System.out.println("\n=== Apache OpenNLP Lab Complete ===");
    }
}