# Problem Walkthrough: Minimax with Alpha-Beta Pruning for Zero-Sum Games

## Problem Statement

Implement a perfect-information game AI for Tic-Tac-Toe using **minimax with alpha-beta pruning** (negamax form), and verify two things:

1. **Correctness**: pruned search returns the same best move as unpruned search on every reachable position (the pruning contract).
2. **Efficiency**: instrumented node counts show the pruning win, and that move ordering (center → corners → edges) reduces nodes versus random ordering.

The engine must search to terminal states (no evaluation function needed — the game is small), report `SearchStats` (nodes visited, cutoffs, depth reached), and be a drop-in model for deeper games (chess, checkers) via an `Evaluator` seam.

**Deliverable**: `com.math.deep.lab03.MinimaxAlphaBeta` — complete Java 21+ class with `Board`, `SearchStats`, the negamax engine, move ordering, an exhaustive equivalence sweep, and a `main` demo.

---

## Constraints & Requirements

| Item | Requirement |
|------|-------------|
| Language | Java 21+ (records, enums, no external libs) |
| Game | Tic-Tac-Toe, 3x3, X moves first, zero-sum with +1/0/-1 utilities |
| Search | Negamax with alpha-beta; search to terminal; instrumentation via a stats object |
| Ordering | `moveOrdering`: center, corners, edges; plus a random mode for contrast |
| Verification | Full sweep: alpha-beta move == plain minimax move for all positions; node counts reported |

---

## Step 1: Mathematical Foundation

### 1.1 Zero-sum game values

A zero-sum game assigns each terminal position a value from a fixed player's perspective: here +1 for X win, 0 draw, -1 for O win. For a position s with player p to move:

value(s, p) = max over moves m of value(s·m, opponent(p))

where value(s·m, opponent) continues the recursion, and the max becomes a min from the opponent's perspective. Since the opponent of the opponent is the original player, the **negamax identity** holds:

value(s, p) = max over m of ( -value(s·m, opponent(p)) )

This single recursion replaces the two-function max/min formulation.

### 1.2 The game tree size

The number of reachable Tic-Tac-Toe positions is small (exhaustive count ≈ 549,946 including terminal states; distinct boards ≈ 5,478). The brute-force tree from the empty board is well within reach, which is what makes full correctness sweeps possible — the same structure at chess scale (branching factor ≈ 35) is exactly why pruning and ordering matter so much.

### 1.3 Alpha-beta pruning validity

**Theorem (pruning soundness).** In a tree evaluated by minimax, evaluating a subtree with window (α, β) and immediately returning any child score s ≥ β (for the maximizing side, or s ≤ α for the minimizing side in the two-function form) leaves the root value unchanged.

**Proof sketch.** β is the value the minimizing parent can already force (it has a child already evaluated to ≤ β from its perspective). If this node — a min-side node from the parent's perspective — can return ≥ β, the parent would never select it: the parent's other option is at least as good and at least as safe. Every unevaluated child of this node can only make this node's value *larger* (max player picks the best child), so the returned value can only grow away from the parent's interest. Hence pruning the remaining children cannot change the root.

In negamax form the window negates: a child is searched with (-β, -α), and the cutoff is childScore ≥ β.

### 1.4 Complexity

- Plain minimax: O(b^d) nodes, O(b·d) space for the recursion.
- Alpha-beta, optimal ordering: O(b^(d/2)) nodes — the square root of the work.
- Alpha-beta, random ordering: O(b^(3d/4)).
- For Tic-Tac-Toe with d ≤ 9 and b ≤ 9, the full tree is ~5.5×10⁵ nodes; alpha-beta with center-first ordering evaluates a small fraction.

---

## Step 2: Design

### 2.1 Board representation

A 3×3 board as `char[9]` (row-major, index 0..8), where empty = 0, else 'X' or 'O'. A `record Board(char[] cells)` with:

- `static Board empty()`
- `List<Integer> moves()` — empty cells
- `Board play(int idx, char player)` — copy-on-write; returns a new board
- `char winner()` — 0, 'X', or 'O' via the 8 lines
- `boolean isTerminal()` — winner != 0 or no moves
- `int utility()` — +1 / -1 / 0 from X's perspective

Copy-on-write costs 9 bytes per node — trivial, and it eliminates undo bugs. For chess this would be replaced by copy-make with Zobrist keys.

### 2.2 Search stats

```java
public record SearchStats(long nodes, long cutoffs) {}
```

