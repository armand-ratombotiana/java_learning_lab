# Math Foundation: RAG Platform

## Embedding Similarity
- cosine_sim(q, d) = (q · d) / (||q|| * ||d||)
- Scores range [-1, 1]; typically 0.7+ indicates relevance

## Reranker Score
- Cross-encoder output: softmax over [relevant, irrelevant] logits
- Can use logit for "relevant" class directly as relevance score

## Evaluation Metrics (RAGAS)
- Faithfulness: Do claims in answer match retrieved context? (LLM-based evaluation)
- Answer Relevancy: Is answer relevant to the question?
- Context Precision: Are retrieved documents truly relevant?
- Context Recall: Are all relevant documents retrieved?

## MRR (Mean Reciprocal Rank)
- MRR = 1/|Q| * ∑(q∈Q) 1/rank_q
- Measures rank of first relevant document in retrieval results
