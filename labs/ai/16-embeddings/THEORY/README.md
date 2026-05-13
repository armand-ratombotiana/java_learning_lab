# Embeddings - Theory

## 1. Word Embeddings

### Word2Vec
- **CBOW**: Predict word from context
- **Skip-gram**: Predict context from word
- **Negative sampling**: Reduce computation

### GloVe
- Global word co-occurrence
- Matrix factorization approach

## 2. Contextual Embeddings

### ELMo
- Bidirectional LSTM
- Context-dependent

### BERT Embeddings
- Transformer-based
- Contextual from both sides

## 3. Sentence Embeddings

### Approaches
- Mean/max pooling of word embeddings
- Sentence-BERT (SBERT)
- Universal Sentence Encoder

## 4. Semantic Search

### Vector Search
- Embed query and documents
- Cosine similarity ranking

### Dense vs Sparse
- Dense: BERT-based
- Sparse: TF-IDF, BM25

## 5. Applications

### Similarity Search
- Duplicate detection
- Semantic matching

### Classification
- Use embeddings as features

### Clustering
- Semantic clustering