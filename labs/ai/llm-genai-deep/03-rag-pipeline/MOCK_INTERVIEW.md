# Mock Interview: Design a RAG System for Legal Document Q&A

## Scenario
You are interviewing for a senior applied scientist role at a legal tech startup. They want you to design a RAG system for answering questions about contracts and case law.

## Interviewer Opening Question
"Design a RAG pipeline for legal document Q&A. What are the key components and trade-offs?"

## Candidate Response
"I'd design a modular RAG pipeline with five stages: ingestion, retrieval, reranking, synthesis, and citation grounding. The key challenge in legal is hallucination risk and citation accuracy, so I'd emphasize chunking strategy, hybrid search, and a dedicated citation verification step."

## Interviewer Probing Questions

**Q: How do you handle long legal documents (100+ pages)?**
"I'd use hierarchical chunking: split by natural sections (clauses, articles), then sub-chunk to 512 tokens with 128-token overlap. Store section metadata for citation."

**Q: Retrieval strategy?**
"Hybrid search: dense embeddings (e.g., instructor-xl) for semantic similarity + sparse BM25 for keyword matching on legal terms. Merge results with RRF."

**Q: How do you handle citations?**
"Each retrieved chunk carries document ID, section, page number. The LLM must output inline citations [Doc1:Sec3.2]. A verification module checks that cited chunks actually support the claim via NLI."

## Candidate Solution (Python)

```python
from dataclasses import dataclass
from typing import List
import numpy as np

@dataclass
class Chunk:
    text: str
    doc_id: str
    section: str
    page: int
    embedding: np.ndarray = None

class ChunkingStrategy:
    def __init__(self, max_tokens=512, overlap=128):
        self.max_tokens = max_tokens
        self.overlap = overlap

    def chunk_document(self, text: str, doc_id: str) -> List[Chunk]:
        sections = text.split("\n\nSection ")
        chunks = []
        for sec in sections:
            lines = sec.split("\n")
            section_name = lines[0][:50] if lines else ""
            words = " ".join(lines).split()
            for i in range(0, len(words), self.max_tokens - self.overlap):
                chunk_text = " ".join(words[i:i + self.max_tokens])
                chunks.append(Chunk(text=chunk_text, doc_id=doc_id, section=section_name, page=0))
        return chunks

class HybridRetriever:
    def __init__(self, dense_model, bm25_index):
        self.dense_model = dense_model
        self.bm25_index = bm25_index

    def retrieve(self, query: str, k=20):
        dense_scores = self.dense_model.search(query, k=k)
        bm25_scores = self.bm25_index.search(query, k=k)
        # Reciprocal Rank Fusion
        combined = {}
        for rank, (idx, _) in enumerate(dense_scores):
            combined[idx] = 1.0 / (60 + rank + 1)
        for rank, (idx, _) in enumerate(bm25_scores):
            combined[idx] = combined.get(idx, 0) + 1.0 / (60 + rank + 1)
        return sorted(combined.items(), key=lambda x: -x[1])[:k]

class CitationVerifier:
    def __init__(self, nli_model):
        self.nli_model = nli_model

    def verify(self, claim: str, chunks: List[Chunk]) -> List[str]:
        valid_citations = []
        for chunk in chunks:
            score = self.nli_model.predict(claim, chunk.text)
            if score > 0.8:
                valid_citations.append(f"[{chunk.doc_id}:{chunk.section}]")
        return valid_citations

class LegalRAGPipeline:
    def __init__(self, retriever, llm, citation_verifier):
        self.retriever = retriever
        self.llm = llm
        self.verifier = citation_verifier

    def answer(self, query: str):
        chunks = self.retriever.retrieve(query)
        context = "\n\n".join(c.text for c in chunks)
        prompt = f"""Answer the legal question using the provided context.
        Always cite sources inline as [DocId:Section].
        Context: {context}
        Question: {query}
        Answer:"""
        raw_answer = self.llm.generate(prompt)
        citations = self.verifier.verify(raw_answer, chunks)
        return {"answer": raw_answer, "citations": citations}
```

## Interviewer Feedback
"Strong design with clear attention to legal-specific needs: hierarchical chunking, hybrid retrieval, and citation verification. Your RRF merging and NLI-based verification show production thinking."

## Key Takeaways
- Legal RAG requires citation grounding to be trustworthy
- Hierarchical chunking preserves document structure
- Hybrid retrieval (dense + sparse) outperforms either alone for legal text
- NLI-based citation verification reduces hallucination risk
- Overlap in chunking prevents information loss at boundaries
