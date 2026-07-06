# How Network Flow Works

## Ford-Fulkerson Example

Network: s->a(10), s->b(5), a->b(15), a->t(10), b->t(10)

Path 1: s->a->t, min capacity = min(10,10) = 10
  After: s->a(0), a->t(0), residual: a->s(10), t->a(10)

Path 2: s->b->t, min capacity = min(5,10) = 5
  After: s->b(0), b->t(5), residual: b->s(5), t->b(5)

Path 3: s->a->b->t via residuals? Let's check:
  s->a: residual = 0 (forward). But a->s has 10 backward.
  Actually, s->a is saturated, so we need alternative.
  s->b is saturated too. Flow = 15. Max flow = 15.

## Edmonds-Karp Difference

Edmonds-Karp always picks the SHORTEST augmenting path (by number of edges). This guarantees that each edge is saturated at most O(V) times, giving O(VE^2) bound.

## Dinic Level Graph

Phase 1: BFS from s gives levels: s(0), a(1), b(1), t(2). DFS finds blocking flow: s->a->t (10), s->b->t (5). After no more augmenting paths in level graph, recompute levels.

Phase 2: s(0), then? a(1) but s->a has 0 capacity, b(1) but s->b has 0 capacity. t is unreachable. Done. Max flow = 15.
