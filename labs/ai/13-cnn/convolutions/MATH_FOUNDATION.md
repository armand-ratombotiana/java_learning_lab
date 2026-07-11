# Mathematical Foundation of 2D Convolutions

## 📐 The Discrete 2D Convolution Operation
In mathematics, the convolution of two functions $f$ and $g$ produces a third function that expresses how the shape of one is modified by the other.

In Deep Learning, we perform a **discrete 2D cross-correlation** (often loosely called convolution).

Let:
- $I$ be the 2D input image matrix.
- $K$ be the 2D kernel (filter) matrix of size $m \times n$.
- $S(i, j)$ be the output value at position $(i, j)$ in the feature map.

The mathematical operation is:
$$ S(i, j) = (I * K)(i, j) = \sum_{m} \sum_{n} I(i + m, j + n) K(m, n) $$

This is simply an element-wise multiplication of the kernel against the corresponding patch of the image, followed by a sum of all those products.

## 📏 Calculating Output Dimensions
When designing a CNN architecture, you must calculate the exact dimensions of the output feature map after passing through a convolutional layer.

Let:
- $W$ = Input width/height (assuming a square image)
- $F$ = Filter size (e.g., 3 for a 3x3 kernel)
- $P$ = Padding (number of zero-pixels added to each edge)
- $S$ = Stride (step size)

The formula for the output dimension $O$ is:
$$ O = \lfloor \frac{W - F + 2P}{S} \rfloor + 1 $$

### Example Calculation
- Input Image: 28x28 ($W = 28$)
- Filter: 5x5 ($F = 5$)
- Stride: 1 ($S = 1$)
- Padding: 0 ($P = 0$)

$$ O = \lfloor \frac{28 - 5 + 0}{1} \rfloor + 1 = 23 + 1 = 24 $$
The output feature map will be 24x24 pixels.

## 🧠 Parameter Count Calculation
To understand why CNNs are so efficient, calculate the number of learnable parameters.

Let:
- $F$ = Filter size (e.g., 3)
- $C_{in}$ = Number of input channels (e.g., 3 for RGB)
- $K$ = Number of filters in the layer (e.g., 32)

Formula for parameters in a Conv layer:
$$ \text{Parameters} = ((F \times F \times C_{in}) + 1) \times K $$
*(The $+1$ is for the bias term for each filter)*

**Example**: A layer with 32 filters of size 3x3 applied to an RGB image.
$$ \text{Parameters} = ((3 \times 3 \times 3) + 1) \times 32 = (27 + 1) \times 32 = 896 \text{ parameters} $$
Compare this to the millions required for a dense layer!