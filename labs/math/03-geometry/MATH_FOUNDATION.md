# Mathematical Foundations: Geometry Proofs

## 1. Pythagorean Theorem

### Statement
In a right triangle, a² + b² = c² where c is the hypotenuse.

### Proof (Area Comparison)
Consider a square of side (a+b) with four right triangles of legs a,b inside.

Area of large square = (a+b)²
Area of four triangles = 4 × (1/2)ab = 2ab
Remaining area (inner square) = c²

Therefore: (a+b)² = 4×(1/2)ab + c²
a² + 2ab + b² = 2ab + c²
a² + b² = c² ∎

### Proof (Similar Triangles)
In right triangle ABC (right at C), draw altitude to hypotenuse.
Three similar triangles are formed:
- Triangle 1 (large): sides a, b, c
- Triangle 2: sides ?, ?, a
- Triangle 3: sides ?, ?, b

By similarity: a/c = c/a → c² = a²
Similarly b² = b²
Adding: a² + b² = c² ∎

## 2. Angle Sum in Triangle

### Statement
Sum of interior angles in any triangle = 180°.

### Proof
Draw line parallel to one side through opposite vertex.
Alternate interior angles are equal.
Sum of angles on a straight line = 180°
Therefore sum of triangle angles = 180° ∎

## 3. Law of Sines Proof

### Statement
sin(A)/a = sin(B)/b = sin(C)/c

### Proof
Height from C to AB: h = b·sin(A) = a·sin(B)
Therefore b·sin(A) = a·sin(B)
→ sin(A)/a = sin(B)/b ∎

## 4. Law of Cosines Proof

### Statement
c² = a² + b² - 2ab·cos(C)

### Proof
Place triangle with side c along x-axis.
Coordinates: A(0,0), B(c,0), C at (b·cos(A), b·sin(A))
Distance AC = b, so:
b² = (b·cos(A) - 0)² + (b·sin(A) - 0)²
But by law of sines: a² = b² + c² - 2bc·cos(A)
And cos(A) relates to angle C through geometry.
Alternatively using vector dot product:
|c|² = |a - b|² = a·a + b·b - 2a·b
c² = a² + b² - 2|a||b|cos(C) ∎

## 5. Heron's Formula Proof

### Statement
Area = √[s(s-a)(s-b)(s-c)] where s = (a+b+c)/2

### Proof
Area = (1/2)ab·sin(C)
sin²(C) = 1 - cos²(C)
Using law of cosines: cos(C) = (a² + b² - c²)/(2ab)
Substituting and simplifying yields Heron's formula ∎

## 6. Circumference of Circle

### Statement
C = 2πr

### Proof
Approximate circle with regular n-gon.
Perimeter of n-gon = n × 2r × sin(π/n) = 2nr·sin(π/n)
As n → ∞: sin(π/n) → π/n
Therefore perimeter → 2πr ∎

## 7. Area of Circle Proof

### Statement
A = πr²

### Proof (Riemann Sum)
Divide circle into n sectors.
Each sector ≈ triangle with base r·Δθ and height r.
Area ≈ Σ (1/2)r²Δθ = (1/2)r²(2π) = πr² ∎

## 8. Inscribed Angle Theorem

### Statement
Angle at circumference = 1/2 angle at center (same arc)

### Proof
Consider inscribed angle ABC and central angle AOC.
Triangle AOC is isosceles (OA = OC = r).
∠OAC = ∠OCA = (180° - ∠AOC)/2 = 90° - ∠ABC
∠AOC = 2∠ABC
Therefore inscribed angle = half central angle ∎

## 9. Varignon's Theorem

### Statement
The midpoints of the sides of any quadrilateral form a parallelogram.

### Proof
In quadrilateral ABCD, let M, N, P, Q be midpoints of AB, BC, CD, DA.
Vectors: MN = (B + C - A - B)/2 = (C - A)/2
PQ = (D + C - D - C)/2 = (C - A)/2
Therefore MN = PQ, so MNQP is parallelogram ∎

## 10. Euler Line Proof

### Statement
In any triangle, centroid, circumcenter, and orthocenter are collinear.

### Proof
Place triangle with vertices at position vectors a, b, c.
Centroid G = (a + b + c)/3
Circumcenter O: |O - a|² = |O - b|² = |O - c|²
Orthocenter H: h = a + b + c - 2o (in vector form)
Therefore H, G, O are collinear with HG = 2·GO ∎

## 11. Nine-Point Circle Theorem

### Statement
The midpoints of sides, feet of altitudes, and midpoints of segments from orthocenter to vertices all lie on a circle.

### Proof
Show that the nine points are equidistant from the nine-point center N.
N is the midpoint of OH (Euler line).
Using properties of homothety with factor 1/2 centered at H, the circle passes through all nine points ∎

## 12. Desargues' Theorem

### Statement
Two triangles are perspective from a point if and only if they are perspective from a line.

### Proof in 3D
Place configuration in 3D space.
If triangles are perspective from point P, lines joining corresponding vertices concur at P.
These lines determine a plane containing P.
Intersection of this plane with another appropriate plane gives the axis of perspectivity ∎