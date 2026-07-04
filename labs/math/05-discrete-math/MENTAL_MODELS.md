# Mental Models for Discrete Mathematics

## The Truth Table

Every logical proposition can be evaluated for all possible inputs:

```
A B | A AND B
0 0 |    0
0 1 |    0
1 0 |    0
1 1 |    1
```

## The Set as Container

A set is a collection of distinct objects. Operations: union, intersection, difference, complement.

## The Induction Dominoes

If domino 1 falls, and every domino knocks over the next, then all dominoes fall:

1. Base case: $P(1)$ is true
2. Inductive step: if $P(k)$ then $P(k+1)$
3. Conclusion: $P(n)$ for all $n$

## The Graph as Network

Nodes connected by edges. Models relationships: social networks, road maps, circuit connections.

## The Function as Mapping

A function $f: A \to B$ maps each element of $A$ to exactly one element of $B$.

## In Java

```java
// A Set<T> implements mathematical set with add/remove/contains
// A Map<K,V> implements a function from K to V
Function<String, Integer> length = String::length; // f: String → Integer
```
