# Self-Attention Theory & Intuition

## 💡 The Problem with RNNs
Before Transformers, sequence data (like text) was processed by Recurrent Neural Networks (RNNs) or LSTMs. They read words one by one, left to right, maintaining a hidden state.
- **Problem 1: The Bottleneck**: The hidden state at the end of a sentence must compress the meaning of the entire sentence into a single vector. For long sentences, it "forgets" earlier words.
- **Problem 2: Sequential Execution**: You cannot process word 5 until you have processed word 4. This prevents parallelization on GPUs, making training extremely slow.

## 🔍 The Solution: Attention Is All You Need
In 2017, Google researchers introduced the Transformer, replacing recurrence entirely with the **Attention Mechanism**.
Instead of reading words sequentially, Attention looks at *all* words in a sentence simultaneously. For every word, it asks: "Which other words in this sentence should I pay attention to in order to understand my own context?"

Example: "The bank of the river." vs "The bank on Main Street."
The word "bank" means different things. Self-attention allows the representation of the word "bank" to be updated based on its strong connection (attention) to "river" or "Main Street".

## 🔑 The Information Retrieval Analogy (Query, Key, Value)
Self-attention is modeled after database retrieval systems.

Imagine you go to a library (the database):
1. **Query (Q)**: What you are looking for. (e.g., "Books about Java").
2. **Key (K)**: The title/metadata of the books on the shelf. (e.g., "Effective Java", "Python Basics").
3. **Value (V)**: The actual content of the book.

When a word wants to understand its context:
1. It projects a **Query** ("I am the word 'bank', I need context").
2. Every other word projects a **Key** ("I am 'river', I describe nature").
3. We calculate the similarity (Dot Product) between the Query and all Keys. This is the **Attention Score**.
4. The word then takes a weighted sum of all the **Values** of the other words, based on the attention scores. Words with high scores (like 'river') contribute heavily to the updated meaning of 'bank'.