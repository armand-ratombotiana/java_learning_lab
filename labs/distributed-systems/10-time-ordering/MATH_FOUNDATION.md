# Time and Ordering: Mathematical Foundation

## Lamport Clock Properties

Given events a and b with Lamport timestamps L(a) and L(b):

If a → b (a happens-before b), then L(a) < L(b)
(Converse is not true: L(a) < L(b) does not imply a → b)

## Vector Clock Properties

Given events a and b with vector clocks VC(a) and VC(b):

a → b iff VC(a) < VC(b) (all components ≤, at least one <)
a ∥ b iff VC(a) ⋚̸ VC(b) (neither is ≤ the other)

Vector clocks exactly characterize causality.

## HLC Bounds

For HLC values (c, l):

|l - c| ≤ ε (bounded by max clock drift)
c ≤ pt + ε (where pt = physical time)

HLC is always close to physical time while providing causality.

## Clock Drift Model

Clock C(t) at real time t:
C(t) = t + drift(t)

Where |drift(t)| ≤ ρ × t (for quartz: ρ ≈ 10⁻⁶)

Maximum skew between two clocks after T: 2ρT
After 1 hour: ~7.2ms skew
After 24 hours: ~173ms skew
