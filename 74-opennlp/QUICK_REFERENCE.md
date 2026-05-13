# Apache OpenNLP Quick Reference

## NLP Tasks

| Task | Class | Description |
|------|-------|-------------|
| Sentence Detection | SentenceDetectorME | Split text into sentences |
| Tokenization | TokenizerME | Split sentences into tokens |
| POS Tagging | POSTaggerME | Part-of-speech tagging |
| Named Entity Recognition | NameFinderME | Find entities (person, org, location) |
| Language Detection | LanguageDetectorME | Detect language |
| Document Classification | DocumentCategorizerME | Classify documents |
| Chunking | ChunkerME | Phrase chunking |

## Key APIs

### Sentence Detection
```java
SentenceModel model = new SentenceModel(IN);
SentenceDetectorME detector = new SentenceDetectorME(model);
String[] sentences = detector.sentDetect(text);
```

### Tokenization
```java
TokenizerModel model = new TokenizerModel(IN);
TokenizerME tokenizer = new TokenizerME(model);
String[] tokens = tokenizer.tokenize(text);
```

### POS Tagging
```java
POSModel model = new POSModel(IN);
POSTaggerME tagger = new POSTaggerME(model);
String[] tags = tagger.tag(tokens);
// Output: [NNP, VBZ, DT, NN, NN]
```

### Named Entity Recognition
```java
TokenNameFinderModel model = new TokenNameFinderModel(IN);
NameFinderME finder = new NameFinderME(model);
Span[] spans = finder.find(tokens);
// Finds: person, organization, location, date, time, money
```

## Pre-trained Models
- `en-sent.bin` - English sentence detector
- `en-token.bin` - English tokenizer
- `en-pos-maxent.bin` - English POS tagger
- `en-ner-person.bin` - Person name finder
- `en-ner-location.bin` - Location finder
- `en-ner-date.bin` - Date finder