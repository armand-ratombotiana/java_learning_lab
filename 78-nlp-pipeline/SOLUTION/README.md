# NLP Pipeline - Solution

## Overview

This module provides comprehensive examples for building NLP pipelines using Stanford CoreNLP and Apache Lucene. It covers tokenization, POS tagging, NER, text processing, feature extraction, and search.

## Dependencies

```xml
<dependency>
    <groupId>edu.stanford.nlp</groupId>
    <artifactId>stanford-corenlp</artifactId>
    <version>3.9.3</version>
</dependency>
<dependency>
    <groupId>org.apache.lucene</groupId>
    <artifactId>lucene-core</artifactId>
    <version>9.11.1</version>
</dependency>
```

## Key Concepts

### 1. Stanford CoreNLP

The `StanfordNLPExample` class demonstrates:
- Pipeline creation with multiple annotators
- Tokenization
- Sentence splitting
- POS tagging
- Lemmatization
- Named Entity Recognition
- Dependency parsing

### 2. Tokenization

The `TokenizationExample` class covers:
- Lucene-based tokenization
- Lowercasing
- Stop word removal
- Stemming

### 3. Lucene Search

The `LuceneSearchExample` class provides:
- Index creation
- Query parsing
- Search execution
- Filtered search

### 4. Text Processing

The `TextProcessingExample` class implements:
- Text normalization
- HTML tag removal
- Contraction expansion
- Accent removal

### 5. Feature Extraction

The `FeatureExtractionExample` class demonstrates:
- Word frequency calculation
- Top N words extraction
- TF-IDF calculation

### 6. Pipeline Integration

The `PipelineExample` class provides:
- End-to-end NLP pipeline
- Keyword extraction

## Classes Overview

| Class | Description |
|-------|-------------|
| `StanfordNLPExample` | Stanford CoreNLP integration |
| `TokenizationExample` | Text tokenization and preprocessing |
| `LuceneSearchExample` | Full-text search |
| `TextProcessingExample` | Text normalization utilities |
| `FeatureExtractionExample` | Feature extraction methods |
| `PipelineExample` | Complete pipeline integration |

## Running Tests

```bash
cd 78-nlp-pipeline
mvn test -Dtest=Test
```

## Examples

### Stanford CoreNLP Pipeline

```java
Properties props = new Properties();
props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse");
StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

CoreDocument document = pipeline.annotate("John works at Google.");
List<CoreEntityMention> entities = document.entityMentions();
```

### Tokenization

```java
Analyzer analyzer = new StandardAnalyzer();
List<String> tokens = tokenizeWithLucene("Hello World");
```

### TF-IDF

```java
double tfidf = calculateTFIDF("term", tokens, documents);
```

## Best Practices

1. **Pipeline Configuration**: Select appropriate annotators for your task
2. **Performance**: Use lighter pipelines when full annotations aren't needed
3. **Error Handling**: Handle exceptions for model loading
4. **Caching**: Reuse pipeline instances for better performance

## Further Reading

- [Stanford CoreNLP Documentation](https://stanfordnlp.github.io/CoreNLP/)
- [Lucene Documentation](https://lucene.apache.org/core/)
- [Stanford NLP Group](https://nlp.stanford.edu/)