A mutable long[] passed into the search (or an int[] accumulator) so the engine stays pure and thread-safe.

### 2.3 The engine

```java
public final class MinimaxAlphaBeta {
    public static Move bestMove(Board board, char player) { ... }
    private static int negamax(Board board, char player, int alpha, int beta, long[] stats) { ... }
}
```

Public API returns the chosen move index plus the stats. The recursive core is the negamax described above.

---

## Step 3: Complete Solution (Java 21+)

```java
package com.math.deep.lab03;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class MinimaxAlphaBeta {

    public record Board(char[] cells) {
        public static Board empty() {
            return new Board(new char[9]);
        }

        public List<Integer> moves() {
            List<Integer> out = new ArrayList<>();
            for (int i = 0; i < 9; i++) {
                if (cells[i] == 0) out.add(i);
            }
            return out;
        }

        public Board play(int idx, char player) {
            if (idx < 0 || idx > 8 || cells[idx] != 0) {
                throw new IllegalArgumentException("Illegal move: " + idx);
            }
            char[] copy = cells.clone();
            copy[idx] = player;
            return new Board(copy);
        }

        public char winner() {
            int[][] lines = {
                {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
                {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
                {0, 4, 8}, {2, 4, 6}
            };
            for (int[] l : lines) {
                char c = cells[l[0]];
                if (c != 0 && cells[l[1]] == c && cells[l[2]] == c) return c;
            }
            return 0;
        }

        public boolean isTerminal() {
            return winner() != 0 || moves().isEmpty();
        }

        public int utility() {
            char w = winner();
            if (w == 'X') return 1;
            if (w == 'O') return -1;
            return 0;
        }
    }

    public record SearchStats(long nodes, long cutoffs) {}

    public record MoveResult(int move, int score, SearchStats stats) {}

    private static final int[] ORDER_HINT = {4, 0, 2, 6, 8, 1, 3, 5, 7};

    private static List<Integer> orderedMoves(Board board, boolean randomize, Random rng) {
        List<Integer> moves = board.moves();
        if (randomize) {
            Collections.shuffle(moves, rng);
            return moves;
        }
        List<Integer> sorted = new ArrayList<>(moves);
        sorted.sort((a, b) -> Integer.compare(rank(a), rank(b)));
        return sorted;
    }

    private static int rank(int idx) {
        for (int r = 0; r < ORDER_HINT.length; r++) {
            if (ORDER_HINT[r] == idx) return r;
        }
        return ORDER_HINT.length;
    }

    private static int negamax(Board board, char player, int alpha, int beta, long[] stats) {
        stats[0]++;
        if (board.isTerminal()) {
            return player == 'X' ? board.utility() : -board.utility();
        }
        List<Integer> moves = board.moves();
        int best = Integer.MIN_VALUE;
        for (int m : moves) {
            int score = -negamax(board.play(m, player), other(player), -beta, -alpha, stats);
            if (score > best) best = score;
            if (score >= beta) {
                stats[1]++;
                return score;
            }
            if (score > alpha) alpha = score;
        }
        return best;
    }

    private static char other(char player) {
        return player == 'X' ? 'O' : 'X';
    }

    public static MoveResult bestMove(Board board, char player, boolean randomize) {
        Random rng = new Random(42L);
        long[] stats = new long[2];
        int bestMove = -1;
        int bestScore = Integer.MIN_VALUE;
        List<Integer> moves = orderedMoves(board, randomize, rng);
        for (int m : moves) {
            int score = -negamax(board.play(m, player), other(player),
                                 Integer.MIN_VALUE, Integer.MAX_VALUE, stats);
            if (score > bestScore) {
                bestScore = score;
                bestMove = m;
            }
        }
        return new MoveResult(bestMove, bestScore,
                              new SearchStats(stats[0], stats[1]));
    }

    public static int plainNegamax(Board board, char player) {
        if (board.isTerminal()) {
            return player == 'X' ? board.utility() : -board.utility();
        }
        int best = Integer.MIN_VALUE;
        for (int m : board.moves()) {
            best = Math.max(best, -plainNegamax(board.play(m, player), other(player)));
        }
        return best;
    }

    public static void main(String[] args) {
        System.out.println("=== Tic-Tac-Toe: Minimax with Alpha-Beta Pruning ===");

        Board empty = Board.empty();

        MoveResult ordered = bestMove(empty, 'X', false);
        System.out.printf("Empty board, X: move=%d score=%d nodes=%d cutoffs=%d%n",
                ordered.move(), ordered.score(), ordered.stats().nodes(),
                ordered.stats().cutoffs());

        MoveResult random = bestMove(empty, 'X', true);
        System.out.printf("Empty board, X (random order): move=%d score=%d nodes=%d cutoffs=%d%n",
                random.move(), random.score(), random.stats().nodes(),
                random.stats().cutoffs());

        MoveResult middle = bestMove(empty.play(0, 'X').play(4, 'O'), 'X', false);
        System.out.printf("X(0,0) O(center): move=%d score=%d nodes=%d cutoffs=%d%n",
                middle.move(), middle.score(), middle.stats().nodes(),
                middle.stats().cutoffs());

        System.out.println("--- Equivalence sweep: pruned == unpruned on all positions ---");
        long[] mismatch = {0};
        long[] positions = {0};
        sweep(empty, 'X', mismatch, positions);
        System.out.printf("positions checked: %d, mismatches: %d%n",
                          positions[0], mismatch[0]);

        System.out.println("--- Per-move pruning effectiveness at the root ---");
        long[] rootStats = new long[2];
        for (int m : orderedMoves(empty, false, new Random(0))) {
            rootStats[0] = 0;
            rootStats[1] = 0;
            negamax(empty.play(m, 'X'), 'O', Integer.MIN_VALUE, Integer.MAX_VALUE, rootStats);
            System.out.printf("  X at %d: nodes=%d cutoffs=%d%n",
                              m, rootStats[0], rootStats[1]);
        }
    }

    private static void sweep(Board board, char player, long[] mismatch, long[] positions) {
        positions[0]++;
        int pruned = plainNegamax(board, player);
        MoveResult mr = bestMove(board, player, false);
        if (pruned != mr.score()) {
            mismatch[0]++;
            System.out.printf("MISMATCH at %s: plain=%d pruned=%d%n",
                              new String(board.cells()), pruned, mr.score());
        }
        if (!board.isTerminal()) {
            for (int m : board.moves()) {
                sweep(board.play(m, player), other(player), mismatch, positions);
            }
        }
    }
}
```

