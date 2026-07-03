# Mental Models for Records

## The ID Card Model

Think of a record as an **ID card**. An ID card is defined entirely by the information printed on it: name, photo, date of birth, ID number, expiration date. Two ID cards with the same information are effectively the same card (for identification purposes). You can't change the information on an ID card once it's issued — you get a new card if anything changes.

This is exactly how records work:
- The record's state is entirely defined by its components (like the fields on the ID card)
- Two records with the same component values are equal
- Records are immutable — once created, they cannot be changed

## The Spreadsheet Row Model

Think of a record as a **row in a spreadsheet** with named columns. The column headers are the component names, and the data in each cell is the component value. A spreadsheet row has no identity beyond its data — if you have two identical rows, they represent the same information.

```java
record Employee(String name, String department, double salary) {}
```

This is like a spreadsheet with columns: Name, Department, Salary. Each `Employee` record is a row.

## The Algebraic Data Type Model

Records are **product types** in type theory. A product type `R(A, B)` means a value of `R` contains **both** an `A` **and** a `B`. The number of possible values is the product of the number of values of each component:

```
|R(A, B)| = |A| × |B|
```

For example, if `A` is a boolean (2 values) and `B` is a boolean (2 values), then `R` has `2 × 2 = 4` possible values: (true, true), (true, false), (false, true), (false, false).

This contrasts with **sum types** (sealed classes), where a value is **either** one type **or** another:

```
|S(A, B)| = |A| + |B|
```

Records (product types) and sealed classes (sum types) together form algebraic data types.

## The Final Class With Automatic Methods Model

For developers familiar with traditional Java, think of a record as a class that:
1. Is `final` (cannot be extended)
2. Has `private final` fields for all constructor parameters
3. Has a generated constructor, accessors, `equals()`, `hashCode()`, and `toString()`
4. Extends `java.lang.Record` instead of `Object`

That is, a record is exactly the class you would write by hand for a simple value object — except the compiler writes it for you, correctly and consistently.

## The Value vs. Identity Distinction

The most important mental model shift:

- **Classes** represent **identity** — each object has a unique identity, can change state, and equality is usually identity-based (`==`) unless overridden
- **Records** represent **values** — each record's value is its state, cannot change, and equality is always value-based

Think about money: a $5 bill (identity: serial number, mutable: can be exchanged) vs. the number 5 (value: always equal to other 5s, immutable). Classes are like the $5 bill; records are like the number 5.

## The Constructor as the Only Entry Point

A record's canonical constructor is the **only way** to create an instance. There is no:
- Default constructor (unless all components have defaults, but records don't have default values)
- Builder pattern (you can add one, but it must call the canonical constructor)
- Deserialization bypass (even serialization goes through the canonical constructor)
- Clone or copy (records inherently don't support cloning)

This means validation in the compact constructor is always enforced — there are no backdoors. This is a strong security guarantee.

## The Accessor vs. Getter Distinction

Record accessors are named after the component, not prefixed with `get`:

```java
// Record accessor
point.x()    // not getX()

// Traditional JavaBean getter
point.getX()
```

This is a deliberate naming choice reflecting that records don't follow the JavaBeans convention. Records are not beans — they don't have a no-arg constructor, they're not mutable, and they're not meant for frameworks that rely on the JavaBeans pattern (though many frameworks support records anyway).
