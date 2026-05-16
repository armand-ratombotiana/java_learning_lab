# Mental Models for Design Patterns

## Pattern as Recipe

```
Pattern = Recipe
├── Ingredients (classes/objects)
├── Steps (implementation)
└── Outcome (solved problem)

Just as a recipe can be made many ways,
a pattern can be implemented in different languages
```

## Pattern as Template

```
Abstract Factory
├── AbstractProductA
├── AbstractProductB  
├── AbstractFactory
└── ConcreteFactory + ConcreteProducts

Template shows structure, you fill in details
```

## Pattern as Communication

```
"Use a Singleton here"

vs

"Create a class with one instance,
provide global access point"

Pattern language is shorthand for design decisions
```

## Pattern Categories Visual

```
┌─────────────────────────────────────────┐
│        DESIGN PATTERNS                  │
├─────────────────────────────────────────┤
│ CREATIONAL     STRUCTURAL   BEHAVIORAL │
│ ├─ Singleton   ├─ Adapter   ├─ Observer│
│ ├─ Factory    ├─ Bridge    ├─ Strategy │
│ ├─ Abstract   ├─ Composite├─ Command │
│ ├─ Builder    ├─ Decorator├─ State   │
│ └─ Prototype  ├─ Facade    └─ Template │
└─────────────────────────────────────────┘
```

## Key Insight

Patterns are not code - they're templates for solving problems. Learn the intent and applicability, not just the implementation.