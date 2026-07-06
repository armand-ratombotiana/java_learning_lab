# Why Randomized Algorithms Exist

Randomization provides two fundamental advantages in algorithm design: simplicity and performance. Many problems that have complex deterministic solutions yield to simple randomized algorithms. Additionally, randomization often defeats adversarial inputs that would cause deterministic algorithms to perform poorly.

The classic example is quicksort. The deterministic version with a fixed pivot has O(n^2) worst-case behavior on sorted input. Randomized quicksort, using a random pivot, has O(n log n) expected time on any input. The adversary cannot predict the pivot choice, so no input reliably causes worst-case behavior.

Quickselect faces the same issue: selecting the k-th smallest element in worst-case O(n^2) with a deterministic pivot. The randomized version achieves expected O(n) with a simple algorithm. The Blum-Floyd-Pratt-Rivest-Tarjan deterministic O(n) algorithm exists but is significantly more complex.

Reservoir sampling solves a problem that deterministic algorithms cannot: sampling from a stream of unknown length without storing all elements. The algorithm is remarkably simple: each element takes its place in the sample with probability proportional to its position.

Freivalds' checker exploits randomness to reduce an O(n^3) matrix multiplication verification problem to O(n^2). For large matrices, this difference is enormous. Deterministic verification of matrix multiplication requires full recomputation (though there are recent theoretical breakthroughs).

Karger's algorithm uses randomization to solve a fundamental graph problem that has complex deterministic solutions. The algorithm's simplicity belies its power: it finds the minimum cut with high probability after O(n^2 log n) repetitions.
