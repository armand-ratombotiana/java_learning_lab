# Mock Interview: Implement Xavier and He Initialization — Why Does It Matter?

## Scenario
You are interviewing for a deep learning research role. They want to test your understanding of how initialization affects training dynamics.

## Interviewer Opening Question
"Implement Xavier (Glorot) and He initialization from scratch. Explain why initialization matters and when to use each."

## Candidate Response
"Xavier initialization sets weights with variance 1/(fan_in) for uniform or 2/(fan_in + fan_out) for normal distribution. He initialization uses variance 2/fan_in. Xavier was designed for tanh/sigmoid activations to maintain variance across layers. He initialization accounts for ReLU's zeroing of half the activations."

## Interviewer Probing Questions

**Q: What happens with poor initialization?**
"Too large: exploding activations and gradients, NaN loss. Too small: vanishing gradients, network barely learns. The goal is to keep the variance of activations and gradients roughly constant across layers."

**Q: How did you derive the Xavier variance?**
"Assume linear activations with identical fan_in = fan_out. Var(y) = fan_in * Var(w) * Var(x). To keep Var(y) = Var(x), need Var(w) = 1/fan_in. For backprop: Var(dL/dx) = fan_out * Var(w) * Var(dL/dy), so Var(w) = 1/fan_out. Harmonic mean: Var(w) = 2/(fan_in + fan_out)."

**Q: What initialization does PyTorch use by default?**
"nn.Linear uses Kaiming (He) uniform with gain = sqrt(5) for the uniform bound. nn.Conv2d uses He normal. Embedding uses normal(0, 1). The specific defaults depend on the module."

## Candidate Solution (Python)

