# Word2Vec Theory & Intuition

## 💡 The Problem with One-Hot Encoding
If you have a vocabulary of 10,000 words, you can represent the word "king" as a vector with 10,000 elements, where all are 0 except for a 1 at the index for "king". This is One-Hot Encoding.

**The Problem**: 
1. The vectors are massive and sparse (mostly zeros), wasting memory.
2. **No Semantic Meaning**: The dot product (similarity) between the One-Hot vector for "king" and "queen" is exactly 0. The similarity between "king" and "apple" is also exactly 0. The model has no idea that king and queen are related concepts.

## 🧠 The Solution: Dense Embeddings (Word2Vec)
In 2013, Google researchers introduced Word2Vec. The idea is simple: **"You shall know a word by the company it keeps"** (John Rupert Firth).

If the word "king" and "queen" frequently appear surrounded by similar words (e.g., "The ___ sat on the throne"), they should have similar mathematical representations.

Word2Vec trains a shallow neural network to predict a word based on its context (or vice versa). We don't actually care about the predictions; we only care about the **weights** the network learns. These weights become the dense, continuous vector representations (Embeddings) of the words.

In this semantic space, distance has meaning.
Famously: $Vector(\text{King}) - Vector(\text{Man}) + Vector(\text{Woman}) \approx Vector(\text{Queen})$.

## 🏗️ The Two Architectures
1. **Continuous Bag of Words (CBOW)**: Predicts the target word based on its surrounding context words. (e.g., Given "The", "sat", "on", "the", predict "cat"). Faster to train.
2. **Skip-Gram**: Predicts the surrounding context words based on the target word. (e.g., Given "cat", predict "The", "sat", "on", "the"). Better for rare words and small datasets.