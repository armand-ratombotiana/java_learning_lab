# Mental Models for Algebra

## The Balance Scale

An equation is a balanced scale: $3x + 5 = 20$. Whatever you do to one side, do to the other.

```
   [3x + 5]    =    [20]
   ────────         ────
```

Subtract 5: `3x = 15` → Divide by 3: `x = 5`

## The Function Machine

A function $f$ takes input $x$, applies a rule, produces output $f(x)$:

```
  x ──→ [ f ] ──→ f(x)
```

```java
Function<Integer, Integer> f = x -> 2 * x + 3;
f.apply(5); // 13
```

## The Variable as Box

A variable is a labeled box that holds a value. Solving an equation means finding what's in the box.

## The Graph as Picture

Every equation $y = f(x)$ draws a picture. The solutions are where the graph crosses the axes.

## Abstract Algebra Lens

Groups are symmetries of objects. Rings are number-like systems. Fields are where division works. Java's `interface` is a modern algebraic structure — a set of operations with specified behavior.
