# Apache OpenNLP - Solution

## Overview

This module provides comprehensive examples for Natural Language Processing using Apache OpenNLP. It covers tokenization, sentence detection, POS tagging, Named Entity Recognition (NER), chunking, and parsing.

## Dependencies

```xml
<dependency>
    <groupId>org.apache.opennlp</groupId>
    <artifactId>opennlp-tools</artifactId>
    <version>2.5.0</version>
</dependency>
```

## Key Concepts

### 1. Tokenization

The `TokenizationExample` class demonstrates:
- Simple tokenizer usage
- Model-based tokenization
- Token span detection
- Token probability retrieval

### 2. Sentence Detection

The `SentenceDetectionExample` class covers:
- Sentence boundary detection
- Sentence position spans
- Sentence probability calculation

### 3. POS Tagging

The `POSExample` class provides:
- Part-of-speech tagging
- Tag probability retrieval
- POS span generation

### 4. Named Entity Recognition

The `NERExample` class implements:
- Person name detection
- Location detection
- Organization detection
- Entity type identification

### 5. Chunker

The `ChunkerExample` class demonstrates:
- Phrase chunking
- Chunk span detection
- Top-k sequence generation

### 6. Lemmatizer

The `LemmatizerExample` class covers:
- Word lemmatization
- POS-aware lemmatization

### 7. Parser

The `ParserExample` class provides:
- Syntactic parsing
- Multiple parse generation

## Classes Overview

| Class | Description |
|-------|-------------|
| `TokenizationExample` | Text tokenization |
| `SentenceDetectionExample` | Sentence boundary detection |
| `POSExample` | Part-of-speech tagging |
| `NERExample` | Named Entity Recognition |
| `ChunkerExample` | Phrase chunking |
| `LemmatizerExample` | Word lemmatization |
| `ParserExample` | Syntactic parsing |
| `PipelineExample` | Complete NLP pipeline |

## Running Tests

```bash
cd 74-opennlp
mvn test -Dtest=Test
```

## Examples

### Tokenization

```java
Tokenizer tokenizer = SimpleTokenizer.INSTANCE;
String[] tokens = tokenizer.tokenize("This is a test sentence.");
```

### Sentence Detection

```java
SentenceModel model = new SentenceModel(new FileInputStream("models/en-sent.bin"));
SentenceDetectorME detector = new SentenceDetectorME(model);
String[] sentences = detector.sentDetect(text);
```

### POS Tagging

```java
POSModel model = new POSModel(new FileInputStream("models/en-pos-maxent.bin"));
POSTaggerME tagger = new POSTaggerME(model);
String[] tags = tagger.tag(tokens);
```

### Named Entity Recognition

```java
TokenNameFinderModel model = new TokenNameFinderModel(new FileInputStream("models/en-ner-person.bin"));
NameFinderME finder = new NameFinderME(model);
Span[] entities = finder.find(tokens);
```

## Required Models

OpenNLP requires trained models for each task. Download from:
- https://opennlp.apache.org/models.html

Required models:
- `en-sent.bin` - Sentence detector
- `en-token.bin` - Tokenizer
- `en-pos-maxent.bin` - POS tagger
- `en-ner-person.bin` - Person name finder
- `en-chunker.bin` - Chunker

## Best Practices

1. **Model Loading**: Load models once and reuse
2. **Resource Management**: Use try-with-resources for model streams
3. **Error Handling**: Handle exceptions for model loading
4. **Pipeline Integration**: Combine multiple NLP tasks efficiently

## Further Reading

- [Apache OpenNLP Documentation](https://opennlp.apache.org/docs/)
- [OpenNLP Models](https://opennlp.apache.org/models.html)
- [OpenNLP GitHub](https://github.com/apache/opennlp)