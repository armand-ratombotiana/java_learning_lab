# Linear Programming — Visual Guide

## 2D LP Example

Maximize x + y subject to: x + 2y <= 10, 2x + y <= 10, x, y >= 0. Feasible region: polygon with vertices (0,0), (5,0), (10/3,10/3), (0,5). Objective lines x + y = k. Optimal at (10/3,10/3) with value 20/3.

## Simplex Path on Polygon

Start at (0,0). Objective x+y has positive coefficients, so increase x or y. Enter x: move along x-axis to (5,0) (hit constraint 2x+y <= 10). At (5,0): y has positive reduced cost. Enter y: move along constraint boundary to (10/3,10/3). At this point, both reduced costs are zero — optimal.

## Tablaeu Visualization

Initial tableau:

[1, 2, 1, 0, 0 | 10]
[2, 1, 0, 1, 0 | 10]
[-1, -1, 0, 0, 1 | 0]

Rows 1-2: constraints with slack variables s1,s2. Row 3: objective (-c). Columns: x, y, s1, s2, RHS.

After pivoting: the tableau transforms until the objective row has all non-negative reduced costs (for maximization).

## Two-Phase Visual

Phase I: add artificial variables a1, a2 to constraints. Objective: minimize a1 + a2. Simplex drives a1, a2 out of the basis. When they leave, Phase II begins with the original objective.

## Sensitivity Visual

A constraint's boundary line shifts. The optimal solution moves along the objective line until it hits the next vertex. The range over which the optimal basis remains unchanged is the sensitivity range.