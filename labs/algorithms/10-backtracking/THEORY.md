# Backtracking — Theoretical Foundation

## The Backtracking Paradigm
Backtracking incrementally builds candidates and abandons partial candidates (backtracks) when they cannot become valid solutions.

## Key Components
1. **Choice**: At each step, make a decision
2. **Constraints**: Rules that must be satisfied
3. **Goal**: Condition that defines a complete solution

## Algorithm Template
`
void backtrack(candidate, state):
    if isSolution(candidate):
        output(candidate)
        return
    for each choice in generateChoices(candidate):
        if isValid(choice, state):
            makeChoice(choice, state)
            backtrack(nextCandidate, state)
            undoChoice(choice, state)
`

## Complexity
- Often exponential (O(branching^depth))
- Pruning can dramatically reduce search space
- Best case: solution found early with effective pruning
