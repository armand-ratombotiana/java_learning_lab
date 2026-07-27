# Mock Interview: RAG (Retrieval-Augmented Generation)

## Question 1: RAG Pipeline Design
**Q**: Design a RAG system for enterprise document Q&A.

**A**:
```
User Query -> Query Processing -> Retrieval -> Augmentation -> Generation -> Response
                |                    |
        Query Expansion/         Vector Search +
        Rewriting                BM25 Hybrid
                                 Search
```

Components:
- **Indexing**: Chunk documents (256-1024 tokens), embed with text-embedding-3-small
- **Retrieval**: Hybrid (dense + sparse) with reciprocal rank fusion
- **Reranking**: Cross-encoder on top-k results (k=100 -> top 10)
- **Generation**: LLM with context + instruction prompt

```python
class RAGPipeline:
    def __init__(self, embed_model, llm, vector_store):
        self.embed_model = embed_model
        self.llm = llm
        self.vector_store = vector_store

    def query(self, q, k=10, rerank_k=3):
        q_emb = self.embed_model.encode(q)
        results = self.vector_store.search(q_emb, k=k)
        results = self.rerank(q, results)[:rerank_k]
        context = self.format_context(results)
        prompt = f"Answer based on context:\n{context}\n\nQ: {q}\nA:"
        return self.llm.generate(prompt)
```

## Question 2: Chunking Strategies
**Q**: Compare different document chunking strategies for RAG.

**A**:
- **Fixed-size**: 256/512 tokens with overlap (50-100). Simple but can split meaning.
- **Recursive**: Split on paragraphs -> sentences -> words. Better semantic preservation.
- **Semantic**: Split on topic changes (embedding similarity threshold).
- **Document-based**: Keep document structure (sections, headings).
- **Agentic**: LLM decides where to split (expensive but best quality).

Chunk overlap: 10-20% of chunk size to prevent context fragmentation at boundaries.

## Question 3: Retrieval Optimization
**Q**: How do you optimize retrieval quality in RAG?

**A**:
- **Hybrid search**: Dense (embedding) + Sparse (BM25) with RRF fusion
- **Query expansion**: Generate 3-5 query variations, search all, merge results
- **Hypothetical document embedding (HyDE)**: Generate "ideal" answer, use its embedding for search
- **Multi-vector retrieval**: ColBERT late interaction for finer-grained matching
- **Metadata filtering**: Pre-filter by date, source, category before vector search
- **Reranking**: Cross-encoder on top-100 to select top-5 (2x accuracy improvement)

## Question 4: RAG Evaluation
**Q**: How do you evaluate a RAG system end-to-end?

**A**: Multi-dimensional evaluation:

```yaml
retrieval:
  context_precision: "% of retrieved docs that are relevant"
  context_recall: "% of relevant docs that were retrieved"
  mrr: "Mean reciprocal rank of first relevant doc"

generation:
  faithfulness: "% of claims supported by context"
  answer_relevancy: "How well answer addresses query"
  completeness: "Does answer cover all aspects of query?"
  conciseness: "No unnecessary information"

end_to_end:
  user_satisfaction: "Human rating or implicit signals"
  task_completion: "Did user find what they needed?"
  latency: "P50/P95/P99 response time"
```

## Question 5: Advanced RAG Techniques
**Q**: Describe RAG optimization techniques beyond basic retrieval.

**A**:
- **Self-RAG**: Model retrieves only when needed (decides when retrieval is useful)
- **Corrective RAG (CRAG)**: If retrieval quality is low, try web search instead
- **Adaptive RAG**: Route queries based on complexity (simple -> LLM only, complex -> RAG)
- **Iterative RAG**: Multiple retrieval-generation cycles, refine query each round
- **Agentic RAG**: Agents decide which tools to use (search, calculator, code interpreter)
- **Graph RAG**: Build knowledge graph from documents, traverse relations
- **RAPTOR**: Hierarchical summarization for multi-level retrieval
