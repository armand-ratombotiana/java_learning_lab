# Why Consistency Models Exist

## Problem Statement
In distributed systems, replicas can diverge. Without clear guarantees, developers can't reason about program correctness.

## Purpose
1. Define contracts between storage systems and applications
2. Enable developers to write correct distributed programs
3. Allow systems to optimize performance while maintaining guarantees
4. Provide a spectrum of tradeoffs between correctness and performance

## Motivation
Strong consistency provides intuitive semantics but hurts performance under partition. Weaker models enable high performance but require careful programming. Consistency models formalize this tradeoff.
