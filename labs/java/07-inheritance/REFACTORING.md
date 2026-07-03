# Refactoring Inheritance

## Replace Inheritance with Composition

Before: `class Dog extends Animal { }` — just for code reuse.
After: `class Dog { private AnimalDelegate delegate; }` — HAS-A instead of IS-A.

## Pull Up Common Code

Both subclasses have same method? Pull it up to the superclass.
Before: Duplicate `getName()` in `Dog` and `Cat`.
After: Define `getName()` in `Animal`.

## Push Down Unused Code

Method defined in superclass but only used in one subclass? Push it down.

## Replace Inheritance Hierarchy with Interface

When shared code is minimal, replace abstract class with interface.
Before: `abstract class Shape { abstract double area(); }`
After: `interface Shape { double area(); }`

## Add @Override

Missing `@Override` on all overridden methods.

## Replace Constant Method with Field

Before: `class Math { double pi() { return 3.14; } }`
After: `static final double PI = 3.14;`
