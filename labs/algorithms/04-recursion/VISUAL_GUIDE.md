# Visual Guide — Recursion

## Recursion Tree for fib(5)
`
                    fib(5)
                   /      \
              fib(4)      fib(3)
             /     \      /    \
         fib(3)   fib(2) fib(2) fib(1)
         /    \   /   \   /   \
     fib(2) fib(1) 1 1  1 1   1
     /   \
    1    1
`
Total: 15 calls for fib(5), O(φⁿ) growth.

## Tower of Hanoi (3 disks)
`
Move disk 1: A → C
Move disk 2: A → B
Move disk 1: C → B
Move disk 3: A → C
Move disk 1: B → A
Move disk 2: B → C
Move disk 1: A → C
`
"@

wf "CODE_DEEP_DIVE.md" @"
# Code Deep Dive — Recursion

## Tower of Hanoi
`java
public static void hanoi(int n, char from, char to, char aux) {
    if (n == 1) {
        System.out.println("Move disk 1 from " + from + " to " + to);
        return;
    }
    hanoi(n - 1, from, aux, to);
    System.out.println("Move disk " + n + " from " + from + " to " + to);
    hanoi(n - 1, aux, to, from);
}
`

## Generate All Subsets (Backtracking)
`java
public static void subsets(int[] nums, int idx, List<Integer> current, List<List<Integer>> result) {
    if (idx == nums.length) {
        result.add(new ArrayList<>(current));
        return;
    }
    subsets(nums, idx + 1, current, result);
    current.add(nums[idx]);
    subsets(nums, idx + 1, current, result);
    current.remove(current.size() - 1);
}
`
"@

wf "STEP_BY_STEP.md" @"
# Step-by-Step — Recursion

## Solving Recursively
1. Identify base case(s): simplest instance
2. Assume subproblem solved: function works for smaller input
3. Combine solutions: use subproblem result for current problem
4. Ensure progress: each call moves toward base case

## Converting Iteration → Recursion
1. Loop variable → parameter
2. Loop condition → base case check
3. Loop body → recursive case
4. Loop update → recursive call with modified parameter

## Common Pitfalls
- Missing base case → infinite recursion
- Base case never reached
- Excessive depth → use iteration for deep recursion
- Recomputation → use memoization for overlapping subproblems
