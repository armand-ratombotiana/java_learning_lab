# Mock Interview: Implement Quantization and Knowledge Distillation

## Scenario
You are interviewing for a ML engineer role at an edge deployment company. They need to compress a large model for mobile deployment.

## Interviewer Opening Question
"Implement post-training quantization and knowledge distillation from scratch. How would you deploy a 500MB model to a device with only 100MB of memory?"

## Candidate Response
"I'd use three techniques: (1) Knowledge distillation — train a smaller student model to mimic a larger teacher. (2) Post-training quantization (PTQ) — reduce weights from FP32 to INT8 or INT4. (3) Pruning — remove unimportant weights. Together, these can reduce model size by 10-20x with minimal accuracy loss."

## Interviewer Probing Questions

**Q: What's the difference between PTQ and QAT?**
"PTQ quantizes a pre-trained model using calibration data without retraining. QAT (Quantization-Aware Training) simulates quantization during training, generally achieving better accuracy at the cost of additional training time."

**Q: How do you choose the student architecture for distillation?**
"Match the student's receptive field and inductive biases to the teacher. A common choice is 2-4x fewer parameters, same architectural family. For example, a TinyBERT student for a BERT-Base teacher."

**Q: What's the temperature parameter in distillation?**
"Temperature T softens the softmax outputs: softmax(logits / T). Higher T produces softer probability distributions that reveal the teacher's dark knowledge (relative similarities between non-target classes). T=1 is standard; T=2-8 are common for distillation."

## Candidate Solution (Python)

