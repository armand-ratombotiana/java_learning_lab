# Replication: Mathematical Foundation

## Quorum Theorem

For system with N replicas, read quorum R, write quorum W:

If R + W > N: read always sees at least one up-to-date copy
(Strong consistency)

If R + W ≤ N: read may not see latest write
(Weak consistency)

## Replication Lag

For async replication with propagation delay d:
- Write at leader at time t₀
- Follower applies at time t₀ + d
- Reads from follower between t₀ and t₀ + d see stale data

## Availability with Replication

For replication factor R, with each node having availability A:
P(data available) = 1 - P(all R replicas down) = 1 - (1-A)^R

With R=3, A=0.99: P(available) = 1 - (0.01)^3 = 0.999999
