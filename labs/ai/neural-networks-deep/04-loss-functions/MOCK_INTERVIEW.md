# Mock Interview: Implement Cross-Entropy and Focal Loss from Scratch

## Scenario
You are interviewing for a computer vision engineer role. They need to handle class imbalance in their object detection pipeline.

## Interviewer Opening Question
"Implement cross-entropy loss and focal loss from scratch. Explain why focal loss is better for imbalanced datasets."

## Candidate Response
"Cross-entropy loss is -log(p_t) where p_t is the model's probability for the true class. Focal loss adds a modulating factor (1 - p_t)^gamma to down-weight easy examples: FL(p_t) = -(1 - p_t)^gamma * log(p_t). When gamma > 0, the loss is dominated by hard, misclassified examples."

## Interviewer Probing Questions

**Q: How do you choose gamma for focal loss?**
"gamma=2 is standard (from the RetinaNet paper). Higher gamma focuses more on hard examples. I'd sweep gamma in [0.5, 1, 2, 5] on a validation set. gamma=0 reduces to cross-entropy."

**Q: What about alpha-balancing for class imbalance?**
"Alpha-balanced focal loss adds a class weight alpha_t: FL(p_t) = -alpha_t * (1 - p_t)^gamma * log(p_t). alpha_t should be inversely proportional to class frequency. For extreme imbalance, use alpha = 0.25 for foreground, 0.75 for background."

**Q: What are alternatives to focal loss for imbalance?**
"Class-weighted cross-entropy, online hard example mining (OHEM), and dice loss. Focal loss is the most general as it smoothly reweights based on example difficulty."

## Candidate Solution (Python)

```python
import torch
import torch.nn as nn
import torch.nn.functional as F
import numpy as np

class CrossEntropyLoss(nn.Module):
    """Cross-entropy loss from scratch (without built-in softmax)."""
    def __init__(self, reduction="mean"):
        super().__init__()
        self.reduction = reduction

    def forward(self, logits, targets):
        """
        logits: (batch, num_classes) — raw scores (not softmaxed)
        targets: (batch,) — class indices
        """
        batch, num_classes = logits.shape
        # Stable softmax: subtract max for numerical stability
        logits_max = logits.max(dim=1, keepdim=True).values
        logits_stable = logits - logits_max
        # Log-sum-exp trick for log softmax
        log_prob = logits_stable - torch.logsumexp(logits_stable, dim=1, keepdim=True)
        # Negative log-likelihood of target class
        loss = -log_prob[torch.arange(batch), targets]
        if self.reduction == "mean":
            return loss.mean()
        elif self.reduction == "sum":
            return loss.sum()
        return loss

class FocalLoss(nn.Module):
    """Focal loss: down-weights easy examples to focus on hard ones."""
    def __init__(self, gamma=2.0, alpha=None, reduction="mean"):
        super().__init__()
        self.gamma = gamma
        self.alpha = alpha
        self.reduction = reduction

    def forward(self, logits, targets):
        """
        logits: (batch, num_classes) or (batch, num_anchors, num_classes)
        targets: (batch,) or (batch, num_anchors)
        """
        if logits.dim() > 2:
            logits = logits.view(-1, logits.shape[-1])
            targets = targets.view(-1)

        # Compute probabilities via softmax
        probs = F.softmax(logits, dim=1)
        batch = probs.shape[0]

        # Gather probability of target class
        p_t = probs[torch.arange(batch), targets]

        # Focal loss: -(1 - p_t)^gamma * log(p_t)
        ce_loss = -torch.log(p_t + 1e-8)
        focal_weight = (1 - p_t) ** self.gamma
        loss = focal_weight * ce_loss

        # Apply alpha balancing if provided
        if self.alpha is not None:
            if isinstance(self.alpha, (float, int)):
                alpha_t = self.alpha
            else:
                alpha_t = self.alpha[targets]
            loss = alpha_t * loss

        if self.reduction == "mean":
            return loss.mean()
        elif self.reduction == "sum":
            return loss.sum()
        return loss

class BinaryFocalLoss(nn.Module):
    """Binary version of focal loss for object detection."""
    def __init__(self, gamma=2.0, alpha=0.25, reduction="sum"):
        super().__init__()
        self.gamma = gamma
        self.alpha = alpha
        self.reduction = reduction

    def forward(self, logits, targets):
        probs = torch.sigmoid(logits)
        # Binary cross-entropy
        ce_loss = F.binary_cross_entropy_with_logits(logits, targets, reduction="none")
        # Focal modulation
        p_t = probs * targets + (1 - probs) * (1 - targets)
        focal_weight = (1 - p_t) ** self.gamma
        alpha_t = self.alpha * targets + (1 - self.alpha) * (1 - targets)
        loss = alpha_t * focal_weight * ce_loss
        if self.reduction == "mean":
            return loss.mean()
        elif self.reduction == "sum":
            return loss.sum()
        return loss

class LossComparator:
    """Compare loss values across different losses."""
    def __init__(self, num_classes=10):
        self.num_classes = num_classes

    def compare(self, logits, targets):
        ce = CrossEntropyLoss(reduction="none")
        fl_05 = FocalLoss(gamma=0.5, reduction="none")
        fl_10 = FocalLoss(gamma=1.0, reduction="none")
        fl_20 = FocalLoss(gamma=2.0, reduction="none")

        ce_loss = ce(logits, targets)
        fl_losses = {
            "gamma=0.5": fl_05(logits, targets),
            "gamma=1.0": fl_10(logits, targets),
            "gamma=2.0": fl_20(logits, targets),
        }

        # Analyze per-example behavior
        probs = F.softmax(logits, dim=1)
        p_t = probs[torch.arange(logits.shape[0]), targets]

        print("Loss comparison by example difficulty:")
        print(f"{'p_t':>8} {'CE':>8} {'FL(0.5)':>8} {'FL(1.0)':>8} {'FL(2.0)':>8}")
        for i in range(len(p_t)):
            print(f"{p_t[i].item():>8.4f} {ce_loss[i].item():>8.4f} "
                  f"{fl_losses['gamma=0.5'][i].item():>8.4f} "
                  f"{fl_losses['gamma=1.0'][i].item():>8.4f} "
                  f"{fl_losses['gamma=2.0'][i].item():>8.4f}")

    def analyze_imbalance(self):
        """Show how focal loss handles class imbalance."""
        probs = torch.linspace(0.01, 0.99, 100)
        ce_loss = -torch.log(probs)
        fl_losses = {}
        for gamma in [0.5, 1.0, 2.0, 5.0]:
            fl_losses[gamma] = -((1 - probs) ** gamma) * torch.log(probs)
        return probs, ce_loss, fl_losses
```

## Interviewer Feedback
"Excellent implementation with both multi-class and binary variants. Your understanding of how gamma down-weights easy examples and alpha balances classes is precise. The comparison across gamma values shows practical insight."

## Key Takeaways
- Cross-entropy: -log(p_t) — treats all examples equally
- Focal loss adds (1 - p_t)^gamma to reduce loss for well-classified examples
- gamma > 1 focuses training on hard, misclassified examples
- alpha parameter handles class frequency imbalance
- Binary focal loss is used in object detection (RetinaNet) with gamma=2, alpha=0.25
