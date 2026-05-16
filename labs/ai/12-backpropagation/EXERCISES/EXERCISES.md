# Backpropagation - EXERCISES

## Exercise 1: Chain Rule Application
Compute the gradient of L with respect to x where:
- L = f(g(h(x)))
- h(x) = 2x
- g(h) = h²
- f(g) = log(g)

```java
public double chainRuleGradient(double x) {
    double h = 2 * x;           // dh/dx = 2
    double g = h * h;           // dg/dh = 2h = 4x
    double f = Math.log(g);     // df/dg = 1/g = 1/(4x²)
    double dL_dx = (1/g) * (2 * h) * 2;
    return dL_dx;
}
// dL/dx = 4/(x) at x > 0
```

## Exercise 2: Compute Delta for Sigmoid
Given upstream gradient = [0.5, -0.3] and pre-activation z = [0, 0.5], compute the delta.

```java
public double[] computeDeltaSigmoid(double[] upstreamGrad, double[] z) {
    double[] delta = new double[z.length];
    for (int i = 0; i < z.length; i++) {
        double sigma = 1.0 / (1.0 + Math.exp(-z[i]));
        double dsigma = sigma * (1 - sigma);
        delta[i] = upstreamGrad[i] * dsigma;
    }
    return delta;
}
// delta = [0.5 * 0.25, -0.3 * 0.375] = [0.125, -0.1125]
```

## Exercise 3: Weight Gradient
Compute weight gradient for a connection between neuron j in layer l-1 and neuron i in layer l:
- delta_i = 0.2
- a_j = 0.5

```java
public double computeWeightGradient(double delta, double activation) {
    return delta * activation;
}
// dW = 0.2 * 0.5 = 0.1
```

## Exercise 4: Backprop Through Two Layers
Compute gradient w.r.t. input x for a 2-layer network:
- Layer 1: W1 = [[0.5, 0.3], [0.2, 0.8]], x = [1, 0.5]
- Layer 2: W2 = [0.6, 0.4]
- upstream gradient = 1.0

```java
public double[] backpropTwoLayers(double[][] W1, double[] W2, double[] x, double upstreamGrad) {
    double[] z1 = new double[2];
    z1[0] = W1[0][0] * x[0] + W1[0][1] * x[1];
    z1[1] = W1[1][0] * x[0] + W1[1][1] * x[1];

    double[] a1 = new double[2];
    a1[0] = Math.max(0, z1[0]);  // ReLU
    a1[1] = Math.max(0, z1[1]);

    double delta2 = upstreamGrad;
    double[] delta1 = new double[2];
    delta1[0] = delta2 * W2[0] * (z1[0] > 0 ? 1 : 0);
    delta1[1] = delta2 * W2[1] * (z1[1] > 0 ? 1 : 0);

    double[] dX = new double[2];
    dX[0] = delta1[0] * W1[0][0] + delta1[1] * W1[1][0];
    dX[1] = delta1[0] * W1[0][1] + delta1[1] * W1[1][1];

    return dX;
}
```

## Exercise 5: Update Weights with Momentum
Update weights with momentum = 0.9:
- current velocity = [0.01, 0.02]
- gradient = [0.1, 0.15]
- learning rate = 0.1

```java
public double[] updateWithMomentum(double[] velocity, double[] gradient, double lr, double momentum) {
    double[] newVelocity = new double[velocity.length];
    for (int i = 0; i < velocity.length; i++) {
        newVelocity[i] = momentum * velocity[i] - lr * gradient[i];
    }
    return newVelocity;
}
// new v = [0.9*0.01 - 0.1*0.1, 0.9*0.02 - 0.1*0.15] = [-0.001, 0.015]
```

---

## Solutions

### Exercise 1:
Chain rule: dL/dx = dL/dg * dg/dh * dh/dx
- dL/dg = 1/g
- dg/dh = 2h
- dh/dx = 2
- At x=1: dL/dx = 4

### Exercise 2:
sigma(0) = 0.5, dsigma(0) = 0.25
sigma(0.5) ≈ 0.6225, dsigma ≈ 0.235
delta = [0.5*0.25, -0.3*0.235] = [0.125, -0.0705]

### Exercise 3:
dW = delta * activation = 0.2 * 0.5 = 0.1

### Exercise 4:
z1 = [0.65, 0.4], a1 = [0.65, 0.4]
delta2 = 1.0
delta1 = [0.39, 0.16]
dX = [0.2995, 0.247]

### Exercise 5:
v_new = 0.9 * v - 0.1 * grad
v_new[0] = -0.001, v_new[1] = 0.015