# Flashcards — Push-Relabel

Q: What is a preflow?
A: Flow where inflow may exceed outflow (excess)

Q: Height function invariant?
A: height[u] <= height[v] + 1 for all residual edges

Q: When is an edge admissible?
A: height[u] = height[v] + 1 and residual capacity > 0

Q: Gap heuristic?
A: If no node has height h, set all higher nodes to n

Q: Push-relabel complexity with FIFO?
A: O(V^3)

Q: Potentials in min-cost flow?
A: Johnson's potentials ensure non-negative reduced costs

Q: Capacity scaling?
A: Process capacities from MSB to LSB, adding lower bits progressively

Q: Relabel-to-front?
A: Maintain active list, move relabeled vertex to front