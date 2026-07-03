# Refactoring Polymorphism

## Replace instanceof with Polymorphism

Before: `if (animal instanceof Dog) { ((Dog) animal).bark(); } else if (animal instanceof Cat) { ... }`
After: `animal.makeSound();` — each subclass implements `makeSound()`.

## Replace if-else with Strategy Pattern

Before: `if (type == "CREDIT") ... else if (type == "PAYPAL") ...`
After: `PaymentStrategy strategy = getStrategy(type); strategy.pay(amount);`

## Extract Common Interface

Before: Two unrelated classes with same method but no common type.
After: Define interface, both implement, use interface reference.

## Replace Switch with Polymorphism

Before: `switch (shapeType) { case "circle": ... }`
After: `shape.draw();` — each shape class implements `draw()`.

## Use Covariant Return Types

Before: Subclass method returns supertype, caller must cast.
After: Override with covariant (more specific) return type.

## Move Overload Resolution to Strategy

Before: Overloaded methods for different types becoming unwieldy.
After: Use `Consumer<T>` / `Function<T,R>` to parameterize behavior.
