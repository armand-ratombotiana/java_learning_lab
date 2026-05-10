package com.learning.lab.module78;

import java.util.*;

public class Lab {
    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Module 78: NLP Pipeline Components");
        System.out.println("=".repeat(60));

        pipelineOverview();
        textPreprocessing();
        tokenization();
        normalization();
        posTagging();
        dependencyParsing();
        namedEntityRecognition();
        coreferenceResolution();
        sentenceSimilarity();
        textClassification();
        sentimentAnalysis();
        featureExtraction();
        pipelineOptimization();

        System.out.println("\n" + "=".repeat(60));
        System.out.println("Module 78 Lab Complete!");
        System.out.println("=".repeat(60));
    }

    static void pipelineOverview() {
        System.out.println("\n--- NLP Pipeline Overview ---");
        System.out.println("A sequence of components for processing natural language");

        System.out.println("\nStandard Pipeline:");
        String[] stages = {
            "1. Text Input (raw text, documents)",
            "2. Sentence Segmentation",
            "3. Tokenization",
            "4. Part-of-Speech Tagging",
            "5. Named Entity Recognition",
            "6. Dependency Parsing",
            "7. Coreference Resolution",
            "8. Feature Extraction",
            "9. Task-Specific Processing"
        };
        for (String stage : stages) {
            System.out.println("   " + stage);
        }

        System.out.println("\nPipeline Architecture:");
        System.out.println("   Input -> [Segmenter] -> [Tokenizer] -> [Tagger] ->");
        System.out.println("           [NER] -> [Parser] -> [Features] -> Output");

        System.out.println("\nDesign Patterns:");
        System.out.println("   - Sequential: Simple, one-way flow");
        System.out.println("   - Branching: Multiple processing paths");
        System.out.println("   - Conditional: Based on input type");
        System.out.println("   - Parallel: Independent components");

        System.out.println("\nLibrary Integration:");
        System.out.println("   - Apache OpenNLP: Core NLP tasks");
        System.out.println("   - Stanford CoreNLP: Rich annotations");
        System.out.println("   - Apache Lucene: Text search/indexing");
    }

    static void textPreprocessing() {
        System.out.println("\n--- Text Preprocessing ---");
        System.out.println("Initial cleaning and normalization of raw text");

        System.out.println("\nPreprocessing Steps:");

        System.out.println("\n1. Encoding Detection:");
        System.out.println("   - Detect file encoding (UTF-8, UTF-16, etc.)");
        System.out.println("   - Handle encoding errors");
        System.out.println("   CharsetDetector detector = new CharsetDetector();");
        System.out.println("   Charset charset = detector.detect(bytes);");

        System.out.println("\n2. HTML/XML Removal:");
        String rawText = "<p>This is <b>bold</b> text</p>";
        System.out.println("   Input: \"" + rawText + "\"");
        String cleaned = rawText.replaceAll("<[^>]+>", "");
        System.out.println("   Output: \"" + cleaned + "\"");

        System.out.println("\n3. Unicode Normalization:");
        System.out.println("   - Normalize accented characters");
        System.out.println("   - Handle special symbols");
        String unicode = "café";
        System.out.println("   Input: " + unicode);
        System.out.println("   Normalized: " + unicode.replace("\u0301", ""));

        System.out.println("\n4. Whitespace Handling:");
        String whitespace = "Multiple   spaces   here";
        System.out.println("   Input: \"" + whitespace + "\"");
        System.out.println("   Normalized: \"" + whitespace.replaceAll("\\s+", " ") + "\"");

        System.out.println("\n5. Control Characters:");
        System.out.println("   - Remove newlines, tabs within sentences");
        System.out.println("   - Replace with spaces");

        System.out.println("\n6. Domain-Specific Cleaning:");
        System.out.println("   - Code snippets: preserve syntax");
        System.out.println("   - URLs: remove or replace");
        System.out.println("   - Email addresses: anonymize");
    }

    static void tokenization() {
        System.out.println("\n--- Tokenization ---");
        System.out.println("Splitting text into tokens (words, punctuation, etc.)");

        System.out.println("\nToken Types:");
        String[] tokenTypes = {
            "WORD: alphanumeric sequences",
            "NUMBER: numeric tokens",
            "PUNCT: punctuation marks",
            "URL: web addresses",
            "EMAIL: email addresses",
            "DATE: date expressions"
        };
        for (String type : tokenTypes) {
            System.out.println("   - " + type);
        }

        System.out.println("\nTokenization Examples:");
        String sentence = "I visited google.com on Jan 15th, 2024!";
        String[] tokens = tokenize(sentence);
        System.out.println("   Input: " + sentence);
        System.out.println("   Tokens: " + Arrays.toString(tokens));

        System.out.println("\nTokenization Challenges:");
        System.out.println("   - Contractions: \"don't\" -> \"don\", \"'t\"");
        System.out.println("   - Hyphenation: \"well-known\"");
        System.out.println("   - Unicode: emoji, special characters");
        System.out.println("   - Multi-word expressions");

        System.out.println("\nWhitespace Tokenizer:");
        System.out.println("   WhitespaceTokenizer tokenizer = new WhitespaceTokenizer();");
        System.out.println("   tokenizer.setString(input);");
        System.out.println("   while (tokenizer.hasMoreTokens()) {");
        System.out.println("       String token = tokenizer.nextToken();");
        System.out.println("   }");

        System.out.println("\nPTB Tokenizer (Penn Treebank):");
        System.out.println("   - Splits punctuation");
        System.out.println("   - Handles clitics");
        System.out.println("   - Standard for POS tagging");
    }

    static String[] tokenize(String text) {
        return text.replaceAll("[.,!?;:\\-]", " $0 ").split("\\s+");
    }

    static void normalization() {
        System.out.println("\n--- Text Normalization ---");
        System.out.println("Standardizing text for consistent processing");

        System.out.println("\nNormalization Types:");

        System.out.println("\n1. Lowercasing:");
        String upper = "Hello WORLD";
        System.out.println("   Input: " + upper);
        System.out.println("   Output: " + upper.toLowerCase());

        System.out.println("\n2. Lemmatization:");
        String[] words = {"running", "ran", "runs", "better"};
        String[] lemmas = lemmatize(words);
        System.out.println("   Words: " + Arrays.toString(words));
        System.out.println("   Lemmas: " + Arrays.toString(lemmas));

        System.out.println("\n3. Stemming:");
        String[] stemmed = stem(words);
        System.out.println("   Stems: " + Arrays.toString(stemmed));
        System.out.println("   - Faster than lemmatization");
        System.out.println("   - Less accurate");

        System.out.println("\n4. Stopword Removal:");
        String text = "The quick brown fox jumped over the lazy dog";
        String[] tokens = text.split(" ");
        String filtered = filterStopwords(tokens);
        System.out.println("   Original: " + text);
        System.out.println("   Filtered: " + filtered);

        System.out.println("\n5. Slang Normalization:");
        Map<String, String> slangMap = new HashMap<>();
        slangMap.put("brb", "be right back");
        slangMap.put("idk", "I don't know");
        slangMap.put("btw", "by the way");
        System.out.println("   Slang mapping: " + slangMap);

        System.out.println("\n6. Number Normalization:");
        System.out.println("   \"one hundred twenty three\" -> 123");
        System.out.println("   \"$99.99\" -> 99.99");
    }

    static String[] lemmatize(String[] words) {
        Map<String, String> dict = new HashMap<>();
        dict.put("running", "run");
        dict.put("ran", "run");
        dict.put("runs", "run");
        dict.put("better", "good");
        String[] result = new String[words.length];
        for (int i = 0; i < words.length; i++) {
            result[i] = dict.getOrDefault(words[i], words[i]);
        }
        return result;
    }

    static String[] stem(String[] words) {
        Map<String, String> dict = new HashMap<>();
        dict.put("running", "run");
        dict.put("ran", "ran");
        dict.put("runs", "run");
        dict.put("better", "better");
        String[] result = new String[words.length];
        for (int i = 0; i < words.length; i++) {
            result[i] = dict.getOrDefault(words[i], words[i]);
        }
        return result;
    }

    static String filterStopwords(String[] tokens) {
        Set<String> stopwords = Set.of("the", "a", "an", "is", "are", "was", "were", "over", "the");
        StringBuilder sb = new StringBuilder();
        for (String token : tokens) {
            if (!stopwords.contains(token.toLowerCase())) {
                sb.append(token).append(" ");
            }
        }
        return sb.toString().trim();
    }

    static void posTagging() {
        System.out.println("\n--- Part-of-Speech Tagging ---");
        System.out.println("Assigning grammatical categories to tokens");

        System.out.println("\nPOS Tags:");
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

        System.out.println("\nTagging Process:");
        String sentence = "The quick brown fox jumps over the lazy dog";
        String[] tokens = sentence.split(" ");
        String[] posTagsResult = posTag(tokens);
        System.out.println("   Input: " + sentence);
        System.out.println("   Tags:");
        for (int i = 0; i < tokens.length; i++) {
            System.out.printf("     %s -> %s%n", tokens[i], posTagsResult[i]);
        }

        System.out.println("\nPOS Tagger Implementation:");
        System.out.println("   POSTagger tagger = createTagger();");
        System.out.println("   Sequence sequence = tagger.tag(new Sentence(tokens));");
        System.out.println("   for (TaggedWord word : sequence) {");
        System.out.println("       System.out.println(word.getWord() + \"/\" + word.getTag());");
        System.out.println("   }");
    }

    static String[] posTag(String[] tokens) {
        String[] tags = {"DT", "JJ", "JJ", "NN", "VB", "IN", "DT", "JJ", "NN"};
        if (tokens.length > tags.length) {
            tags = Arrays.copyOf(tags, tokens.length);
            Arrays.fill(tags, tags.length, tokens.length, "NN");
        }
        return tags;
    }

    static void dependencyParsing() {
        System.out.println("\n--- Dependency Parsing ---");
        System.out.println("Analyzing grammatical structure and word relationships");

        System.out.println("\nDependency Relations:");
        Map<String, String> depTypes = new LinkedHashMap<>();
        depTypes.put("nsubj", "Nominal subject");
        depTypes.put("dobj", "Direct object");
        depTypes.put("iobj", "Indirect object");
        depTypes.put("ROOT", "Root of the sentence");
        depTypes.put("det", "Determiner");
        depTypes.put("amod", "Adjective modifier");
        depTypes.put("prep", "Prepositional modifier");
        depTypes.put("pobj", "Object of preposition");
        System.out.println("   " + depTypes);

        System.out.println("\nParse Tree Example:");
        System.out.println("   Sentence: \"The cat sat on the mat\"");
        System.out.println("   ");
        System.out.println("              sat(ROOT)");
        System.out.println("             /       \\");
        System.out.println("         cat       mat");
        System.out.println("        /  |       |  \\");
        System.out.println("     The  .   on   the  .");

        System.out.println("\nDependency Parsing API:");
        System.out.println("   Parser parser = createParser();");
        System.out.println("   DependencyGraph graph = parser.parse(sentence);");
        System.out.println("   for (Dependency relation : graph.relations()) {");
        System.out.println("       String governor = relation.getGovernor().getLemma();");
        System.out.println("       String dependent = relation.getDependent().getLemma();");
        System.out.println("       String type = relation.getType();");
        System.out.println("   }");

        System.out.println("\nApplications:");
        System.out.println("   - Information extraction");
        System.out.println("   - Question answering");
        System.out.println("   - Relation extraction");
    }

    static void namedEntityRecognition() {
        System.out.println("\n--- Named Entity Recognition ---");
        System.out.println("Identifying and classifying entities in text");

        System.out.println("\nEntity Types:");
        Map<String, String> entities = new LinkedHashMap<>();
        entities.put("PERSON", "People names");
        entities.put("LOCATION", "Places, cities");
        entities.put("ORGANIZATION", "Companies, agencies");
        entities.put("DATE", "Dates");
        entities.put("TIME", "Times");
        entities.put("MONEY", "Currency");
        entities.put("PERCENT", "Percentages");
        entities.put("GPE", "Geo-political entities");
        System.out.println("   " + entities);

        System.out.println("\nNER Process:");
        String text = "Apple Inc. was founded by Steve Jobs in Cupertino on April 1, 1976";
        System.out.println("   Input: " + text);
        List<NERResult> results = extractEntities(text);
        System.out.println("   Entities:");
        for (NERResult r : results) {
            System.out.printf("     [%s] %s -> %s%n", r.type, r.text, r.span);
        }

        System.out.println("\nNER Implementation:");
        System.out.println("   NERecognizer recognizer = createRecognizer();");
        System.out.println("   List<CoreMap> entities = recognizer.recognize(sentence);");
        System.out.println("   for (CoreMap entity : entities) {");
        System.out.println("       String type = entity.get(EntityType.class);");
        System.out.println("       String mention = entity.get(Text.class);");
        System.out.println("   }");

        System.out.println("\nCustom NER:");
        System.out.println("   - Domain-specific entities");
        System.out.println("   - Training data required");
        System.out.println("   - CRF or neural models");
    }

    static List<NERResult> extractEntities(String text) {
        List<NERResult> results = new ArrayList<>();
        results.add(new NERResult("Apple Inc.", "ORGANIZATION", "0-9"));
        results.add(new NERResult("Steve Jobs", "PERSON", "23-32"));
        results.add(new NERResult("Cupertino", "LOCATION", "38-47"));
        results.add(new NERResult("April 1, 1976", "DATE", "52-65"));
        return results;
    }

    static class NERResult {
        String text, type, span;
        NERResult(String text, String type, String span) {
            this.text = text; this.type = type; this.span = span;
        }
    }

    static void coreferenceResolution() {
        System.out.println("\n--- Coreference Resolution ---");
        System.out.println("Linking mentions that refer to the same entity");

        System.out.println("\nCoreference Types:");
        System.out.println("   - Pronouns: He, she, it, they");
        System.out.println("   - Definite noun phrases: the company");
        System.out.println("   - Named entities: John = Mr. Smith");

        System.out.println("\nExample:");
        String[] sentences = {
            "John works at Google.",
            "He leads the AI team.",
            "He joined the company in 2019."
        };
        System.out.println("   Sentences:");
        for (String s : sentences) {
            System.out.println("     " + s);
        }

        System.out.println("\n   Coreference Chain: John -> He -> He");
        System.out.println("   Entity: John (mentioned 3 times)");

        System.out.println("\nCoreference API:");
        System.out.println("   CoreferenceSystem coref = createCorefSystem();");
        System.out.println("   Map<Mention, Entity> clusters = coref.resolve(document);");
        System.out.println("   ");
        System.out.println("   for (Entity entity : clusters.values()) {");
        System.out.println("       List<Mention> mentions = entity.getMentions();");
        System.out.println("       System.out.println(mentions);");
        System.out.println("   }");

        System.out.println("\nApplications:");
        System.out.println("   - Text understanding");
        System.out.println("   - Question answering");
        System.out.println("   - Summarization");
    }

    static void sentenceSimilarity() {
        System.out.println("\n--- Sentence Similarity ---");
        System.out.println("Measuring semantic similarity between sentences");

        System.out.println("\nSimilarity Methods:");

        System.out.println("\n1. Word Overlap (Jaccard):");
        String s1 = "The cat is on the mat";
        String s2 = "The dog is on the rug";
        double jaccard = jaccardSimilarity(s1, s2);
        System.out.println("   \"" + s1 + "\"");
        System.out.println("   vs");
        System.out.println("   \"" + s2 + "\"");
        System.out.printf("   Jaccard: %.2f%n", jaccard);

        System.out.println("\n2. Word Mover's Distance:");
        System.out.println("   - Measures minimum work to transform one text to another");
        System.out.println("   - Uses word embeddings");

        System.out.println("\n3. Semantic Embedding Similarity:");
        double[] embed1 = {0.1, 0.3, 0.5, 0.7};
        double[] embed2 = {0.2, 0.3, 0.4, 0.6};
        double cosine = cosineSimilarity(embed1, embed2);
        System.out.printf("   Embedding cosine: %.4f%n", cosine);

        System.out.println("\n4. Semantic Textual Similarity (STS):");
        Map<String, Double> scores = new LinkedHashMap<>();
        scores.put("Identical sentences", 1.0);
        scores.put("Synonymous sentences", 0.85);
        scores.put("Related sentences", 0.6);
        scores.put("Unrelated sentences", 0.1);
        System.out.println("   " + scores);

        System.out.println("\nUse Cases:");
        System.out.println("   - Duplicate detection");
        System.out.println("   - Semantic search");
        System.out.println("   - Paraphrase detection");
    }

    static double jaccardSimilarity(String s1, String s2) {
        Set<String> set1 = new HashSet<>(Arrays.asList(s1.toLowerCase().split(" ")));
        Set<String> set2 = new HashSet<>(Arrays.asList(s2.toLowerCase().split(" ")));
        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);
        return (double) intersection.size() / union.size();
    }

    static double cosineSimilarity(double[] a, double[] b) {
        double dot = 0, magA = 0, magB = 0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            magA += a[i] * a[i];
            magB += b[i] * b[i];
        }
        return dot / (Math.sqrt(magA) * Math.sqrt(magB));
    }

    static void textClassification() {
        System.out.println("\n--- Text Classification ---");
        System.out.println("Categorizing text into predefined classes");

        System.out.println("\nClassification Types:");
        System.out.println("   - Binary: Spam/not spam");
        System.out.println("   - Multi-class: Topic classification");
        System.out.println("   - Multi-label: Multiple categories");

        System.out.println("\nMethods:");

        System.out.println("\n1. Naive Bayes:");
        System.out.println("   - Probabilistic");
        System.out.println("   - Fast training");
        double nbAccuracy = 91.5;
        System.out.printf("   Accuracy: %.1f%%%n", nbAccuracy);

        System.out.println("\n2. Support Vector Machine:");
        System.out.println("   - High-dimensional features");
        System.out.println("   - Effective for text");
        double svmAccuracy = 94.2;
        System.out.printf("   Accuracy: %.1f%%%n", svmAccuracy);

        System.out.println("\n3. Deep Learning:");
        System.out.println("   - BERT, RoBERTa fine-tuned");
        System.out.println("   - Best accuracy");
        double dlAccuracy = 97.8;
        System.out.printf("   Accuracy: %.1f%%%n", dlAccuracy);

        System.out.println("\nFeature Extraction:");
        System.out.println("   - TF-IDF vectors");
        System.out.println("   - Word embeddings");
        System.out.println("   - Transformer embeddings");

        System.out.println("\nClassification Example:");
        Map<String, String> testDocs = new LinkedHashMap<>();
        testDocs.put("Win free lottery now!", "SPAM");
        testDocs.put("Meeting scheduled for 3pm", "NOTIFICATION");
        testDocs.put("Your order is on the way", "NOTIFICATION");
        for (Map.Entry<String, String> entry : testDocs.entrySet()) {
            System.out.println("   \"" + entry.getKey() + "\" -> " + entry.getValue());
        }
    }

    static void sentimentAnalysis() {
        System.out.println("\n--- Sentiment Analysis ---");
        System.out.println("Determining the sentiment expressed in text");

        System.out.println("\nSentiment Levels:");
        System.out.println("   - Document-level: Overall sentiment");
        System.out.println("   - Sentence-level: Per-sentence analysis");
        System.out.println("   - Aspect-level: Specific aspects");

        System.out.println("\nSentiment Categories:");
        System.out.println("   - Positive");
        System.out.println("   - Negative");
        System.out.println("   - Neutral");

        System.out.println("\nExample Analysis:");
        String[] reviews = {
            "This product is amazing! Best purchase ever.",
            "Terrible quality. Broke after one use.",
            "Decent product. Nothing special."
        };
        String[] sentiments = analyzeSentiment(reviews);
        for (int i = 0; i < reviews.length; i++) {
            System.out.printf("   \"%s...\" -> %s%n", reviews[i].substring(0, 25), sentiments[i]);
        }

        System.out.println("\nSentiment Scores:");
        Map<String, Double> scores = new LinkedHashMap<>();
        scores.put("Strong Positive", 0.9);
        scores.put("Positive", 0.65);
        scores.put("Neutral", 0.0);
        scores.put("Negative", -0.5);
        scores.put("Strong Negative", -0.9);
        System.out.println("   " + scores);

        System.out.println("\nImplementation:");
        System.out.println("   SentimentAnalyzer analyzer = createAnalyzer();");
        System.out.println("   SentimentResult result = analyzer.analyze(text);");
        System.out.println("   double score = result.getScore();");
        System.out.println("   Sentiment sentiment = result.getSentiment();");
    }

    static String[] analyzeSentiment(String[] reviews) {
        String[] sentiments = new String[reviews.length];
        for (int i = 0; i < reviews.length; i++) {
            if (reviews[i].contains("amazing") || reviews[i].contains("Best")) {
                sentiments[i] = "Positive";
            } else if (reviews[i].contains("Terrible") || reviews[i].contains("Broke")) {
                sentiments[i] = "Negative";
            } else {
                sentiments[i] = "Neutral";
            }
        }
        return sentiments;
    }

    static void featureExtraction() {
        System.out.println("\n--- Feature Extraction ---");
        System.out.println("Converting text to numerical features");

        System.out.println("\nFeature Types:");

        System.out.println("\n1. Bag of Words (BoW):");
        String[] vocab = {"cat", "dog", "mat", "sat"};
        double[] bowVector = {1, 0, 1, 1};
        System.out.println("   Vocabulary: " + Arrays.toString(vocab));
        System.out.println("   Vector: " + Arrays.toString(bowVector));

        System.out.println("\n2. TF-IDF:");
        Map<String, Double> tfidf = new LinkedHashMap<>();
        tfidf.put("cat", 0.5);
        tfidf.put("sat", 0.8);
        tfidf.put("the", 0.1);
        System.out.println("   " + tfidf);

        System.out.println("\n3. N-grams:");
        System.out.println("   Unigrams: cat, sat, on, mat");
        System.out.println("   Bigrams: cat sat, sat on, on the");
        System.out.println("   Trigrams: cat sat on, sat on the");

        System.out.println("\n4. Word Embeddings:");
        System.out.println("   - Word2Vec");
        System.out.println("   - GloVe");
        System.out.println("   - FastText");
        System.out.println("   - Contextual (BERT)");

        System.out.println("\n5. Character Features:");
        System.out.println("   - Character n-grams");
        System.out.println("   - Character embeddings");
        System.out.println("   - Subword information");

        System.out.println("\nFeature Selection:");
        System.out.println("   - Chi-squared test");
        System.out.println("   - Mutual information");
        System.out.println("   - Information gain");
    }

    static void pipelineOptimization() {
        System.out.println("\n--- Pipeline Optimization ---");
        System.out.println("Improving NLP pipeline performance");

        System.out.println("\nOptimization Techniques:");

        System.out.println("\n1. Caching:");
        System.out.println("   - Cache tokenization results");
        System.out.println("   - Cache parsed trees");
        System.out.println("   - Cache embeddings");
        System.out.println("   Cache<String, ParseTree> cache = Caffeine.newBuilder()");
        System.out.println("       .expireAfterWrite(Duration.ofMinutes(10))");
        System.out.println("       .build();");

        System.out.println("\n2. Parallel Processing:");
        System.out.println("   - Process sentences in parallel");
        System.out.println("   - Use thread pools");
        System.out.println("   ExecutorService executor = Executors.newFixedThreadPool(4);");

        System.out.println("\n3. Lazy Evaluation:");
        System.out.println("   - Only compute when needed");
        System.out.println("   - Conditional processing");
        System.out.println("   if (needsEntityRecognition()) {");
        System.out.println("       runNER();");
        System.out.println("   }");

        System.out.println("\n4. Model Optimization:");
        System.out.println("   - Use smaller models when possible");
        System.out.println("   - Quantization");
        System.out.println("   - Pruning");

        System.out.println("\n5. Memory Management:");
        System.out.println("   - Process in batches");
        System.out.println("   - Release resources promptly");
        System.out.println("   - Use streaming for large documents");

        System.out.println("\nPerformance Metrics:");
        Map<String, String> metrics = new LinkedHashMap<>();
        metrics.put("Throughput", "1000 docs/sec");
        metrics.put("Latency (p95)", "50ms");
        metrics.put("Memory", "2GB");
        System.out.println("   " + metrics);
    }
}