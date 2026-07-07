# Bit Manipulation — Mental Models

## Binary as a Light Switch Panel

Think of each bit as a light switch: 1 = ON, 0 = OFF. AND is like both switches must be ON for the light to turn on. OR is like either switch ON turns the light on. XOR is like a hallway light: flipping either switch toggles the light. NOT is like a single switch that is always reversed.

## Bitmask as Backpack

A 32-bit integer is a backpack with 32 compartments. Each compartment can hold either nothing (0) or one item (1). Subset enumeration is like trying all combinations of items to take. Setting bit i (1 << i) is like putting an item in compartment i. Testing if bit i is set (mask & (1 << i) != 0) is like checking if the item is in the backpack.

## Gray Code as Minimizing Changes

Imagine rearranging furniture in a room. Gray code is like moving one piece of furniture at a time. If you need to try all arrangements, Gray code ensures each step changes only one thing. This minimizes disruption (or in hardware, minimizes switching noise and power consumption).

## XOR Basis as Vector Building Blocks

Think of the XOR basis as a set of LEGO bricks of different sizes. Each brick is a power of two position. Given a collection of vectors (XOR bricks), the basis is a minimal set that can build any vector in the original collection. Building a vector means XORing the right basis vectors together, like finding the right LEGO bricks to make a specific shape.

## Bit DP as Filling a Decision Tree

TSP bit DP is like having a task list of n cities to visit. The mask represents which cities have been visited. The DP transitions are decisions: "which city to visit next from my current position." The 2^n masks represent all possible subsets of visited cities, and each state (mask, last) represents the optimal cost to have visited that exact set and end at the given city.

## Population Count as Counting Coins

Counting set bits is like sorting coins: pick the lowest set bit (the rightmost coin), remove it, increment count, repeat. Brian Kernighan's algorithm does exactly this: each x = x & (x - 1) removes one set bit, just like taking one coin from a pile.