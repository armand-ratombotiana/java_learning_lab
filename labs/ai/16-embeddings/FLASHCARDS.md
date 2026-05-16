# Embeddings - FLASHCARDS

### Card 1
**Q:** What is an embedding?
**A:** Dense vector representation of words/items that captures semantic meaning in continuous space

### Card 2
**Q:** Difference between Word2Vec Skip-Gram and CBOW?
**A:** Skip-Gram: predict context from center word. CBOW: predict center word from context

### Card 3
**Q:** Why use negative sampling in Word2Vec?
**A:** Faster training than full softmax; samples random negative words instead of computing all

### Card 4
**Q:** What is co-occurrence matrix in GloVe?
**A:** Matrix storing how often word pairs appear together in context window

### Card 5
**Q:** How does negative sampling relate to skip-gram?
**A:** Instead of predicting all words in vocabulary, sample a few negative (incorrect) words

### Card 6
**Q:** What is subsampling in Word2Vec?
**A:** Randomly remove frequent words like "the" from training to balance vocabulary

### Card 7
**Q:** What is cosine similarity for embeddings?
**A:** Angle between vectors; 1=identical, 0=orthogonal, -1=opposite

### Card 8
**Q:** What is the benefit of subword (FastText) embeddings?
**A:** Handle out-of-vocabulary words by combining known subword units

### Card 9
**Q:** How do embeddings capture semantic relationships?
**A:** Similar words have similar vectors; analogies work via vector arithmetic

### Card 10
**Q:** What is hierarchical softmax?
**A:** Tree-based approximation of softmax where words are leaf nodes

### Card 11
**Q:** How to use embeddings for semantic search?
**A:** Encode query and documents as embeddings, rank by cosine similarity

### Card 12
**Q:** What is mean pooling in sentence embeddings?
**A:** Average all word embeddings in sentence to get fixed-size sentence vector

**Total: 12 flashcards**