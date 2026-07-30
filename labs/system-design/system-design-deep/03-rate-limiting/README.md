# Lab 03: Rate Limiting

## Overview
Master rate limiting algorithms and distributed rate limiting design. Covers token bucket, leaky bucket, sliding window, fixed window, and Redis-based distributed implementations.

## Algorithms

| Algorithm | Mechanism | Burst Handling | Accuracy | Memory |
|-----------|-----------|---------------|----------|--------|
| **Token Bucket** | Tokens refill at fixed rate | Allows bursts up to bucket size | Medium | Low |
| **Leaky Bucket** | Requests drip at fixed rate | Smooths bursts | Medium | Low |
| **Fixed Window** | Counter per time window | Allows edge-case bursts | Low | Very Low |
| **Sliding Window** | Rolling time window counters | Precise limiting | High | Medium |
| **Sliding Log** | Timestamp log per request | Most precise | Highest | High |

## Learning Objectives
- Implement all five rate limiting algorithms
- Understand trade-offs in accuracy vs memory
- Design distributed rate limiting with Redis
- Handle race conditions in concurrent environments
- Apply rate limiting to API gateway design
