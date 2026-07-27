# Mock Interview: Design an Embedding Model for Semantic Search at Scale

## Scenario
You are interviewing for a staff ML engineer role at a search company. You must design a text embedding system serving millions of queries per day.

## Interviewer Opening Question
"Walk me through how you would design and deploy an embedding model for large-scale semantic search."

## Candidate Response
"I'd start with a bi-encoder architecture using a pretrained transformer as the backbone, outputting a pooled embedding vector. For deployment, I'd use approximate nearest neighbor search with HNSW indexing for sub-10ms retrieval at 99% recall."

## Interviewer Probing Questions

**Q: How do you choose the embedding dimension?**
"512 dimensions strikes a good balance between accuracy and latency. I'd evaluate on BEIR benchmarks and sweep 128, 256, 512, 768."

**Q: How do you train the model?**
"Contrastive learning with in-batch negatives. I'd use a temperature-scaled cross-entropy loss on (query, positive, negative) triples mined from click logs."

**Q: What about multilingual support?**
"Use a multilingual backbone like XLM-R. Align embeddings across languages via a shared embedding space trained on parallel data."

## Candidate Solution (Python)

```python
import torch
import torch.nn as nn
from transformers import AutoModel, AutoTokenizer

class SemanticEmbedder(nn.Module):
    def __init__(self, model_name="microsoft/mpnet-base", embed_dim=512):
        super().__init__()
        self.encoder = AutoModel.from_pretrained(model_name)
        self.projection = nn.Linear(self.encoder.config.hidden_size, embed_dim)

    def forward(self, input_ids, attention_mask):
        outputs = self.encoder(input_ids=input_ids, attention_mask=attention_mask)
        # CLS + mean pooling
        cls = outputs.last_hidden_state[:, 0, :]
        mean = outputs.last_hidden_state.mean(dim=1)
        pooled = (cls + mean) / 2.0
        return self.projection(pooled)

def contrastive_loss(embeddings, temperature=0.05):
    # embeddings shape: (batch_size * 3, dim) — anchor, pos, neg
    norms = torch.norm(embeddings, dim=1, keepdim=True)
    embeddings = embeddings / (norms + 1e-8)
    sim = torch.matmul(embeddings, embeddings.T) / temperature
    labels = torch.arange(embeddings.size(0), device=embeddings.device)
    return nn.CrossEntropyLoss()(sim, labels)

# Inference pipeline
def encode_texts(texts, model, tokenizer, device="cuda"):
    encoded = tokenizer(texts, padding=True, truncation=True, return_tensors="pt").to(device)
    with torch.no_grad():
        return model(**encoded).cpu().numpy()
```

## Interviewer Feedback
"Strong answer — you covered architecture, training, deployment, and trade-offs. I'd like to see more on data flywheel and continuous improvement, but this is a solid pass."

## Key Takeaways
- Bi-encoders with contrastive learning are the standard for semantic search
- Embedding dimension is a latency vs. accuracy trade-off
- In-batch negatives are a simple yet effective training technique
- Normalize embeddings before similarity computation
- Deploy with HNSW indexing for low-latency retrieval at scale
