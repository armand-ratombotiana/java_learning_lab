# Visual Guide to Sealed Classes

## Sealed Hierarchy Structure

```
                    ┌──────────────────────────────┐
                    │  sealed interface Shape       │
                    │  permits Circle, Rectangle,   │
                    │           Triangle, Polygon   │
                    └──────────────┬───────────────┘
                                  │ extends
              ┌───────────────────┼───────────────────┐
              │                   │                   │
     ┌────────▼────────┐ ┌───────▼────────┐ ┌───────▼────────┐
     │ Circle          │ │ Rectangle      │ │ Triangle       │
     │ (record, final) │ │ (record, final)│ │ (record, final)│
     └─────────────────┘ └────────────────┘ └────────────────┘
     
              │
     ┌───────▼───────────────────────┐
     │ sealed class Polygon          │
     │ extends Shape                 │
     │ permits Triangle, Rectangle   │
     └───────────────────────────────┘
       (Note: Triangle and Rectangle could extend
        Polygon instead of directly extending Shape)
```

## Subtype Modifiers Visualization

```
sealed interface Vehicle
  permits Car, Motorcycle, Truck, Custom

  ├── Car
  │   └── modifier: non-sealed
  │       └── ElectricCar extends Car (OK)
  │       └── SUV extends Car (OK)
  │
  ├── Motorcycle
  │   └── modifier: final
  │       └── Cannot extend
  │
  ├── Truck
  │   └── modifier: sealed
  │       └── permits LightTruck, HeavyTruck
  │           ├── LightTruck (final)
  │           └── HeavyTruck (final)
  │
  └── Custom
      └── modifier: non-sealed
          └── Any class can extend
```

## Exhaustiveness Verification

```
switch (Shape s) {
    case Circle c:      ────┐
    case Rectangle r:   ────┤  Covers all 3 final subtypes
    case Triangle t:    ────┘
}
→ Compiler: EXHAUSTIVE ✓

switch (Shape s) {
    case Circle c:      ────┐
    case Rectangle r:   ────┤  Missing Triangle!
}
→ Compiler: NON-EXHAUSTIVE ✗
   Error: Missing case for Triangle
```

## Module vs. Package Boundaries

```
module com.example.shapes (module-info.java)

  Package: com.example.shapes.core
    ┌────────────────────────────────────┐
    │ sealed interface Shape             │
    │   permits Circle, Rectangle,       │
    │           Triangle, Polygon        │
    └────────────────────────────────────┘

  Package: com.example.shapes.geometry
    Circle (final) ← OK (same module)
    Rectangle (final) ← OK (same module)

  Package: com.example.shapes.polygons
    Triangle (final) ← OK (same module)
    Polygon (sealed) ← OK (same module)
    Pentagon (final, extends Polygon) ← OK

  External module: com.example.client
    Cannot extend Shape ← ERROR (different module)
```

## Pattern Matching Flow

```
Value: new Circle(5.0)

Sealed type: Shape (permits Circle, Rectangle, Triangle)

  Check 1: instanceof Circle? → YES
    ├─ Match: case Circle c → area = π × 5.0²
    └─ Execute body
    
  Check 2: instanceof Rectangle? → SKIPPED (already matched)
  Check 3: instanceof Triangle? → SKIPPED (already matched)
  
  All cases covered: No default needed
```

## Inheritance vs. Sealed Control

```
Accessibility vs. Extensibility:

                    Can subclass?   Can access?
  public            Yes (anyone)    Anyone
  protected         Same pkg + sub  Same pkg + sub
  package-private   Same package    Same package  
  private           Nested only     Nested only
  
  sealed            Permits list    API access level
```

The sealed modifier controls extensibility independently of accessibility. A `public sealed` class is visible to everyone but extensible only by permitted subtypes.
