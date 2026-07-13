# Chunking Theory & Intuition

## 💡 The Context Boundary Problem
In a RAG system, you cannot feed an entire 500-page PDF into an LLM prompt. You must split it into chunks and embed each chunk.

The naive approach is **Fixed-Size Chunking**: Split the text every 500 characters.
- **The Problem**: What if character 500 lands right in the middle of a critical sentence? 
  *Chunk 1*: "The company's new remote work policy states that all employees must be in the office on"
  *Chunk 2*: "Tuesdays and Thursdays."
  
If a user asks "What days must I be in the office?", the vector search might retrieve Chunk 2 because it contains the days. But Chunk 2 has zero context. The LLM won't know if "Tuesdays and Thursdays" refers to the cafeteria menu or the remote work policy. The context boundary was destroyed.

## 🔄 The Solution: Intelligent Splitting
To preserve meaning, chunks must respect natural language boundaries.

### 1. Recursive Character Splitting
Instead of blindly splitting at 500 characters, this algorithm tries to split at the largest logical boundary possible (e.g., double newlines `\n\n` representing paragraphs). 
If a paragraph is still larger than the 500-character limit, it falls back to splitting by single newlines `\n` (sentences), then by spaces ` ` (words), and only as a last resort by characters.

### 2. Overlap
Even with intelligent boundaries, related thoughts might span across two paragraphs. To maintain continuity, chunks are configured with an **Overlap** (e.g., 50 characters). The end of Chunk 1 is duplicated at the beginning of Chunk 2.

### 3. Semantic Chunking
A more advanced, ML-driven approach. Instead of looking at punctuation, it embeds every single sentence individually. It then compares the cosine similarity of consecutive sentences. If the similarity drops below a certain threshold, it assumes a topic change has occurred and creates a chunk boundary there.