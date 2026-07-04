# Consistency Models: Visual Guide

## Consistency Spectrum

```
Stronger Guarantee ──────────────────────────── Weaker Guarantee
     │                    │                    │
Linearizability ──── Causal ────────────── Eventual
     │                    │                    │
  W = write a      W₁(a) → W₂(b)         W₁(a)   W₂(b)
  R = read a       (causal)              (order unknown)
     │                    │                    │
  R(a) must         R₁(a) then R₂(b)     R₁(a) or R₂(b)
  see latest        or R₂(b) then R₁(a)  in any order
```

## Operation Ordering Examples

```
Linearizable:
  Client1: ──W(a=1)──────W(a=2)────────────────
  Client2: ──────────R(a=1)──R(a=2)────────────
  Client3: ──────────────────R(a=1)──R(a=2)────
                          ✗ VIOLATION!

Eventual:
  Client1: ──W(a=1)────────────────────────────
  Client2: ──────────R(a=1)────────────────────
  Client3: ──────────────────R(a=1)────────────
  Client4: ──────────────────────────R(a=1)────
                          ✓ OK (eventually)
```
