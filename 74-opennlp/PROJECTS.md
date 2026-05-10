# OpenNLP Projects - Module 74

This module covers NLP tasks including Named Entity Recognition (NER), Part-of-Speech (POS) tagging, sentence detection, and text processing using Apache OpenNLP.

## Table of Contents

- [Mini-Projects per Concept](#mini-projects-per-concept)
  1. [Sentence Detection](#1-sentence-detection-2-hours)
  2. [Tokenization](#2-tokenization-2-hours)
  3. [POS Tagging](#3-pos-tagging-2-hours)
  4. [Chunking/Parsing](#4-chunkingparsing-2-hours)
  5. [Named Entity Recognition](#5-named-entity-recognition-2-hours)
  6. [Lemmatization](#6-lemmatization-2-hours)
  7. [Coreference Resolution](#7-coreference-resolution-2-hours)
  8. [Binary Spinning (Model Training)](#8-binary-spinning-model-training-2-hours)
  9. [Custom Model Training](#9-custom-model-training-3-hours)
  10. [Document Categorization](#10-document-categorization-2-hours)
- [Real-World Projects](#real-world-projects)
  1. [Resume Parser System](#1-resume-parser-system)
  2. [News Article Classifier](#2-news-article-classifier)
  3. [Medical Record NLP Pipeline](#3-medical-record-nlp-pipeline)
  4. [Legal Document Analyzer](#4-legal-document-analyzer)
  5. [Sentiment Analysis Platform](#5-sentiment-analysis-platform)

---

## Mini-Projects per Concept

### Prerequisites

All mini-projects share this base Maven configuration:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.learning</groupId>
    <artifactId>opennlp-mini-projects</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <properties>
        <java.version>21</java.version>
        <opennlp.version>2.3.0</opennlp.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.apache.opennlp</groupId>
            <artifactId>opennlp</artifactId>
            <version>${opennlp.version}</version>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-simple</artifactId>
            <version>2.0.9</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
    </dependencies>
</project>
```

Download OpenNLP models from: https://opennlp.apache.org/models

---

### 1. Sentence Detection (2 hours)

#### Project: Sentence Boundary Detector

**Objectives:**
- Load and use OpenNLP sentence detection models
- Handle edge cases (abbreviations, quotes, multiple punctuation)
- Build confidence scoring for detected sentences

**Project Structure:**
```
sentence-detector/
├── src/main/java/com/learning/opennlp/SentenceDetectorApp.java
├── src/main/java/com/learning/opennlp/service/SentenceDetectionService.java
└── src/main/resources/models/en-sent.bin
```

**Implementation:**

```java
package com.learning.opennlp.service;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.sentdetect.SentenceDetectorEvaluationMonitor;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.eval.EvaluationMonitor;
import opennlp.tools.sentdetect.SentenceDetectorEvaluator;
import opennlp.tools.sentdetect.SentenceSample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class SentenceDetectionService {
    
    private static final Logger logger = LoggerFactory.getLogger(SentenceDetectionService.class);
    
    private SentenceDetectorME sentenceDetector;
    private SentenceModel model;
    
    public SentenceDetectionService() {
        try {
            loadModel("en-sent.bin");
        } catch (IOException e) {
            logger.error("Failed to load sentence model", e);
        }
    }
    
    public SentenceDetectionService(String modelPath) throws IOException {
        loadModel(modelPath);
    }
    
    private void loadModel(String modelPath) throws IOException {
        try (InputStream modelIn = getClass().getClassLoader().getResourceAsStream("models/" + modelPath)) {
            if (modelIn == null) {
                logger.warn("Model not found: {}", modelPath);
                return;
            }
            model = new SentenceModel(modelIn);
            sentenceDetector = new SentenceDetectorME(model);
            logger.info("Sentence model loaded successfully");
        }
    }
    
    public String[] detect(String text) {
        if (sentenceDetector == null) {
            throw new IllegalStateException("Sentence detector not initialized");
        }
        return sentenceDetector.sentDetect(text);
    }
    
    public List<SentenceWithConfidence> detectWithConfidence(String text) {
        String[] sentences = detect(text);
        double[] probabilities = sentenceDetector.getSentenceProbabilities();
        
        List<SentenceWithConfidence> results = new ArrayList<>();
        for (int i = 0; i < sentences.length; i++) {
            double prob = i < probabilities.length ? probabilities[i] : 0.0;
            results.add(new SentenceWithConfidence(sentences[i].trim(), prob));
        }
        return results;
    }
    
    public String[] detectWithSplit(String text) {
        return sentenceDetector.sentPosDetect(text);
    }
    
    public int countSentences(String text) {
        return detect(text).length;
    }
    
    public Map<String, Object> getStatistics(String text) {
        String[] sentences = detect(text);
        Map<String, Object> stats = new HashMap<>();
        stats.put("sentenceCount", sentences.length);
        stats.put("totalCharacters", text.length());
        stats.put("avgSentenceLength", text.length() / (double) Math.max(sentences.length, 1));
        return stats;
    }
    
    public double evaluate(ObjectStream<SentenceSample> samples) throws IOException {
        SentenceDetectorEvaluator evaluator = new SentenceDetectorEvaluator(
            new SentenceDetectorEvaluationMonitor() {});
        evaluator.evaluate(samples, sentenceDetector);
        return evaluator.getFMeasure();
    }
    
    public static class SentenceWithConfidence {
        private final String sentence;
        private final double confidence;
        
        public SentenceWithConfidence(String sentence, double confidence) {
            this.sentence = sentence;
            this.confidence = confidence;
        }
        
        public String getSentence() { return sentence; }
        public double getConfidence() { return confidence; }
        public boolean isHighConfidence() { return confidence > 0.8; }
    }
}
```

```java
package com.learning.opennlp;

import com.learning.opennlp.service.SentenceDetectionService;
import java.util.List;

public class SentenceDetectorApp {
    
    public static void main(String[] args) {
        SentenceDetectionService service = new SentenceDetectionService();
        
        String text = "Dr. Smith works at Google. He lives in Mountain View. " +
                     "She said: \"Hello, world!\" This is a test. Is it working?";
        
        System.out.println("=== Basic Detection ===");
        String[] sentences = service.detect(text);
        for (int i = 0; i < sentences.length; i++) {
            System.out.println((i + 1) + ": " + sentences[i]);
        }
        
        System.out.println("\n=== Detection with Confidence ===");
        List<SentenceDetectionService.SentenceWithConfidence> results = 
            service.detectWithConfidence(text);
        for (var result : results) {
            System.out.printf("[%.2f] %s%n", result.getConfidence(), result.getSentence());
        }
        
        System.out.println("\n=== Statistics ===");
        System.out.println(service.getStatistics(text));
    }
}
```

**Test Cases to Implement:**
1. Handle abbreviations (Dr., Mr., etc.)
2. Handle multiple punctuation (!!!, ???)
3. Handle quoted sentences
4. Handle single sentence documents
5. Handle empty input

---

### 2. Tokenization (2 hours)

#### Project: Advanced Tokenizer

**Objectives:**
- Implement whitespace and custom tokenization
- Handle contractions, hyphenated words, URLs
- Build tokenizer with position tracking

**Project Structure:**
```
tokenizer/
├── src/main/java/com/learning/opennlp/TokenizerApp.java
├── src/main/java/com/learning/opennlp/service/TokenizationService.java
└── src/main/resources/models/en-token.bin
```

**Implementation:**

```java
package com.learning.opennlp.service;

import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenSample;
import opennlp.tools.tokenize.TokenizerFactory;
import opennlp.tools.tokenize.TokenizerEvaluationMonitor;
import opennlp.tools.util.Sequence;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.utilmext;

import java.io.*;
import java.util.*;

public class TokenizationService {
    
    private Tokenizer tokenizer;
    private TokenizerME tokenizerMe;
    
    public TokenizationService() {
        try {
            loadModel("en-token.bin");
        } catch (IOException e) {
            logger.error("Failed to load tokenizer model", e);
            tokenizer = new SimpleWhitespaceTokenizer();
        }
    }
    
    public TokenizationService(String modelPath) throws IOException {
        loadModel(modelPath);
    }
    
    private void loadModel(String modelPath) throws IOException {
        try (InputStream modelIn = getClass().getClassLoader()
                .getResourceAsStream("models/" + modelPath)) {
            if (modelIn != null) {
                TokenizerModel model = new TokenizerModel(modelIn);
                tokenizerMe = new TokenizerME(model);
                tokenizer = tokenizerMe;
            }
        }
    }
    
    public String[] tokenize(String text) {
        return tokenizer.tokenize(text);
    }
    
    public List<TokenWithPosition> tokenizeWithPositions(String text) {
        String[] tokens = tokenizer.tokenize(text);
        int[] starts = tokenizerMe.getTokenStartPositions();
        int[] ends = tokenizerMe.getTokenEndPositions();
        
        List<TokenWithPosition> results = new ArrayList<>();
        for (int i = 0; i < tokens.length; i++) {
            results.add(new TokenWithPosition(
                tokens[i],
                starts[i],
                ends[i]
            ));
        }
        return results;
    }
    
    public double[] getTokenProbabilities() {
        return tokenizerMe.getTokenProbs();
    }
    
    public String[][] tokenizeSentences(String[] sentences) {
        String[][] result = new String[sentences.length][];
        for (int i = 0; i < sentences.length; i++) {
            result[i] = tokenize(sentences[i]);
        }
        return result;
    }
    
    public Map<String, Integer> getTokenFrequency(String text) {
        String[] tokens = tokenize(text);
        Map<String, Integer> freq = new HashMap<>();
        for (String token : tokens) {
            freq.merge(token.toLowerCase(), 1, Integer::sum);
        }
        return freq;
    }
    
    public static class TokenWithPosition {
        private final String token;
        private final int start;
        private final int end;
        
        public TokenWithPosition(String token, int start, int end) {
            this.token = token;
            this.start = start;
            this.end = end;
        }
        
        public String getToken() { return token; }
        public int getStart() { return start; }
        public int getEnd() { return end; }
        public int length() { return end - start; }
    }
    
    static class SimpleWhitespaceTokenizer implements Tokenizer {
        @Override
        public String[] tokenize(String text) {
            return text.split("\\s+");
        }
        
        @Override
        public String[] tokenizePos(String text) {
            return tokenize(text);
        }
        
        @Override
        public Sequence bestSequence(String text, double[] dArr) {
            return null;
        }
        
        @Override
        public Sequence[] bestSequences(String text, double d) {
            return new Sequence[0];
        }
    }
    
    private static final org.slf4j.Logger logger = 
        org.slf4j.LoggerFactory.getLogger(TokenizationService.class);
}
```

**Test Cases:**
1. "Don't worry" -> ["Don", "'t", "worry"]
2. "https://example.com" as single token
3. "well-known" hyphen handling
4. "100,000" number handling

---

### 3. POS Tagging (2 hours)

#### Project: Part-of-Speech Tagger

**Objectives:**
- Load and use POS tagging models
- Extract grammatical components
- Build tag probability analysis

**Project Structure:**
```
pos-tagger/
├── src/main/java/com/learning/opennlp/PosTaggerApp.java
├── src/main/java/com/learning/opennlp/service/PosTaggingService.java
└── src/main/resources/models/en-pos-maxent.bin
```

**Implementation:**

```java
package com.learning.opennlp.service;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.postag.POSDictionary;
import opennlp.tools.util.Sequence;
import opennlp.tools.util.InvalidFormatException;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class PosTaggingService {
    
    private POSTaggerME tagger;
    private POSModel model;
    
    private static final Map<String, String> TAG_DESCRIPTIONS = Map.of(
        "NN", "Noun (singular)",
        "NNS", "Noun (plural)",
        "NNP", "Proper noun (singular)",
        "NNPS", "Proper noun (plural)",
        "VB", "Verb (base form)",
        "VBD", "Verb (past tense)",
        "VBG", "Verb (gerund/present participle)",
        "VBN", "Verb (past participle)",
        "VBP", "Verb (non-3rd person singular present)",
        "VBZ", "Verb (3rd person singular present)",
        "JJ", "Adjective",
        "JJR", "Adjective (comparative)",
        "JJS", "Adjective (superlative)",
        "RB", "Adverb",
        "RBR", "Adverb (comparative)",
        "RBS", "Adverb (superlative)",
        "DT", "Determiner",
        "IN", "Preposition/Subordinating conjunction",
        "TO", "to",
        "PRP", "Personal pronoun",
        "PRP$", "Possessive pronoun",
        "CC", "Coordinating conjunction",
        "CD", "Cardinal number",
        "MD", "Modal",
        "WP", "Wh-pronoun",
        "WRB", "Wh-adverb",
        "UH", "Interjection",
        "PUNCT", "Punctuation"
    );
    
    public PosTaggingService() {
        try {
            loadModel("en-pos-maxent.bin");
        } catch (IOException e) {
            logger.error("Failed to load POS model", e);
        }
    }
    
    private void loadModel(String modelPath) throws IOException {
        try (InputStream modelIn = getClass().getClassLoader()
                .getResourceAsStream("models/" + modelPath)) {
            if (modelIn != null) {
                model = new POSModel(modelIn);
                tagger = new POSTaggerME(model);
            }
        }
    }
    
    public String[] tag(String[] tokens) {
        if (tagger == null) {
            throw new IllegalStateException("POS tagger not initialized");
        }
        return tagger.tag(tokens);
    }
    
    public List<PosTagResult> tagWithProbability(String[] tokens) {
        String[] tags = tag(tokens);
        double[] probs = tagger.getProbs();
        
        List<PosTagResult> results = new ArrayList<>();
        for (int i = 0; i < tokens.length; i++) {
            results.add(new PosTagResult(
                tokens[i],
                tags[i],
                probs[i]
            ));
        }
        return results;
    }
    
    public Map<String, List<String>> groupByTag(String[] tokens) {
        String[] tags = tag(tokens);
        Map<String, List<String>> grouped = new LinkedHashMap<>();
        
        for (int i = 0; i < tokens.length; i++) {
            grouped.computeIfAbsent(tags[i], k -> new ArrayList<>()).add(tokens[i]);
        }
        return grouped;
    }
    
    public List<String> extractNouns(String[] tokens) {
        String[] tags = tag(tokens);
        List<String> nouns = new ArrayList<>();
        
        for (int i = 0; i < tokens.length; i++) {
            if (tags[i].startsWith("NN")) {
                nouns.add(tokens[i]);
            }
        }
        return nouns;
    }
    
    public List<String> extractVerbs(String[] tokens) {
        String[] tags = tag(tokens);
        List<String> verbs = new ArrayList<>();
        
        for (int i = 0; i < tokens.length; i++) {
            if (tags[i].startsWith("VB")) {
                verbs.add(tokens[i]);
            }
        }
        return verbs;
    }
    
    public List<String> extractAdjectives(String[] tokens) {
        String[] tags = tag(tokens);
        List<String> adjectives = new ArrayList<>();
        
        for (int i = 0; i < tokens.length; i++) {
            if (tags[i].startsWith("JJ")) {
                adjectives.add(tokens[i]);
            }
        }
        return adjectives;
    }
    
    public Map<String, Object> getSyntaxTree(String[] tokens, String[] tags) {
        Map<String, Object> tree = new HashMap<>();
        tree.put("nounPhrases", extractNounPhrases(tokens, tags));
        tree.put("verbPhrases", extractVerbPhrases(tokens, tags));
        return tree;
    }
    
    private List<String[]> extractNounPhrases(String[] tokens, String[] tags) {
        List<String[]> phrases = new ArrayList<>();
        List<String> currentPhrase = new ArrayList<>();
        
        for (int i = 0; i < tags.length; i++) {
            if (tags[i].startsWith("NN") || tags[i].equals("JJ") || tags[i].equals("DT")) {
                currentPhrase.add(tokens[i]);
            } else if (!currentPhrase.isEmpty()) {
                phrases.add(currentPhrase.toArray(new String[0]));
                currentPhrase.clear();
            }
        }
        if (!currentPhrase.isEmpty()) {
            phrases.add(currentPhrase.toArray(new String[0]));
        }
        return phrases;
    }
    
    private List<String[]> extractVerbPhrases(String[] tokens, String[] tags) {
        List<String[]> phrases = new ArrayList<>();
        List<String> currentPhrase = new ArrayList<>();
        
        for (int i = 0; i < tags.length; i++) {
            if (tags[i].startsWith("VB")) {
                currentPhrase.add(tokens[i]);
            } else if (!currentPhrase.isEmpty()) {
                phrases.add(currentPhrase.toArray(new String[0]));
                currentPhrase.clear();
            }
        }
        if (!currentPhrase.isEmpty()) {
            phrases.add(currentPhrase.toArray(new String[0]));
        }
        return phrases;
    }
    
    public String getTagDescription(String tag) {
        return TAG_DESCRIPTIONS.getOrDefault(tag, "Unknown");
    }
    
    public static class PosTagResult {
        private final String token;
        private final String tag;
        private final double probability;
        
        public PosTagResult(String token, String tag, double probability) {
            this.token = token;
            this.tag = tag;
            this.probability = probability;
        }
        
        public String getToken() { return token; }
        public String getTag() { return tag; }
        public double getProbability() { return probability; }
    }
    
    private static final org.slf4j.Logger logger = 
        org.slf4j.LoggerFactory.getLogger(PosTaggingService.class);
}
```

---

### 4. Chunking/Parsing (2 hours)

#### Project: Shallow Parser / Chunker

**Objectives:**
- Implement noun phrase chunking
- Build verb phrase extraction
- Create chunk-based text analysis

**Project Structure:**
```
chunker/
├── src/main/java/com/learning/opennlp/ChunkerApp.java
├── src/main/java/com/learning/opennlp/service/ChunkingService.java
└── src/main/resources/models/en-chunker.bin
```

**Implementation:**

```java
package com.learning.opennlp.service;

import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkSample;
import opennlp.tools.util.Sequence;

import java.io.*;
import java.util.*;

public class ChunkingService {
    
    private ChunkerME chunker;
    private ChunkerModel model;
    
    public ChunkingService() {
        try {
            loadModel("en-chunker.bin");
        } catch (IOException e) {
            logger.error("Failed to load chunker model", e);
        }
    }
    
    private void loadModel(String modelPath) throws IOException {
        try (InputStream modelIn = getClass().getClassLoader()
                .getResourceAsStream("models/" + modelPath)) {
            if (modelIn != null) {
                model = new ChunkerModel(modelIn);
                chunker = new ChunkerME(model);
            }
        }
    }
    
    public String[] chunk(String[] tokens, String[] tags) {
        if (chunker == null) {
            throw new IllegalStateException("Chunker not initialized");
        }
        return chunker.chunk(tokens, tags);
    }
    
    public List<Chunk> chunkWithSpans(String[] tokens, String[] tags) {
        String[] chunkTags = chunk(tokens, tags);
        Span[] spans = chunker.chunkAsSpans(tokens, tags);
        
        List<Chunk> chunks = new ArrayList<>();
        for (Span span : spans) {
            String[] chunkTokens = Arrays.copyOfRange(tokens, span.getStart(), span.getEnd());
            chunks.add(new Chunk(
                span.getType(),
                String.join(" ", chunkTokens),
                span.getStart(),
                span.getEnd()
            ));
        }
        return chunks;
    }
    
    public double[] getChunkProbabilities(String[] tokens, String[] tags) {
        return chunker.getProbs();
    }
    
    public List<NounPhrase> extractNounPhrases(String[] tokens, String[] tags) {
        String[] chunkTags = chunk(tokens, tags);
        List<NounPhrase> phrases = new ArrayList<>();
        
        int start = -1;
        String type = null;
        
        for (int i = 0; i < chunkTags.length; i++) {
            if (chunkTags[i].startsWith("B-NP")) {
                if (start >= 0) {
                    phrases.add(new NounPhrase(
                        Arrays.copyOfRange(tokens, start, i),
                        i - start
                    ));
                }
                start = i;
                type = "NP";
            } else if (chunkTags[i].startsWith("B-VP")) {
                if (start >= 0) {
                    phrases.add(new NounPhrase(
                        Arrays.copyOfRange(tokens, start, i),
                        i - start
                    ));
                }
                start = i;
                type = "VP";
            } else if (chunkTags[i].equals("I-NP") && start < 0) {
                start = i;
                type = "NP";
            } else if (chunkTags[i].equals("I-VP") && start < 0) {
                start = i;
                type = "VP";
            }
        }
        
        if (start >= 0) {
            phrases.add(new NounPhrase(
                Arrays.copyOfRange(tokens, start, tokens.length),
                tokens.length - start
            ));
        }
        
        return phrases;
    }
    
    public List<VerbPhrase> extractVerbPhrases(String[] tokens, String[] tags) {
        String[] chunkTags = chunk(tokens, tags);
        List<VerbPhrase> phrases = new ArrayList<>();
        
        List<String> currentPhrase = new ArrayList<>();
        
        for (int i = 0; i < chunkTags.length; i++) {
            if (chunkTags[i].startsWith("B-VP") || chunkTags[i].equals("I-VP")) {
                currentPhrase.add(tokens[i]);
            } else if (!currentPhrase.isEmpty()) {
                phrases.add(new VerbPhrase(
                    currentPhrase.toArray(new String[0]),
                    currentPhrase.size()
                ));
                currentPhrase.clear();
            }
        }
        
        if (!currentPhrase.isEmpty()) {
            phrases.add(new VerbPhrase(
                currentPhrase.toArray(new String[0]),
                currentPhrase.size()
            ));
        }
        
        return phrases;
    }
    
    public Map<String, Object> analyzeChunks(String[] tokens, String[] tags) {
        Map<String, Object> analysis = new HashMap<>();
        String[] chunkTags = chunk(tokens, tags);
        
        Map<String, Integer> chunkCounts = new HashMap<>();
        for (String tag : chunkTags) {
            if (!tag.equals("O")) {
                String type = tag.startsWith("B-") ? tag.substring(2) : tag.substring(2);
                chunkCounts.merge(type, 1, Integer::sum);
            }
        }
        
        analysis.put("chunkCounts", chunkCounts);
        analysis.put("nounPhrases", extractNounPhrases(tokens, tags));
        analysis.put("verbPhrases", extractVerbPhrases(tokens, tags));
        
        return analysis;
    }
    
    public static class Chunk {
        private final String type;
        private final String text;
        private final int start;
        private final int end;
        
        public Chunk(String type, String text, int start, int end) {
            this.type = type;
            this.text = text;
            this.start = start;
            this.end = end;
        }
        
        public String getType() { return type; }
        public String getText() { return text; }
        public int getStart() { return start; }
        public int getEnd() { return end; }
    }
    
    public static class NounPhrase {
        private final String[] tokens;
        private final int length;
        
        public NounPhrase(String[] tokens, int length) {
            this.tokens = tokens;
            this.length = length;
        }
        
        public String[] getTokens() { return tokens; }
        public String getText() { return String.join(" ", tokens); }
        public int getLength() { return length; }
    }
    
    public static class VerbPhrase {
        private final String[] tokens;
        private final int length;
        
        public VerbPhrase(String[] tokens, int length) {
            this.tokens = tokens;
            this.length = length;
        }
        
        public String[] getTokens() { return tokens; }
        public String getText() { return String.join(" ", tokens); }
        public int getLength() { return length; }
    }
    
    private static final org.slf4j.Logger logger = 
        org.slf4j.LoggerFactory.getLogger(ChunkingService.class);
}
```

---

### 5. Named Entity Recognition (2 hours)

#### Project: Entity Extractor

**Objectives:**
- Build multi-type NER system
- Implement custom entity patterns
- Create entity linking capability

**Project Structure:**
```
ner/
├── src/main/java/com/learning/opennlp/NerApp.java
├── src/main/java/com/learning/opennlp/service/NerService.java
└── src/main/resources/models/en-ner-*.bin
```

**Implementation:**

```java
package com.learning.opennlp.service;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.namefind.NameFinderEvaluationMonitor;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.util.Span;
import opennlp.tools.util.InvalidFormatException;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class NerService {
    
    private Map<String, NameFinderME> finders;
    private Map<String, TokenNameFinderModel> models;
    
    private static final Map<String, String> ENTITY_LABELS = Map.of(
        "person", "PERSON",
        "location", "LOCATION",
        "organization", "ORGANIZATION",
        "date", "DATE",
        "time", "TIME",
        "money", "MONEY",
        "percentage", "PERCENTAGE"
    );
    
    public NerService() {
        finders = new HashMap<>();
        models = new HashMap<>();
        loadDefaultModels();
    }
    
    private void loadDefaultModels() {
        String[] modelFiles = {
            "en-ner-person.bin",
            "en-ner-location.bin",
            "en-ner-organization.bin",
            "en-ner-date.bin",
            "en-ner-time.bin",
            "en-ner-money.bin"
        };
        
        String[] types = {
            "person", "location", "organization", "date", "time", "money"
        };
        
        for (int i = 0; i < modelFiles.length; i++) {
            try {
                loadModel(types[i], modelFiles[i]);
            } catch (Exception e) {
                logger.debug("Could not load model: {}", modelFiles[i]);
            }
        }
    }
    
    public void loadModel(String type, String modelPath) throws IOException {
        try (InputStream modelIn = getClass().getClassLoader()
                .getResourceAsStream("models/" + modelPath)) {
            if (modelIn != null) {
                TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
                models.put(type, model);
                finders.put(type, new NameFinderME(model));
            }
        }
    }
    
    public List<Entity> find(String[] tokens) {
        List<Entity> entities = new ArrayList<>();
        
        for (Map.Entry<String, NameFinderME> entry : finders.entrySet()) {
            Span[] spans = entry.getValue().find(tokens);
            
            for (Span span : spans) {
                String entityText = Span.getTokens(tokens, span)[0];
                for (int i = 1; i < span.length(); i++) {
                    entityText += " " + tokens[span.getStart() + i];
                }
                
                entities.add(new Entity(
                    entityText.trim(),
                    entry.getKey(),
                    span.getStart(),
                    span.getEnd(),
                    span.getProb()
                ));
            }
        }
        
        return mergeOverlappingEntities(entities);
    }
    
    public List<Entity> findByType(String[] tokens, String entityType) {
        NameFinderME finder = finders.get(entityType);
        if (finder == null) {
            return Collections.emptyList();
        }
        
        Span[] spans = finder.find(tokens);
        List<Entity> entities = new ArrayList<>();
        
        for (Span span : spans) {
            String entityText = getSpanText(tokens, span);
            entities.add(new Entity(
                entityText,
                entityType,
                span.getStart(),
                span.getEnd(),
                span.getProb()
            ));
        }
        
        return entities;
    }
    
    private String getSpanText(String[] tokens, Span span) {
        StringBuilder sb = new StringBuilder();
        for (int i = span.getStart(); i < span.getEnd(); i++) {
            if (i > span.getStart()) sb.append(" ");
            sb.append(tokens[i]);
        }
        return sb.toString();
    }
    
    private List<Entity> mergeOverlappingEntities(List<Entity> entities) {
        if (entities.isEmpty()) return entities;
        
        List<Entity> sorted = new ArrayList<>(entities);
        sorted.sort(Comparator.comparingInt(Entity::getStart));
        
        List<Entity> merged = new ArrayList<>();
        Entity current = sorted.get(0);
        
        for (int i = 1; i < sorted.size(); i++) {
            Entity next = sorted.get(i);
            
            if (next.getStart() < current.getEnd()) {
                if (next.getProbability() > current.getProbability()) {
                    current = next;
                }
            } else {
                merged.add(current);
                current = next;
            }
        }
        merged.add(current);
        
        return merged;
    }
    
    public Map<String, List<Entity>> groupByType(String[] tokens) {
        List<Entity> entities = find(tokens);
        Map<String, List<Entity>> grouped = new LinkedHashMap<>();
        
        for (Entity entity : entities) {
            grouped.computeIfAbsent(entity.getType(), k -> new ArrayList<>()).add(entity);
        }
        
        return grouped;
    }
    
    public Map<String, Object> analyzeEntities(String[] tokens) {
        Map<String, Object> analysis = new HashMap<>();
        
        List<Entity> entities = find(tokens);
        analysis.put("totalEntities", entities.size());
        analysis.put("entitiesByType", groupByType(tokens));
        analysis.put("uniqueEntities", getUniqueEntities(entities));
        
        return analysis;
    }
    
    private Map<String, String> getUniqueEntities(List<Entity> entities) {
        Map<String, String> unique = new LinkedHashMap<>();
        
        for (Entity entity : entities) {
            String key = entity.getText().toLowerCase();
            if (!unique.containsKey(key)) {
                unique.put(key, entity.getType());
            }
        }
        
        return unique;
    }
    
    public static class Entity {
        private final String text;
        private final String type;
        private final int start;
        private final int end;
        private final double probability;
        
        public Entity(String text, String type, int start, int end, double probability) {
            this.text = text;
            this.type = type;
            this.start = start;
            this.end = end;
            this.probability = probability;
        }
        
        public String getText() { return text; }
        public String getType() { return type; }
        public int getStart() { return start; }
        public int getEnd() { return end; }
        public double getProbability() { return probability; }
        public boolean isHighConfidence() { return probability > 0.8; }
    }
    
    private static final org.slf4j.Logger logger = 
        org.slf4j.LoggerFactory.getLogger(NerService.class);
}
```

---

### 6. Lemmatization (2 hours)

#### Project: Word Form Normalizer

**Objectives:**
- Implement lemmatization using OpenNLP
- Build morphological analysis
- Create lemmatization-based search

**Project Structure:**
```
lemmatizer/
├── src/main/java/com/learning/opennlp/LemmatizerApp.java
├── src/main/java/com/learning/opennlp/service/LemmatizationService.java
└── src/main/resources/models/en-lemmatizer.bin
```

**Implementation:**

```java
package com.learning.opennlp.service;

import opennlp.tools.lemmatizer.LemmatizerModel;
import opennlp.tools.lemmatizer.LemmatizerME;
import opennlp.tools.lemmatizer.LemmatizerEvaluationMonitor;
import opennlp.tools.lemmatizer.LemmaSample;
import opennlp.tools.util.Sequence;

import java.io.*;
import java.util.*;

public class LemmatizationService {
    
    private LemmatizerME lemmatizer;
    private LemmatizerModel model;
    
    public LemmatizationService() {
        try {
            loadModel("en-lemmatizer.bin");
        } catch (IOException e) {
            logger.error("Failed to load lemmatizer model", e);
        }
    }
    
    private void loadModel(String modelPath) throws IOException {
        try (InputStream modelIn = getClass().getClassLoader()
                .getResourceAsStream("models/" + modelPath)) {
            if (modelIn != null) {
                model = new LemmatizerModel(modelIn);
                lemmatizer = new LemmatizerME(model);
            }
        }
    }
    
    public String[] lemmatize(String[] tokens, String[] posTags) {
        if (lemmatizer == null) {
            return fallbackLemmatize(tokens);
        }
        return lemmatizer.lemmatize(tokens, posTags);
    }
    
    public List<LemmaResult> lemmatizeWithPos(String[] tokens, String[] posTags) {
        String[] lemmas = lemmatize(tokens, posTags);
        double[] probs = lemmatizer.getProbs();
        
        List<LemmaResult> results = new ArrayList<>();
        for (int i = 0; i < tokens.length; i++) {
            results.add(new LemmaResult(
                tokens[i],
                lemmas[i],
                posTags[i],
                probs[i]
            ));
        }
        return results;
    }
    
    private String[] fallbackLemmatize(String[] tokens) {
        String[] lemmas = new String[tokens.length];
        
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i].toLowerCase();
            String lemma = DICTIONARY.getOrDefault(token, token);
            lemmas[i] = lemma;
        }
        
        return lemmas;
    }
    
    public Map<String, String> getLemmas(String[] tokens, String[] posTags) {
        String[] lemmas = lemmatize(tokens, posTags);
        Map<String, String> lemmaMap = new LinkedHashMap<>();
        
        for (int i = 0; i < tokens.length; i++) {
            lemmaMap.put(tokens[i], lemmas[i]);
        }
        
        return lemmaMap;
    }
    
    public List<String> getUniqueLemmas(String[] tokens, String[] posTags) {
        String[] lemmas = lemmatize(tokens, posTags);
        return Arrays.asList(lemmas).stream()
            .distinct()
            .toList();
    }
    
    public Map<String, Object> analyzeLemmas(String[] tokens, String[] posTags) {
        Map<String, Object> analysis = new HashMap<>();
        
        String[] lemmas = lemmatize(tokens, posTags);
        
        Map<String, Long> lemmaFreq = new HashMap<>();
        for (String lemma : lemmas) {
            lemmaFreq.merge(lemma, 1L, Long::sum);
        }
        
        analysis.put("lemmaCount", lemmas.length);
        analysis.put("uniqueLemmas", lemmaFreq.size());
        analysis.put("lemmaDistribution", lemmaFreq);
        analysis.put("lemmas", getLemmas(tokens, posTags));
        
        return analysis;
    }
    
    public String getBaseForm(String word, String posTag) {
        String[] tokens = {word};
        String[] tags = {posTag};
        String[] lemmas = lemmatize(tokens, tags);
        return lemmas[0];
    }
    
    private static final Map<String, String> DICTIONARY = Map.of(
        "running", "run",
        "ran", "run",
        "dogs", "dog",
        "cats", "cat",
        "better", "good",
        "best", "good",
        "wrote", "write",
        "written", "write",
        "eating", "eat",
        "eaten", "eat",
        "went", "go",
        "gone", "go",
        "saw", "see",
        "seen", "see"
    );
    
    public static class LemmaResult {
        private final String token;
        private final String lemma;
        private final String posTag;
        private final double probability;
        
        public LemmaResult(String token, String lemma, String posTag, double probability) {
            this.token = token;
            this.lemma = lemma;
            this.posTag = posTag;
            this.probability = probability;
        }
        
        public String getToken() { return token; }
        public String getLemma() { return lemma; }
        public String getPosTag() { return posTag; }
        public double getProbability() { return probability; }
        public boolean isChanged() { return !token.equalsIgnoreCase(lemma); }
    }
    
    private static final org.slf4j.Logger logger = 
        org.slf4j.LoggerFactory.getLogger(LemmatizationService.class);
}
```

---

### 7. Coreference Resolution (2 hours)

#### Project: Pronoun Resolver

**Objectives:**
- Implement basic coreference detection
- Build entity tracking across sentences
- Create mention clustering

**Implementation:**

```java
package com.learning.opennlp.service;

import java.util.*;

public class CoreferenceService {
    
    private Map<String, List<CoreferenceMention>> entityMentions;
    private List<CoreferenceChain> chains;
    
    public CoreferenceService() {
        this.entityMentions = new HashMap<>();
        this.chains = new ArrayList<>();
    }
    
    public List<CoreferenceChain> resolve(List<String> sentences, 
            List<List<NerService.Entity>> entitiesPerSentence) {
        
        chains.clear();
        
        for (int i = 0; i < sentences.size(); i++) {
            List<NerService.Entity> entities = entitiesPerSentence.get(i);
            
            for (NerService.Entity entity : entities) {
                String canonicalForm = entity.getText().toLowerCase();
                
                entityMentions.computeIfAbsent(canonicalForm, k -> new ArrayList<>())
                    .add(new CoreferenceMention(
                        entity.getText(),
                        entity.getType(),
                        i,
                        entity.getStart(),
                        entity.getEnd()
                    ));
            }
        }
        
        for (String key : entityMentions.keySet()) {
            List<CoreferenceMention> mentions = entityMentions.get(key);
            if (mentions.size() > 1) {
                chains.add(new CoreferenceChain(key, mentions));
            }
        }
        
        return chains;
    }
    
    public String resolvePronoun(String pronoun, int currentSentence, 
            List<CoreferenceChain> chains) {
        
        String pronounLower = pronoun.toLowerCase();
        String type = getPronounType(pronounLower);
        
        for (CoreferenceChain chain : chains) {
            for (CoreferenceMention mention : chain.getMentions()) {
                if (mention.getSentenceIndex() < currentSentence) {
                    if (matchesType(chain.getRepresentative().getType(), type)) {
                        return chain.getRepresentative().getText();
                    }
                }
            }
        }
        
        return pronoun;
    }
    
    private String getPronounType(String pronoun) {
        if (Set.of("he", "him", "his", "himself").contains(pronoun)) return "person";
        if (Set.of("she", "her", "hers", "herself").contains(pronoun)) return "person";
        if (Set.of("it", "its", "itself").contains(pronoun)) return "object";
        if (Set.of("they", "them", "their", "themselves").contains(pronoun)) return "multiple";
        return "unknown";
    }
    
    private boolean matchesType(String entityType, String pronounType) {
        if (pronounType.equals("object")) {
            return entityType.equals("organization") || entityType.equals("location");
        }
        return entityType.equals("person");
    }
    
    public static class CoreferenceChain {
        private final String canonicalForm;
        private final List<CoreferenceMention> mentions;
        
        public CoreferenceChain(String canonicalForm, List<CoreferenceMention> mentions) {
            this.canonicalForm = canonicalForm;
            this.mentions = mentions;
        }
        
        public String getCanonicalForm() { return canonicalForm; }
        public List<CoreferenceMention> getMentions() { return mentions; }
        public CoreferenceMention getRepresentative() { return mentions.get(0); }
    }
    
    public static class CoreferenceMention {
        private final String text;
        private final String type;
        private final int sentenceIndex;
        private final int start;
        private final int end;
        
        public CoreferenceMention(String text, String type, int sentenceIndex, 
                int start, int end) {
            this.text = text;
            this.type = type;
            this.sentenceIndex = sentenceIndex;
            this.start = start;
            this.end = end;
        }
        
        public String getText() { return text; }
        public String getType() { return type; }
        public int getSentenceIndex() { return sentenceIndex; }
        public int getStart() { return start; }
        public int getEnd() { return end; }
    }
}
```

---

### 8. Binary Spinning (Model Training) (2 hours)

#### Project: Model Training Pipeline

**Objectives:**
- Train sentence detection model from data
- Train tokenization model
- Evaluate trained models

**Implementation:**

```java
package com.learning.opennlp.service;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.sentdetect.SentenceDetectorTrainer;
import opennlp.tools.sentdetect.SentenceDetectorTrainingParameters;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.tokenize.TokenizerTrainer;
import opennlp.tools.tokenize.TokenizerTrainingParameters;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.TrainingParameters;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ModelTrainingService {
    
    public void trainSentenceDetector(String trainingFile, String modelFile) 
            throws IOException {
        
        InputStreamFactory inputStreamFactory = new MarkableFileInputStreamFactory(
            new File(trainingFile));
        
        ObjectStream<String> lineStream = new PlainTextByLineStream(inputStreamFactory, 
            StandardCharsets.UTF_8);
        
        ObjectStream<SentenceSample> sampleStream = new SentenceSampleStream(lineStream);
        
        TrainingParameters params = new TrainingParameters();
        params.put("Algorithm", "MAXENT");
        params.put("Iterations", "100");
        params.put("Cutoff", "5");
        
        SentenceDetectorTrainer trainer = new SentenceDetectorTrainer();
        SentenceModel model = trainer.train(sampleStream);
        
        try (OutputStream out = new FileOutputStream(modelFile)) {
            model.serialize(out);
        }
        
        logger.info("Sentence detector model trained and saved to: {}", modelFile);
    }
    
    public void trainTokenizer(String trainingFile, String modelFile) throws IOException {
        
        InputStreamFactory inputStreamFactory = new MarkableFileInputStreamFactory(
            new File(trainingFile));
        
        ObjectStream<String> lineStream = new PlainTextByLineStream(inputStreamFactory,
            StandardCharsets.UTF_8);
        
        ObjectStream<TokenSample> sampleStream = new TokenSampleStream(lineStream);
        
        TrainingParameters params = new TrainingParameters();
        params.put("Algorithm", "PERCEPTRON");
        params.put("Iterations", "100");
        
        TokenizerTrainer trainer = new TokenizerTrainer();
        TokenizerModel model = trainer.train(sampleStream);
        
        try (OutputStream out = new FileOutputStream(modelFile)) {
            model.serialize(out);
        }
        
        logger.info("Tokenizer model trained and saved to: {}", modelFile);
    }
    
    public void evaluateModel(String modelFile, String testFile) throws IOException {
        try (InputStream modelIn = new FileInputStream(modelFile)) {
            SentenceModel model = new SentenceModel(modelIn);
            SentenceDetectorME detector = new SentenceDetectorME(model);
            
            try (BufferedReader reader = new BufferedReader(new FileReader(testFile))) {
                String line;
                int totalSentences = 0;
                int correctDetections = 0;
                
                while ((line = reader.readLine()) != null) {
                    String[] sentences = detector.sentDetect(line);
                    totalSentences += sentences.length;
                }
                
                logger.info("Evaluated {} sentences", totalSentences);
            }
        }
    }
    
    static class SentenceSampleStream implements ObjectStream<SentenceSample> {
        private final ObjectStream<String> lineStream;
        
        public SentenceSampleStream(ObjectStream<String> lineStream) {
            this.lineStream = lineStream;
        }
        
        @Override
        public SentenceSample read() throws IOException {
            String line = lineStream.read();
            if (line == null) return null;
            
            String[] parts = line.split("\t");
            if (parts.length >= 2) {
                return new SentenceSample(parts[0], parts[1].split(" "));
            }
            return new SentenceSample(line, new String[0]);
        }
        
        @Override
        public void reset() throws IOException { lineStream.reset(); }
        @Override
        public void close() throws IOException { lineStream.close(); }
    }
    
    static class TokenSampleStream implements ObjectStream<TokenSample> {
        private final ObjectStream<String> lineStream;
        
        public TokenSampleStream(ObjectStream<String> lineStream) {
            this.lineStream = lineStream;
        }
        
        @Override
        public TokenSample read() throws IOException {
            String line = lineStream.read();
            if (line == null) return null;
            
            String[] tokens = line.split(" ");
            return new TokenSample(tokens);
        }
        
        @Override
        public void reset() throws IOException { lineStream.reset(); }
        @Override
        public void close() throws IOException { lineStream.close(); }
    }
    
    private static final org.slf4j.Logger logger = 
        org.slf4j.LoggerFactory.getLogger(ModelTrainingService.class);
}
```

---

### 9. Custom Model Training (3 hours)

#### Project: Custom NER Model Trainer

**Objectives:**
- Prepare training data for custom NER
- Train custom entity recognizer
- Test and validate custom model

**Implementation:**

```java
package com.learning.opennlp.service;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.NameModel;
import opennlp.tools.namefind.NameFinderEventReader;
import opennlp.tools.namefind.NameFinderFactory;
import opennlp.tools.namefind.NameFinderTrainingParameters;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.*;

import java.io.*;
import java.util.*;

public class CustomNerTrainingService {
    
    private static final Map<String, String> ENTITY_TYPES = Map.of(
        "SKILL", "skill",
        "CERTIFICATION", "certification",
        "DEGREE", "degree",
        "JOB_TITLE", "job_title",
        "COMPANY", "company"
    );
    
    public void trainCustomNer(String trainingFile, String modelFile) throws IOException {
        
        InputStreamFactory inputStreamFactory = new MarkableFileInputStreamFactory(
            new File(trainingFile));
        
        ObjectStream<NameSample> sampleStream = new NameFinderEventReader(
            inputStreamFactory, StandardCharsets.UTF_8);
        
        TrainingParameters params = new TrainingParameters();
        params.put("Algorithm", "PERCEPTRON");
        params.put("Iterations", "100");
        params.put("Cutoff", "1");
        
        TokenNameFinderModel model = NameFinderME.train(
            "en",
            "custom-ner",
            sampleStream,
            params,
            new NameFinderFactory()
        );
        
        try (OutputStream out = new FileOutputStream(modelFile)) {
            model.serialize(out);
        }
        
        logger.info("Custom NER model trained: {}", modelFile);
    }
    
    public void evaluateCustomNer(String modelFile, String testFile) throws IOException {
        try (InputStream modelIn = new FileInputStream(modelFile)) {
            TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
            NameFinderME finder = new NameFinderME(model);
            
            try (BufferedReader reader = new BufferedReader(new FileReader(testFile))) {
                String line;
                int totalEntities = 0;
                
                while ((line = reader.readLine()) != null) {
                    String[] tokens = line.split(" ");
                    Span[] spans = finder.find(tokens);
                    totalEntities += spans.length;
                }
                
                logger.info("Found {} entities in test data", totalEntities);
            }
            
            finder.clearAdaptiveData();
        }
    }
    
    public String convertToTrainingFormat(String text, List<EntityAnnotation> annotations) {
        StringBuilder sb = new StringBuilder();
        
        String[] tokens = text.split(" ");
        Map<Integer, String> entityMap = new HashMap<>();
        
        for (EntityAnnotation ann : annotations) {
            for (int i = ann.getStart(); i < ann.getEnd(); i++) {
                entityMap.put(i, ann.getType());
            }
        }
        
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i].replaceAll("[^a-zA-Z0-9]", "");
            String entity = entityMap.getOrDefault(i, "O");
            
            if (i > 0) sb.append(" ");
            sb.append(token).append("_").append(entity);
        }
        
        return sb.toString();
    }
    
    public List<String> generateTrainingData(List<Document> documents) {
        List<String> trainingLines = new ArrayList<>();
        
        for (Document doc : documents) {
            String line = convertToTrainingFormat(doc.getText(), doc.getAnnotations());
            trainingLines.add(line);
        }
        
        return trainingLines;
    }
    
    public static class EntityAnnotation {
        private final String type;
        private final int start;
        private final int end;
        
        public EntityAnnotation(String type, int start, int end) {
            this.type = type;
            this.start = start;
            this.end = end;
        }
        
        public String getType() { return type; }
        public int getStart() { return start; }
        public int getEnd() { return end; }
    }
    
    public static class Document {
        private final String text;
        private final List<EntityAnnotation> annotations;
        
        public Document(String text, List<EntityAnnotation> annotations) {
            this.text = text;
            this.annotations = annotations;
        }
        
        public String getText() { return text; }
        public List<EntityAnnotation> getAnnotations() { return annotations; }
    }
    
    private static final org.slf4j.Logger logger = 
        org.slf4j.LoggerFactory.getLogger(CustomNerTrainingService.class);
}
```

---

### 10. Document Categorization (2 hours)

#### Project: Text Classifier

**Objectives:**
- Implement document classification
- Build category training pipeline
- Create multi-class classifier

**Implementation:**

```java
package com.learning.opennlp.service;

import opennlp.tools.doccat.*;
import opennlp.tools.util.*;

import java.io.*;
import java.util.*;

public class DocumentCategorizationService {
    
    private DoccatModel model;
    private DocumentCategorizerME categorizer;
    
    private static final Map<String, String> CATEGORIES = Map.of(
        "TECHNICAL", "technical",
        "BUSINESS", "business",
        "LEGAL", "legal",
        "MEDICAL", "medical",
        "NEWS", "news"
    );
    
    public DocumentCategorizationService() {
        try {
            loadModel("en-doccat.bin");
        } catch (IOException e) {
            logger.error("Could not load doccat model", e);
        }
    }
    
    private void loadModel(String modelPath) throws IOException {
        try (InputStream modelIn = getClass().getClassLoader()
                .getResourceAsStream("models/" + modelPath)) {
            if (modelIn != null) {
                model = new DoccatModel(modelIn);
                categorizer = new DocumentCategorizerME(model);
            }
        }
    }
    
    public void train(String trainingFile, String modelFile) throws IOException {
        
        InputStreamFactory inputStreamFactory = new MarkableFileInputStreamFactory(
            new File(trainingFile));
        
        ObjectStream<DocumentSample> sampleStream = new DocumentSampleStream(
            new PlainTextByLineStream(inputStreamFactory, StandardCharsets.UTF_8));
        
        TrainingParameters params = new TrainingParameters();
        params.put("Algorithm", "MAXENT");
        params.put("Iterations", "100");
        params.put("Cutoff", "1");
        
        DoccatModel model = DocumentCategorizerME.train(
            "en",
            sampleStream,
            params,
            new DoccatFactory()
        );
        
        try (OutputStream out = new FileOutputStream(modelFile)) {
            model.serialize(out);
        }
        
        categorizer = new DocumentCategorizerME(model);
        logger.info("Document categorization model trained");
    }
    
    public String categorize(String text) {
        if (categorizer == null) {
            return "UNKNOWN";
        }
        
        double[] outcomes = categorizer.categorize(text);
        return categorizer.getBestCategory(outcomes);
    }
    
    public Map<String, Double> categorizeWithProbabilities(String text) {
        double[] outcomes = categorizer.categorize(text);
        
        Map<String, Double> results = new LinkedHashMap<>();
        String[] categoryNames = categorizer.getCategoryLabels();
        
        for (int i = 0; i < categoryNames.length; i++) {
            results.put(categoryNames[i], outcomes[i]);
        }
        
        return results;
    }
    
    public List<CategorizedDocument> categorizeBatch(List<String> documents) {
        List<CategorizedDocument> results = new ArrayList<>();
        
        for (String doc : documents) {
            String category = categorize(doc);
            Map<String, Double> probs = categorizeWithProbabilities(doc);
            results.add(new CategorizedDocument(doc, category, probs));
        }
        
        return results;
    }
    
    public Map<String, Integer> getCategoryDistribution(List<String> documents) {
        Map<String, Integer> distribution = new HashMap<>();
        
        for (String doc : documents) {
            String category = categorize(doc);
            distribution.merge(category, 1, Integer::sum);
        }
        
        return distribution;
    }
    
    public static class CategorizedDocument {
        private final String text;
        private final String category;
        private final Map<String, Double> probabilities;
        
        public CategorizedDocument(String text, String category, 
                Map<String, Double> probabilities) {
            this.text = text;
            this.category = category;
            this.probabilities = probabilities;
        }
        
        public String getText() { return text; }
        public String getCategory() { return category; }
        public Map<String, Double> getProbabilities() { return probabilities; }
    }
    
    private static final org.slf4j.Logger logger = 
        org.slf4j.LoggerFactory.getLogger(DocumentCategorizationService.class);
}
```

---

## Real-World Projects

### Maven Dependencies for All Real-World Projects

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.apache.opennlp</groupId>
        <artifactId>opennlp</artifactId>
        <version>2.3.0</version>
    </dependency>
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

---

### 1. Resume Parser System

**Project Structure:**
```
resume-parser/
├── pom.xml
├── src/main/java/com/learning/opennlp/
│   ├── ResumeParserApplication.java
│   ├── config/OpenNLPConfig.java
│   ├── service/
│   │   ├── ResumeParsingService.java
│   │   ├── ContactInfoExtractor.java
│   │   ├── SkillExtractor.java
│   │   └── EducationExtractor.java
│   ├── model/
│   │   ├── ParsedResume.java
│   │   └── ResumeSection.java
│   └── controller/ResumeController.java
```

**Implementation:**

```java
package com.learning.opennlp.service;

import com.learning.opennlp.model.*;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.util.Span;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ResumeParsingService {
    
    private final TokenizerME tokenizer;
    private final POSTaggerME posTagger;
    private final NameFinderME nameFinder;
    private final NameFinderME skillFinder;
    
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("\\+?\\d{1,3}[-.\\s]?\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}");
    private static final Pattern LINKEDIN_PATTERN = 
        Pattern.compile("linkedin\\.com/in/[a-zA-Z0-9-]+");
    
    private static final Set<String> SKILL_KEYWORDS = Set.of(
        "java", "python", "javascript", "spring", "hibernate", "react", "angular",
        "docker", "kubernetes", "aws", "azure", "gcp", "sql", "nosql", "git",
        "agile", "scrum", "ci/cd", "machine learning", "data science"
    );
    
    private static final Set<String> DEGREE_KEYWORDS = Set.of(
        "bachelor", "master", "phd", "mba", "bs", "ms", "ba", "ma"
    );
    
    public ResumeParsingService(TokenizerME tokenizer, POSTaggerME posTagger,
            NameFinderME nameFinder, NameFinderME skillFinder) {
        this.tokenizer = tokenizer;
        this.posTagger = posTagger;
        this.nameFinder = nameFinder;
        this.skillFinder = skillFinder;
    }
    
    public ParsedResume parseResume(String resumeText) {
        ParsedResume parsed = new ParsedResume();
        
        parsed.setContactInfo(extractContactInfo(resumeText));
        parsed.setName(extractName(resumeText));
        parsed.setSkills(extractSkills(resumeText));
        parsed.setEducation(extractEducation(resumeText));
        parsed.setExperience(extractExperience(resumeText));
        
        return parsed;
    }
    
    private ContactInfo extractContactInfo(String text) {
        ContactInfo info = new ContactInfo();
        
        java.util.regex.Matcher emailMatcher = EMAIL_PATTERN.matcher(text);
        if (emailMatcher.find()) {
            info.setEmail(emailMatcher.group());
        }
        
        java.util.regex.Matcher phoneMatcher = PHONE_PATTERN.matcher(text);
        if (phoneMatcher.find()) {
            info.setPhone(phoneMatcher.group());
        }
        
        java.util.regex.Matcher linkedinMatcher = LINKEDIN_PATTERN.matcher(text);
        if (linkedinMatcher.find()) {
            info.setLinkedIn(linkedinMatcher.group());
        }
        
        return info;
    }
    
    private String extractName(String text) {
        String[] tokens = tokenizer.tokenize(text);
        Span[] spans = nameFinder.find(tokens);
        
        if (spans.length > 0) {
            return Span.getTokens(tokens, spans[0])[0];
        }
        
        return extractNameFromHeader(text);
    }
    
    private String extractNameFromHeader(String text) {
        String[] lines = text.split("\n");
        if (lines.length > 0) {
            String firstLine = lines[0].trim();
            if (firstLine.length() < 50 && !firstLine.contains("@")) {
                return firstLine;
            }
        }
        return null;
    }
    
    private List<String> extractSkills(String text) {
        String[] tokens = tokenizer.tokenize(text);
        String[] tags = posTagger.tag(tokens);
        
        List<String> skills = new ArrayList<>();
        
        String lowerText = text.toLowerCase();
        for (String skill : SKILL_KEYWORDS) {
            if (lowerText.contains(skill)) {
                skills.add(skill);
            }
        }
        
        Span[] spans = skillFinder.find(tokens);
        for (Span span : spans) {
            String skillText = Span.getTokens(tokens, span)[0];
            if (!skills.contains(skillText.toLowerCase())) {
                skills.add(skillText);
            }
        }
        
        return skills.stream().distinct().collect(Collectors.toList());
    }
    
    private List<Education> extractEducation(String text) {
        List<Education> educationList = new ArrayList<>();
        String[] lines = text.split("\n");
        
        boolean inEducationSection = false;
        
        for (String line : lines) {
            String lowerLine = line.toLowerCase();
            
            if (lowerLine.contains("education")) {
                inEducationSection = true;
                continue;
            }
            
            if (inEducationSection) {
                if (lowerLine.contains("experience") || lowerLine.contains("skills")) {
                    inEducationSection = false;
                    continue;
                }
                
                for (String degree : DEGREE_KEYWORDS) {
                    if (lowerLine.contains(degree)) {
                        Education edu = new Education();
                        edu.setDegree(line.trim());
                        educationList.add(edu);
                        break;
                    }
                }
            }
        }
        
        return educationList;
    }
    
    private List<Experience> extractExperience(String text) {
        List<Experience> experiences = new ArrayList<>();
        String[] lines = text.split("\n");
        
        boolean inExperienceSection = false;
        StringBuilder currentExp = new StringBuilder();
        
        for (String line : lines) {
            String lowerLine = line.toLowerCase();
            
            if (lowerLine.contains("experience") || lowerLine.contains("work history")) {
                inExperienceSection = true;
                continue;
            }
            
            if (inExperienceSection) {
                if (lowerLine.contains("education") || lowerLine.contains("skills")) {
                    inExperienceSection = false;
                    
                    if (currentExp.length() > 0) {
                        experiences.add(createExperience(currentExp.toString()));
                        currentExp.setLength(0);
                    }
                    continue;
                }
                
                currentExp.append(line).append("\n");
            }
        }
        
        if (currentExp.length() > 0) {
            experiences.add(createExperience(currentExp.toString()));
        }
        
        return experiences;
    }
    
    private Experience createExperience(String text) {
        Experience exp = new Experience();
        exp.setDescription(text.trim());
        
        String[] tokens = tokenizer.tokenize(text);
        String[] tags = posTagger.tag(tokens);
        
        List<String> companies = extractCompanies(tokens, tags);
        if (!companies.isEmpty()) {
            exp.setCompany(companies.get(0));
        }
        
        return exp;
    }
    
    private List<String> extractCompanies(String[] tokens, String[] tags) {
        List<String> companies = new ArrayList<>();
        
        for (int i = 0; i < tags.length; i++) {
            if (tags[i].equals("NNP") && i > 0 && tags[i-1].equals("NNP")) {
                String company = tokens[i-1] + " " + tokens[i];
                companies.add(company);
            }
        }
        
        return companies;
    }
}
```

```java
package com.learning.opennlp.model;

import java.util.List;

public class ParsedResume {
    private String name;
    private ContactInfo contactInfo;
    private List<String> skills;
    private List<Education> education;
    private List<Experience> experience;
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public ContactInfo getContactInfo() { return contactInfo; }
    public void setContactInfo(ContactInfo contactInfo) { this.contactInfo = contactInfo; }
    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }
    public List<Education> getEducation() { return education; }
    public void setEducation(List<Education> education) { this.education = education; }
    public List<Experience> getExperience() { return experience; }
    public void setExperience(List<Experience> experience) { this.experience = experience; }
}

class ContactInfo {
    private String email;
    private String phone;
    private String linkedIn;
    private String location;
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getLinkedIn() { return linkedIn; }
    public void setLinkedIn(String linkedIn) { this.linkedIn = linkedIn; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}

class Education {
    private String degree;
    private String institution;
    private String year;
    private String gpa;
    
    public String getDegree() { return degree; }
    public void setDegree(String degree) { this.degree = degree; }
    public String getInstitution() { return institution; }
    public void setInstitution(String institution) { this.institution = institution; }
    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }
    public String getGpa() { return gpa; }
    public void setGpa(String gpa) { this.gpa = gpa; }
}

class Experience {
    private String company;
    private String role;
    private String description;
    private String duration;
    
    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
}
```

**REST Controller:**

```java
package com.learning.opennlp.controller;

import com.learning.opennlp.model.ParsedResume;
import com.learning.opennlp.service.ResumeParsingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/resume")
public class ResumeController {
    
    private final ResumeParsingService resumeParsingService;
    
    public ResumeController(ResumeParsingService resumeParsingService) {
        this.resumeParsingService = resumeParsingService;
    }
    
    @PostMapping("/parse")
    public ResponseEntity<ParsedResume> parseResume(@RequestBody String resumeText) {
        ParsedResume parsed = resumeParsingService.parseResume(resumeText);
        return ResponseEntity.ok(parsed);
    }
}
```

---

### 2. News Article Classifier

**Project Structure:**
```
news-classifier/
├── pom.xml
├── src/main/java/com/learning/opennlp/
│   ├── NewsClassifierApplication.java
│   ├── service/
│   │   ├── NewsClassificationService.java
│   │   └── NewsAnalyticsService.java
│   ├── model/
│   │   ├── NewsArticle.java
│   │   └── ClassificationResult.java
│   └── controller/NewsController.java
```

**Implementation:**

```java
package com.learning.opennlp.service;

import com.learning.opennlp.model.*;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DoccatModel;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class NewsClassificationService {
    
    private DocumentCategorizerME categorizer;
    private Map<String, List<String>> topicKeywords;
    
    private static final Map<String, List<String>> CATEGORY_KEYWORDS = Map.of(
        "SPORTS", List.of("game", "team", "player", "score", "match", "win", "lose", "championship"),
        "POLITICS", List.of("government", "election", "vote", "president", "congress", "policy"),
        "TECHNOLOGY", List.of("tech", "software", "ai", "startup", "innovation", "digital"),
        "BUSINESS", List.of("market", "stock", "company", "economy", "investment", "finance"),
        "ENTERTAINMENT", List.of("movie", "music", "celebrity", "film", "actor", "award"),
        "HEALTH", List.of("medical", "health", "doctor", "disease", "treatment", "patient"),
        "SCIENCE", List.of("research", "study", "scientist", "discovery", "space", "environment")
    );
    
    public NewsClassificationService(DoccatModel model) {
        this.categorizer = new DocumentCategorizerME(model);
        this.topicKeywords = CATEGORY_KEYWORDS;
    }
    
    public ClassificationResult classify(NewsArticle article) {
        ClassificationResult result = new ClassificationResult();
        
        String combinedText = article.getTitle() + " " + article.getContent();
        
        double[] outcomes = categorizer.categorize(combinedText);
        String category = categorizer.getBestCategory(outcomes);
        
        result.setPrimaryCategory(category);
        result.setConfidence(outcomes[categorizer.getIndex(category)]);
        
        List<CategoryScore> allScores = getAllCategoryScores(outcomes);
        result.setAllScores(allScores);
        
        result.setTopics(extractTopics(combinedText));
        result.setKeywords(extractKeywords(article.getContent()));
        result.setSentiment(analyzeSentiment(article.getContent()));
        
        return result;
    }
    
    private List<CategoryScore> getAllCategoryScores(double[] outcomes) {
        List<CategoryScore> scores = new ArrayList<>();
        String[] categories = categorizer.getCategoryLabels();
        
        for (int i = 0; i < categories.length; i++) {
            scores.add(new CategoryScore(categories[i], outcomes[i]));
        }
        
        scores.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
        return scores;
    }
    
    private List<String> extractTopics(String text) {
        String lowerText = text.toLowerCase();
        List<String> topics = new ArrayList<>();
        
        for (Map.Entry<String, List<String>> entry : topicKeywords.entrySet()) {
            for (String keyword : entry.getValue()) {
                if (lowerText.contains(keyword)) {
                    topics.add(entry.getKey());
                    break;
                }
            }
        }
        
        return topics.stream().distinct().collect(Collectors.toList());
    }
    
    private List<String> extractKeywords(String content) {
        String[] tokens = content.split("\\s+");
        
        Map<String, Integer> frequency = new HashMap<>();
        for (String token : tokens) {
            String clean = token.replaceAll("[^a-zA-Z]", "").toLowerCase();
            if (clean.length() > 4) {
                frequency.merge(clean, 1, Integer::sum);
            }
        }
        
        return frequency.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(10)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }
    
    private String analyzeSentiment(String content) {
        String[] positiveWords = {"success", "growth", "improve", "achieve", "positive", "strong"};
        String[] negativeWords = {"crisis", "fail", "decline", "loss", "concern", "weak"};
        
        String lowerContent = content.toLowerCase();
        
        int positiveCount = 0;
        int negativeCount = 0;
        
        for (String word : positiveWords) {
            if (lowerContent.contains(word)) positiveCount++;
        }
        
        for (String word : negativeWords) {
            if (lowerContent.contains(word)) negativeCount++;
        }
        
        if (positiveCount > negativeCount) return "POSITIVE";
        if (negativeCount > positiveCount) return "NEGATIVE";
        return "NEUTRAL";
    }
    
    public Map<String, List<NewsArticle>> classifyBatch(List<NewsArticle> articles) {
        Map<String, List<NewsArticle>> classified = new HashMap<>();
        
        for (NewsArticle article : articles) {
            ClassificationResult result = classify(article);
            String category = result.getPrimaryCategory();
            
            classified.computeIfAbsent(category, k -> new ArrayList<>()).add(article);
        }
        
        return classified;
    }
    
    public static class CategoryScore {
        private final String category;
        private final double score;
        
        public CategoryScore(String category, double score) {
            this.category = category;
            this.score = score;
        }
        
        public String getCategory() { return category; }
        public double getScore() { return score; }
    }
}
```

```java
package com.learning.opennlp.model;

import java.util.List;

public class NewsArticle {
    private String id;
    private String title;
    private String content;
    private String author;
    private String publishedDate;
    private String source;
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getPublishedDate() { return publishedDate; }
    public void setPublishedDate(String publishedDate) { this.publishedDate = publishedDate; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}

class ClassificationResult {
    private String primaryCategory;
    private double confidence;
    private List<NewsClassificationService.CategoryScore> allScores;
    private List<String> topics;
    private List<String> keywords;
    private String sentiment;
    
    public String getPrimaryCategory() { return primaryCategory; }
    public void setPrimaryCategory(String primaryCategory) { this.primaryCategory = primaryCategory; }
    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }
    public List<NewsClassificationService.CategoryScore> getAllScores() { return allScores; }
    public void setAllScores(List<NewsClassificationService.CategoryScore> allScores) { this.allScores = allScores; }
    public List<String> getTopics() { return topics; }
    public void setTopics(List<String> topics) { this.topics = topics; }
    public List<String> getKeywords() { return keywords; }
    public void setKeywords(List<String> keywords) { this.keywords = keywords; }
    public String getSentiment() { return sentiment; }
    public void setSentiment(String sentiment) { this.sentiment = sentiment; }
}
```

---

### 3. Medical Record NLP Pipeline

**Project Structure:**
```
medical-nlp/
├── pom.xml
├── src/main/java/com/learning/opennlp/
│   ├── MedicalNlpApplication.java
│   ├── service/
│   │   ├── MedicalEntityExtractor.java
│   │   ├── DiagnosisExtractor.java
│   │   └── MedicationExtractor.java
│   ├── model/
│   │   ├── MedicalRecord.java
│   │   └── ExtractionResult.java
```

**Implementation:**

```java
package com.learning.opennlp.service;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.util.Span;

import java.util.*;
import java.util.regex.Pattern;

public class MedicalEntityExtractor {
    
    private final TokenizerME tokenizer;
    private final POSTaggerME posTagger;
    private final NameFinderME diagnosisFinder;
    private final NameFinderME medicationFinder;
    
    private static final Pattern ICD_CODE_PATTERN = 
        Pattern.compile("[A-Z]\\d{2,3}\\.?\\d{0,4}");
    private static final Pattern DATE_PATTERN = 
        Pattern.compile("\\d{1,2}[/-]\\d{1,2}[/-]\\d{2,4}");
    private static final Pattern DOSAGE_PATTERN = 
        Pattern.compile("\\d+\\s*(mg|ml|g|mcg|units?|drops?|tablets?|capsules?)", 
            Pattern.CASE_INSENSITIVE);
    
    private static final Set<String> DIAGNOSIS_INDICATORS = Set.of(
        "diagnosed", "diagnosis", "suspected", "present", "history of", "complaining"
    );
    
    private static final Set<String> MEDICATION_INDICATORS = Set.of(
        "prescribed", "medication", "taking", "currently on", "treatment with"
    );
    
    public MedicalEntityExtractor(TokenizerME tokenizer, POSTaggerME posTagger,
            NameFinderME diagnosisFinder, NameFinderME medicationFinder) {
        this.tokenizer = tokenizer;
        this.posTagger = posTagger;
        this.diagnosisFinder = diagnosisFinder;
        this.medicationFinder = medicationFinder;
    }
    
    public ExtractionResult extract(String medicalText) {
        ExtractionResult result = new ExtractionResult();
        
        result.setDiagnoses(extractDiagnoses(medicalText));
        result.setMedications(extractMedications(medicalText));
        result.setProcedures(extractProcedures(medicalText));
        result.setIcdCodes(extractIcdCodes(medicalText));
        result.setDates(extractDates(medicalText));
        result.setVitals(extractVitals(medicalText));
        
        return result;
    }
    
    private List<Diagnosis> extractDiagnoses(String text) {
        List<Diagnosis> diagnoses = new ArrayList<>();
        
        String[] tokens = tokenizer.tokenize(text);
        Span[] spans = diagnosisFinder.find(tokens);
        
        for (Span span : spans) {
            String diagnosisText = getSpanText(tokens, span);
            
            String status = "CONFIRMED";
            String lowerText = text.toLowerCase();
            
            for (String indicator : DIAGNOSIS_INDICATORS) {
                if (lowerText.contains(indicator)) {
                    status = indicator.equals("suspected") ? "SUSPECTED" : "CONFIRMED";
                    break;
                }
            }
            
            diagnoses.add(new Diagnosis(diagnosisText.trim(), status));
        }
        
        return diagnoses;
    }
    
    private List<Medication> extractMedications(String text) {
        List<Medication> medications = new ArrayList<>();
        String[] lines = text.split("\n");
        
        for (String line : lines) {
            for (String indicator : MEDICATION_INDICATORS) {
                if (line.toLowerCase().contains(indicator)) {
                    String[] tokens = tokenizer.tokenize(line);
                    
                    java.util.regex.Matcher dosageMatcher = DOSAGE_PATTERN.matcher(line);
                    String dosage = dosageMatcher.find() ? dosageMatcher.group() : null;
                    
                    medications.add(new Medication(
                        line.trim(),
                        dosage,
                        extractFrequency(line)
                    ));
                    break;
                }
            }
        }
        
        return medications;
    }
    
    private String extractFrequency(String text) {
        String[] freqIndicators = {"once", "twice", "daily", "twice daily", "weekly", "as needed"};
        
        for (String freq : freqIndicators) {
            if (text.toLowerCase().contains(freq)) {
                return freq;
            }
        }
        
        return null;
    }
    
    private List<Procedure> extractProcedures(String text) {
        List<Procedure> procedures = new ArrayList<>();
        
        String[] lines = text.split("\n");
        
        for (String line : lines) {
            String lowerLine = line.toLowerCase();
            
            if (lowerLine.contains("performed") || lowerLine.contains("underwent")) {
                procedures.add(new Procedure(line.trim()));
            }
        }
        
        return procedures;
    }
    
    private List<String> extractIcdCodes(String text) {
        List<String> codes = new ArrayList<>();
        
        java.util.regex.Matcher matcher = ICD_CODE_PATTERN.matcher(text);
        while (matcher.find()) {
            codes.add(matcher.group());
        }
        
        return codes;
    }
    
    private List<String> extractDates(String text) {
        List<String> dates = new ArrayList<>();
        
        java.util.regex.Matcher matcher = DATE_PATTERN.matcher(text);
        while (matcher.find()) {
            dates.add(matcher.group());
        }
        
        return dates;
    }
    
    private Map<String, String> extractVitals(String text) {
        Map<String, String> vitals = new HashMap<>();
        
        Map<String, Pattern> vitalPatterns = Map.of(
            "BP", Pattern.compile("(\\d{2,3}/\\d{2,3})\\s*mmHg", Pattern.CASE_INSENSITIVE),
            "HR", Pattern.compile("(\\d{2,3})\\s*(bpm|beats)", Pattern.CASE_INSENSITIVE),
            "TEMP", Pattern.compile("(\\d{2,3}\\.?\\d?)\\s*°?[Ff]", Pattern.CASE_INSENSITIVE),
            "RR", Pattern.compile("(\\d{2})\\s*breaths", Pattern.CASE_INSENSITIVE),
            "O2", Pattern.compile("(\\d{2,3})\\s*%\\s*(sat|SpO2)", Pattern.CASE_INSENSITIVE)
        );
        
        for (Map.Entry<String, Pattern> entry : vitalPatterns.entrySet()) {
            java.util.regex.Matcher matcher = entry.getValue().matcher(text);
            if (matcher.find()) {
                vitals.put(entry.getKey(), matcher.group(1));
            }
        }
        
        return vitals;
    }
    
    private String getSpanText(String[] tokens, Span span) {
        StringBuilder sb = new StringBuilder();
        for (int i = span.getStart(); i < span.getEnd(); i++) {
            if (i > span.getStart()) sb.append(" ");
            sb.append(tokens[i]);
        }
        return sb.toString();
    }
    
    public static class Diagnosis {
        private final String description;
        private final String status;
        
        public Diagnosis(String description, String status) {
            this.description = description;
            this.status = status;
        }
        
        public String getDescription() { return description; }
        public String getStatus() { return status; }
    }
    
    public static class Medication {
        private final String name;
        private final String dosage;
        private final String frequency;
        
        public Medication(String name, String dosage, String frequency) {
            this.name = name;
            this.dosage = dosage;
            this.frequency = frequency;
        }
        
        public String getName() { return name; }
        public String getDosage() { return dosage; }
        public String getFrequency() { return frequency; }
    }
    
    public static class Procedure {
        private final String description;
        
        public Procedure(String description) {
            this.description = description;
        }
        
        public String getDescription() { return description; }
    }
}

class ExtractionResult {
    private List<MedicalEntityExtractor.Diagnosis> diagnoses;
    private List<MedicalEntityExtractor.Medication> medications;
    private List<MedicalEntityExtractor.Procedure> procedures;
    private List<String> icdCodes;
    private List<String> dates;
    private Map<String, String> vitals;
    
    public List<MedicalEntityExtractor.Diagnosis> getDiagnoses() { return diagnoses; }
    public void setDiagnoses(List<MedicalEntityExtractor.Diagnosis> diagnoses) { this.diagnoses = diagnoses; }
    public List<MedicalEntityExtractor.Medication> getMedications() { return medications; }
    public void setMedications(List<MedicalEntityExtractor.Medication> medications) { this.medications = medications; }
    public List<MedicalEntityExtractor.Procedure> getProcedures() { return procedures; }
    public void setProcedures(List<MedicalEntityExtractor.Procedure> procedures) { this.procedures = procedures; }
    public List<String> getIcdCodes() { return icdCodes; }
    public void setIcdCodes(List<String> icdCodes) { this.icdCodes = icdCodes; }
    public List<String> getDates() { return dates; }
    public void setDates(List<String> dates) { this.dates = dates; }
    public Map<String, String> getVitals() { return vitals; }
    public void setVitals(Map<String, String> vitals) { this.vitals = vitals; }
}
```

---

### 4. Legal Document Analyzer

**Project Structure:**
```
legal-analyzer/
├── pom.xml
├── src/main/java/com/learning/opennlp/
│   ├── LegalAnalyzerApplication.java
│   ├── service/
│   │   ├── LegalClauseExtractor.java
│   │   ├── ObligationDetector.java
│   │   └── ContractRiskAnalyzer.java
```

**Implementation:**

```java
package com.learning.opennlp.service;

import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.chunker.ChunkerME;

import java.util.*;
import java.util.regex.Pattern;

public class LegalClauseExtractor {
    
    private final TokenizerME tokenizer;
    private final POSTaggerME posTagger;
    private final ChunkerME chunker;
    
    private static final Map<String, String> CLAUSE_TYPES = Map.of(
        "confidentiality", "CONFIDENTIALITY",
        "indemnification", "INDEMNIFICATION",
        "liability", "LIABILITY",
        "termination", "TERMINATION",
        "payment", "PAYMENT",
        "warranty", "WARRANTY",
        "intellectual property", "IP",
        "dispute resolution", "DISPUTE",
        "force majeure", "FORCE_MAJEURE",
        "governing law", "GOVERNING_LAW"
    );
    
    private static final Set<String> OBLIGATION_VERBS = Set.of(
        "shall", "will", "must", "agrees to", "is required", "is obligated"
    );
    
    private static final Set<String> CONDITIONAL_WORDS = Set.of(
        "if", "unless", "provided that", "subject to", "in the event"
    );
    
    public LegalClauseExtractor(TokenizerME tokenizer, POSTaggerME posTagger,
            ChunkerME chunker) {
        this.tokenizer = tokenizer;
        this.posTagger = posTagger;
        this.chunker = chunker;
    }
    
    public List<LegalClause> extractClauses(String legalDocument) {
        List<LegalClause> clauses = new ArrayList<>();
        String[] sections = splitIntoSections(legalDocument);
        
        for (String section : sections) {
            String type = identifyClauseType(section);
            
            if (type != null) {
                List<String> obligations = extractObligations(section);
                List<String> conditions = extractConditions(section);
                String effectiveDate = extractEffectiveDate(section);
                
                clauses.add(new LegalClause(
                    section.trim(),
                    type,
                    obligations,
                    conditions,
                    effectiveDate
                ));
            }
        }
        
        return clauses;
    }
    
    private String[] splitIntoSections(String document) {
        return document.split("(?i)(?<=section\\s+\\d+|article\\s+\\d+)\\s*");
    }
    
    private String identifyClauseType(String section) {
        String lowerSection = section.toLowerCase();
        
        for (Map.Entry<String, String> entry : CLAUSE_TYPES.entrySet()) {
            if (lowerSection.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        
        return null;
    }
    
    private List<String> extractObligations(String text) {
        List<String> obligations = new ArrayList<>();
        
        for (String verb : OBLIGATION_VERBS) {
            int index = text.toLowerCase().indexOf(verb);
            if (index >= 0) {
                int start = Math.max(0, index - 10);
                int end = Math.min(text.length(), index + 100);
                obligations.add(text.substring(start, end).trim());
            }
        }
        
        return obligations;
    }
    
    private List<String> extractConditions(String text) {
        List<String> conditions = new ArrayList<>();
        
        for (String word : CONDITIONAL_WORDS) {
            int index = text.toLowerCase().indexOf(word);
            if (index >= 0) {
                int start = index;
                int end = Math.min(text.length(), index + word.length() + 100);
                conditions.add(text.substring(start, end).trim());
            }
        }
        
        return conditions;
    }
    
    private String extractEffectiveDate(String text) {
        Pattern datePattern = Pattern.compile(
            "(effective|from|as of)\\s+(\\d{1,2}\\s+\\w+\\s+\\d{4})",
            Pattern.CASE_INSENSITIVE
        );
        
        java.util.regex.Matcher matcher = datePattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(2);
        }
        
        return null;
    }
    
    public Map<String, Integer> analyzeClauseDistribution(List<LegalClause> clauses) {
        Map<String, Integer> distribution = new HashMap<>();
        
        for (LegalClause clause : clauses) {
            distribution.merge(clause.getType(), 1, Integer::sum);
        }
        
        return distribution;
    }
    
    public static class LegalClause {
        private final String text;
        private final String type;
        private final List<String> obligations;
        private final List<String> conditions;
        private final String effectiveDate;
        
        public LegalClause(String text, String type, List<String> obligations,
                List<String> conditions, String effectiveDate) {
            this.text = text;
            this.type = type;
            this.obligations = obligations;
            this.conditions = conditions;
            this.effectiveDate = effectiveDate;
        }
        
        public String getText() { return text; }
        public String getType() { return type; }
        public List<String> getObligations() { return obligations; }
        public List<String> getConditions() { return conditions; }
        public String getEffectiveDate() { return effectiveDate; }
    }
}
```

```java
package com.learning.opennlp.service;

import java.util.*;

public class ContractRiskAnalyzer {
    
    private static final Map<String, RiskLevel> RISK_INDICATORS = Map.of(
        "unlimited liability", RiskLevel.HIGH,
        "sole discretion", RiskLevel.HIGH,
        "indemnify", RiskLevel.HIGH,
        "waive", RiskLevel.HIGH,
        "notwithstanding", RiskLevel.MEDIUM,
        "subject to", RiskLevel.MEDIUM,
        "may terminate", RiskLevel.LOW,
        "shall pay", RiskLevel.MEDIUM
    );
    
    private static final Set<String> UNFAIR_TERMS = Set.of(
        "waive all rights",
        "unlimited liability",
        "sole discretion",
        "without limitation"
    );
    
    public RiskAnalysis analyzeRisk(String contractText) {
        RiskAnalysis analysis = new RiskAnalysis();
        
        List<RiskIndicator> risks = identifyRisks(contractText);
        analysis.setRisks(risks);
        
        RiskLevel overall = calculateOverallRisk(risks);
        analysis.setOverallRisk(overall);
        
        analysis.setHighRiskCount((int) risks.stream()
            .filter(r -> r.getLevel() == RiskLevel.HIGH).count());
        
        analysis.setRecommendations(generateRecommendations(risks));
        
        return analysis;
    }
    
    private List<RiskIndicator> identifyRisks(String text) {
        List<RiskIndicator> risks = new ArrayList<>();
        
        for (Map.Entry<String, RiskLevel> entry : RISK_INDICATORS.entrySet()) {
            if (text.toLowerCase().contains(entry.getKey())) {
                risks.add(new RiskIndicator(entry.getKey(), entry.getLevel()));
            }
        }
        
        for (String term : UNFAIR_TERMS) {
            if (text.toLowerCase().contains(term)) {
                risks.add(new RiskIndicator(term + " - review carefully", RiskLevel.HIGH));
            }
        }
        
        return risks;
    }
    
    private RiskLevel calculateOverallRisk(List<RiskIndicator> risks) {
        long highCount = risks.stream().filter(r -> r.getLevel() == RiskLevel.HIGH).count();
        long mediumCount = risks.stream().filter(r -> r.getLevel() == RiskLevel.MEDIUM).count();
        
        if (highCount > 2) return RiskLevel.HIGH;
        if (highCount > 0 && mediumCount > 3) return RiskLevel.HIGH;
        if (mediumCount > 2) return RiskLevel.MEDIUM;
        
        return RiskLevel.LOW;
    }
    
    private List<String> generateRecommendations(List<RiskIndicator> risks) {
        List<String> recommendations = new ArrayList<>();
        
        long highRisk = risks.stream().filter(r -> r.getLevel() == RiskLevel.HIGH).count();
        
        if (highRisk > 0) {
            recommendations.add("Review high-risk clauses with legal counsel");
        }
        
        if (risks.stream().anyMatch(r -> r.getText().contains("indemnify"))) {
            recommendations.add("Consider adding liability caps");
        }
        
        if (risks.stream().anyMatch(r -> r.getText().contains("termination"))) {
            recommendations.add("Ensure termination rights are balanced");
        }
        
        return recommendations;
    }
    
    public enum RiskLevel { LOW, MEDIUM, HIGH }
    
    public static class RiskIndicator {
        private final String text;
        private final RiskLevel level;
        
        public RiskIndicator(String text, RiskLevel level) {
            this.text = text;
            this.level = level;
        }
        
        public String getText() { return text; }
        public RiskLevel getLevel() { return level; }
    }
    
    public static class RiskAnalysis {
        private List<RiskIndicator> risks;
        private RiskLevel overallRisk;
        private int highRiskCount;
        private List<String> recommendations;
        
        public List<RiskIndicator> getRisks() { return risks; }
        public void setRisks(List<RiskIndicator> risks) { this.risks = risks; }
        public RiskLevel getOverallRisk() { return overallRisk; }
        public void setOverallRisk(RiskLevel overallRisk) { this.overallRisk = overallRisk; }
        public int getHighRiskCount() { return highRiskCount; }
        public void setHighRiskCount(int highRiskCount) { this.highRiskCount = highRiskCount; }
        public List<String> getRecommendations() { return recommendations; }
        public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }
    }
}
```

---

### 5. Sentiment Analysis Platform

**Project Structure:**
```
sentiment-platform/
├── pom.xml
├── src/main/java/com/learning/opennlp/
│   ├── SentimentApplication.java
│   ├── service/
│   │   ├── SentimentAnalyzer.java
│   │   ├── AspectSentimentAnalyzer.java
│   │   └── SentimentAggregator.java
```

**Implementation:**

```java
package com.learning.opennlp.service;

import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;

import java.util.*;
import java.util.regex.Pattern;

public class SentimentAnalyzer {
    
    private final TokenizerME tokenizer;
    private final POSTaggerME posTagger;
    private final SentenceDetectorME sentenceDetector;
    
    private static final Map<String, Double> POSITIVE_WORDS = Map.of(
        "excellent", 0.9, "great", 0.8, "good", 0.6, "amazing", 0.9,
        "wonderful", 0.8, "fantastic", 0.9, "love", 0.8, "happy", 0.6,
        "satisfied", 0.6, "recommend", 0.7, "best", 0.8, "perfect", 0.9,
        "awesome", 0.8, "delighted", 0.8, "impressed", 0.7
    );
    
    private static final Map<String, Double> NEGATIVE_WORDS = Map.of(
        "terrible", -0.9, "horrible", -0.9, "bad", -0.6, "awful", -0.9,
        "poor", -0.6, "disappointed", -0.7, "hate", -0.8, "worst", -0.9,
        "frustrating", -0.6, "useless", -0.8, "broken", -0.7, "waste", -0.8,
        "disgusting", -0.9, "pathetic", -0.8, "unacceptable", -0.7
    );
    
    private static final Map<String, Double> INTENSIFIERS = Map.of(
        "very", 1.5, "really", 1.4, "extremely", 1.6, "absolutely", 1.8,
        "highly", 1.4, "incredibly", 1.7, "quite", 1.2, "pretty", 1.2
    );
    
    private static final Map<String, Double> NEGATORS = Map.of(
        "not", -1.0, "never", -1.0, "no", -1.0, "don't", -1.0,
        "didn't", -1.0, "won't", -1.0, "can't", -1.0, "without", -0.8
    );
    
    public SentimentAnalyzer(TokenizerME tokenizer, POSTaggerME posTagger,
            SentenceDetectorME sentenceDetector) {
        this.tokenizer = tokenizer;
        this.posTagger = posTagger;
        this.sentenceDetector = sentenceDetector;
    }
    
    public SentimentResult analyze(String text) {
        SentimentResult result = new SentimentResult();
        
        String[] sentences = sentenceDetector.sentDetect(text);
        List<SentenceSentiment> sentenceSentiments = new ArrayList<>();
        
        double totalScore = 0.0;
        int sentenceCount = 0;
        
        for (String sentence : sentences) {
            double score = calculateSentenceSentiment(sentence);
            sentenceSentiments.add(new SentenceSentiment(sentence, score));
            totalScore += score;
            sentenceCount++;
        }
        
        result.setSentences(sentenceSentiments);
        result.setOverallScore(totalScore / Math.max(sentenceCount, 1));
        result.setSentimentLabel(determineLabel(result.getOverallScore()));
        result.setConfidence(calculateConfidence(sentenceSentiments));
        
        return result;
    }
    
    private double calculateSentenceSentiment(String sentence) {
        String[] tokens = tokenizer.tokenize(sentence);
        String[] tags = posTagger.tag(tokens);
        
        double score = 0.0;
        double multiplier = 1.0;
        
        for (int i = 0; i < tokens.length; i++) {
            String tokenLower = tokens[i].toLowerCase();
            
            if (INTENSIFIERS.containsKey(tokenLower)) {
                multiplier = INTENSIFIERS.get(tokenLower);
                continue;
            }
            
            if (POSITIVE_WORDS.containsKey(tokenLower)) {
                score += POSITIVE_WORDS.get(tokenLower) * multiplier;
            }
            
            if (NEGATIVE_WORDS.containsKey(tokenLower)) {
                score += NEGATIVE_WORDS.get(tokenLower) * multiplier;
            }
            
            if (NEGATORS.containsKey(tokenLower)) {
                multiplier = NEGATORS.get(tokenLower);
            }
            
            if (tags[i].equals("JJ") || tags[i].equals("JJR") || tags[i].equals("JJS")) {
                if (!POSITIVE_WORDS.containsKey(tokenLower) && 
                    !NEGATIVE_WORDS.containsKey(tokenLower)) {
                    score += scoreAdjective(tokenLower);
                }
            }
            
            if (!tags[i].startsWith("JJ")) {
                multiplier = 1.0;
            }
        }
        
        return score;
    }
    
    private double scoreAdjective(String adjective) {
        if (POSITIVE_WORDS.containsKey(adjective)) {
            return POSITIVE_WORDS.get(adjective);
        }
        if (NEGATIVE_WORDS.containsKey(adjective)) {
            return NEGATIVE_WORDS.get(adjective);
        }
        return 0.0;
    }
    
    private String determineLabel(double score) {
        if (score >= 0.3) return "POSITIVE";
        if (score <= -0.3) return "NEGATIVE";
        return "NEUTRAL";
    }
    
    private double calculateConfidence(List<SentenceSentiment> sentiments) {
        if (sentiments.isEmpty()) return 0.0;
        
        long positiveCount = sentiments.stream()
            .filter(s -> s.getScore() > 0.1).count();
        long negativeCount = sentiments.stream()
            .filter(s -> s.getScore() < -0.1).count();
        
        long max = Math.max(positiveCount, negativeCount);
        return (double) max / sentiments.size();
    }
    
    public static class SentimentResult {
        private List<SentenceSentiment> sentences;
        private double overallScore;
        private String sentimentLabel;
        private double confidence;
        
        public List<SentenceSentiment> getSentences() { return sentences; }
        public void setSentences(List<SentenceSentiment> sentences) { this.sentences = sentences; }
        public double getOverallScore() { return overallScore; }
        public void setOverallScore(double overallScore) { this.overallScore = overallScore; }
        public String getSentimentLabel() { return sentimentLabel; }
        public void setSentimentLabel(String sentimentLabel) { this.sentimentLabel = sentimentLabel; }
        public double getConfidence() { return confidence; }
        public void setConfidence(double confidence) { this.confidence = confidence; }
    }
    
    public static class SentenceSentiment {
        private final String sentence;
        private final double score;
        
        public SentenceSentiment(String sentence, double score) {
            this.sentence = sentence;
            this.score = score;
        }
        
        public String getSentence() { return sentence; }
        public double getScore() { return score; }
    }
}
```

```java
package com.learning.opennlp.service;

import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.postag.POSTaggerME;

import java.util.*;

public class AspectSentimentAnalyzer {
    
    private final TokenizerME tokenizer;
    private final POSTaggerME posTagger;
    private final SentimentAnalyzer sentimentAnalyzer;
    
    private static final Map<String, List<String>> ASPECT_KEYWORDS = Map.of(
        "PRODUCT", List.of("product", "item", "quality", "design", "features"),
        "SERVICE", List.of("service", "support", "customer", "help", "staff"),
        "PRICE", List.of("price", "cost", "value", "money", "expensive", "cheap"),
        "DELIVERY", List.of("delivery", "shipping", "arrived", "packaging"),
        "UX", List.of("easy", "simple", "intuitive", "fast", "slow", "website", "app")
    );
    
    public AspectSentimentAnalyzer(TokenizerME tokenizer, POSTaggerME posTagger,
            SentimentAnalyzer sentimentAnalyzer) {
        this.tokenizer = tokenizer;
        this.posTagger = posTagger;
        this.sentimentAnalyzer = sentimentAnalyzer;
    }
    
    public Map<String, SentimentAnalyzer.SentimentResult> analyzeAspects(String review) {
        Map<String, SentimentAnalyzer.SentimentResult> aspectResults = new LinkedHashMap<>();
        
        for (Map.Entry<String, List<String>> entry : ASPECT_KEYWORDS.entrySet()) {
            String aspect = entry.getKey();
            List<String> keywords = entry.getValue();
            
            String aspectText = extractAspectText(review, keywords);
            
            if (aspectText != null && !aspectText.isEmpty()) {
                SentimentAnalyzer.SentimentResult sentiment = 
                    sentimentAnalyzer.analyze(aspectText);
                aspectResults.put(aspect, sentiment);
            }
        }
        
        return aspectResults;
    }
    
    private String extractAspectText(String review, List<String> keywords) {
        String lowerReview = review.toLowerCase();
        
        for (String keyword : keywords) {
            int index = lowerReview.indexOf(keyword);
            if (index >= 0) {
                int start = Math.max(0, index - 50);
                int end = Math.min(review.length(), index + 100);
                return review.substring(start, end).trim();
            }
        }
        
        return null;
    }
    
    public AspectSummary summarizeAspectResults(
            Map<String, SentimentAnalyzer.SentimentResult> aspects) {
        
        AspectSummary summary = new AspectSummary();
        Map<String, String> aspectSentiments = new LinkedHashMap<>();
        Map<String, Double> aspectScores = new LinkedHashMap<>();
        
        for (Map.Entry<String, SentimentAnalyzer.SentimentResult> entry : aspects.entrySet()) {
            aspectSentiments.put(entry.getKey(), entry.getValue().getSentimentLabel());
            aspectScores.put(entry.getKey(), entry.getValue().getOverallScore());
        }
        
        summary.setAspectSentiments(aspectSentiments);
        summary.setAspectScores(aspectScores);
        
        double avgScore = aspectScores.values().stream()
            .mapToDouble(Double::doubleValue)
            .average()
            .orElse(0.0);
        
        summary.setOverallScore(avgScore);
        
        return summary;
    }
    
    public static class AspectSummary {
        private Map<String, String> aspectSentiments;
        private Map<String, Double> aspectScores;
        private double overallScore;
        
        public Map<String, String> getAspectSentiments() { return aspectSentiments; }
        public void setAspectSentiments(Map<String, String> aspectSentiments) { this.aspectSentiments = aspectSentiments; }
        public Map<String, Double> getAspectScores() { return aspectScores; }
        public void setAspectScores(Map<String, Double> aspectScores) { this.aspectScores = aspectScores; }
        public double getOverallScore() { return overallScore; }
        public void setOverallScore(double overallScore) { this.overallScore = overallScore; }
    }
}
```

---

## Build and Run Instructions

```bash
# Compile all projects
mvn clean compile

# Run specific project
cd <project-folder>
mvn spring-boot:run

# Download required OpenNLP models
# Models available at: https://opennlp.apache.org/models
# Place in: src/main/resources/models/
```

## Extension Ideas

1. **Model Training**: Train custom models for domain-specific NER
2. **Multi-language Support**: Add support for non-English languages
3. **Integration**: Connect with Elasticsearch for search functionality
4. **Visualization**: Add frontend dashboard for analytics
5. **Pipeline Optimization**: Implement caching and parallel processing