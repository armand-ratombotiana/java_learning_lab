# Math Foundation: Feature Transformations

## 1. Log Transformation

$$ x' = \ln(1 + x) $$

**Purpose**: Make right-skewed distributions more normal. Apply when values span orders of magnitude.

## 2. Box-Cox Transformation

$$ x'(\lambda) = \begin{cases} \frac{x^{\lambda} - 1}{\lambda}, & \lambda \neq 0 \\ \ln(x), & \lambda = 0 \end{cases} $$

Finds optimal λ to make data more normal.

## 3. Interaction Features

$$ x_{ij} = x_i \times x_j $$

Captures non-additive effects. For example, `age × income` might predict spending better than either alone.

```java
public DoubleColumn interaction(DoubleColumn a, DoubleColumn b, String name) {
    double[] result = new double[a.size()];
    for (int i = 0; i < a.size(); i++) {
        result[i] = a.getDouble(i) * b.getDouble(i);
    }
    return DoubleColumn.create(name, result);
}
```

## 4. Polynomial Features

$$ \phi(x) = [1, x, x^2, x^3, \ldots, x^d] $$

Adds non-linearity to linear models:

```java
// PolynomialFeatures in Java
public Table polynomialExpand(DoubleColumn col, int degree) {
    Table out = Table.create(col.name() + "_poly");
    for (int d = 1; d <= degree; d++) {
        double[] vals = new double[col.size()];
        for (int i = 0; i < col.size(); i++) {
            vals[i] = Math.pow(col.getDouble(i), d);
        }
        out.addColumn(DoubleColumn.create(col.name() + "^" + d, vals));
    }
    return out;
}
```

## 5. Binning (Discretization)

$$ f(x) = \begin{cases} 0, & x < t_1 \\ 1, & t_1 \leq x < t_2 \\ 2, & t_2 \leq x < t_3 \\ 3, & x \geq t_3 \end{cases} $$

Converts continuous to categorical. Risk: loses within-bin information.

## 6. Principal Component Analysis (PCA)

$$ Z = XW $$

Where $W$ contains eigenvectors of $X^TX$. Reduces dimensionality while preserving variance.
