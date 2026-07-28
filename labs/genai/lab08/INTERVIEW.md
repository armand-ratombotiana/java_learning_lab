# Lab 08: Interview Questions

## Q1: How does CLIP learn multimodal representations?
**A:** CLIP uses a dual encoder (image encoder + text encoder) trained with contrastive loss on 400M image-text pairs. It maximizes cosine similarity between matched pairs and minimizes it for unmatched pairs.

## Q2: What is the advantage of contrastive pretraining over supervised pretraining?
**A:** Contrastive pretraining leverages naturally occurring image-text pairs (no manual labels), scales to arbitrary concepts, and enables zero-shot transfer.

## Q3: How does cross-modal attention differ from self-attention?
**A:** Cross-modal attention uses queries from one modality and keys/values from another, enabling information flow between modalities. Self-attention operates within a single modality.

## Q4: What is image patch embedding in Vision Transformers (ViT)?
**A:** Images are split into fixed-size patches (e.g., 16x16), each flattened and linearly projected to the model dimension, then treated as a sequence of tokens.

## Q5: How do you evaluate multimodal model alignment?
**A:** Recall@K (retrieval), zero-shot classification accuracy, image-text matching accuracy, and human evaluation of cross-modal generation.
