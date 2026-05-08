# Edge Cases & Pitfalls: Deep Learning

Common mistakes and how to avoid them.

---

## 1. Data Processing Pitfalls

### ❌ Wrong: Not normalizing data
```python
# WRONG - Different scales cause slow convergence
data = [1000, 500, 10, 0.1]
# Model struggles with large range differences
```

### ✅ Correct: Normalize all inputs
```python
# CORRECT - Standardize to zero mean, unit variance
from sklearn.preprocessing import StandardScaler
scaler = StandardScaler()
normalized_data = scaler.fit_transform(data)
```

### ❌ Wrong: Data leakage in preprocessing
```python
# WRONG - Fit on all data including test
scaler.fit(all_data)  # Leakage!
```

### ✅ Correct: Separate preprocessing
```python
# CORRECT - Only fit on training data
scaler.fit(train_data)
test_data_normalized = scaler.transform(test_data)
```

---

## 2. Neural Network Architecture Pitfalls

### ❌ Wrong: Wrong input shape
```python
# WRONG - Wrong image format
model.predict([255, 240, 200])  # Single pixel values

# OR
model.predict(image)  # Missing batch dimension
```

### ✅ Correct: Proper input handling
```python
# CORRECT - Proper shape (batch, channels, height, width)
batch = batch.unsqueeze(0)  # Add batch dimension
# OR
image = image.reshape(1, 3, 224, 224)
```

### ❌ Wrong: Forgetting to flatten
```python
# WRONG - Passing 2D tensor to fully connected layer
x = torch.randn(16, 3, 32, 32)
x = self.fc(x)  # Error!
```

### ✅ Correct: Flatten properly
```python
# CORRECT - Flatten before FC layer
x = x.view(x.size(0), -1)  # OR x = x.flatten(1)
x = self.fc(x)
```

### ❌ Wrong: Wrong activation for output
```python
# WRONG - No activation for classification
output = self.fc(x)  # Raw logits
# Should use softmax/sigmoid for probabilities
```

### ✅ Correct: Proper output activation
```python
# CORRECT
output = torch.softmax(self.fc(x), dim=1)  # Multi-class
output = torch.sigmoid(self.fc(x))  # Binary
```

---

## 3. Training Pitfalls

### ❌ Wrong: Learning rate too high
```python
# WRONG - Loss oscillates or diverges
optimizer = torch.optim.Adam(model.parameters(), lr=1.0)
```

### ✅ Correct: Start with standard learning rate
```python
# CORRECT
optimizer = torch.optim.Adam(model.parameters(), lr=1e-3)
```

### ❌ Wrong: Learning rate too low
```python
# WRONG - Training takes forever
optimizer = torch.optim.Adam(model.parameters(), lr=1e-10)
```

### ✅ Correct: Use appropriate LR finder
```python
# CORRECT - Find optimal LR
from torch_lr_finder import LRFinder
finder = LRFinder(model, optimizer, criterion)
finder.range_test(trainloader, end_lr=1, num_iter=100)
finder.plot()
```

### ❌ Wrong: Not setting model to train/eval mode
```python
# WRONG - Model in wrong mode
model.eval()  # Wrong if you want to train
```

### ✅ Correct: Proper mode switching
```python
# CORRECT
model.train()  # Enable dropout, batch norm training
model.eval()   # Disable for inference
```

### ❌ Wrong: Not zeroing gradients
```python
# WRONG - Gradients accumulate
loss.backward()
optimizer.step()  # Wrong! Gradients keep accumulating
```

### ✅ Correct: Zero gradients
```python
# CORRECT
optimizer.zero_grad()  # Before backward
loss.backward()
optimizer.step()
```

### ❌ Wrong: Not using gradient clipping
```python
# WRONG - Gradients can explode
loss.backward()  # No clipping
```

### ✅ Correct: Clip gradients
```python
# CORRECT
torch.nn.utils.clip_grad_norm_(model.parameters(), max_norm=1.0)
```

