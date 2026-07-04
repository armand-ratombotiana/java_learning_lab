# Distributed Transactions: Mathematical Foundation

## Atomicity Formalization

A transaction T is atomic if for any execution:

∀ resource r: either all updates by T on r are applied, or none are.

Formally: ∃ a global commit decision C ∈ {commit, abort} such that:
- If C = commit: all participants apply all changes
- If C = abort: no participant applies any change

## SAGA Correctness

A SAGA S = {T₁, C₁, T₂, C₂, ..., Tₙ, Cₙ} is correct if:

1. ∀ i: Tᵢ and Cᵢ are compensating (Cᵢ undoes Tᵢ's effects)
2. In a successful execution: T₁; T₂; ...; Tₙ
3. In a failed execution at step k: T₁; T₂; ...; Tₖ₋₁; Cₖ₋₁; ...; C₂; C₁

## 2PC Blocking Probability

P(block) = P(coordinator fails during uncertainty window)

In practice: uncertainty window = time between first prepare response and commit decision.
