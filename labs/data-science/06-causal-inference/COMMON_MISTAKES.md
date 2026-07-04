# Common Mistakes in Causal Inference

## 1. Adjusting for a Collider

Conditioning on a common effect of treatment and outcome creates selection bias.

**Example**: Studying the effect of education on earnings, conditioning on "currently employed" (a collider). Both low education and low earnings can cause unemployment → conditioning on employed creates a spurious link.

**Fix**: Draw the DAG before deciding what to adjust for. Never adjust for colliders.

## 2. Adjusting for a Mediator

Controlling for a variable on the causal path between treatment and outcome removes part of the treatment effect.

**Example**: Estimating the total effect of a drug on recovery, but controlling for "blood pressure" (which the drug affects, and which affects recovery).

**Fix**: Only adjust for confounders (causes of both treatment and outcome), not mediators.

## 3. Assuming Unconfoundedness

Believing you've controlled for all confounders when you haven't. This is the most fundamental assumption and cannot be tested from data — it must be justified by domain knowledge.

**Fix**: Sensitivity analysis. "How strong would an unobserved confounder need to be to change my conclusion?"

## 4. Interpreting ATE as CATE

The average treatment effect may differ substantially from conditional effects. A treatment that helps on average may harm specific subgroups.

**Fix**: Estimate CATE (Conditional Average Treatment Effect) by subgroup analysis or causal forests.

## 5. Poor Overlap (Extrapolation)

Matching or adjusting outside the common support region relies on extrapolation from the model.

**Fix**: Trim the sample to the region of common support (drop units with propensity scores near 0 or 1).

## 6. p-Value Fishing with Multiple Causal Questions

Testing 100 different causal claims and reporting only the significant ones.

**Fix**: Pre-register the causal analysis plan. Adjust for multiple comparisons.
