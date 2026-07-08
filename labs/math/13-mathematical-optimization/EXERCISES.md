# Exercises: Mathematical Optimization

## Theoretical Exercises

### Problem 1: Convexity
Show that f(x) = x² is convex. Prove that the sum of convex functions is convex.

### Problem 2: Gradient Descent Convergence
For f(x) = (1/2)x², derive the iteration of gradient descent with step α. For what range of α does the iteration converge?

### Problem 3: Newton's Method
Show that Newton's method applied to f(x) = x² - a gives the iteration x_{k+1} = (x_k + a/x_k)/2 (Heron's method). What is the convergence order?

### Problem 4: KKT Conditions
Derive the KKT conditions for minimizing f(x,y) = x² + y² subject to x + y = 1.

### Problem 5: Duality
Formulate the dual of the primal problem: min f(x) subject to g(x) ≤ 0. Explain weak and strong duality.

### Problem 6: BFGS
Explain why BFGS maintains positive definiteness of the Hessian approximation. What happens with poor line search?

### Problem 7: Conjugate Gradient
Prove that conjugate gradient converges in at most n iterations for an n-dimensional quadratic problem.

### Problem 8: Stochastic Gradient
Compare the convergence properties of SGD with full-batch gradient descent. Why is SGD preferred for large datasets?

## Programming Exercises

### Exercise 1: Gradient Descent
Implement gradient descent for f(x,y) = x² + y². Plot the convergence path.

### Exercise 2: Momentum
Add momentum to gradient descent. Compare convergence speed with and without momentum for the Rosenbrock function.

### Exercise 3: Nesterov Acceleration
Implement Nesterov accelerated gradient. Compare its convergence to standard momentum.

### Exercise 4: AdaGrad
Implement AdaGrad for a sparse optimization problem. Show that it handles sparse gradients better than vanilla GD.

### Exercise 5: Adam
Implement the Adam optimizer. Test it on a non-convex function with multiple local minima.

### Exercise 6: Newton's Method
Implement Newton's method for optimization in 1D. Test on f(x) = x⁴ - 3x³ + 2.

### Exercise 7: Golden Section Search
Find the minimum of f(x) = sin(x) + sin(2x) on [0, 2π] using golden section search.

### Exercise 8: BFGS
Implement BFGS for the Rosenbrock function. Compare convergence speed against gradient descent.

### Exercise 9: Numerical Gradient
Compare forward, central, and complex-step differentiation for gradient computation. Which is most accurate?

### Exercise 10: Benchmark
Benchmark all optimizers on several test functions (quadratic, Rosenbrock, Rastrigin). Create a convergence comparison table.

## Mini-Project: Logistic Regression
Implement logistic regression using gradient descent. Train on a binary classification dataset and visualize the decision boundary.

## Real-World Project: Neural Network Trainer
Build a minimal neural network training framework implementing backpropagation with multiple optimizer choices (SGD, Momentum, Adam). Train on MNIST digit classification.
