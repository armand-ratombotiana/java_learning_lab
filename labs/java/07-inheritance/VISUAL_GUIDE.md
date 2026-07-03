# Inheritance — Visual Guide

## Class Hierarchy

```
                    ┌──────────┐
                    │  Object  │
                    └────┬─────┘
                         │ extends
                    ┌────▼─────┐
                    │  Animal  │
                    │ ──────── │
                    │ eat()    │
                    │ sound()  │
                    └────┬─────┘
                         │
              ┌──────────┼──────────┐
              │          │          │
         ┌────▼───┐ ┌───▼────┐ ┌───▼────┐
         │  Dog   │ │  Cat   │ │  Bird  │
         │ ────── │ │ ────── │ │ ────── │
         │ bark() │ │ meow() │ │ fly()  │
         │ sound()│ │ sound()│ │ sound()│
         └────────┘ └────────┘ └────────┘
```

## VTable Layout

```
Animal vtable:
┌──────────────┬──────────────────────┐
│ Index 0      │ Object.toString()    │
│ Index 1      │ Object.hashCode()    │
│ Index 2      │ Object.equals()      │
│ Index 3      │ Animal.eat()         │
│ Index 4      │ Animal.sound()       │
└──────────────┴──────────────────────┘

Dog vtable (inherits Animal indices, overrides sound):
┌──────────────┬──────────────────────┐
│ Index 0      │ Object.toString()    │
│ Index 1      │ Object.hashCode()    │
│ Index 2      │ Object.equals()      │
│ Index 3      │ Animal.eat()         │  (inherited)
│ Index 4      │ Dog.sound()          │  (overrides)
│ Index 5      │ Dog.bark()           │  (new)
└──────────────┴──────────────────────┘
```

## super Keyword

```
Dog.sound() {
    // Optional: call parent's version
    super.sound();  // ← bypasses Dog's vtable, calls Animal.sound()
    System.out.println("Woof!");  // ← adds Dog-specific behavior
}
```

## Constructor Chain

```
new Dog("Buddy")
    │
    ├── Dog("Buddy")                      ← constructor body starts
    │    └── super("Buddy")               ← explicit super call (1st statement)
    │         └── Animal("Buddy")         
    │              └── super()            ← implicit Object() super call
    │                   └── Object()      ← Object constructor
    │              └── this.name = name   ← Animal body
    │    └── Dog body                     ← Dog body
    │
    └── Dog instance ready
```
