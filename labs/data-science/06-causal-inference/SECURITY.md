# Security in Causal Inference

## 1. Causal Claims as Attack Surface

If a high-stakes decision (hiring, lending, sentencing) is based on a claimed causal effect, adversarial parties may exploit the analysis.

**Example**: A company claims "our training program increases salary by $5K." An employee denied a promotion could sue, challenging the causal analysis. If the analysis didn't properly control for confounders, the company loses.

## 2. Causal Effect Manipulation

If a causal model drives automated decisions, actors who understand the model can manipulate inputs to get desired outcomes.

## 3. Simpson's Paradox in Reporting

Presenting aggregate causal estimates that hide subgroup heterogeneity (confounding by group) can be unintentionally misleading or intentionally deceptive.

**Mitigation**: Always report subgroup effects alongside aggregate estimates. Pre-specify subgroups in the analysis plan.

## 4. Data Snooping in Causal Discovery

Automated causal discovery algorithms (PC algorithm, FCI) test many conditional independencies. Without correction, false discoveries are guaranteed.

**Mitigation**: Use stability selection or bootstrap confidence for discovered edges.

## 5. Transparency in Causal Assumptions

All causal claims rest on untestable assumptions (unconfoundedness, parallel trends, exclusion restriction). A secure analysis makes these assumptions explicit and tests sensitivity.

```java
// Document assumptions as code
public class CausalAssumptions {
    public static final String[] ASSUMPTIONS = {
        "Unconfoundedness: no unobserved confounders",
        "Positivity: 0 < P(T=1|X) < 1 for all X",
        "Consistency: Y = Y(T) (well-defined treatment)",
        "No interference: units' outcomes don't affect each other"
    };
    
    public static void reportAssumptions() {
        for (String a : ASSUMPTIONS) {
            System.out.println("Assumption: " + a);
            System.out.println("  Justification: ...");
            System.out.println("  Sensitivity test: ...");
        }
    }
}
```
