# Data Stream Algorithms — Mental Models

## River of Data

A data stream is like a river flowing past you. You can take a cup of water (sample), test for pollutants (sketch), or count how many different fish species swim by (distinct count). You cannot stop the river, go back upstream, or analyze all the water. You must make decisions and collect statistics as the water flows past.

## Reservoir as a Small Fish Tank

Reservoir sampling is like maintaining a small fish tank that can hold at most k fish. As fish swim past in a river, you occasionally catch one. For the first k, you keep them all. For each subsequent fish, you decide probabilistically whether to swap it with a fish already in the tank. The result: every fish in the entire river has an equal chance of being in your tank at the end.

## Count-Min Sketch as a Voting System

The Count-Min Sketch is like asking multiple witnesses (hash functions) to estimate how many times an event occurred. Each witness has their own scoreboard (a row of counters). When an event occurs, each witness increments their score. To estimate the count, take the minimum across all witnesses. This is always an overestimate (or exact), because if one witness overcounts (due to hash collisions), the others may have better estimates.

## Misra-Gries as a Boxing Match

The Misra-Gries algorithm for frequent items is like a boxing tournament with k-1 belts. Each new element is a challenger. If the element already holds a belt, it keeps it and gets a token. If no belt is available, the element takes a belt from someone else (all losers give up one token, and if any reaches zero, the belt becomes available). At the end, the belt holders are candidates for being frequent items.

## Sliding Window as a Moving Frame

A sliding window is like looking through a train window at moving scenery. The window shows only the most recent W meters of track. As the train moves, old scenery leaves the window and new scenery enters. Counting distinct elements in a sliding window is like counting how many different animals you have seen in the last W meters, knowing that once an animal leaves the window, it should no longer count.