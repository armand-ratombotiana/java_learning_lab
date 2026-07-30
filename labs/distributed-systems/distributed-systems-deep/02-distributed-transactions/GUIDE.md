# Distributed Transactions — Deep Dive Guide

## 2PC Protocol

1. **Phase 1 — Prepare**: coordinator sends prepare to all participants
2. **Phase 2 — Commit/Abort**: if all vote Yes, commit; else abort

**Problem**: blocking if coordinator fails after prepare — participants hold locks until coordinator recovers.

## 3PC Protocol

1. **CanCommit**: coordinator asks if participants can commit
2. **PreCommit**: coordinator sends pre-commit, participants acknowledge
3. **DoCommit**: coordinator sends commit

Non-blocking because participants can timeout and abort after PreCommit.

## Saga Pattern

Each transaction has a compensating action. If one fails, previously completed transactions are compensated.

**Choreography**: services react to events (event-driven)
**Orchestration**: central Saga coordinator sends commands

## TCC (Try-Confirm/Cancel)

| Phase | Action |
|-------|--------|
| Try   | Reserve resources |
| Confirm | Commit — must succeed |
| Cancel | Release reserves |

## XA Transactions

Standard interface for distributed transactions (JTA). Uses 2PC. Supported by databases, JMS queues, and J2EE containers.