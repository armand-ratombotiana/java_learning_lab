# Flashcards — Data Stream Algorithms

Q: Reservoir sampling sample probability?
A: k/n for each element

Q: AMS estimates?
A: Second frequency moment F2

Q: Count-Min Sketch estimate property?
A: Always overestimates (or exact)

Q: Count-Min Sketch error guarantee?
A: eps * n error with prob 1 - delta

Q: Misra-Gries guarantees?
A: Elements with frequency > n/k are always in counters

Q: Heavy hitter?
A: Element appearing more than n/k times

Q: Welford's algorithm computes?
A: Streaming mean and variance (numerically stable)

Q: Space-Saving uses which data structure?
A: Min-heap for smallest count tracking