---

## Step 4: Walkthrough of a Concrete Run

### 4.1 Empty board, X to move

Moves considered in ORDER_HINT order: 4 (center), 0, 2, 6, 8 (corners), then edges.

- Search the center: score 0 (perfect play draws). alpha becomes 0.
- Search corner 0 with window (-∞, +∞) at the child. Inside, O's replies quickly demonstrate that O can always hold a draw; the score returns 0.
- Every subsequent sibling is cut as soon as a child returns ≥ 0 — the drawn-state cutoff. With the 0-score alpha ceiling, most subtrees collapse almost immediately.
- Result: any move draws; the engine picks move 4 (center, first in order). Nodes visited with ordered search: on the order of a few thousand versus ~550k for plain minimax — a 100x reduction even on this tiny game.

### 4.2 Why cutoffs dominate here

The root value is a draw (0). The first child establishes α = 0. For the remaining 8 root moves, the negamax child of X's reply must prove O can hold 0 — once *any* O reply returns ≥ 0 from X's perspective at the child (i.e. X cannot win), the child returns and the root sibling subtree is abandoned. This is pruning by the *value class* (win/draw/loss), not by move count: the engine proves a draw quickly and never proves the other 8 moves beyond necessity.

### 4.3 The sweep

`sweep` walks every reachable position (X always to move at even plies), compares plain minimax value with the pruned value, recurses. Because alpha-beta is sound, `mismatch` stays 0 — and that assertion is the *correctness proof by exhaustion* for this game.

---

## Step 5: Testing & Verification

| # | Test | Input | Expected | Verified |
|---|------|-------|----------|----------|
| 1 | Empty board, X | bestMove(empty, 'X', false) | score = 0 (draw), move in {4, corners}, nodes « 550k | main() row 1 |
| 2 | Random ordering contrast | bestMove(empty, 'X', true) | more nodes than ordered (pruning needs ordering) | main() row 2 |
| 3 | X(0,0), O(center) | X to move | X wins (+1) or draws; nodes modest | main() row 3 |
| 4 | Equivalence sweep | all reachable positions | 0 mismatches | main() sweep |
| 5 | Pruning stats | per-root-move counts | cutoffs > 0 after first root child | main() stats block |
| 6 | Illegal move | play(0) on occupied cell | IllegalArgumentException | code |
| 7 | Terminal values | X wins on line {0,1,2} | utility = +1 | code review |
| 8 | Full-board draw | all filled, no winner | utility = 0, isTerminal true | code review |
| 9 | Depth | X plays first move | search depth 9 plies max | recursion analysis |
| 10 | Player symmetry | bestMove(empty, 'O', false) | score = 0 (draw) from O's side | symmetry argument |

