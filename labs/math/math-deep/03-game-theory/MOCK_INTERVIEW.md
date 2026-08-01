# Mock Interview: Minimax with Alpha-Beta Pruning

> Coding mock interview — 45-minute session, Java 21+, whiteboard style.

---

## Interview Setup

**Role**: Gameplay Engineer (Board Game Platform Team)
**Candidate Level**: Senior Engineer
**Focus Area**: Game AI, search trees, pruning, evaluation functions
**Problem**: Implement minimax with alpha-beta pruning for a zero-sum game (Tic-Tac-Toe), with move ordering, depth limits, and an evaluation function — then demonstrate the pruning benefit empirically.
**Language**: Java 21+ (records, sealed types, enums, pattern matching allowed)

---

## Interviewer Cheat Sheet (Prepared Questions)

1. Define minimax. Why does the max player pick the max of the min player's choices?
2. Prove/explain alpha-beta pruning: what do alpha and beta mean, and why is the cutoff valid?
3. What is the best-case branching factor for alpha-beta, and what enables it?
4. How do you handle depth limits and imperfect evaluation functions?
5. What is the horizon effect?
6. Follow-up: iterative deepening, transposition tables, move ordering, negamax.

---

## Transcript

### Part 1: Problem Clarification (5 minutes)

**Interviewer**: "We're building the AI for a casual board game platform — chess, checkers, the whole family. Today I want minimax with alpha-beta pruning on Tic-Tac-Toe as the reference implementation. Clarify the scope."

**Candidate**: "Three questions. Perfect information, deterministic, two-player, zero-sum — Tic-Tac-Toe qualifies on all counts. Do you want a full game-tree search to terminal states, or a depth-limited version with an evaluation function? Tic-Tac-Toe is small enough that exhaustive search proves optimal play, but the depth-limit machinery is what generalizes to chess. And do you want the pruned version instrumented — nodes evaluated with and without pruning — so we can measure the win?"

**Interviewer**: "Exhaustive search to the end of the game. And yes, instrument it — I want the node counts."

**Candidate**: "Then the design is: board as a record with a bitmask-based or char-array state, a negamax-style or plain minimax engine with alpha/beta, and a node counter. Move ordering is cheap here because I can order moves by center, corners, edges — the classic heuristic — and measure how many nodes it saves."

### Part 2: Theory (8 minutes)

**Interviewer**: "Define minimax precisely."

**Candidate**: "Each position has a value from the perspective of the player to move, with a terminal valuation: +1 win, 0 draw, -1 loss for the side to move (or from a fixed root perspective). The max player (to move) chooses the move maximizing value; the opponent then chooses the move minimizing it. So value(s) = max over children of min over grandchildren of ... terminal value. Backward induction: solve the tree from the leaves up."

**Interviewer**: "Now alpha-beta. What do alpha and beta mean, and why is a cutoff safe?"

**Candidate**: "Alpha is the best value the maximizing player is already guaranteed along the current path; beta is the best value the minimizing player is guaranteed. A node's subtree can only matter if its value falls inside (alpha, beta) — if a child returns a value ≥ beta, the min player will never allow this line (they already have a better option), so the remaining children are irrelevant and we prune. Symmetrically, a max node cuts off when a child value ≤ alpha. The validity is the neat part: pruning never changes the root result, it only removes subtrees that can't influence it. The invariant is that every uncut value equals what unpruned search would return."

**Interviewer**: "What's the complexity with and without it?"

**Candidate**: "Plain minimax explores O(b^d) nodes with b the branching factor and d the depth. With optimal move ordering, alpha-beta examines O(b^(d/2)) — the square root of the work, effectively doubling the searchable depth at equal cost. With random ordering it degrades to about O(b^(3d/4)): partial pruning. For Tic-Tac-Toe, b starts at 9 and decreases; the full tree has ~549,946 reachable nodes (a famous number), and with good move ordering alpha-beta evaluates a small fraction of them."

### Part 3: Design (8 minutes)

**Interviewer**: "Design the classes."

**Candidate**: "A `Board` record — the game state — with methods `moves()`, `apply(move)`, `isTerminal()`, `terminalValue()`, plus `undo` is unnecessary since I'll copy-on-play (small board). An enum `Player { X, O }`. The engine is a `MinimaxEngine` with public `bestMove(Board, int maxDepth)` and package-private `int search(Board, int depth, int alpha, int beta, boolean maximizing)`, wrapped with a `SearchStats` record counting nodes, cutoffs, and max depth reached. For instrumentation I'll pass a `NodeCounter` — a long[] so the engine stays pure."

**Interviewer**: "Why does move ordering matter so much here?"

**Candidate**: "Because pruning effectiveness is exponential in ordering quality. If the first move examined at each node is the best, alpha and beta tighten immediately and every sibling gets cut. My ordering: center first (it's part of every winning line), then corners, then edges. It's a heuristic that's near-optimal for Tic-Tac-Toe and a cheap approximation of the 'kill move first' principle we'd apply in chess."

### Part 4: Implementation (18 minutes)

**Interviewer**: "Code it."

**Candidate**: "I'll implement negamax-style with alpha-beta — the classic compact form — with the board as a char array."

