# Lab 06: Interview Questions

## Q1: How does LoRA reduce memory usage during fine-tuning?
**A:** Instead of updating the full d×d weight matrix, LoRA learns two low-rank matrices A (d×r) and B (r×d) where r << d. Only 2rd parameters are trained instead of d^2.

## Q2: What typical rank values are used for LoRA and why?
**A:** r = 8–64. Lower ranks are more parameter-efficient; higher ranks capture more task-specific information. The optimal rank depends on task complexity.

## Q3: Explain the LoRA scaling factor (alpha/r).
**A:** The LoRA update is scaled by `alpha / r`. Alpha controls the contribution of the adapter. Higher alpha = stronger adaptation. Typically alpha = 2r or alpha = r.

## Q4: How does QLoRA differ from LoRA?
**A:** QLoRA quantizes the frozen base model weights (e.g., to 4-bit NormalFloat) while keeping LoRA adapters in FP16. This allows fine-tuning large models on a single GPU.

## Q5: Can LoRA adapters be merged? What are the trade-offs?
**A:** Yes, `W_merged = W + (alpha/r) * AB`. Merging eliminates inference overhead but prevents task-switching without reloading. Unmerged adapters allow dynamic task-switching.