```python
import torch
import torch.nn as nn
import numpy as np

class XavierInitialization:
    @staticmethod
    def normal(tensor, gain=1.0):
        fan_in, fan_out = XavierInitialization._fan_in_fan_out(tensor)
        std = gain * np.sqrt(2.0 / (fan_in + fan_out))
        return nn.init.normal_(tensor, 0, std)

    @staticmethod
    def uniform(tensor, gain=1.0):
        fan_in, fan_out = XavierInitialization._fan_in_fan_out(tensor)
        limit = gain * np.sqrt(6.0 / (fan_in + fan_out))
        return nn.init.uniform_(tensor, -limit, limit)

    @staticmethod
    def _fan_in_fan_out(tensor):
        dimensions = tensor.dim()
        if dimensions < 2:
            raise ValueError("Fan in/out require at least 2 dimensions")
        num_input_maps = tensor.shape[1]
        num_output_maps = tensor.shape[0]
        receptive_field_size = 1
        if dimensions > 2:
            receptive_field_size = np.prod(tensor.shape[2:])
        fan_in = num_input_maps * receptive_field_size
        fan_out = num_output_maps * receptive_field_size
        return fan_in, fan_out

class HeInitialization:
    @staticmethod
    def normal(tensor, nonlinearity="relu"):
        fan_in, _ = HeInitialization._fan_in_fan_out(tensor)
        gain = nn.init.calculate_gain(nonlinearity)
        std = gain / np.sqrt(fan_in)
        return nn.init.normal_(tensor, 0, std)

    @staticmethod
    def uniform(tensor, nonlinearity="relu"):
        fan_in, _ = HeInitialization._fan_in_fan_out(tensor)
        gain = nn.init.calculate_gain(nonlinearity)
        limit = gain * np.sqrt(3.0 / fan_in)
        return nn.init.uniform_(tensor, -limit, limit)

    @staticmethod
    def _fan_in_fan_out(tensor):
        dimensions = tensor.dim()
        num_input_maps = tensor.shape[1] if dimensions > 1 else 1
        num_output_maps = tensor.shape[0]
        receptive_field_size = 1
        if dimensions > 2:
            receptive_field_size = np.prod(tensor.shape[2:])
        fan_in = num_input_maps * receptive_field_size
        fan_out = num_output_maps * receptive_field_size
        return fan_in, fan_out

def initialize_model(model, method="he"):
    """Initialize all linear and conv layers."""
    for name, param in model.named_parameters():
        if "weight" in name and param.dim() >= 2:
            if method == "xavier":
                XavierInitialization.uniform(param)
            elif method == "he":
                HeInitialization.uniform(param)
        elif "bias" in name:
            nn.init.zeros_(param)

class VariancePropagationAnalyzer:
    """Analyze how variance propagates through the network."""
    def __init__(self, model):
        self.model = model

    def analyze(self, x, method="he"):
        initialize_model(self.model, method)
        self.model.eval()
        variances = {"input": x.var().item()}
        with torch.no_grad():
            for name, module in self.model.named_modules():
                if isinstance(module, (nn.Linear, nn.Conv2d)):
                    x = module(x)
                    if isinstance(module, nn.Linear):
                        act_fn = "linear"
                    else:
                        act_fn = "conv"
                    variances[f"{name}_{act_fn}"] = x.var().item()
                    if hasattr(module, "activation"):
                        x = module.activation(x)
                        variances[f"{name}_after_act"] = x.var().item()
        return variances

def initialization_comparison():
    """Empirical comparison of initialization methods."""
    torch.manual_seed(42)
    width = 512
    depth = 20
    x = torch.randn(64, width)

    model_he = nn.Sequential(*[
        nn.Linear(width, width, bias=False) for _ in range(depth)
    ])
    model_xavier = nn.Sequential(*[
        nn.Linear(width, width, bias=False) for _ in range(depth)
    ])

    initialize_model(model_he, "he")
    initialize_model(model_xavier, "xavier")

    model_he.eval()
    model_xavier.eval()
    with torch.no_grad():
        out_he = x
        out_xav = x
        he_vars = [out_he.var().item()]
        xav_vars = [out_xav.var().item()]
        for layer_he, layer_xav in zip(model_he, model_xavier):
            out_he = F.relu(layer_he(out_he))
            out_xav = torch.tanh(layer_xav(out_xav))
            he_vars.append(out_he.var().item())
            xav_vars.append(out_xav.var().item())

        print("Variance across layers:")
        for i, (hv, xv) in enumerate(zip(he_vars, xav_vars)):
            print(f"Layer {i:2d}: He(ReLU)={hv:.4f}, Xavier(Tanh)={xv:.4f}")

        print(f"\nFinal variance ratio: He={he_vars[-1]/he_vars[0]:.4f}, "
              f"Xavier={xav_vars[-1]/xav_vars[0]:.4f}")

# Demonstrate exploding/vanishing with bad init
def demonstrate_bad_init():
    width = 256
    depth = 30
    x = torch.randn(64, width)
    model = nn.Sequential(*[
        nn.Linear(width, width, bias=False) for _ in range(depth)
    ])
    # Bad initialization: normal(0, 3)
    for p in model.parameters():
        nn.init.normal_(p, 0, 3)
    model.eval()
    with torch.no_grad():
        out = x
        for i, layer in enumerate(model):
            out = F.relu(layer(out))
            if out.isnan().any():
                print(f"NaN at layer {i} — exploding gradients!")
                break
            var = out.var().item()
            if var > 1e10 or var < 1e-10:
                print(f"Layer {i}: var={var:.2e} {'EXPLODING' if var > 1e10 else 'VANISHING'}")
```

## Interviewer Feedback
"Excellent implementation and derivation. Your variance propagation analysis clearly demonstrates why proper initialization matters. The comparison of He for ReLU and Xavier for tanh shows you understand the activation-specific reasoning."

## Key Takeaways
- Xavier: Var(w) = 2/(fan_in + fan_out) — designed for tanh/sigmoid
- He: Var(w) = 2/fan_in — accounts for ReLU's zeroing of half activations
- Proper initialization keeps activation and gradient variance constant
- Bad initialization causes exploding or vanishing gradients
- Activation function choice determines the right initialization method
