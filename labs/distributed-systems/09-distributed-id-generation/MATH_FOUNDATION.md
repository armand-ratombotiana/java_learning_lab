# Distributed ID Generation: Mathematical Foundation

## Uniqueness Probability

### UUID v4 (122 random bits)
P(collision after N IDs) ≈ N² / 2¹²³

For N = 10¹⁸: P ≈ 10⁻¹⁰ (practically zero)

### Snowflake (64-bit)
Max unique IDs per second per worker: 2¹² = 4096

Total unique IDs before wrap: 2⁴¹ ms ≈ 69 years

## Sequence Exhaustion

For Snowflake:
- Sequence bits = 12 → 4096 IDs/ms/node
- Sequence bits = 16 → 65536 IDs/ms/node
- Sequence bits = 20 → 1048576 IDs/ms/node

## Collision Probability in Snowflake

P(collision) = P(clock backwards) × P(sequence reuse)

With proper clock handling: P ≈ 0 (deterministic uniqueness)

## ID Space Coverage

UUID v4 covers 2¹²² possible values.
For comparison:
- 2¹²² >> total stars in observable universe (≈ 10²²)
- 2⁶³ ≈ 9.2 × 10¹⁸ (Snowflake max positive value)
