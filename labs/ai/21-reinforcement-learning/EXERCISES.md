# Exercises: Reinforcement Learning

## Overview
This document contains practice problems organized by difficulty. Attempt each problem before consulting the solution.

## Exercise 1: Conceptual Understanding (â˜…â˜†â˜†)
**Question**: Explain the key differences between Reinforcement Learning and traditional programming approaches. What types of problems is Reinforcement Learning best suited for?

**Solution**: Reinforcement Learning learns patterns from data rather than following explicit rules. It excels at problems where rules are difficult to specify, patterns are complex, or systems need to adapt. Traditional programming works better for well-understood problems with clear rules.

## Exercise 2: Mathematical Derivation (â˜…â˜…â˜†)
**Question**: Derive the gradient update rule for gradient descent. Show all steps.

**Solution**: 
Given loss L(Î¸) and learning rate Î·:
1. Compute gradient: g = âˆ‡L(Î¸)
2. Update: Î¸_new = Î¸_old - Î·Â·g
This follows from the first-order Taylor approximation: L(Î¸+Î”) â‰ˆ L(Î¸) + âˆ‡L(Î¸)Â·Î”. To decrease L, choose Î” = -Î·Â·âˆ‡L(Î¸).

## Exercise 3: Implementation (â˜…â˜…â˜†)
**Question**: Implement the forward pass of a basic Reinforcement Learning algorithm in Java. Include input validation.

**Solution**: See the implementation in src/main/java/com/ai/21/ for the complete solution.

## Exercise 4: Analysis (â˜…â˜…â˜…)
**Question**: Compare the bias-variance tradeoff for models of different complexity. Use a concrete example.

**Solution**: A high-bias model (e.g., linear regression for non-linear data) systematically misses patterns but has low variance across training sets. A high-variance model (e.g., deep decision tree) captures noise but predictions vary significantly with training data. The optimal model balances both sources of error.

## Exercise 5: Debugging (â˜…â˜…â˜†)
**Question**: Training loss is NaN. What are the possible causes and how would you debug each?

**Solution**: 
Causes: (1) Division by zero - check denominators in loss; (2) log(0) - add small epsilon; (3) Exploding gradients - clip gradients; (4) Learning rate too high - reduce and check loss pattern.
Debug: Add assertions, log gradients, test on synthetic data.

## Exercise 6: Performance (â˜…â˜…â˜…)
**Question**: Your implementation is too slow. Describe optimization strategies you would apply.

**Solution**: (1) Profile to identify bottlenecks; (2) Vectorize operations; (3) Reduce memory allocations; (4) Use appropriate data structures; (5) Parallelize independent operations; (6) Cache intermediate results.

## Exercise 7: Design (â˜…â˜…â˜…)
**Question**: Design a modular architecture for a Reinforcement Learning system that supports multiple algorithms and configurations.

**Solution**: Use Strategy pattern for algorithms, Factory for component creation, Builder for configuration. Separate data processing, model computation, training, and evaluation into distinct modules with clear interfaces.

## Exercise 8: Advanced (â˜…â˜…â˜…)
**Question**: How would you extend the basic Reinforcement Learning algorithm to handle streaming data?

**Solution**: Implement incremental learning where the model updates after each sample or mini-batch without retraining from scratch. Use techniques like elastic weight consolidation to prevent catastrophic forgetting, and implement concept drift detection to trigger adaptation when data distribution changes.
