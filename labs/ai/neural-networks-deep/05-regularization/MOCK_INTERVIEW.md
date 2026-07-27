# Mock Interview: Implement Dropout, L2 Regularization, and Label Smoothing

## Scenario
You are interviewing for a deep learning engineer role. They want to test your understanding of regularization techniques.

## Interviewer Opening Question
"Implement dropout, L2 regularization, and label smoothing from scratch. Explain how each prevents overfitting and when to use them."

## Candidate Response
"Dropout randomly zeroes a fraction of neurons during training, forcing the network to learn redundant representations. L2 regularization (weight decay) penalizes large weights by adding sum(w^2) to the loss. Label smoothing replaces one-hot targets with a uniform distribution, preventing the model from becoming overconfident."

## Interviewer Probing Questions

**Q: How does dropout behave differently at train vs test time?**
"During training, activations are scaled by 1/(1-p) to maintain expected magnitude (inverted dropout). During inference, dropout is disabled and the full network is used. The scaling during training means no scaling is needed at test time."

**Q: What's the relationship between L2 and weight decay?**
"In standard SGD, L2 regularization and weight decay are equivalent: loss += lambda * ||w||^2. In Adam, they're not equivalent — decoupled weight decay (AdamW) applies weight decay directly to the weights, not through the gradient."

**Q: What's a typical label smoothing value?**
"epsilon = 0.1 is standard. The target becomes: y_smooth = (1 - epsilon) * y_onehot + epsilon / num_classes. For 1000 classes: 0.9 for true class, 0.0001 for others."

## Candidate Solution (Python)

```python
import torch
import torch.nn as nn
import torch.nn.functional as F
import numpy as np

class Dropout(nn.Module):
    """Dropout with inverted scaling."""
    def __init__(self, p=0.5):
        super().__init__()
        self.p = p  # probability of zeroing

    def forward(self, x):
        if not self.training or self.p == 0:
            return x
        # Inverted dropout: scale up during training
        mask = torch.rand_like(x) > self.p
        return x * mask / (1.0 - self.p)

def l2_regularization(model, lambda_l2=1e-4):
    """Compute L2 penalty for all parameters."""
    l2_norm = 0.0
    for param in model.parameters():
        if param.requires_grad:
            l2_norm += torch.sum(param ** 2)
    return lambda_l2 * l2_norm

class LabelSmoothingLoss(nn.Module):
    """Cross-entropy with label smoothing."""
    def __init__(self, num_classes, smoothing=0.1, reduction="mean"):
        super().__init__()
        self.num_classes = num_classes
        self.smoothing = smoothing
        self.reduction = reduction
        self.confidence = 1.0 - smoothing
        self.uniform = smoothing / num_classes

    def forward(self, logits, targets):
        """
        logits: (batch, num_classes)
        targets: (batch,) — class indices
        """
        batch, num_classes = logits.shape
        # Compute log probabilities
        log_probs = F.log_softmax(logits, dim=1)

        # Create smoothed targets
        with torch.no_grad():
            smooth_targets = torch.full_like(log_probs, self.uniform)
            smooth_targets[torch.arange(batch), targets] = self.confidence

        loss = -torch.sum(smooth_targets * log_probs, dim=1)
        if self.reduction == "mean":
            return loss.mean()
        elif self.reduction == "sum":
            return loss.sum()
        return loss

class RegularizedModel(nn.Module):
    """Example model with all regularization techniques."""
    def __init__(self, input_size, hidden_size, num_classes, dropout_p=0.3):
        super().__init__()
        self.fc1 = nn.Linear(input_size, hidden_size)
        self.dropout = Dropout(dropout_p)
        self.fc2 = nn.Linear(hidden_size, hidden_size)
        self.fc3 = nn.Linear(hidden_size, num_classes)

    def forward(self, x):
        x = F.relu(self.fc1(x))
        x = self.dropout(x)
        x = F.relu(self.fc2(x))
        x = self.dropout(x)
        x = self.fc3(x)
        return x

class RegularizationTrainer:
    def __init__(self, model, lr=0.001, lambda_l2=1e-4, label_smoothing=0.1, num_classes=10):
        self.model = model
        self.lr = lr
        self.lambda_l2 = lambda_l2
        self.loss_fn = LabelSmoothingLoss(num_classes, smoothing=label_smoothing)

    def train_step(self, x, y, optimizer):
        self.model.train()
        logits = self.model(x)
        # Label smoothed loss
        loss = self.loss_fn(logits, y)
        # L2 regularization
        loss += l2_regularization(self.model, self.lambda_l2)
        optimizer.zero_grad()
        loss.backward()
        optimizer.step()
        return loss.item()

    def evaluate_regularization_effects(self):
        """Show how each technique affects weights and predictions."""
        x = torch.randn(100, 10)
        y = torch.randint(0, 5, (100,))

        model_no_reg = RegularizedModel(10, 64, 5)
        model_dropout = RegularizedModel(10, 64, 5, dropout_p=0.3)
        model_l2 = RegularizedModel(10, 64, 5)

        # Compare weight norms
        w_norm_no = sum(p.norm().item() for p in model_no_reg.parameters())
        w_norm_drop = sum(p.norm().item() for p in model_dropout.parameters())
        w_norm_l2 = sum(p.norm().item() for p in model_l2.parameters())

        print(f"Weight norms — no reg: {w_norm_no:.4f}, dropout: {w_norm_drop:.4f}, L2: {w_norm_l2:.4f}")

        # Compare prediction confidence
        model_no_reg.eval()
        model_dropout.eval()
        probs_no = F.softmax(model_no_reg(x[:5]), dim=1)
        probs_drop = F.softmax(model_dropout(x[:5]), dim=1)
        print(f"\nMax probabilities (no reg): {probs_no.max(dim=1).values}")
        print(f"Max probabilities (dropout): {probs_drop.max(dim=1).values}")

class GradientFlowAnalyzer:
    """Analyze how regularization affects gradient flow."""
    def __init__(self, model):
        self.model = model

    def gradient_norms(self):
        norms = {}
        for name, param in self.model.named_parameters():
            if param.grad is not None:
                norms[name] = param.grad.norm().item()
        return norms
```

## Interviewer Feedback
"Comprehensive implementation of all three regularization techniques. Your explanation of inverted dropout scaling, L2/weight decay equivalence, and label smoothing values is spot on. The comparison of effects shows practical understanding."

## Key Takeaways
- Dropout: random neuron masking with inverted scaling at train time
- L2 / weight decay: penalizes large weight magnitudes
- Label smoothing: replaces hard 0/1 targets with smoothed distribution
- Regularization prevents overfitting by reducing model capacity utilization
- Each technique addresses a different aspect of generalization
