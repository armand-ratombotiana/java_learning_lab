# Linear Programming — Mental Models

## Hill Climbing on a Polyhedron

The simplex method is like hiking on a polyhedral mountain (the feasible region). Each vertex is a location. The objective function gives the elevation. Starting at one vertex, walk along edges that go uphill (increasing the objective). When all adjacent vertices are downhill, you have reached the top (optimal solution). The number of edges to traverse is typically small.

## Corner of a Shipment Box

A linear program's feasible region is like the shape of a cardboard box being crushed: it's a convex polytope with flat faces (constraints) and sharp corners (vertices). Each corner is where some constraints meet. The optimal solution is always at a corner, never in the middle. This is the key insight behind the simplex method: only check the corners, not the interior.

## Resource Allocation and Shadow Prices

Dual variables (shadow prices) are like the market value of resources. If a factory has 100 hours of labor, the shadow price tells how much profit one additional hour would bring. The dual LP is like a competitor trying to buy all resources at minimum total cost, pricing each resource such that no product is profitable to produce (weak duality), and the total buyout price equals the producer's profit (strong duality).

## Two-Phase Method as Jumpstarting a Car

Sometimes the LP's constraints push the feasible region away from the origin. The two-phase method is like jumpstarting a car: Phase I adds artificial variables (jumper cables) to find any feasible point. When Phase I finds a feasible basis (engine starts), the artificial variables are removed, and Phase II proceeds with the original objective.

## Sensitivity Analysis as Stress Testing

Sensitivity analysis is like testing how much you can push on the walls of the feasible region before the optimal solution changes. It asks: by how much can a cost coefficient change before the optimal basis changes? By how much can a resource increase or decrease before it affects the objective? This tells you which constraints are tight and which have slack.