# Perceptron Theory & Intuition

## 💡 Biological Inspiration
The human brain consists of billions of interconnected neurons. A biological neuron receives electrical signals from other neurons through its dendrites. If the total incoming signal is strong enough, the neuron "fires" and sends an electrical spike down its axon to other neurons.

In 1957, Frank Rosenblatt invented the **Perceptron**—a mathematical model inspired by this biological process. It is the simplest type of Artificial Neural Network, consisting of just a single artificial neuron.

## ⚙️ How It Works
A Perceptron takes multiple numerical inputs, applies weights to them, adds a bias, and passes the result through an activation function to produce a single output.

1. **Inputs ($x$)**: The features of your data (e.g., pixel intensities, size of a house).
2. **Weights ($w$)**: The importance of each input. If a weight is large, that input has a strong influence on the neuron's decision.
3. **Bias ($b$)**: A constant value added to the sum. It shifts the activation threshold. Think of it as the neuron's innate tendency to fire even when inputs are zero.
4. **Weighted Sum**: The Perceptron multiplies each input by its corresponding weight and sums them all up, adding the bias.
5. **Activation Function**: A mathematical function that decides whether the neuron should "fire" (output 1) or not (output 0). The classic Perceptron uses a Step Function.

## 🚧 The Limitation: Linear Separability
The Perceptron is a **linear classifier**. It draws a straight line (or hyperplane in higher dimensions) to separate data into two classes.
- It can easily solve the logical AND problem or OR problem.
- **The XOR Problem**: It is mathematically impossible for a single Perceptron to solve the XOR (Exclusive OR) problem because the data points cannot be separated by a single straight line. This limitation famously caused the "AI Winter" in the 1970s until Multi-Layer Perceptrons (MLPs) and Backpropagation were developed.