# Mental Models for Randomized Algorithms

## Las Vegas — "The Bus Schedule"

A Las Vegas bus is guaranteed to arrive, but the exact time is random. Similarly, Las Vegas algorithms always produce the correct answer, but running time is random. The algorithm might finish quickly or take longer, but it never gives a wrong answer.

## Monte Carlo — "The Medical Test"

A medical test might give false positives (false alarm) or false negatives (missed detection) with known probabilities. Monte Carlo algorithms have bounded error probability. By repeating the test, you can make the error probability arbitrarily small.

## Reservoir Sampling — "The Lottery"

Imagine you need to pick k winners from a box of tickets, but you don't know how many tickets exist. You process tickets one by one. The first k tickets go into the "holding pool." For each subsequent ticket, with decreasing probability you might replace a random existing ticket. This ensures every ticket has equal probability of being selected.

## Fisher-Yates — "The Card Shuffle"

To shuffle a deck of cards fairly, pick a random card from the deck and put it at the beginning. Then pick a random card from the remaining deck and put it second. Continue until the entire deck is shuffled. This produces every permutation with equal probability.

## Karger's Algorithm — "The Contracting City"

Imagine a city where neighborhoods are slowly merging. When two neighborhoods merge, all connections between them become internal (no longer cross the boundary). The surviving connections between the remaining neighborhoods after enough mergers represent a cut of the original city. Different merger orders produce different cuts, and some order will produce the true minimum cut.