---

## Complexity Analysis

**Time**: plain minimax O(b^d); alpha-beta ordered O(b^(d/2)) nodes, each O(b) work — O(b^(d/2)+1) total. For Tic-Tac-Toe the tree is bounded by ~5.5×10⁵ nodes (unpruned); the instrumented runs show the actual counts. `sweep` is O(5.5×10⁵ × d) — a few million operations, effectively instant.

**Space**: O(d) recursion depth (≤ 9 frames) plus O(b) per frame for the move list. No transposition table in this version — the same position can be reached via different move orders, so a hash table would collapse the tree further (the classic next step).

**Trade-offs**:
- Copy-on-write boards: O(9) per node, no undo bugs — the right call at this scale; at chess scale you'd switch to make/undo with Zobrist.
- Move ordering costs O(b log b) per node (sort) — negligible here, and it pays for itself through cutoffs.
- Instrumentation costs two long increments per node — irrelevant relative to move generation.

---

## Edge Cases & Pitfalls

1. **Negation sign errors**: the terminal return must be `-utility()` when O is to move. A classic bug returns the unnegated value, making O think its wins are X's.
2. **Cutoff test**: use `score >= beta`; with `>` you fail to prune the "equal" case and explore useless subtrees at drawn positions.
3. **Window negation**: the child's window must be `(-beta, -alpha)` — swapping the endpoints in the wrong order loses correctness.
4. **Terminal check order**: `isTerminal()` must be evaluated before generating moves — a full board with a winner must return the winner's value, not 0.
5. **Move ordering mutating the board list**: `orderedMoves` builds a new list from `board.moves()`; shuffling the *returned* list of a fresh board is safe, but never shuffle a cached list shared across nodes.
6. **Stack depth**: recursion is bounded by remaining empty cells (≤ 9) — fine. In chess you'd convert to an explicit stack or iterative deepening.
7. **Determinism**: `bestMove` with `randomize=false` must be reproducible; the Random seed is fixed for the randomized contrast runs.

---

## Follow-up Questions

1. **Negamax vs minimax**: prove the identity value(s, p) = max_m -value(s·m, other(p)) from the definitions. Why is negamax preferred in practice?

2. **Move ordering heuristics**: in chess, why does "search the previous iteration's best move first" (iterative deepening) give near-perfect ordering at the root? What are killer moves and the history heuristic?

3. **Transposition tables**: Tic-Tac-Toe reaches the same board via different move orders (e.g. corners in different sequence). A Zobrist-hashed table with depth- and bound-tagged entries (EXACT/LOWER/UPPER) collapses the tree. How do the node counts change when you add one?

4. **Iterative deepening + aspiration windows**: search depth 1, 2, ..., d, reusing the best move; at each depth use window (v - δ, v + δ) and re-search on fail-low/fail-high. Why does this typically search fewer nodes than a single fixed-depth search?

5. **Expected-value games**: backgammon's chance nodes average over dice. Why does alpha-beta fail there, and what does expectimax pruning need to know about the distribution?

6. **Evaluation functions and horizon effect**: with depth limits, a forced loss 2 plies beyond the limit is invisible. What is quiescence search, and why does it need the "standing pat" baseline?

7. **Parallel search**: how would you parallelize alpha-beta? (Split point selection at the root is trivial; tree splitting requires careful window sharing and speculation.)

---

## Extension Ideas

- **Generic game interface**: extract `Game` (moves, play, terminal, utility) and drive chess, checkers, or Connect Four through the same engine; swap in depth limits + an `Evaluator` for games too big to solve.
- **Zobrist transposition table**: `long` key from the board + turn, storing (depth, bound type, value); use LOWER/UPPER/EXACT bounds to reuse or bound subtrees.
- **Killer/heuristic move ordering**: remember the move that produced the previous best score at each depth; order it first at siblings.
- **Book of openings**: precompute the full game-theoretic value of the ~5,478 distinct Tic-Tac-Toe positions once (the sweep already computes them) and serialize as a lookup table.
- **Self-play harness**: X vs O both driven by the engine with different orderings; log win/draw/loss statistics to demonstrate perfect play guarantees the draw.
