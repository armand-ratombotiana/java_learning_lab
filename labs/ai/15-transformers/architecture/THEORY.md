# Transformer Architecture Theory

## 💡 The Problem with Pure Attention
The Self-Attention mechanism (from the previous lab) has a massive flaw if used by itself: **It has no concept of order**.
The sentence "The dog bit the man" and "The man bit the dog" produce the exact same set of words interacting with each other. Because attention computes similarities across the whole sequence simultaneously, it treats the sentence like a "bag of words". We must inject information about the *position* of the words into the model.

## 🏗️ Core Components of the Transformer

### 1. Positional Encoding
Since the model doesn't process words sequentially, we add a unique vector to each word's embedding based on its position in the sentence. This allows the model to differentiate between "dog" at position 1 and "dog" at position 4.

### 2. Multi-Head Attention
A single attention mechanism might focus heavily on syntactic relationships (e.g., verbs attending to subjects). But we also want the model to understand semantic relationships (e.g., pronouns attending to nouns). 
Instead of one attention mechanism, we use multiple "Heads" in parallel. 
- The input is split into $h$ smaller chunks.
- Attention is computed independently on each chunk.
- The results are concatenated back together.
This allows the model to attend to information from different representation subspaces simultaneously.

### 3. Residual Connections (Add & Norm)
Deep networks suffer from vanishing gradients. To fix this, Transformers use **Residual Connections** (skip connections). The input to a layer is added directly to its output: $Output = Layer(x) + x$.
This provides a highway for gradients to flow backward unimpeded. After adding, the result is normalized using **Layer Normalization** to stabilize training.

### 4. Feed-Forward Network (FFN)
After the attention block, the data passes through a standard Multi-Layer Perceptron (usually two linear transformations with a ReLU activation in between). While attention aggregates information *across* words, the FFN processes each word's updated representation *independently*.

## 🔄 Encoder-Decoder Structure
The original Transformer (designed for translation) consists of:
- **The Encoder**: Reads the source sentence (e.g., English) and creates a deep contextual representation. It uses standard Self-Attention.
- **The Decoder**: Generates the target sentence (e.g., French) one word at a time. It uses **Masked Self-Attention** (so it can't look at future words it hasn't generated yet) and **Cross-Attention** (where Queries come from the Decoder, but Keys and Values come from the Encoder's output).