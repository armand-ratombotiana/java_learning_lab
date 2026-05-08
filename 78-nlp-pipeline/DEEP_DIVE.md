# Deep Dive: NLP Pipeline - End-to-End Natural Language Processing

## Table of Contents
1. [Introduction to NLP Pipelines](#introduction)
2. [Text Preprocessing](#preprocessing)
3. [Feature Extraction](#feature-extraction)
4. [Stanford CoreNLP Integration](#stanford)
5. [Sentiment Analysis](#sentiment)
6. [Text Summarization](#summarization)
7. [Topic Modeling](#topic-modeling)
8. [Building Complete Pipelines](#complete-pipelines)

---

## 1. Introduction to NLP Pipelines

An NLP pipeline is a sequence of processing steps that transform raw text into structured, actionable information. Modern NLP pipelines include:

- **Text Cleaning**: Remove noise, normalize text
- **Tokenization**: Break into tokens
- **Annotation**: POS, NER, dependencies
- **Feature Extraction**: Create numerical representations
- **Analysis**: Sentiment, topics, summary

---

## 2. Text Preprocessing

### 2.1 Tokenization

```java
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

public class TokenizationExample {
    
    public static String[] tokenize(String text) throws Exception {
        TokenizerModel model = new TokenizerModel(
            new FileInputStream("models/en-token.bin")
        );
        
        TokenizerME tokenizer = new TokenizerME(model);
        return tokenizer.tokenize(text);
    }
    
    // Custom tokenization for special cases
    public static String[] customTokenize(String text) {
        // Split on whitespace and punctuation
        return text.split("[\\s.,!?;:]+");
    }
}
```

### 2.2 Stemming and Lemmatization

```java
import opennlp.tools.stemmer.PorterStemmer;
import opennlp.tools.lemmatizer.LemmatizerModel;
import opennlp.tools.lemmatizer.LemmatizerME;

public class StemmingLemmatization {
    
    public static String stem(String word) {
        PorterStemmer stemmer = new PorterStemmer();
        return stemmer.stem(word);
    }
    
    public static String lemmatize(String word, String pos) 
            throws Exception {
        LemmatizerModel model = new LemmatizerModel(
            new FileInputStream("models/en-lemmatizer.bin")
        );
        
        LemmatizerME lemmatizer = new LemmatizerME(model);
        return lemmatizer.lemmatize(word, pos);
    }
}
```

### 2.3 Stopword Removal

```java
import java.util.Set;
import java.util.Arrays;

public class StopwordRemoval {
    
    private static final Set<String> STOPWORDS = Set.of(
        "the", "is", "at", "which", "on", "a", "an", "and", "or"
    );
    
    public static String[] removeStopwords(String[] tokens) {
        return Arrays.stream(tokens)
            .filter(token -> !STOPWORDS.contains(token.toLowerCase()))
            .toArray(String[]::new);
    }
}
```

---

## 3. Feature Extraction

### 3.1 Bag of Words

```java
import weka.core.FastVector;
import weka.core.Attribute;
import weka.core.Instances;

public class BagOfWords {
    
    public static Instances createBowFeatures(
            String[] documents, 
            int vocabSize) {
        
        // Build vocabulary
        Map<String, Integer> vocab = buildVocabulary(documents, vocabSize);
        
        // Create features
        FastVector attributes = new FastVector();
        for (String word : vocab.keySet()) {
            attributes.addElement(new Attribute(word));
        }
        
        // Add class attribute
        attributes.addElement(new Attribute("class", (FastVector) null));
        
        return new Instances("BowData", attributes, documents.length);
    }
    
    private static Map<String, Integer> buildVocabulary(
            String[] docs, int maxVocab) {
        // Build word frequency map
    }
}
```

### 3.2 TF-IDF

```java
import weka.core.FastVector;
import weka.core.Attribute;

public class TFIDF {
    
    public static double[][] computeTFIDF(
            String[] documents, 
            int maxFeatures) {
        
        // Compute term frequency
        Map<String, Integer>[] termFreq = computeTermFreq(documents);
        
        // Compute document frequency
        Map<String, Integer> docFreq = computeDocFreq(termFreq);
        
        // Compute TF-IDF
        double[][] tfidf = new double[documents.length][maxFeatures];
        
        for (int i = 0; i < documents.length; i++) {
            for (String term : termFreq[i].keySet()) {
                double tf = termFreq[i].get(term);
                double df = docFreq.get(term);
                double idf = Math.log(documents.length / df);
                tfidf[i][idx] = tf * idf;
            }
        }
        
        return tfidf;
    }
}
```

---

## 4. Stanford CoreNLP Integration

```java
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.util.*;

public class StanfordNLPExample {
    
    public static void main(String[] args) {
        // Create pipeline
        Properties props = new Properties();
        props.setProperty("annotators", 
            "tokenize,ssplit,pos,lemma,ner,depparse");
        props.setProperty("outputFormat", "text");
        
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        
        // Process text
        String text = "John works at Google in Mountain View.";
        Annotation annotation = pipeline.process(text);
        
        // Get tokens
        for (CoreLabel token : annotation.get(CoreAnnotations.TokensAnnotation.class)) {
            System.out.println(token.word() + "/" + 
                token.get(CoreAnnotations.PartOfSpeechAnnotation.class));
        }
        
        // Get named entities
        for (CoreMap entity : annotation.get(
                CoreAnnotations.MentionsAnnotation.class)) {
            System.out.println(entity.get(CoreAnnotations.EntityTypeAnnotation.class) + 
                ": " + entity.get(CoreAnnotations.TextAnnotation.class));
        }
    }
}
```

---

## 5. Sentiment Analysis

### 5.1 VADER Sentiment

```java
// Note: VADER is primarily Python, but can use wrapper libraries
// Or implement rule-based sentiment

public class SentimentAnalysis {
    
    public enum Sentiment { POSITIVE, NEGATIVE, NEUTRAL }
    
    public static Sentiment analyze(String text) {
        // Simple rule-based approach
        String[] positiveWords = {"good", "great", "excellent", "amazing"};
        String[] negativeWords = {"bad", "terrible", "poor", "awful"};
        
        int score = 0;
        String[] tokens = text.toLowerCase().split("\\s+");
        
        for (String token : tokens) {
            for (String pos : positiveWords) {
                if (token.contains(pos)) score++;
            }
            for (String neg : negativeWords) {
                if (token.contains(neg)) score--;
            }
        }
        
        if (score > 0) return Sentiment.POSITIVE;
        if (score < 0) return Sentiment.NEGATIVE;
        return Sentiment.NEUTRAL;
    }
}
```

### 5.2 Stanford CoreNLP Sentiment

```java
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;

public class StanfordSentiment {
    
    public static String getSentiment(String text) {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,parse,sentiment");
        
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        Annotation annotation = pipeline.process(text);
        
        return annotation.get(
            CoreAnnotations.SentencesAnnotation.class
        ).stream()
            .map(s -> s.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class))
            .map(SentimentModel::classify)
            .map(tree -> tree.label())
            .findFirst()
            .orElse("NEUTRAL");
    }
}
```

---

## 6. Text Summarization

### 6.1 Extractive Summarization

```java
public class ExtractiveSummarization {
    
    public static String summarize(String text, int numSentences) 
            throws Exception {
        
        // Use sentence detection
        String[] sentences = detectSentences(text);
        
        // Score sentences based on TF-IDF
        double[] scores = scoreSentences(sentences);
        
        // Select top sentences
        List<Integer> topIndices = getTopIndices(scores, numSentences);
        
        // Build summary
        return topIndices.stream()
            .map(i -> sentences[i])
            .collect(Collectors.joining(" "));
    }
    
    private static double[] scoreSentences(String[] sentences) {
        // Implementation: score based on word frequency
    }
}
```

### 6.2 Abstractive Summarization with LLM

```java
public class LLMSummarization {
    
    private final ChatLanguageModel model;
    
    public String summarize(String text) {
        String prompt = """
            Summarize the following article in 3 sentences:
            
            """ + text;
        
        return model.chat(prompt);
    }
}
```

---

## 7. Topic Modeling

### 7.1 LDA Implementation

```java
// Using Weka's LDA implementation
import weka.clusterers.LDA;

public class TopicModeling {
    
    public static void main(String[] args) throws Exception {
        // Load documents
        Instances data = loadDocuments("data/corpus.arff");
        
        // Create LDA model
        int numTopics = 10;
        
        // Note: Weka doesn't have built-in LDA, use external library
        // Or implement simple version
        
        // Output topics
        // Each topic is a distribution over words
    }
}
```

---

## 8. Building Complete Pipelines

```java
public class CompleteNLPPipeline {
    
    private TokenizerME tokenizer;
    private POSTaggerME posTagger;
    private NameFinderME nameFinder;
    private ChatLanguageModel llm;
    
    public void initialize() throws Exception {
        tokenizer = new TokenizerME(
            new TokenizerModel(new FileInputStream("models/en-token.bin")));
        posTagger = new POSTaggerME(
            new POSModel(new FileInputStream("models/en-pos.bin")));
        nameFinder = new NameFinderME(
            new NameModel(new FileInputStream("models/en-ner.bin")));
        
        llm = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .build();
    }
    
    public PipelineResult process(String text) {
        // Step 1: Tokenize
        String[] tokens = tokenizer.tokenize(text);
        
        // Step 2: POS tagging
        String[] tags = posTagger.tag(tokens);
        
        // Step 3: NER
        Span[] entities = nameFinder.find(tokens);
        
        // Step 4: Analyze
        String sentiment = analyzeSentiment(text);
        String summary = summarize(text);
        
        return new PipelineResult(tokens, tags, entities, sentiment, summary);
    }
}
```

---

## Summary

NLP pipelines combine multiple processing stages:

1. **Preprocessing**: Tokenize, stem, remove stopwords
2. **Feature Extraction**: Bag of Words, TF-IDF
3. **Analysis**: POS, NER, sentiment, topics
4. **Output**: Structured results, summaries

Key tools: Stanford CoreNLP, OpenNLP, Weka, LLMs

---

*Continue to QUIZZES.md and EDGE_CASES.md.*