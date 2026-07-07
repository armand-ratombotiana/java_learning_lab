# History of Online Algorithms

1970: Laszlo Belady proved the optimal offline paging algorithm (MIN): evict the page that will be used furthest in the future. This provided the benchmark for competitive analysis.

1972: Peter Denning introduced the working set model for paging, the theoretical foundation for LRU.

1985: Daniel Sleator and Robert Tarjan published the first competitive analysis of paging algorithms, defining the competitive ratio framework. They proved that LRU and FIFO are k-competitive and that no deterministic algorithm can be better.

1987: James Aspnes and colleagues introduced competitive analysis for the ski rental problem, establishing deterministic and randomized competitive ratios.

1989: Allan Borodin and colleagues formalized the online model in their STOC paper "Online Computation and Competitive Analysis."

1991: The secretary problem was analyzed in the competitive analysis framework, though its optimal stopping rule was known much earlier (Lindley 1961, Dynkin 1963).

1990s: Multi-armed bandits were formalized in computer science, with Lai and Robbins establishing lower bounds on regret. Auer, Cesa-Bianchi, and Fischer introduced the epsilon-greedy algorithm with theoretical guarantees.

2000s: Online convex optimization and online learning emerged as a unified framework for online algorithms and machine learning. Regret analysis became the standard performance measure.

2010s: Online algorithms became critical for cloud computing, data center resource allocation, and real-time bidding in advertising. Modern recommender systems use bandit algorithms at scale.