# Visual Guide — Backtracking

## N-Queens Search Tree (4x4)
`
Root
├── Q at (0,0)
│   ├── Q at (1,2) → (2,? nothing) ✗
│   ├── Q at (1,3) → (2,1) → (3,? nothing) ✗
│   └── nothing valid at row 1 ✗
├── Q at (0,1)
│   └── Q at (1,3) → (2,0) → (3,2) ✓ Solution!
├── Q at (0,2)
│   └── Q at (1,0) → (2,3) → (3,1) ✓ Solution!
└── Q at (0,3)
    ├── Q at (1,0) → (2,? nothing) ✗
    └── Q at (1,1) → (2,? nothing) ✗
`

## Sudoku Search
`
Cell (0,0) try '1' → valid
  Cell (0,1) try '1' → invalid (same row)
  Cell (0,1) try '2' → invalid
  ...
  Cell (0,1) try '9' → none valid → BACKTRACK
Cell (0,0) try '2' → valid
... continues until solution
`
"@

wf "CODE_DEEP_DIVE.md" @"
# Code Deep Dive — Backtracking

## Subset Sum
`java
public List<List<Integer>> subsetSum(int[] nums, int target) {
    List<List<Integer>> result = new ArrayList<>();
    backtrack(nums, target, 0, new ArrayList<>(), result);
    return result;
}

private void backtrack(int[] nums, int remaining, int start,
        List<Integer> current, List<List<Integer>> result) {
    if (remaining == 0) {
        result.add(new ArrayList<>(current));
        return;
    }
    for (int i = start; i < nums.length; i++) {
        if (nums[i] <= remaining) {
            current.add(nums[i]);
            backtrack(nums, remaining - nums[i], i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }
}
`

## Permutations
`java
public List<List<Integer>> permute(int[] nums) {
    List<List<Integer>> result = new ArrayList<>();
    boolean[] used = new boolean[nums.length];
    backtrack(nums, used, new ArrayList<>(), result);
    return result;
}

private void backtrack(int[] nums, boolean[] used,
        List<Integer> current, List<List<Integer>> result) {
    if (current.size() == nums.length) {
        result.add(new ArrayList<>(current));
        return;
    }
    for (int i = 0; i < nums.length; i++) {
        if (!used[i]) {
            used[i] = true;
            current.add(nums[i]);
            backtrack(nums, used, current, result);
            current.remove(current.size() - 1);
            used[i] = false;
        }
    }
}
`
"@

wf "STEP_BY_STEP.md" @"
# Step-by-Step — Backtracking

## General Approach
1. Define the state representation
2. Identify base case (solution found)
3. Generate all possible next choices
4. Filter by constraints (pruning)
5. Make a choice, recurse
6. Undo the choice (backtrack)

## Optimization Techniques
- **Forward checking**: Pre-check future constraints before choosing
- **Constraint propagation**: Reduce domains of unassigned variables
- **Most constrained variable**: Choose variable with fewest options first
- **Least constraining value**: Choose value that restricts others least
- **Symmetry breaking**: Avoid symmetric solutions
