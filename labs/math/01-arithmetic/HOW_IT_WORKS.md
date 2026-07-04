# How Arithmetic Works

## Addition

$$
a + b = \underbrace{a + 1 + 1 + \dots + 1}_{b \text{ times}}
$$

Implemented in hardware via **full adders** that compute sum and carry bits.

## Subtraction as Addition of Negatives

$$
a - b = a + (-b)
```

Computers use **two's complement** to represent negatives, making subtraction identical to addition.

## Multiplication

$$
a \times b = \underbrace{a + a + \dots + a}_{b \text{ times}}
$$

Hardware uses **shift-and-add** (Booth's algorithm) for efficiency.

## Division

$$
a \div b = q \text{ remainder } r \quad \text{where } a = q \times b + r,\; 0 \le r < |b|
```

Java's `/` operator on integers truncates toward zero; `%` yields remainder with sign of dividend.

## In Java

```java
int sum = 5 + 3;          // 8
int diff = 5 - 3;         // 2
int prod = 5 * 3;         // 15
int quot = 5 / 3;         // 1 (truncated)
int rem  = 5 % 3;         // 2
```
