# RNN & LSTM - FLASHCARDS

### Card 1
**Q:** What is the main problem with vanilla RNNs?
**A:** Vanishing/exploding gradients - gradients multiply through each timestep, making it hard to learn long-term dependencies

### Card 2
**Q:** What are the four gates in an LSTM?
**A:** Forget gate (f), Input gate (i), Cell candidate (C-tilde), Output gate (o)

### Card 3
**Q:** What does the forget gate in LSTM control?
**A:** How much of the previous cell state to keep - decides what information to discard

### Card 4
**Q:** How does LSTM solve vanishing gradient?
**A:** Gated architecture allows gradient to flow unchanged through cell state (constant error carousel)

### Card 5
**Q:** What is BPTT?
**A:** Backpropagation Through Time - unroll the RNN through time and compute gradients at each step

### Card 6
**Q:** Why use tanh over sigmoid for hidden state?
**A:** Zero-centered, stronger gradients, avoids bias shifting; output range [-1, 1] vs [0, 1]

### Card 7
**Q:** What is teacher forcing in RNN training?
**A:** Use actual previous target instead of model prediction as input during training

### Card 8
**Q:** What is sequence-to-sequence architecture?
**A:** Encoder processes input sequence → Context vector → Decoder generates output sequence

### Card 9
**Q:** Why use bidirectional RNN?
**A:** Access to past and future context for each position, better for tasks like NER

### Card 10
**Q:** What is gradient clipping?
**A:** Cap gradient magnitude to prevent exploding gradients (e.g., if norm > 5, scale down)

### Card 11
**Q:** What is the cell state in LSTM?
**A:** Long-term memory carrier that flows through entire chain with minimal modification

### Card 12
**Q:** What is sequence padding in RNNs?
**A:** Adding zeros to make all sequences same length; necessary for batch processing

**Total: 12 flashcards**