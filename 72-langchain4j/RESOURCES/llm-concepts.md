# LLM Concepts

## Large Language Models

### What is an LLM?
A neural network trained on massive text data to predict and generate human-like text.

### Key Capabilities
- Text generation
- Question answering
- Summarization
- Translation
- Code generation

## Core Concepts

### Prompt Engineering
Designing effective inputs to get desired outputs from LLMs.

**Techniques:**
- Few-shot learning (examples in prompt)
- Chain-of-thought (step-by-step reasoning)
- Role prompting

### Embeddings
Numerical vector representations of text that capture semantic meaning.

```
Text → "Hello world" → [0.1, -0.3, 0.5, ...] → Vector
```

### Vector Databases
Store embeddings for similarity search (RAG applications).

### Chains
Sequence of LLM calls where output of one feeds into the next.

### Agents
LLMs that can take actions via tools (search, calculator, API calls).

## Retrieval-Augmented Generation (RAG)

```
1. User query
2. Retrieve relevant docs (vector search)
3. Add docs to prompt context
4. Generate answer with LLM
5. Return response
```

## Model Types
- **GPT-4, Claude, Gemini**: General purpose
- **Code models**: Code generation (Codex, CodeLlama)
- **Embedding models**: Text to vectors