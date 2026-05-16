# Fine-tuning - FLASHCARDS

### Card 1
**Q:** What is LoRA?
**A:** Low-Rank Adaptation - adds small trainable matrices to attention layers

### Card 2
**Q:** How does LoRA work?
**A:** Decomposes weight change into two low-rank matrices (A and B)

### Card 3
**Q:** What is QLoRA?
**A:** LoRA + quantization - uses 4-bit quantized base model, trains LoRA

### Card 4
**Q:** What is PEFT?
**A:** Parameter-Efficient Fine-Tuning - methods like LoRA, Prefix Tuning

### Card 5
**Q:** Why fine-tune vs RAG?
**A:** Fine-tune: modify model behavior permanently. RAG: add external knowledge

### Card 6
**Q:** What is adapter tuning?
**A:** Insert small adapter modules between transformer layers

### Card 7
**Q:** What is prefix tuning?
**A:** Prepend trainable virtual tokens to input that learn task-specific behavior

### Card 8
**Q:** Why freeze base model in LoRA?
**A:** Keeps original knowledge, only adapts behavior through small changes

### Card 9
**Q:** What is domain adaptation?
**A:** Fine-tune on domain-specific data to improve performance in that area

### Card 10
**Q:** What is instruction fine-tuning?
**A:** Fine-tune on instruction-response pairs to improve instruction following

### Card 11
**Q:** What does r parameter mean in LoRA?
**A:** Rank - dimension of low-rank matrices, controls expressiveness

### Card 12
**Q:** Benefits of fine-tuning over prompting?
**A:** Can customize behavior deeply, better token efficiency, private data stays local

**Total: 12 flashcards**