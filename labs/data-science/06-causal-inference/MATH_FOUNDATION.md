# Math Foundation: Causal Inference

## 1. Average Treatment Effect (ATE)

$$ \tau_{ATE} = E[Y(1) - Y(0)] $$

The expected difference in outcomes if everyone were treated vs. everyone untreated.

## 2. Conditional Average Treatment Effect (CATE)

$$ \tau(x) = E[Y(1) - Y(0) | X = x] $$

The treatment effect for units with specific characteristics x. Used for personalized treatment decisions.

## 3. Propensity Score

$$ e(X) = P(T = 1 | X = x) $$

Under **unconfoundedness** ($Y(1), Y(0) \perp T | X$), conditioning on the propensity score is sufficient to remove confounding bias.

## 4. Difference-in-Differences (DiD)

$$ \tau_{DiD} = (E[Y_{post}|T=1] - E[Y_{pre}|T=1]) - (E[Y_{post}|T=0] - E[Y_{pre}|T=0]) $$

Requires **parallel trends assumption**: in the absence of treatment, the treatment group would have followed the same trend as the control group.

## 5. Instrumental Variables (IV)

$$ \tau_{IV} = \frac{Cov(Z, Y)}{Cov(Z, T)} $$

Z must satisfy:
- **Relevance**: $Cov(Z, T) \neq 0$ (Z predicts treatment)
- **Exclusion**: Z affects Y only through T (no direct path)
- **Independence**: Z is as good as randomly assigned given X

## 6. Backdoor Criterion

A set of variables W satisfies the backdoor criterion for (X, Y) if:
1. No node in W is a descendant of X
2. W blocks every path between X and Y that contains an arrow into X

If W satisfies the backdoor criterion: $P(Y|do(X)) = \sum_w P(Y|X, W = w) P(W = w)$
