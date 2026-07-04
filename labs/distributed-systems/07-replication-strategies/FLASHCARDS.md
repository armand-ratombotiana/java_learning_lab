# Replication: Flashcards

## Front: What is single-leader replication?
**Back**: One node accepts writes, followers replicate.

## Front: What is multi-leader replication?
**Back**: Multiple nodes accept writes, replicate to each other.

## Front: What is quorum?
**Back**: Minimum nodes needed for a valid read/write operation.

## Front: What does R+W > N guarantee?
**Back**: Strong consistency (read quorum overlaps write quorum).

## Front: What is read repair?
**Back**: Fix stale replicas detected during reads.

## Front: What is hinted handoff?
**Back**: Temporarily store writes for downed replicas.

## Front: What is anti-entropy?
**Back**: Periodic full sync of replica data using Merkle trees.

## Front: What is replication factor?
**Back**: Number of copies of each data item across nodes.