---

## 4. Overfitting & Underfitting Pitfalls

### ❌ Wrong: Overfitting due to too many parameters
```python
# WRONG - Model remembers training data
model = nn.Sequential([
    nn.Linear(10, 10000),
    nn.ReLU(),
    nn.Linear(10000, 10)  # Way too big!
])
```

### ✅ Correct: Right-sized model
```python
# CORRECT - Appropriate capacity
model = nn.Sequential([
    nn.Linear(10, 64),
    nn.ReLU(),
    nn.Linear(64, 10)
])
```

### ❌ Wrong: Not using dropout
```python
# WRONG - No regularization
model = nn.Sequential([
    nn.Linear(512, 512),
    nn.ReLU(),
    nn.Linear(512, 10)
])
```

### ✅ Correct: Add dropout
```python
# CORRECT
model = nn.Sequential([
    nn.Linear(512, 512),
    nn.ReLU(),
    nn.Dropout(0.3),
    nn.Linear(512, 10)
])
```

### ❌ Wrong: No validation monitoring
```python
# WRONG - Only watch training loss
for epoch in range(100):
    train_loss = train_one_epoch()
    # Should check validation!
```

### ✅ Correct: Monitor validation
```python
# CORRECT
for epoch in range(100):
    train_loss = train_one_epoch()
    val_loss = validate()
    if val_loss < best_loss:
        save_checkpoint()
```

---

## 5. Batch & Memory Pitfalls

### ❌ Wrong: Batch size too large for GPU
```python
# WRONG - Out of memory
train_loader = DataLoader(dataset, batch_size=512)
```

### ✅ Correct: Reduce batch size
```python
# CORRECT
train_loader = DataLoader(dataset, batch_size=32)
```

### ❌ Wrong: Not using mixed precision
```python
# WRONG - Slower training
outputs = model(data)
loss = criterion(outputs, labels)
```

### ✅ Correct: Use mixed precision
```python
# CORRECT - Faster training
with torch.cuda.amp.autocast():
    outputs = model(data)
    loss = criterion(outputs, labels)
```

---

## 6. CNN-Specific Pitfalls

### ❌ Wrong: Negative stride in convolution
```python
# WRONG - Usually not what you want
nn.Conv2d(3, 64, 3, stride=-1)
```

### ✅ Correct: Standard stride
```python
# CORRECT
nn.Conv2d(3, 64, 3, stride=1)  # or stride=2
```

### ❌ Wrong: Padding missing
```python
# WRONG - Image shrinks each layer
nn.Conv2d(3, 64, 3)  # No padding
```

### ✅ Correct: Add padding
```python
# CORRECT
nn.Conv2d(3, 64, 3, padding=1)  # Maintains size
```

### ❌ Wrong: Forgetting to normalize image
```python
# WRONG - Raw pixel values
model.predict(image)  # Values 0-255
```

### ✅ Correct: Normalize image
```python
# CORRECT - ImageNet normalization
transforms.Normalize(mean=[0.485, 0.456, 0.406],
                     std=[0.229, 0.224, 0.225])
```

---

## 7. RNN-Specific Pitfalls

### ❌ Wrong: Not handling variable length sequences
```python
# WRONG - All sequences padded to same length but not masked
packed = pack_padded_sequence(sequences, lengths, 
                             batch_first=True, enforce_sorted=False)
```

### ✅ Correct: Use packing
```python
# CORRECT - Handle variable lengths
from torch.nn.utils.rnn import pad_sequence, pack_padded_sequence
padded = pad_sequence(sequences, batch_first=True)
packed = pack_padded_sequence(padded, lengths, 
                             batch_first=True, enforce_sorted=False)
output, hidden = rnn(packed)
```

### ❌ Wrong: Not using hidden state correctly
```python
# WRONG - New hidden each time
output, hidden = rnn(x, None)
```

