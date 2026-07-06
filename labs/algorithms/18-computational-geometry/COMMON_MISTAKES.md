# Common Mistakes — Computational Geometry

- **EPS too small or too large** — Can cause false collinear or non-collinear results
- **Integer overflow** — Cross product of large coordinates can overflow 32-bit int
- **Monotone chain duplicate endpoints** — Both lower and upper hull include first/last; must remove one
- **Graham Scan collinear points** — Sorting by polar angle with collinear points needs tie-breaking by distance
- **Closest pair strip sorting** — Re-sorting strip by y each recursive step adds log factor if done wrong
- **Line intersection collinear case** — Special case for overlapping collinear segments often forgotten
- **Cross product order** — (q-p) x (r-p) vs (r-p) x (q-p) changes orientation direction
- **Handling duplicate points** — Should be removed or handled early in all algorithms
- **Returning hull in wrong order** — Typically counterclockwise, with first point repeated for closed polygon
- **Stack overflow on large inputs** — Recursive closest pair may stack overflow on >10K points
