# Online Algorithms — Code Deep Dive

The OnlinePaging class uses separate inner classes for LRU, FIFO, and Marker. LRU uses LinkedHashMap with accessOrder=true, removeEldestEntry overridden to return size() > capacity. On page access: get (which triggers ordering), or put (which triggers eviction via removeEldestEntry).

FIFO uses a Queue + HashSet. On access: if set.contains(page), do nothing. Else if queue.size() == capacity: evict = queue.poll(); set.remove(evict); queue.offer(page); set.add(page). The page fault count tracks misses.

Marker uses a Map<Integer, Boolean> marked. Pages in cache are entries. On access: if present, mark=true. If not present (fault): find any unmarked page to evict (randomized). If all marked, clear all marks (new epoch), then evict unmarked.

The SkiRental class: deterministic strategy rents for B-1 days, then buys. The method returns cumulative cost. Randomized strategy pre-chooses a threshold T uniformly from {0, ..., B-1} with specific probabilities to achieve optimal competitive ratio.

The SecretaryProblem class: generate n random scores. Find best in first n/e: maxObserved. For remaining: select first score > maxObserved. Return position and whether it was the global best. Run multiple simulations to estimate success probability.

The MultiArmedBandit class: maintains two parallel arrays: counts[k] and rewards[k]. For each round: if random < epsilon, pick random arm. Else pick argmax(rewards[i]/counts[i]). Update counts and rewards for the chosen arm based on observed reward (generated randomly based on true reward probabilities for simulation).