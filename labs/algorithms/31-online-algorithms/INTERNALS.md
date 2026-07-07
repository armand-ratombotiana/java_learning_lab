# Online Algorithms — Internal Implementation Details

The OnlinePaging class implements three algorithms:
- LRU: uses LinkedHashMap with accessOrder=true. On page hit, the page is automatically moved to the end of the access order. On page fault, remove the first entry (least recently accessed) and add the new page.
- FIFO: uses a Queue (ArrayDeque) and a HashSet. On page fault, poll the queue, remove from set, add new page to queue and set. Does NOT update on page hits (the FIFO characteristic).
- Marker: maintains a boolean[] marked array of size k (cache). On page fault, if an unmarked page exists, select one randomly, replace it, mark it. If all pages are marked, reset all marks and start a new epoch.

The SkiRental class implements both deterministic and randomized strategies. The deterministic strategy tracks total spent and whether skis have been bought. The randomized strategy pre-rolls a threshold day according to the optimal distribution (shifted geometric with probability q = 1/(B-1)).

The SecretaryProblem class simulates the optimal stopping rule: generate n random values, observe the first n/e without selecting, then select the first value that exceeds the maximum observed.

The MultiArmedBandit class maintains arrays for counts and sums. Epsilon-greedy: for each round, generate a random number. If < epsilon, choose a random arm. Otherwise, choose the arm with highest average reward. Extensions include epsilon-decreasing (annealing) and UCB (Upper Confidence Bound) for comparison.