# Consistency Models: Interview Questions

## Q1: When would you choose eventual over strong consistency?
**A**: When availability and performance are prioritized over immediate correctness - social feeds, analytics, product recommendations.

## Q2: How does Spanner achieve external consistency?
**A**: Uses TrueTime (GPS + atomic clocks) to assign commit timestamps with bounded clock uncertainty. Waits for the uncertainty interval to pass before making commits visible.

## Q3: Explain causal consistency with an example.
**A**: Alice posts a photo (W1), Bob comments on it (W2, causally after W1). Causal consistency ensures everyone sees W1 before W2.

## Q4: How do you implement read-your-writes?
**A**: Client tracks write timestamps in a local cache. Reads check cache first; if the local value is newer than the remote, return cached value.

## Q5: What's the overhead of causal consistency?
**A**: O(N) metadata per operation (vector clock of size equal to number of nodes). Network overhead and merge complexity.
