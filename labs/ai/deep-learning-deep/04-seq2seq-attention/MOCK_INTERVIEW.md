# Mock Interview: Implement Seq2Seq with Attention for Machine Translation

## Scenario
You are interviewing for an NLP engineer role. They want you to implement a neural machine translation system with attention.

## Interviewer Opening Question
"Implement a sequence-to-sequence model with attention for translating English to French. Walk through the encoder-decoder architecture and the attention mechanism."

## Candidate Response
"I'll implement a bidirectional LSTM encoder, an LSTM decoder with Bahdanau (additive) attention, and the full training loop. The attention mechanism computes alignment scores between the decoder hidden state and all encoder outputs, producing a context vector."

## Interviewer Probing Questions

**Q: Why use Bahdanau attention instead of Luong?**
"Bahdanau attention computes scores as v * tanh(W1 * h_decoder + W2 * h_encoder). It's more expressive but slower. Luong uses simpler dot product. For translation, Bahdanau often works better because of the non-linearity."

**Q: How do you handle variable-length sequences?**
"I use padding with a mask. The attention weights for padded positions are set to -inf before softmax, so they contribute zero to the context vector."

**Q: What about out-of-vocabulary words?**
"Use byte-pair encoding (BPE) subword tokenization instead of word-level. This handles rare words and OOV naturally."

## Candidate Solution (Python)

```python
import torch
import torch.nn as nn
import torch.nn.functional as F
import numpy as np

class Encoder(nn.Module):
    def __init__(self, vocab_size, embed_size, hidden_size, num_layers=2, dropout=0.3):
        super().__init__()
        self.embedding = nn.Embedding(vocab_size, embed_size)
        self.rnn = nn.LSTM(embed_size, hidden_size, num_layers,
                           batch_first=True, bidirectional=True, dropout=dropout)
        self.fc = nn.Linear(hidden_size * 2, hidden_size)

    def forward(self, x):
        # x: (batch, seq_len)
        embedded = self.embedding(x)  # (batch, seq_len, embed_size)
        outputs, (hidden, cell) = self.rnn(embedded)
        # outputs: (batch, seq_len, hidden_size * 2)
        # hidden: (num_layers * 2, batch, hidden_size)
        hidden = torch.tanh(self.fc(torch.cat((hidden[-2], hidden[-1]), dim=1)))
        cell = torch.tanh(self.fc(torch.cat((cell[-2], cell[-1]), dim=1)))
        return outputs, hidden, cell

class BahdanauAttention(nn.Module):
    def __init__(self, hidden_size):
        super().__init__()
        self.W1 = nn.Linear(hidden_size, hidden_size)
        self.W2 = nn.Linear(hidden_size * 2, hidden_size)  # encoder outputs are bidir
        self.V = nn.Linear(hidden_size, 1)

    def forward(self, decoder_hidden, encoder_outputs, mask=None):
        # decoder_hidden: (batch, hidden_size)
        # encoder_outputs: (batch, seq_len, hidden_size*2)
        batch, seq_len = encoder_outputs.shape[:2]

        # Add time dimension for broadcasting
        hidden = decoder_hidden.unsqueeze(1).repeat(1, seq_len, 1)  # (batch, seq_len, hidden)

        # Score computation
        score = self.V(torch.tanh(self.W1(hidden) + self.W2(encoder_outputs)))  # (batch, seq_len, 1)
        score = score.squeeze(-1)  # (batch, seq_len)

        if mask is not None:
            score = score.masked_fill(mask == 0, -1e10)

        attention_weights = F.softmax(score, dim=1)  # (batch, seq_len)
        context = torch.bmm(attention_weights.unsqueeze(1), encoder_outputs).squeeze(1)
        return context, attention_weights

class Decoder(nn.Module):
    def __init__(self, vocab_size, embed_size, hidden_size, dropout=0.3):
        super().__init__()
        self.embedding = nn.Embedding(vocab_size, embed_size)
        self.attention = BahdanauAttention(hidden_size)
        self.rnn = nn.LSTM(hidden_size * 2 + embed_size, hidden_size, batch_first=True)
        self.fc = nn.Linear(hidden_size * 2, vocab_size)
        self.dropout = nn.Dropout(dropout)

    def forward(self, x, hidden, cell, encoder_outputs, mask=None):
        # x: (batch, 1) — previous token
        embedded = self.dropout(self.embedding(x))  # (batch, 1, embed_size)

        # Compute attention context
        context, attention = self.attention(hidden.squeeze(0), encoder_outputs, mask)
        context = context.unsqueeze(1)  # (batch, 1, hidden_size)

        # Concatenate and feed to RNN
        rnn_input = torch.cat((embedded, context), dim=2)  # (batch, 1, embed + hidden)
        output, (hidden, cell) = self.rnn(rnn_input, (hidden, cell))

        # Final prediction
        output = output.squeeze(1)  # (batch, hidden)
        context = context.squeeze(1)  # (batch, hidden)
        prediction = self.fc(torch.cat((output, context), dim=1))  # (batch, vocab_size)
        return prediction, hidden, cell, attention

class Seq2Seq(nn.Module):
    def __init__(self, encoder, decoder, device):
        super().__init__()
        self.encoder = encoder
        self.decoder = decoder
        self.device = device

    def forward(self, src, trg, teacher_forcing_ratio=0.5):
        batch, trg_len = trg.shape
        trg_vocab_size = self.decoder.fc.out_features
        outputs = torch.zeros(batch, trg_len, trg_vocab_size).to(self.device)

        encoder_outputs, hidden, cell = self.encoder(src)

        decoder_input = trg[:, 0].unsqueeze(1)  # <sos>
        for t in range(1, trg_len):
            output, hidden, cell, attention = self.decoder(
                decoder_input, hidden, cell, encoder_outputs)
            outputs[:, t, :] = output
            teacher_force = np.random.random() < teacher_forcing_ratio
            top1 = output.argmax(dim=1)
            decoder_input = trg[:, t].unsqueeze(1) if teacher_force else top1.unsqueeze(1)
        return outputs

# Training setup
def train_model(model, train_loader, criterion, optimizer, num_epochs=10):
    model.train()
    for epoch in range(num_epochs):
        total_loss = 0
        for batch in train_loader:
            src, trg = batch
            src, trg = src.to(model.device), trg.to(model.device)
            optimizer.zero_grad()
            output = model(src, trg)
            output = output[:, 1:, :].reshape(-1, output.shape[-1])
            trg = trg[:, 1:].reshape(-1)
            loss = criterion(output, trg)
            loss.backward()
            torch.nn.utils.clip_grad_norm_(model.parameters(), 1.0)
            optimizer.step()
            total_loss += loss.item()
        print(f"Epoch {epoch}: loss={total_loss / len(train_loader):.4f}")
```

## Interviewer Feedback
"Comprehensive implementation covering bidirectional encoding, Bahdanau attention with masking, and teacher forcing. The attention mechanism and training loop are correctly implemented."

## Key Takeaways
- Bidirectional encoder captures context from both directions
- Bahdanau attention computes alignment via a learnable scoring function
- Padding masks prevent attention over non-existent tokens
- Teacher forcing stabilizes training by using ground truth as decoder input
- Gradient clipping prevents explosion in recurrent networks
