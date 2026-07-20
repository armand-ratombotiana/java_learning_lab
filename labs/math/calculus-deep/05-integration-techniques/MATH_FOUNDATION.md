# Math Foundation for Integration Techniques

## Prerequisite Mathematics

This document reviews the mathematical prerequisites needed to study Integration Techniques effectively.

## 1. Algebra Review

### 1.1 Fundamental Operations
- Addition, subtraction, multiplication, division of real numbers
- Exponentiation: a^n for integer and real n
- Roots and radicals: root(n,a)
- Logarithmic and exponential functions

### 1.2 Solving Equations
- Linear equations: ax + b = 0
- Quadratic equations: ax^2 + bx + c = 0, solved by x = (-b +/- sqrt(b^2 - 4ac)) / (2a)
- Systems of linear equations

### 1.3 Inequalities
- Basic inequality rules and interval notation
- Absolute value inequalities: |x - a| < b
- Triangle inequality: |a + b| <= |a| + |b|

## 2. Functions and Graphs

### 2.1 Function Basics
- Domain, codomain, and range
- Function notation: f(x)
- Composition: (f o g)(x) = f(g(x))
- Inverse functions: f^{-1}(f(x)) = x

### 2.2 Important Function Families
- **Linear**: f(x) = mx + b, slope m, y-intercept b
- **Quadratic**: f(x) = ax^2 + bx + c, parabola
- **Polynomial**: f(x) = a_n x^n + a_{n-1}x^{n-1} + ... + a_0
- **Rational**: f(x) = p(x)/q(x), vertical asymptotes at zeros of q
- **Exponential**: f(x) = a^x, growth/decay
- **Logarithmic**: f(x) = log_a(x), inverse of exponential
- **Trigonometric**: sin(x), cos(x), tan(x), periodic

### 2.3 Transformations
- Vertical shift: f(x) + c
- Horizontal shift: f(x + c)
- Reflection: -f(x) across x-axis, f(-x) across y-axis
- Stretch/compress: c * f(x), f(cx)

## 3. Trigonometry

### 3.1 Unit Circle
- sin(theta) = y-coordinate on unit circle
- cos(theta) = x-coordinate on unit circle
- tan(theta) = sin(theta)/cos(theta)

### 3.2 Important Identities
- sin^2(theta) + cos^2(theta) = 1
- sin(2*theta) = 2*sin(theta)*cos(theta)
- cos(2*theta) = cos^2(theta) - sin^2(theta)
- sin(a +/- b) = sin(a)*cos(b) +/- cos(a)*sin(b)

## 4. Key Formulas for Reference

| Formula | Description |
|---------|-------------|
| f'(x) = lim_{h->0} (f(x+h) - f(x))/h | Derivative (instantaneous rate of change) |
| int_a^b f(x) dx = lim_{n->inf} sum_{i=1}^n f(x_i*) Delta_x | Definite integral (accumulated area) |
| sum_{i=1}^n i = n(n+1)/2 | Sum of first n integers |
| d/dx x^n = n*x^{n-1} | Power rule for derivatives |
| int x^n dx = x^{n+1}/(n+1) + C (n != -1) | Power rule for integrals |

## 5. Practice Problems

1. Simplify: (x^2 - 4)/(x - 2)
2. Solve for x: e^{2x} - 3e^{x} + 2 = 0
3. Find the domain: f(x) = ln(x^2 - 4)
4. Solve the inequality: |x - 3| < 2

## 6. Solutions

1. ((x-2)(x+2))/(x-2) = x + 2 for x != 2
2. Let u = e^x: u^2 - 3u + 2 = 0, u = 1 or u = 2, so x = 0 or x = ln(2)
3. x^2 - 4 > 0 => x < -2 or x > 2
4. -2 < x - 3 < 2 => 1 < x < 5
