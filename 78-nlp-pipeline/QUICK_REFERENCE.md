# NLP Pipeline Quick Reference

## Complete Pipeline
```
Raw Text → Preprocessing → Feature Extraction → Modeling → Evaluation
```

## Text Preprocessing
```java
String text = "Hello World! NLP is amazing.";

// Lowercasing
text = text.toLowerCase();

// Remove punctuation
text = text.replaceAll("\\p{Punct}", "");

// Tokenization
String[] tokens = text.split("\\s+");

// Stop word removal
tokens = removeStopWords(tokens);

// Stemming/Lemmatization
tokens = stemWords(tokens);
```

## Feature Extraction

### Bag of Words
```java
CountVectorizer cv = new CountVectorizer();
double[][] bow = cv.fitTransform(documents);
```

### TF-IDF
```java
TfidfVectorizer tfidf = new TfidfVectorizer();
double[][] tfidfMatrix = tfidf.fitTransform(documents);
```

### Word Embeddings
```java
Word2Vec w2v = Word2Vec.load("GoogleNews-vectors-negative300.bin");
double[] vector = w2v.getWordVector("java");
```

## Stanford CoreNLP
```java
Properties props = new Properties();
props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse");
StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
CoreDocument doc = new CoreDocument("NLP is transforming how we process text.");
pipeline.annotate(doc);
```

## Sentiment Analysis
```java
// Stanford CoreNLP Sentiment
for (CoreSentence sentence : doc.sentences()) {
    String sentiment = sentence.sentiment();
}

// VADER (compound: -1 to +1)
double sentimentScore = vader.getPolarityScores(text).compound;
```

## Text Summarization
- **Extractive**: Score sentences by word frequency, select top-K
- **Abstractive**: Use Seq2Seq or Transformer models (BART, T5, Pegasus)

## Topic Modeling

### LDA
```java
LDAModel model = new LDAModel(numTopics);
model.fit(documentTermMatrix);
double[][] topicWordDist = model.getTopicWordDist();
int[][] documentTopicDist = model.getDocTopicDist();
```

### NMF
```java
NMF nmf = new NMF(numTopics);
nmf.fit(tfidfMatrix);
```