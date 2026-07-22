# Interview Questions: Game Theory

## LeetCode Problem Map
| Problem | Difficulty | Company Signal | Pattern |
|---------|-----------|----------------|---------|
| LC 292 Nim Game | Easy | Microsoft, Apple | Math / XOR |
| LC 464 Can I Win | Medium | Google | DP + bitmask |
| LC 486 Predict the Winner | Medium | Google, Amazon | DP / minimax |
| LC 877 Stone Game | Medium | Google, Apple | DP / math proof |
| LC 1025 Divisor Game | Easy | Microsoft | DP |
| LC 1140 Stone Game II | Medium | Google | DP with suffix sum |
| LC 1406 Stone Game III | Hard | Google | DP |
| LC 1510 Stone Game IV | Hard | Google | DP |
| LC 1563 Stone Game V | Hard | Google | DP + prefix sum |

## NeetCode Reference
Not in NeetCode 150. These problems appear in DP and game theory sections of premium platforms.

## Company-Specific Questions
### Google
- Questions focus on minimax DP with optimal play assumptions
- Stone Game series is heavily tested for Google interviews
- Expect variations where both players play optimally and you must compute the maximum guaranteed score

### Microsoft
- Nim Game variants, usually math-based (XOR/knowledge problems)
- Focus on turn-based games with simple state transition
- May ask for proof of optimal strategy

### Meta
- Less focus on pure game theory; more on applying DP to game-like scenarios
- Expect "optimal play" problems disguised as resource allocation
- May combine game theory with array partitioning concepts

### Amazon
- Game theory is less common; when it appears, it's usually combined with DP
- May ask about minimax in the context of decision systems
- Expect "who wins" style questions with mathematical insights

### Apple
- Math-heavy Nim variants with simple proofs
- Focus on XOR and parity solutions
- Memory-constrained game state evaluation

### Oracle
- Rare in core Oracle interviews
- May appear in research or advanced roles related to game AI
- Expect questions about state space reduction techniques

## Real Production Scenarios
- Scenario 1: Ad bidding optimization - using game theory to determine optimal bid prices in real-time auction systems where competitors' strategies are unknown
- Scenario 2: Resource allocation in multiplayer games - designing fair matchmaking using minimax evaluation of player skill distributions
- Scenario 3: Network routing negotiation - applying cooperative game theory to model ISP peering agreements and bandwidth allocation

## Interview Tips
- Recognize impartial vs partisan games; most LC problems are impartial
- Learn to identify Nim-like XOR patterns (Grundy numbers for advanced roles)
- DP state definition is critical: dp[i][j] = maximum advantage for current player on subarray i..j
- Common edge cases: single element, even/odd length arrays, zero-sum constraints

## Java-Specific Considerations
- Use `int[][]` DP arrays for stone game variants; memoization with `int[n][n]` for subarray states
- For Can I Win (bitmask), use `Boolean[]` memo with bitmask key; `Map<Integer, Boolean>` for sparse state
- Pitfall: not using `Integer` cache object and causing infinite recursion in memoization
- Minimax typically expressed as: `maxScore = Math.max(pick + (suffix - opponent), ...)`
- Use prefix sum arrays for O(1) range sum lookups
