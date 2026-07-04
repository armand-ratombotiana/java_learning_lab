# Math Foundation: Relational Algebra and Set Theory

## Relational Algebra (Procedural Query Language)

### Basic Operations
| Operation | Symbol | SQL Equivalent |
|---|---|---|
| Selection | σ<sub>condition</sub>(R) | `SELECT * FROM R WHERE condition` |
| Projection | π<sub>cols</sub>(R) | `SELECT cols FROM R` |
| Union | R ∪ S | `R UNION S` |
| Intersection | R ∩ S | `R INTERSECT S` |
| Difference | R − S | `R EXCEPT S` |
| Cartesian Product | R × S | `R CROSS JOIN S` |
| Rename | ρ<sub>new</sub>(R) | `AS new` |

### Joins in Relational Algebra
- **Theta join**: R ⋈<sub>θ</sub> S = σ<sub>θ</sub>(R × S)
- **Equijoin**: θ is equality on attributes
- **Natural join**: R ⋈ S = equijoin on all common attributes
- **Outer joins**: Retain unmatched rows (left/right/full)

## Normalization Math

### Functional Dependency (FD)
X → Y means X determines Y (same X implies same Y)

### Armstrong's Axioms
1. **Reflexivity**: If Y ⊆ X, then X → Y
2. **Augmentation**: If X → Y, then XZ → YZ
3. **Transitivity**: If X → Y and Y → Z, then X → Z
4. **Union**: If X → Y and X → Z, then X → YZ
5. **Decomposition**: If X → YZ, then X → Y and X → Z

### Closure of Attributes
Given FDs {A→B, B→C}, closure of A = {A, B, C}

## ACID Probability
- Probability of system failure in time T: P(fail) = 1 − e<sup>−λT</sup>
- Mean Time To Failure (MTTF): 1/λ
- With N independent servers: P(all fail) = P(fail)<sup>N</sup>

## Cost-Based Optimization
- **Query cost** = Σ(page reads × I/O cost + CPU operations × CPU cost)
- **Selectivity** = estimated rows returned / total rows
- **Cardinality estimation**: histogram-based, sampling-based
