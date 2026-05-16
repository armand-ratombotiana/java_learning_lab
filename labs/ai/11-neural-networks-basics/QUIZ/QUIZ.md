# Neural Networks Basics - QUIZ

## Section 1: Perceptron

**Q1: A perceptron can learn which of these problems?**
- A) XOR
- B) AND
- C) Both AND and XOR
- D) None

**Q2: The perceptron learning rule updates weights by:**
- A) w = w - learning_rate * error * input
- B) w = w + learning_rate * error * input
- C) w = w * learning_rate * error
- D) w = w / learning_rate * error

**Q3: A single perceptron with step function creates a:**
- A) Circular decision boundary
- B) Linear decision boundary
- C) Curved decision boundary
- D) No decision boundary

**Q4: The bias in a perceptron allows:**
- A) The decision boundary to pass through the origin
- B) The decision boundary to shift from the origin
- C) Faster training
- D) More weights

**Q5: Perceptron convergence is guaranteed for:**
- A) Any dataset
- B) Linearly separable data
- C) Non-linearly separable data
- D) No data

## Section 2: Multi-Layer Perceptron

**Q6: Universal approximation theorem states:**
- A) Single perceptron can solve any problem
- B) Single hidden layer can approximate any continuous function
- C) MLPs cannot approximate any function
- D) Deep networks are always better than shallow

**Q7: Hidden layers in an MLP are:**
- A) Input and output only
- B) Between input and output layers
- C) Always at the output
- D) Optional

**Q8: Fully connected means:**
- A) Every neuron connects to all neurons in next layer
- B) Every neuron connects to previous layer only
- C) No connections
- D) Random connections

**Q9: Increasing network depth typically:**
- A) Decreases model capacity
- B) Increases hierarchical feature learning
- C) Removes non-linearity
- D) Simplifies gradients

**Q10: Width vs Depth:**
- A) Wide networks are always better
- B) Deep networks learn hierarchical representations
- C) Both are equivalent
- D) Depth doesn't matter

## Section 3: Activation Functions

**Q11: Sigmoid output range is:**
- A) (-1, 1)
- B) (0, 1)
- C) (0, infinity)
- D) (-infinity, infinity)

**Q12: The vanishing gradient problem is worst with:**
- A) ReLU
- B) Sigmoid
- C) Leaky ReLU
- D) SELU

**Q13: ReLU(x) =**
- A) x
- B) max(0, x)
- C) e^x
- D) 1 / (1 + e^-x)

**Q14: Tanh is:**
- A) Shifted sigmoid
- B) Zero-centered sigmoid
- C) Always positive
- D) Non-differentiable

**Q15: Softmax is used for:**
- A) Regression
- B) Binary classification
- C) Multi-class classification
- D) Clustering

## Section 4: Loss Functions

**Q16: Cross-entropy is preferred for classification because:**
- A) It's simpler than MSE
- B) It has better gradient properties
- C) It always converges faster
- D) It doesn't need gradients

**Q17: MSE is best for:**
- A) Classification
- B) Regression
- C) Clustering
- D) Ranking

**Q18: Hinge loss is used in:**
- A) Logistic Regression
- B) Support Vector Machines
- C) K-Means
- D) Decision Trees

**Q19: Cross-entropy loss for perfect prediction is:**
- A) 1
- B) 0
- C) Infinity
- D) -1

**Q20: When predictions are confident but wrong, cross-entropy:**
- A) Gives small penalty
- B) Gives large penalty
- C) No penalty
- D) Negative penalty

## Section 5: Initialization & Architecture

**Q21: Xavier initialization is designed for:**
- A) ReLU activations
- B) Sigmoid and tanh activations
- C) Softmax only
- D) No activation

**Q22: He initialization is designed for:**
- A) Sigmoid activations
- B) ReLU activations
- C) Tanh activations
- D) Linear activations

**Q23: Too small initialization leads to:**
- A) Exploding gradients
- B) Vanishing gradients
- C) No effect
- D) Instant convergence

**Q24: For a 4-class classification, output layer should have:**
- A) 1 neuron
- B) 2 neurons
- C) 4 neurons
- D) Any number

**Q25: Dropout primarily helps with:**
- A) Underfitting
- B) Overfitting
- C) Slow training
- D) Gradient problems

---

## Answers

1. B (AND is linearly separable)
2. B
3. B
4. B
5. B
6. B
7. B
8. A
9. B
10. B
11. B
12. B
13. B
14. B
15. C
16. B
17. B
18. B
19. B
20. B
21. B
22. B
23. B
24. C
25. B