```java
public record Board(char[] cells) {
    public static Board empty() { return new Board(new char[9]); }
    public List<Integer> moves() { ... }
    public Board play(int idx, char p) { ... }
    public boolean isTerminal() { return winner() != 0 || moves().isEmpty(); }
    public int winner() { ... }  // 'X', 'O', or 0
    public int utility() { return winner() == 'X' ? 1 : winner() == 'O' ? -1 : 0; }
}

public record SearchStats(long nodes, long cutoffs, int depthReached) {}

public final class MinimaxEngine {
    public int bestMove(Board board, char player, SearchStats stats) {
        int best = -1;
        int bestScore = Integer.MIN_VALUE;
        for (int m : ordered(board.moves())) {
            Board child = board.play(m, player);
            int score = -search(child, other(player), Integer.MIN_VALUE, Integer.MAX_VALUE, stats);
            if (score > bestScore) { bestScore = score; best = m; }
        }
        return best;
    }

    private int search(Board board, char player, int alpha, int beta, SearchStats stats) {
        stats.nodes++;
        if (board.isTerminal()) return board.utility() * (player == 'X' ? 1 : -1);
        for (int m : ordered(board.moves())) {
            int score = -search(board.play(m, player), other(player),
                                -beta, -alpha, stats);
            if (score >= beta) { stats.cutoffs++; return score; }
            if (score > alpha) alpha = score;
        }
        return alpha;
    }
}
```

**Interviewer**: "Walk me through the negation trick — that's the part people get wrong."

**Candidate**: "In negamax, from the perspective of the player to move, a position's value is the negation of the value from the opponent's perspective: value = -value' where value' is computed for the opponent. So terminal utility is signed by whose turn it is, and the recurrence becomes a single max loop with the alpha/beta windows negated and swapped: the child's window is (-beta, -alpha). A cutoff is 'score ≥ beta' because the parent is the negation of this node: if I can guarantee score ≥ beta, the parent's guaranteed -score ≤ -beta < its own window — it won't choose this line. The symmetric case is handled by the negation when recursing up."

**Interviewer**: "Where does the `other(player)` swap in the utility sign?"

**Candidate**: "`utility()` is defined from X's perspective (+1 X wins). When it's O's turn at a terminal, O's negamax value is the negation: -utility(). That single sign flip is the whole trick — it keeps the code one recursive function instead of two."

### Part 5: Testing (5 minutes)

**Interviewer**: "How do you verify the engine plays optimally?"

**Candidate**: "Four checks. (1) The engine as X must never lose — run it against itself for X and O, all 9 opening moves, and assert no X losses across the full game. (2) Exhaustive reference: with no pruning, minimax and alpha-beta must return the *identical* best move for every reachable position in a sweep — the correctness contract of pruning. (3) Known fork scenario: X at (0,0), O at (1,1), X at (2,2)... check the engine creates the fork. (4) The stats: on an empty board, alpha-beta with good ordering should evaluate far fewer nodes than the ~550k reachable count; and with 'ordered' vs 'random' ordering the cutoff counts must differ."

**Interviewer**: "And the horizon effect you mentioned?"

**Candidate**: "That's for depth-limited search — we don't have it here since we search to terminal. In chess, a depth limit can hide a catastrophe just past the horizon — a queen being captured on the next ply. The standard defenses are quiescence search (search captures to a stable position) and null-move pruning. For this lab, the full-depth search sidesteps it entirely."

### Part 6: Follow-ups (5 minutes)

**Interviewer**: "How does this scale to chess?"

**Candidate**: "Branching factor ~35, so alpha-beta alone gives 35^(d/2) — nowhere near enough. The real stack: iterative deepening with the previous depth's best move searched first (move ordering via the killer heuristic and history heuristic), transposition tables keyed by Zobrist hash, quiescence search, and null-move pruning. With all of that a modern engine searches 15+ plies deep. The alpha-beta core is identical to what we wrote — the magic is ordering and the hash table."

**Interviewer**: "What if the game has chance, like backgammon?"

**Candidate**: "Expectiminimax: max and min nodes plus chance nodes that average over dice outcomes. Alpha-beta doesn't directly apply to chance nodes — you can only prune when the bounds are adjusted by the probability distribution; there's an algorithm called *Expectimax alpha-beta* or *Star1/Star2* pruning that bounds the average. It's much less effective than in deterministic games."

---

## Scoring Rubric

| Area | Excellent (3) | Good (2) | Needs Work (1) |
|------|---------------|----------|----------------|
| Theory | Explains negamax negation and cutoff validity with the window argument | Defines alpha/beta correctly | Minimax only, no pruning |
| Implementation | Correct negamax with instrumentation and move ordering | Correct minimax, slow | Bugs in terminal handling |
| Verification | Sweep test: pruned == unpruned best moves everywhere | Single test position | No correctness check |
| Analysis | States O(b^(d/2)) best case and why ordering drives it | Knows pruning exists | No complexity discussion |

## Red Flags
- Flipping the utility sign for the opponent (double negation bugs).
- Cutoff test `score > beta` instead of `>=` (loses the "already equal" prune).
- Forgetting that terminal positions must be checked *before* expanding moves.
- Mutating the board during search without undo.

## Key Takeaways
- Minimax = backward induction; negamax = one function with negated windows.
- alpha = max-side guarantee, beta = min-side guarantee; cutoff when the window closes.
- Best case O(b^(d/2)) — doubling effective depth at equal cost.
- Move ordering is the difference between the best and worst case.