### ✅ Correct: Pass hidden state
```python
# CORRECT - Use previous hidden
output, hidden = rnn(x, hidden)
```

---

## 8. Transformer Pitfalls

### ❌ Wrong: Forgetting positional encoding
```python
# WRONG - No position info
embeddings = embedding(input_ids)
```

### ✅ Correct: Add positional encoding
```python
# CORRECT - Add position information
embeddings = embedding(input_ids) + positional_encoding
```

### ❌ Wrong: Wrong attention mask
```python
# WRONG - Padding tokens included in attention
```

### ✅ Correct: Proper masking
```python
# CORRECT - Mask padding
attention_mask = (input_ids != pad_token).float()
```

---

## 9. Loss Function Pitfalls

### ❌ Wrong: Using MSE for classification
```python
# WRONG - Not appropriate for classification
criterion = nn.MSELoss()
```

### ✅ Correct: Use cross-entropy
```python
# CORRECT
criterion = nn.CrossEntropyLoss()  # Multi-class
criterion = nn.BCEWithLogitsLoss()  # Binary
```

### ❌ Wrong: Wrong shape for loss
```python
# WRONG - Wrong shape
loss = criterion(outputs, labels)  # Need logits!
```

### ✅ Correct: Proper shape
```python
# CORRECT
outputs = model(x)  # Raw logits (N, C)
labels = torch.tensor([0, 2, 1, ...])  # (N,) class indices
loss = criterion(outputs, labels)
```

---

## 10. Debugging NaN Losses

### Diagnosis Steps:
```python
# 1. Check for NaN in data
print(torch.isnan(data).any())

# 2. Reduce learning rate
lr = 1e-5

# 3. Add gradient clipping
torch.nn.utils.clip_grad_norm_(model.parameters(), 1.0)

# 4. Check for division by zero
eps = 1e-8
normalized = data / (std + eps)

# 5. Use smaller batch
batch_size = 8
```

### Common Causes:
- Learning rate too high
- Division by zero
- Log of zero or negative numbers
- Gradient explosion
- Incorrect loss function
- NaN in input data

---

## 11. Checkpoint & Save Pitfalls

### ❌ Wrong: Not saving optimizer state
```python
# WRONG - Can't resume training properly
torch.save(model.state_dict(), 'model.pt')
```

### ✅ Correct: Save complete state
```python
# CORRECT
torch.save({
    'epoch': epoch,
    'model_state_dict': model.state_dict(),
    'optimizer_state_dict': optimizer.state_dict(),
    'loss': loss,
}, 'checkpoint.pt')
```

---

## 12. Inference Pitfalls

### ❌ Wrong: Forgetting model.eval() during inference
```python
# WRONG
model.eval()  # Needed for dropout off
output = model(input)
```

### ✅ Correct: Proper inference
```python
# CORRECT
model.eval()
with torch.no_grad():
    output = model(input)
```

---

## 13. Checklist: Before Training

- [ ] Data normalized (zero mean, unit variance)
- [ ] Input shapes correct
- [ ] Learning rate reasonable (1e-3 for Adam)
- [ ] Model not too large/small
- [ ] Proper loss function for task
- [ ] Train/val split created
- [ ] Gradient clipping set
- [ ] Checkpoints planned
- [ ] Overfitting check with small data first
- [ ] Reproducibility seed set (torch.manual_seed)

---

## 14. Checklist: During Training

- [ ] Training loss decreasing
- [ ] Validation loss not increasing
- [ ] No NaN in loss
- [ ] Gradients reasonable magnitude
- [ ] GPU memory stable
- [ ] Epoch time reasonable

---

## 15. Checklist: After Training

- [ ] Model saved properly
- [ ] Evaluation on held-out test set
- [ ] Confusion matrix analyzed
- [ ] Predictions make sense
- [ ] Performance metrics recorded

---

**Remember**: Debugging deep learning requires systematic approach - always check data first, then model, then training process.