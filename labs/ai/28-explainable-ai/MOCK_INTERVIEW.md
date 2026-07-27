# Mock Interview: Explainable AI

**Topic:** Explain SHAP, LIME, integrated gradients — comparison

## Core Questions

### Q1: What is the goal of XAI?

**Answer:**
Explainable AI aims to make ML model decisions interpretable to humans.

**Types of explanations:**
- **Global:** Overall model behavior (feature importance, partial dependence)
- **Local:** Why a specific prediction was made
- **Counterfactual:** What would change the prediction

**Desirable properties:**
- **Fidelity:** Explanation matches model behavior
- **Interpretability:** Human-understandable
- **Completeness:** Accounts for all model behavior
- **Consistency:** Similar inputs get similar explanations

### Q2: Explain LIME.

**Answer:**
**LIME (Local Interpretable Model-agnostic Explanations):**

For a given prediction $f(x)$, approximate the local decision boundary with an interpretable surrogate model $g$.

```
Process:
1. Generate perturbed samples around x (x' by masking features)
2. Get predictions f(x') from black-box model
3. Weight samples by proximity to x (exponential kernel)
4. Train weighted linear model (or decision tree) g on (x', f(x'))
5. Coefficients of g = feature importances for this prediction
```

**Limitations:**
- Unstable: different perturbation seeds give different explanations
- Assumes local linearity (may not hold)
- Kernel width $\sigma$ is a sensitive hyperparameter
- Can be computationally expensive per prediction

### Q3: Explain SHAP.

**Answer:**
**SHAP (SHapley Additive exPlanations)** is a unified framework based on Shapley values from cooperative game theory.

**Shapley value for feature $i$:**
$\phi_i = \sum_{S \subseteq N \setminus \{i\}} \frac{|S|! (|N| - |S| - 1)!}{|N|!} [f(S \cup \{i\}) - f(S)]$

**Properties (unique to Shapley):**
- **Efficiency:** $\sum \phi_i = f(x) - \mathbb{E}[f]$
- **Symmetry:** Equal contribution for equal impact features
- **Dummy:** Features with no impact get $\phi_i = 0$
- **Additivity:** Shapley values of sum of models = sum of Shapley values

**SHAP approximates Shapley values efficiently:**
- **TreeSHAP:** Exact for tree-based models ($O(TLD)$)
- **KernelSHAP:** Model-agnostic (LIME-style with Shapley kernel)
- **DeepSHAP:** Deep learning specific (backprop-based approximation)

### Q4: Explain Integrated Gradients.

**Answer:**
**Integrated Gradients (IG)** attributes a model's prediction to input features by integrating gradients along a path from a baseline to the input.

$\text{IG}_i(x) = (x_i - x_i') \times \int_{\alpha=0}^{1} \frac{\partial f(x' + \alpha(x - x'))}{\partial x_i} d\alpha$

**Properties:**
- **Sensitivity:** Features that differ from baseline get non-zero attribution
- **Implementation invariance:** Identical models give identical attributions
- **Completeness:** $\sum \text{IG}_i = f(x) - f(x')$

**Practical considerations:**
- Baseline choice matters (zero, blur, random noise, or class-specific baseline)
- Riemann sum approximation with $k$ steps (typically 20-300)
- Gradient noise can be reduced with expected gradients (multiple baselines)

### Q5: Compare all three methods.

| Aspect | LIME | SHAP | Integrated Gradients |
|--------|------|------|---------------------|
| **Type** | Local surrogate | Game-theoretic | Gradient-based |
| **Model-agnostic** | Yes | Yes (KernelSHAP) | No (needs gradients) |
| **Local/Global** | Local | Both | Local |
| **Theoretical guarantees** | None | Strong (Shapley axioms) | Strong (attribution axioms) |
| **Speed** | Fast per point | Slow (exact), fast (approx) | Moderate |
| **Stability** | Low | High | Moderate |
| **Suitable for** | Any model | Any model (TreeSHAP fast for GBMs) | Deep learning (images, text) |
| **Fidelity** | Moderate | High | High |

### Q6: When to use each?

**Answer:**
- **SHAP:** Default XAI method. Best theoretical foundation. TreeSHAP for XGBoost/LightGBM. KernelSHAP for any model.
- **LIME:** Quick debugging when model-agnostic approximation is sufficient. Faster than KernelSHAP but less reliable.
- **Integrated Gradients:** Deep learning models, especially for images and text. Natural for differentiable models.
- **Additional methods:**
  - **Grad-CAM:** For CNN image classification — class activation maps
  - **Partial dependence plots:** Global feature effects (shows average relationship)
  - **Permutation importance:** Global feature importance (model-agnostic)
  - **Anchors:** High-precision if-then rules for classification

## Advanced

- **Shapley value limitations:** Assumes features are independent — correlated features get split arbitrarily
- **Conditional Shapley (SHAP with dependence):** Accounts for feature correlations but loses additivity
- **Interventional vs. observational Shapley:** Interventional (standard SHAP) vs. conditional (removes correlation effect)
- **Counterfactual explanations:** Find minimal change to flip prediction — useful for recourse
