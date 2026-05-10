package com.learning.lab.module74;

import java.util.*;

public class Lab {
    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Module 74: Apache OpenNLP - NLP Tasks");
        System.out.println("=".repeat(60));

        opennlpOverview();
        sentenceDetection();
        tokenization();
        posTagging();
        chunking();
        namedEntityRecognition();
        lemmatization();
        coreference();
        parsing();
        documentCategorization();
        languageDetection();
        customModels();

        System.out.println("\n" + "=".repeat(60));
        System.out.println("Module 74 Lab Complete!");
        System.out.println("=".repeat(60));
    }

    static void opennlpOverview() {
        System.out.println("\n--- Apache OpenNLP Overview ---");
        System.out.println("OpenNLP is a machine learning-based toolkit for NLP tasks");

        System.out.println("\nKey Features:");
        System.out.println("   - Sentence detection");
        System.out.println("   - Tokenization");
        System.out.println("   - Part-of-Speech tagging");
        System.out.println("   - Chunking");
        System.out.println("   - Named Entity Recognition");
        System.out.println("   - Parsing");
        System.out.println("   - Coreference resolution");
        System.out.println("   - Language detection");

        System.out.println("\nArchitecture:");
        System.out.println("   Input -> Pipeline -> Models -> Output");
        System.out.println("   ");
        System.out.println("   +------------------+");
        System.out.println("   | Document        |");
        System.out.println("   +--------+---------+");
        System.out.println("            |");
        System.out.println("   +--------v---------+");
        System.out.println("   | Pipeline        |");
        System.out.println("   | (Tokenizer,     |");
        System.out.println("   |  POS Tagger,    |");
        System.out.println("   |  NER, Parser)   |");
        System.out.println("   +--------+---------+");
        System.out.println("            |");
        System.out.println("   +--------v---------+");
        System.out.println("   | Output          |");
        System.out.println("   +------------------+");

        System.out.println("\nModel Types:");
        System.out.println("   - en-token.bin: English tokenizer");
        System.out.println("   - en-pos-maxent.bin: POS tagger");
        System.out.println("   - en-ner-*.bin: Named entity recognizer");
        System.out.println("   - en-parser-*: Dependency parser");
    }

    static void sentenceDetection() {
        System.out.println("\n--- Sentence Detection ---");
        System.out.println("Identify sentence boundaries in text");

        System.out.println("\nConcept:");
        System.out.println("   - Detect end of sentence markers");
        System.out.println("   - Handle abbreviations, quotes");
        System.out.println("   - Multi-line text processing");

        System.out.println("\nUse Cases:");
        String[] useCases = {"Text preprocessing", "Text-to-speech", "Text summarization",
                           "Information extraction", "Machine translation"};
        for (String use : useCases) {
            System.out.println("   - " + use);
        }

        System.out.println("\nExample:");
        String text = "Dr. Smith works at Microsoft. He lives in New York. " +
                     "NLP is fascinating! It has many applications.";
        System.out.println("   Input: \"" + text + "\"");

        String[] sentences = splitIntoSentences(text);
        System.out.println("\n   Detected Sentences:");
        for (int i = 0; i < sentences.length; i++) {
            System.out.printf("     [%d] %s%n", i + 1, sentences[i]);
        }

        System.out.println("\nSentence Detection API:");
        System.out.println("   SentenceModel model = new SentenceModel(");
        System.out.println("       new FileInputStream(\"en-sent.bin\"));");
        System.out.println("   SentenceDetectorME detector = new SentenceDetectorME(model);");
        System.out.println("   String[] sentences = detector.sentDetect(text);");
    }

    static String[] splitIntoSentences(String text) {
        String[] abbrev = {"Dr.", "Mr.", "Mrs.", "Ms.", "Prof.", "Inc.", "Ltd."};
        String processed = text;
        for (String ab : abbrev) {
            processed = processed.replace(ab, ab.replace(".", "<ABBREV>"));
        }
        return processed.split("(?<=[.!?])\\s+");
    }

    static void tokenization() {
        System.out.println("\n--- Tokenization ---");
        System.out.println("Split text into individual tokens (words, punctuation)");

        System.out.println("\nToken Types:");
        System.out.println("   - Words: alphanumeric sequences");
        System.out.println("   - Numbers: numeric sequences");
        System.out.println("   - Punctuation: separate tokens");
        System.out.println("   - Contractions: split or keep together");

        System.out.println("\nTokenizer Types:");
        System.out.println("   1. SimpleTokenizer: Whitespace-based");
        System.out.println("   2. TokenizerME: ML-based");
        System.out.println("   3. WhitespaceTokenizer: Split on spaces");
        System.out.println("   4. SimpleTokenizer: Split on punctuation");

        System.out.println("\nExample:");
        String sentence = "Java's new features include virtual threads and structured concurrency.";
        System.out.println("   Input: \"" + sentence + "\"");

        String[] tokens = tokenize(sentence);
        System.out.println("\n   Tokens:");
        for (int i = 0; i < tokens.length; i++) {
            System.out.printf("     [%d] '%s'%n", i + 1, tokens[i]);
        }

        System.out.println("\nTokenizer API:");
        System.out.println("   TokenizerModel model = new TokenizerModel(");
        System.out.println("       new FileInputStream(\"en-token.bin\"));");
        System.out.println("   Tokenizer tokenizer = new TokenizerME(model);");
        System.out.println("   String[] tokens = tokenizer.tokenize(text);");

        System.out.println("\nAdvanced Tokenization:");
        System.out.println("   - PTBTokenizer: Penn Treebank style");
        System.out.println("   - Institution tokens handling");
        System.out.println("   - Emoji and special character handling");
    }

    static String[] tokenize(String text) {
        String cleaned = text.replaceAll("[.,!?;:\\-\"'()]", " $0 ");
        return cleaned.split("\\s+");
    }

    static void posTagging() {
        System.out.println("\n--- Part-of-Speech Tagging ---");
        System.out.println("Assign grammatical categories to tokens");

        System.out.println("\nPOS Tags (Penn Treebank):");
        Map<String, String> posTags = new LinkedHashMap<>();
        posTags.put("NN", "Noun (singular)");
        posTags.put("NNS", "Noun (plural)");
        posTags.put("VB", "Verb (base)");
        posTags.put("VBD", "Verb (past)");
        posTags.put("VBG", "Verb (gerund)");
        posTags.put("JJ", "Adjective");
        posTags.put("RB", "Adverb");
        posTags.put("DT", "Determiner");
        posTags.put("IN", "Preposition");
        posTags.put("CC", "Conjunction");
        System.out.println("   " + posTags);

        System.out.println("\nExample:");
        String sentence = "The quick brown fox jumps over the lazy dog.";
        System.out.println("   Input: \"" + sentence + "\"");

        String[] tokens = tokenize(sentence);
        String[] posTags = assignPOSTags(tokens);

        System.out.println("\n   Tagged:");
        for (int i = 0; i < tokens.length; i++) {
            System.out.printf("     %s / %s%n", tokens[i], posTags[i]);
        }

        System.out.println("\nPOS Tagger API:");
        System.out.println("   POSModel model = new POSModel(");
        System.out.println("       new FileInputStream(\"en-pos-maxent.bin\"));");
        System.out.println("   POSTaggerME tagger = new POSTaggerME(model);");
        System.out.println("   String[] tags = tagger.tag(tokens);");
    }

    static String[] assignPOSTags(String[] tokens) {
        String[] simpleTags = {"DT", "JJ", "JJ", "NN", "VB", "IN", "DT", "JJ", "NN", "."};
        if (tokens.length >= simpleTags.length) {
            return Arrays.copyOf(simpleTags, tokens.length);
        }
        String[] result = new String[tokens.length];
        Arrays.fill(result, "UNK");
        System.arraycopy(simpleTags, 0, result, 0, Math.min(simpleTags.length, tokens.length));
        return result;
    }

    static void chunking() {
        System.out.println("\n--- Chunking ---");
        System.out.println("Identify phrases (groups of tokens) in text");

        System.out.println("\nChunk Types:");
        System.out.println("   - NP (Noun Phrase): The quick brown fox");
        System.out.println("   - VP (Verb Phrase): jumps over");
        System.out.println("   - PP (Prepositional Phrase): over the lazy dog");
        System.out.println("   - ADJP (Adjective Phrase): very happy");

        System.out.println("\nExample:");
        String sentence = "The quick brown fox jumps over the lazy dog";
        System.out.println("   Input: \"" + sentence + "\"");

        String[] tokens = sentence.split(" ");
        String[] posTags = assignPOSTags(tokens);
        String[] chunks = chunk(tokens, posTags);

        System.out.println("\n   Chunks:");
        for (int i = 0; i < tokens.length; i++) {
            System.out.printf("     %s [%s] -> %s%n", tokens[i], posTags[i], chunks[i]);
        }

        System.out.println("\nChunker API:");
        System.out.println("   ChunkerModel model = new ChunkerModel(");
        System.out.println("       new FileInputStream(\"en-chunker.bin\"));");
        System.out.println("   ChunkerME chunker = new ChunkerME(model);");
        System.out.println("   String[] chunks = chunker.chunk(tokens, tags);");
    }

    static String[] chunk(String[] tokens, String[] posTags) {
        String[] chunks = new String[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            if (i == 0 || posTags[i].startsWith("DT") || posTags[i].startsWith("JJ") ||
                posTags[i].startsWith("NN")) {
                chunks[i] = "B-NP";
            } else if (posTags[i].startsWith("NN")) {
                chunks[i] = "I-NP";
            } else if (posTags[i].startsWith("VB")) {
                chunks[i] = "B-VP";
            } else {
                chunks[i] = "O";
            }
        }
        return chunks;
    }

    static void namedEntityRecognition() {
        System.out.println("\n--- Named Entity Recognition (NER) ---");
        System.out.println("Identify and classify named entities in text");

        System.out.println("\nEntity Types:");
        Map<String, String> entityTypes = new LinkedHashMap<>();
        entityTypes.put("PERSON", "People names");
        entityTypes.put("LOCATION", "Places, cities, countries");
        entityTypes.put("ORGANIZATION", "Companies, agencies");
        entityTypes.put("DATE", "Dates, time expressions");
        entityTypes.put("MONEY", "Currency amounts");
        entityTypes.put("PERCENT", "Percentage values");
        entityTypes.put("TIME", "Time expressions");
        System.out.println("   " + entityTypes);

        System.out.println("\nExample:");
        String text = "Apple Inc. was founded by Steve Jobs in Cupertino on April 1, 1976.";
        System.out.println("   Input: \"" + text + "\"");

        List<NEREntity> entities = extractNamedEntities(text);
        System.out.println("\n   Detected Entities:");
        for (NEREntity entity : entities) {
            System.out.printf("     [%s] \"%s\" (%s)%n", entity.type, entity.text, entity.span);
        }

        System.out.println("\nNER API:");
        System.out.println("   NERModel model = new NERModel(");
        System.out.println("       new FileInputStream(\"en-ner-person.bin\"));");
        System.out.println("   NERecognizerME recognizer = new NERecognizerME(model);");
        System.out.println("   Span[] spans = recognizer.find(text);");
    }

    static List<NEREntity> extractNamedEntities(String text) {
        List<NEREntity> entities = new ArrayList<>();
        entities.add(new NEREntity("Apple Inc.", "ORGANIZATION", "0-9"));
        entities.add(new NEREntity("Steve Jobs", "PERSON", "24-33"));
        entities.add(new NEREntity("Cupertino", "LOCATION", "45-54"));
        entities.add(new NEREntity("April 1, 1976", "DATE", "59-72"));
        return entities;
    }

    static class NEREntity {
        String text, type, span;
        NEREntity(String text, String type, String span) {
            this.text = text; this.type = type; this.span = span;
        }
    }

    static void lemmatization() {
        System.out.println("\n--- Lemmatization ---");
        System.out.println("Reduce words to their base/dictionary form (lemma)");

        System.out.println("\nLemmatization vs Stemming:");
        System.out.println("   - Stemming: Crude suffix removal (running -> run)");
        System.out.println("   - Lemmatization: Dictionary-based (running -> run, better -> good)");

        System.out.println("\nExample:");
        String[] words = {"running", "better", "children", "went", "cars", "playing"};
        System.out.println("   Words: " + Arrays.toString(words));

        String[] lemmas = lemmatize(words);
        System.out.println("\n   Lemmas:");
        for (int i = 0; i < words.length; i++) {
            System.out.printf("     %s -> %s%n", words[i], lemmas[i]);
        }

        System.out.println("\nLemmatizer API:");
        System.out.println("   LemmatizerModel model = new LemmatizerModel(");
        System.out.println("       new FileInputStream(\"en-lemmatizer.bin\"));");
        System.out.println("   Lemmatizer lemmatizer = new LemmatizerME(model);");
        System.out.println("   String[] lemmas = lemmatizer.lemmatize(tokens, tags);");
    }

    static String[] lemmatize(String[] words) {
        Map<String, String> dictionary = new HashMap<>();
        dictionary.put("running", "run");
        dictionary.put("better", "good");
        dictionary.put("children", "child");
        dictionary.put("went", "go");
        dictionary.put("cars", "car");
        dictionary.put("playing", "play");
        String[] lemmas = new String[words.length];
        for (int i = 0; i < words.length; i++) {
            lemmas[i] = dictionary.getOrDefault(words[i], words[i]);
        }
        return lemmas;
    }

    static void coreference() {
        System.out.println("\n--- Coreference Resolution ---");
        System.out.println("Identify when different expressions refer to the same entity");

        System.out.println("\nCoreference Types:");
        System.out.println("   - Pronouns: He, she, it, they");
        System.out.println("   - Named entities: John -> Mr. Smith");
        System.out.println("   - Definite noun phrases: The company -> Apple");

        System.out.println("\nExample:");
        String[] sentences = {
            "John works at Microsoft.",
            "He loves programming.",
            "He joined the company 5 years ago."
        };
        System.out.println("   Sentences:");
        for (String s : sentences) {
            System.out.println("     " + s);
        }

        System.out.println("\n   Coreference Chains:");
        System.out.println("     Chain 1: John -> He -> He");
        System.out.println("     Chain 2: Microsoft -> the company");

        System.out.println("\nCoreference API:");
        System.out.println("   CoreferenceModel model = new CoreferenceModel(");
        System.out.println("       new FileInputStream(\"en-coref.bin\"));");
        System.out.println("   CoreferenceAnalyzer analyzer = new CoreferenceAnalyzer(model);");
        System.out.println("   List<CoreferenceCluster> clusters = analyzer.analyze(document);");
    }

    static void parsing() {
        System.out.println("\n--- Parsing ---");
        System.out.println("Analyze grammatical structure of sentences");

        System.out.println("\nParser Types:");
        System.out.println("   1. Constituency Parser: Phrase structure tree");
        System.out.println("   2. Dependency Parser: Word-to-word relationships");

        System.out.println("\nExample Parse Tree:");
        System.out.println("   Sentence");
        System.out.println("   ├── NP (The quick brown fox)");
        System.out.println("   │   ├── DT (The)");
        System.out.println("   │   ├── ADJP (quick brown)");
        System.out.println("   │   └── NN (fox)");
        System.out.println("   └── VP (jumps over the lazy dog)");
        System.out.println("       ├── VB (jumps)");
        System.out.println("       └── PP (over the lazy dog)");
        System.out.println("           ├── IN (over)");
        System.out.println("           └── NP (the lazy dog)");

        System.out.println("\nDependency Relations:");
        Map<String, String> depRelations = new LinkedHashMap<>();
        depRelations.put("nsubj", "Nominal subject");
        depRelations.put("dobj", "Direct object");
        depRelations.put("iobj", "Indirect object");
        depRelations.put("prep", "Prepositional modifier");
        depRelations.put("ROOT", "Root of sentence");
        System.out.println("   " + depRelations);

        System.out.println("\nParser API:");
        System.out.println("   ParserModel model = new ParserModel(");
        System.out.println("       new FileInputStream(\"en-parser-chunking.bin\"));");
        System.out.println("   Parser parser = ParserFactory.create(model);");
        System.out.println("   Parse parse = parser.parse(sentence);");
    }

    static void documentCategorization() {
        System.out.println("\n--- Document Categorization ---");
        System.out.println("Classify documents into predefined categories");

        System.out.println("\nCategorization Approaches:");
        System.out.println("   - Text classification: Spam detection, sentiment");
        System.out.println("   - Topic modeling: Discover topics in corpus");
        System.out.println("   - Multi-label: Multiple categories per doc");

        System.out.println("\nAlgorithms:");
        System.out.println("   - Naive Bayes: Probabilistic classification");
        System.out.println("   - MaxEnt: Maximum entropy model");
        System.out.println("   - SVM: Support vector machine");

        System.out.println("\nExample:");
        Map<String, String> testDocuments = new LinkedHashMap<>();
        testDocuments.put("Buy now! Limited offer!", "SPAM");
        testDocuments.put("Meeting at 3pm tomorrow", "NOTIFICATION");
        testDocuments.put("Your order has shipped", "NOTIFICATION");
        testDocuments.put("Win free prize click here", "SPAM");
        testDocuments.put("Project deadline extended", "WORK");
        System.out.println("   Test Documents:");
        for (Map.Entry<String, String> entry : testDocuments.entrySet()) {
            System.out.printf("     \"%s\" -> %s%n", entry.getKey(), entry.getValue());
        }

        System.out.println("\nDoccat API:");
        System.out.println("   DoccatModel model = new DoccatModel(");
        System.out.println("       new FileInputStream(\"en-sentiment.bin\"));");
        System.out.println("   DocumentCategorizerME categorizer = new DocumentCategorizerME(model);");
        System.out.println("   String category = categorizer.categorize(text);");
    }

    static void languageDetection() {
        System.out.println("\n--- Language Detection ---");
        System.out.println("Identify the language of given text");

        System.out.println("\nSupported Languages:");
        String[] languages = {"English", "German", "French", "Spanish", "Italian",
                            "Portuguese", "Dutch", "Russian", "Chinese", "Japanese"};
        System.out.println("   " + Arrays.toString(languages));

        System.out.println("\nExample:");
        String[] testTexts = {
            "This is a sample text in English",
            "Dies ist ein Beispieltext auf Deutsch",
            "Ceci est un exemple de texte en francais",
            "Este es un texto de ejemplo en espanol"
        };

        System.out.println("   Detection Results:");
        for (String text : testTexts) {
            String detected = detectLanguage(text);
            System.out.printf("     \"%s...\" -> %s%n", text.substring(0, 20), detected);
        }

        System.out.println("\nLanguage Detector API:");
        System.out.println("   LanguageModel model = new LanguageModel(");
        System.out.println("       new FileInputStream(\"langdetect.bin\"));");
        System.out.println("   LanguageDetector detector = new LanguageDetectorME(model);");
        System.out.println("   String language = detector.detect(text);");
    }

    static String detectLanguage(String text) {
        if (text.matches(".*[aeiou].*") && text.contains("th")) return "English";
        if (text.contains("ist") && text.contains("ein")) return "German";
        if (text.contains("est") && text.contains("une")) return "French";
        if (text.contains("es") && text.contains("un")) return "Spanish";
        return "Unknown";
    }

    static void customModels() {
        System.out.println("\n--- Training Custom Models ---");
        System.out.println("Create specialized models for domain-specific NLP");

        System.out.println("\nTraining Pipeline:");
        System.out.println("   1. Prepare training data");
        System.out.println("   2. Configure training parameters");
        System.out.println("   3. Train the model");
        System.out.println("   4. Evaluate performance");
        System.out.println("   5. Save and deploy model");

        System.out.println("\nTraining POS Tagger:");
        System.out.println("   TrainingParameters params = new TrainingParameters();");
        System.out.println("   params.put(TrainingParameters.CUTOFF, 5);");
        System.out.println("   params.put(TrainingParameters.ITERATIONS, 100);");
        System.out.println("   ");
        System.out.println("   POSDictionary dictionary = ...;");
        System.out.println("   POSTaggerFactory factory = new POSTaggerFactory();");
        System.out.println("   POSModel model = trainer.train(params);");

        System.out.println("\nTraining NER Model:");
        System.out.println("   trainingData = new File(\"ner-train.txt\");");
        System.out.println("   NERTrainingSample[] samples = ...;");
        System.out.println("   NERrainer trainer = new NERrainer();");
        System.out.println("   NERModel model = trainer.train(samples);");

        System.out.println("\nData Formats:");
        System.out.println("   - Token/Tag format: word/tag");
        System.out.println("   - XML format with annotations");
        System.out.println("   - JSON with token/label arrays");

        System.out.println("\nEvaluation Metrics:");
        Map<String, Double> metrics = new LinkedHashMap<>();
        metrics.put("Precision", 0.92);
        metrics.put("Recall", 0.89);
        metrics.put("F1-Score", 0.90);
        System.out.println("   " + metrics);
    }
}