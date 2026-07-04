# Mental Models for CAP Theorem

## The Triangle Model
Visualize CAP as a triangle where each vertex represents one guarantee. A system can only occupy one edge (two guarantees).

## The Two-Out-Of-Three Model
Like choosing features for a product: you can have speed, quality, or cost - pick two.

## The Network Partition Model
Imagine two offices connected by a bridge. If the bridge collapses:
- **CP**: Shut down one office (no availability)
- **AP**: Both offices work but may have different info (inconsistent)
- **CA**: Impossible - the bridge failure breaks everything

## The PACELC Extension
- **P**artition: tradeoff between **A**vailability and **C**onsistency
- **E**lse (no partition): tradeoff between **L**atency and **C**onsistency
