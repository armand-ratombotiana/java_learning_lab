# Mental Models for Sealed Classes

## The VIP Club Model

Think of a sealed class as a **VIP club** with a strict guest list. The club has a door policy:
- Only people on the **permits list** can enter
- Each person on the list enters through their own designated door
- If someone new needs to enter, the club owner must add them to the permits list
- Once admitted, a person can either:
  - Stay as is (final — can't bring anyone else)
  - Start their own VIP list (sealed — restrict their guests)
  - Open a public entrance (non-sealed — anyone can enter through them)

The compiler acts as the bouncer, checking the guest list at compile time. This allows the system to know exactly who's inside at any point.

## The Taxonomy Model

Think of sealed classes as a **biological taxonomy** (kingdom, phylum, class, etc.):

- The `sealed` type is like a **phylum** — it explicitly lists all known classes within it
- `final` subtypes are like **species** — they are the leaf nodes, no further classification
- `sealed` subtypes are like **classes** within the phylum — they have their own restricted subtypes
- `non-sealed` subtypes are like **invasive species** — they can spread anywhere

The value of this model is that, given any animal, you can categorize it exhaustively: it belongs to one of the known phyla, and within that phyla, one of the known classes, etc. The classification is complete and known at compile time.

## The Enum of Types Model

The simplest mental model: **sealed classes are enums for types**.

- An `enum` restricts the **values** of a type (e.g., `DayOfWeek` can only be one of 7 values)
- A `sealed` class restricts the **subtypes** of a type (e.g., `Shape` can only be one of known subtypes)

```java
// Enum: fixed set of VALUES
enum Day { MONDAY, TUESDAY, ... }

// Sealed: fixed set of SUBTYPES
sealed interface Shape permits Circle, Rectangle, Triangle {}
```

Just as you can `switch` over an enum exhaustively, you can `switch` over a sealed type exhaustively — the compiler knows all possibilities.

## The Algebraic Data Type Model

For the mathematically inclined, sealed classes represent **sum types** in algebraic data types:

```
Shape = Circle | Rectangle | Triangle
```

This means a `Shape` takes one of exactly three forms. Each form can carry different data:
- A `Circle` carries a `radius`
- A `Rectangle` carries `width` and `height`
- A `Triangle` carries `base` and `height`

Algebraic data types are "algebraic" because you can combine sum types and product types:

```
// Product type (record): Point = x × y
record Point(int x, int y) {}

// Sum type (sealed): Shape = Circle | Rectangle | Triangle
sealed interface Shape permits Circle, Rectangle, Triangle {}
record Circle(Point center, double radius) implements Shape {}
// etc.
```

The algebra: a `Shape` is either a `Circle` (containing a `Point` product and a `double`) OR a `Rectangle` (containing two `Point` products) OR a `Triangle`.

## The Menu Model

Think of a sealed class as a **restaurant menu** with fixed sections:

- The `sealed` type is the **menu** itself
- Each permitted subtype is a **section** (Appetizers, Mains, Desserts)
- Each section can be:
  - `final`: Single dish, no variations (e.g., "Soup of the Day")
  - `sealed`: Sub-sections within (e.g., "Mains" → "Pasta", "Seafood", "Meat")
  - `non-sealed`: Chef's special — can be anything

When you order (write a switch), you must cover every section. If the chef adds a new section, the kitchen compile fails until you update the ordering system.

## The Compiler as a Theorem Prover

Sealed classes turn the compiler into a **theorem prover** for case analysis:

```
Theorem: For all values of type Shape, the area function returns a result.
Proof by case analysis:
  Case Circle: area = πr²  → result computed ✓
  Case Rectangle: area = w·h → result computed ✓
  Case Triangle: area = ½bh → result computed ✓
  No other cases exist (sealed class).
  Therefore, the theorem is proven. QED.
```

If a new subtype is added, the theorem becomes incomplete, and the compiler refuses to accept the program until the proof (switch expression) is updated.
