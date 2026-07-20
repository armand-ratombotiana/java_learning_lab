# Mathematics Academy

![Status](https://img.shields.io/badge/status-active-success.svg)
![Labs](https://img.shields.io/badge/labs-19-blue)
![Difficulty](https://img.shields.io/badge/difficulty-foundation--to--advanced-red)
![Topics](https://img.shields.io/badge/topics-math--cs--stats-green)

## Overview

The Mathematics Academy builds a comprehensive foundation in mathematical thinking, progressing from basic arithmetic through advanced topics. Each lab connects theory to practical Java implementation — you will implement algorithms, build simulations, and solve computational problems. Topics cover pure mathematics (number theory, algebra, geometry), applied mathematics (probability, statistics, graph theory), and advanced computational mathematics (optimization, information theory, numerical methods) essential for software engineering, data science, machine learning, and systems design.

## Curriculum

| # | Lab | Topic | Duration | Difficulty |
|---|-----|-------|----------|------------|
| 01 | [arithmetic](./01-arithmetic) | Number systems, operations, properties, modular arithmetic, proofs | 3h | ★☆☆ |
| 02 | [algebra](./02-algebra) | Linear/quadratic equations, polynomials, factoring, systems of equations | 3h | ★☆☆ |
| 03 | [geometry](./03-geometry) | Shapes, area, volume, coordinate geometry, transformations, vectors | 3h | ★☆☆ |
| 04 | [trigonometry](./04-trigonometry) | Functions, identities, unit circle, inverse trig, polar coordinates | 3h | ★★☆ |
| 05 | [discrete-math](./05-discrete-math) | Logic, set theory, relations, functions, proof techniques, induction | 4h | ★★☆ |
| 06 | [combinatorics](./06-combinatorics) | Permutations, combinations, pigeonhole principle, generating functions | 3h | ★★☆ |
| 07 | [graph-theory](./07-graph-theory) | Graphs, trees, traversal, shortest path, coloring, network flow | 4h | ★★★ |
| 08 | [probability](./08-probability) | Random variables, distributions, expectation, Bayes' theorem | 4h | ★★★ |
| 09 | [statistics](./09-statistics) | Descriptive/inferential stats, hypothesis testing, regression, ANOVA | 4h | ★★★ |
| 10 | [number-theory](./10-number-theory) | Prime numbers, modular arithmetic, RSA, GCD, Chinese remainder theorem | 4h | ★★★ |
| 11 | [calculus](./11-calculus) | Limits, derivatives, integrals, series, multivariable calculus | 5h | ★★★ |
| 12 | [linear-algebra](./12-linear-algebra) | Vectors, matrices, SVD, QR decomposition, eigenvalues, transformations | 5h | ★★★★ |
| 13 | [mathematical-optimization](./13-mathematical-optimization) | Gradient descent, Newton's method, Lagrange multipliers, convex optimization | 5h | ★★★★ |
| 14 | [information-theory](./14-information-theory) | Entropy, mutual information, KL divergence, channel capacity, compression | 4h | ★★★★ |
| 15 | [numerical-methods](./15-numerical-methods) | Integration, root finding, interpolation, ODE solvers, finite differences | 5h | ★★★★ |

**Total estimated time: 60 hours**

### Deep-Dive Modules

The following atomic deep-dive micro-labs provide expanded coverage of core topics, with 24 educational files each, Java 21+ source code, JUnit 5 tests, and 7 project subdirectories:

| # | Module | Labs | Topics |
|---|--------|------|--------|
| 16 | [calculus-deep](./calculus-deep) | 8 | Limits, continuity, derivatives, integration, differential equations, multivariable calculus, vector calculus |
| 17 | [linear-algebra-deep](./linear-algebra-deep) | 8 | Vector spaces, linear transformations, matrix decompositions, eigenvalues, inner products, tensors, applied LA, matrix calculus |
| 18 | [probability-deep](./probability-deep) | 8 | Probability axioms, random variables, distributions, multivariate statistics, LLN/CLT, estimation, hypothesis testing, Bayesian |
| 19 | [discrete-deep](./discrete-deep) | 6 | Logic, set theory, combinatorics, graph theory, generating functions, number theory |

**Total deep-dive labs: 30 | Additional estimated time: 150 hours**

## How to Use

Each lab contains:
- **THEORY.md** — Comprehensive mathematical treatment with proofs
- **MATH_FOUNDATION.md** — Prerequisite math review (where applicable)
- **CODE_DEEP_DIVE.md** — Java implementations of algorithms
- **EXERCISES.md** — Problem sets (pen-and-paper + coding)
- **MINI_PROJECT/** — Applied project directory
- **REAL_WORLD_PROJECT/** — Cross-domain application directory
- **QUIZ.md** — Knowledge check questions
- **FLASHCARDS.md** — Spaced-repetition review cards

Read THEORY.md and MATH_FOUNDATION.md for conceptual understanding. Implement algorithms from CODE_DEEP_DIVE.md. Solve EXERCISES.md (both theoretical and programming). Apply learning in MINI_PROJECT and REAL_WORLD_PROJECT.

## Prerequisites

- Basic algebra and arithmetic at high-school level
- Java 21+ (for programming exercises)
- Scientific curiosity and willingness to work through proofs
- For advanced labs (12-15): multivariable calculus and linear algebra recommended

## Learning Path

```
01 ──→ 02 ──→ 03 ──→ 04 ──→ 05 ──→ 06 ──→ 07 ──→ 11
Arith   Alg    Geom   Trig    Disc    Combo   Graph   Calc
                                Math                   
                                │
                                ├─→ 08 ──→ 09 ──→ 10
                                │    Prob    Stats   NumTh
                                │
                                └─→ 12 ──→ 13 ──→ 14 ──→ 15
                                    LinAlg   Opt    InfoTh  NumMet
```

Labs 01–04 build foundational mathematics. Lab 05 introduces discrete structures. Labs 06–07 extend to combinatorics and graphs. Labs 08–09 bridge to probability and statistics. Lab 10 covers number theory and cryptography. Lab 11 covers calculus. Labs 12–15 are advanced topics: linear algebra, mathematical optimization, information theory, and numerical methods — recommended for those pursuing machine learning, data science, or scientific computing.

## Related Academies

- [Data Science Academy](../data-science) — Probability & statistics applied to data
- [AI Academy](../ai) — Linear algebra, calculus, optimization for ML
- [Data Structures Academy](../data-structures) — Graph algorithms, trees
- [Algorithms Academy](../algorithms) — Combinatorial optimization, sorting, searching
- [Distributed Systems Academy](../distributed-systems) — Graph theory, consensus protocols

## Resources

- [Apache Commons Math](https://commons.apache.org/proper/commons-math) — Java math library
- [EJML](https://ejml.org) — Java matrix library
- [JGraphT](https://jgrapht.org) — Java graph library
- [3Blue1Brown](https://www.youtube.com/c/3blue1brown) — Visual math explanations
- [Khan Academy](https://www.khanacademy.org/math) — Supplementary lessons
- [AoPS](https://artofproblemsolving.com) — Advanced problem-solving
