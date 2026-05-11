# UML Basics

## Class Diagram Elements

### Classes
```
+-----------------+
|   ClassName     |
+-----------------+
| - attribute     |
| + method()      |
+-----------------+
```

**Visibility:**
- `+` Public
- `-` Private
- `#` Protected
- `~` Package

### Relationships

| Type | Symbol | Description |
|------|--------|-------------|
| Inheritance | `----|>` | Class extends another |
| Implementation | `----|>` | Class implements interface |
| Association | `------>` | One-way relationship |
| Aggregation | `<>----` | Has-a (optional) |
| Composition | `<>--` | Has-a (mandatory) |
| Dependency | `.-->` | Uses temporarily |

### Example
```
      Animal
         ^
         |
    +---------+
    |         |
  Dog      Cat
```

## Sequence Diagrams
Shows how objects interact over time, focusing on message passing between components.