```python
import torch
import torch.nn as nn
import torch.nn.functional as F
import numpy as np
from typing import Optional

class KnowledgeDistillationLoss(nn.Module):
    """Distillation loss with temperature scaling."""
    def __init__(self, temperature=4.0, alpha=0.5):
        super().__init__()
        self.temperature = temperature
        self.alpha = alpha
        self.ce_loss = nn.KLDivLoss(reduction="batchmean")

    def forward(self, student_logits, teacher_logits, true_labels):
        # Soft targets from teacher
        teacher_soft = F.softmax(teacher_logits / self.temperature, dim=1)
        student_log = F.log_softmax(student_logits / self.temperature, dim=1)
        distill_loss = self.ce_loss(student_log, teacher_soft) * (self.temperature ** 2)

        # Hard target loss
        hard_loss = F.cross_entropy(student_logits, true_labels)

        return self.alpha * hard_loss + (1 - self.alpha) * distill_loss

class KnowledgeDistillation:
    """Train a student model using knowledge distillation."""
    def __init__(self, teacher: nn.Module, student: nn.Module,
                 temperature=4.0, alpha=0.5):
        self.teacher = teacher.eval()
        self.student = student
        self.criterion = KnowledgeDistillationLoss(temperature, alpha)

    def train_epoch(self, train_loader, optimizer, device="cuda"):
        self.student.train()
        total_loss = 0.0
        for x, y in train_loader:
            x, y = x.to(device), y.to(device)
            optimizer.zero_grad()
            with torch.no_grad():
                teacher_logits = self.teacher(x)
            student_logits = self.student(x)
            loss = self.criterion(student_logits, teacher_logits, y)
            loss.backward()
            optimizer.step()
            total_loss += loss.item()
        return total_loss / len(train_loader)

class PostTrainingQuantization:
    """Post-training quantization to INT8."""
    def __init__(self, model: nn.Module, calibration_loader):
        self.model = model
        self.calibration_loader = calibration_loader
        self.quantized_state = {}

    def _compute_scale_and_zero(self, tensor, symmetric=False):
        min_val = tensor.min()
        max_val = tensor.max()
        if symmetric:
            max_abs = max(abs(min_val), abs(max_val))
            scale = max_abs / 127.0
            zero_point = 0
        else:
            scale = (max_val - min_val) / 255.0
            zero_point = int(-min_val / scale) if scale > 0 else 0
            zero_point = max(0, min(255, zero_point))
        return scale, zero_point

    def quantize_weights(self, symmetric=False):
        """Quantize all linear/conv weights to INT8."""
        quantized_model = {}
        for name, param in self.model.named_parameters():
            if param.dim() >= 2:  # Weights, not bias
                scale, zero_point = self._compute_scale_and_zero(param.data, symmetric)
                if symmetric:
                    q_data = torch.clamp(torch.round(param.data / scale), -127, 127).char()
                else:
                    q_data = torch.clamp(torch.round(param.data / scale) + zero_point, 0, 255).uchar()
                quantized_model[name] = {
                    "q_data": q_data,
                    "scale": scale,
                    "zero_point": zero_point,
                    "shape": param.shape,
                    "dtype": "int8_sym" if symmetric else "int8_asym"
                }
                # Dequantize for comparison
                if symmetric:
                    dq_data = q_data.float() * scale
                else:
                    dq_data = (q_data.float() - zero_point) * scale
                quantized_model[name]["dequantized"] = dq_data
            else:
                quantized_model[name] = {
                    "q_data": param.data,
                    "scale": 1.0,
                    "zero_point": 0,
                    "shape": param.shape,
                    "dtype": "fp32"
                }
        return quantized_model

    def quantize_activations(self, symmetric=False):
        """Calibrate activation quantization ranges."""
        activation_ranges = {}
        hooks = []

        def get_activation_hook(name):
            def hook(module, input, output):
                if name not in activation_ranges:
                    activation_ranges[name] = {"min": [], "max": []}
                activation_ranges[name]["min"].append(output.min().item())
                activation_ranges[name]["max"].append(output.max().item())
            return hook

        for name, module in self.model.named_modules():
            if isinstance(module, (nn.Linear, nn.Conv2d)):
                hooks.append(module.register_forward_hook(get_activation_hook(name)))

        self.model.eval()
        with torch.no_grad():
            for x, _ in self.calibration_loader:
                self.model(x)

        for hook in hooks:
            hook.remove()

        # Aggregate ranges
        for name in activation_ranges:
            ranges = activation_ranges[name]
            overall_min = min(ranges["min"])
            overall_max = max(ranges["max"])
            scale, zp = self._compute_scale_and_zero(
                torch.tensor([overall_min, overall_max]), symmetric)
            activation_ranges[name] = {"scale": scale, "zero_point": zp}
        return activation_ranges

class Pruning:
    """Simple magnitude-based pruning."""
    def __init__(self, model, prune_ratio=0.3):
        self.model = model
        self.prune_ratio = prune_ratio
        self.masks = {}

    def create_masks(self):
        for name, param in self.model.named_parameters():
            if param.dim() >= 2:
                threshold = torch.quantile(param.data.abs(), self.prune_ratio)
                self.masks[name] = param.data.abs() > threshold

    def apply_pruning(self):
        self.create_masks()
        for name, param in self.model.named_parameters():
            if name in self.masks:
                param.data *= self.masks[name]

class ModelCompressor:
    def __init__(self, teacher, student, calibration_loader):
        self.teacher = teacher
        self.student = student
        self.calibration_loader = calibration_loader

    def compress(self, train_loader=None, device="cuda"):
        report = {}

        # Step 1: Knowledge distillation (if training data available)
        if train_loader is not None:
            distiller = KnowledgeDistillation(self.teacher, self.student)
            optimizer = torch.optim.AdamW(self.student.parameters(), lr=1e-4)
            initial_acc = self._evaluate(self.student)
            for epoch in range(5):
                loss = distiller.train_epoch(train_loader, optimizer, device)
            final_acc = self._evaluate(self.student)
            report["distillation"] = {
                "student_params": sum(p.numel() for p in self.student.parameters()),
                "teacher_params": sum(p.numel() for p in self.teacher.parameters()),
                "accuracy_before": initial_acc,
                "accuracy_after": final_acc
            }

        # Step 2: Quantization
        qt = PostTrainingQuantization(self.student, self.calibration_loader)
        quantized = qt.quantize_weights(symmetric=True)
        original_size = sum(p.numel() * 4 for p in self.student.parameters())
        quantized_size = sum(
            v["q_data"].numel() * (1 if v["dtype"] == "int8_sym" else 4)
            for v in quantized.values()
        )
        report["quantization"] = {
            "original_mb": original_size / 1e6,
            "quantized_mb": quantized_size / 1e6,
            "compression_ratio": original_size / quantized_size,
        }

        # Step 3: Pruning
        pruner = Pruning(self.student, prune_ratio=0.3)
        pruner.apply_pruning()
        nonzero = sum(p.count_nonzero().item() for p in self.student.parameters() if p.dim() >= 2)
        total = sum(p.numel() for p in self.student.parameters() if p.dim() >= 2)
        report["pruning"] = {"sparsity": 1 - nonzero / total}

        return report

    def _evaluate(self, model):
        model.eval()
        return 0.0  # Simplified
```

## Interviewer Feedback
"Comprehensive implementation covering the full compression toolkit: distillation, quantization, and pruning. Your understanding of temperature scaling, calibration, and symmetric vs asymmetric quantization is solid. This is a production-grade answer."

## Key Takeaways
- Knowledge distillation: student learns from teacher's soft targets
- Temperature controls softening of probability distributions
- PTQ: quantize weights to INT8/INT4 with calibration data
- QAT: simulate quantization during training for better accuracy
- Pruning: remove weights below a magnitude threshold
- Combining all three techniques achieves ~10-20x compression
