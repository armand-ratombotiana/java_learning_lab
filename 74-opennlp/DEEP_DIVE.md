# Deep Dive: Apache OpenNLP - Natural Language Processing in Java

## Table of Contents
1. [Introduction to OpenNLP](#introduction)
2. [Core NLP Tasks](#core-tasks)
3. [Implementation Guide](#implementation)
4. [Model Training](#model-training)
5. [Advanced Features](#advanced-features)
6. [Real-World Applications](#applications)

---

## 1. Introduction to Apache OpenNLP

Apache OpenNLP is a machine learning-based toolkit for processing natural language text. It provides Java-based implementations for common NLP tasks including sentence detection, tokenization, part-of-speech tagging, named entity recognition, and document classification.

### Why OpenNLP?

- **Pure Java**: No native dependencies, runs on any JVM
- **Pre-trained Models**: Ready-to-use models for common tasks
- **Extensible**: Custom models can be trained
- **Lightweight**: Small memory footprint
- **Mature**: Battle-tested in production for years

---

## 2. Core NLP Tasks

### 2.1 Sentence Detection

```java
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

public class SentenceDetectionExample {
    public static void main(String[] args) throws Exception {
        // Load pre-trained model
        SentenceModel model = new SentenceModel(
            new FileInputStream("models/en-sent.bin")
        );
        
        SentenceDetectorME detector = new SentenceDetectorME(model);
        
        // Detect sentences
        String text = "This is the first sentence. This is the second! " +
                      "And this is the third?";
        
        String[] sentences = detector.sentDetect(text);
        
        for (String sentence : sentences) {
            System.out.println("Sentence: " + sentence);
        }
        
        // Get confidence scores
        double[] scores = detector.getSentenceProbabilities();
    }
}
```

**Key Concepts**:
- `SentenceDetectorME`: Maximum entropy-based sentence detector
- `sentDetect()`: Returns array of detected sentences
- `getSentenceProbabilities()`: Returns confidence scores

### 2.2 Tokenization

```java
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.tokenize.SimpleTokenizer;

public class TokenizationExample {
    public static void main(String[] args) throws Exception {
        // Using pre-trained model
        TokenizerModel model = new TokenizerModel(
            new FileInputStream("models/en-token.bin")
        );
        
        TokenizerME tokenizer = new TokenizerME(model);
        
        String text = "The quick brown fox jumps over the lazy dog.";
        String[] tokens = tokenizer.tokenize(text);
        
        for (String token : tokens) {
            System.out.println("Token: " + token);
        }
        
        // Or use simple tokenizer (no model needed)
        String[] simpleTokens = SimpleTokenizer.INSTANCE.tokenize(text);
    }
}
```

**Types of Tokenizers**:
- `TokenizerME`: ML-based with best accuracy
- `SimpleTokenizer`: Rule-based, no model required
- `WhitespaceTokenizer`: Splits on whitespace only

### 2.3 Part-of-Speech Tagging

```java
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

public class POSExample {
    public static void main(String[] args) throws Exception {
        // Load model
        POSModel model = new POSModel(
            new FileInputStream("models/en-pos-maxent.bin")
        );
        
        POSTaggerME tagger = new POSTaggerME(model);
        
        // Tokenize first
        String[] tokens = {"The", "quick", "brown", "fox", "jumps"};
        
        // Get POS tags
        String[] tags = tagger.tag(tokens);
        
        for (int i = 0; i < tokens.length; i++) {
            System.out.println(tokens[i] + " -> " + tags[i]);
        }
        
        // Common tags: NN (noun), VB (verb), JJ (adjective), DT (determiner)
    }
}
```

**Common POS Tags**:
- NN: Noun, singular
- NNS: Noun, plural
- VB: Verb, base form
- VBD: Verb, past tense
- JJ: Adjective
- RB: Adverb
- DT: Determiner

### 2.4 Named Entity Recognition

```java
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.NameModel;
import opennlp.tools.util.Span;

public class NERExample {
    public static void main(String[] args) throws Exception {
        // Load NER model
        NameModel model = new NameModel(
            new FileInputStream("models/en-ner-person.bin")
        );
        
        NameFinderME finder = new NameFinderME(model);
        
        // Tokenize text
        String[] tokens = {"John", "works", "at", "Google", "in", "Mountain", "View"};
        
        // Find entities
        Span[] spans = finder.find(tokens);
        
        for (Span span : spans) {
            System.out.println(span.getType() + ": " + 
                span.getCoveredText(tokens));
        }
        
        // Clear for next document
        finder.clearAdaptiveData();
    }
}
```

**Entity Types**:
- PERSON: People's names
- ORGANIZATION: Company names
- LOCATION: Geographic locations
- DATE: Dates and times
- MONEY: Monetary amounts
- PERCENT: Percentages

---

## 3. Implementation Guide

### 3.1 Complete NLP Pipeline

```java
import opennlp.tools.sentdetect.*;
import opennlp.tools.tokenize.*;
import opennlp.tools.postag.*;
import opennlp.tools.namefind.*;

public class NLPPipeline {
    
    private SentenceDetectorME sentenceDetector;
    private TokenizerME tokenizer;
    private POSTaggerME posTagger;
    private NameFinderME nameFinder;
    
    public NLPPipeline() throws Exception {
        this.sentenceDetector = new SentenceDetectorME(
            new SentenceModel(new FileInputStream("models/en-sent.bin")));
        this.tokenizer = new TokenizerME(
            new TokenizerModel(new FileInputStream("models/en-token.bin")));
        this.posTagger = new POSTaggerME(
            new POSModel(new FileInputStream("models/en-pos-maxent.bin")));
        this.nameFinder = new NameFinderME(
            new NameModel(new FileInputStream("models/en-ner-person.bin")));
    }
    
    public void process(String text) {
        // 1. Detect sentences
        String[] sentences = sentenceDetector.sentDetect(text);
        
        for (String sentence : sentences) {
            System.out.println("Sentence: " + sentence);
            
            // 2. Tokenize
            String[] tokens = tokenizer.tokenize(sentence);
            
            // 3. POS tagging
            String[] tags = posTagger.tag(tokens);
            
            // 4. Named Entity Recognition
            Span[] entities = nameFinder.find(tokens);
            
            for (int i = 0; i < tokens.length; i++) {
                System.out.println(tokens[i] + " [" + tags[i] + "]");
            }
            
            for (Span entity : entities) {
                System.out.println("Entity: " + entity.getType() + 
                    " - " + entity.getCoveredText(tokens));
            }
        }
    }
    
    public void close() {
        sentenceDetector.close();
        tokenizer.close();
        posTagger.close();
        nameFinder.close();
    }
}
```

### 3.2 Document Classification

```java
import opennlp.tools.doccat.*;
import opennlp.tools.util.*;

public class DocumentClassifier {
    
    private DocumentCategorizerME categorizer;
    
    public DocumentClassifier() throws Exception {
        // Train from training data
        InputStreamFactory<DocumentSampleStream> factory = 
            new PlainTextDocumentSampleStream(
                new FileInputStream("training-data.txt"));
        
        DoccatModel model = DocumentCategorizerTrainer.train(
            "en", 
            new DocumentSampleStream(factory), 
            TrainingParameters.defaultParams());
        
        this.categorizer = new DocumentCategorizerME(model);
    }
    
    public String categorize(String text) {
        String[] tokens = text.split("\\s+");
        
        double[] outcomes = categorizer.categorize(tokens);
        
        return categorizer.getBestCategory(outcomes);
    }
}
```

### 2.5 Language Detection

```java
import opennlp.tools.langdetect.*;
import opennlp.tools.util.*;

public class LanguageDetector {
    
    private LanguageDetectorME detector;
    
    public LanguageDetector() throws Exception {
        LanguageDetectorModel model = new LanguageDetectorModel(
            new FileInputStream("models/en-lang.bin"));
        
        this.detector = new LanguageDetectorME(model);
    }
    
    public String detect(String text) {
        Language[] languages = detector.predictLanguages(text);
        
        for (Language lang : languages) {
            System.out.println(lang.getLang() + ": " + lang.getConfidence());
        }
        
        return languages[0].getLang();
    }
}
```

---

## 4. Model Training

### 4.1 Training a Custom Tokenizer

```java
import opennlp.tools.tokenize.*;
import opennlp.tools.util.*;

public class CustomTokenizerTrainer {
    
    public static void main(String[] args) throws Exception {
        // Load training data
        ObjectStream<TokenSample> samples = new FileTokenSampleStream(
            new FileInputStream("tokenizer-training-data.txt"),
            StandardCharsets.UTF_8);
        
        // Train model
        TokenizerModel model = TokenizerTrainer.train(
            samples,
            new TokenizerFactory("en", null, null, null),
            TrainingParameters.defaultParams());
        
        // Save model
        FileOutputStream out = new FileOutputStream("custom-tokenizer.bin");
        model.serialize(out);
    }
}
```

**Training Data Format**:
```
A|tokens B|with C|embedded D|tags
```

### 4.2 Training a Custom NER Model

```java
import opennlp.tools.namefind.*;
import opennlp.tools.util.*;

public class NERTrainer {
    
    public static void main(String[] args) throws Exception {
        // Load training data
        ObjectStream<NameSample> samples = new FileNameSampleStream(
            new FileInputStream("ner-training-data.txt"));
        
        // Training parameters
        TrainingParameters params = new TrainingParameters();
        params.put("算法", "MAXENT");
        params.put("Iterations", 100);
        params.put("Cutoff", 1);
        
        // Train
        NameModel model = NameFinderTrainer.train(
            "CUSTOM",
            samples,
            new NameFinderFactory(),
            params);
        
        // Save
        model.serialize(new FileOutputStream("custom-ner.bin"));
    }
}
```

**Training Data Format**:
```
<START:PERSON>John Smith<END> works at <START:ORG>Google<END>
```

---

## 5. Advanced Features

### 5.1 Chunking

```java
import opennlp.tools.chunker.*;

public class ChunkerExample {
    
    public static void main(String[] args) throws Exception {
        ChunkerModel model = new ChunkerModel(
            new FileInputStream("models/en-chunker.bin"));
        
        ChunkerME chunker = new ChunkerME(model);
        
        String[] tokens = {"The", "quick", "brown", "fox", "jumps"};
        String[] tags = {"DT", "JJ", "JJ", "NN", "VB"};
        
        String[] chunks = chunker.chunk(tokens, tags);
        
        for (int i = 0; i < tokens.length; i++) {
            System.out.println(tokens[i] + " -> " + chunks[i]);
        }
    }
}
```

### 5.2 Coreference Resolution

```java
import opennlp.tools.coref.*;
import opennlp.tools.coref.mention.*;

public class CoreferenceExample {
    
    public static void main(String[] args) throws Exception {
        // Load coreference model
        CoreferenceParser parser = new CoreferenceParser(
            new FileInputStream("models/en-coref.bin"));
        
        // Process sentences
        Parse[] parses = parser.parseSentences(
            "John bought a car. He drove it to the store.");
        
        // Get coreference chains
        DiscourseElement[] elements = parses[0].getTokenNodes();
    }
}
```

### 5.3 Custom Pipeline Components

```java
public class CustomNLPComponent {
    
    private Tokenizer tokenizer;
    private POSTagger posTagger;
    
    public CustomNLPComponent() {
        // Initialize components
    }
    
    public ProcessingResult process(String input) {
        // Custom processing logic
    }
}
```

---

## 6. Real-World Applications

### 6.1 Text Analytics Pipeline

```java
@Service
public class TextAnalyticsService {
    
    private NLPPipeline pipeline;
    
    public AnalyticsResult analyze(String text) {
        // Process through NLP pipeline
        // Extract entities, tags, sentences
        // Return structured result
    }
}
```

### 6.2 Document Processing

```java
@Service
public class DocumentProcessingService {
    
    public ProcessedDocument processDocument(Document doc) {
        // Extract text
        // Run NLP pipeline
        // Extract metadata
        // Return processed result
    }
}
```

### 6.3 Search Enhancement

```java
@Service
public class SearchEnhancementService {
    
    public List<String> enhanceQuery(String query) {
        // Extract key terms
        // Get POS tags
        // Expand with synonyms
    }
}
```

---

## Summary

| Task | Class | Purpose |
|------|-------|---------|
| Sentence Detection | SentenceDetectorME | Split text into sentences |
| Tokenization | TokenizerME | Split text into tokens |
| POS Tagging | POSTaggerME | Tag tokens with parts of speech |
| NER | NameFinderME | Extract named entities |
| Classification | DocumentCategorizerME | Categorize documents |
| Chunking | ChunkerME | Identify phrase chunks |

---

## Additional Resources

- [Apache OpenNLP Documentation](https://opennlp.apache.org/)
- [OpenNLP Models](https://opennlp.apache.org/models.html)
- [NLP Fundamentals](https://www.nltk.org/book/)

---

*Continue to QUIZZES.md for assessment and EDGE_CASES.md for debugging.*