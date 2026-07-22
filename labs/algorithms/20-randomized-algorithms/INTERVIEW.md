# Interview Questions: Randomized Algorithms

## LeetCode Problem Map
| Problem | Difficulty | Company Signal | Pattern |
|---------|-----------|----------------|---------|
| LC 380 Insert Delete GetRandom O(1) | Medium | Google, Meta, Amazon, Microsoft | HashMap + ArrayList |
| LC 398 Random Pick Index | Medium | Google, Amazon | Reservoir sampling |
| LC 528 Random Pick with Weight | Medium | Google, Meta, Amazon | Prefix sum + binary search |
| LC 470 Implement Rand10() Using Rand7() | Medium | Google, Microsoft | Rejection sampling |
| LC 478 Generate Random Point in Circle | Medium | Google, Amazon | Rejection / polar |
| LC 519 Random Flip Matrix | Medium | Google | Fisher-Yates / map |
| LC 710 Random Pick with Blacklist | Hard | Google | HashMap mapping |

## NeetCode Reference
- LC 380 Insert Delete GetRandom O(1) (NeetCode 150)

## Company-Specific Questions
### Google
- Design a uniform random shuffle that works on streaming data
- How would you test if a random number generator is truly random?
- Implement Rand10() from Rand7() is a Google classic
- Reservoir sampling for random selection from an unbounded stream

### Microsoft
- How does Windows generate cryptographically secure random numbers?
- Design a random load balancer for Azure
- Implement Fisher-Yates shuffle for online card games

### Meta
- Random Pick with Weight for A/B testing allocation
- How would you implement random feed ranking with weighted probabilities?
- Design a fair random selection algorithm for content moderation

### Amazon
- Random Pick with Blacklist for product sampling
- How would you randomly select warehouse items for quality check?
- Design a random shuffle for product recommendations

### Apple
- Generate secure random numbers for iCloud Keychain
- How does iOS generate random UUIDs?
- Implement a shuffle algorithm that works in memory-constrained environments

### Oracle
- How does Oracle's DBMS_RANDOM package work?
- Design a random sampling algorithm for large database tables
- Explain Oracle's SAMPLE clause implementation

## Real Production Scenarios
- Scenario 1: A/B testing allocation - using weighted random selection to assign users to experiment variants while maintaining consistent user experience per session
- Scenario 2: Load balancer request distribution - implementing weighted random routing across backend servers with health-aware probability adjustments
- Scenario 3: Content sampling - debugging a reservoir sampling implementation that produces biased results when the stream contains duplicate elements

## Interview Tips
- Fisher-Yates shuffle: O(n) time, O(1) space, produces uniform permutations
- Reservoir sampling: O(n) time, O(k) space for k samples from n items without knowing n
- Rejection sampling: expected number of iterations = 1 / probability(acceptance)
- Common edge cases: negative weights, zero probabilities, empty data structures

## Java-Specific Considerations
- `java.util.Random` for non-cryptographic randomness; `ThreadLocalRandom` for best performance
- `java.security.SecureRandom` for cryptographic randomness (uses /dev/urandom or native CSPRNG)
- `Collections.shuffle(List)` uses Fisher-Yates internally with a `Random` source
- `Random.nextInt(n)` returns [0, n); beware of `Random.nextDouble()` vs `Random.nextFloat()`
- Pitfall: creating a new `Random` instance per call (use `ThreadLocalRandom.current()`)
- Pitfall: `Random.nextInt(n)` is not perfectly uniform for n not a power of 2 (slight bias)
- For weighted random: `int index = Arrays.binarySearch(prefix, random)` with `-(insert - 1)` adjustment
