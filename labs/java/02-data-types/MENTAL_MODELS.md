# Data Types — Mental Models

## Model 1: The Hardware Shelf

Primitives are like items stored directly on shelves. Their size is fixed (byte = tiny box, int = medium box, long = large box). When you access them, you get the item itself. References are like index cards pointing to large items stored in a warehouse (heap). Multiple index cards can point to the same item.

## Model 2: The Number Line

Integer types are different-sized segments of the number line:
```
byte:   [-128 ───────────────────── 127]
short:  [-32,768 ─────────────────────────────── 32,767]
int:    [-2^31 ─────────────────────────────────────────── 2^31-1]
long:   [-2^63 ───────────────────────────────────────────────────────────── 2^63-1]
```

Widening moves right (more range). Narrowing moves left (possible truncation).

## Model 3: The Shipping Container

A `double` is a 64-bit container with sign, exponent, and mantissa. Floating-point values are approximations — like trying to represent 1/3 in decimal (0.3333...). Some numbers cannot be represented exactly.

## Model 4: The Autoboxing Vending Machine

Put in a primitive coin, get a wrapper candy. The machine caches Integer candies for -128 to 127 — common values are pre-made. Outside this range, the machine manufactures new candies each time.
